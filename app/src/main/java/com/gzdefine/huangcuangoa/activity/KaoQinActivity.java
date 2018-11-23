package com.gzdefine.huangcuangoa.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.bumptech.glide.Glide;
import com.gzdefine.huangcuangoa.App;
import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.entity.LoginResponse;
import com.gzdefine.huangcuangoa.util.Constant;
import com.gzdefine.huangcuangoa.util.DateUtil;
import com.gzdefine.huangcuangoa.util.DeviceUtils;
import com.gzdefine.huangcuangoa.util.Ok;
import com.gzdefine.huangcuangoa.util.ToastUtil;
import com.gzdefine.huangcuangoa.view.CircleImageView;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;


public class KaoQinActivity extends BaseActivity {
    @Bind(R.id.user_head)
    CircleImageView user_head;
    @Bind(R.id.name)
    TextView name;

    @Bind(R.id.morning)
    TextView morning;

    @Bind(R.id.afternoon)
    TextView afternoon;

    @Bind(R.id.data)
    TextView data;
    @Bind(R.id.tv_time1)
    TextView tv_time1;
    @Bind(R.id.tv_time3)
    TextView tv_time3;
    @Bind(R.id.tv_time2)
    TextView tv_time2;
    @Bind(R.id.Circle11)
    LinearLayout Circle11;
    @Bind(R.id.Circle12)
    LinearLayout Circle12;
    @Bind(R.id.Circle2)
    CircleImageView Circle2;
    @Bind(R.id.Circle22)
    CircleImageView Circle22;
    @Bind(R.id.re_signIn)
    RelativeLayout re_signIn;

    @Bind(R.id.re_signIn1)
    RelativeLayout re_signIn1;
    @Bind(R.id.re_signOut)
    RelativeLayout re_signOut;
    public static final int UPDATE_ADDDRESS = 0;
    public static final int UPDATE_TIME = 1;
    public static final int SignStaus = 2;
    public static final int SignIn = 3;
    public static final int SignOut = 4;
    public static final int SignIn1 = 5;
    Boolean threadRunning = true;
    private boolean falg;
    private boolean falg1;
    private TimerTask mTimerTask;
    private Timer mTimer;

