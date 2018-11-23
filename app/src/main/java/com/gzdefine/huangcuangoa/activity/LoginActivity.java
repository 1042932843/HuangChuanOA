package com.gzdefine.huangcuangoa.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.gzdefine.huangcuangoa.App;
import com.gzdefine.huangcuangoa.MainActivity;
import com.gzdefine.huangcuangoa.Model.MyOkmodel;
import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.Service.WebSocketService;
import com.gzdefine.huangcuangoa.entity.LoginResponse;
import com.gzdefine.huangcuangoa.util.Constant;
import com.gzdefine.huangcuangoa.util.Ok;
import com.gzdefine.huangcuangoa.util.SHA;
import com.gzdefine.huangcuangoa.util.SPUtils;
import com.gzdefine.huangcuangoa.util.StringKit;
import com.gzdefine.huangcuangoa.util.SystemBarTintManager;
import com.gzdefine.huangcuangoa.util.ToastUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText et_userid;
    private EditText et_password;
    public static final int REQUEST_CODE = 'l' + 'o' + 'g' + 'i' + 'n';
    private MyOkmodel myOkmodel;
    String seesion = "";
    private String user;
    private String pawd;
    private String password;
    private String passwor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.white);//通知栏所需颜色
        }

        setContentView(R.layout.activity_login);
        et_userid = (EditText) findViewById(R.id.et_userid);
        et_password = (EditText) findViewById(R.id.et_password);
        // 读取偏好设置: 已登录过
        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        String phone = sharedPreferences.getString("phone", null); // 最后一次登录的手机号码
        String pawd = sharedPreferences.getString("pawd", null); // 最后一次登录的手机号码
        this.et_userid.setText(phone);
        this.et_password.setText(pawd);
//        PgyUpdateManager.register(this,new PgyUpdateManagerListener(this));
        ButterKnife.bind(this);
        myOkmodel = new MyOkmodel(this);
        stopService(new Intent(this, WebSocketService.class));
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

    @OnClick(R.id.upload)
    void upload() {
        String user = "" + et_userid.getText().toString();
        password = "" + et_password.getText().toString();
        if (StringKit.isEmpty(user)) {
            ToastUtil.showShort(this, "请输入账号");
            return;
        }

        if (StringKit.isEmpty(password)) {
            ToastUtil.showShort(this, "请输入密码");
            return;
        }

        if (user.equals("admin")) {
            ToastUtil.showShort(this, "管理员帐号只能在oa办公系统后台使用");
            return;
        }
        try {
            passwor = SHA.getSHA256StrJava(password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Login login = new Login(this);
        login.login(user, passwor);
    }


    void loginSucees(String re, String user, String pawd) {
        try {
            JSONObject object = new JSONObject(re);
            String s = object.getString("data");
            Log.d("reg", "s:" + s);
            String message = object.getString("message");
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
                edit.putString("pawd", password);
                edit.commit();
//                App.me().setName(user);
//                App.me().setPawd(pawd);
//                setResult(RESULT_OK); // 回调登录成功
                App.me().login(login);
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                ToastUtil.showShort(this, message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


//    @OnClick(R.id.register)
//    void register() {
//        startActivity(new Intent(this, RegisterActivity.class));
//    }
//    @OnClick(R.id.forget_password)
//    void forget_password() {
//        startActivity(new Intent(this, UpdatePasswordActivity.class));
//
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //登陆
    class Login extends Ok {
        public Login(Context context) {
            super(context);
        }

        public void login(String username, String password) {
            user = username;
            pawd = password;

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
            Log.d("reg", "header " + headers);
            //从请求头获取cookies组合
            List<String> cookies = headers.values("Set-Cookie");
            //遍历获取含JSESSIONID的字段
            for (int i = 0; i < cookies.size(); i++) {
                String session = cookies.get(i);
                Log.d("reg", "session:" + session);
//                if (session.indexOf("logInOutId") > -1) {
//                   continue;
//                }
                seesion = session.substring(0, session.indexOf(";"));
                if (seesion.contains("JSESSIONID")) {
                    Log.i("info_s", "放了session  :" + seesion);
                    SPUtils.put(App.me(), "session", session);//（就是我们要的会话）
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    loginSucees(re, user, pawd);
                }
            });
        }
    }
}
