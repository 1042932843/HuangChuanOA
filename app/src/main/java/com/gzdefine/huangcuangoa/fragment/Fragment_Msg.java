package com.gzdefine.huangcuangoa.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.gzdefine.huangcuangoa.App;
import com.gzdefine.huangcuangoa.Model.KeyValuePair;
import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.activity.CalendarActivity;
import com.gzdefine.huangcuangoa.activity.CalendarInfoActivity;
import com.gzdefine.huangcuangoa.activity.MapActivity;
import com.gzdefine.huangcuangoa.activity.MeetingActivity;
import com.gzdefine.huangcuangoa.activity.MeetingInfoActivity;
import com.gzdefine.huangcuangoa.activity.WebViewNoTitleActivity;
import com.gzdefine.huangcuangoa.entity.LoginResponse;
import com.gzdefine.huangcuangoa.util.DateUtil;
import com.gzdefine.huangcuangoa.util.ListDataSave;
import com.gzdefine.huangcuangoa.util.SPUtils;
import com.gzdefine.huangcuangoa.view.DotsTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.gzdefine.huangcuangoa.util.Constant.mail;
import static com.gzdefine.huangcuangoa.util.Constant.myToDo;

//消息
public class Fragment_Msg extends Fragment {
    RequestQueue queue = null;
    private TextView hn;
    private TextView wanshang;
    private ImageView wanshang_img;
    private TextView baitian;
    private ImageView baitian_img;
    private TextView wendu;
    private TextView wanshang_text;
    private TextView baitian_text;
    private LinearLayout kaoqin_layout, linearLayout;
    DotsTextView dotsTextView;
    //    int pp ;
    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    ListDataSave dataSave;
    private ArrayList<KeyValuePair> listBean;
    boolean isFirstLoc = true; // 是否首次定位
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                String message = "";
                switch (msg.what) {
                    case 0://考勤
                        message = (String) msg.obj;
                        updateKaoqinList(message);
                        break;
                    case 1://会议
                        message = (String) msg.obj;
                        updateConferceList(message);
                        break;
                    case 2://日程
                        message = (String) msg.obj;
                        updatePlanList(message);
                        break;
                    case 3://流程
                        message = (String) msg.obj;
                        updateProcessList(message);
                        break;
                    case 4://邮件
                        message = (String) msg.obj;
                        updateMailList(message);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    });

    public void weatherClick(double lon, double lat, final String Add) {
//        String url = "https://free-api.heweather.com/v5/now?city=CN101010100&key=583f28135e334d89be6976bf7c224981";
//        String url = "https://free-api.heweather.com/v5/hourly?city=huangchuan&key=583f28135e334d89be6976bf7c224981";
//        String url = "https://free-api.heweather.com/v5/now?city="+city+"&key=583f28135e334d89be6976bf7c224981";
        String url = "https://api.seniverse.com/v3/weather/daily.json?key=vbklsgs45m3lqil1&location=" + lat + ":" + lon + "&language=zh-Hans&unit=c&start=-1&days=5";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("reg", "jsonObject:" + jsonObject);
                try {
                    JSONArray arr = jsonObject.getJSONArray("results");
                    if (arr.length() > 0) {
                        JSONObject object = new JSONObject(arr.getString(0));
                        Log.d("reg", "object:" + object);
                        JSONArray jsonArray = object.getJSONArray("daily");
                        JSONObject jon = object.getJSONObject("location");
                        String path = jon.getString("path");
                        String[] strarray = path.split("[,]");
                        Log.d("reg", "shi:" + strarray[1]);
                        Log.d("reg", "shen:" + strarray[2]);

                        if (Add.indexOf("潢川") != -1 || Add.indexOf("信阳") != -1) {
                            hn.setText("河南●潢川");
                            App.me().setdizhi("河南●潢川");
                        } else {
                            if (strarray.length > 0) {
                                hn.setText(strarray[2] + "•" + strarray[1]);
                                App.me().setdizhi(strarray[2] + "●" + strarray[1]);
                            }
                        }

                        if (jsonArray.length() > 0) {
                            JSONObject ss = jsonArray.getJSONObject(0);
                            String high = ss.getString("high"); //最高
                            String low = ss.getString("low"); //最低
                            String text_day = ss.getString("text_day"); //白天天气现象文字
                            String text_night = ss.getString("text_night"); ////晚间天气现象文字
                            String code_day = ss.getString("code_day"); //白天天气现象代码
                            String code_night = ss.getString("code_night"); //晚间天气现象代码
                            Log.d("reg", "text_day:" + text_day);
                            Log.d("reg", "text_night:" + text_night);
                            Log.d("reg", "白天天气现象代码:" + code_day);
                            Log.d("reg", "晚间天气现象代码:" + code_night);
                            Log.d("reg", "high:" + high);
                            Log.d("reg", "low:" + low);

                            App.me().setwendu(low + "℃" + "~" + high + "℃");
                            App.me().setbaitian_img("a" + code_day);
                            App.me().setwanshang_img("a" + code_night);
                            App.me().setbaitian_wenzi(text_day);
                            App.me().setwanshang_wenzi(text_night);
                            baitian.setVisibility(View.VISIBLE);
                            wanshang.setVisibility(View.VISIBLE);
                            wendu.setText(low + "℃" + "~" + high + "℃");
                            baitian_text.setText(text_day);
                            wanshang_text.setText(text_night);

                            if (isAdded()) {
                                baitian_img.setImageDrawable(getResources().getDrawable(getResource("a" + code_day)));
                                wanshang_img.setImageDrawable(getResources().getDrawable(getResource("a" + code_night)));
                            }
                        } else {
                            baitian.setVisibility(View.GONE);
                            wanshang.setVisibility(View.GONE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println(volleyError);
            }
        });
        queue.add(request);
    }

    void initoption() {
        LocationClientOption option = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系
        int span = 3000;
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setScanSpan(span);
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setIsNeedLocationDescribe(true);
        mLocClient.setLocOption(option);

    }

    /**
     * 获取图片名称获取图片的资源id的方法
     *
     * @param imageName
     * @return
     */
    public int getResource(String imageName) {
        Context ctx = getActivity().getBaseContext();
        int resId = getResources().getIdentifier(imageName, "drawable", ctx.getPackageName());
        return resId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_msg, container, false);
        ButterKnife.bind(this, view);
//        if (layout == null) {
//            ctx = this.getActivity();
//            layout = ctx.getLayoutInflater().inflate(R.layout.fragment_msg,
//                    null);
//        } else {
//            ViewGroup parent = (ViewGroup) layout.getParent();
//            if (parent != null) {
//                parent.removeView(layout);
//            }
//        }
        dataSave = new ListDataSave(getActivity(), "baiyu");
        listBean = new ArrayList<KeyValuePair>();
        kaoqin_layout = (LinearLayout) view.findViewById(R.id.kaoqin_layout);
        linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);
        kaoqin_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MapActivity.class);
                startActivity(intent);
            }
        });
        dotsTextView = (DotsTextView) view.findViewById(R.id.dots);
        queue = Volley.newRequestQueue(getActivity());
        hn = (TextView) view.findViewById(R.id.hn);
        wendu = (TextView) view.findViewById(R.id.wendu);
        wanshang_text = (TextView) view.findViewById(R.id.wanshang_text);
        baitian_text = (TextView) view.findViewById(R.id.baitian_text);
        wanshang = (TextView) view.findViewById(R.id.wanshang);
        baitian = (TextView) view.findViewById(R.id.baitian);
        wanshang_img = (ImageView) view.findViewById(R.id.wanshang_img);
        baitian_img = (ImageView) view.findViewById(R.id.baitian_img);
        mLocClient = new LocationClient(getActivity());
        mLocClient.registerLocationListener(myListener);
        mLocClient.start();
        linearLayout.setVisibility(View.VISIBLE);
        dotsTextView.show();
        dotsTextView.start();
        EventBus.getDefault().register(this);
        return view;
    }

    //    private String deCodeContent(String s){
