package org.bigdata.alert.ding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DingDingContent {

    private String msgtype;

    private Text text;

    private Markdown markdown;

    private At at;

    @Data
    public class At {
        private boolean isAtAll;
        private List<String> atMobiles;
    }

    @Data
    public class Markdown {
        private String title;
        private String text;
    }

    @Data
    public class Text {
        private String content;
    }

}
