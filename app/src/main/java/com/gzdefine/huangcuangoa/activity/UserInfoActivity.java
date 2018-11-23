package com.gzdefine.huangcuangoa.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.ListPopupWindow;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.gzdefine.huangcuangoa.App;
import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.util.Constant;
import com.gzdefine.huangcuangoa.util.Ok;
import com.gzdefine.huangcuangoa.util.StringKit;
import com.gzdefine.huangcuangoa.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;


public class UserInfoActivity extends BaseActivity {
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case 0:
                        String re = (String) msg.obj;
                        JSONObject jsonObject = new JSONObject(re);

                        String success=jsonObject.getString("success");
                        if (success.equals("true")){
                            JSONArray jsonArray=jsonObject.getJSONArray("paramList");
                            JSONObject entiy=jsonArray.getJSONObject(0);
                            user_name.setText(""+entiy.getString("userName"));
                            fale.setText((""+entiy.getString("sex")).equals("Male")?"男":"女");
                            depart.setText(""+entiy.getString("mainDep"));
                            phone.setText(""+entiy.getString("mobile"));
                            idcard.setText(""+entiy.getString("idcard"));
                            email.setText(""+entiy.getString("email"));
                            qq.setText(""+entiy.getString("QQ"));
                            jjlxr.setText(""+entiy.getString("urgent"));
                            jjlxrdh.setText(""+entiy.getString("urgentMobile"));
                        }else {
                            String message=jsonObject.getString("msg");
                            ToastUtil.showShort(UserInfoActivity.this,message);
                        }
                        break;
                    case 1:
                        String re2 = (String) msg.obj;
                        JSONObject jsonObject2 = new JSONObject(re2);
                        String message2=jsonObject2.getString("msg");
                        ToastUtil.showShort(UserInfoActivity.this,message2);
                        finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    });

    @Bind(R.id.user_name)
    EditText user_name;
    @Bind(R.id.fale)
    Button fale;
    @Bind(R.id.depart)
    EditText depart;
    @Bind(R.id.phone)
    EditText phone;
    @Bind(R.id.idcard)
    EditText idcard;
    @Bind(R.id.email)
    EditText email;
    @Bind(R.id.qq)
    EditText qq;
    @Bind(R.id.jjlxr)
    EditText jjlxr;
    @Bind(R.id.jjlxrdh)
    EditText jjlxrdh;
    UserControl userControl;
    ListPopupWindow expressPopupWindow;
    private List<String> strings1;

    @Override
    public int initContentID() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        setAppTitle("个人信息");
        initData();
    }

    private void initData() {
        userControl = new UserControl(this);
        if (null != App.me().login()) {
            userControl.getUserInfo(App.me().login().getUserId());
        }
        strings1 = new ArrayList<String>();
        strings1.add("男");
        strings1.add("女");
        expressPopupWindow = new ListPopupWindow(this);
        expressPopupWindow.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_item, strings1));
        expressPopupWindow.setAnchorView(fale);
        expressPopupWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
        expressPopupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        expressPopupWindow.setModal(true);
        expressPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                fale.setText(strings1.get(position));
                fale.setTextColor(Color.parseColor("#000000"));
                Log.d("reg", "性别：" + strings1.get(position));
                expressPopupWindow.dismiss();
            }
        });
    }

    @OnClick(R.id.fale)
    void  fale(){
        expressPopupWindow.show();
    }

    @OnClick(R.id.update_userinfo)
    void update() {
        String user_nameString = user_name.getText().toString();
        String  faleString = fale.getText().toString().equals("男")?"Male":"Famale";
        String departString = depart.getText().toString();
        String phoneString = phone.getText().toString();
        String idcardString = idcard.getText().toString();
        String emailString = email.getText().toString();
        String qqString = qq.getText().toString();
        String jjlxrString = jjlxr.getText().toString();
        String jjlxrdhString = jjlxrdh.getText().toString();
        if (StringKit.isEmpty(user_nameString)){
            ToastUtil.showShort(this,"请输入用户名");
            user_name.requestFocus();
            return;
        }
        if (StringKit.isEmpty(faleString)){
            ToastUtil.showShort(this,"请输入性别");
            fale.requestFocus();
            return;
        }
        if (StringKit.isEmpty(departString)){
            ToastUtil.showShort(this,"请输入部门");
            depart.requestFocus();
            return;
        }
        if (StringKit.isEmpty(phoneString)){
            ToastUtil.showShort(this,"请输入手机号");
            phone.requestFocus();
            return;
        }
        if (StringKit.isEmpty(idcardString)){
            ToastUtil.showShort(this,"请输入身份证");
            idcard.requestFocus();
            return;
        }
        if (StringKit.isEmpty(emailString)){
            ToastUtil.showShort(this,"请输入邮箱");
            email.requestFocus();
            return;
        }
        if (StringKit.isEmpty(qqString)){
            ToastUtil.showShort(this,"请输入QQ");
            qq.requestFocus();
            return;
        }
        if (StringKit.isEmpty(jjlxrString)){
            ToastUtil.showShort(this,"请输入紧急联系人");
            jjlxr.requestFocus();
            return;
        }
        if (StringKit.isEmpty(jjlxrdhString)){
            ToastUtil.showShort(this,"请输入紧急联系人电话");
            jjlxrdh.requestFocus();
            return;
        }
        userControl.updateUserInfo(App.me().login().getUserId(),user_nameString,faleString,departString,phoneString
                                   ,idcardString,emailString,qqString,jjlxrString,jjlxrdhString );


    }

    @OnClick(R.id.img_back)
    void back(){
        finish();
    }


    class UserControl extends Ok {
        int what;

        public UserControl(Context context) {
            super(context);
        }

        public void getUserInfo(String userId) {
            what = 0;
            super.post(Constant.DOMAIN + "mobile/userInformation/userget.do", "userId", userId);
        }
        public void updateUserInfo(String userId,String user_name,String fale,String depart,
                                   String phone,String idcard,String email,
                                   String qq,String jjlxr,String jjlxrdh) {
            what = 1;
            super.post(Constant.DOMAIN + "mobile/userInformation/userUpdate.do", "QQ", qq,
                     "email", email, "idcard", idcard, "mobile", phone,
                    "sex", fale, "urgent", jjlxr, "urgentMobile", jjlxrdh, "userId", userId,
                    "userName",user_name);
        }


        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String re = response.body().string();
            Log.d("reg", "result:" + re);
            App.me().checklogin(UserInfoActivity.this, re);
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
}
