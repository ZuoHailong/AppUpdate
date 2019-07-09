package com.hailong.appupdate.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;

/**
 * Created by ZuoHailong on 2019/7/8.
 */
public class ImageUtil {

    /**
     * 对View的可视部分进行截屏
     * 注意：要截取的区域一定要有背景色的设置，比如设置为白色（#ffffff），否则会出现截取后查看图片黑屏，或者分享到微信（QQ、纷享销客等）后黑屏的问题。
     */
    public static Bitmap screenShotView(View view) {
        //开启缓存功能
        view.setDrawingCacheEnabled(true);
        //创建缓存
        view.buildDrawingCache();
        //获取缓存Bitmap
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        return bitmap;
    }

    /**
     * 图片模糊化处理
     *
     * @param context
     * @param bitmap  待处理图片
     * @param width   模糊后的bitmap宽度
     * @param height  模糊后的bitmap高度
     * @return
     */
    public static Bitmap blur(Context context, Bitmap bitmap, int width, int height) {

        //创建一个缩小后的bitmap
        Bitmap inputBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        //创建将在ondraw中使用到的经过模糊处理后的bitmap
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        //创建RenderScript，ScriptIntrinsicBlur固定写法
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        //根据inputBitmap，outputBitmap分别分配内存
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);

        //设置模糊半径取值(0-25]之间，不同半径得到的模糊效果不同
        blurScript.setRadius(25);
        blurScript.setInput(tmpIn);
        blurScript.forEach(tmpOut);

        //得到最终的模糊bitmap
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }
}
