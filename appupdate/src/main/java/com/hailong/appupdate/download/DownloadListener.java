package com.hailong.appupdate.download;

/**
 * Date : 2019-07-06
 */
public interface DownloadListener {

    void onStart();

    void onProgress(int progress);

    void onSuccess(String filePath);

    void onFailed();

    void onPaused();

    void onCanceled();

}
