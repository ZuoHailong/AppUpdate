package com.hailong.appupdate.exmple;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.hailong.appupdate.AppUpdateManager;

public class MainActivity extends FragmentActivity {

    //    private static String[] arrayContent = new String[]{"1、实现apkUrl形式的版本更新功能", "2、实现stream形式的版本更新功能", "3、优化用户体验", "4、修复一些bug"};
    private static String[] arrayContent = new String[]{"1、实现apkUrl形式的版本更新功能", "2、实现stream形式的版本更新功能", "3、优化用户体验", "4、修复一些bug", "1、实现apkUrl形式的版本更新功能", "2、实现stream形式的版本更新功能", "3、优化用户体验", "4、修复一些bug", "1、实现apkUrl形式的版本更新功能", "2、实现stream形式的版本更新功能", "3、优化用户体验", "4、修复一些bug", "1、实现apkUrl形式的版本更新功能", "2、实现stream形式的版本更新功能", "3、优化用户体验", "4、修复一些bug"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateByApkUrl();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 以apkUrl的下载链接的形式更新版本
     */
    private void updateByApkUrl() {
        AppUpdateManager.Builder builder = new AppUpdateManager.Builder(MainActivity.this);
        //TODO github上的文件下载极慢（甚至连接失败），测试时可以更换为自己服务器上的文件链接
//        builder.apkUrl("https://github.com/ZuoHailong/AppUpdate/blob/master/example/file/appupdate_example.apk")
        builder.apkUrl("https://drumbeat-update-app.oss-cn-hangzhou.aliyuncs.com/Centralizer/develop/AppManager.apk")
//                .newVerName("2.2.2")
                .updateForce(false)
                .updateContent(arrayContent)
                .build();
    }
}
