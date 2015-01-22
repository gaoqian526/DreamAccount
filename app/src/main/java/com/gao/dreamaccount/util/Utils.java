package com.gao.dreamaccount.util;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Utils {

    /**
     * 格式化double
     */
    public static String formateDouble(double data) {
        DecimalFormat df = new DecimalFormat();
        String pattern = ",###,###.##\u00A4";
        df.applyPattern(pattern);
        return df.format(data);
    }

    /**
     * 格式化int
     */
    public static String formateInteger(int num) {
        // 0 代表前面补充0
        // 4 代表长度为4
        // d 代表参数为正数型
        String str = String.format("%02d", num);
        return str;
    }


    /**
     * 获取唯一字符串
     */
    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        return str.replaceAll("-", "");
    }

    /**
     * 获取设定时间到现在的时间差
     */
    public static String getDayCount(long time) {
        if (System.currentTimeMillis() > time) {
            return "已到期";
        } else {
            long l = time - System.currentTimeMillis();
            long day = l / (24 * 60 * 60 * 1000);
            long hour = (l / (60 * 60 * 1000) - day * 24);
            long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
            long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
            return "" + day + "天";
        }
    }

    // 格式到天
    public static String getDay(long time) {
        return new SimpleDateFormat("yyyy-MM-dd").format(time);
    }

    // 格式到天
    public static String getDoneDay(long time) {
        return new SimpleDateFormat("yy-MM-dd").format(time);
    }

    // 格式到天 不包含年
    public static String getDayNoYear(long time) {
        return new SimpleDateFormat("MM-dd").format(time);
    }

    /**
     * 获取给定时间月份
     */
    public static int getTimeMonth(long time) {
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        int month = c.get(Calendar.MONTH) + 1; // 获取月份，0表示1月份
        return month;
    }

    /**
     * 获取当月月份
     */
    public static int getCurrentMonth() {
        final Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH) + 1; // 获取月份，0表示1月份
        return month;
    }

    /**
     * 获取当年
     */
    public static int getCurrentYear() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR); // 获取年
        return year;
    }

    /**
     * 获取sd卡
     */
    public static String getFilePath() {
        File sdDir = null;
        // 判断sd卡是否存在
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            // 获取根目录
            sdDir = Environment.getExternalStorageDirectory();
            return sdDir.getPath();
        } else {// 返回手机内存
            return Environment.getDataDirectory().getPath();
        }
    }

    /**
     * 转换时间戳
     */
    @SuppressLint("SimpleDateFormat")
    public static long getTimeTemp(String time, String format) {
        SimpleDateFormat format1 = null;
        if (TextUtils.isEmpty(format)) {
            format1 = new SimpleDateFormat("yyyy-MM-dd");
        } else {
            format1 = new SimpleDateFormat(format);
        }
        try {
            Date date;
            date = format1.parse(time);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // 获得某个月里共有几天
    public static Integer countDay(Date time) {
        Date date = time;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(cal.DATE, 1);
        cal.roll(cal.DATE, -1);
        return cal.get(cal.DAY_OF_MONTH);
    }
}
