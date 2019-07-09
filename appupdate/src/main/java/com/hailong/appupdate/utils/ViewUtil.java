package com.hailong.appupdate.utils;

/**
 * Created by ZuoHailong on 2019/7/8.
 */
public class ViewUtil {

    /**
     * 记录上次点击按钮的时间
     **/
    private static long lastClickTime = 0;
    /**
     * 按钮连续点击最低间隔时间 单位：毫秒
     **/
    private final static int CLICK_TIME = 1200;


    /**
     * 防止快速多次点击
     */
    public static boolean fastDoubleClick() {
        if (System.currentTimeMillis() - lastClickTime <= CLICK_TIME) {
            return true;
        }
        lastClickTime = System.currentTimeMillis();
        return false;
    }
}
