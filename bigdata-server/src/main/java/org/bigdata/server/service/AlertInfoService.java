package org.bigdata.server.service;

import org.bigdata.server.bean.dto.alert.AbstractAlertInfoDTO;
import org.bigdata.server.bean.dto.alert.AlertQueryDTO;
import org.bigdata.server.bean.vo.AlertInfoVO;

import java.util.List;
import java.util.Map;

public interface AlertInfoService {

    List<AlertInfoVO> list(AlertQueryDTO alterQueryDTO);

    Integer add(AbstractAlertInfoDTO abstractAlterInfoDTO);

    Map<String, String> templateParams(String type);

    Integer delete(Integer id);

    Integer updateStatus(Integer id, Integer status);

    AlertInfoVO getById(Integer id);

    Integer update(Integer id, AbstractAlertInfoDTO abstractAlterInfoDTO);
}
