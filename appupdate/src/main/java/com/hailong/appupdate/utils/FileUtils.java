package com.hailong.appupdate.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Created by ZuoHailong on 2019/7/8.
 */
public class FileUtils {

    private static FileUtils fileUtils;

    private static String APPS_ROOT_DIR;
    private static final String IMAGE_PATH = "/Image";
    private static final String TEMP_PATH = "/Temp";
    private static final String APP_CRASH_PATH = "/AppCrash";
    private static final String FILE_PATH = "/File";

    public FileUtils(Context context) {
        APPS_ROOT_DIR = getExternalStorePath() + File.separator + context.getPackageName();
    }

    public static FileUtils newInstance(Context context) {
        if (fileUtils == null) {
            synchronized (FileUtils.class) {
                if (fileUtils == null) {
                    fileUtils = new FileUtils(context);
                }
            }
        }
        return fileUtils;
    }

    /**
     * 外置存储卡的路径
     *
     * @return
     */
    @Nullable
    public static String getExternalStorePath() {
        if (isExistExternalStore()) {
            return Environment.getExternalStorageDirectory().getPath();
        }
        return null;
    }

    /**
     * 是否有外存卡
     *
     * @return
     */
    public static boolean isExistExternalStore() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }


    /**
     * 返回图片存放目录
     *
     * @return File
     */
    @Nullable
    public File getImagePath() {
        return create(APPS_ROOT_DIR + IMAGE_PATH);
    }

    /**
     * 返回临时存放目录
     *
     * @return File
     */
    @Nullable
    public File getTempPath() {
        return create(APPS_ROOT_DIR + TEMP_PATH);
    }

    /**
     * 存储日志文件目录
     *
     * @return File
     */
    public File getAppCrashPath() {
        return create(APPS_ROOT_DIR + APP_CRASH_PATH);
    }

    /**
     * 存储文件目录
     *
     * @return File
     */
    public File getFilePath() {
        return create(APPS_ROOT_DIR + FILE_PATH);
    }

    /**
     * 7.0以上拍照 安装应用等文件问题
     *
     * @param context context
     * @param file    file
     * @return Uri
     */
    public static Uri getFileUri(Context context, File file) {
        Uri fileUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }

    /**
     * 获取应用下的.fileprovider的路径
     *
     * @param context
     * @return
     */
    public static String getFileProviderName(Context context) {
        return context.getPackageName() + ".fileprovider";
    }


    /**
     * 获取目录下的文件列表
     *
     * @param strPath strPath
     */
    public static LinkedList<File> listLinkedFiles(String strPath) {
        File dir = new File(strPath);
        File file[] = dir.listFiles();
        LinkedList<File> list = null;
        if (dir.exists() && file != null && file.length > 0) {
            list = new LinkedList<>();
            Collections.addAll(list, file);
        }
        return list;
    }

    /**
     * 删除多个文件
     *
     * @param filesName filesName
     */
    public static void deleteListFiles(String filesName) {
        LinkedList<File> files = listLinkedFiles(filesName);
        if (files != null && files.size() > 0) {
            for (File file : files) {
                file.delete();
            }
        }
    }

    /**
     * 删除指定文件
     *
     * @param filesName filesName
     */
    public static void deleteFile(String filesName) {
        File file = new File(filesName);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 将Assets中的文件拷贝到Sdcard指定路径
     *
     * @param context
     * @param assetsFileName
     * @param toPath         Sdcard指定路径
     */
    public static void copyAssetsToSdcard(Context context, String assetsFileName, String toPath) {
        try {
            boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
            if (!sdCardExist)
                return;

            File dirFile = new File(toPath);
            if (!dirFile.exists())
                dirFile.mkdirs();

            File file = new File(toPath, assetsFileName);
            if (file.exists())
                return;

            InputStream ins = context.getAssets().open(assetsFileName);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = ins.read(buffer)) != -1) {
                fos.write(buffer, 0, length);
            }
            fos.flush();
            fos.close();
            ins.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File create(String path) {
        if (!isExistExternalStore()) {
            return null;
        }
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }

}
