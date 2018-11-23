package com.gzdefine.huangcuangoa.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.gzdefine.huangcuangoa.App;
import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.util.PermissionUtil;
import com.gzdefine.huangcuangoa.util.SystemBarTintManager;
import com.pgyersdk.feedback.PgyFeedbackShakeManager;

import org.simple.eventbus.EventBus;

/**
 * Created by Administrator on 2016/11/17 0017.
 */

public abstract class BaseActivity extends AppCompatActivity {
    //首先声明权限授权
    public static final int PERMISSION_DENIEG = 1;//权限不足，权限被拒绝的时候
    public static final int PERMISSION_REQUEST_CODE = 0;//系统授权管理页面时的结果参数
    public static final String PACKAGE_URL_SCHEME = "package:";//权限方案
    public PermissionUtil checkPermission;//检测权限类的权限检测器
    private boolean isrequestCheck = true;//判断是否需要系统权限检测。防止和系统提示框重叠

    public abstract int initContentID();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.windowBackground);//通知栏所需颜色
        }
//        PgyCrashManager.register(this);
//        EventBus.getDefault().register(this);
        App.me().addActivity(this);

        initContentID();
        process(savedInstanceState);
    }

//    //显示对话框提示用户缺少权限
//    public void showMissingPermissionDialog() {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("你有权限未允许");//提示帮助
//        builder.setMessage("是否去设置权限");
//
//        //如果是拒绝授权，则退出应用
//        //退出
//        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                finish();
//            }
//        });
//        //打开设置，让用户选择打开权限
//        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                startAppSettings();//打开设置
//            }
//        });
//        builder.setCancelable(false);
//        builder.show();
//    }

    //获取全部权限
    public boolean hasAllPermissionGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }
    //打开系统应用设置(ACTION_APPLICATION_DETAILS_SETTINGS:系统设置权限)
    public void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
        startActivity(intent);
    }

    //请求权限去兼容版本
    public void requestPermissions(String... permission) {
        ActivityCompat.requestPermissions(this, permission, PERMISSION_REQUEST_CODE);

    }

    /**
     * 用于权限管理
     * 如果全部授权的话，则直接通过进入
     * 如果权限拒绝，缺失权限时，则使用dialog提示
     *
     * @param requestCode  请求代码
     * @param permissions  权限参数
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PERMISSION_REQUEST_CODE == requestCode && hasAllPermissionGranted(grantResults)) //判断请求码与请求结果是否一致
        {
            isrequestCheck = true;//需要检测权限，直接进入，否则提示对话框进行设置
            getAllGrantedPermission();
        } else {
            //提示对话框设置
            isrequestCheck = false;
//            showMissingPermissionDialog();//dialog
        }
    }

    /*
    * 当获取到所需权限后，进行相关业务操作
     */
    public void getAllGrantedPermission() {

    }

    protected void process(Bundle savedInstanceState) {
        if (getPermissions() != null) {
            checkPermission = new PermissionUtil(this);
            if (checkPermission.permissionSet(getPermissions())) {
                requestPermissions(getPermissions());     //去请求权限
            } else {
                getAllGrantedPermission();
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        //根据activity生命周期，onRestart()->onResume()
        //此处表示从系统设置页面返回后，检查用户是否将所需的权限打开
        if (!isrequestCheck) {
            if (getPermissions() != null) {
                if (checkPermission.permissionSet(getPermissions())) {

//                    showMissingPermissionDialog();//dialog
                } else {
                    //获取全部权限,走正常业务
                    getAllGrantedPermission();
                }
            }
        } else {
            isrequestCheck = true;
        }
    }

    public String[] getPermissions() {
        return null;
    }
    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        App.me().hideInput(getWindow());
        return super.onTouchEvent(event);
    }



    @Override
    protected void onPause() {
//        PgyFeedbackShakeManager.unregister();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        PgyFeedbackShakeManager.unregister();
        EventBus.getDefault().unregister(this);
        App.me().removeActivity(this);
        super.onDestroy();
    }

    public void setAppTitle(String title) {
        Log.d("reg","title:"+title);
      TextView apptitle= (TextView) findViewById(R.id.txt_title);
        apptitle.setText(title);
    }
}
