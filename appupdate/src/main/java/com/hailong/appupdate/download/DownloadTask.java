package com.hailong.appupdate.download;

import android.content.Context;
import android.os.AsyncTask;

import com.hailong.appupdate.bean.DownloadParamsBean;
import com.hailong.appupdate.utils.ApkUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 下载的AsyncTask，支持断点下载
 * Date : 2019-07-06
 */
public class DownloadTask extends AsyncTask<String, Integer, Integer> {

    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILED = 1;
    public static final int TYPE_PAUSED = 2;
    public static final int TYPE_CANCELED = 3;


    private DownloadListener listener;

    private boolean isCanceled = false;

    private boolean isPaused = false;

    private int lastProgress;

    private Context context;
    private String filePath;
    private DownloadParamsBean downloadParamsBean;

    public DownloadTask(Context context, DownloadParamsBean downloadParamsBean, DownloadListener listener) {
        this.context = context;
        this.downloadParamsBean = downloadParamsBean;
        this.listener = listener;
    }

    /**
     * OkHttp进行下载
     *
     * @param params
     * @return
     */
    @Override
    protected Integer doInBackground(String... params) {
        InputStream is = null;
        RandomAccessFile savedFile = null;
        File file = null;
        try {
            long downloadedLength = 0; // 记录已下载的文件长度
            String downloadUrl = params[0];
            String fileName = ApkUtil.getApkName(downloadUrl);
            String directory = ApkUtil.getApkFileDir(context);
            filePath = directory + File.separator + fileName;
            file = new File(filePath);
            if (file.exists()) {
                if (downloadParamsBean.getFileDownloadUrl().endsWith(".apk")) {
                    downloadedLength = file.length();
                } else if (downloadParamsBean.isBreakpoint()) {//接口返回流方式下载，是否支持断点下载需由server支持
                    downloadedLength = file.length();
                } else {//不支持断点下载
                    downloadedLength = 0;
                    file.delete();
                    file = new File(filePath);
                }
            }
            long contentLength = getContentLength(downloadUrl);
            //文件大小
            contentLength = contentLength <= 0 ? downloadParamsBean.getFileContentLength() : contentLength;
            if (contentLength == 0) {//从下载connection中无法获取文件长度
                return TYPE_FAILED;
            } else if (contentLength == downloadedLength) {
                // 已下载字节和文件总字节相等，说明已经下载完成了
                return TYPE_SUCCESS;
            }
            listener.onStart();//正式开始下载
            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            // 断点下载，指定从哪个字节开始下载
            builder.addHeader("RANGE", "bytes=" + downloadedLength + "-");
            Request request = builder.url(downloadUrl).build();
            Response response = client.newCall(request).execute();
            if (response != null) {
                is = response.body().byteStream();
                savedFile = new RandomAccessFile(file, "rw");
                // 断点下载，跳过已下载的字节
                savedFile.seek(downloadedLength);
                byte[] b = new byte[8912];
                int total = 0;
                int len;
                while ((len = is.read(b)) != -1) {
                    if (isCanceled) {
                        return TYPE_CANCELED;
                    } else if (isPaused) {
                        return TYPE_PAUSED;
                    } else {
                        total += len;
                        savedFile.write(b, 0, len);
                        // 计算已下载的百分比
                        int progress = (int) ((total + downloadedLength) * 100 / contentLength);
                        publishProgress(progress);
                    }
                }
                response.body().close();
                return TYPE_SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (savedFile != null) {
                    savedFile.close();
                }
                if (isCanceled && file != null) {
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return TYPE_FAILED;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        if (progress > lastProgress) {
            listener.onProgress(progress);
            lastProgress = progress;
        }
    }

    @Override
    protected void onPostExecute(Integer status) {
        switch (status) {
            case TYPE_SUCCESS:
                listener.onSuccess(filePath);
                break;
            case TYPE_FAILED:
                listener.onFailed();
                break;
            case TYPE_PAUSED:
                listener.onPaused();
                break;
            case TYPE_CANCELED:
                listener.onCanceled();
            default:
                break;
        }
    }

    public void pauseDownload() {
        isPaused = true;
    }


    public void cancelDownload() {
        isCanceled = true;
    }

    private long getContentLength(String downloadUrl) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();
        Response response = client.newCall(request).execute();
        if (response != null && response.isSuccessful()) {
            long contentLength = response.body().contentLength();
            response.close();
            return contentLength;
        }
        return 0;
    }

}
