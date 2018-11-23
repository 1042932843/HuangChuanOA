package com.gzdefine.huangcuangoa.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
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


public class CalendarInfoActivity extends BaseActivity {
    public static final int REQUEST_CODE = 'c' + 'a' + 'l' + 'e' + 'n' + 'd' + 'a' + 'r' + 'n' + 'f' + 'o';
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case 0:
                        JSONObject jsonObject = new JSONObject((String) msg.obj);
                        String success = jsonObject.getString("success");
                        JSONArray jsonArray = jsonObject.getJSONArray("paramList");
                        if (success.equals("true")) {
                            if (jsonArray.length() > 0) {
                                JSONObject date = jsonArray.getJSONObject(0);
                                title.setText("" + date.getString("subject"));
                                start_time.setText("开始时间：" + DateUtil.timedate(date.getString("pstartTime")));
                                end_time.setText("结束时间：" + DateUtil.timedate(date.getString("pendTime")));
                                status.setText("状态：" + date.getString("status"));
                                content.setText("" + date.getString("content"));
                            }
                        } else {
                            String message = jsonObject.getString("msg");
                            ToastUtil.showShort(CalendarInfoActivity.this, "" + message);
                        }


                        break;
                    case 1://删除日程
                        JSONObject jsonObject2 = new JSONObject((String) msg.obj);
                        String message2 = jsonObject2.getString("msg");
                        ToastUtil.showShort(CalendarInfoActivity.this, "" + message2);
                        finish();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    });
    private AlertDialog dialog;
    private TitlePopup titlePopup;
    CalendarControl meettingControl;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.start_time)
    TextView start_time;
    @Bind(R.id.end_time)
    TextView end_time;
    @Bind(R.id.status)
    TextView status;
    @Bind(R.id.content)
    TextView content;
    String confId;

    @Override
    public int initContentID() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_info);
        ButterKnife.bind(this);
        App.me().clearmsgcount("2");
        setAppTitle("日程详情");
        initPopWindow();
        initControl();
    }


    private void initControl() {
        meettingControl = new CalendarControl(this);
        confId = getIntent().getStringExtra("id");
        Log.d("reg", "日程详情Id" + confId);
        String userId = App.me().login().getUserId();
        if (confId != null && userId != null) {
            meettingControl.getCalendarInfo(confId, userId);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String userId = App.me().login().getUserId();
        if (confId != null && userId != null) {
            meettingControl.getCalendarInfo(confId, userId);
        }
    }

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
        img_right.setVisibility(View.VISIBLE);
        Glide.with(this).load(R.mipmap.icon_more).into(img_right);
        // 实例化标题栏弹窗
        titlePopup = new TitlePopup(this, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        titlePopup.setItemOnClickListener(onitemClick);
        // 给标题栏弹窗添加子类
        titlePopup.addAction(new ActionItem(this, "修改日程"));
        titlePopup.addAction(new ActionItem(this, "删除日程"));
    }

    private TitlePopup.OnItemOnClickListener onitemClick = new TitlePopup.OnItemOnClickListener() {

        @Override
        public void onItemClick(ActionItem item, int position) {
            // mLoadingDialog.show();
            switch (position) {
                case 0:// 修改日历
                    Intent intent = new Intent(CalendarInfoActivity.this, EditCalendarActivity.class);
                    intent.putExtra("title", "" + title.getText().toString());
                    intent.putExtra("start_time", "" + start_time.getText().toString());
                    intent.putExtra("end_time", "" + end_time.getText().toString());
                    intent.putExtra("status", "" + status.getText().toString());
                    intent.putExtra("content", "" + content.getText().toString());
                    intent.putExtra("pkId", "" + confId);
                    startActivity(intent);
                    break;
                case 1:// 删除日历
                    showAlertDialog("是否确定删除日历?", confId);
                    break;
                default:
                    break;
            }
        }
    };

    public void showAlertDialog(final String s, final String User_name) {

        AlertDialog.Builder builder = new AlertDialog.Builder(CalendarInfoActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_normal_layo, null);
        TextView message = (TextView) view.findViewById(R.id.title);
        message.setText(s);
        TextView positiveButton = (TextView) view.findViewById(R.id.positiveButton);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView negativeButton = (TextView) view.findViewById(R.id.negativeButton);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meettingControl.DeleteCalendarInfo(confId);
                dialog.dismiss();
            }
        });
        builder.setView(view);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();


    }

    class CalendarControl extends Ok {
        int what;

        public CalendarControl(Context context) {
            super(context);
        }

        public void getCalendarInfo(String confId, String userId) {
            what = 0;
            super.post(Constant.DOMAIN + "mobile/confernceMobile/planTaskDetails.do", "planId", confId, "userId", userId);
        }


        public void DeleteCalendarInfo(String confId) {
            what = 1;
            super.post(Constant.DOMAIN + "mobile/confernceMobile/AppPlanTaskDel.do", "pkId", confId);
        }


        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String re = response.body().string();
            Log.d("reg", "result:" + re);
            App.me().checklogin(CalendarInfoActivity.this, re);
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
