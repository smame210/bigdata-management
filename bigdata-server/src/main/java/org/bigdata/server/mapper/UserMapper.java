package org.bigdata.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.bigdata.server.bean.entity.User;

/**
 * 系统用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
