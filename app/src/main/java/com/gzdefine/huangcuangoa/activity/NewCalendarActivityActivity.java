package com.gzdefine.huangcuangoa.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.gzdefine.huangcuangoa.App;
import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.util.Constant;
import com.gzdefine.huangcuangoa.util.KeyBoardUtils;
import com.gzdefine.huangcuangoa.util.Ok;
import com.gzdefine.huangcuangoa.util.StringKit;
import com.gzdefine.huangcuangoa.util.ToastUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class NewCalendarActivityActivity extends BaseActivity {

    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                JSONObject jsonObject=new JSONObject((String) msg.obj);
                String message=jsonObject.getString("msg");
                Boolean success=jsonObject.getBoolean("success");
                ToastUtil.showShort(NewCalendarActivityActivity.this,""+message);
                if (success){
                    finish();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return false;
        }
    });
    @Bind(R.id.start_time)
    Button start_time;
    @Bind(R.id.end_time)
    Button end_time;
    @Bind(R.id.status)
    Button status;
    @Bind(R.id.content)
    EditText content;
    @Bind(R.id.title)
    EditText title;

    CalendarControl calendarControl;
    private static final List<String> options1Items = new ArrayList<>();//条件选择器中的item;

    @Override
    public int initContentID() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_calendar_activity);
        ButterKnife.bind(this);
        setAppTitle("新建日程");
        initOptions();
    }

    private void initOptions() {
        options1Items.add("未开始");
        options1Items.add("执行中");
        options1Items.add("延迟");
        options1Items.add("暂停");
        options1Items.add("中止");
        options1Items.add("完成");
        calendarControl=new CalendarControl(this);
    }

    @OnClick(R.id.img_back)
    void back() {
        finish();
    }

    @OnClick(R.id.start_time)
    void start_time() {
        HideINPUT();
        TimePickerView pvTime = new TimePickerView.Builder(NewCalendarActivityActivity.this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date2, View v) {//选中事件回调
                String time = getTime(date2);
                start_time.setText(time);
            }
        })
                .setType(TimePickerView.Type.YEAR_MONTH_DAY_HOUR_MIN)//默认全部显示
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .setContentSize(20)//滚轮文字大小
                .setTitleSize(20)//标题文字大小
//                        .setTitleText("请选择时间")//标题文字
                .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true)//是否循环滚动
                .setTextColorCenter(Color.BLACK)//设置选中项的颜色
                .setTitleColor(Color.BLACK)//标题文字颜色
                .setSubmitColor(Color.BLUE)//确定按钮文字颜色
                .setCancelColor(Color.BLUE)//取消按钮文字颜色
//                        .setTitleBgColor(0xFF666666)//标题背景颜色 Night mode
//                        .setBgColor(0xFF333333)//滚轮背景颜色 Night mode
//                        .setRange(calendar.get(Calendar.YEAR) - 20, calendar.get(Calendar.YEAR) + 20)//默认是1900-2100年
//                        .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
//                        .setRangDate(startDate,endDate)//起始终止年月日设定
//                        .setLabel("年","月","日","时","分","秒")
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
//                        .isDialog(true)//是否显示为对话框样式
                .build();
        pvTime.setDate(Calendar.getInstance());//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
        pvTime.show();
    }

    @OnClick(R.id.end_time)
    void end_time() {
        HideINPUT();
        TimePickerView pvTime = new TimePickerView.Builder(NewCalendarActivityActivity.this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date2, View v) {//选中事件回调
                String time = getTime(date2);
                end_time.setText(time);
            }
        })
                .setType(TimePickerView.Type.YEAR_MONTH_DAY_HOUR_MIN)//默认全部显示
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .setContentSize(20)//滚轮文字大小
                .setTitleSize(20)//标题文字大小
//                        .setTitleText("请选择时间")//标题文字
                .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true)//是否循环滚动
                .setTextColorCenter(Color.BLACK)//设置选中项的颜色
                .setTitleColor(Color.BLACK)//标题文字颜色
                .setSubmitColor(Color.BLUE)//确定按钮文字颜色
                .setCancelColor(Color.BLUE)//取消按钮文字颜色
