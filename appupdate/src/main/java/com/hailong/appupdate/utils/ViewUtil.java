package com.hailong.appupdate.utils;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;

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

    @SuppressLint("NewApi")
    public static void initGridViewMeasure(GridView gv, BaseAdapter adapter, int size) {
        int totalHeight = 0;
        if (adapter.getCount() % size == 0) {
            for (int i = 0, len = adapter.getCount(); i < len / size; i++) {
                // listAdapter.getCount()返回数据项的数目
                View listItem = adapter.getView(i, null, gv);
                // 计算子项View 的宽高
                listItem.measure(0, 0);
                // 统计所有子项的总高度
                totalHeight += listItem.getMeasuredHeight();
            }
        } else {
            for (int i = 0, len = adapter.getCount(); i < (len / size) + 1; i++) {
                // listAdapter.getCount()返回数据项的数目
                View listItem = adapter.getView(i, null, gv);
                // 计算子项View 的宽高
                listItem.measure(0, 0);
                // 统计所有子项的总高度
                totalHeight += listItem.getMeasuredHeight();
            }
        }
        ViewGroup.LayoutParams params = gv.getLayoutParams();
        params.height = totalHeight + (gv.getVerticalSpacing() * ((adapter.getCount() - 1)) / size);
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        gv.setLayoutParams(params);
    }

    //动态测量listview高度
    public static void initListViewMeasure(ListView lv, BaseAdapter adapter) {
        //动态测量listview高度
        int totalHeight = 0;
        for (int i = 0, len = adapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = adapter.getView(i, null, lv);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = lv.getLayoutParams();
        params.height = totalHeight + (lv.getDividerHeight() * (adapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        lv.setLayoutParams(params);
    }

}
