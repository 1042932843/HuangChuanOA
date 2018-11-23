package com.gzdefine.huangcuangoa.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.entity.Attendance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AttendanceRecordAdapter extends BaseAdapter {
    private final Context context;
    private List<Attendance> list;

    public AttendanceRecordAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<Attendance>();
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Attendance getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class holder {
        private TextView userName;//用户名
        private TextView departName;//部门名称
        private TextView registerTime;//登记时间
        private TextView regMins;//迟到或早退分钟 正常上班时为0
        private TextView inOffFlag;//上下班标识 1=签到 2=签退
        private TextView signRemark;//up为早班 down为午班
        private TextView regFlag; //登记标识 1=正常（上班/下班） 2＝迟到 3=早退 4＝休息 5＝旷工 6=放假
        private LinearLayout regFlag_line;
        private LinearLayout line;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final holder holder;
        if (view == null) {
            view = View.inflate(context, R.layout.itme_list_attendancerecord, null);
            holder = new holder();
            holder.userName = (TextView) view.findViewById(R.id.userName);
            holder.departName = (TextView) view.findViewById(R.id.departName);
            holder.registerTime = (TextView) view.findViewById(R.id.registerTime);
            holder.regMins = (TextView) view.findViewById(R.id.regMins);
            holder.signRemark = (TextView) view.findViewById(R.id.signRemark);
            holder.regFlag = (TextView) view.findViewById(R.id.regFlag);
            holder.regFlag_line = (LinearLayout) view.findViewById(R.id.regFlag_line);
            holder.line = (LinearLayout) view.findViewById(R.id.line);
//            holder.inOffFlag = (TextView) view.findViewById(R.id.inOffFlag);
            view.setTag(holder);
        } else {
            holder = (holder) view.getTag();
        }

        final Attendance item = list.get(position);
        if (null != item) {
            if (null != item.getUserName()) {
                holder.userName.setText(item.getUserName());
            }
            if (null != item.getDepartName()) {
                holder.departName.setText(item.getDepartName());
            }


            if (null != item.getRegisterTime()) {
                if (item.getRegisterTime().endsWith("00:00:00")) {
                    holder.registerTime.setText("日期: " + item.getRegisterTime().replace("00:00:00",""));
                }else{
                    holder.registerTime.setText("时间: " + item.getRegisterTime());
                }
            }
            if (null != item.getSignRemark()) {
                if (item.getSignRemark().equals("up")) {
                    holder.signRemark.setText("早班");
                } else if (item.getSignRemark().equals("down")) {
                    holder.signRemark.setText("午班");
                }
            }
            if (item.getRegType() != null) {
                String[] aa = item.getRegType().split("：");
                holder.regFlag.setText(aa[0]);
                int color = Color.parseColor(aa[1]);
                holder.regFlag.setTextColor(color);
                shapeSolid(context, holder.regFlag, color);
            }
            if (item.getRegMins() != null && !item.getRegMins().equals("0")) {
                String str = "迟到(分钟)：<font color='#FF0000'>" + item.getRegMins() + "</font>";
                holder.regMins.setTextSize(14);
                holder.regMins.setText(Html.fromHtml(str));
            }

        }
        return view;


    }

    /**
     * 设置圆角的背景
     *
     * @param context 上下文
     * @param v       View
     */
//    int strokeColor;//边框颜色
//
    public void shapeSolid(Context context, View v, int strokeColor) {
        GradientDrawable gd = (GradientDrawable) v.getBackground();
        int strokeWidth = 1; // 1dp 边框宽度
        int roundRadius = 1; // 8dp 圆角半径
        int fillColor = 0xffffffff;//内部填充颜色
        gd.setColor(fillColor);
        gd.setCornerRadius(dp2px(context, roundRadius));
        gd.setStroke(dp2px(context, strokeWidth), strokeColor);
    }

    /**
     * 根据手机的分辨率dp 转成px(像素)
     */
    public int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    // 设置数据
    public void setData(List<Attendance> data) {
        this.list = data;
    }

    // 添加数据
    public void addData(Attendance bean) {
        // 下标 数据
        list.add(0, bean);
    }


    public void clear() {
        list.clear();
    }

    public boolean addAll(Collection<? extends Attendance> collection) {
        boolean pa = list.addAll(collection);
        return pa;
    }


    public boolean add(Attendance object) {
        return list.add(object);
    }

    // 删除一个数据
    public void removeData(int position) {
        list.remove(position);
    }


}
