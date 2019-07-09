package com.hailong.appupdate.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ZuoHailong on 2019/7/8.
 */
public class DownloadParamsBean implements Parcelable {
    private String fileDownloadUrl;
    private long fileContentLength;
    private boolean isBreakpoint;

    public DownloadParamsBean() {
    }

    protected DownloadParamsBean(Parcel in) {
        fileDownloadUrl = in.readString();
        fileContentLength = in.readLong();
        isBreakpoint = in.readByte() != 0;
    }

    public static final Creator<DownloadParamsBean> CREATOR = new Creator<DownloadParamsBean>() {
        @Override
        public DownloadParamsBean createFromParcel(Parcel in) {
            return new DownloadParamsBean(in);
        }

        @Override
        public DownloadParamsBean[] newArray(int size) {
            return new DownloadParamsBean[size];
        }
    };

    public String getFileDownloadUrl() {
        return fileDownloadUrl;
    }

    public void setFileDownloadUrl(String fileDownloadUrl) {
        this.fileDownloadUrl = fileDownloadUrl;
    }

    public long getFileContentLength() {
        return fileContentLength;
    }

    public void setFileContentLength(long fileContentLength) {
        this.fileContentLength = fileContentLength;
    }

    public boolean isBreakpoint() {
        return isBreakpoint;
    }

    public void setBreakpoint(boolean breakpoint) {
        isBreakpoint = breakpoint;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fileDownloadUrl);
        dest.writeLong(fileContentLength);
        dest.writeByte((byte) (isBreakpoint ? 1 : 0));
    }
}
