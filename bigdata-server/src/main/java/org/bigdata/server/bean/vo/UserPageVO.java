package org.bigdata.server.bean.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户分页响应 VO（适配前端 CommonRecord + PaginatingQueryRecord）
 */
@Data
public class UserPageVO {
    private long current;
    private long size;
    private long total;
    private List<UserRow> records;

    public static UserPageVO from(IPage<?> page, List<UserRow> records) {
        UserPageVO vo = new UserPageVO();
        vo.setCurrent(page.getCurrent());
        vo.setSize(page.getSize());
        vo.setTotal(page.getTotal());
        vo.setRecords(records);
        return vo;
    }

    @Data
    public static class UserRow {
        private Integer id;
        private String userName;
        private Integer userGender;
        private String nickName;
        private String userPhone;
        private String userEmail;
        private Integer status;
        private String createBy;
        private LocalDateTime createTime;
        private String updateBy;
        private LocalDateTime updateTime;
    }
}