//        if (s == null) return null;
//        try {
//            String content =new String(Base64.decode(s,Base64.DEFAULT));
//            JSONObject jsonObject=new JSONObject(content);
//            String message=jsonObject.getString("msg");
//            return message;
//        } catch (JSONException e) {
//            return null;
//        }
//    }

    @Bind(R.id.kaoqin_content)
    TextView kaoqin_content;
    @Bind(R.id.kaoqin_unread_msg_number)
    TextView kaoqin_unread_msg_number;
//    @Bind(R.id.kaoqin_time)
//    TextView kaoqin_time;

    private void updateKaoqinList(String message) throws JSONException {
        kaoqin_unread_msg_number.setVisibility(View.VISIBLE);
        kaoqin_content.setText("" + message);
    }


    @Bind(R.id.huiyi_layout)
    LinearLayout huiyi_layout;
    @Bind(R.id.huiyi_content)
    TextView huiyi_content;
    @Bind(R.id.huiyi_unread_msg_number)
    TextView huiyi_unread_msg_number;
    @Bind(R.id.huiyi_time)
    TextView huiyi_time;

    private void updateConferceList(String message) throws JSONException {
        huiyi_unread_msg_number.setVisibility(View.VISIBLE);
        huiyi_content.setText("" + message);
    }

    @Bind(R.id.richeng_layout)
    LinearLayout richeng_layout;
    @Bind(R.id.richeng_content)
    TextView richeng_content;
    @Bind(R.id.richeng_unread_msg_number)
    TextView richeng_unread_msg_number;
