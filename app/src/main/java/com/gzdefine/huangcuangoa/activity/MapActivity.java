package com.gzdefine.huangcuangoa.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.gzdefine.huangcuangoa.App;
import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.entity.LoginResponse;
import com.gzdefine.huangcuangoa.entity.makerInfo;
import com.gzdefine.huangcuangoa.util.Constant;
import com.gzdefine.huangcuangoa.util.DateUtil;
import com.gzdefine.huangcuangoa.util.Ok;
import com.gzdefine.huangcuangoa.util.SPUtils;
import com.gzdefine.huangcuangoa.util.ToastUtil;

import org.json.JSONObject;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 此demo用来展示如何结合定位SDK实现室内定位，并使用MyLocationOverlay绘制定位位置
 */
public class MapActivity extends BaseActivity {
    public static final int REQUEST_CODE = 'm' + 'a' + 'p' + 'a' + 'c' + 't' + 'i' + 'v' + 'i' + 't' + 'y';
    public static final int UPDATE_ADDDRESS = 0;
    private static int REQUEST_THUMBNAIL = 1;// 请求缩略图信号标识
    //handler
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_ADDDRESS:
                    tv_address.setText("" + address);
            }
            return false;
        }
    });


    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();

    @Bind(R.id.tv_address)
    TextView tv_address;
    @Bind(R.id.txt_right)
    TextView upLoad;
    MapView mMapView;
    BaiduMap mBaiduMap;
    // UI相关
    //自定义图标
    private BitmapDescriptor mbitmap = BitmapDescriptorFactory.fromResource(R.mipmap.red_zhen);
    boolean isFirstLoc = true; // 是否首次定位
    private String address; //定位地址
    private LatLng ll;

    @Override
    public int initContentID() {
        return 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_map);
        LoginResponse login = App.me().login();
        if (null != login) {
            App.me().clearmsgcount("0");
            // 地图初始化
            mMapView = (MapView) findViewById(R.id.bmapView);
            mBaiduMap = mMapView.getMap();

            // 开启定位图层
            mBaiduMap.setMyLocationEnabled(true);
            // 开启室内图
            mBaiduMap.setIndoorEnable(false);
            // 定位初始化
            // 隐藏logo
            View child = mMapView.getChildAt(1);
            if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
                child.setVisibility(View.INVISIBLE);
            }
            //地图上比例尺
            mMapView.showScaleControl(false);
            // 隐藏缩放控件
            mMapView.showZoomControls(false);
            mLocClient = new LocationClient(this);
            mLocClient.registerLocationListener(myListener);
            initoption();
            mLocClient.start();

            ButterKnife.bind(this);
            setAppTitle("考勤");
            initView();
        }else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

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



    private void initView() {
        upLoad.setText("提交");
        upLoad.setVisibility(View.VISIBLE);
    }

    //提交签到
    @OnClick(R.id.txt_right)
    void upLoad() {
//        if (null != App.me().getTime("2") && !App.me().getTime("2").equals("")) {
        long dq1 = Long.parseLong(DateUtil.dataOne1(DateUtil.getTodayDateTime()));

//            Log.d("reg", "App.me().getTime(2):" + App.me().getTime("2"));
//            long kq = Long.parseLong(App.me().getTime("2"));
//            if (dq1 != kq) {
        long dq = Long.parseLong(DateUtil.dataOne(DateUtil.getTodayDateTime()));
        long d = Long.parseLong(DateUtil.dataOne(DateUtil.getDate1() + " 08:00"));
        long d1 = Long.parseLong(DateUtil.dataOne(DateUtil.getDate1() + " 08:30"));
        long y = Long.parseLong(DateUtil.dataOne(DateUtil.getDate1() + " 14:20"));
        long y1 = Long.parseLong(DateUtil.dataOne(DateUtil.getDate1() + " 14:40"));
//        if ((dq >= d && dq <= d1) || (dq >= y && dq <= y1)) {
//            isFirstLoc = true;
//            mLocClient.start();
//            try {
//                //上午8:00-8:30
//                //下午14:20-14:40
//                //115.100651,32.131013
////            double distance = GetLongDistance(115.100651, 32.131013,115.102115,32.131307);
//                double distance = GetLongDistance(115.100651, 32.131013, ll.longitude, ll.latitude);
//                int i = (int) distance;
//                Log.d("reg", "两点多少米：" + i);
//                if (i <= 145) {
//                    String insid = (String) SPUtils.get(this, "insid", "");//insid  是签到ID，SPUtils传来的
//                    String results = ((EditText) findViewById(R.id.results)).getText().toString();
//                    SignInControl signInControl = new SignInControl(this);
//                    signInControl.SignIn(insid, address, results);
//                } else {
//                    App.me().toast("不在考勤范围内考勤无效");
//                }
//            } catch (Exception e) {
//                ToastUtil.showShort(this, "提交失败，请重试");
//                e.printStackTrace();
//            }

//        } else {
            try {
                String insid = (String) SPUtils.get(this, "insid", "");//insid  是签到ID，SPUtils传来的
                String results = ((EditText) findViewById(R.id.results)).getText().toString();
                SignInControl signInControl = new SignInControl(this);
                signInControl.SignIn(insid, address, results);
            } catch (Exception e) {
                ToastUtil.showShort(this, "提交失败，请重试");
                e.printStackTrace();
            }
//        }
//            } else {
//                App.me().toast("考勤失败:已有考勤记录,请稍后再试");
//            }
//        } else {
//            long dq = Long.parseLong(DateUtil.dataOne(DateUtil.getTodayDateTime()));
//            long d = Long.parseLong(DateUtil.dataOne(DateUtil.getDate1() + " 08:00"));
//            long d1 = Long.parseLong(DateUtil.dataOne(DateUtil.getDate1() + " 08:30"));
//            long y = Long.parseLong(DateUtil.dataOne(DateUtil.getDate1() + " 14:20"));
//            long y1 = Long.parseLong(DateUtil.dataOne(DateUtil.getDate1() + " 14:40"));
//            if ((dq >= d && dq <= d1) || (dq >= y && dq <= y1)) {
//                isFirstLoc = true;
//                mLocClient.start();
//                try {
//                    //上午8:00-8:30
//                    //下午14:20-14:40
//                    //115.100651,32.131013
////            double distance = GetLongDistance(115.100651, 32.131013,115.102115,32.131307);
//                    double distance = GetLongDistance(115.100651, 32.131013, ll.longitude, ll.latitude);
//                    int i = (int) distance;
//                    Log.d("reg", "两点多少米：" + i);
//                    if (i <= 145) {
//                        String insid = (String) SPUtils.get(this, "insid", "");//insid  是签到ID，SPUtils传来的
//                        String results = ((EditText) findViewById(R.id.results)).getText().toString();
//                        SignInControl signInControl = new SignInControl(this);
//                        signInControl.SignIn(insid, address, results);
//                    } else {
//                        App.me().toast("不在考勤范围内考勤无效");
//                    }
//                } catch (Exception e) {
//                    ToastUtil.showShort(this, "提交失败，请重试");
//                    e.printStackTrace();
//                }
//
//            } else {
//                try {
//                    String insid = (String) SPUtils.get(this, "insid", "");//insid  是签到ID，SPUtils传来的
//                    String results = ((EditText) findViewById(R.id.results)).getText().toString();
//                    SignInControl signInControl = new SignInControl(this);
//                    signInControl.SignIn(insid, address, results);
//                } catch (Exception e) {
//                    ToastUtil.showShort(this, "提交失败，请重试");
//                    e.printStackTrace();
//                }
//            }
//        }


    }

    void initoption() {
        LocationClientOption option = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps
//        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
//        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
//        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
//        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集
//        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocClient.setLocOption(option);
    }


    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {


        @Override
        public void onReceiveLocation(final BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }

            MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                ll = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
//                makerInfo marker = new makerInfo(location.getLongitude(), location.getLatitude(), location.getAddrStr());
//                addOverlay(marker);
                //发消息更新地址
                Message msg = new Message();
                msg.what = UPDATE_ADDDRESS;
                address = location.getAddrStr();
                handler.sendMessage(msg);

            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }

    /*    @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }*/
    }


    //为地图添加标签
    public void addOverlay(makerInfo Info) {
        OverlayOptions overlayoptions = null;
        Marker marker = null;
        overlayoptions = new MarkerOptions()//
                .position(Info.getLatlng())// 设置marker的位置
                .icon(mbitmap)// 设置marker的图标
                .zIndex(9);// 設置marker的所在層級

        marker = (Marker) mBaiduMap.addOverlay(overlayoptions);

        Bundle bundle = new Bundle();
        bundle.putSerializable("marker", Info);
        marker.setExtraInfo(bundle);
    }

    @OnClick(R.id.img_back)
    void back() {
        finish();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    class SignInControl extends Ok {

        public SignInControl(Context context) {
            super(context);
        }

        public void SignIn(String insid, String address, String results) {
            super.post(Constant.DOMAIN + "mobile/attendance/spotCheck.do", "userId", App.me().login().getUserId(), "insId", insid, "checkAddress", address, "results", results);
        }

        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String re = response.body().string();
            Log.d("reg", "考勤:" + re);
            App.me().checklogin(MapActivity.this, re);
            // JSON串转用户对象列表
            try {
                JSONObject jsonObject = new JSONObject(re);
                Boolean success = jsonObject.getBoolean("success");
                String message = jsonObject.getString("message");
                Looper.prepare();
                if (success) {
//                  App.me().setTime(DateUtil.dataOne1(DateUtil.getTodayDateTime()), "2");
                    SPUtils.put(MapActivity.this, "insid", "");
                    Toast.makeText(MapActivity.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MapActivity.this, message, Toast.LENGTH_SHORT).show();
                }
                Looper.loop();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
