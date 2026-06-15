package org.bigdata.server.bean.dto.user;

import lombok.Data;

/**
 * 用户新增/编辑 DTO
 */
@Data
public class UserSaveDTO {
    private Integer id;
    private String userName;
    private String nickName;
    private Integer gender;
    private String phone;
    private String email;
    private Integer status;
}
