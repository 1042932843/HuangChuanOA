package com.gzdefine.huangcuangoa.activity;

import android.os.Bundle;
import android.widget.EditText;

import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.util.StringKit;
import com.gzdefine.huangcuangoa.util.ToastUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RegisterActivity extends BaseActivity {

    @Bind(R.id.et_password)
    EditText et_password;
    @Bind(R.id.et_yzm)
    EditText et_yzm;
    @Bind(R.id.et_phone)
    EditText et_phone;


    @Override
    public int initContentID() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        setAppTitle("注册");
    }
    @OnClick(R.id.register)
    void register(){
        String password=et_password.getText().toString();
        String yzm=et_yzm.getText().toString();
        String phone=et_phone.getText().toString();
        if (StringKit.isEmpty(password)){
            et_password.setError("请输入原密码");
            et_password.requestFocus();
            return;
        }
        if (StringKit.isEmpty(phone)){
            et_phone.setError("请输入原密码");
            et_phone.requestFocus();
            return;
        }
        if (StringKit.isEmpty(yzm)){
            et_yzm.setError("请输入验证码");
            et_yzm.requestFocus();
            return;
        }
        ToastUtil.showShort(this,"注册成功!");

    }
    @OnClick(R.id.img_back)
    void back() {
        finish();
    }
}
