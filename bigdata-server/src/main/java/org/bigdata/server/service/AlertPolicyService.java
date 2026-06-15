package org.bigdata.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.bigdata.server.bean.dto.alert.AlertPolicyDTO;
import org.bigdata.server.bean.dto.alert.AlertPolicyQueryDTO;
import org.bigdata.server.bean.vo.AlertPolicyVO;

public interface AlertPolicyService {

    IPage<AlertPolicyVO> page(AlertPolicyQueryDTO alertPolicyQueryDTO);

    AlertPolicyVO getById(Integer id);

    Integer add(AlertPolicyDTO alertPolicyDTO);

    Integer delete(Integer id);

    Integer update(Integer id, AlertPolicyDTO alertPolicyDTO);

    Integer updateStatus(Integer id, Integer status);
}
