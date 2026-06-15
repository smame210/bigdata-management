package org.bigdata.server.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bigdata.server.bean.dto.user.UserLoginDTO;
import org.bigdata.server.bean.entity.User;
import org.bigdata.server.bean.vo.UserInfoVO;
import org.bigdata.server.bean.vo.UserLoginVO;
import org.bigdata.server.config.AuthContext;
import org.bigdata.server.config.JwtUtil;
import org.bigdata.server.mapper.UserMapper;
import org.bigdata.server.service.IUserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public UserLoginVO login(UserLoginDTO userInfoDTO) {
        // 按用户名查找用户
        User user = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                        .eq(User::getUserName, userInfoDTO.getUserName()));

        if (user == null || user.getStatus() != 1) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 校验 bcrypt 密码
        if (!passwordEncoder.matches(userInfoDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 生成 JWT
        String token = jwtUtil.generateToken(user.getId(), user.getUserName());
        UserLoginVO vo = new UserLoginVO();
        vo.setToken(token);
        vo.setRefreshToken(UUID.randomUUID().toString().replace("-", ""));
        return vo;
    }

    @Override
    public UserInfoVO getCurrentUserInfo() {
        Integer userId = AuthContext.getUserId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        UserInfoVO vo = new UserInfoVO();
        vo.setUserId(user.getId().longValue());
        vo.setUserName(user.getUserName());
        // 角色暂时返回超级管理员
        vo.setRoles(new String[]{"R_SUPER"});
        vo.setButtons(new String[]{});
        return vo;
    }
}
