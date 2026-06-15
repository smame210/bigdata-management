package org.bigdata.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bigdata.server.bean.dto.user.UserLoginDTO;
import org.bigdata.server.bean.vo.UserInfoVO;
import org.bigdata.server.bean.vo.UserLoginVO;
import org.bigdata.server.service.IUserService;
import org.bigdata.server.util.R;
import org.springframework.web.bind.annotation.*;

/**
 * 用户认证
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    /**
     * 登陆
     */
    @PostMapping("/login")
    public R<UserLoginVO> login(@RequestBody UserLoginDTO userInfoDTO) {
        try {
            return R.ok(userService.login(userInfoDTO));
        } catch (RuntimeException e) {
            return R.failed(e.getMessage());
        }
    }

    /**
     * 获取当前用户信息（从 JWT token 解析）
     */
    @GetMapping("/getUserInfo")
    public R<UserInfoVO> getUserInfo() {
        try {
            return R.ok(userService.getCurrentUserInfo());
        } catch (RuntimeException e) {
            return R.failed(e.getMessage());
        }
    }
}
