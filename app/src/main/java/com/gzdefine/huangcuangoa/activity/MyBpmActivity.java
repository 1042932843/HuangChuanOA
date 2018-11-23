package com.gzdefine.huangcuangoa.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.gzdefine.huangcuangoa.App;
import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.adapter.BpmAdapter;
import com.gzdefine.huangcuangoa.entity.Bpm;
import com.gzdefine.huangcuangoa.util.Constant;
import com.gzdefine.huangcuangoa.util.LogUtil;
import com.gzdefine.huangcuangoa.util.Ok;
import com.gzdefine.huangcuangoa.util.ToastUtil;
import com.gzdefine.huangcuangoa.util.pinyin.PinYinKit;
import com.gzdefine.huangcuangoa.view.CustomEditText;

import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

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

public class MyBpmActivity extends BaseActivity implements TextWatcher {

    bpm bpmCollor;
    @Bind(R.id.listview)
    ListView lv;
    @Bind(R.id.school_friend_member_search_input)
    CustomEditText input;
    List<Bpm> dates;
    BpmAdapter adapter;
    //更新UIhandler
    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what==1){
                if (adapter==null){
                    LogUtil.d(""+dates.toString());
                    adapter=  new BpmAdapter(MyBpmActivity.this,dates);
                }
                lv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
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
        setContentView(R.layout.activity_my_bpm);
        ButterKnife.bind(this);
        setAppTitle("我的流程");
        initdate();
        input.addTextChangedListener(this);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bpm bpm=dates.get(position);
                String instId = bpm.getInstId();
                if (instId!=null){
                    Intent intent=new Intent(MyBpmActivity.this,WebViewActivity.class);
                    intent.putExtra("url",Constant.DOMAIN+"bpm/core/bpmInst/inform.do?instId="+instId);
                    startActivity(intent);
                }else {
                    ToastUtil.showShort(MyBpmActivity.this,"流程id有误");
                }

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initdate() {
        setAppTitle("我的流程");
        dates=new ArrayList<>();
        bpmCollor=new bpm(this);
        bpmCollor.getdata();
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
        List<Bpm> fSortModels = new ArrayList<Bpm>();

        if (TextUtils.isEmpty(str))
            fSortModels = dates;
        else {
            fSortModels.clear();
            for (Bpm bmp : dates) {
                //判断名字
                String subject = bmp.getSubject();
                if (subject.indexOf(str.toString()) != -1 ||
                        PinYinKit.getPingYin(subject).startsWith(str.toString()) || PinYinKit.getPingYin(subject).startsWith(str.toUpperCase().toString())) {
                    fSortModels.add(bmp);
                }
            }
        }
//        Collections.sort(fSortModels, comparator);
        adapter.updateListView(fSortModels);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    class bpm extends Ok{

        public bpm(Context context){
            super(context);
        }
        public void getdata() {
            super.post(Constant.DOMAIN + "mobileOlineController/findList.do","userId",App.me().login().getUserId(),"subject","","pageIndex","1","pageSize","1000","sortField","createTime_start");
        }

        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String re = response.body().string();
            Log.d("reg", "result:" + re);
            App.me().checklogin(MyBpmActivity.this,re);
            // JSON串转用户对象列表
            try {
                JSONObject obj=new JSONObject(re);
                String data=obj.getString("data");
                dates = JSON.parseArray(data, Bpm.class);
                Message message=new Message();
                message.what=1;
                handler.sendMessage(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    @OnClick(R.id.img_back)
    void back() {
        finish();
    }
}
