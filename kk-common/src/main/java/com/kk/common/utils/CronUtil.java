package com.kk.common.utils;

import org.springframework.scheduling.support.CronExpression;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * created with IntelliJ IDEA.
 * description: 基于 Spring Boot 3 内置 CronExpression 的工具类
 * auth: kk
 * date: 2026-06-12
 */
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
     * 便捷方法：从当前时间开始计算下一次执行时间
     *
     * @param cronExpression Cron表达式
     * @return 下一个执行时间，无效则返回null
     */
    public static LocalDateTime getNextExecutionTime(String cronExpression) {
        return getNextExecutionTime(cronExpression, LocalDateTime.now());
    }

    /**
     * 计算下一个执行时间（返回Date格式）
     *
     * @param cronExpression Cron表达式
     * @param fromTime       起始时间（Date格式）
     * @return 下一个执行时间，无效则返回null
     */
    public static Date getNextExecutionDate(String cronExpression, Date fromTime) {
        if (!isValid(cronExpression) || fromTime == null) {
            return null;
        }
        LocalDateTime nextTime = getNextExecutionTime(cronExpression,
                LocalDateTime.ofInstant(fromTime.toInstant(), ZoneId.systemDefault()));
        return nextTime == null ? null : Date.from(nextTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 便捷方法：从当前时间开始计算下一次执行时间（返回Date格式）
     *
     * @param cronExpression Cron表达式
     * @return 下一个执行时间，无效则返回null
     */
    public static Date getNextExecutionDate(String cronExpression) {
        return getNextExecutionDate(cronExpression, new Date());
    }

    /**
     * 计算接下来 N 次执行时间（返回Date格式）
     *
     * @param cronExpression Cron表达式
     * @param count          需要计算的数量
     * @return 执行时间列表
     */
    public static List<Date> getNextExecutionDates(String cronExpression, int count) {
        List<Date> result = new ArrayList<>();
        if (!isValid(cronExpression) || count <= 0) {
            return result;
        }
        List<LocalDateTime> times = getNextExecutionTimes(cronExpression, count);
        for (LocalDateTime time : times) {
            result.add(Date.from(time.atZone(ZoneId.systemDefault()).toInstant()));
        }
        return result;
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
            if (next != null) {
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
     * 获取 Cron 表达式的中文描述
     *
     * @param cronExpression Cron表达式
     * @return 中文描述
     */
    public static String getDescription(String cronExpression) {
        if (!isValid(cronExpression)) {
            return "无效的Cron表达式";
        }

        String[] parts = cronExpression.split("\\s+");
        if (parts.length != 5 && parts.length != 6) {
            return "Cron表达式格式不正确";
        }

        // 6位: 秒 分 时 日 月 周
        // 5位: 分 时 日 月 周
        int offset = parts.length == 6 ? 0 : -1;
        String second = offset == 0 ? parts[0] : "0";
        String minute = parts[1 + offset];
        String hour = parts[2 + offset];
        String dayOfMonth = parts[3 + offset];
        String month = parts[4 + offset];
        String dayOfWeek = parts[5 + offset];

        StringBuilder desc = new StringBuilder();

        // 解析秒
        String secondDesc = parseSecond(second);
        // 解析分
        String minuteDesc = parseMinute(minute);
        // 解析时
        String hourDesc = parseHour(hour);
        // 解析日
        String dayDesc = parseDayOfMonth(dayOfMonth, dayOfWeek);
        // 解析月
        String monthDesc = parseMonth(month);

        // 组合描述
        if (dayDesc != null && !dayDesc.isEmpty()) {
            desc.append(dayDesc);
        }
        if (monthDesc != null && !monthDesc.isEmpty()) {
            desc.append(monthDesc);
        }
        if (hourDesc != null && !hourDesc.isEmpty()) {
            desc.append(hourDesc);
        }
        if (minuteDesc != null && !minuteDesc.isEmpty()) {
            desc.append(minuteDesc);
        }
        if (secondDesc != null && !secondDesc.isEmpty()) {
            desc.append(secondDesc);
        }

        if (desc.length() == 0) {
            return "Cron表达式: " + cronExpression;
        }

        return desc.toString();
    }

    /**
     * 解析秒字段
     */
    private static String parseSecond(String second) {
        if ("0".equals(second) || "0/1".equals(second)) {
            return "";
        }
        if (second.startsWith("0/")) {
            return "每" + second.substring(2) + "秒";
        }
        if (second.contains("-")) {
            return "第" + second + "秒";
        }
        if (second.contains(",")) {
            return "第" + second + "秒";
        }
        return "第" + second + "秒";
    }

    /**
     * 解析分字段
     */
    private static String parseMinute(String minute) {
        if ("*".equals(minute)) {
            return "每分钟";
        }
        if (minute.startsWith("0/")) {
            return "每" + minute.substring(2) + "分钟";
        }
        if (minute.startsWith("*/")) {
            return "每" + minute.substring(2) + "分钟";
        }
        if (minute.contains("-")) {
            return minute + "分钟";
        }
        if (minute.contains(",")) {
            return "第" + minute + "分钟";
        }
        if ("0".equals(minute)) {
            return "";
        }
        return "第" + minute + "分钟";
    }

    /**
     * 解析时字段
     */
    private static String parseHour(String hour) {
        if ("*".equals(hour)) {
            return "每小时";
        }
        if (hour.startsWith("*/")) {
            return "每" + hour.substring(2) + "小时";
        }
        if (hour.contains("-")) {
            return hour + "点";
        }
        if (hour.contains(",")) {
            return hour + "点";
        }
        if ("0".equals(hour)) {
            return "";
        }
        return hour + "点";
    }

    /**
     * 解析日字段
     */
    private static String parseDayOfMonth(String dayOfMonth, String dayOfWeek) {
        // 如果指定了周，日字段可能是 ?
        if ("?".equals(dayOfMonth) || "*".equals(dayOfMonth)) {
            if (!"?".equals(dayOfWeek) && !"*".equals(dayOfWeek)) {
                return parseDayOfWeek(dayOfWeek);
            }
            return "每天";
        }
        if (dayOfMonth.startsWith("L")) {
            return "每月最后一天";
        }
        if (dayOfMonth.contains("W")) {
            return "每月最近工作日";
        }
        if (dayOfMonth.contains("-")) {
            return "每月" + dayOfMonth + "日";
        }
        if (dayOfMonth.contains(",")) {
            return "每月" + dayOfMonth + "日";
        }
        return "每月" + dayOfMonth + "日";
    }

    /**
     * 解析周字段
     */
    private static String parseDayOfWeek(String dayOfWeek) {
        if ("?".equals(dayOfWeek) || "*".equals(dayOfWeek)) {
            return "";
        }
        if (dayOfWeek.contains("-")) {
            return "每周" + dayOfWeek;
        }
        if (dayOfWeek.contains(",")) {
            return "每周" + dayOfWeek;
        }
        if (dayOfWeek.matches("\\d+")) {
            return "每周" + getWeekDayName(Integer.parseInt(dayOfWeek));
        }
        return "每周" + dayOfWeek;
    }

    /**
     * 解析月字段
     */
    private static String parseMonth(String month) {
        if ("*".equals(month) || "?".equals(month)) {
            return "";
        }
        if (month.contains("-")) {
            return month + "月";
        }
        if (month.contains(",")) {
            return month + "月";
        }
        try {
            int m = Integer.parseInt(month);
            return m + "月";
        } catch (NumberFormatException e) {
            return month;
        }
    }

    /**
     * 获取星期名称
     */
    private static String getWeekDayName(int day) {
        switch (day) {
            case 0:
            case 7:
                return "日";
            case 1:
                return "一";
            case 2:
                return "二";
            case 3:
                return "三";
            case 4:
                return "四";
            case 5:
                return "五";
            case 6:
                return "六";
            default:
                return String.valueOf(day);
        }
    }
}
