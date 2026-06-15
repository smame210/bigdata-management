package org.bigdata.server.exception;

/**
 * 业务逻辑异常
 * 用于表示业务处理过程中的逻辑错误
 * 此异常通常会被全局异常处理器捕获并转换为友好的错误响应
 */
public class BizException extends RuntimeException {

    /**
     * 错误码
     */
    private final int code;

    /**
     * 默认错误码
     */
    private static final int DEFAULT_ERROR_CODE = 400;

    /**
     * 使用默认错误码构造业务异常
     *
     * @param message 错误信息
     */
    public BizException(String message) {
        super(message);
        this.code = DEFAULT_ERROR_CODE;
    }

    /**
     * 使用自定义错误码构造业务异常
     *
     * @param code 错误码
     * @param message 错误信息
     */
    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 使用默认错误码和原因构造业务异常
     *
     * @param message 错误信息
     * @param cause 原因
     */
    public BizException(String message, Throwable cause) {
        super(message, cause);
        this.code = DEFAULT_ERROR_CODE;
    }

    /**
     * 使用自定义错误码和原因构造业务异常
     *
     * @param code 错误码
     * @param message 错误信息
     * @param cause 原因
     */
    public BizException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    public int getCode() {
        return code;
    }
}
