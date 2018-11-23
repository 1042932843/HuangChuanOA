package com.gzdefine.huangcuangoa.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;

import com.gzdefine.huangcuangoa.App;
import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.util.Constant;
import com.gzdefine.huangcuangoa.util.Ok;
import com.gzdefine.huangcuangoa.util.StringKit;
import com.gzdefine.huangcuangoa.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class UpdatePasswordActivity extends BaseActivity {
    @Bind(R.id.et_new_password)
    EditText et_new_password;
    @Bind(R.id.et_new_password2)
    EditText et_new_password2;
    PassWordControl passWordControl;
    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                JSONObject jsonObject=new JSONObject((String) msg.obj);
                  String message= jsonObject.getString("msg");
                ToastUtil.showShort(UpdatePasswordActivity.this,message);
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }
    });

    @Override
    public int initContentID() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        ButterKnife.bind(this);
        setAppTitle("修改密码");
        passWordControl=new PassWordControl(this);
    }

    @OnClick(R.id.update_password)
    void update_password() {
        String newPassWord=et_new_password.getText().toString();//新密码
        String newPassWord2=et_new_password2.getText().toString();//确认密码
        if (StringKit.isEmpty(newPassWord)){
            et_new_password.setError("请输入新密码");
            et_new_password.requestFocus();
            return;
        }
        if (!newPassWord.equals(newPassWord2)){
            et_new_password2.setError("2次密码输入不一致,请重新输入");
            et_new_password2.requestFocus();
            return;
        }
        passWordControl.upDate(App.me().login().getUserId(),newPassWord);
    }

    @OnClick(R.id.img_back)
    void back() {
        finish();
    }
    class PassWordControl extends Ok {

        public PassWordControl(Context context) {
            super(context);
        }


        public void upDate(String userId,String password) {
            super.post(Constant.DOMAIN + "mobile/userInformation/modifyPassword.do","userId",userId,"password",password);
        }

        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String re = response.body().string();
            Log.d("reg", "result:" + re);
            App.me().checklogin(UpdatePasswordActivity.this, re);
            // JSON串转用户对象列表
            try {
                //发消息更新UI
                Message msg = new Message();
                msg.obj = re;
                handler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
