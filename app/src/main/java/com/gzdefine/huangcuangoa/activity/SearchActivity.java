package com.gzdefine.huangcuangoa.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.gzdefine.huangcuangoa.App;
import com.gzdefine.huangcuangoa.DateTimePicker;
import com.gzdefine.huangcuangoa.Model.KeyValue;
import com.gzdefine.huangcuangoa.Model.KeyValuePair;
import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.adapter.SearchAdapter;
import com.gzdefine.huangcuangoa.entity.LoginResponse;
import com.gzdefine.huangcuangoa.entity.Search;
import com.gzdefine.huangcuangoa.listener.ListItemClickHelp;
import com.gzdefine.huangcuangoa.util.DateUtil;
import com.gzdefine.huangcuangoa.util.ListDataSave;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/1/4 0004.
 */
public class SearchActivity extends BaseActivity implements DateTimePicker.OnDateSetListener, ListItemClickHelp {
    public static final int REQUEST_CODE = 's' + 'e' + 'a' + 'r' + 'c' + 'h' + 'a' + 'c' + 't' + 'i' + 't' + 'y';
    private ImageView img_back;
    private EditText search;
    private TextView txt_right;
    private TextView qingkong;
    private TextView kaoqing_text;
    private TextView start_text;
    private TextView end_text;
    private ArrayList<Search> arrayList = new ArrayList<>();
    private ListView listView;
    private SearchAdapter adapter;
    private LinearLayout start_date, End_date, kaoqing_lin;
    private DateTimePicker dateTimePicker;
    private boolean falg;
    private AlertDialog jobDialog;
    ListDataSave dataSave;
    private ArrayList<KeyValuePair> listBean;
    private ArrayList<KeyValue> lb;
    private ArrayList List = new ArrayList();
    Search m = new Search();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listBean = new ArrayList<KeyValuePair>();
        lb = new ArrayList<KeyValue>();
//        setContentView(R.layout.activity_theservant);
        setContentView(R.layout.activity_search);
        img_back = (ImageView) findViewById(R.id.img_back);
        search = (EditText) findViewById(R.id.search);
        LoginResponse user = App.me().login();
        if (null!=user.getUserId()){
            dataSave = new ListDataSave(this, user.getUserId()+"baiyu");
        }
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        listView = (ListView) findViewById(R.id.list);

        listView.setAdapter(adapter = new SearchAdapter(SearchActivity.this, SearchActivity.this));

