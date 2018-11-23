package com.gzdefine.huangcuangoa.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.gzdefine.huangcuangoa.App;
import com.gzdefine.huangcuangoa.Model.Collectio;
import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.adapter.CollectionAdaptent;
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
 * Created by Administrator on 2017/6/15 0015.
 */
public class CollectionActivity extends BaseActivity implements View.OnClickListener, PtrHandler, OnScrollLastItemListener, AdapterView.OnItemClickListener {
    private PullToRefreshLayout pull;
    private ListView listView;
    private ImageView back;
    private CollectionAdaptent collectionAdaptent;
    private List<Collectio> home;
    private List<Collectio> selectedList;
    private CollectionApi collectionApi;
    private  Handler handler = new Handler();
    private TextView txt_title;
    private String colId;

    @Override
    public int initContentID() {
        return 0;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_collection);
        collectionApi = new CollectionApi(this);
        assignViews();
        initViews();
        String tiele = getIntent().getStringExtra("txt_title");
        colId = getIntent().getStringExtra("colId");

        txt_title.setText(tiele);

    }


    private void assignViews() {
        pull = (PullToRefreshLayout) findViewById(R.id.pull);
        listView = (ListView) findViewById(R.id.list);
        txt_title = (TextView) findViewById(R.id.txt_title);
        back = (ImageView) findViewById(R.id.img_back);
        listView.setAdapter(collectionAdaptent = new CollectionAdaptent(this));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Collectio co = collectionAdaptent.getItem(position);
                Intent intent = new Intent(CollectionActivity.this, NewsdetailActivity.class);
                intent.putExtra(NewsdetailActivity.REQUEST_ID, co.getNEW_ID_());
                startActivityForResult(intent, NewsdetailActivity.REQUEST_CODE);
            }
        });
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
        }
    }


    // listview的点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // 判断view是否相同
        if (view.getTag() instanceof CollectionAdaptent.holder) {
            // 如果是的话，重用
            CollectionAdaptent.holder holder = (CollectionAdaptent.holder) view.getTag();
            // 自动触发
            holder.checkboxOperateData.toggle();
        }
    }

    @Override
    public void onScrollLastItem(AbsListView view) {
        if (null != colId) {
            collectionApi.getnext(colId);
        }
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        if (null != colId) {
            collectionApi.Collection(colId);
        }
        //结束后调用
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pull.refreshComplete();
            }
        }, 1000);
    }

    class CollectionApi extends Ok {
        int what = 0;
        private int page;
        private boolean hasMore;
        public CollectionApi(Context context) {
            super(context);
        }


        public void Collection(String colId) {
            page = 1;
            hasMore = false;
            super.post(Constant.DOMAIN + "mobile/contacts/findNewListPageByColumnId.do","colId",colId, "pageIndex", page + "");
        }

        private void getnext(String colId) {
            if (hasMore) {
                super.post(Constant.DOMAIN + "mobile/contacts/findNewListPageByColumnId.do","colId",colId, "pageIndex", page + "");
            }

        }

        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            final String re = response.body().string();
            Log.d("reg", "result:" + re);
            App.me().checklogin(CollectionActivity.this, re);

            handler.post(new Runnable() {
                             @Override
                             public void run() {
                                 // JSON串转用户对象列表
                                 if (collectionAdaptent == null) {
                                     collectionAdaptent = new CollectionAdaptent(CollectionActivity.this);
                                 } else {
                                     collectionAdaptent.clear();
                                 }

                                 if (page == 1) {
                                     if (home == null) {
                                         home = new ArrayList<Collectio>();
                                     } else {
                                         home.clear();
                                     }
                                 }
                                 try {
                                     JSONObject obj = new JSONObject(re);
                                     Log.d("reg","obj:"+obj);
                                     String success = obj.getString("success");
                                     if (success.equals("true")) {
                                         JSONArray jsonArray = obj.getJSONArray("data");
                                         for (int i = 0; i < jsonArray.length(); i++) {
                                             String o = jsonArray.getString(i);
                                             home.add(JSON.parseObject(o, Collectio.class));
                                         }
                                         page += 1;
                                         hasMore = jsonArray.length() >= 10;
                                         collectionAdaptent.addAll(home);
                                         collectionAdaptent.notifyDataSetChanged();
                                     } else {
                                         String message = obj.getString("message");
                                         App.me().toast(message);
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
        if (requestCode == NewsdetailActivity.REQUEST_CODE && resultCode == RESULT_OK) {
            pull.post(new Runnable() {
                @Override
                public void run() {
                    pull.autoRefresh();
                }
            });
        }

    }
}
