package org.bigdata.server.service;

import org.bigdata.server.bean.dto.user.UserQueryDTO;
import org.bigdata.server.bean.dto.user.UserSaveDTO;
import org.bigdata.server.bean.vo.UserPageVO;

/**
 * 用户管理服务接口
 */
public interface IUserManageService {
    /**
     * 分页查询用户列表
     */
    UserPageVO getUserList(UserQueryDTO query);

    /**
     * 新增用户
     */
    void addUser(UserSaveDTO dto);

    /**
     * 更新用户
     */
    void updateUser(UserSaveDTO dto);

    /**
     * 删除用户
     */
    void deleteUser(Integer id);

    /**
     * 更新用户状态
     */
    void updateUserStatus(Integer id, Integer status);

    /**
     * 重置密码
     */
    void resetPassword(Integer id, String oldPassword, String newPassword);
}
