package com.hailong.appupdate;

import android.app.Activity;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.hailong.appupdate.widget.UpdateDialog;

/**
 * Describe：app版本更新管理器
 * Created by ZuoHailong on 2019/5/25.
 */
public class AppUpdateManager {

    private AppUpdateManager(Builder builder) {
        UpdateDialog updateDialog = UpdateDialog.newInstance(builder.context);
        updateDialog.setUpdateContent(builder.updateContent)
                .setUpdateForce(builder.updateForce)
                .setNewVernName(builder.newVerName)
                .setTopResId(builder.topResId)
                .setConfirmBgColor(builder.confirmBgColor)
                .setCancelBgColor(builder.cancelBgColor)
                .setConfirmBgResource(builder.confirmBgResource)
                .setCancelBgResource(builder.cancelBgResource)
                .setProgressDrawable(builder.progressDrawable)
                .setApkUrl(builder.apkUrl)
                .setTitle(builder.title)
                .setConfirmText(builder.confirmText)
                .setCancelText(builder.cancleText)
                .setApkContentLength(builder.apkContentLength)
                .isBreakpoint(builder.breakpoint)
                .show(builder.context.getFragmentManager(), "update");
    }

    /**
     * UpdateAppManager的构建器
     */
    public static class Builder {

        /*必选字段*/
        private Activity context;

        /*可选字段*/
        private boolean updateForce;//是否强制更新，默认false
        private String apkUrl;//apk下载链接
        private String[] updateContent;//版本更新内容
        private String title;
        private String newVerName;
        private String confirmText;
        private String cancleText;
        private int topResId;//标题背景的资源图片
        private int confirmBgColor;//确定按钮背景色
        private int cancelBgColor;//取消按钮背景色
        private int confirmBgResource;//确定按钮背景
        private int cancelBgResource;//取消按钮背景
        private int progressDrawable;//进度条样式
        private long apkContentLength;//apk文件大小
        private boolean breakpoint;//是否支持断点下载，默认不支持（针对访问接口获取文件流的下载方式）

        /**
         * 构建器
         *
         * @param activity
         */
        public Builder(@NonNull Activity activity) {
            this.context = activity;
        }




        /**
         * 设置apk下载链接
         *
         * @param apkUrl
         * @return
         */
        public Builder apkUrl(String apkUrl) {
            this.apkUrl = apkUrl;
            return this;
        }

        /**
         * 设置版本更新内容
         *
         * @param updateContent
         * @return
         */
        public Builder updateContent(String[] updateContent) {
            this.updateContent = updateContent;
            return this;
        }

        /**
         * 是否必须更新
         *
         * @param isForce
         * @return
         */
        public Builder updateForce(boolean isForce) {
            this.updateForce = isForce;
            return this;
        }

        /**
         * 设置新版本号
         *
         * @param newVerName
         * @return
         */
        public Builder newVerName(String newVerName) {
            this.newVerName = newVerName;
            return this;
        }

        /**
         * 更新框标题
         *
         * @param title
         * @return
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * 设置确认按钮文字
         *
         * @param confirmText
         * @return
         */
        public Builder confirmText(String confirmText) {
            this.confirmText = confirmText;
            return this;
        }

        /**
         * 设置取消按钮文字
         *
         * @param cancelText
         * @return
         */
        public Builder cancelText(String cancelText) {
            this.cancleText = cancelText;
            return this;
        }

        /**
         * apk文件大小
         *
         * @param apkContentLength
         * @return
         */
        public Builder apkContentLength(long apkContentLength) {
            this.apkContentLength = apkContentLength;
            return this;
        }

        /**
         * 是否支持断点下载，默认不支持
         * 注意：采用直接指向文件的链接方式下载，不需要作此设置，默认支持断点下载；
         * 当采用访问接口获取二进制流的方式下载时，需要向server端开发人员核实是否支持断点下载。
         *
         * @param breakpoint
         * @return
         */
        public Builder breakpoint(boolean breakpoint) {
            this.breakpoint = breakpoint;
            return this;
        }




        //TODO
        //TODO
        //TODO
        //TODO

        /**
         * 设置标题背景的资源图片
         *
         * @param topResId
         * @return
         */
        public Builder topResId(@DrawableRes int topResId) {
            this.topResId = topResId;
            return this;
        }

        /**
         * 设置确定按钮背景色
         *
         * @param color
         * @return
         */
        public Builder confirmBgColor(@ColorInt int color) {
            this.confirmBgColor = confirmBgColor;
            return this;
        }

        /**
         * 设置确定按钮背景
         *
         * @param resid
         * @return
         */
        public Builder confirmBgResource(@DrawableRes int resid) {
            this.confirmBgResource = resid;
            return this;
        }

        /**
         * 设置取消按钮背景色
         *
         * @param color
         * @return
         */
        public Builder cancelBgColor(@ColorInt int color) {
            this.cancelBgColor = cancelBgColor;
            return this;
        }

        /**
         * 设置取消按钮背景
         *
         * @param resid
         * @return
         */
        public Builder cancelBgResource(@DrawableRes int resid) {
            this.cancelBgResource = resid;
            return this;
        }

        /**
         * 设置进度条样式
         *
         * @param resid
         * @return
         */
        public Builder progressDrawable(@DrawableRes int resid) {
            this.progressDrawable = resid;
            return this;
        }

        /**
         * 开始构建
         *
         * @return
         */
        public AppUpdateManager build() {
            return new AppUpdateManager(this);
        }
    }

}
