package com.gzdefine.huangcuangoa;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.appcompat.BuildConfig;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gzdefine.huangcuangoa.Service.LocationService;
import com.gzdefine.huangcuangoa.activity.LoginActivity;
import com.gzdefine.huangcuangoa.entity.LoginResponse;
import com.gzdefine.huangcuangoa.util.Constant;
import com.gzdefine.huangcuangoa.util.Ok;
import com.gzdefine.huangcuangoa.util.SPUtils;
import com.gzdefine.huangcuangoa.util.ToastUtil;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.pgyersdk.Pgy;
import com.pgyersdk.activity.FeedbackActivity;
import com.pgyersdk.crash.PgyCrashManager;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Response;

import static com.gzdefine.huangcuangoa.util.ConfigKit.CONFIG;


/**
 * Created by K on 2016/3/21.
 */
public class App extends Application {

    static App me;
    private SharedPreferences mSharedPreference;
    private SharedPreferences.Editor editor;
    private Lgon logi;
    private String phone;
    private String pawd;
    public boolean logflag = true;

    public static App me() {
        return me;
    }

    String seesion = "";
    ArrayList<Activity> lists;

    Toast toast;
    public LocationService locationService;
    public Vibrator mVibrator;

    public void toast(CharSequence text) {
        try {
            Log.d("RegOne", "text" + TextUtils.isEmpty(text));
            if (TextUtils.isEmpty(text)) return;
            if (toast == null) {
                toast = Toast.makeText(me, text, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
            }
            toast.setText(text);
            toast.show();
        } catch (Exception e) {
            PgyCrashManager.reportCaughtException(this, e);
            Log.e("RegOne", "err", e);
        }
    }

    public SharedPreferences config() {
        return getSharedPreferences(CONFIG, MODE_PRIVATE);
    }


    InputMethodManager input;

    public InputMethodManager input() {
        if (input == null) {
            input = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        }
        return input;
    }

    public boolean checkloginflag() {
        return logflag;
    }

//    public void checklogin(final Activity context, String re) {
//        Log.d("reg", "re:" + re);
//        if (re.contains("用户登录")) {
//            if(logflag) {
//                logflag=false;
//                TimerTask task = new TimerTask() {
//                    public void run() {
//                        loginAct(context);
//                    }
//                };
//                Timer timer = new Timer();
//                timer.schedule(task, 1000);
//            }
//            return;
//        } else if (re.contains("您访问的页面出错了")) {
//            Looper.prepare();
//            ToastUtil.showShort(context, "接口出错了,请联系后台开发人员");
//            Looper.loop();
//        }
//
//        // 读取偏好设置: 已登录过
//        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
//        // 最后一次登录的手机号码
//        phone = sharedPreferences.getString("phone", null);
//        // 最后一次登录的手机号码
//        pawd = sharedPreferences.getString("pawd", null);
//        if (phone != null && pawd != null) {
//            logflag = true;
//        }
//    }
//
//    void loginAct(Activity context) {
//
//        if (logi != null) {
//            // 读取偏好设置: 已登录过
//            SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
//            // 最后一次登录的手机号码
//            phone = sharedPreferences.getString("phone", null);
//            // 最后一次登录的手机号码
//            pawd = sharedPreferences.getString("pawd", null);
//            if (phone != null && pawd != null) {
//                String passwo = SHA.getSHA256StrJava(pawd);
//                logi.login(phone, passwo);
//            } else {
//
//                Intent intent = new Intent(context, LoginActivity.class);
//                context.startActivity(intent);
//                finishAll();
//            }
//
//        }
//    }

    public void checklogin(final Activity context, String re) {
        if (re.contains("用户登录")) {
            loginAct(context);
        } else if (re.contains("您访问的页面出错了")) {
            Looper.prepare();
            ToastUtil.showShort(context, "接口出错了,请联系后台开发人员");
            Looper.loop();
        }
    }

    void loginAct(Activity context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
       // finishAll();
    }

    public void hideInput(Window window) {
        View view = window.getCurrentFocus();
        hideInput(view);
    }

    public void hideInput(View view) {
        if (view != null) {
            view.clearFocus();
            IBinder binder = view.getWindowToken();
            input().hideSoftInputFromWindow(binder, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    Handler handler;

    public Handler handler() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        return handler;
    }

    LiteOrm orm;

    public LiteOrm orm() {
        if (orm == null) {
            orm = LiteOrm.newSingleInstance(this, "LocalStorage.db");
            orm.setDebugged(BuildConfig.DEBUG);
        }
        return orm;
    }

    public void addActivity(Activity activity) {
        lists.add(activity);
    }

    public void finishAll() {
        for (int i = 0; i < lists.size(); i++) {
            lists.get(i).finish();
        }
        lists.removeAll(lists);
    }

    public void removeActivity(Activity activity) {
        lists.remove(activity);
    }

    @Nullable
    LoginResponse login;

    public LoginResponse login() {
        if (login == null) {
            List<LoginResponse> list = orm().query(QueryBuilder.get(LoginResponse.class).limit(0, 1));
            if (list.size() > 0) {
                login = list.get(0);
                Log.d("login", "LoginResponse:" + login);
            }
        }
        return login;
    }

    public void login(@NonNull LoginResponse login) {
        orm().deleteAll(LoginResponse.class);
        orm().save(this.login = login);
    }

    public void logout() {
        orm().deleteAll(LoginResponse.class);
        this.login = null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        me = this;
        logi = new Lgon(this);
//        bindPushService(); // 绑定腾讯信鸽推送服务
//        SDKInitializer.initialize(this);
        Pgy.setDebug(BuildConfig.DEBUG); // 蒲公英
        PgyCrashManager.register(me = this); // 蒲公英异常捕捉
        FeedbackActivity.setBarImmersive(true); // 蒲公英反馈开启沉浸式布局 未用到该功能
        lists = new ArrayList<>();
        /***
         * 初始化定位sdk，建议在Application中创建
         */
        locationService = new LocationService(getApplicationContext());
        mVibrator = (Vibrator) getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        SDKInitializer.initialize(getApplicationContext());
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
//            bindPushService();


    }



    // 绑定推送服务, 重复调用则重新绑定
    public static void bindPushService() {
        LoginResponse user = App.me.login();
        if (null != user) {

        }
    }
    /*
    监听GPS
  */

    public void initGPS(final Activity activity) {
        LocationManager locationManager = (LocationManager) activity
                .getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager
                .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            App.me().toast("请打开GPS");
            AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
            dialog.setMessage("请打开GPS");
            dialog.setPositiveButton("确定",
                    new android.content.DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                            // 转到手机设置界面，用户设置GPS
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            activity.startActivityForResult(intent, 0); // 设置完成后返回到原来的界面

                        }
                    });
            dialog.setNeutralButton("取消", new android.content.DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                }
            });
            dialog.show();
        } else {
            // 弹出Toast
//          Toast.makeText(TrainDetailsActivity.this, "GPS is ready",
//                  Toast.LENGTH_LONG).show();
//          // 弹出对话框
//          new AlertDialog.Builder(this).setMessage("GPS is ready")
//                  .setPositiveButton("OK", null).show();
        }
    }

    public long getDefault() {
        SharedPreferences sharedPreferences = me.getSharedPreferences("Default", 0);
        return sharedPreferences.getLong("de", 0);
    }


    public void setDefault(long resultInfo) {
        SharedPreferences sharedPreferences = me.getSharedPreferences("Default", 0);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putLong("de", resultInfo);
        e.commit();
    }

    public String getAddr() {
        SharedPreferences sharedPreferences = me.getSharedPreferences("Addr", 0);
        return sharedPreferences.getString("ad", "");
    }


    public void setAddr(String resultInfo) {
        SharedPreferences sharedPreferences = me.getSharedPreferences("Addr", 0);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString("ad", resultInfo);
        e.commit();
    }

    public String getwendu() {
        SharedPreferences sharedPreferences = me.getSharedPreferences("wendu", 0);
        return sharedPreferences.getString("we", "");
    }


    public void setwendu(String resultInfo) {
        SharedPreferences sharedPreferences = me.getSharedPreferences("wendu", 0);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString("we", resultInfo);
        e.commit();
    }

    public String getbaitian_img() {
        SharedPreferences sharedPreferences = me.getSharedPreferences("baitian_img", 0);
        return sharedPreferences.getString("ba", "");
    }


    public void setbaitian_img(String resultInfo) {
        SharedPreferences sharedPreferences = me.getSharedPreferences("baitian_img", 0);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString("ba", resultInfo);
        e.commit();
    }

    public String getwanshang_img() {
        SharedPreferences sharedPreferences = me.getSharedPreferences("wanshang_img", 0);
        return sharedPreferences.getString("wa", "");
    }


    public void setwanshang_img(String resultInfo) {
        SharedPreferences sharedPreferences = me.getSharedPreferences("wanshang_img", 0);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString("wa", resultInfo);
        e.commit();
    }

    public String getdizhi() {
        SharedPreferences sharedPreferences = me.getSharedPreferences("dizhi", 0);
        return sharedPreferences.getString("di", "");
    }


    public void setdizhi(String resultInfo) {
        SharedPreferences sharedPreferences = me.getSharedPreferences("dizhi", 0);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString("di", resultInfo);
        e.commit();
    }

    public String getbaitian_wenzi() {
        SharedPreferences sharedPreferences = me.getSharedPreferences("bt_wz", 0);
        return sharedPreferences.getString("wenzi", "");
    }


    public void setbaitian_wenzi(String resultInfo) {
        SharedPreferences sharedPreferences = me.getSharedPreferences("bt_wz", 0);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString("wenzi", resultInfo);
        e.commit();
    }

    public String getwanshang_wenzi() {
        SharedPreferences sharedPreferences = me.getSharedPreferences("ws_wz", 0);
        return sharedPreferences.getString("ws_", "");
    }


    public void setwanshang_wenzi(String resultInfo) {
        SharedPreferences sharedPreferences = me.getSharedPreferences("ws_wz", 0);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString("ws_", resultInfo);
        e.commit();
    }


    public String getName() {
        SharedPreferences sharedPreferences = me.getSharedPreferences("name", 0);
        return sharedPreferences.getString("na", "");
    }


    public void setName(String resultInfo) {
        SharedPreferences sharedPreferences = me.getSharedPreferences("name", 0);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString("na", resultInfo);
        e.commit();
    }

    public String getPawd() {
        SharedPreferences sharedPreferences = me.getSharedPreferences("pawd", 0);
        return sharedPreferences.getString("pa", "");
    }


    public void setPawd(String resultInfo) {
        SharedPreferences sharedPreferences = me.getSharedPreferences("pawd", 0);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString("pa", resultInfo);
        e.commit();
    }


    public String getTime(String type) {
        SharedPreferences sharedPreferences = me.getSharedPreferences("Time", 0);
        return sharedPreferences.getString(type + "_data", "");
    }


    public void setTime(String resultInfo, String type) {
        SharedPreferences sharedPreferences = me.getSharedPreferences("Time", 0);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString(type + "_data", resultInfo);
        e.commit();
    }


    public void clearmsgcount(String type) {
        LoginResponse log = App.me().login();
        SharedPreferences sp = me().getSharedPreferences("app_msg", me().MODE_PRIVATE);
        if(log!=null){
            String key = log.getUserId() + "_" + type;
            SharedPreferences.Editor edit = sp.edit();
            edit.putInt(key + "_what", 0);
            edit.putString(key + "_msg", "");
            edit.putString(key, "0");
            edit.commit();
        }

    }

    public boolean saveArray(List<String> list) {
        mSharedPreference = me().getSharedPreferences("ingoreList", me().MODE_PRIVATE);
        SharedPreferences.Editor mEdit1 = mSharedPreference.edit();
        mEdit1.putInt("Status_size", list.size());

        for (int i = 0; i < list.size(); i++) {
            mEdit1.remove("Status_" + i);
            mEdit1.putString("Status_" + i, list.get(i));
        }
        return mEdit1.commit();
    }

    public List<String> loadArray() {
        List<String> list = new ArrayList<>();
        mSharedPreference = me().getSharedPreferences("ingoreList", me().MODE_PRIVATE);
        list.clear();
        int size = mSharedPreference.getInt("Status_size", 0);
        for (int i = 0; i < size; i++) {
            list.add(mSharedPreference.getString("Status_" + i, null));

        }
        return list;
    }

    public void QingChu() {
        SharedPreferences.Editor editor = mSharedPreference.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * 保存List
     *
     * @param tag
     * @param datalist
     */
    public <T> void setDataList(String tag, List<T> datalist) {
        if (null == datalist || datalist.size() <= 0)
            return;

        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(datalist);
        Log.d("reg", "存:" + strJson);
        editor = mSharedPreference.edit();
        if (null != editor) {
            editor.clear();
            editor.putString(tag, strJson);
            editor.commit();
        }
    }

    /**
     * 获取List
     *
     * @param tag
     * @return
     */
    public <T> List<T> getDataList(String tag) {
        List<T> datalist = new ArrayList<T>();
        mSharedPreference = me().getSharedPreferences("baiyu", Context.MODE_PRIVATE);
        if (null != mSharedPreference) {
            String strJson = mSharedPreference.getString(tag, null);
            Log.d("reg", "取:" + strJson);
            if (null == strJson) {
                return datalist;
            }
            Gson gson = new Gson();
            datalist = gson.fromJson(strJson, new TypeToken<List<T>>() {
            }.getType());
        }
        return datalist;

    }

    //登陆
    class Lgon extends Ok {
        public Lgon(Context context) {
            super(context);
        }

        public void login(String username, String password) {

            super.post(Constant.DOMAIN + "login.do", "acc", username, "pd", password, "rememberMe", "0", "from", "mobile");
        }

        @Override
        public void onFailure(Call call, IOException e) {
            Log.i("info_callFailure", e.toString());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            final String re = response.body().string();
            Log.d("reg", "My_Ok_Http.result" + re);
            //请求头
            Headers headers = response.headers();
            //从请求头获取cookies组合
            List<String> cookies = headers.values("Set-Cookie");
            //遍历获取含JSESSIONID的字段
            for (int i = 0; i < cookies.size(); i++) {
                String session = cookies.get(i);
                seesion = session.substring(0, session.indexOf(";"));
                if (seesion.contains("JSESSIONID")) {
                    Log.i("info_s", "放了session  :" + seesion);
                    SPUtils.put(App.me(), "session", session);//（就是我们要的会话）
                }
            }
            loginSucees(re, phone, pawd);
        }
    }


    void loginSucees(String re, String user, String pawd) {
        try {
            Log.d("reg", "re:" + re);
            JSONObject object = new JSONObject(re);
            String s = object.getString("data");
            final String message = object.getString("message");
            String success = object.getString("success");
            if (success.equals("true")) {
                JSONObject data = new JSONObject(s);
                LoginResponse login = new LoginResponse();
                login.address = data.getString("address");
                login.fullname = data.getString("fullname");
                if (data.has("photo")) {
                    login.photo = data.getString("photo");
                }
                login.username = data.getString("username");
                login.phone = data.getString("mobile");
                login.userId = data.getString("userId");
                //                // 保存偏好设置
                SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString("phone", user);
                edit.putString("pawd", pawd);
                edit.commit();
//                App.me().setName(user);
//                App.me().setPawd(pawd);
                App.me().login(login);
                logflag = true;

//                startActivity(new Intent(this, MainActivity.class));
            } else {
//                ToastUtil.showShort(this, message);
//                  App.me().toast(message);
                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        //执行耗时操作
                        try {
                            Log.e("bm", "runnable线程： " + Thread.currentThread().getId() + " name:" + Thread.currentThread().getName());
                            Toast.makeText(me(), message, Toast.LENGTH_LONG).show();
                            if (message.indexOf("密码或用户名不正确！") != -1) {
                                App.me().logout();
                                Intent intent = new Intent(me, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finishAll();
                            }
                            Thread.sleep(2000);
                            Log.e("bm", "执行完耗时操作了~");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                new Thread() {
                    public void run() {
                        Looper.prepare();
                        new Handler().post(runnable);//在子线程中直接去new 一个handler
                        Looper.loop();//这种情况下，Runnable对象是运行在子线程中的，可以进行联网操作，但是不能更新UI
                    }
                }.start();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}