//    @Bind(R.id.richeng_time)
//    TextView richeng_time;

    private void updatePlanList(String message) throws JSONException {
        richeng_unread_msg_number.setVisibility(View.VISIBLE);
        richeng_content.setText("" + message);
    }

    @Bind(R.id.liucheng_layout)
    LinearLayout liucheng_layout;
    @Bind(R.id.liucheng_content)
    TextView liucheng_content;
    @Bind(R.id.liucheng_unread_msg_number)
    TextView liucheng_unread_msg_number;
//    @Bind(R.id.liucheng_time)
//    TextView liucheng_time;


    private void showlistbean() {
        LoginResponse log = App.me().login();
        SharedPreferences sp = getActivity().getSharedPreferences("app_msg", getActivity().MODE_PRIVATE);
        String key0 = log.getUserId() + "_0";
        String key1 = log.getUserId() + "_1";
        String key2 = log.getUserId() + "_2";
        String key3 = log.getUserId() + "_3";
        String key4 = log.getUserId() + "_4";

        String str0 = sp.getString(key0, "0");
        String str1 = sp.getString(key1, "0");
        String str2 = sp.getString(key2, "0");
        String str3 = sp.getString(key3, "0");
        String str4 = sp.getString(key4, "0");

        if (!str0.equals("0")) {
            setlistbean(sp, key0);
        } else {
            kaoqin_unread_msg_number.setVisibility(View.GONE);
            kaoqin_content.setText("暂无新消息");
        }

        if (!str1.equals("0")) {
            setlistbean(sp, key1);
        } else {
            huiyi_unread_msg_number.setVisibility(View.GONE);
            huiyi_content.setText("暂无新消息");
        }
        if (!str2.equals("0")) {
            setlistbean(sp, key2);
        } else {
            richeng_unread_msg_number.setVisibility(View.GONE);
            richeng_content.setText("暂无新消息");
        }
        if (!str3.equals("0")) {
            setlistbean(sp, key3);
        } else {
            liucheng_unread_msg_number.setVisibility(View.GONE);
            liucheng_content.setText("暂无新消息");
        }
        if (!str4.equals("0")) {
            setlistbean(sp, key4);
        } else {
            youjian_unread_msg_number.setVisibility(View.GONE);
            youjian_content.setText("暂无新消息");
        }
        initoption();
    }

    @Override
    public void onStart() {
        super.onStart();
        LoginResponse user = App.me().login();
        if (null != user) {
            showlistbean();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        LoginResponse user = App.me().login();
        if (null != user) {
            showlistbean();
        }

    }

    private void updateProcessList(String message) throws JSONException {
        liucheng_unread_msg_number.setVisibility(View.VISIBLE);
        liucheng_content.setText("" + message);
    }

    @Bind(R.id.youjian_layout)
    LinearLayout youjian_layout;
    @Bind(R.id.youjian_content)
    TextView youjian_content;
    @Bind(R.id.youjian_unread_msg_number)
    TextView youjian_unread_msg_number;
    @Bind(R.id.youjian_time)
    TextView youjian_time;

    private void updateMailList(String message) throws JSONException {
        youjian_unread_msg_number.setVisibility(View.VISIBLE);
        youjian_content.setText("" + message);
    }

    @OnClick(R.id.richeng_layout)
    void richeng() {
        startActivity(new Intent(getActivity(), CalendarActivity.class));
    }

    @OnClick(R.id.youjian_layout)
    void youjian() {
//        ToastUtil.showShort(getActivity(),"正在努力开发中，敬请期待");
        Intent intent = new Intent(getActivity(), WebViewNoTitleActivity.class);
        intent.putExtra("url", mail);
        intent.putExtra("type", "1");
        startActivity(intent);
    }

    /*    @OnClick(R.id.kaoqin_layout)
        void kaoqin() {
            Intent intent = new Intent(getActivity(), MapActivity.class);
            startActivity(intent);
        }*/
    @OnClick(R.id.huiyi_layout)
    void huiyi() {
        startActivity(new Intent(getActivity(), MeetingActivity.class));
    }

    @OnClick(R.id.liucheng_layout)
    void liucheng() {
        //        startActivity(new Intent(getActivity(), MyBpmActivity.class));
        Intent intent = new Intent(getActivity(), WebViewNoTitleActivity.class);
        intent.putExtra("url", myToDo);
        intent.putExtra("type", "2");
        startActivity(intent);

    }

    private Activity ctx;

    private View layout;

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    //    else if (mark.equals("1")) {
//        Log.d("reg", "会议");
//        Intent intent0 = new Intent(MainActivity.this, MeetingInfoActivity.class);
//        intent0.putExtra("id", cid);
//        startActivity(intent0);
//    } else if (mark.equals("2")) {
//        Log.d("reg", "日程");
//        Intent intent = new Intent(MainActivity.this, CalendarInfoActivity.class);
//        intent.putExtra("id", cid);
//        startActivity(intent);
//    } else if (mark.equals("3")) {
//        Log.d("reg", "流程");
//        Intent intent1 = new Intent(MainActivity.this, WebViewNoTitleActivity.class);
//        intent1.putExtra("url", myToDo);
//        intent1.putExtra("type", "2");
//        startActivity(intent1);
//    } else if (mark.equals("4")) {
//        Log.d("reg", "邮件");
//        Intent intent2 = new Intent(MainActivity.this, WebViewNoTitleActivity.class);
//        intent2.putExtra("url", mail);
//        intent2.putExtra("type", "1");
//        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent2);
//    }
    @Subscriber(mode = ThreadMode.ASYNC)
    public void s(String s) {
        Log.d("reg", "msg" + s);
        try {
//            pp++;
            JSONObject data = new JSONObject(s);
            String contentString = data.getString("content");
            JSONObject content = new JSONObject(contentString);
            if (content.getString("type").contains("考勤抽查")) {
                dialog(content.getString("msg"), "抽查签到", 0, content.getString("insid"));
            } else if (content.getString("type").contains("会议通知")) {
                dialog(content.getString("msg"), "会议通知", 1, content.getString("cid"));
            } else if (content.getString("type").contains("邮件通知")) {
                dialog(content.getString("msg"), "邮件通知", 4, null);
            } else if (content.getString("type").contains("日程安排")) {
                dialog(content.getString("msg"), "日程安排", 2, content.getString("pid"));
            } else if (content.getString("type").contains("流程通知")) {
                dialog(content.getString("msg"), "流程通知", 3, null);
            }
            LoginResponse log = App.me().login();
            if (log != null) {
                String key = log.getUserId() + "_" + data.getString("mesType");
                SharedPreferences sp = getActivity().getSharedPreferences("app_msg", getActivity().MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();
                edit.putInt(key + "_what", Integer.valueOf(data.getString("mesType")));
                edit.putString(key + "_msg", content.getString("msg"));
                edit.putString(key, "1");
                edit.commit();
                setlistbean(sp, key);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    dialog(content.getString("msg"),"抽查签到",0,content.getString("insid"));
    protected void dialog(String msg, String lx, final int type, final String insid) {

        Dialog dialog = new AlertDialog.Builder(getActivity()).setIcon(
                android.R.drawable.btn_star).setTitle(lx).setMessage(msg).setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (type == 0) {
                            SPUtils.put(getActivity(), "insid", insid);
                            Intent intent = new Intent(getActivity(), MapActivity.class);
                            startActivity(intent);
                        } else if (type == 1) {
                            Intent intent = new Intent(getActivity(), MeetingInfoActivity.class);
                            intent.putExtra("id", insid);
                            startActivity(intent);
                        } else if (type == 4) {
                            Intent intent0 = new Intent(getActivity(), WebViewNoTitleActivity.class);
                            intent0.putExtra("url", mail);
                            intent0.putExtra("type", "1");
                            startActivity(intent0);
                        } else if (type == 2) {
                            Intent intent1 = new Intent(getActivity(), CalendarInfoActivity.class);
                            intent1.putExtra("id", insid);
                            startActivity(intent1);
                        } else if (type == 3) {
                            Intent intent2 = new Intent(getActivity(), WebViewNoTitleActivity.class);
                            intent2.putExtra("url", myToDo);
                            intent2.putExtra("type", "2");
                            startActivity(intent2);
                        }
                        dialog.dismiss();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();
        dialog.show();
    }

    private void setlistbean(SharedPreferences sp, String key) {
        String str = sp.getString(key, "0");

        if (!str.equals("0")) {
            listBean.add(new KeyValuePair(key, str));
            dataSave.setDataList("javaBean", listBean);
            Message msg = new Message();
            msg.what = sp.getInt(key + "_what", 0);
            msg.obj = sp.getString(key + "_msg", "");
            Log.d("reg","msg.obj:"+msg.obj);
            Log.d("reg"," msg.what :"+ msg.what );
            handler.sendMessage(msg);
        }
    }

//    private void initDate() {
//        msgControl = new MsgControl(getActivity());
//        if (App.me().login() != null) {
//            msgControl.getMessage(App.me().login().getUserId());
//        }
//    }


//    class MsgControl extends Ok {
//
//        public MsgControl(Context context) {
//            super(context);
//        }
//
//        public void getMessage(String userId) {
//            super.post(Constant.DOMAIN + "mobile/myMessage/messagePush.do", "userId", userId);
//        }
//
//
//        @Override
//        public void onFailure(Call call, IOException e) {
//
//        }
//
//        @Override
//        public void onResponse(Call call, Response response) throws IOException {
//            String re = response.body().string();
//            Log.d("reg", "result:" + re);
//            App.me().checklogin(getActivity(), re);
//            // JSON串转用户对象列表
//            try {
//                //发消息更新UI
//                Message msg = new Message();
//                msg.what = UPDATE_FROM_JIEKOU;
//                msg.obj = re;
//                handler.sendMessage(msg);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//    }

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
            if (isFirstLoc == true) {
                Log.d("reg", "location:" + location.getAddrStr());
                Log.d("reg", "isFirstLoc:" + isFirstLoc);
                if (null == location.getAddrStr()) {
                    isFirstLoc = true;
                } else {
                    isFirstLoc = false;
                    LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                    Log.d("reg", "lon:" + ll.longitude);
                    Log.d("reg", "lat:" + ll.latitude);
                    Log.d("reg", "DateUtil.getDate1()):" + DateUtil.getDate1());
                    Log.d("reg", "location.getAddrStr():" + location.getAddrStr());
                    Log.d("reg", "getAddr():" + App.me().getAddr());
                    Log.d("reg", "DateUtil.getDate1();" + DateUtil.getStringToDate(DateUtil.getDate1()));
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            dotsTextView.stop();
                            dotsTextView.hide();
                            linearLayout.setVisibility(View.GONE);
                        }
                    });
                    if (App.me().getAddr() != null && App.me().getAddr().equals(location.getAddrStr())) {
                        if (App.me().getDefault() != 0) {
                            if (DateUtil.getStringToDate(DateUtil.getDate1()) > App.me().getDefault()) {
                                Log.d("reg", "!");
                                weatherClick(ll.longitude, ll.latitude, location.getAddrStr());
                                App.me().setDefault(DateUtil.getStringToDate(DateUtil.getDate1()));
                            } else {
                                Log.d("reg", "0");
                                Log.d("reg", "App.me().getdizhi();" + App.me().getdizhi());
                                Log.d("reg", "App.me().getwendu();" + App.me().getwendu());
                                Log.d("reg", "App.me().getbaitian_img();" + App.me().getbaitian_img());
                                Log.d("reg", "App.me().getwanshang_img();" + App.me().getwanshang_img());

                                if (isAdded()) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (App.me().getdizhi() != null) {
                                                hn.setText(App.me().getdizhi());
                                            }
                                            if (App.me().getwendu() != null) {
                                                wendu.setText(App.me().getwendu());
                                            }
                                            if (App.me().getbaitian_wenzi() != null) {
                                                baitian_text.setText(App.me().getbaitian_wenzi());
                                            }

                                            if (App.me().getwanshang_wenzi() != null) {
                                                wanshang_text.setText(App.me().getwanshang_wenzi());
                                            }
                                            if (App.me().getbaitian_img() != null) {
                                                baitian.setVisibility(View.VISIBLE);
                                                baitian_img.setImageDrawable(getResources().getDrawable(getResource(App.me().getbaitian_img())));
                                            } else {
                                                baitian.setVisibility(View.GONE);
                                            }
                                            if (App.me().getwanshang_img() != null) {
                                                wanshang.setVisibility(View.VISIBLE);
                                                wanshang_img.setImageDrawable(getResources().getDrawable(getResource(App.me().getwanshang_img())));
                                            } else {
                                                wanshang.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                }
                            }
                        } else {
                            weatherClick(ll.longitude, ll.latitude, location.getAddrStr());
                            App.me().setDefault(DateUtil.getStringToDate(DateUtil.getDate1()));
                        }
                    } else {
                        weatherClick(ll.longitude, ll.latitude, location.getAddrStr());
                        App.me().setAddr(location.getAddrStr());
                    }

                }

            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }

      /*  @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mLocClient.stop();
    }
}
