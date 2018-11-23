package com.gzdefine.huangcuangoa.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.gzdefine.huangcuangoa.App;
import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.adapter.AttendanceRecordAdapter;
import com.gzdefine.huangcuangoa.entity.Attendance;
import com.gzdefine.huangcuangoa.entity.LoginResponse;
import com.gzdefine.huangcuangoa.ui.OnScrollLastItemListener;
import com.gzdefine.huangcuangoa.ui.OnScrollListener;
import com.gzdefine.huangcuangoa.util.Constant;
import com.gzdefine.huangcuangoa.util.Ok;
import com.gzdefine.huangcuangoa.view.PullToRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/1/4 0004.
 */
public class AttendanceRecordActivity extends BaseActivity implements View.OnClickListener, PtrHandler, OnScrollLastItemListener {
    private List<Attendance> attend;
    private PullToRefreshLayout pull;
    private ListView listView;
    private ImageView back;
    private ComplaintApi complaintApi;
    private TextView title;
    private AttendanceRecordAdapter adapter;
    //    private List<Complaint> home;
    Handler handler = new Handler();
    private EditText search;
    private boolean falg;
    private String Q_regFlag_SN_EQ;
    private String Q_registerTime_D_GE;
    private String Q_registerTime_D_LE;
    private String departOrUser;
    private String signremark;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_theservant);
        setContentView(R.layout.activity_attendancerecord);
        complaintApi = new ComplaintApi(this);
        Q_regFlag_SN_EQ = getIntent().getStringExtra("Q_regFlag_SN_EQ");
        Q_registerTime_D_GE = getIntent().getStringExtra("startDate");
        Q_registerTime_D_LE = getIntent().getStringExtra("endDate");
        signremark=getIntent().getStringExtra("signremark");
        assignViews();
        initViews();
        title.setText("考勤记录");
    }

    private void assignViews() {
        search = (EditText) findViewById(R.id.search);
        search.setFocusableInTouchMode(false);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AttendanceRecordActivity.this, SearchActivity.class);
                startActivityForResult(intent, SearchActivity.REQUEST_CODE);
            }
        });
        title = (TextView) findViewById(R.id.txt_title);

        pull = (PullToRefreshLayout) findViewById(R.id.pull);
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter = new AttendanceRecordAdapter(this));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter != null) {
                    Attendance theservant = adapter.getItem(position);
//                    Intent intent = new Intent(ComplaintActivity.this, ComplaintorderActivity.class);
//                    intent.putExtra(ComplaintorderActivity.REQUEST_COMPLAIN_ID, theservant.getComplain_id());
//                    startActivityForResult(intent, DoctorDetailsActivity.REQUEST_CODE);
                }
            }
        });
//        listView.setAdapter(complaintAdapter = new ComplaintAdapter(this));
        back = (ImageView) findViewById(R.id.img_back);
    }


    private void initViews() {
        View[] views = {back};
        for (View view : views) {
            view.setOnClickListener(this);
        }
        listView.setOnScrollListener(new OnScrollListener(this));
        pull.setPtrHandler(this);
        pull.post(new Runnable() {
            @Override
            public void run() {
                pull.autoRefresh();
            }
        });
    }

    @Override
    public int initContentID() {
        return 0;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
        }
    }

    @Override
    public void onScrollLastItem(AbsListView view) {
        LoginResponse user = App.me().login();
        if (null != user) {
            if (null != complaintApi) {
//                if (falg == true) {
//                    complaintApi.getnext(null, null, null, null);
//                } else {
                    complaintApi.getnext(Q_regFlag_SN_EQ, Q_registerTime_D_GE, Q_registerTime_D_LE, departOrUser,signremark);
//                }
            }
        }
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        LoginResponse user = App.me().login();
        if (null != user.getUserId()) {
            complaintApi.Complaint(Q_regFlag_SN_EQ, Q_registerTime_D_GE, Q_registerTime_D_LE, departOrUser,signremark);
        }
        //结束后调用
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pull.refreshComplete();
            }
        }, 1000);
    }


    class ComplaintApi extends Ok {
        int what = 0;
        private int page;
        private boolean hasMore;

        public ComplaintApi(Context context) {
            super(context);
        }


        public void Complaint(String Q_regFlag_SN_EQ, String Q_registerTime_D_GE, String Q_registerTime_D_LE, String departOrUser,String signremark) {
            page = 0;
            hasMore = false;
            super.post(Constant.DOMAIN + "hr/core/hrDutyRegister/getAllRegister.do",
                    "regFlag", Q_regFlag_SN_EQ,
                    "regStart", Q_registerTime_D_GE,
                    "regEnd", Q_registerTime_D_LE,
                    "departOrUser", departOrUser,
                    "signremark",signremark,
                    "pageSize", "10",
                    "pageIndex", page);
        }

        public void getnext(String Q_regFlag_SN_EQ, String Q_registerTime_D_GE, String Q_registerTime_D_LE, String departOrUser,String signremark) {
            if (hasMore) {
                super.post(Constant.DOMAIN + "hr/core/hrDutyRegister/getAllRegister.do",
                        "regFlag", Q_regFlag_SN_EQ,
                        "regStart", Q_registerTime_D_GE,
                        "regEnd", Q_registerTime_D_LE,
                        "departOrUser", departOrUser,
                        "signremark",signremark,
                        "pageSize", "10",
                        "pageIndex", page);
            }
        }

        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            final String re = response.body().string();
            App.me().checklogin(AttendanceRecordActivity.this, re);

            handler.post(new Runnable() {
                @Override
                public void run() {

                    if (adapter == null) {
                        adapter = new AttendanceRecordAdapter(AttendanceRecordActivity.this);
                    } else {
                        adapter.clear();
                    }

                    if (page == 0) {
                        if (attend == null) {
                            attend = new ArrayList<Attendance>();
                        } else {
                            attend.clear();
                        }
                    }
                    try {
                        JSONObject obj = new JSONObject(re);
                        String success = obj.getString("success");
                        if (success.equals("true")) {
                            JSONArray jsonArray = obj.getJSONArray("data");
                            if (jsonArray.length() == 0) {
                                String message = obj.getString("message");
                                App.me().toast(message);
                                adapter.notifyDataSetChanged();
                            } else {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    String o = jsonArray.getString(i);
                                    attend.add(JSON.parseObject(o, Attendance.class));
                                }
                                page += 1;
                                hasMore = jsonArray.length() >= 10;
                                adapter.addAll(attend);
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            String message = obj.getString("message");
                            App.me().toast(message);
                            adapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SearchActivity.REQUEST_CODE && resultCode == RESULT_OK) {
            Q_regFlag_SN_EQ = data.getStringExtra("Q_regFlag_SN_EQ");
            Q_registerTime_D_GE = data.getStringExtra("Q_registerTime_D_GE");
            Q_registerTime_D_LE = data.getStringExtra("Q_registerTime_D_LE");
            departOrUser = data.getStringExtra("departOrUser");
            Log.d("reg", "考勤结果:" + Q_regFlag_SN_EQ);
            Log.d("reg", "开始日期:" + Q_registerTime_D_GE);
            Log.d("reg", "结束日期 :" + Q_registerTime_D_LE);
            Log.d("reg", "姓名/部门名称:" + departOrUser);
            LoginResponse user = App.me().login();
            if (null != user.getUserId()) {
                falg = true;
                complaintApi.Complaint(Q_regFlag_SN_EQ, Q_registerTime_D_GE, Q_registerTime_D_LE, departOrUser,signremark);
            }
        }
    }
}
