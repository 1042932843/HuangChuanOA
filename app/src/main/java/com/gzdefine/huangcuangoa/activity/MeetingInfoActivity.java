package com.gzdefine.huangcuangoa.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gzdefine.huangcuangoa.App;
import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.util.Constant;
import com.gzdefine.huangcuangoa.util.DateUtil;
import com.gzdefine.huangcuangoa.util.Ok;
import com.gzdefine.huangcuangoa.util.ToastUtil;
import com.gzdefine.huangcuangoa.view.TitleMenu.ActionItem;
import com.gzdefine.huangcuangoa.view.TitleMenu.TitlePopup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class MeetingInfoActivity extends BaseActivity {
    public static final int REQUEST_CODE = 'm' + 'e' + 'e' + 't' + 'i' + 'n' + 'g' + 'i' + 'n' + 'f' + 'o';
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case 0:
                        JSONObject jsonObject = new JSONObject((String) msg.obj);
                        String success = jsonObject.getString("success");
                        if (success.equals("true")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("paramList");
                            if (jsonArray.length() > 0) {
                                JSONObject date = jsonArray.getJSONObject(0);
                                title.setText("" + date.getString("title"));
                                start_time.setText("开始时间：" + DateUtil.timeForMeeting(date.getString("startTime")));
                                end_time.setText("结束时间：" + DateUtil.timeForMeeting(date.getString("endTime")));
                                palce.setText("地点：" + date.getString("address"));
                                fqr.setText("发起人：" + date.getString("fqr"));
                                lxr.setText("联系人：" + date.getString("lxr"));
                                content.setText("会议内容：" + date.getString("content"));
                                if ("0".equals(date.getString("status"))) {
                                    img_right.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            String message = jsonObject.getString("msg");
                            ToastUtil.showShort(MeetingInfoActivity.this, "" + message);
                        }

                        break;
                    case 1://删除会议
                        JSONObject jsonObject2 = new JSONObject((String) msg.obj);
                        String message2 = jsonObject2.getString("msg");
                        ToastUtil.showShort(MeetingInfoActivity.this, "" + message2);
                        finish();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    });
    MeettingControl meettingControl;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.start_time)
    TextView start_time;
    @Bind(R.id.end_time)
    TextView end_time;
    @Bind(R.id.palce)
    TextView palce;
    @Bind(R.id.fqr)
    TextView fqr;
    @Bind(R.id.lxr)
    TextView lxr;

    @Bind(R.id.content)
    TextView content;

    private String confId;

    @Override
    public int initContentID() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_info);
        ButterKnife.bind(this);
        setAppTitle("会议详情");
        App.me().clearmsgcount("1");
        meettingControl = new MeettingControl(this);
        confId = getIntent().getStringExtra("id");
        String userId = App.me().login().getUserId();
        if (confId != null && userId != null) {
            meettingControl.getMeetingInfo(confId, userId);
        }
        initPopWindow();
    }

    private TitlePopup titlePopup;

    @OnClick(R.id.img_back)
    void back() {

        finish();


    }

    @Bind(R.id.img_right)
    ImageView img_right;

    @OnClick(R.id.img_right)
    void add() {
        titlePopup.show(findViewById(R.id.layout_bar));
    }

    private void initPopWindow() {

        Glide.with(this).load(R.mipmap.icon_more).into(img_right);
        // 实例化标题栏弹窗
        titlePopup = new TitlePopup(this, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        titlePopup.setItemOnClickListener(onitemClick);
        // 给标题栏弹窗添加子类
        titlePopup.addAction(new ActionItem(this, "修改会议"));
        titlePopup.addAction(new ActionItem(this, "删除会议"));
    }

    private TitlePopup.OnItemOnClickListener onitemClick = new TitlePopup.OnItemOnClickListener() {
        @Override
        public void onItemClick(ActionItem item, int position) {
            // mLoadingDialog.show();
            switch (position) {
                case 0:// 修改日历
                    Intent intent = new Intent(MeetingInfoActivity.this, EditMeetingActivity.class);
                    intent.putExtra("title", "" + title.getText().toString());
                    intent.putExtra("start_time", "" + start_time.getText().toString());
                    intent.putExtra("end_time", "" + end_time.getText().toString());
                    intent.putExtra("palce", "" + palce.getText().toString());
                    intent.putExtra("fqr", "" + fqr.getText().toString());
                    intent.putExtra("lxr", "" + lxr.getText().toString());
                    intent.putExtra("content", "" + content.getText().toString());
                    intent.putExtra("confId", "" + confId);
                    startActivity(intent);
                    break;
                case 1:// 删除日历
                    meettingControl.DeleteMeetingInfo(confId);
                    break;
                default:
                    break;
            }
        }
    };

    class MeettingControl extends Ok {
        int what;

        public MeettingControl(Context context) {
            super(context);
        }

        //userId  用户id
//theDay   当前日期（yyyy-MM-dd）
        public void getMeetingInfo(String confId, String userId) {
            what = 0;
            super.post(Constant.DOMAIN + "mobile/confernceMobile/confernceDetails.do", "confId", confId, "userId", userId);
        }

        public void DeleteMeetingInfo(String conId) {
            what = 1;
            super.post(Constant.DOMAIN + "mobile/confernceMobile/deleteConfernce.do", "conId", conId);
        }

        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String re = response.body().string();
            Log.d("reg", "result:" + re);
            App.me().checklogin(MeetingInfoActivity.this, re);
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
