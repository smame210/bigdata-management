package org.bigdata.server.exception;

import org.springframework.boot.ExitCodeGenerator;

/**
 * 数据库初始化异常
 * 当抛出此异常时，应用将会停止运行
 */
public class DatabaseInitializationException extends RuntimeException implements ExitCodeGenerator {
    
    private static final int EXIT_CODE = 1;
    
    public DatabaseInitializationException(String message) {
        super(message);
    }
    
    public DatabaseInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    @Override
    public int getExitCode() {
        return EXIT_CODE;
    }
}