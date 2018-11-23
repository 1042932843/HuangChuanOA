package com.gzdefine.huangcuangoa.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.gzdefine.huangcuangoa.App;
import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.adapter.EventAdapter;
import com.gzdefine.huangcuangoa.entity.Event;
import com.gzdefine.huangcuangoa.util.Constant;
import com.gzdefine.huangcuangoa.util.Ok;

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


public class MeetingNoneActivity extends BaseActivity {


    @Bind(R.id.lv_eventlist)
    ListView lv_eventlist;
    private List<Event> list;
    private TextView text;
    private EventAdapter adapter;
    MeettingControl meettingControl;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case 0:
                        String re = (String) msg.obj;
                        JSONObject jsonObject = new JSONObject(re);
                        JSONArray jsonArray = jsonObject.getJSONArray("paramList");
                        if (jsonArray.length() == 0) {
                            text.setVisibility(View.VISIBLE);
                        }else {
                            text.setVisibility(View.GONE);
                        }
                        list.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject entiy = jsonArray.getJSONObject(i);
                            Event event = new Event();
                            event.setConfId(entiy.getString("conId"));
                            event.setStartTime(entiy.getString("startTime"));
                            event.setEndTime(entiy.getString("endTime"));
                            event.setTitle(entiy.getString("title"));
                            list.add(event);
                        }
                        adapter.updateListView(list);
                        break;
                }
            } catch (Exception e) {
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
        setContentView(R.layout.activity_metting_none);
        text = (TextView) findViewById(R.id.text);
        ButterKnife.bind(this);
        initListView();
        setAppTitle("会议");
    }

    @Override
    protected void onResume() {
        super.onResume();
        meettingControl.getMeeting(App.me().login().getUserId());
    }

    @OnClick(R.id.img_back)
    void back() {
        finish();
    }

    private void initListView() {
        meettingControl = new MeettingControl(this);
        //初始化http请求
        meettingControl.getMeeting(App.me().login().getUserId());

        list = new ArrayList<>();
        adapter = new EventAdapter(this, list);
        lv_eventlist.setAdapter(adapter);
        lv_eventlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MeetingNoneActivity.this, MeetingInfoActivity.class);
                intent.putExtra("id", list.get(position).getConfId());
                startActivity(intent);
//                monthCalendar.setDate(list.get(position).getYear(), list.get(position).getMouth(), list.get(position).getDay(), true);
            }
        });
    }


    class MeettingControl extends Ok {
        int what;

        public MeettingControl(Context context) {
            super(context);
        }

        //userId  用户id
        public void getMeeting(String userId) {
            what = 0;
            super.post(Constant.DOMAIN + "mobile/confernceMobile/NotRelease.do", "userId", userId);
        }


        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String re = response.body().string();
            Log.d("reg", "result:" + re);
            App.me().checklogin(MeetingNoneActivity.this, re);
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
