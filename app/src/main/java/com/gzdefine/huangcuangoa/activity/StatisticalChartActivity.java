package com.gzdefine.huangcuangoa.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gzdefine.huangcuangoa.App;
import com.gzdefine.huangcuangoa.DateTimePicker;
import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.util.Constant;
import com.gzdefine.huangcuangoa.util.DateUtil;
import com.gzdefine.huangcuangoa.util.Ok;
import com.gzdefine.huangcuangoa.view.PullToRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/1/19 0019.
 */
public class StatisticalChartActivity extends BaseActivity implements View.OnClickListener, DateTimePicker.OnDateSetListener {

    private LinearLayout start_date, End_date, kaoqing_lin;
    private TextView start_text;
    private TextView end_text;
    private TextView txt_title;
    private ImageView back;
    //饼形图控件
    private PieChartView pie_chart;
    private boolean falg;
    private Button chaxun;
    private LinearLayout lin;
    //数据
    private PieChartData pieChardata;
    private StatisticalChart statisticalChart;
    List<SliceValue> values;
    private ArrayList<Integer> data;
    private PullToRefreshLayout pull;
    private AlertDialog jobDialog;
    private DateTimePicker dateTimePicker;
    private ArrayList<Integer> color;
    private ArrayList<String> stateChar;
    private ArrayList<String> stateChar1;
    private ArrayList<String> regFlag;
    private ArrayList<String> regFlag1;
    private Handler handler = new Handler();
    private TextView kaoqing_text;
    private TextView txt_right;
    // 每行图片的个数
    final static int WEIGHT = 3;


    @Override
    public int initContentID() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statisticalchart);
        statisticalChart = new StatisticalChart(this);
//        if (null != statisticalChart) {
//            statisticalChart.Statistical();
//        }
        assignViews();
        initViews();
        txt_title.setText("考勤统计表");
