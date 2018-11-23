/*
 * Copyright © 2015 珠海云集软件科技有限公司.
 * Website：http://www.YunJi123.com
 * Mail：dev@yunji123.com
 * Tel：+86-0756-8605060
 * QQ：340022641(dove)
 * Author：dove
 */

package com.gzdefine.huangcuangoa.listener;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;

import com.gzdefine.huangcuangoa.BuildConfig;
import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.util.StringUtil;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.UpdateManagerListener;

import java.io.File;


public class PgyUpdateManagerListener extends UpdateManagerListener {
    public static final int PERMISSION_DENIEG = 1;//权限不足，权限被拒绝的时候
    final Activity activity;

    public PgyUpdateManagerListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onNoUpdateAvailable() {
    }


    @Override
    public void onUpdateAvailable(String s) {
        final AppBean bean = getAppBeanFromString(s);
        if ((!StringUtil.matchesNumber(bean.getVersionCode()) ||
                Integer.valueOf(bean.getVersionCode()) > BuildConfig.VERSION_CODE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setIcon(R.mipmap.desktop);
            builder.setTitle("发现新版本：" + bean.getVersionName());
            builder.setMessage(bean.getReleaseNote());
            builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            //获取权限后的操作。读取文件
                            startDownloadTask(activity, bean.getDownloadURL());
                        } else {
                            //请求权限
                            ActivityCompat.requestPermissions(activity,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    PERMISSION_DENIEG);
                        }
                    } else {
                        startDownloadTask(activity, bean.getDownloadURL());
                    }
                }
            });
            builder.setNegativeButton("取消", null);
            builder.setCancelable(false);
            builder.show();
        }


    }


//    @Override
//    public void onUpdateAvailable(String s) {
//        final AppBean bean = getAppBeanFromString(s);
//        if ((!StringUtil.matchesNumber(bean.getVersionCode()) ||
//                Integer.valueOf(bean.getVersionCode()) > BuildConfig.VERSION_CODE)) {
////            new AlertDialog.Builder(activity.getParent())
//            new AlertDialog.Builder(activity)
//                    .setIcon(R.mipmap.desktop)
//                    .setTitle("发现新版本：" + bean.getVersionName())
//                    .setMessage(bean.getReleaseNote())
//                    .setPositiveButton(
//                            "更新",
//                            new DialogInterface.OnClickListener() {
//
//                                @Override
//                                public void onClick(
//                                        DialogInterface dialog,
//                                        int which) {
//                                    // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
//                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                        if (checkPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                                            //获取权限后的操作。读取文件
//                                            startDownloadTask(activity, bean.getDownloadURL());
//                                        } else {
//                                            //请求权限
//                                            ActivityCompat.requestPermissions(activity,
//                                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                                                    PERMISSION_DENIEG);
//                                        }
//                                    } else {
//                                        startDownloadTask(activity, bean.getDownloadURL());
//                                    }
//
//                                }
//                            }).setNegativeButton(
//                    "取消",
//                    new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(
//                                DialogInterface dialog,
//                                int which) {
//                            dialog.dismiss();
//                        }
//                    }).show();
//
//        }
////        final AppBean bean = getAppBeanFromString(s);
////        if ((!StringUtil.matchesNumber(bean.getVersionCode()) ||
////                Integer.valueOf(bean.getVersionCode()) > BuildConfig.VERSION_CODE)) {
////            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
////            builder.setIcon(R.mipmap.desktop);
////            builder.setTitle("发现新版本：" + bean.getVersionName());
////            builder.setMessage(bean.getReleaseNote());
////            builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
////                @Override
////                public void onClick(DialogInterface dialog, int which) {
////                    startDownloadTask(activity, bean.getDownloadURL()); // 使用蒲公英下载链接, 有时候很慢
//////                    startDownloadTask(activity, Constant.UPDATE); // 使用七牛下载链接, CDN节点
////                }
////            });
////            builder.setNegativeButton("取消", null);
////            builder.setCancelable(false);
////            builder.show();
////        }
//    }


    public static void installApk(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    /**
     * 检测权限是否授权
     *
     * @return
     */
    private boolean checkPermission(Context context, String permission) {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, permission);
    }


}

