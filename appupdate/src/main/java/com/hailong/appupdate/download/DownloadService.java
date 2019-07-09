package com.hailong.appupdate.download;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.hailong.appupdate.bean.DownloadParamsBean;
import com.hailong.appupdate.utils.ApkUtil;

import java.io.File;

/**
 * Date : 2019-07-06
 */
public class DownloadService extends Service {

    private DownloadTask downloadTask;

    private DownloadBinder mBinder = new DownloadBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void recycle() {
        downloadTask = null;
    }

    /**
     * Binder：管理Service
     */
    public class DownloadBinder extends Binder {

        private Context context;
        private String downloadUrl;

        /**
         * 开始下载
         *
         * @param context
         * @param downloadParamsBean downloadParamsBean.getFileContentLength() 待下载文件大小：直接指向文件的下载链接不需要传此参数；当通过访问接口，以接口返回二进制流的形式下载时，此值必需。
         * @param downloadListener   下载监听
         */
        public void startDownload(Context context, DownloadParamsBean downloadParamsBean, DownloadListener downloadListener) {
            this.context = context;
            this.downloadUrl = downloadParamsBean.getFileDownloadUrl();
            downloadTask = new DownloadTask(context, downloadParamsBean, downloadListener);
            downloadTask.execute(this.downloadUrl);
        }

        public void pauseDownload() {
            if (downloadTask != null) {
                downloadTask.pauseDownload();
            }
        }

        public void cancelDownload() {
            if (downloadTask != null) {
                downloadTask.cancelDownload();
            } else {
                if (downloadUrl != null) {
                    // 取消下载时需将文件删除
                    String fileName = ApkUtil.getApkName(downloadUrl);
                    String directory = ApkUtil.getApkFileDir(context);
                    File file = new File(directory + fileName);
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
        }


    }

}
