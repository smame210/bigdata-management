package org.bigdata.server.bean.dto.user;

import lombok.Data;

/**
 * 用户分页查询 DTO
 */
@Data
public class UserQueryDTO {
    private Integer current = 1;
    private Integer size = 10;
    private String userName;
    private Integer userGender;
    private String nickName;
    private String userPhone;
    private String userEmail;
    private Integer status;
}
