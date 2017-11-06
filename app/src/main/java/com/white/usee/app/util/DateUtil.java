package com.white.usee.app.util;

import android.text.format.DateUtils;

import com.white.usee.app.BaseApplication;
import com.white.usee.app.R;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by white on 15-11-17.
 */
public class DateUtil {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取当前时间
     */
    public static Date getDate() {
        Date date = new Date();
        return date;
    }

    public static SimpleDateFormat getDateFormat() {
        return sdf;
    }

    /**
     * 获取当前时间(字符串形式)
     */
    public static String getDateString() {
        if (sdf == null) sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return sdf.format(date);
    }

    /**
     * 获得当前年
     */
    public static int getYear() {
        return getDate().getYear() + 1900;
    }

    /**
     * 获得当前月
     */
    public static int getMonth() {
        return getDate().getMonth();
    }

    /**
     * 获得当前日期
     */
    public static int getDay() {
        return getDate().getDate();
    }

    /**
     * 添加天数
     */
    public static Date addDay(int day) {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, day);
        return cal.getTime();
    }

    /**
     * 添加小时数
     */
    public static Date addHour(int hour) {
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        cal.setTime(date);
        cal.add(Calendar.HOUR, hour);
        return cal.getTime();
    }

    /**
     * 当前时间加上对应天数和小时数
     */
    public static Date addDayAndHour(int day, int hour) throws ParseException {
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        cal.setTime(date);
        cal.add(Calendar.DATE, day);
        cal.add(Calendar.HOUR, hour);
        return cal.getTime();
    }

    public static Date String2Date(String dateString) throws ParseException {
        return sdf.parse(dateString);
    }

    public static String Date2String(Date date) throws ParseException {
        return sdf.format(date);
    }

    /**
     * 计算输入的日期至今查几天几小时,利用getTime获取到毫秒数,然后相剪,获得差值毫秒,再计算
     */
    public static String RestDateSinceNow(String date) {

        try {
            Date dateString = String2Date(date);
            long time1 = getDate().getTime();
            long time2 = dateString.getTime();
            long dTime = time2 - time1;
            if (dTime > 0) {
                long day = dTime / (24 * 60 * 60 * 1000);
                long hour = dTime / (60 * 60 * 1000) - day * 24;
                String result = "" + day + "天" + hour + "小时";
                return result;
            }
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long RestDateSince(String date) {
        try {
            Date dateString = String2Date(date);
            long time = dateString.getTime();
            long now = new Date().getTime();
            return now - time;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(null);
    }

    public static String showDanmuOrCommentDay(String danmuDate) {
        try {
            long timeStamp = (Long.valueOf(danmuDate)*1000);
            Date date =new Date(timeStamp);
            Date nowDate = new Date();
            long restTime = nowDate.getTime() - timeStamp;
            int day = (int) Math.ceil(restTime / (1000 * 60 * 60 * 24));
            if (org.apache.commons.lang3.time.DateUtils.isSameDay(date,nowDate)) {
                return (date.getHours()>=10?date.getHours():("0"+date.getHours())) + ":" + (date.getMinutes()>=10?date.getMinutes():("0"+date.getMinutes()));
            } else {
                if (day==0) day++;
                return day + BaseApplication.getInstance().getString(R.string.days_ago);
            }
        }catch (Exception e){
            return  "时间格式错误";
        }


    }
}
