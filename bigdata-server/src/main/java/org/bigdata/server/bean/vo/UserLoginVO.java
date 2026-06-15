package org.bigdata.server.bean.vo;

import lombok.Data;

@Data
public class UserLoginVO {
    /**
     * refresh token
     */
    private String refreshToken;

    /**
     * token
     */
    private String token;
}