        if (null != dataSave.getDataList("listMap").toString() && !dataSave.getDataList("listMap").toString().equals("")) {
            Gson gson = new Gson();
            String strJson = gson.toJson(dataSave.getDataList("listMap"));
            try {

                JSONArray jsonArray = new JSONArray(strJson);
                for (int i = 0; i < jsonArray.length(); i++) {
                    String o = jsonArray.getString(i);
//                    sea = JSON.parseObject(o, Search.class);
                    arrayList.add(JSON.parseObject(o, Search.class));
//                    Log.d("reg","mz:"+sea.getDepartOrUser());
//                    Log.d("reg","jieguo:"+sea.getQ_regFlag_SN_EQ());
//                    Log.d("reg","kaishi:"+sea.getQ_registerTime_D_GE());
//                    Log.d("reg","jiesu:"+sea.getQ_registerTime_D_LE());

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapter.clear();
            adapter.addAll(arrayList);
//            adapter.DataSave(strJson);
            adapter.notifyDataSetChanged();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter != null) {
                    Search search = adapter.getItem(position);
                    Log.d("reg", "mz:" + search.getDepartOrUser());
                    Log.d("reg", "jieguo:" + search.getQ_regFlag_SN_EQ());
                    Log.d("reg", "kaishi:" + search.getQ_registerTime_D_GE());
                    Log.d("reg", "jiesu:" + search.getQ_registerTime_D_LE());
                    Intent intent = new Intent();
                    if (null != search.getQ_regFlag_SN_EQ() && !search.getQ_regFlag_SN_EQ().equals("")) {
                        intent.putExtra("Q_regFlag_SN_EQ", search.getQ_regFlag_SN_EQ());
                    } else {
                        intent.putExtra("Q_regFlag_SN_EQ", "");
                    }
                    if (null != search.getQ_registerTime_D_GE() && !search.getQ_registerTime_D_GE().equals("")) {
                        if (search.getQ_registerTime_D_GE().equals("开始日期")) {
                            intent.putExtra("Q_registerTime_D_GE", "");
                        } else {
                            intent.putExtra("Q_registerTime_D_GE", search.getQ_registerTime_D_GE());
                        }
                    } else {
                        intent.putExtra("Q_registerTime_D_GE", "");
                    }
                    if (null != search.getQ_registerTime_D_LE() && !search.getQ_registerTime_D_LE().equals("")) {
                        if (search.getQ_registerTime_D_LE().equals("结束日期")) {
                            intent.putExtra("Q_registerTime_D_LE ", "");
                        } else {
                            intent.putExtra("Q_registerTime_D_LE", search.getQ_registerTime_D_LE());
                        }
                    } else {
                        intent.putExtra("Q_registerTime_D_LE", "");
                    }
                    if (null != search.getDepartOrUser() && !search.getDepartOrUser().equals("")) {
                        intent.putExtra("departOrUser", search.getDepartOrUser());
                    } else {
                        intent.putExtra("departOrUser", "");
                    }
                    setResult(RESULT_OK, intent);
                    finish();

                }
            }
        });

        kaoqing_text = (TextView) findViewById(R.id.kaoqing_text);
        start_text = (TextView) findViewById(R.id.start_text);
        end_text = (TextView) findViewById(R.id.end_text);
        qingkong = (TextView) findViewById(R.id.qingkong);
        qingkong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataSave.qingchu();
                arrayList.clear();
                adapter.clear();
                adapter.notifyDataSetChanged();
                App.me().toast("清除完成");
            }
        });
        txt_right = (TextView) findViewById(R.id.txt_right);
        txt_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
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


                if (null != kaoqing_text.getTag() && !kaoqing_text.getTag().toString().equals("")) {
                    intent.putExtra("Q_regFlag_SN_EQ", kaoqing_text.getTag().toString());
                } else {
                    intent.putExtra("Q_regFlag_SN_EQ", "");
                }

                if (!(start_text.getText().toString().equals("开始日期") &&end_text.getText().toString().equals("结束日期"))) {
                    if (start_text.getText().toString().equals("开始日期")) {
                        App.me().toast("请选择开始日期");
                        return;
                    } else {
                        intent.putExtra("Q_registerTime_D_GE", start_text.getText().toString());
                    }
                    if (end_text.getText().toString().equals("结束日期")) {
                        App.me().toast("请选择结束日期");
                        return;
                    } else {
                        intent.putExtra("Q_registerTime_D_LE", end_text.getText().toString());
                    }

                }

//                if (null != start_text.getText().toString() &&! start_text.getText().toString().equals("")) {
//                    if (start_text.getText().toString().equals("开始日期")) {
//                        App.me().toast("请选择开始日期");
//                        return;
//                    } else {
//                        intent.putExtra("Q_registerTime_D_GE", start_text.getText().toString());
//                    }
//                }
//                if (null!= end_text.getText().toString() && !end_text.getText().toString().equals("")) {
//                    if (end_text.getText().toString().equals("结束日期")) {
//                        App.me().toast("请选择结束日期");
//                        return;
//                    } else {
//                        intent.putExtra("Q_registerTime_D_LE", end_text.getText().toString());
//                    }
//                }


//                    if (null != start_text.getText().toString() && !start_text.getText().toString().equals("")) {
//                        if (start_text.getText().toString().equals("开始日期")) {
//                        intent.putExtra("Q_registerTime_D_GE", "");
//                            App.me().toast("请选择完整的开始结束时间");
//                            return;
//                        } else {
//                            intent.putExtra("Q_registerTime_D_GE", start_text.getText().toString());
//                        }
//                    } else {
//                    intent.putExtra("Q_registerTime_D_GE", "");
//                        App.me().toast("请选择完整的开始结束时间");
//                        return;
//                    }
//                    if (null != end_text.getText().toString() && !end_text.getText().toString().equals("")) {
//                        if (end_text.getText().toString().equals("结束日期")) {
//                        intent.putExtra("Q_registerTime_D_LE ", "");
//                            App.me().toast("请选择完整的开始结束时间");
//                            return;
//                        } else {
//                            intent.putExtra("Q_registerTime_D_LE", end_text.getText().toString());
//                        }
//                    } else {
//                    intent.putExtra("Q_registerTime_D_LE", "");
//                        App.me().toast("请选择完整的开始结束时间");
//                        return;
//                    }

