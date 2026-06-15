package org.bigdata.server.enums;

import org.bigdata.server.exception.BizException;

public enum Frequency {
    ONCE, MINUTELY, HOURLY, DAILY, WEEKLY, MONTHLY, CUSTOM
    ;

    public static Frequency getFrequency(String frequency) {
        return Frequency.valueOf(frequency.toUpperCase());
    }

    public static boolean support(String frequency) {
        try {
            getFrequency(frequency);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getCronExpress(Frequency frequency) {
        switch (frequency) {
            case MINUTELY:
                return "0 * * ? * *"; // 每分钟执行一次
            case HOURLY:
                return "0 0 * ? * *"; // 每小时 0 分钟执行
            case DAILY:
                return "0 0 0 * * ?"; // 每天凌晨 0 点执行
            case WEEKLY:
                return "0 0 0 ? * MON"; // 每周一 0 点执行
            case MONTHLY:
                return "0 0 0 1 * ?"; // 每月 1 日 0 点执行
            default:
                throw new BizException("Unsupported frequency: " + frequency);
        }
    }

}

