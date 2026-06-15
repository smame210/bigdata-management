package org.bigdata.server.util;

import cn.hutool.core.date.DateUtil;
import org.quartz.CronExpression;

import java.util.Date;

public class CronUtil {

    public static String getCronExpression(Date startTime) {
        return String.format("%d %d %d %d %d ? %d",
                DateUtil.second(startTime),
                DateUtil.minute(startTime),
                DateUtil.hour(startTime, true),
                DateUtil.dayOfMonth(startTime),
                DateUtil.month(startTime) + 1,
                DateUtil.year(startTime));
    }

    public static boolean isCronExpression(String cronExpression) {
        return CronExpression.isValidExpression(cronExpression);
    }
}