//                        .setTitleBgColor(0xFF666666)//标题背景颜色 Night mode
//                        .setBgColor(0xFF333333)//滚轮背景颜色 Night mode
//                        .setRange(calendar.get(Calendar.YEAR) - 20, calendar.get(Calendar.YEAR) + 20)//默认是1900-2100年
//                        .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
//                        .setRangDate(startDate,endDate)//起始终止年月日设定
//                        .setLabel("年","月","日","时","分","秒")
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
//                        .isDialog(true)//是否显示为对话框样式
                .build();
        pvTime.setDate(Calendar.getInstance());//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
        pvTime.show();
    }

    @OnClick(R.id.status)
    void setStart_time() {
        HideINPUT();
        OptionsPickerView pvOptions = new OptionsPickerView.Builder(NewCalendarActivityActivity.this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String s = options1Items.get(options1);
                status.setText(s);

            }
        })
//                        .setSubmitText("确定")//确定按钮文字
//                        .setCancelText("取消")//取消按钮文字
//                        .setTitleText("城市选择")//标题
                .setSubCalSize(20)//确定和取消文字大小
//                        .setTitleSize(20)//标题文字大小
//                        .setTitleColor(Color.BLACK)//标题文字颜色
                .setSubmitColor(Color.BLUE)//确定按钮文字颜色
                .setCancelColor(Color.BLUE)//取消按钮文字颜色
//                        .setTitleBgColor(0xFF333333)//标题背景颜色 Night mode
//                        .setBgColor(0xFF000000)//滚轮背景颜色 Night mode
//                        .setContentTextSize(18)//滚轮文字大小
//                        .setTextColorCenter(Color.BLUE)//设置选中项的颜色
                .setTextColorCenter(Color.BLACK)//设置选中项的颜色
//                        .setLineSpacingMultiplier(1.6f)//设置两横线之间的间隔倍数
//                        .setLinkage(false)//设置是否联动，默认true
//                        .setLabels("省", "市", "区")//设置选择的三级单位
//                        .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
//                        .setCyclic(false, false, false)//循环与否
//                        .setSelectOptions(1, 1, 1)  //设置默认选中项
//                        .setOutSideCancelable(false)//点击外部dismiss default true
//                        .isDialog(true)//是否显示为对话框样式
                .build();
        pvOptions.setPicker(options1Items);
        pvOptions.show();
    }
private void HideINPUT(){
    KeyBoardUtils.closeKeybord(title,this);
}

    public String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }


    @OnClick(R.id.update_userinfo)
    void update_userinfo(){
        String userId=App.me().login().getUserId();
        String stime=start_time.getText().toString();
        String etime=end_time.getText().toString();
        String statusS=status.getText().toString();
        String titleS=title.getText().toString();
        String contentS=content.getText().toString();
        if (userId==null){
            ToastUtil.showShort(this,"userId有误，请联系后台人员");
            return;
        }
        if (StringKit.isEmpty(stime)){
            ToastUtil.showShort(this,"请选择开始时间");
            return;
        }
        if (StringKit.isEmpty(etime)){
            ToastUtil.showShort(this,"请选择结束时间");
            return;
        }
        if (StringKit.isEmpty(statusS)){
            ToastUtil.showShort(this,"请选择状态");
            return;
        }
        if (StringKit.isEmpty(titleS)){
            ToastUtil.showShort(this,"请输入标题");
            return;
        }
        if (StringKit.isEmpty(contentS)){
            ToastUtil.showShort(this,"请输入内容");
            return;
        }
        calendarControl.addCalendarInfo(userId,contentS,etime,stime,statusS,titleS);
    }
    class CalendarControl extends Ok {
        public CalendarControl(Context context) {
            super(context);
        }

        public void addCalendarInfo(String userId,String content, String endTime, String startTime, String status, String title) {
            super.post(Constant.DOMAIN + "mobile/confernceMobile/AppPlanTaskAdd.do", "userId", userId, "content", content,
                    "endTime", endTime, "startTime", startTime, "status", status,"title",title);
        }



        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String re = response.body().string();
            Log.d("reg", "result:" + re);
            App.me().checklogin(NewCalendarActivityActivity.this, re);
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