    //handler
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                String re;
                JSONObject jsonObject;
                switch (msg.what) {
                    case UPDATE_ADDDRESS:
                        tv_address.setText(msg.obj.toString());
                        break;
                    case UPDATE_TIME:
                        if (falg == false) {
                            tv_time1.setText(DateUtil.getDate2());
                            tv_time2.setText(DateUtil.getDate2());
                        }
                        if (falg1 == false) {
                            tv_time3.setText(DateUtil.getDate2());
                        }
                        break;
                    case SignStaus:
                        re = (String) msg.obj;
                        jsonObject = new JSONObject(re);
                        JSONObject jsonArray = jsonObject.getJSONObject("data");
                        Log.d("reg","签到："+jsonArray);
                        String success = jsonObject.getString("success");
                        if (success.equals("true")) {
                            Boolean upSign = jsonArray.getBoolean("upSign");//签到
                            Boolean downSign = jsonArray.getBoolean("downSign");//签到
                            Boolean isPlan = jsonArray.getBoolean("isPlan");//是否排班内
                            Boolean isShowOut = jsonArray.getBoolean("isShowOut");//签退
                            String upTime = jsonArray.getString("upTime");//早班签到时间
                            String downTime = jsonArray.getString("downTime");//早班签到时间

                            String morningStart = jsonArray.getString("morningStart");
                            String morningEnd = jsonArray.getString("morningEnd");
                            String afternoonStart = jsonArray.getString("afternoonStart");
                            String afternoonEnd = jsonArray.getString("afternoonEnd");

                            Log.d("reg", "upTime:" + upTime);
                            Log.d("reg", "downTime:" + downTime);

                            if (!TextUtils.isEmpty(morningStart)&&!TextUtils.isEmpty(morningEnd)&&!TextUtils.isEmpty(afternoonStart)&&!TextUtils.isEmpty(afternoonEnd)){
                                morning.setVisibility(View.VISIBLE);
                                morning.setText("上午签到时间 "+morningStart+" - "+morningEnd);
                            }else {
                                morning.setVisibility(View.GONE);
                            }


                            if (null!=jsonArray.getString("afternoonStart")&&!jsonArray.getString("afternoonStart").equals("")&&null!=jsonArray.getString("afternoonEnd")&&!jsonArray.getString("afternoonEnd").equals("")){
                                afternoon.setVisibility(View.VISIBLE);
                                afternoon.setText("下午签到时间 "+afternoonStart+" - "+afternoonEnd);
                            }else {
                                afternoon.setVisibility(View.GONE);
                            }

                            if (null != upTime && !upTime.equals("null") && !upTime.equals("")&& !upTime.equals(" ")) {
                                String[] strarray1 = upTime.split("[ ]");
                                String[] str1 = strarray1[1].split("[.]");
                                if (!str1[0].endsWith("00:00:00")) {
                                    time = str1[0];
                                }
                                Log.d("reg", "str1:" + str1[0]);
                            }

                            if (null != downTime && !downTime.equals("null") && !downTime.equals("")&& !downTime.equals(" ")) {
                                String[] strarray = downTime.split("[ ]");
                                String[] str = strarray[1].split("[.]");
                                if (str.length>0&&!str[0].endsWith("00:00:00")) {
                                    time1 = str[0];
                                }
                            }
                            isQianDao(upSign, downSign);//早下班判断是否签到
                            isQianTui(isShowOut);//判断是否签退
                        } else {
                            String data = jsonObject.getString("data");
                            String message = jsonObject.getString("message");
                            ToastUtil.showShort(KaoQinActivity.this, message + data);
                        }
                        break;
                    case SignIn:
                        re = (String) msg.obj;
                        jsonObject = new JSONObject(re);
                        String success1 = jsonObject.getString("success");
                        if (success1.equals("true")) {
//                            App.me().setTime(DateUtil.dataOne1(DateUtil.getTodayDateTime()), "1");
                            JSONObject object = jsonObject.getJSONObject("data");
                            String date = object.getString("date");
                            Log.d("reg", "date:" + date);
                            falg = true;
                            if (date != null) {
                                tv_time1.setText(date);
                            }
                            Circle11.setBackgroundDrawable(getResources().getDrawable(R.drawable.list_frame_background1));
                            re_signIn.setEnabled(false);
                            ToastUtil.showShort(KaoQinActivity.this, jsonObject.getString("message"));
                        } else {
                            String data = jsonObject.getString("data");
                            String message = jsonObject.getString("message");
                            ToastUtil.showShort(KaoQinActivity.this, message + data);
                        }
                        break;
                    case SignOut:
//                        re = (String) msg.obj;
//                        jsonObject = new JSONObject(re);
//                        ToastUtil.showShort(KaoQinActivity.this, jsonObject.getString("message"));
                        re = (String) msg.obj;
                        jsonObject = new JSONObject(re);
                        String success2 = jsonObject.getString("success");
                        if (success2.equals("true")) {
//                            App.me().setTime(DateUtil.dataOne1(DateUtil.getTodayDateTime()), "3");
                            JSONObject object = jsonObject.getJSONObject("data");
                            String date = object.getString("date");
                            Log.d("reg", "下午签到:" + date);
                            falg1 = true;
                            if (date != null) {
                                tv_time3.setText(date);
                            }
                            Circle12.setBackgroundDrawable(getResources().getDrawable(R.drawable.list_frame_background1));
                            re_signIn1.setEnabled(false);
                            ToastUtil.showShort(KaoQinActivity.this, jsonObject.getString("message"));
                        } else {
                            String data = jsonObject.getString("data");
                            String message = jsonObject.getString("message");
                            ToastUtil.showShort(KaoQinActivity.this, message + data);
                        }
                        break;
                    case SignIn1:
//                        re = (String) msg.obj;
//                        jsonObject = new JSONObject(re);
//                        String success2 = jsonObject.getString("success");
//                        if (success2.equals("true")) {
//                            App.me().setTime(DateUtil.dataOne1(DateUtil.getTodayDateTime()), "3");
//                            JSONObject object = jsonObject.getJSONObject("data");
//                            String date = object.getString("date");
//                            Log.d("reg", "下午签到:" + date);
//                            falg1 = true;
//                            if (date != null) {
//                                tv_time3.setText(date);
//                            }
//                            Circle12.setBackgroundDrawable(getResources().getDrawable(R.drawable.list_frame_background1));
//                            re_signIn1.setEnabled(false);
//                            ToastUtil.showShort(KaoQinActivity.this, jsonObject.getString("message"));
//                        } else {
//                            String data = jsonObject.getString("data");
//                            String message = jsonObject.getString("message");
//                            ToastUtil.showShort(KaoQinActivity.this, message + data);
//                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    });
    private String time;
    private String time1;
    private LatLng ll;


    private void isQianDao(Boolean isShowIn, Boolean downSign) {
        if (!isShowIn) {
            falg = true;
            if (time != null) {
                tv_time1.setText(time);
            }else {
                tv_time1.setText("非签到时段");
            }
            Circle11.setBackgroundDrawable(getResources().getDrawable(R.drawable.list_frame_background1));
            re_signIn.setEnabled(false);
        } else {
            falg = false;
        }
        if (!downSign) {
            falg1 = true;
            if (time1 != null) {
                tv_time3.setText(time1);
            }else {
                tv_time3.setText("非签到时段");
            }
            Circle12.setBackgroundDrawable(getResources().getDrawable(R.drawable.list_frame_background1));
            re_signIn1.setEnabled(false);
        } else {
            falg1 = false;
        }


    }


    private void isQianTui(Boolean isShowOut) {
        if (!isShowOut) {
            Circle2.setImageResource(R.color.none1);
            Circle22.setImageResource(R.color.none2);
            re_signOut.setEnabled(false);
        }
    }


    SignInControl signInControl;
    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    boolean isFirstLoc = true; // 是否首次定位
    @Bind(R.id.tv_address)
    TextView tv_address;

    @Override
    public int initContentID() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kao_qin);
        mTimer = new Timer();
        ButterKnife.bind(this);
        setAppTitle("签到");


        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        initoption();
        mLocClient.start();
        LoginResponse loginResponse = App.me().login();
        if (null != loginResponse) {
            initDate();
        } else {
            startActivityForResult(new Intent(KaoQinActivity.this, LoginActivity.class), LoginActivity.REQUEST_CODE);
            finish();
        }
        lm = (LocationManager) KaoQinActivity.this.getSystemService(KaoQinActivity.this.LOCATION_SERVICE);
        boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (ok) {//开了定位服务
            if (ContextCompat.checkSelfPermission(KaoQinActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // 没有权限，申请权限。
//                        Toast.makeText(getActivity(), "没有权限", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(KaoQinActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_CODE);
            } else {
                // 有权限了，去放肆吧。
//                        Toast.makeText(getActivity(), "有权限", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(KaoQinActivity.this, "系统检测到未开启GPS定位服务", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 1315);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("reg", "onResume()");
        mTimer = new Timer();
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        initoption();
        mLocClient.start();
    }

    @Override
    protected void onPause() {
        Log.d("reg", "onPause()");
        super.onPause();
    }

    @Override
    protected void onStart() {
        mTimer = new Timer();
        Log.d("reg", "onStart()");
        isFirstLoc = true;
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        initoption();
        mLocClient.start();
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        Log.d("reg", "onDestroy()");
        threadRunning = false;
        mLocClient.stop();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer=null;
        }
        // 退出时销毁定位
        // 关闭定位图层
        super.onDestroy();
    }

    private void initDate() {
        signInControl = new SignInControl(this);
        signInControl.getStaus();
        LoginResponse login = App.me().login();
        if (login != null) {
            String imageUrl = Constant.DOMAIN + "sys/core/file/imageView.do?thumb=true&fileId=" + login.getPhoto();
            Log.d("max", imageUrl);
            Glide.with(this).load(imageUrl).placeholder(R.mipmap.default_photo).into(user_head);
        }
        name.setText(login.fullname);
        data.setText(DateUtil.getDate1() + "　" + getWeek());
        Log.d("reg", "getWeek:" + getWeek());

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (threadRunning) {
                    try {
                        Thread.sleep(1000);
                        Message message = new Message();
                        message.what = UPDATE_TIME;
                        handler.sendMessage(message);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    private static String getWeek() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String week = null;
//获取指定年份月份中指定某天是星期几
        calendar.set(Calendar.DAY_OF_MONTH, day);  //指定日
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        // 获取指定日期转换成星期几
        switch (dayOfWeek) {
            case 1:
                week = "星期日";
                break;
            case 2:
                week = "星期一";
                break;
            case 3:
                week = "星期二";
                break;
            case 4:
                week = "星期三";
                break;
            case 5:
                week = "星期四";
                break;
            case 6:
                week = "星期五";
                break;
            case 7:
                week = "星期六";
                break;

        }
        return week;
    }

    void initoption() {
        LocationClientOption option = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系
        int span = 3000;
        option.setScanSpan(span);
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setIsNeedLocationDescribe(true);
        mLocClient.setLocOption(option);
    }

    @OnClick(R.id.img_back)
    void back() {
        finish();
    }


    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {


        @Override
        public void onReceiveLocation(final BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null) {
                return;
            }

            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    ll = new LatLng(location.getLatitude(), location.getLongitude());
                    Log.d("reg", "经纬度：" + ll);
                    Log.d("reg", "location.getAddrStr()：" + location.getAddrStr());
                    //发消息更新地址
                    Message msg = new Message();
                    msg.what = UPDATE_ADDDRESS;
                    msg.obj = location.getAddrStr();
                    handler.sendMessage(msg);
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }
            };
            if(mTimer!=null){
                mTimer.schedule(mTimerTask, 5000);
            }

//            if (isFirstLoc) {
//                isFirstLoc = false;


//            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限被用户同意。

                } else {
                    // 权限被用户拒绝了。
                    Toast.makeText(KaoQinActivity.this, "定位权限被禁止，签到功能无法使用！",Toast.LENGTH_LONG).show();
                    finish();
                }

            }
        }
    }

    private LocationManager lm;
    private static final int LOCATION_CODE = 1;
    @OnClick(R.id.re_signIn)
    public void signIn() {

        if (ll != null) {
            if(TextUtils.isEmpty(""+ll.longitude)||TextUtils.isEmpty(""+ll.latitude)||"4.9E-324".equals(ll.longitude)||"4.9E-324".equals(ll.latitude)){
                Toast.makeText(this,"位置信息有误，请检查定位或者网络",Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("reg", "ll.longitude:" + ll.longitude);
            Log.d("reg", "ll.latitude:" + ll.latitude);
            Log.d("reg", "唯一标识码：" + DeviceUtils.getUniqueId(this));
//        if (null!=App.me().getTime("1")&&!App.me().getTime("1").equals("")) {
            long dq1 = Long.parseLong(DateUtil.dataOne1(DateUtil.getTodayDateTime()));
//        Log.d("reg", "App.me().getTime(1):" + App.me().getTime("1"));
//            long kq = Long.parseLong(App.me().getTime("1"));
//            if (dq1 != kq) {
//            long dq = Long.parseLong(DateUtil.dataOne(DateUtil.getTodayDateTime()));
//            long d = Long.parseLong(DateUtil.dataOne(DateUtil.getDate1() + "07:50"));
//            long d1 = Long.parseLong(DateUtil.dataOne(DateUtil.getDate1() + "08:30"));
//            long y = Long.parseLong(DateUtil.dataOne(DateUtil.getDate1() + "14:20"));
//            long y1 = Long.parseLong(DateUtil.dataOne(DateUtil.getDate1() + "14:40"));

            if (true) {//取消了判断条件，交给后台去做
//                if ((dq >= d && dq <= d1)) {
                isFirstLoc = true;
                mLocClient.start();
                //上午8:00-8:30
                //下午14:20-14:40
                //115.100651,32.131013
//            double distance = GetLongDistance(115.100651, 32.131013,115.102115,32.131307);

                Dialog dialog = new AlertDialog.Builder(this).setIcon(
                        android.R.drawable.btn_star).setTitle("签到").setMessage(
                        "每天只有一次签到机会，你确定签到吗？").setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                signInControl.SignIn1("up");

                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
                dialog.show();
//                } else {
//                    Dialog dialog = new AlertDialog.Builder(this).setIcon(
//                            android.R.drawable.btn_star).setTitle("签到").setMessage(
//                            "每天只有一次签到机会，你确定签到吗？").setPositiveButton("确定",
//                            new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    signInControl.SignIn(DeviceUtils.getUniqueId(KaoQinActivity.this));
//
//                                }
//                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                        }
//                    }).create();
//                    dialog.show();
//                }
//                Log.d("reg", "dq:" + dq);
//                Log.d("reg", "d:" + d);
//                Log.d("reg", "d1:" + d1);
//                Log.d("reg", "y:" + y);
//                Log.d("reg", "y1:" + y1);
            } else {
                App.me().toast("不在签到范围内签到无效");
            }
        } else {
            App.me().toast("正在获取签到地址请稍后再试");
        }
    }

//    else {
//                App.me().toast("签到失败:已有签到记录,请稍后再试");
//            }
//        } else {
//            long dq = Long.parseLong(DateUtil.dataOne(DateUtil.getTodayDateTime()));
//            long d = Long.parseLong(DateUtil.dataOne(DateUtil.getDate1() + " 08:00"));
//            long d1 = Long.parseLong(DateUtil.dataOne(DateUtil.getDate1() + " 08:30"));
//            long y = Long.parseLong(DateUtil.dataOne(DateUtil.getDate1() + " 14:20"));
//            long y1 = Long.parseLong(DateUtil.dataOne(DateUtil.getDate1() + " 14:40"));
//
//            if ((dq >= d && dq <= d1)) {
//                isFirstLoc = true;
//                mLocClient.start();
//                //上午8:00-8:30
//                //下午14:20-14:40
//                //115.100651,32.131013
////            double distance = GetLongDistance(115.100651, 32.131013,115.102115,32.131307);
//                double distance = GetLongDistance(115.100651, 32.131013, ll.longitude, ll.latitude);
//                int i = (int) distance;
//                Log.d("reg", "两点多少米：" + i);
//                if (i <= 145) {
//                    Dialog dialog = new AlertDialog.Builder(this).setIcon(
//                            android.R.drawable.btn_star).setTitle("签到").setMessage(
//                            "每天只有一次签到机会，你确定签到吗？").setPositiveButton("确定",
//                            new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    signInControl.SignIn(DeviceUtils.getUniqueId(KaoQinActivity.this));
//
//                                }
//                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                        }
//                    }).create();
//                    dialog.show();
//                } else {
//                    App.me().toast("不在签到范围内签到无效");
//                }
//            } else {
//                Dialog dialog = new AlertDialog.Builder(this).setIcon(
//                        android.R.drawable.btn_star).setTitle("签到").setMessage(
//                        "每天只有一次签到机会，你确定签到吗？").setPositiveButton("确定",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                signInControl.SignIn(DeviceUtils.getUniqueId(KaoQinActivity.this));
//
//                            }
//                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                }).create();
//                dialog.show();
//            }

//            Log.d("reg", "dq:" + dq);
//            Log.d("reg", "d:" + d);
//            Log.d("reg", "d1:" + d1);
//            Log.d("reg", "y:" + y);
//            Log.d("reg", "y1:" + y1);
//        }
//    }

    @OnClick(R.id.re_signIn1)
    public void signIn1() {

        if (ll != null) {
            double distance = GetLongDistance(115.100651, 32.131013, ll.longitude, ll.latitude);
//            double distance = GetLongDistance(115.100651, 32.131013, 115.100651, 32.131013);
            int i = (int) distance;
            boolean isOK=true;

            // 这里解除了范围限制，按客户需求。
            if (isOK) {
//                if ((dq >= y && dq <= y1)) {
                isFirstLoc = true;
                mLocClient.start();
                //上午8:00-8:30
                //下午14:20-14:40
                //115.100651,32.131013
//            double distance = GetLongDistance(115.100651, 32.131013,115.102115,32.131307);

                Log.d("reg", "两点多少米：" + i);

                Dialog dialog = new AlertDialog.Builder(this).setIcon(
                        android.R.drawable.btn_star).setTitle("签到").setMessage(
                        "确定签到吗？").setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                signInControl.SignIn1("down");

                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
                dialog.show();
//                } else {
//                    Dialog dialog = new AlertDialog.Builder(this).setIcon(
//                            android.R.drawable.btn_star).setTitle("签到").setMessage(
//                            "每天只有一次签到机会，你确定签到吗？").setPositiveButton("确定",
//                            new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    signInControl.SignOut(DeviceUtils.getUniqueId(KaoQinActivity.this));
//
//                                }
//                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                        }
//                    }).create();
//                    dialog.show();
//                }
            } else {
                App.me().toast("不在签到范围内签到无效");
            }


//            Log.d("reg", "dq:" + dq);
//            Log.d("reg", "d:" + d);
//            Log.d("reg", "d1:" + d1);
//            Log.d("reg", "y:" + y);
//            Log.d("reg", "y1:" + y1);
        }else {
            App.me().toast("正在获取签到地址请稍后再试");
        }
    }


//    else {
//                App.me().toast("签到失败:已有签到记录,请稍后再试");
//            }
//        }
//
//    else {
//            long dq = Long.parseLong(DateUtil.dataOne(DateUtil.getTodayDateTime()));
//            long d = Long.parseLong(DateUtil.dataOne(DateUtil.getDate1() + " 08:00"));
//            long d1 = Long.parseLong(DateUtil.dataOne(DateUtil.getDate1() + " 08:30"));
//            long y = Long.parseLong(DateUtil.dataOne(DateUtil.getDate1() + " 14:20"));
//            long y1 = Long.parseLong(DateUtil.dataOne(DateUtil.getDate1() + " 14:40"));
//
//            if ((dq >= d && dq <= d1) || (dq >= y && dq <= y1)) {
//                isFirstLoc = true;
//                mLocClient.start();
//                //上午8:00-8:30
//                //下午14:20-14:40
//                //115.100651,32.131013
////            double distance = GetLongDistance(115.100651, 32.131013,115.102115,32.131307);
//                double distance = GetLongDistance(115.100651, 32.131013, ll.longitude, ll.latitude);
//                int i = (int) distance;
//                Log.d("reg", "两点多少米：" + i);
//                if (i <= 145) {
//                    Dialog dialog = new AlertDialog.Builder(this).setIcon(
//                            android.R.drawable.btn_star).setTitle("签到").setMessage(
//                            "每天只有一次签到机会，你确定签到吗？").setPositiveButton("确定",
//                            new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    signInControl.SignOut(DeviceUtils.getUniqueId(KaoQinActivity.this));
//
//                                }
//                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                        }
//                    }).create();
//                    dialog.show();
//                } else {
//                    App.me().toast("不在签到范围内签到无效");
//                }
//            } else {
//                Dialog dialog = new AlertDialog.Builder(this).setIcon(
//                        android.R.drawable.btn_star).setTitle("签到").setMessage(
//                        "每天只有一次签到机会，你确定签到吗？").setPositiveButton("确定",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                signInControl.SignOut(DeviceUtils.getUniqueId(KaoQinActivity.this));
//
//                            }
//                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                }).create();
//                dialog.show();
//            }
//
//            Log.d("reg", "dq:" + dq);
//            Log.d("reg", "d:" + d);
//            Log.d("reg", "d1:" + d1);
//            Log.d("reg", "y:" + y);
//            Log.d("reg", "y1:" + y1);
//        }
//    }


    @OnClick(R.id.re_signOut)
    public void signOut() {
        Dialog dialog = new AlertDialog.Builder(this).setIcon(
                android.R.drawable.btn_star).setTitle("考勤签退").setMessage(
                "每天只有一次签退机会，你确定签到吗？").setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        signInControl.SignIn1("down");
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).create();
        dialog.show();

    }

    static double DEF_PI = 3.14159265359; // PI
    static double DEF_2PI = 6.28318530712; // 2*PI
    static double DEF_PI180 = 0.01745329252; // PI/180.0
    static double DEF_R = 6370693.5; // radius of earth

    /**
     * 第一种方式，按勾股定律求结果
     *
     * @param lon1
     * @param lat1
     * @param lon2
     * @param lat2
     * @return
     * @author kazeik.chen QQ:77132995  2014-4-1下午4:30:09
     * TODO kazeik@163.com
     */
    public double GetShortDistance(double lon1, double lat1, double lon2,
                                   double lat2) {
        double ew1, ns1, ew2, ns2;
        double dx, dy, dew;
        double distance;
        // 角度转换为弧度
        ew1 = lon1 * DEF_PI180;
        ns1 = lat1 * DEF_PI180;
        ew2 = lon2 * DEF_PI180;
        ns2 = lat2 * DEF_PI180;
        // 经度差
        dew = ew1 - ew2;
        // 若跨东经和西经180 度，进行调整
        if (dew > DEF_PI)
            dew = DEF_2PI - dew;
        else if (dew < -DEF_PI)
            dew = DEF_2PI + dew;
        dx = DEF_R * Math.cos(ns1) * dew; // 东西方向长度(在纬度圈上的投影长度)
        dy = DEF_R * (ns1 - ns2); // 南北方向长度(在经度圈上的投影长度)
        // 勾股定理求斜边长
        distance = Math.sqrt(dx * dx + dy * dy);
        return distance;
    }

    /**
     * 第二种方式，按大圆劣弧求距离
     *
     * @param lon1
     * @param lat1
     * @param lon2
     * @param lat2
     * @return
     * @author kazeik.chen QQ:77132995  2014-4-1下午4:30:30
     * TODO kazeik@163.com
     */
    public double GetLongDistance(double lon1, double lat1, double lon2,
                                  double lat2) {
        double ew1, ns1, ew2, ns2;
        double distance;
        // 角度转换为弧度
        ew1 = lon1 * DEF_PI180;
        ns1 = lat1 * DEF_PI180;
        ew2 = lon2 * DEF_PI180;
        ns2 = lat2 * DEF_PI180;
        // 求大圆劣弧与球心所夹的角(弧度)
        distance = Math.sin(ns1) * Math.sin(ns2) + Math.cos(ns1)
                * Math.cos(ns2) * Math.cos(ew1 - ew2);
        // 调整到[-1..1]范围内，避免溢出
        if (distance > 1.0)
            distance = 1.0;
        else if (distance < -1.0)
            distance = -1.0;
        // 求大圆劣弧长度
        distance = DEF_R * Math.acos(distance);
        return distance;
    }

    class SignInControl extends Ok {
        int what = 0;

        public SignInControl(Context context) {
            super(context);
        }

        public void getStaus() {
            what = SignStaus;
            super.post(Constant.DOMAIN + "hr/core/hrDutyRegister/isShowButton.do");
        }

        public void SignIn(String sole) {
            what = SignIn;
            super.post(Constant.DOMAIN + "hr/core/hrDutyRegister/signIn.do", "sign", "up", "sole_Up", sole);
        }

        public void SignIn1(String upanddown) {
            if("up".equals(upanddown)){
                what = SignIn;
            }else{
                what = SignOut;
            }
            if(TextUtils.isEmpty(tv_address.getText().toString())){
                Toast.makeText(KaoQinActivity.this,"请获取到正确地址后再提交",Toast.LENGTH_SHORT).show();
                return;
            }
            super.post(Constant.DOMAIN + "hr/core/hrDutyRegister/signInBeta.do", "sign", upanddown,"longitude",ll.longitude,"latitude",ll.latitude,"sole_Down",DeviceUtils.getUniqueId(KaoQinActivity.this),"address",tv_address.getText().toString());
        }

        public void SignOut(String sole) {
            what = SignOut;
//            super.post(Constant.DOMAIN + "hr/core/hrDutyRegister/signOut.do"); //签退
            super.post(Constant.DOMAIN + "hr/core/hrDutyRegister/signOutzw.do", "sign", "down", "sole_Down", sole);
        }

        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String re = response.body().string();
            Log.d("reg", "result:" + re);
            App.me().checklogin(KaoQinActivity.this, re);
            // JSON串转用户对象列表
            try {
                //发消息更新UI
                Message msg = new Message();
                msg.what = what;
                msg.obj = re;
                handler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == LoginActivity.REQUEST_CODE && resultCode == LoginActivity.RESULT_OK) {
//            Log.d("reg", "1");
//            startActivity(new Intent(this, KaoQinActivity.class));
//        }
//    }

}
