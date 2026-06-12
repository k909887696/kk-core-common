package com.kk.common.utils;

/**
 * created with IntelliJ IDEA.
 * description: 基于 Spring Boot 3 内置 CronExpression 的工具类
 * auth: kk
 * date: 2026-06-12
 */
import org.springframework.scheduling.support.CronExpression;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CronUtil {

    /**
     * 验证 Cron 表达式是否合法
     */
    public static boolean isValid(String cronExpression) {
        try {
            CronExpression.parse(cronExpression);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 计算下一个执行时间
     *
     * @param cronExpression Cron表达式
     * @param fromTime       起始时间
     * @return 下一个执行时间，无效则返回null
     */
    public static LocalDateTime getNextExecutionTime(String cronExpression, LocalDateTime fromTime) {
        if (!isValid(cronExpression) || fromTime == null) {
            return null;
        }

        CronExpression cron = CronExpression.parse(cronExpression);
        // 注意：next() 返回的是 Optional<ZonedDateTime>
        return cron.next(fromTime.atZone(ZoneId.systemDefault()))
                .toLocalDateTime();
    }

    /**
     * 计算接下来 N 次执行时间
     *
     * @param cronExpression Cron表达式
     * @param fromTime       起始时间
     * @param count          需要计算的数量
     * @return 执行时间列表
     */
    public static List<LocalDateTime> getNextExecutionTimes(String cronExpression,
                                                            LocalDateTime fromTime,
                                                            int count) {
        List<LocalDateTime> result = new ArrayList<>();
        if (!isValid(cronExpression) || fromTime == null || count <= 0) {
            return result;
        }

        CronExpression cron = CronExpression.parse(cronExpression);
        LocalDateTime current = fromTime;

        for (int i = 0; i < count; i++) {
            LocalDateTime next = cron.next(current.atZone(ZoneId.systemDefault()))
                    .toLocalDateTime();
            if (next!=null) {
                result.add(next);
                current = next;
            } else {
                break;
            }
        }

        return result;
    }

    /**
     * 便捷方法：从当前时间开始计算 N 次执行时间
     */
    public static List<LocalDateTime> getNextExecutionTimes(String cronExpression, int count) {
        return getNextExecutionTimes(cronExpression, LocalDateTime.now(), count);
    }

    /**
     * 获取 Cron 表达式的原始字符串（Spring 的 CronExpression 不提供描述功能）
     */
    public static String getDescription(String cronExpression) {
        if (!isValid(cronExpression)) {
            return "无效的Cron表达式";
        }
        return "Cron表达式: " + cronExpression;
    }
}