//                }

                if (null != search.getText().toString() && !search.getText().toString().equals("")) {
                    intent.putExtra("departOrUser", search.getText().toString());
                } else {
                    intent.putExtra("departOrUser", "");
                }


                if (search.getText().toString() == null && search.getText().toString().equals("")) {
                    m.setDepartOrUser("");
                } else {
                    m.setDepartOrUser(search.getText().toString());
                }

                if (null != kaoqing_text.getText().toString() && !kaoqing_text.getText().toString().equals("")) {
                    if (kaoqing_text.getText().toString().equals("考勤结果")) {
                        m.setQ_regFlag_SN_EQ("");
                    } else {
                        m.setQ_regFlag_SN_EQ(kaoqing_text.getTag().toString());
                    }
                } else {
                    m.setQ_regFlag_SN_EQ("");
                }
                if (null != start_text.getText().toString() && !start_text.getText().toString().equals("")) {
                    if (start_text.getText().toString().equals("开始日期")) {
                        m.setQ_registerTime_D_GE("");
                    } else {
                        m.setQ_registerTime_D_GE(start_text.getText().toString());
                    }
                } else {
                    m.setQ_registerTime_D_GE("");
                }

                if (null != end_text.getText().toString() && !end_text.getText().toString().equals("")) {
                    if (end_text.getText().toString().equals("结束日期")) {
                        m.setQ_registerTime_D_LE("");
                    } else {
                        m.setQ_registerTime_D_LE(end_text.getText().toString());
                    }
                } else {
                    m.setQ_registerTime_D_LE("");
                }


                if ((m.getDepartOrUser().equals("") && m.getQ_regFlag_SN_EQ().equals("") && m.getQ_registerTime_D_LE().equals("") && m.getQ_registerTime_D_GE().equals(""))) {
                    setResult(RESULT_OK, intent);
                    finish(); // 返回上一个页面
                } else {
                    Gson gson = new Gson();
                    String strJson = gson.toJson(dataSave.getDataList("listMap"));
                    String mjson = gson.toJson(m);
                    List.add(m);
                    try {
                        JSONArray jsonArray = new JSONArray(strJson);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject o = jsonArray.getJSONObject(i);
                            Search os = new Search();
                            os.setDepartOrUser(o.getString("departOrUser"));
                            os.setQ_regFlag_SN_EQ(o.getString("Q_regFlag_SN_EQ"));
                            os.setQ_registerTime_D_LE(o.getString("Q_registerTime_D_LE"));
                            os.setQ_registerTime_D_GE(o.getString("Q_registerTime_D_GE"));
                            if (strJson.indexOf(mjson) != -1) {
                                if (!gson.toJson(os).equals(mjson)) {
                                    List.add(os);
                                }
                            } else {
                                List.add(os);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dataSave.setDataList("listMap", List);
                    adapter.notifyDataSetChanged();
                    setResult(RESULT_OK, intent);
                    finish(); // 返回上一个页面
                }

            }
        });
        kaoqing_lin = (LinearLayout) findViewById(R.id.kaoqing_lin);
        kaoqing_lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                job();
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

    }

    private void BtnTime() {
        if (dateTimePicker == null) {
            dateTimePicker = new DateTimePicker(this, this, 1);
        }
        dateTimePicker.show();
    }

    private void job() {
        if (jobDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("考勤结果");
            final String[] jobs = {"1", "2","3"};
            final String[] items = {"正常", "迟到", "未签到"};
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
                    kaoqing_text.setText("考勤结果");
                }
            });
            jobDialog = builder.create();
        }
        jobDialog.show();
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
                start_text.setText("开始日期");
            } else {
                end_text.setTag(null);
                end_text.setText("结束日期");
            }
        }
    }

    @Override
    public int initContentID() {
        return 0;
    }


    @Override
    public void onClick(View item, View widget, int position, int which) {
        switch (which) {
            case R.id.img:
                Log.d("reg", "position:" + position);
                if (arrayList.size() > 0) {
                    arrayList.remove(position);
                    dataSave.setDataList("listMap", arrayList);
                    adapter.clear();
                    adapter.addAll(arrayList);
                    adapter.notifyDataSetChanged();
                }
                break;

            default:
                break;
        }
    }
}
