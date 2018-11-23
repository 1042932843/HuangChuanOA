package com.gzdefine.huangcuangoa.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gzdefine.huangcuangoa.App;
import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.adapter.EventAdapter;
import com.gzdefine.huangcuangoa.entity.Event;
import com.gzdefine.huangcuangoa.util.Constant;
import com.gzdefine.huangcuangoa.util.DateUtil;
import com.gzdefine.huangcuangoa.util.Ok;
import com.gzdefine.huangcuangoa.util.ToastUtil;
import com.necer.ncalendar.calendar.MonthCalendar;
import com.necer.ncalendar.listener.OnClickMonthCalendarListener;
import com.necer.ncalendar.listener.OnMonthCalendarPageChangeListener;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;


/**
 * Created by necer on 2017/6/15.
 */

public class MeetingActivity extends BaseActivity implements View.OnClickListener {


    private TextView tv_today;
    private ImageView iv_finish;
    private ListView lv_eventlist;
    private List<Event> list;
    private TextView tv_title;
    private TextView data;
    private TextView text;
    private MonthCalendar monthCalendar;
    private EventAdapter adapter;
    MeettingControl meettingControl;
    List<String> flagList = new ArrayList<>();
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case 0:
                        meettingControl.whetherAddConfernce(App.me().login().getUserId());
                        String re = (String) msg.obj;
                        JSONObject jsonObject = new JSONObject(re);
                        JSONArray jsonArray = jsonObject.getJSONArray("paramList");
                        list.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject entiy = jsonArray.getJSONObject(i);
                            Event event = new Event();
                            event.setConfId(entiy.getString("confId"));
                            event.setStartTime(entiy.getString("startTime"));
                            event.setEndTime(entiy.getString("endTime"));
                            event.setTitle(entiy.getString("title"));
                            list.add(event);
                        }
                        adapter.updateListView(list);
                        break;
                    case 1:
                        meettingControl.getFlagMeeting(App.me().login().getUserId(), "2017-09-12", "2025-10-10");
                        String re2 = (String) msg.obj;
                        JSONObject jsonObject2 = new JSONObject(re2);
                        Boolean isAdmin = jsonObject2.getBoolean("success");
                        showAddTextView(isAdmin);
                        break;
                    case 2:
                        String re3 = (String) msg.obj;
                        JSONObject jsonObject3 = new JSONObject(re3);
                        String success = jsonObject3.getString("success");
                        if (success.equals("true")) {
                            JSONArray jsonArray2 = jsonObject3.getJSONArray("list");
                            if (jsonArray2.length() == 0) {
                                text.setVisibility(View.VISIBLE);
                            }else {
                                text.setVisibility(View.GONE);
                            }
                            for (int i = 0; i < jsonArray2.length(); i++) {
                                JSONObject object = jsonArray2.getJSONObject(i);
                                String time = object.getString("Time");
                                flagList.add(time);
                            }
                            monthCalendar.setPointList(flagList);
                        } else {
                            String message = jsonObject3.getString("msg");
                            ToastUtil.showShort(MeetingActivity.this, message);
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }


    });

    private void showAddTextView(Boolean isAdmin) {
        if (isAdmin) {
            tv_today.setVisibility(View.VISIBLE);
        } else {
            tv_today.setVisibility(View.GONE);
        }


    }

    @Override
    public int initContentID() {
        return 0;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);
        setAppTitle("会议");
        App.me().clearmsgcount("1");
        tv_today = (TextView) findViewById(R.id.txt_right);
        tv_today.setText("+");
        tv_today.setTextSize(34);
        tv_today.setVisibility(View.VISIBLE);
        tv_title = (TextView) findViewById(R.id.txt_title);
        data = (TextView) findViewById(R.id.data);
        text = (TextView) findViewById(R.id.text);
        iv_finish = (ImageView) findViewById(R.id.img_back);
        monthCalendar = (MonthCalendar) findViewById(R.id.monthCalendar);
        lv_eventlist = (ListView) findViewById(R.id.lv_eventlist);
        initListView();
        tv_today.setOnClickListener(this);
        iv_finish.setOnClickListener(this);


        monthCalendar.setOnClickMonthCalendarListener(new OnClickMonthCalendarListener() {
            @Override
            public void onClickMonthCalendar(DateTime dateTime) {
                meettingControl.getMeeting(App.me().login().getUserId(), "" + dateTime.toLocalDate());
            }
        });

        monthCalendar.setOnMonthCalendarPageChangeListener(new OnMonthCalendarPageChangeListener() {
            @Override
            public void onMonthCalendarPageSelected(DateTime dateTime) {
                data.setText(dateTime.getYear() + "年" + dateTime.getMonthOfYear() + "月");
            }
        });


    }

    private void initListView() {
        meettingControl = new MeettingControl(this);//初始化http请求
        meettingControl.getMeeting(App.me().login().getUserId(), "" + DateUtil.getCurrentTime_Today());

        list = new ArrayList<>();
        adapter = new EventAdapter(this, list);
        lv_eventlist.setAdapter(adapter);
        lv_eventlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MeetingActivity.this, MeetingInfoActivity.class);
                intent.putExtra("id", list.get(position).getConfId());
                startActivity(intent);
//                monthCalendar.setDate(list.get(position).getYear(), list.get(position).getMouth(), list.get(position).getDay(), true);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_right:
                startActivity(new Intent(this, NewMeetingActivity.class));
                break;
            case R.id.img_back:
                finish();
                break;
        }
    }

    class MeettingControl extends Ok {
        int what;

        public MeettingControl(Context context) {
            super(context);
        }

        //userId  用户id
//theDay   当前日期（yyyy-MM-dd）
        public void getMeeting(String userId, String theDay) {
            what = 0;
            super.post(Constant.DOMAIN + "mobile/confernceMobile/confernceTheDay.do", "userId", userId, "theDay", theDay);
        }

        //userId  用户id
//theDay   当前日期（yyyy-MM-dd）
        public void whetherAddConfernce(String userId) {
            what = 1;
            super.post(Constant.DOMAIN + "mobile/confernceMobile/whetherAddConfernce.do", "userId", userId);
        }

        //userId  用户id
//theDay   当前日期（yyyy-MM-dd）
        public void getFlagMeeting(String userId, String theStart, String theEnd) {
            what = 2;
            super.post(Constant.DOMAIN + "mobile/confernceMobile/monthConf.do", "userId", userId, "theStart", theStart, "theEnd", theEnd);
        }

        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String re = response.body().string();
            Log.d("reg", "result:" + re);
            App.me().checklogin(MeetingActivity.this, re);
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
