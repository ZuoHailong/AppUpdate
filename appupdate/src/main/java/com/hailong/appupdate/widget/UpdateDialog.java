package com.hailong.appupdate.widget;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;

import com.hailong.appupdate.bean.DownloadParamsBean;
import com.hailong.appupdate.R;
import com.hailong.appupdate.download.DownloadListener;
import com.hailong.appupdate.download.DownloadService;
import com.hailong.appupdate.utils.ApkUtil;
import com.hailong.appupdate.utils.ImageUtil;
import com.hailong.appupdate.utils.ViewUtil;
import com.hailong.appupdate.utils.WeakHandler;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Describe：更新询问框
 * Created by ZuoHailong on 2019/5/25.
 */
public class UpdateDialog extends DialogFragment implements View.OnClickListener {

    private static final int MSG_WHAT_PROGRESS = 101;
    private static final int MSG_WHAT_REQUEST_PERMISSION_SET = 102;
    private static final int MSG_WHAT_DOWNLOAD_START = 103;

    private ImageView ivTop;
    private TextView tvTitle, tvNewVerName, tvConfirm, tvCancle, tvDownloadStatus, tvProgress;
    private Group groupProgress;
    private ListView lvUpdateContent;
    private ProgressBar progressBar;

    private static Context context;
    private static UpdateDialog updateDialog;

    private boolean isForce;
    private String[] content;
    private String title, newVerName, apkUrl, confirmText, cancleText;
    private int topResId, confirmBgColor, cancelBgColor, confirmBgResource, cancelBgResource, progressDrawable;
    private long fileContentLength;
    private boolean breakpoint;

