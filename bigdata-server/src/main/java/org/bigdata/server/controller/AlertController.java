package org.bigdata.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bigdata.server.bean.dto.alert.AbstractAlertInfoDTO;
import org.bigdata.server.bean.dto.alert.AlertQueryDTO;
import org.bigdata.server.bean.vo.AlertInfoVO;
import org.bigdata.server.service.AlertInfoService;
import org.bigdata.server.util.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.Map;

/**
 * 告警配置
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/alert")
@RequiredArgsConstructor
public class AlertController {

    private final AlertInfoService alterInfoService;

    /**
     * 告警列表查询
     *
     * @param alterQueryDTO 查询条件
     * @return 查询结果
     */
    @GetMapping("/list")
    public R<List<AlertInfoVO>> list(@ModelAttribute AlertQueryDTO alterQueryDTO) {
        log.info("查询告警列表：{}", alterQueryDTO);
        return R.ok(alterInfoService.list(alterQueryDTO));
    }

    /**
     * 根据id查询告警配置
     *
     * @param id 主键ID
     * @return 告警配置
     */
    @GetMapping("/{id}")
    public R<AlertInfoVO> getById(@PathVariable Integer id) {
        log.info("查询告警详情：{}", id);
        return R.ok(alterInfoService.getById(id));
    }

    /**
     * 新增告警配置
     *
     * @param abstractAlterInfoDTO 告警配置
     * @return 新增结果
     */
    @PostMapping()
    public R<Integer> add(@Valid @RequestBody AbstractAlertInfoDTO abstractAlterInfoDTO) {
        log.info("新增告警规则");
        return R.ok(alterInfoService.add(abstractAlterInfoDTO));
    }

    /**
     * 删除告警配置
     *
     * @param id 主键ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public R<Integer> delete(@PathVariable Integer id) {
        log.info("删除告警规则, id: {}", id);
        return R.ok(alterInfoService.delete(id));
    }

    /**
     * 更新告警规则配置
     *
     * @param id                   主键id
     * @param abstractAlterInfoDTO 告警配置
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public R<Integer> update(@PathVariable Integer id, @Valid @RequestBody AbstractAlertInfoDTO abstractAlterInfoDTO) {
        log.info("更新告警规则, id: {}, alertInfo: {}", id, abstractAlterInfoDTO);
        return R.ok(alterInfoService.update(id, abstractAlterInfoDTO));
    }

    /**
     * 更新告警配置状态
     *
     * @param id     主键ID
     * @param status 状态
     * @return 更新结果
     */
    @PutMapping("/{id}/status")
    public R<Integer> updateStatus(@PathVariable Integer id,
                                   @Valid
                                   @Min(value = 0, message = "状态不在正确范围内")
                                   @Max(value = 1, message = "状态不在正确范围内")
                                   @RequestParam Integer status) {
        log.info("修改告警配置状态, id: {}", id);
        return R.ok(alterInfoService.updateStatus(id, status));
    }

    /**
     * 查询告警模版参数
     *
     * @param type 告警类型
     * @return 模版参数
     */
    @GetMapping("/template/params/{type}")
    public R<Map<String, String>> templateParams(@PathVariable String type) {
        log.info("查询告警模版参数列表：{}", type);
        return R.ok(alterInfoService.templateParams(type));
    }
}
