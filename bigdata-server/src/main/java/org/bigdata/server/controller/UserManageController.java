package org.bigdata.server.controller;

import lombok.RequiredArgsConstructor;
import org.bigdata.server.bean.dto.user.UserQueryDTO;
import org.bigdata.server.bean.dto.user.UserSaveDTO;
import org.bigdata.server.bean.vo.UserPageVO;
import org.bigdata.server.service.IUserManageService;
import org.bigdata.server.util.R;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理接口
 */
@RestController
@RequestMapping("/systemManage")
@RequiredArgsConstructor
public class UserManageController {

    private final IUserManageService userManageService;

    /**
     * 分页查询用户列表
     */
    @GetMapping("/getUserList")
    public R<UserPageVO> getUserList(UserQueryDTO query) {
        return R.ok(userManageService.getUserList(query));
    }

    /**
     * 新增用户
     */
    @PostMapping("/addUser")
    public R<Void> addUser(@RequestBody UserSaveDTO dto) {
        userManageService.addUser(dto);
        return R.ok();
    }

    /**
     * 更新用户
     */
    @PutMapping("/updateUser")
    public R<Void> updateUser(@RequestBody UserSaveDTO dto) {
        userManageService.updateUser(dto);
        return R.ok();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/deleteUser/{id}")
    public R<Void> deleteUser(@PathVariable Integer id) {
        userManageService.deleteUser(id);
        return R.ok();
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/updateUserStatus")
    public R<Void> updateUserStatus(@RequestParam Integer id, @RequestParam Integer status) {
        userManageService.updateUserStatus(id, status);
        return R.ok();
    }

    /**
     * 重置密码
     */
    @PutMapping("/resetPassword")
    public R<Void> resetPassword(@RequestParam Integer id, @RequestParam String oldPassword, @RequestParam String newPassword) {
        userManageService.resetPassword(id, oldPassword, newPassword);
        return R.ok();
    }
}
