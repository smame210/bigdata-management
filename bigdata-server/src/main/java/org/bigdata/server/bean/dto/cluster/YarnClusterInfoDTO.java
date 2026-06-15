package org.bigdata.server.bean.dto.cluster;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import org.bigdata.server.bean.entity.ClusterInfo;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

@Data
public class YarnClusterInfoDTO extends AbstractClusterInfoDTO {
    @NotBlank(message = "请上传core-site.xml文件")
    private String coreSitePath;

    @NotBlank(message = "请上传yarn-site.xml文件")
    private String yarnSitePath;

    @NotBlank(message = "请上传hdfs-site.xml文件")
    private String hdfsSitePath;

    @NotBlank(message = "请填写YARN ResourceManager地址")
    private String yarnResourceManagerUrl;

    private Map<String, Object> otherConfig;

    @Override
    public ClusterInfo toEntity() {
        ClusterInfo clusterInfo = new ClusterInfo();
        clusterInfo.setId(getId());
        clusterInfo.setClusterName(getClusterName());
        clusterInfo.setClusterType(getClusterType());
        clusterInfo.setClusterStatus(getClusterStatus());

        JSONObject metaJSON = JSONObject.of(
                "coreSitePath", coreSitePath,
                "yarnSitePath", yarnSitePath,
                "hdfsSitePath", hdfsSitePath,
                "yarnResourceManagerUrl", yarnResourceManagerUrl,
                "otherConfig", otherConfig
        );
        clusterInfo.setMetadata(metaJSON.toJSONString());
        return clusterInfo;
    }
}
