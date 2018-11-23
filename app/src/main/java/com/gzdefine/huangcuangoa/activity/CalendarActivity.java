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

import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;


/**
 * Created by necer on 2017/6/15.
 */

public class CalendarActivity extends BaseActivity implements View.OnClickListener {


    private TextView tv_today;
    private ImageView iv_finish;
    private ListView lv_eventlist;
    private List<Event> list;
    private TextView tv_title;
    private MonthCalendar monthCalendar;
    private EventAdapter adapter;
    CalendarControl calendarControl;
    private TextView text;
    List<String> flagList = new ArrayList<>();
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case 0:
                        calendarControl.getFlagCalendar(App.me().login().getUserId(), "2017-09-12", "2025-10-10");
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
                            event.setConfId(entiy.getString("planId"));
                            event.setStartTime(entiy.getString("pstartTime"));
                            event.setEndTime(entiy.getString("pendTime"));
                            event.setTitle(entiy.getString("subject"));
                            list.add(event);
                        }
                        adapter.updateListView(list);
                        break;
                    case 1:
                        String re3 = (String) msg.obj;
                        JSONObject jsonObject3 = new JSONObject(re3);
                        String success = jsonObject3.getString("success");
                        if (success.equals("true")) {
                            JSONObject jsonObject4 = jsonObject3.getJSONObject("list");
                            JSONArray jsonArray3 = jsonObject4.getJSONArray("paramList");
                            if (jsonArray3.length() == 0) {
                                text.setVisibility(View.VISIBLE);
                            }else {
                                text.setVisibility(View.GONE);
                            }
                            for (int i = 0; i < jsonArray3.length(); i++) {
                                JSONObject object = jsonArray3.getJSONObject(i);
                                String time = object.getString("pstartTime");
                                flagList.add(time);
                            }
                            monthCalendar.setPointList(flagList);
                        }else {
                            String Msg = jsonObject3.getString("msg");
                            ToastUtil.showShort(CalendarActivity.this, Msg);
                        }

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        setAppTitle("日程");
        App.me().clearmsgcount("2");
        tv_today = (TextView) findViewById(R.id.txt_right);
        text = (TextView) findViewById(R.id.text);
        tv_today.setText("+");
        tv_today.setTextSize(34);
        tv_today.setVisibility(View.VISIBLE);
        tv_title = (TextView) findViewById(R.id.txt_title);
        iv_finish = (ImageView) findViewById(R.id.img_back);
        monthCalendar = (MonthCalendar) findViewById(R.id.monthCalendar);
        lv_eventlist = (ListView) findViewById(R.id.lv_eventlist);
        initListView();

        tv_today.setOnClickListener(this);
        iv_finish.setOnClickListener(this);


        monthCalendar.setOnClickMonthCalendarListener(new OnClickMonthCalendarListener() {
            @Override
            public void onClickMonthCalendar(DateTime dateTime) {
                calendarControl.getCalendar(App.me().login().getUserId(), "" + dateTime.toLocalDate());
            }
        });

        monthCalendar.setOnMonthCalendarPageChangeListener(new OnMonthCalendarPageChangeListener() {
            @Override
            public void onMonthCalendarPageSelected(DateTime dateTime) {
                tv_title.setText(dateTime.getYear() + "年" + dateTime.getMonthOfYear() + "月");
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        calendarControl.getCalendar(App.me().login().getUserId(), "" + DateUtil.getCurrentTime_Today());
    }

    private void initListView() {
        calendarControl = new CalendarControl(this);//初始化http请求

        list = new ArrayList<>();
        adapter = new EventAdapter(this, list);
        lv_eventlist.setAdapter(adapter);
        lv_eventlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CalendarActivity.this, CalendarInfoActivity.class);
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
                startActivity(new Intent(this, NewCalendarActivityActivity.class));
                break;
            case R.id.img_back:
                finish();
                break;
        }
    }

    @OnClick(R.id.img_back)
    void back() {
        finish();
    }

    class CalendarControl extends Ok {
        int what;

        public CalendarControl(Context context) {
            super(context);
        }

        //userId  用户id
       //theDay   当前日期（yyyy-MM-dd）
        public void getCalendar(String userId, String theDay) {
            what = 0;
            super.post(Constant.DOMAIN + "mobile/confernceMobile/planTaskList.do", "userId", userId, "theDay", theDay);
        }

        //userId  用户id
       //theDay   当前日期（yyyy-MM-dd）
        public void getFlagCalendar(String userId, String theStart, String theEnd) {
            what = 1;
            super.post(Constant.DOMAIN + "mobile/confernceMobile/monthPlanTask.do", "userId", userId, "theStart", theStart, "theEnd", theEnd);
        }


        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String re = response.body().string();
            Log.d("reg", "result:" + re);
            App.me().checklogin(CalendarActivity.this, re);
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
