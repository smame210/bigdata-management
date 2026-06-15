package org.bigdata.server.bean.vo;

import lombok.Data;

@Data
public class UserInfoVO {
    private String[] buttons;

    private String[] roles;

    private Long userId;

    private String userName;
}
