package org.bigdata.server.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.bigdata.server.bean.dto.alert.AlertPolicyQueryDTO;
import org.bigdata.server.bean.entity.AlertPolicy;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface AlertPolicyMapper extends BaseMapper<AlertPolicy> {

    IPage<AlertPolicy> selectByPage(Page<?> page, @Param("params") AlertPolicyQueryDTO alertPolicyQueryDTO);

    AlertPolicy getById(@Param("id") Integer id);
}




