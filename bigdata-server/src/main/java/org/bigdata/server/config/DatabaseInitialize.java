package org.bigdata.server.config;

import cn.hutool.core.io.resource.ResourceUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.bigdata.server.exception.DatabaseInitializationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
@Data
@Configuration
public class DatabaseInitialize {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${bigdata.user.default-password}")
    private String defaultPassword;

    @PostConstruct
    private void initDatabase() {
        log.info("数据库初始化...");
        if (currentDatabaseExists()) {
            return;
        }

        try {
            init();
            log.info("数据库初始化完成");
        } catch (Exception e) {
            throw new DatabaseInitializationException("数据库初始化失败!", e);
        }
    }

    /**
     * 检测当前的库是否存在
     *
     * @return 当前的库是否存在
     */
    private boolean currentDatabaseExists() {
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            connection.close();
        } catch (SQLException e) {
            return false;
        } catch (Exception e) {
            throw new DatabaseInitializationException("检查数据库连接时发生错误", e);
        }
        return true;
    }

    /**
     * 创建数据库
     */
    private void init() {
        URI databaseURI;
        try {
            databaseURI = new URI(url.replace("jdbc:", ""));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        String databasePlatform = databaseURI.getScheme();
        String hostAndPort = databaseURI.getAuthority();
        String databaseName = databaseURI.getPath().substring(1);
        String newURL = "jdbc:" + databasePlatform + "://" + hostAndPort + "/";
        try {
            // 创建数据库
            try (Connection connection = DriverManager.getConnection(newURL, username, password);
                 Statement statement = connection.createStatement()) {
                switch (databasePlatform) {
                    case "postgresql":
                        statement.execute("CREATE DATABASE \"" + databaseName + "\"");
                        break;
                    default:
                        throw new DatabaseInitializationException("不支持的[" + databasePlatform + "]数据源类型！");
                }
            }

            // 连接到新创建的数据库并执行SQL脚本
            String dbUrl = "jdbc:" + databasePlatform + "://" + hostAndPort + "/" + databaseName;
            try (Connection dbConnection = DriverManager.getConnection(dbUrl, username, password)) {
                switch (databasePlatform) {
                    case "postgresql":
                        runSQLScript("script/init_postgresql.sql", dbConnection);
                        break;
                    default:
                        throw new DatabaseInitializationException("不支持的[" + databasePlatform + "]数据源类型！");
                }

                // 用配置的默认密码覆盖种子管理员密码
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                try (Statement stmt = dbConnection.createStatement()) {
                    String encodedPassword = encoder.encode(defaultPassword);
                    stmt.execute("UPDATE public.\"user\" SET password = '" + encodedPassword + "' WHERE user_name = 'admin'");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 运行SQL脚本
     *
     * @param path       SQL脚本文件的路径
     * @param connection 数据库连接
     */
    private void runSQLScript(String path, Connection connection) {
        try {
            BufferedReader sqlFileStreamReader = ResourceUtil.getUtf8Reader(path);
            ScriptRunner scriptRunner = new ScriptRunner(connection);
            scriptRunner.runScript(sqlFileStreamReader);
            sqlFileStreamReader.close();
        } catch (Exception e) {
            log.error("执行sql失败！", e);
        }
    }


}
