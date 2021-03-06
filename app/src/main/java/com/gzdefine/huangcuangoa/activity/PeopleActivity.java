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
import android.widget.EditText;
import android.widget.ListView;

import com.gzdefine.huangcuangoa.App;
import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.adapter.PeopleAdapter;
import com.gzdefine.huangcuangoa.entity.SortModel;
import com.gzdefine.huangcuangoa.util.Constant;
import com.gzdefine.huangcuangoa.util.Ok;
import com.gzdefine.huangcuangoa.util.SystemBarTintManager;
import com.gzdefine.huangcuangoa.util.ToastUtil;
import com.gzdefine.huangcuangoa.util.pinyin.PinYinKit;
import com.gzdefine.huangcuangoa.util.pinyin.PinyinComparator;

import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class PeopleActivity extends BaseActivity implements TextWatcher {
    @Bind(R.id.tabLayout1)
    TabLayout tableLayout1;
    @Bind(R.id.school_friend_member)
    ListView mListView;
    public PinyinComparator comparator = new PinyinComparator();
    List<SortModel> datas;
    PeopleAdapter mAdapter;
    private PeopleActivity.PeopleControl bpmControl;
    public static final int REQUEST_CODE = 'p' + 'e' + 'o' + 'p' + 'l' + 'e';

    Boolean first = true;//是否第一次加载数据
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case 0:
                        JSONObject jsonObject = new JSONObject("" + msg.obj);
                        JSONArray jsonArray = jsonObject.getJSONArray("paramList");
                        tableLayout1.addTab(tableLayout1.newTab().setTag("").setText("所有人"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject entiy = jsonArray.getJSONObject(i);
                            tableLayout1.addTab(tableLayout1.newTab().setTag("" + entiy.getString("id")).setText(entiy.getString("name")));
                        }
                        break;
                    case 1:
                        JSONObject jsonObject2 = new JSONObject("" + msg.obj);
                        JSONArray jsonArray2 = jsonObject2.getJSONArray("paramList");
                        datas.clear();
                        for (int i = 0; i < jsonArray2.length(); i++) {
                            SortModel people = new SortModel();
                            people.setId(jsonArray2.getJSONObject(i).getString("userid"));
                            people.setName(jsonArray2.getJSONObject(i).getString("username"));
                            datas.add(people);
                        }
                        try {
                            datas = filledData(datas);
                        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                            badHanyuPinyinOutputFormatCombination.printStackTrace();
                        }
//            userListNumTxt.setText("全部："+"\t"+sortModelList.size()+"个联系人");
                        // sort by a-z
                        Collections.sort(datas, comparator);
                        if (first) {
                            mAdapter.checkNoAll(datas);
                            first = false;
                        } else {
                            mAdapter.updateListView(datas);

                        }
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
        setContentView(R.layout.activity_people);
        ButterKnife.bind(this);
        setAppTitle("选择参加人员");
        init();
    }

    private void init() {
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        // 使用颜色资源

        tintManager.setStatusBarTintResource(R.color.colorPrimary);
        initViews();
        datas = new ArrayList<>();
        mAdapter = new PeopleAdapter(this, datas);
        mListView.setAdapter(mAdapter);

//        // 给listView设置adapter
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.d("max","position1:"+position);
//                mAdapter.check(position);
//            }
//        });

    }

    private void initViews() {
        EditText mSearchInput = (EditText) findViewById(R.id.school_friend_member_search_input);
//        mListView = (ListView) findViewById(R.id.school_friend_member);
        mSearchInput.addTextChangedListener(this);


        bpmControl = new PeopleActivity.PeopleControl(this);
        bpmControl.gettype();
        //切换布局tabLayout初始化
        tableLayout1.setTabMode(TabLayout.MODE_SCROLLABLE);
        tableLayout1.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String id = (String) tab.getTag();
                bpmControl.getbpm(id);
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

    @OnClick(R.id.done)
    void done() {
        try {
            sendData();
            finish();
        }catch (Exception e){
            ToastUtil.showShort(this,"请至少选择一人");
        }

    }

    @OnClick(R.id.all)
    void  allcheck(){
        mAdapter.checkAll(datas);
    }

    private void sendData() {
        ArrayList<String> peoples = mAdapter.peoples;
        HashMap<String, Boolean> ischecks = mAdapter.ischecks;
        String names = "";
        String id = "";
        for (int i = 0; i < peoples.size(); i++) {
            names += peoples.get(i).toString() + ",";
        }
        Iterator iter = ischecks.entrySet().iterator();        //获取key和value的set
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();        //把hashmap转成Iterator再迭代到entry
      Log.d("max","key:"+(String) entry.getKey());
      Log.d("max","val:"+(Boolean) entry.getValue());
            String key = (String) entry.getKey();        //从entry获取key
            Boolean val = (Boolean) entry.getValue();    //从entry获取value
            if (val) {
                id += key + ",";
            }
        }
      String sendNamess=  names.substring(0, names.length() - 1); //去掉最后一个“,"号
      String endIds = id.substring(0, id.length()-1);//去掉最后一个“,"号
        Intent intent = new Intent();
        intent.putExtra("pro_skill", endIds);
        intent.putExtra("pro_skillstr", sendNamess);
        setResult(RESULT_OK, intent);
        finish();

//        EventBus.getDefault().post(sendNamess,"getNames");
//        EventBus.getDefault().post(endIds,"getIds");
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

    private List<SortModel> filledData(List<SortModel> date) throws BadHanyuPinyinOutputFormatCombination {
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for (int i = 0; i < date.size(); i++) {
            SortModel sortModel = new SortModel();
            sortModel.setName(date.get(i).getName());
            sortModel.setPhone(date.get(i).getPhone());
            sortModel.setId(date.get(i).getId());
            sortModel.setPhoto(date.get(i).getPhoto());
            //汉字转换成拼音
            String pinyin = PinYinKit.getPingYin(date.get(i).getName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

    private void filerData(String str) throws BadHanyuPinyinOutputFormatCombination {
        List<SortModel> fSortModels = new ArrayList<SortModel>();

        if (TextUtils.isEmpty(str))
            fSortModels = datas;
        else {
            fSortModels.clear();
            for (SortModel bpmType : datas) {
                //判断名字
                String name = bpmType.getName();
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

    class PeopleControl extends Ok {
        int what = 0;//0是获取一级菜单，1是2级菜单

        public PeopleControl(Context context) {
            super(context);
        }

        public void gettype() {
            what = 0;
            super.post(Constant.DOMAIN + "mobile/confernceMobile/getGroups.do");
        }

        public void getbpm(String groupsId) {
            what = 1;
            super.post(Constant.DOMAIN + "mobile/confernceMobile/theUser.do", "groupsId", groupsId);
        }


        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String re = response.body().string();
            Log.d("reg", "result:" + re);
            App.me().checklogin(PeopleActivity.this, re);
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