    private DownloadService.DownloadBinder downloadBinder;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadService.DownloadBinder) service;
        }

    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        setCancelable(false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.appupdate_dialogfrag_update, container);
        initView(view);

        /*使用service进行下载，支持断点下载*/
        Intent intent = new Intent(context, DownloadService.class);
        context.startService(intent); // 启动服务
        context.bindService(intent, connection, BIND_AUTO_CREATE); // 绑定服务

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        context.unbindService(connection);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvConfirm) {//立即更新
            if (ViewUtil.fastDoubleClick())
                return;
            tvDownloadStatus.setVisibility(View.GONE);
            /*获取写入权限*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {// >= 6.0
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    return;
                }
            }
            update();
        } else if (v.getId() == R.id.tvCancle) {//暂不更新
            dismiss();
            tvDownloadStatus.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                update();
            } else { // 权限被拒绝
                handler.postDelayed(() -> handler.sendEmptyMessage(MSG_WHAT_REQUEST_PERMISSION_SET), 500);
            }
            return;
        }
    }

    /**
     * 实例化UpdateDialog
     *
     * @param context
     * @return
     */
    public static UpdateDialog newInstance(Context context) {
        if (updateDialog == null) {
            synchronized (UpdateDialog.class) {
                if (updateDialog == null) {
                    updateDialog = new UpdateDialog();
                }
            }
        }
        UpdateDialog.context = context;
        return updateDialog;
    }


    public UpdateDialog setUpdateForce(boolean isForce) {
        this.isForce = isForce;
        return updateDialog;
    }

    public UpdateDialog setConfirmBgColor(int confirmBgColor) {
        this.confirmBgColor = confirmBgColor;
        return updateDialog;
    }

    public UpdateDialog setCancelBgColor(int cancelBgColor) {
        this.cancelBgColor = cancelBgColor;
        return updateDialog;
    }

    public UpdateDialog setConfirmBgResource(int confirmBgResource) {
        this.confirmBgResource = confirmBgResource;
        return updateDialog;
    }

    public UpdateDialog setCancelBgResource(int cancelBgResource) {
        this.cancelBgResource = cancelBgResource;
        return updateDialog;
    }

    public UpdateDialog setProgressDrawable(int progressDrawable) {
        this.progressDrawable = progressDrawable;
        return updateDialog;
    }

    public UpdateDialog setTopResId(int topResId) {
        this.topResId = topResId;
        return updateDialog;
    }

    public UpdateDialog setApkContentLength(long apkContentLength) {
        this.fileContentLength = apkContentLength;
        return updateDialog;
    }

    public UpdateDialog setUpdateContent(String[] content) {
        this.content = content;
        return updateDialog;
    }

    public UpdateDialog setNewVernName(String newVerName) {
        this.newVerName = newVerName;
        return updateDialog;
    }

    public UpdateDialog setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
        return updateDialog;
    }

    public UpdateDialog setConfirmText(String confirmText) {
        this.confirmText = confirmText;
        return updateDialog;
    }

    public UpdateDialog setTitle(String title) {
        this.title = title;
        return updateDialog;
    }

    public UpdateDialog setCancelText(String cancelText) {
        this.cancleText = cancelText;
        return updateDialog;
    }

    public UpdateDialog isBreakpoint(boolean breakpoint) {
        this.breakpoint = breakpoint;
        return updateDialog;
    }

    private void initView(View view) {
        ivTop = view.findViewById(R.id.ivTop);
        tvConfirm = view.findViewById(R.id.tvConfirm);
        tvConfirm.setOnClickListener(this);
        tvCancle = view.findViewById(R.id.tvCancle);
        tvCancle.setOnClickListener(this);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvNewVerName = view.findViewById(R.id.tvNewVersionName);
        groupProgress = view.findViewById(R.id.groupProgress);
        lvUpdateContent = view.findViewById(R.id.lvUpdateContent);
        progressBar = view.findViewById(R.id.progressBar);
        tvProgress = view.findViewById(R.id.tvProgress);
        tvDownloadStatus = view.findViewById(R.id.tvDownloadStatus);

        /**
         * 根据外部设置，初始化Dialog
         */
        if (confirmBgColor != 0) {
            tvConfirm.setBackgroundColor(confirmBgColor);
        }
        if (cancelBgColor != 0) {
            tvCancle.setBackgroundColor(cancelBgColor);
        }
        if (confirmBgResource != 0) {
            tvConfirm.setBackgroundResource(confirmBgResource);
        }
        if (cancelBgResource != 0) {
            tvCancle.setBackgroundResource(cancelBgResource);
        }
        if (progressDrawable != 0) {
            progressBar.setProgressDrawable(context.getResources().getDrawable(progressDrawable));
        }
        if (topResId != 0)
            ivTop.setImageResource(topResId);
        if (isForce) {
            tvCancle.setVisibility(View.GONE);
        } else {
            tvCancle.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(newVerName))
            tvNewVerName.setText(newVerName);
        if (!TextUtils.isEmpty(title))
            tvTitle.setText(title);
        if (!TextUtils.isEmpty(confirmText))
            tvConfirm.setText(confirmText);
        if (!TextUtils.isEmpty(cancleText))
            tvCancle.setText(cancleText);
        UpdateContentAdapter adapter = new UpdateContentAdapter(context, content);
        lvUpdateContent.setAdapter(adapter);

    }

    /**
     * 弹窗，请求跳转到设置页面开启权限
     */
    private void requestPermissionSet() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.appupdate_tip))
                .setMessage(getString(R.string.appupdate_no_write_permission))
                .setNegativeButton(getString(R.string.appupdate_cancel), (dialog, which) -> dialog.dismiss())
                .setPositiveButton(getString(R.string.appupdate_open_permission), (dialog, which) -> toPermissionSettingPage())
                .create().show();
    }

    /**
     * 跳转权限设置页面
     */
    private void toPermissionSettingPage() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * “立即更新”
     */
    private void update() {
        if (!TextUtils.isEmpty(apkUrl)) {
            DownloadParamsBean bean = new DownloadParamsBean();
            bean.setFileDownloadUrl(apkUrl);
            bean.setFileContentLength(fileContentLength);
            bean.setBreakpoint(breakpoint);
            /*下载监听，在service的AsyncTask中回调*/
            downloadBinder.startDownload(context, bean, new DownloadListener() {
                @Override
                public void onStart() {
                    handler.sendEmptyMessage(MSG_WHAT_DOWNLOAD_START);
                }

                @Override
                public void onProgress(int progress) {
                    tvProgress.setText(progress + "%");
                    progressBar.setProgress(progress);
                }

                @Override
                public void onSuccess(String filePath) {
                    tvConfirm.setVisibility(View.VISIBLE);
                    if (!isForce)
                        tvCancle.setVisibility(View.VISIBLE);
                    groupProgress.setVisibility(View.GONE);
                    tvProgress.setText("0%");
                    progressBar.setProgress(0);
                    ApkUtil.installApp(context, filePath);
                    if (!isForce)
                        dismiss();
                }

                @Override
                public void onFailed() {
                    tvConfirm.setVisibility(View.VISIBLE);
                    if (!isForce)
                        tvCancle.setVisibility(View.VISIBLE);
                    groupProgress.setVisibility(View.GONE);
                    tvProgress.setText("0%");
                    progressBar.setProgress(0);
                    // TODO 模糊化
                    tvDownloadStatus.setBackground(new BitmapDrawable(getResources(), ImageUtil.blur(context, ImageUtil.screenShotView(lvUpdateContent),
                            lvUpdateContent.getWidth(), lvUpdateContent.getHeight())));
                    tvDownloadStatus.setVisibility(View.VISIBLE);
                }

                @Override
                public void onPaused() {

                }

                @Override
                public void onCanceled() {

                }
            });
        }
    }

    /**
     * 更新progress、请求权限
     */
    private WeakHandler handler = new WeakHandler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MSG_WHAT_PROGRESS) {
                Bundle bundle = msg.getData();
                if (bundle != null) {
                    int progress = bundle.getInt("progress");
                    if (progress > progressBar.getProgress()) {
                        progressBar.setProgress(progress);
                        tvProgress.setText(progress + "%");
                    }
                }
            } else if (msg.what == MSG_WHAT_REQUEST_PERMISSION_SET) {
                requestPermissionSet();
            } else if (msg.what == MSG_WHAT_DOWNLOAD_START) {
                tvConfirm.setVisibility(View.GONE);
                tvCancle.setVisibility(View.GONE);
                groupProgress.setVisibility(View.VISIBLE);
            }
            return true;
        }
    });

    /**
     * 版本更新内容ListAdapter
     */
    private static class UpdateContentAdapter extends BaseAdapter {

        private Context context;
        private String[] array;

        public UpdateContentAdapter(Context context, String[] array) {
            this.context = context;
            this.array = array;
        }

        @Override
        public int getCount() {
            if (array == null)
                return 0;
            return array.length;
        }

        @Override
        public Object getItem(int i) {
            return array[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(context).inflate(R.layout.appupdate_listitem_update_content, null);
                viewHolder.tv_content = view.findViewById(R.id.tv_content);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.tv_content.setText(array[position]);
            return view;
        }

        private class ViewHolder {
            TextView tv_content;
        }
    }

}
