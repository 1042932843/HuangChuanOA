package com.gzdefine.huangcuangoa.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.gzdefine.huangcuangoa.App;
import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.adapter.BpmTypeAdapter;
import com.gzdefine.huangcuangoa.entity.BpmType;
import com.gzdefine.huangcuangoa.util.Constant;
import com.gzdefine.huangcuangoa.util.Ok;
import com.gzdefine.huangcuangoa.util.SystemBarTintManager;
import com.gzdefine.huangcuangoa.util.pinyin.PinYinKit;

import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class BpmStyleActivity extends BaseActivity implements TextWatcher {

    @Bind(R.id.tabLayout1)
    TabLayout tableLayout1;
    @Bind(R.id.school_friend_member)
    ListView mListView;
    TextView mFooterView;
    List<BpmType> datas;
    BpmTypeAdapter mAdapter;
    private BpmControl bpmControl;
    private TextView text;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case 0:
                        JSONObject jsonObject = new JSONObject("" + msg.obj);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        if (jsonArray.length() == 0) {
                            text.setVisibility(View.VISIBLE);
                        }else {
                            text.setVisibility(View.GONE);
                        }
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject entiy = jsonArray.getJSONObject(i);
                            tableLayout1.addTab(tableLayout1.newTab().setTag("" + entiy.getString("treeId")).setText(entiy.getString("name")));
                        }

                        break;
                    case 1:
                        JSONObject jsonObject2 = new JSONObject("" + msg.obj);
                        JSONArray jsonArray2 = jsonObject2.getJSONArray("data");
                        if (jsonArray2.length() == 0) {
                            text.setVisibility(View.VISIBLE);
                        }else {
                            text.setVisibility(View.GONE);
                        }
                        datas.clear();
                        for (int i = 0; i < jsonArray2.length(); i++) {
                            BpmType bpmType = new BpmType();
                            bpmType.solId = jsonArray2.getJSONObject(i).getString("solId");
                            bpmType.name = jsonArray2.getJSONObject(i).getString("name");
                            datas.add(bpmType);
                        }
                        mAdapter.updateListView(datas);
                        break;
                }
            } catch (JSONException e) {
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
        setContentView(R.layout.activity_bpm_style);
        text = (TextView) findViewById(R.id.text);
        ButterKnife.bind(this);
        setAppTitle("新建流程");
        init();
    }

    private void init() {
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        // 使用颜色资源

        tintManager.setStatusBarTintResource(R.color.colorPrimary);
        initViews();
        datas = new ArrayList<>();
        mAdapter = new BpmTypeAdapter(this, datas);
        mListView.setAdapter(mAdapter);
    }

    private void initViews() {
        EditText mSearchInput = (EditText) findViewById(R.id.school_friend_member_search_input);
        mListView = (ListView) findViewById(R.id.school_friend_member);
        mSearchInput.addTextChangedListener(this);
        // 给listView设置adapter
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BpmType bpmType = datas.get(position);
                Intent intent = new Intent(BpmStyleActivity.this, WebViewActivity.class);
                intent.putExtra("url", Constant.DOMAIN + "bpm/core/bpmInst/start.do?solId=" + bpmType.solId + "&" + "type=app");
                intent.putExtra("typeID",bpmType.solId);
                startActivity(intent);
            }
        });


        bpmControl = new BpmControl(this);
        bpmControl.gettype();
        //切换布局tabLayout初始化
        tableLayout1.setTabMode(TabLayout.MODE_SCROLLABLE);
        tableLayout1.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String treeId = (String) tab.getTag();
                bpmControl.getbpm(treeId);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @OnClick(R.id.img_back)
    void back() {
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        try {
            filerData(s.toString());
        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
            badHanyuPinyinOutputFormatCombination.printStackTrace();
        }
    }


    private void filerData(String str) throws BadHanyuPinyinOutputFormatCombination {
        List<BpmType> fSortModels = new ArrayList<BpmType>();

        if (TextUtils.isEmpty(str))
            fSortModels = datas;
        else {
            fSortModels.clear();
            for (BpmType bpmType : datas) {
                //判断名字
                String name = bpmType.name;
                if (name.indexOf(str.toString()) != -1 ||
                        PinYinKit.getPingYin(name).startsWith(str.toString()) || PinYinKit.getPingYin(name).startsWith(str.toUpperCase().toString())) {
                    fSortModels.add(bpmType);
                }
            }
        }
        mAdapter.updateListView(fSortModels);
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    class BpmControl extends Ok {
        int what = 0;//0是获取一级菜单，1是2级菜单

        public BpmControl(Context context) {
            super(context);
        }

        public void gettype() {
            what = 0;
            super.post(Constant.DOMAIN + "mobileOlineController/getCatTree.do","type", "app");
        }

        public void getbpm(String treeId) {
            what = 1;
            super.post(Constant.DOMAIN + "mobileOlineController/mySolutions.do", "treeId", treeId,"type","app");
        }


        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String re = response.body().string();
            Log.d("reg", "result:" + re);
            App.me().checklogin(BpmStyleActivity.this, re);
            //发消息更新UI
            try {
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
