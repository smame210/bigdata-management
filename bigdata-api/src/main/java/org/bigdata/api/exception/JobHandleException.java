package org.bigdata.api.exception;

public class JobHandleException extends RuntimeException {

    /**
     * 使用默认错误码构造业务异常
     *
     * @param message 错误信息
     */
    public JobHandleException(String message) {
        super(message);
    }

    /**
     * 使用默认错误码和原因构造业务异常
     *
     * @param message 错误信息
     * @param cause 原因
     */
    public JobHandleException(String message, Throwable cause) {
        super(message, cause);
    }
}
