package org.bigdata.server.service;

import org.bigdata.server.bean.dto.user.UserLoginDTO;
import org.bigdata.server.bean.vo.UserInfoVO;
import org.bigdata.server.bean.vo.UserLoginVO;

public interface IUserService {
    UserLoginVO login(UserLoginDTO userInfoDTO);

    UserInfoVO getCurrentUserInfo();
}
