package org.bigdata.server.bean;

import lombok.Data;

@Data
public class PageQueryDTO {
    /**
     * 当前页码
     */
    private Integer current = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;
}
