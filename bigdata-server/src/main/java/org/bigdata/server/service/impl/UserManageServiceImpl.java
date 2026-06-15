package org.bigdata.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.bigdata.server.bean.dto.user.UserQueryDTO;
import org.bigdata.server.bean.dto.user.UserSaveDTO;
import org.bigdata.server.bean.entity.User;
import org.bigdata.server.bean.vo.UserPageVO;
import org.bigdata.server.mapper.UserMapper;
import org.bigdata.server.service.IUserManageService;
import org.bigdata.server.exception.BizException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理服务实现
 */
@Service
@RequiredArgsConstructor
public class UserManageServiceImpl implements IUserManageService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${bigdata.user.default-password}")
    private String defaultPassword;

    @Override
    public UserPageVO getUserList(UserQueryDTO query) {
        Page<User> page = new Page<>(query.getCurrent(), query.getSize());

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
            .like(StringUtils.hasText(query.getUserName()), User::getUserName, query.getUserName())
            .eq(query.getUserGender() != null, User::getGender, query.getUserGender())
            .like(StringUtils.hasText(query.getNickName()), User::getNickName, query.getNickName())
            .like(StringUtils.hasText(query.getUserPhone()), User::getPhone, query.getUserPhone())
            .like(StringUtils.hasText(query.getUserEmail()), User::getEmail, query.getUserEmail())
            .eq(query.getStatus() != null, User::getStatus, query.getStatus())
            .orderByDesc(User::getId);

        Page<User> result = userMapper.selectPage(page, wrapper);

        List<UserPageVO.UserRow> records = result.getRecords().stream().map(u -> {
            UserPageVO.UserRow row = new UserPageVO.UserRow();
            row.setId(u.getId());
            row.setUserName(u.getUserName());
            row.setUserGender(u.getGender());
            row.setNickName(u.getNickName());
            row.setUserPhone(u.getPhone());
            row.setUserEmail(u.getEmail());
            row.setStatus(u.getStatus());
            row.setCreateTime(u.getCreateTime());
            row.setUpdateTime(u.getUpdateTime());
            return row;
        }).collect(Collectors.toList());

        return UserPageVO.from(result, records);
    }

    @Override
    public void addUser(UserSaveDTO dto) {
        User user = new User();
        user.setUserName(dto.getUserName());
        user.setPassword(passwordEncoder.encode(defaultPassword));
        user.setNickName(dto.getNickName());
        user.setGender(dto.getGender());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        userMapper.insert(user);
    }

    @Override
    public void updateUser(UserSaveDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setNickName(dto.getNickName());
        user.setGender(dto.getGender());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setStatus(dto.getStatus());
        userMapper.updateById(user);
    }

    @Override
    public void deleteUser(Integer id) {
        userMapper.deleteById(id);
    }

    @Override
    public void updateUserStatus(Integer id, Integer status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    public void resetPassword(Integer id, String oldPassword, String newPassword) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BizException("原始密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }
}
