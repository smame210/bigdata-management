package org.bigdata.flink.yarn;

import lombok.Getter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

public class YarnClientProvider {
    private YarnClient instance;

    private final String coreSitePath;
    private final String yarnSitePath;
    private final String hdfsSitePath;

    @Getter
    private long lastUsedTime = System.currentTimeMillis();

    public YarnClientProvider(String coreSitePath, String yarnSitePath, String hdfsSitePath) {
        this.coreSitePath = coreSitePath;
        this.yarnSitePath = yarnSitePath;
        this.hdfsSitePath = hdfsSitePath;
    }

    private YarnClient create() {
        YarnClient yarnClient = YarnClient.createYarnClient();

        Configuration conf = new Configuration();
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
        conf.addResource(new org.apache.hadoop.fs.Path(coreSitePath));
        conf.addResource(new org.apache.hadoop.fs.Path(yarnSitePath));
        conf.addResource(new org.apache.hadoop.fs.Path(hdfsSitePath));

        YarnConfiguration yarnConfiguration = new YarnConfiguration(conf);
        yarnClient.init(yarnConfiguration);
        yarnClient.start();

        return yarnClient;
    }

    public YarnClient getYarnClient() {
        if (instance == null) {
            instance = create();
        }
        lastUsedTime = System.currentTimeMillis();
        return instance;
    }

    public void close() {
        if (instance != null) {
            instance.stop();
        }
        instance = null;
    }
}