//        txt_right.setText("考勤记录");
        if (null != statisticalChart) {
            statisticalChart.Statistical();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initViews() {
        back.setOnClickListener(this);
        pie_chart.setOnValueTouchListener(selectListener);//设置点击事件监听
    }
    String today;
    private void assignViews() {
        lin = (LinearLayout) findViewById(R.id.lin);
        txt_title = (TextView) findViewById(R.id.txt_title);
        back = (ImageView) findViewById(R.id.img_back);
        pie_chart = (PieChartView) findViewById(R.id.pie_chart);
        start_text = (TextView) findViewById(R.id.start_text);
        end_text = (TextView) findViewById(R.id.end_text);
        Calendar ca = Calendar.getInstance();
        int mYear = ca.get(Calendar.YEAR);
        int mMonth = ca.get(Calendar.MONTH)+1;
        int mDay = ca.get(Calendar.DAY_OF_MONTH);
        String month;
        String day;

        if (mMonth < 10) {
            month = "0" + mMonth;
        } else {
            month = mMonth + "";
        }
        if (mDay < 10) {
            day = "0" + mDay;
        } else {
            day = mDay + "";
        }


        today = String.format("%s-%s-%s", mYear, month, day);
        start_text.setText(today);
        end_text.setText(today);
        chaxun = (Button) findViewById(R.id.chaxun);
        kaoqing_text = (TextView) findViewById(R.id.kaoqing_text);
        kaoqing_text.setText("全天");
        txt_right = (TextView) findViewById(R.id.txt_right);
        txt_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatisticalChartActivity.this, AttendanceRecordActivity.class);
                startActivity(intent);
            }
        });
        chaxun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (start_text.getText().toString() == null || start_text.getText().toString().equals("开始日期")) {
                    App.me().toast("请选择开始日期");
                    return;
                }
                if (end_text.getText().toString() == null || end_text.getText().toString().equals("结束日期")) {
                    App.me().toast("请选择结束日期");
                    return;
                }


                if ((null != start_text.getText().toString() && !start_text.getText().toString().equals("")) && (null != end_text.getText().toString() && !end_text.getText().toString().equals(""))) {
                    if (!start_text.getText().toString().equals("开始日期") && !end_text.getText().toString().equals("结束日期")) {
                        long startdate = DateUtil.getStringToDate(start_text.getText().toString());
                        Log.d("reg", "开始日期:" + start_text.getText().toString());
                        Log.d("reg", "开始日期时间戳：" + startdate);
                        long enddate = DateUtil.getStringToDate(end_text.getText().toString());
                        Log.d("reg", "结束日期时间戳：" + enddate);
                        Log.d("reg", "结束日期:" + end_text.getText().toString());
                        if (startdate > enddate) {
                            App.me().toast("请选择正确的开始结束时间");
                            return;
                        }
                    }
                }
                String kqsd = kaoqing_text.getText().toString();
                Log.d("reg", "kqsd:" + kqsd);
                switch (kqsd){
                    case "全天":
                        kqsd="";
                        break;
                    case "上午":
                        kqsd="up";
                        break;
                    case "下午":
                        kqsd="down";
                        break;
                }

                if (null != statisticalChart) {
                    statisticalChart.chaxun(start_text.getText().toString(), end_text.getText().toString(), kqsd);
                }
            }

        });
        End_date = (LinearLayout) findViewById(R.id.End_date);
        End_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                falg = true;
                BtnTime();
            }
        });
        start_date = (LinearLayout) findViewById(R.id.start_date);
        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                falg = false;
                BtnTime();
            }
        });
        kaoqing_lin = (LinearLayout) findViewById(R.id.kaoqing_lin);
        kaoqing_lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                job();
            }
        });
    }

    private void job() {
        if (jobDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("考勤时段");
            final String[] jobs = {"", "up", "down"};
            final String[] items = {"全天", "上午", "下午"};
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    kaoqing_text.setText(items[which]);
                    kaoqing_text.setTag(jobs[which]);
                }
            });
            builder.setNegativeButton("返回", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    kaoqing_text.setText("考勤时段");
                }
            });
            jobDialog = builder.create();
        }
        jobDialog.show();
    }

    // 每4个图片一行
    public LinearLayout createImageLayout() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setPadding(0,20,0,0);
        return linearLayout;
    }

    public View createImage(int weight, String name, int cock) {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        // 获取屏幕宽度
        int W = this.getWindowManager().getDefaultDisplay().getWidth();
        // 根据每行个数设置布局大小
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(W / weight, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.setPadding(5, 5, 5, 5);
        linearLayout.setGravity(Gravity.CENTER);

        // 设置图片大小
        View view = new View(this);
        view.setLayoutParams(new LinearLayout.LayoutParams(45, 35));
        view.setBackgroundColor(cock);
        TextView textView = new TextView(this);
        textView.setText(name);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(0, 0, 10, 0);
        linearLayout.setTag(textView.getText());
        linearLayout.addView(textView);
        linearLayout.addView(view);
        return linearLayout;
    }

    private void BtnTime() {
        if (dateTimePicker == null) {
            dateTimePicker = new DateTimePicker(this, this, 1);
        }
        dateTimePicker.show();
    }

    @Override
    public void onDateSet(DateTimePicker dialog, int year, int monthOfYear, int dayOfMonth, int timeOfIndex, String t) {

        Log.d("reg", "year:" + year);
        Log.d("reg", "monthOfYear:" + monthOfYear);
        Log.d("reg", "dayOfMonth:" + dayOfMonth);
        if (year != 0) {
            String month;
            String day;
            int mon = monthOfYear + 1;
            if (mon < 10) {
                month = "0" + mon;
            } else {
                month = mon + "";
            }
            if (dayOfMonth < 10) {
                day = "0" + dayOfMonth;
            } else {
                day = dayOfMonth + "";
            }
            String date = String.format("%s-%s-%s", year, month, day);
            Log.d("reg", "date:" + date);
        /*int tim = timeOfIndex + 1;*/
            if (falg == false) {
                start_text.setText(date);
                start_text.setTag(date);
            } else {
                end_text.setText(date);
                end_text.setTag(date);
            }
        } else {
            if (falg == false) {
                start_text.setTag(null);
                start_text.setText(today);
            } else {
                end_text.setTag(null);
                end_text.setText(today);
            }
        }
    }


    /**
     * 获取数据
     */
    private void setPieChartData(String re) {

//        String zhi = "[{name: \"正常\", value: 260}, {name: \"迟到\", value: 65}, {name: \"未签到\", value: 96}]";
//        String zhi = "[{\"name\":\"正常\",\"value\":995},{\"name\":\"迟到\",\"value\":184},{\"name\":\"未签到\",\"value\":757}]";
        if (data == null) {
            data = new ArrayList<>();
        } else {
            data.clear();
        }

        if (color == null) {
            color = new ArrayList<>();
        } else {
            color.clear();
        }
        if (stateChar == null) {
            stateChar = new ArrayList<>();
        } else {
            stateChar.clear();
        }

        if (regFlag == null) {
            regFlag = new ArrayList<>();
        } else {
            regFlag.clear();
        }

        if (values == null) {
            values = new ArrayList<SliceValue>();
        } else {
            values.clear();
        }
        try {
            JSONArray jsonArray = new JSONArray(re);
            for (int i = 0; i < jsonArray.length(); i++) {
                String o = jsonArray.get(i).toString();
                JSONObject object = new JSONObject(o);
                int value = object.getInt("value");
                String name = object.getString("name");
                String Flag = object.getString("regFlag");
                int colo = Color.parseColor(object.getString("color"));
                data.add(value);
                stateChar.add(name);
                color.add(colo);
                regFlag.add(Flag);
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    // 动态添加圆形图片
                    LinearLayout rowLayout = null;
                    lin.removeAllViews();
                    for (int i = 0; i < stateChar.size(); i++) {
                        if (i % WEIGHT == 0) {
                            // LinearLayout不能换行,增加布局完成。
                            rowLayout = createImageLayout();
                            lin.addView(rowLayout);
                        }
                        int cock = color.get(i);
                        final String name = stateChar.get(i);

                        final View columnLinearLayout = createImage(WEIGHT, name, cock);
                        rowLayout.addView(columnLinearLayout);
                        columnLinearLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d("reg", "name:" + name);

                                Intent intent = new Intent(StatisticalChartActivity.this, AttendanceRecordActivity.class);
                                intent.putExtra("Q_regFlag_SN_EQ", name);
                                if (!start_text.getText().toString().equals("开始日期")) {
                                    intent.putExtra("startDate", start_text.getText().toString());
                                } else {
                                    intent.putExtra("startDate", "");
                                }
                                if (!end_text.getText().toString().equals("结束日期")) {
                                    intent.putExtra("endDate", end_text.getText().toString());
                                } else {
                                    intent.putExtra("endDate", "");
                                }
                                startActivity(intent);

                            }
                        });

                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (regFlag1 == null) {
            regFlag1 = new ArrayList<>();
        } else {
            regFlag1.clear();
        }
        if (stateChar1 == null) {
            stateChar1 = new ArrayList<>();
        } else {
            stateChar1.clear();
        }
        for (int i = 0; i < data.size(); i++) {
            if (!data.get(i).toString().equals("0")) {
                SliceValue sliceValue = new SliceValue((float) data.get(i), color.get(i));
                sliceValue.setLabel((int) sliceValue.getValue() + "  /  " + stateChar.get(i));//设置label
                Log.d("reg", "regFlag.get(i):" + regFlag.get(i));
                Log.d("reg", "stateChar.get(i):" +stateChar.get(i));
                regFlag1.add(regFlag.get(i));
                stateChar1.add(stateChar.get(i));
                values.add(sliceValue);
            }

//            else {
//                SliceValue sliceValue = new SliceValue();
//                values.add(sliceValue);
//            }
        }
        initPieChart();

    }


    /**
     * 初始化
     */
    private void initPieChart() {
        pieChardata = new PieChartData();
        pieChardata.setHasLabels(true);//显示表情
        pieChardata.setHasLabelsOnlyForSelected(false);//不用点击显示占的百分比
        pieChardata.setHasLabelsOutside(false);//占的百分比是否显示在饼图外面
        pieChardata.setHasCenterCircle(true);//是否是环形显示
        pieChardata.setValues(values);//填充数据
        pieChardata.setCenterCircleColor(Color.WHITE);//设置环形中间的颜色
        pieChardata.setCenterCircleScale(0.5f);//设置环形的大小级别
        pie_chart.setPieChartData(pieChardata);
        pie_chart.setValueSelectionEnabled(true);//选择饼图某一块变大
        pie_chart.setAlpha(0.9f);//设置透明度
        pie_chart.setCircleFillRatio(1f);//设置饼图大小
    }


    /**
     * 监听事件
     */
    private PieChartOnValueSelectListener selectListener = new PieChartOnValueSelectListener() {

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onValueSelected(int arg0, SliceValue value) {
//            Log.d("reg", "stateChar:" + stateChar.get(arg0));

//            String[] sourceStrArray = String.valueOf(value.getLabelAsChars()).split("  /  ");
            Intent intent = new Intent(StatisticalChartActivity.this, AttendanceRecordActivity.class);
            Log.d("reg"," stateChar.get(arg0):"+ stateChar1.get(arg0));
            intent.putExtra("Q_regFlag_SN_EQ",  stateChar1.get(arg0));
            if (!start_text.getText().toString().equals("开始日期")) {
                intent.putExtra("startDate", start_text.getText().toString());
            } else {
                intent.putExtra("startDate", "");
            }
            if (!end_text.getText().toString().equals("结束日期")) {
                intent.putExtra("endDate", end_text.getText().toString());
            } else {
                intent.putExtra("endDate", "");
            }
            String kqsd = kaoqing_text.getText().toString();
            Log.d("reg", "kqsd:" + kqsd);
            switch (kqsd){
                case "全天":
                    kqsd="";
                    break;
                case "上午":
                    kqsd="up";
                    break;
                case "下午":
                    kqsd="down";
                    break;
            }
            intent.putExtra("signremark",kqsd);
            startActivity(intent);


            //选择对应图形后，在中间部分显示相应信息
//            pieChardata.setCenterText1(stateChar.get(arg0));
//            pieChardata.setCenterText1Color(color.get(arg0));
//            pieChardata.setCenterText1FontSize(10);
//            pieChardata.setCenterText2(value.getValue() + "（" + calPercent(arg0) + ")");
//            pieChardata.setCenterText2Color(color.get(arg0));
//            pieChardata.setCenterText2FontSize(12);
//            Toast.makeText(ChartActivity.this, stateChar[arg0] + ":" + value.getValue(), Toast.LENGTH_SHORT).show();
        }
    };

    private String calPercent(int i) {
        String result = "";
        int sum = 0;
        for (int i1 = 0; i1 < data.size(); i1++) {
            sum += data.get(i1);
        }
        result = String.format("%.2f", (float) data.get(i) * 100 / sum) + "%";
        return result;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
        }
    }

    class StatisticalChart extends Ok {

        public StatisticalChart(Context context) {
            super(context);
        }


        public void Statistical() {
//            super.post(Constant.DOMAIN + "mobile/contacts/findNewById.do?", "newsId", newsId);
            super.post(Constant.DOMAIN + "oa/statistics/userStatistics/getUserDuty.do");
        }

        public void chaxun(String startDate, String endDate, String signremark) {
//            super.post(Constant.DOMAIN + "mobile/contacts/findNewById.do?", "newsId", newsId);
            super.post(Constant.DOMAIN + "oa/statistics/userStatistics/getUserDuty.do", "startDate", startDate, "endDate", endDate, "signremark", signremark);
        }

        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String re = response.body().string();
            Log.d("reg", "result:" + re);
            App.me().checklogin(StatisticalChartActivity.this, re);
            // JSON串转用户对象列表
            setPieChartData(re);
        }
    }
}
