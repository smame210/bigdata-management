package org.bigdata.alert.ding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bigdata.alert.SenderRequest;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DingDingReq extends SenderRequest {
    private String webhook;

    private String secret;

    private DingDingContent content;

    private Boolean atAll;

    private List<String> atMobiles;
}
