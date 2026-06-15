package org.bigdata.server.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bigdata.server.bean.dto.alert.AlertPolicyDTO;
import org.bigdata.server.bean.dto.alert.AlertPolicyQueryDTO;
import org.bigdata.server.bean.vo.AlertPolicyVO;
import org.bigdata.server.service.AlertPolicyService;
import org.bigdata.server.util.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/alert-policy")
@RequiredArgsConstructor
public class AlertPolicyController {

    private final AlertPolicyService alertPolicyService;


    /**
     * 告警策略列表查询
     *
     * @param alertPolicyQueryDTO 查询条件
     * @return 查询结果
     */
    @GetMapping("/page")
    public R<IPage<AlertPolicyVO>> page(@ModelAttribute AlertPolicyQueryDTO alertPolicyQueryDTO) {
        log.info("查询告警策略列表：{}", alertPolicyQueryDTO);
        return R.ok(alertPolicyService.page(alertPolicyQueryDTO));
    }

    /**
     * 根据id查询告警策略
     *
     * @param id 主键ID
     * @return 告警配置
     */
    @GetMapping("/{id}")
    public R<AlertPolicyVO> getById(@PathVariable Integer id) {
        log.info("查询告警策略详情：{}", id);
        return R.ok(alertPolicyService.getById(id));
    }

    /**
     * 新增告警策略
     *
     * @param alertPolicyDTO 告警策略配置
     * @return 新增结果
     */
    @PostMapping()
    public R<Integer> add(@Valid @RequestBody AlertPolicyDTO alertPolicyDTO) {
        log.info("新增告警策略, {}", alertPolicyDTO);
        return R.ok(alertPolicyService.add(alertPolicyDTO));
    }

    /**
     * 删除告警策略
     *
     * @param id 主键ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public R<Integer> delete(@PathVariable Integer id) {
        log.info("删除告警规则, id: {}", id);
        return R.ok(alertPolicyService.delete(id));
    }

    /**
     * 更新告警策略
     *
     * @param id             主键id
     * @param alertPolicyDTO 告警策略
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public R<Integer> update(@PathVariable Integer id, @Valid @RequestBody AlertPolicyDTO alertPolicyDTO) {
        log.info("更新告警策略, id: {}, alertInfo: {}", id, alertPolicyDTO);
        return R.ok(alertPolicyService.update(id, alertPolicyDTO));
    }

    /**
     * 修改告警策略状态
     *
     * @param id     主键
     * @param status 状态（0-禁用，1-启用）
     * @return 修改结果
     */
    @PutMapping("/{id}/status")
    public R<Integer> updateStatus(@PathVariable Integer id,
                                   @RequestParam Integer status) {
        log.info("修改告警策略状态, id: {}, status: {}", id, status);
        return R.ok(alertPolicyService.updateStatus(id, status));
    }
}
