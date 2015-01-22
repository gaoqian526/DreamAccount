package com.gao.dreamaccount.util;


import com.gao.dreamaccount.R;

import java.util.Random;

public class Constant {
    public static final String appID = "ccaa0ca91f4b74e3";
    public static final String appKey = "788b63eea64d2aa5";
    public static final String KEY_IS_SET_PWD = "set_pwd";
    public static final String KEY_PWD = "pwd";

    public static final String MONTH_FORMATE = "";

    public static String[] STRING_MONTH = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"};
    // 一月--浅蓝色、 二月--浅紫色#DDA0DD、 三月--浅绿色、 四月--深绿色、
    // 五月--大红色、 六月--紫色、 七月--深红色、 八月--黄色、
    // 九月--橘黄色、 十月--草黄色、 十一月--湖蓝色09dcd5、十二月--白色。
    public static int[] COLOR_MONTH = {R.color.color_light_blue, R.color.color_light_purple, R.color.color_light_green, R.color.color_dark_green,
            R.color.color_light_red, R.color.color_dark_purple, R.color.color_dark_red, R.color.color_hu_bule, R.color.color_light_yellow,
            R.color.color_light_orange, R.color.color_grass_yellow, R.color.color_worm_gray};
    public static String[] STRING_DAY = {"1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th", "10th ", "11th ", "12th ", "13th", "14th", "15th ",
            "16th ", "17th ", "18th ", "19th ", "20th", "21st ", "22nd ", "23rd ", "24th", "25th ", "26th ", "27th ", "28th", "29th ", "30th ", "31st",};

    public static int GET_COLOR_RADOME() {
        Random random = new Random();
        return R.color.color_dark_orange;
    }
}
