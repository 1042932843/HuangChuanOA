package com.gzdefine.huangcuangoa.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;
import android.widget.Toast;

import com.gzdefine.huangcuangoa.App;
import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.listener.PgyUpdateManagerListener;
import com.gzdefine.huangcuangoa.util.DataCleanManager;
import com.gzdefine.huangcuangoa.util.ToastUtil;
import com.pgyersdk.crash.PgyCrashManager;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

public class SettingActivity extends BaseActivity {
    @Bind(R.id.tv_cookies_size)
    TextView cookiesSize;
    @Bind(R.id.tv_version)
    TextView version;
    private SettingActivity mainActivity;
    @Override
    public int initContentID() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = this;
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        setAppTitle("设置");
        init();
    }

    private void init() {
        try {
            // ---get the package info---
            PackageManager pm = getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
            String versionName = pi.versionName;
            version.setText(versionName + " for Android");
            cookiesSize.setText(DataCleanManager.getTotalCacheSize(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.check_version)
    void check() {
//        PgyUpdateManager.register(SettingActivity.this,getString(R.string.fileProvider),new PgyUpdateManagerListener(this));
        PgyUpdateManager.register(SettingActivity.this,
                new UpdateManagerListener() {
                    @Override
                    public void onUpdateAvailable(final String result) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                        final AppBean bean = getAppBeanFromString(result);
                        builder.setIcon(R.mipmap.desktop);
                        builder.setTitle("发现新版本：" + bean.getVersionName());
                        builder.setMessage(bean.getReleaseNote());
                        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (checkPermission(mainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                        //获取权限后的操作。读取文件
                                        startDownloadTask(mainActivity, bean.getDownloadURL());
                                    } else {
                                        //请求权限
                                        ActivityCompat.requestPermissions(mainActivity,
                                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                PERMISSION_REQUEST_CODE);
                                    }
                                } else {
                                    startDownloadTask(mainActivity, bean.getDownloadURL());
                                }
                            }
                        });
                        builder.setNegativeButton("取消", null);
                        AlertDialog dialog = builder.create();
                        if (!isFinishing()) {
                            dialog.show();
                        }

                    }

                    @Override
                    public void onNoUpdateAvailable() {
                        Toast.makeText(getApplicationContext(),"已经是最新版本！",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @OnClick(R.id.img_back)
    void  back(){
        finish();
    }

    @OnClick(R.id.clean_cookies)
    void cookies(){
        DataCleanManager.clearAllCache(this);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    ToastUtil.showShort(SettingActivity.this,"清除成功");
                    cookiesSize.setText(DataCleanManager.getTotalCacheSize(SettingActivity.this));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @OnClick(R.id.loginout)
    void  loginout(){
        //关闭聊天服务
        JPushInterface.setAlias(this,"",null);
        JPushInterface.clearAllNotifications(this);
        App.me().logout();
        startActivity(new Intent(this,LoginActivity.class));
        App.me().finishAll();
    }
    /**
     * 检测权限是否授权
     *
     * @return
     */
    private boolean checkPermission(Context context, String permission) {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, permission);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //得到了授权
                    Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
                    try {
                        PgyUpdateManager.register(mainActivity, new PgyUpdateManagerListener(this));
                    } catch (Exception e) {
                        PgyCrashManager.reportCaughtException(this, e);
                        App.me().toast("检查更新失败");
                    }
                } else {
                    //未授权
                    Toast.makeText(this, "您已禁止自动更新", Toast.LENGTH_SHORT).show();
                    Uri uri = Uri.parse("https://www.pgyer.com/RrIO");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }
}
