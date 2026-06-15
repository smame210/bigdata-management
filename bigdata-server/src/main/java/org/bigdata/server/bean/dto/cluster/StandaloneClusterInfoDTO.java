package org.bigdata.server.bean.dto.cluster;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import org.bigdata.server.bean.entity.ClusterInfo;

import jakarta.validation.constraints.NotBlank;

@Data
public class StandaloneClusterInfoDTO extends AbstractClusterInfoDTO {

    @NotBlank(message = "JobManager地址不可为空")
    private String jobManagerUrl;

    @Override
    public ClusterInfo toEntity() {
        ClusterInfo clusterInfo = new ClusterInfo();
        clusterInfo.setId(getId());
        clusterInfo.setClusterName(getClusterName());
        clusterInfo.setClusterType(getClusterType());
        clusterInfo.setClusterStatus(getClusterStatus());

        JSONObject metaJSON = JSONObject.of("jobManagerUrl", jobManagerUrl);
        clusterInfo.setMetadata(metaJSON.toJSONString());
        return clusterInfo;
    }
}
