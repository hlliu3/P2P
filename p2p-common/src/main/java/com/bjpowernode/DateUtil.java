package com.bjpowernode;

import org.omg.PortableInterceptor.INACTIVE;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @className:IntelliJ IDEA
 * @description:
 * @author:
 * @date:2019-06-20 20:38
 */
public class DateUtil {

    /**
     * 查询今天后几天的日期
     * @param days
     * @return
     */
    public static Date getDateAfterDays(Integer days){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }

    /**
     * 查询今天后几月的时间
     *
     */
    public static Date getDateAfterMonths(Integer months){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, months);
        return calendar.getTime();
    }

    public static String createTimeStamp() {
        return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    }
}
