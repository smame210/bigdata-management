package org.bigdata.server.bean.dto.user;

import lombok.Data;

@Data
public class UserLoginDTO {
    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;
}
