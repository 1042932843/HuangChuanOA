/*
 * Copyright © 2015 珠海云集软件科技有限公司.
 * Website：http://www.YunJi123.com
 * Mail：dev@yunji123.com
 * Tel：+86-0756-8605060
 * QQ：340022641(dove)
 * Author：dove
 */

package com.gzdefine.huangcuangoa;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import java.util.Calendar;

/**
 * 日期时间段对话框, 用于选择护士预约时间
 */
public class DateTimePicker extends AlertDialog implements DialogInterface.OnClickListener, DatePicker.OnDateChangedListener, RadioGroup.OnCheckedChangeListener {

    // 选择日期时间段点击确定事件监听器
    private final OnDateSetListener listener;
    private DatePicker datePicker; // 日期选择器
//    private RadioButton morning; // 上午
//    private RadioButton afternoon; // 下午
//    private RadioButton evening; // 晚上

    private int i;
    private int ti;


    private Calendar mDate;

    private NumberPicker Time;
    private EditText Time2;
    private boolean selected;
    private RelativeLayout Time2_lay;
    private int in;

    public DateTimePicker(Context context, OnDateSetListener listener, int in) {
        super(context);
        this.listener = listener;
        this.in = in;
        View view;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            view = View.inflate(getContext(), R.layout.dialog_time_picker, null);
        } else {
            view = View.inflate(getContext(), R.layout.dialog_time_picker2, null);
        }

        setView(view);
        assignViews(view);
        initViews();
    }

    private void assignViews(View root) {
        datePicker = (DatePicker) root.findViewById(R.id.datePicker);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Time = (NumberPicker) root.findViewById(R.id.Time);
                Time.setMinValue(8);
                Time.setMaxValue(20);
                Time.setFocusable(true);
                Time.setFocusableInTouchMode(true);
                Time.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                        setTimeStr();
                    }

                });
            if (in == 1) {
                Time.setVisibility(View.GONE);
            } else {
                Time.setVisibility(View.VISIBLE);
            }

        } else {
            Time2 = (EditText) root.findViewById(R.id.Time2);
            Time2_lay = (RelativeLayout) root.findViewById(R.id.Time2_lay);
            if (in == 1) {
                Time2.setVisibility(View.GONE);
                Time2_lay.setVisibility(View.GONE);
            } else {
                Time2.setVisibility(View.VISIBLE);
                Time2_lay.setVisibility(View.VISIBLE);
            }
        }

//        morning = (RadioButton) root.findViewById(R.id.morning);
//        afternoon = (RadioButton) root.findViewById(R.id.afternoon);
//        evening = (RadioButton) root.findViewById(R.id.evening);


    }

