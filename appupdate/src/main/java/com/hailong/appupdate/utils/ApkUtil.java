package com.hailong.appupdate.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by ZuoHailong on 2019/7/5.
 */
public class ApkUtil {

    /**
     * 判断 APP 是否安装
     *
     * @param context     活动对应的上下文对象
     * @param packageName 需要检查的应用包名
     * @return
     */
    public static boolean isInstalled(Context context, String packageName) {
        try {
            Process process = Runtime.getRuntime().exec("pm list package -3");
            BufferedReader bis = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = bis.readLine()) != null) {
                System.out.println("MainActivity.runCommand, line=" + line.substring(8, line.length()));
                if (packageName.equals(line.substring(8, line.length()))) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("MainActivity.runCommand,e=" + e);
        }
        return false;
    }

    /**
     * 打开某一应用
     *
     * @param context     活动对应上下文对象
     * @param packagename 需要打开的应用包名
     */
    public static void openApp(Activity context, String packagename) {

        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = context.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            // 设置ComponentName 参数1:packagename 参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);

            intent.setComponent(cn);
            context.startActivity(intent);
        }
    }

    /**
     * 获取apk下载存储的文件夹路径（不包含apkName）
     *
     * @param context
     * @return
     */
    public static String getApkFileDir(Context context) {
        return FileUtils.newInstance(context).getTempPath().getAbsolutePath();
    }

    /**
     * 安装应用
     *
     * @param context
     * @param apkPath
     */
    public static void installApp(Context context, String apkPath) {
        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".appupdate.fileProvider", new File(apkPath));
        } else {
            uri = Uri.fromFile(new File(apkPath));
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 卸载应用，android 9.0失效，暂未解决
     *
     * @param context
     * @param packageName
     */
    public static void unstallApp(Activity context, String packageName) {

        Intent uninstall_intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
        uninstall_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        uninstall_intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        uninstall_intent.setData(Uri.parse("package:" + packageName));
        context.startActivity(uninstall_intent);
        context.finish();
    }

    /**
     * 从apkUrl中截取apkName，截取失败定为"temp.apk"
     *
     * @param apkUrl
     * @return
     */
    public static String getApkName(String apkUrl) {
        if (TextUtils.isEmpty(apkUrl))
            return null;
        String apkName = null;
        if (apkUrl.endsWith(".apk"))
            apkName = apkUrl.substring(apkUrl.lastIndexOf("/") + 1);
        if (TextUtils.isEmpty(apkName))
            apkName = "temp.apk";
        return apkName;
    }

}
