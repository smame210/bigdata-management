package org.bigdata.server.bean.dto.cluster;

import lombok.Data;
import org.bigdata.server.bean.entity.ClusterInfo;
import org.bigdata.server.exception.BizException;

@Data
public class UnknowClusterInfoDTO extends AbstractClusterInfoDTO {
    @Override
    public ClusterInfo toEntity() {
        throw new BizException("不支持的集群类型");
    }
}
