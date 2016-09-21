package com.example.ben.zhihudaily.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Zhou bangquan on 16/9/13.
 */

public class DateUtils {
    public static String msToDate(long ms) {
        DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(ms);
            return formatter.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static int daysOfMonth(long ms) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ms);
        return calendar.getActualMaximum(Calendar.DATE);
    }

    public static String dateWithWeekday(long ms) {
        DateFormat formatter = new SimpleDateFormat("MM月dd日");
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(ms);
            int weekday = calendar.get(Calendar.DAY_OF_WEEK);
            String date = formatter.format(calendar.getTime());
            switch (weekday) {
                case Calendar.SUNDAY:
                    return date + " 星期日";
                case Calendar.MONDAY:
                    return date + " 星期一";
                case Calendar.TUESDAY:
                    return date + " 星期二";
                case Calendar.WEDNESDAY:
                    return date + " 星期三";
                case Calendar.THURSDAY:
                    return date + " 星期四";
                case Calendar.FRIDAY:
                    return date + " 星期五";
                case Calendar.SATURDAY:
                    return date + " 星期六";
                default:
                    return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
