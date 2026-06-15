package org.bigdata.server.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.bigdata.alert.template.TemplateProvider;
import org.bigdata.server.bean.dto.alert.AbstractAlertInfoDTO;
import org.bigdata.server.bean.dto.alert.AlertQueryDTO;
import org.bigdata.server.bean.entity.AlertInfo;
import org.bigdata.server.bean.entity.AlertPolicy;
import org.bigdata.server.bean.vo.AlertInfoVO;
import org.bigdata.server.exception.BizException;
import org.bigdata.server.mapper.AlertInfoMapper;
import org.bigdata.server.mapper.AlertPolicyMapper;
import org.bigdata.server.service.AlertInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertInfoServiceImpl implements AlertInfoService {

    private final AlertInfoMapper alterInfoMapper;

    private final AlertPolicyMapper alertPolicyMapper;

    @Override
    public List<AlertInfoVO> list(AlertQueryDTO alterQueryDTO) {
        List<AlertInfo> alterInfos = alterInfoMapper.selectList(Wrappers.<AlertInfo>lambdaQuery()
                .like(StringUtils.isNotBlank(alterQueryDTO.getName()), AlertInfo::getName, alterQueryDTO.getName())
                .eq(StringUtils.isNotBlank(alterQueryDTO.getType()), AlertInfo::getType, alterQueryDTO.getType())
                .eq(Objects.nonNull(alterQueryDTO.getStatus()), AlertInfo::getStatus, alterQueryDTO.getStatus())
        );
        return alterInfos.stream()
                .map(e -> {
                    AlertInfoVO alterInfoVO = new AlertInfoVO();
                    BeanUtils.copyProperties(e, alterInfoVO);
                    return alterInfoVO;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Integer add(AbstractAlertInfoDTO abstractAlterInfoDTO) {
        AlertInfo entity = abstractAlterInfoDTO.toEntity();
        entity.setId(null);
        entity.setStatus(0);
        return alterInfoMapper.insert(entity);
    }

    @Override
    public Map<String, String> templateParams(String type) {
        return TemplateProvider.getTemplateParams(type);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Integer delete(Integer id) {
        List<AlertPolicy> policies = alertPolicyMapper.selectList(
                Wrappers.<AlertPolicy>lambdaQuery()
                        .eq(AlertPolicy::getAlertId, id));
        if (!policies.isEmpty()) {
            throw new BizException("该告警存在" + policies.size() + "条关联的告警策略，请先删除策略后再删除告警");
        }
        return alterInfoMapper.deleteById(id);
    }

    @Override
    public Integer updateStatus(Integer id, Integer status) {
        return alterInfoMapper.update(null, Wrappers.<AlertInfo>lambdaUpdate()
                .set(AlertInfo::getStatus, status)
                .eq(AlertInfo::getId, id));
    }

    @Override
    public AlertInfoVO getById(Integer id) {
        AlertInfo alertInfo = alterInfoMapper.selectById(id);
        if (alertInfo != null) {
            AlertInfoVO alertInfoVO = new AlertInfoVO();
            BeanUtils.copyProperties(alertInfo, alertInfoVO);
            return alertInfoVO;
        }
        return null;
    }

    @Override
    public Integer update(Integer id, AbstractAlertInfoDTO abstractAlterInfoDTO) {
        AlertInfo alertInfo = alterInfoMapper.selectById(id);
        if (alertInfo == null) {
            return 0;
        }
        AlertInfo entity = abstractAlterInfoDTO.toEntity();
        entity.setId(id);
        entity.setStatus(alertInfo.getStatus());
        return alterInfoMapper.updateById(entity);
    }
}




