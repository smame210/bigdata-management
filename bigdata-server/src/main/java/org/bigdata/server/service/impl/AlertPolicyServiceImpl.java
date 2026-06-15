package org.bigdata.server.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bigdata.server.bean.dto.alert.AlertPolicyDTO;
import org.bigdata.server.bean.dto.alert.AlertPolicyQueryDTO;
import org.bigdata.server.bean.entity.AlertInfo;
import org.bigdata.server.bean.entity.AlertPolicy;
import org.bigdata.server.bean.entity.TaskInfo;
import org.bigdata.server.bean.vo.AlertPolicyVO;
import org.bigdata.server.exception.BizException;
import org.bigdata.server.mapper.AlertInfoMapper;
import org.bigdata.server.mapper.TaskInfoMapper;
import org.bigdata.server.service.AlertPolicyService;
import org.bigdata.server.mapper.AlertPolicyMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class AlertPolicyServiceImpl implements AlertPolicyService{

    private final AlertPolicyMapper alertPolicyMapper;

    private final AlertInfoMapper alertInfoMapper;

    private final TaskInfoMapper taskInfoMapper;

    @Override
    public IPage<AlertPolicyVO> page(AlertPolicyQueryDTO alertPolicyQueryDTO) {
        Page<?> page = new Page<>(alertPolicyQueryDTO.getCurrent(), alertPolicyQueryDTO.getSize());
        IPage<AlertPolicy> alertPolicyIPage = alertPolicyMapper.selectByPage(page, alertPolicyQueryDTO);
        return alertPolicyIPage
                .convert(e -> {
                    AlertPolicyVO alertPolicyVO = new AlertPolicyVO();
                    BeanUtils.copyProperties(e, alertPolicyVO);
                    return alertPolicyVO;
                });
    }

    @Override
    public AlertPolicyVO getById(Integer id) {
        AlertPolicy alertPolicy = alertPolicyMapper.getById(id);
        AlertPolicyVO alertPolicyVO = new AlertPolicyVO();
        BeanUtils.copyProperties(alertPolicy, alertPolicyVO);
        alertPolicyVO.setConditions(JSON.parseObject(alertPolicy.getConditions(), new TypeReference<>() {
        }));
        return alertPolicyVO;
    }

    @Override
    public Integer add(AlertPolicyDTO alertPolicyDTO) {
        AlertInfo alertInfo = alertInfoMapper.selectById(alertPolicyDTO.getAlertId());
        if (alertInfo == null) {
            throw new BizException("告警实例不存在！");
        }
        TaskInfo taskInfo = taskInfoMapper.selectById(alertPolicyDTO.getTaskId());
        if (taskInfo == null) {
            throw new BizException("任务不存在！");
        }

        AlertPolicy alertPolicy = alertPolicyDTO.toEntity();
        alertPolicyDTO.setId(null);
        alertPolicy.setStatus(0);
        return alertPolicyMapper.insert(alertPolicy);
    }

    @Override
    public Integer delete(Integer id) {
        return alertPolicyMapper.deleteById(id);
    }

    @Override
    public Integer update(Integer id, AlertPolicyDTO alertPolicyDTO) {
        AlertInfo alertInfo = alertInfoMapper.selectById(alertPolicyDTO.getAlertId());
        if (alertInfo == null) {
            throw new BizException("告警实例不存在！");
        }
        TaskInfo taskInfo = taskInfoMapper.selectById(alertPolicyDTO.getTaskId());
        if (taskInfo == null) {
            throw new BizException("任务不存在！");
        }

        AlertPolicy oldAlertPolicy = alertPolicyMapper.selectById(id);
        if (oldAlertPolicy == null) {
            return 0;
        }
        AlertPolicy alertPolicy = alertPolicyDTO.toEntity();
        alertPolicy.setId(id);
        alertPolicy.setStatus(oldAlertPolicy.getStatus());
        return alertPolicyMapper.updateById(alertPolicy);
    }

    @Override
    public Integer updateStatus(Integer id, Integer status) {
        return alertPolicyMapper.update(null, Wrappers.<AlertPolicy>lambdaUpdate()
                .set(AlertPolicy::getStatus, status)
                .eq(AlertPolicy::getId, id));
    }
}