//    private NumberPicker.OnValueChangeListener mOnHourChangedListener =

    private void setTimeStr() {
        //获取当前输入的时间
        if (null != Time) {
            ti = Time.getValue();
        } else {
            ti = Integer.parseInt(Time2.getText().toString());
        }
        //获取适配器内的时间
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, datePicker.getYear());
        calendar.set(Calendar.MONTH, datePicker.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Calendar calendar3 = Calendar.getInstance();
        calendar3.set(Calendar.HOUR_OF_DAY, 0);
        calendar3.set(Calendar.MINUTE, 0);
        calendar3.set(Calendar.SECOND, 0);
        calendar3.set(Calendar.MILLISECOND, 0);

        //判断适配器内时间与适配器最小时间是否匹配  同时  当前适配器内时间与当前系统时间是否匹配
        if ((datePicker.getMinDate() == calendar.getTimeInMillis()) && calendar.getTimeInMillis() == calendar3.getTimeInMillis()) {
            //如果匹配则获取当前时间
            long time = System.currentTimeMillis();
            Calendar mCalendar2 = Calendar.getInstance();
            mCalendar2.setTimeInMillis(time);
            int h;
            h = mCalendar2.get(Calendar.HOUR_OF_DAY);
            //如果当前时间大于输入的时间,则把输入值更换成当前时间
            if (h > ti) {
                if (null != Time) {
                    Time.setValue(h);
                } else {
                    Time2.setText(h + "");
                }
                ti = h;
            }
            //如果当前时间小于输入时间,则不做处理
        }
        //判断适配器内时间与适配器最小时间不匹配,则不做处理
    if (in==1){
        setTitle(String.format("%s年%s月%s日", datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth()));
    }else {
        setTitle(String.format("%s年%s月%s日 %s:00", datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth(), ti));
    }

    }

    private void initViews() {
//        setTitle("请选择预约时间");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        // 初始化当前年月日

        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), this);

        if (Build.VERSION.SDK_INT >= 11) {
            // 设置最小可选日期为今天
            long time = System.currentTimeMillis();
            Calendar mCalendar2 = Calendar.getInstance();
            mCalendar2.setTimeInMillis(time);
            if (in!=1){
                int h;
                h = mCalendar2.get(Calendar.HOUR_OF_DAY);//提前3个小时
                datePicker.setMinDate(calendar.getTimeInMillis());
                datePicker.setMaxDate(calendar.getTimeInMillis() + 15552000000L);
                if (datePicker.getMinDate() == calendar.getTimeInMillis()) {
                    if (h > ti) {
                        Time.setValue(h);
                    }
                }
            }
        }
        setButton(BUTTON_POSITIVE, "确定", this);
        setButton(BUTTON_NEGATIVE, "取消", this);
        initTimes(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    // 重置时间段按钮 初始化和选择(改变)日期时调用
    private void initTimes(int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
       /* // 所选日期是当天
        if (calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.MONTH) == monthOfYear && calendar.get(Calendar.DAY_OF_MONTH) == dayOfMonth) {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            morning.setEnabled(hour < 12); // 判断是否允许选择早上
            afternoon.setEnabled(hour < 18); // 判断是否允许选择下午
            evening.setEnabled(hour < 24); // 判断是否允许选择晚上
            if (!morning.isEnabled() && morning.isChecked()) {
                afternoon.setChecked(true); // 如果早上被禁用并且被选中 则改为选中下午
            } else if (!afternoon.isEnabled() && afternoon.isChecked()) {
                evening.setChecked(true); // 如果下午被禁用并且被选择 则改为选中晚上
            } else if (!evening.isEnabled() && evening.isChecked()) {
                morning.setChecked(true);
            }
        } else { // 所选日期不是当天
            morning.setEnabled(true);
            afternoon.setEnabled(true);
            evening.setEnabled(true);
        }*/
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (in==1){
            if (which == BUTTON_POSITIVE && listener != null) { // 点击确定按钮
                String t = gett();
                if (t.length() > 0) {
                    // 通知外部事件监听
                    listener.onDateSet(this, datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), gettime(), gett());
                } else {
                    App.me().toast("请输入时间");
                }
            }else if (which == BUTTON_NEGATIVE && listener != null){
                    listener.onDateSet(this, 0,0, 0, 0, gett());

            }
        }else {
            if (which == BUTTON_POSITIVE && listener != null) { // 点击确定按钮
                String t = gett();
                if (t.length() > 0) {
                    // 通知外部事件监听
                    listener.onDateSet(this, datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), gettime(), gett());
                } else {
                    App.me().toast("请输入时间");
                }
            }
        }

    }

    private String gett() {
        i = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            i = Time.getValue();
            if (i < 10) {
                return "0" + i + ":00";
            } else {
                return i + ":00";
            }
        } else {
            String str = Time2.getText().toString();
            if (null != str && str.length() > 0) {
                i = Integer.parseInt(str);
                if (i < 10) {
                    return "0" + i + ":00";
                } else {
                    return i + ":00";
                }
            } else {
                return "";
            }
        }
    }

    private int gettime() {
        int i = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            i = Time.getValue();
            if (i <= 12) {
                return 0;
            } else if (i < 18) {
                return 1;
            } else {
                return 2;
            }
        } else {
            String str = Time2.getText().toString();
            if (null != str && str.length() > 0) {
                i = Integer.parseInt(str);
                if (i <= 12) {
                    return 0;
                } else if (i < 18) {
                    return 1;
                } else {
                    return 2;
                }
            } else {
                return 0;
            }
        }
    }

    private String getdate() {
        int i = gettime();
        switch (i) {
            case 0:
                return "上午";
            case 1:
                return "下午";
            case 2:
                return "晚上";
            default:
                return "";
        }
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if (!selected) {
            selected = true; // 忽略初始化时引发的事件通知
        } else {
            // 设置标题文本为选中的日期和时间段
            setTimeStr();
        }

        // 重置时间段单选按钮组
        initTimes(year, monthOfYear, dayOfMonth);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) { // 时间段选择事件

        onDateChanged(datePicker, datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());

    }


    // 外部事件监听 当选择日期时间段并且点击确定按钮时
    public interface OnDateSetListener {
        void onDateSet(DateTimePicker dialog, int year, int monthOfYear, int dayOfMonth, int timeOfIndex, String time);
    }


}
