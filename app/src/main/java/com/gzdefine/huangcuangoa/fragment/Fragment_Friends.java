package com.gzdefine.huangcuangoa.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.gzdefine.huangcuangoa.App;
import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.adapter.SortAdapter;
import com.gzdefine.huangcuangoa.entity.SortModel;
import com.gzdefine.huangcuangoa.util.Constant;
import com.gzdefine.huangcuangoa.util.Ok;
import com.gzdefine.huangcuangoa.util.SystemBarTintManager;
import com.gzdefine.huangcuangoa.util.pinyin.PinYinKit;
import com.gzdefine.huangcuangoa.util.pinyin.PinyinComparator;
import com.gzdefine.huangcuangoa.view.SideBar;

import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

//通讯录

public class Fragment_Friends extends Fragment implements SideBar
        .OnTouchingLetterChangedListener, TextWatcher, View.OnClickListener {
    private Activity ctx;
    private View layout;

    @Bind(R.id.tabLayout1)
    TabLayout tableLayout1;
    @Bind(R.id.school_friend_member)
    ListView mListView;
    TextView mFooterView;
    ContactsControl contactsControl;
    public PinyinComparator comparator = new PinyinComparator();
    List<SortModel> datas;
    SortAdapter mAdapter;
    public static final int UPDATE_Groups = 0;
    public static final int UPDATE_Contacts = 1;
    private  boolean falg = true;
    //handler
//    Handler handler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            try {
//                String re = null;
//                JSONObject jsonObject;
//                switch (msg.what) {
//                    case UPDATE_Groups:
//                        re = (String) msg.obj;
//                        if (App.me().checkloginflag()) {
//                            Log.d("reg", "通讯录:" + re);
//                            jsonObject = new JSONObject(re);
//                            String data = jsonObject.getString("data");
//                            JSONArray dataArray = new JSONArray(data);
//                            tableLayout1.addTab(tableLayout1.newTab().setTag("").setText("所有人"));
//                            for (int i = 0; i < dataArray.length(); i++) {
//                                JSONObject entiy = dataArray.getJSONObject(i);
//                                tableLayout1.addTab(tableLayout1.newTab().setTag("" + entiy.getString("GROUP_ID_")).setText(entiy.getString("NAME_")));
//                            }
//                        } else {
//                            Log.d("reg","App.me().checkloginflag():"+App.me().checkloginflag());
//                            Log.d("reg","111111111:"+re);
//
//                                if (re.indexOf("input")!=-1) {
//
//
//                                }else {
//                                    Log.d("reg","zou");
//                                    new Thread(){
//                                        @Override
//                                        public void run() {
//                                            super.run();
//                                            try {
//                                                while ( true) {
//                                                    Log.d("reg","111");
//                                                    if (App.me().checkloginflag()) {
//                                                        contactsControl.Groups();
//                                                        break;
//                                                    } else {
//                                                        Thread.sleep(2000);
//                                                    }
//                                                }
//                                            }catch (Exception e){
//                                            }
//                                        }
//                                    }.start();
//                                }
//                            }
//
////						contactsControl.Contacts("");//更新
//                        break;
//                    case UPDATE_Contacts:
//
//                        if (App.me().checkloginflag()) {
//
//                            re = (String) msg.obj;
//                            jsonObject = new JSONObject(re);
//                            String data2 = jsonObject.getString("data");
//                            JSONArray dataArray2 = new JSONArray(data2);
//                            datas.clear();
//                            for (int i = 0; i < dataArray2.length(); i++) {
//                                JSONObject entiy = dataArray2.getJSONObject(i);
//                                SortModel model = new SortModel();
//                                model.setName(entiy.getString("FULLNAME_"));
//                                model.setPhone(entiy.getString("MOBILE_"));
//                                model.setPhoto(entiy.getString("PHOTO_"));
//                                datas.add(model);
//                            }
//                            try {
//                                datas = filledData(datas);
//                            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
//                                badHanyuPinyinOutputFormatCombination.printStackTrace();
//                            }
//                            mFooterView.setText(datas.size() + "位联系人");
////            userListNumTxt.setText("全部："+"\t"+sortModelList.size()+"个联系人");
//                            // sort by a-z
//                            Collections.sort(datas, comparator);
//                            mAdapter.updateListView(datas);
//                        }else {
//                                if (re!=null&&!(re.indexOf("密码或用户名不正确！")!=-1)) {
//                                    new Thread(){
//                                        @Override
//                                        public void run() {
//                                            super.run();
//                                            try {
//                                                while ( true) {
//                                                    if (App.me().checkloginflag()) {
//                                                        contactsControl.Contacts(groupsId);
//                                                        break;
//                                                    } else {
//                                                        Thread.sleep(2000);
//                                                    }
//                                                }
//                                            }catch (Exception e){
//                                            }
//                                        }
//                                    }.start();
//                                }
//
//                        }
//
//                        break;
//
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return false;
//        }
//    });

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                String re;
                JSONObject jsonObject;
                switch (msg.what) {
                    case UPDATE_Groups:
                        re= (String) msg.obj;
                        jsonObject=new JSONObject(re);
                        String data=jsonObject.getString("data");
                        JSONArray dataArray=new JSONArray(data);
                        tableLayout1.addTab(tableLayout1.newTab().setTag("").setText("所有人"));
                        for (int i=0;i<dataArray.length();i++){
                            JSONObject entiy = dataArray.getJSONObject(i);
                            tableLayout1.addTab(tableLayout1.newTab().setTag("" + entiy.getString("GROUP_ID_")).setText(entiy.getString("NAME_")));
                        }
//						contactsControl.Contacts("");//更新
                        break;
                    case UPDATE_Contacts:
                        re= (String) msg.obj;
                        jsonObject=new JSONObject(re);
                        String data2=jsonObject.getString("data");
                        JSONArray dataArray2=new JSONArray(data2);
                        datas.clear();
                        for (int i=0;i<dataArray2.length();i++){
                            JSONObject entiy = dataArray2.getJSONObject(i);
                            SortModel model = new SortModel();
                            model.setName(entiy.getString("FULLNAME_"));
                            model.setPhone(entiy.getString("MOBILE_"));
                            model.setPhoto(entiy.getString("PHOTO_"));
                            datas.add(model);
                        }
                        try {
                            datas = filledData(datas);
                        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                            badHanyuPinyinOutputFormatCombination.printStackTrace();
                        }
                        mFooterView.setText(datas.size() + "位联系人");
//            userListNumTxt.setText("全部："+"\t"+sortModelList.size()+"个联系人");
                        // sort by a-z
                        Collections.sort(datas, comparator);
                        mAdapter.updateListView(datas);
                        isRefresh=false;
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    });

    private String groupsId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (layout == null) {
            ctx = this.getActivity();
            layout = ctx.getLayoutInflater().inflate(R.layout.fragment_friends,
                    null);
        } else {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null) {
                parent.removeView(layout);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(getActivity(), true);
        }
        ButterKnife.bind(this, layout);
        SystemBarTintManager tintManager = new SystemBarTintManager(getActivity());
        tintManager.setStatusBarTintEnabled(true);
        // 使用颜色资源

        tintManager.setStatusBarTintResource(R.color.colorPrimary);
        initViews();
        initdate();
        return layout;
    }

    @TargetApi(19)
    private static void setTranslucentStatus(Activity activity, boolean on) {

        Window win = activity.getWindow();

        WindowManager.LayoutParams winParams = win.getAttributes();

        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

        if (on) {
            winParams.flags |= bits;

        } else {
            winParams.flags &= ~bits;

        }

        win.setAttributes(winParams);

    }

    private void initViews() {
        contactsControl = new ContactsControl(getActivity());//初始化http请求
        contactsControl.Groups();
        SideBar mSideBar = (SideBar) layout.findViewById(R.id.school_friend_sidrbar);
        TextView mDialog = (TextView) layout.findViewById(R.id.school_friend_dialog);
        EditText mSearchInput = (EditText) layout.findViewById(R.id.school_friend_member_search_input);
        mListView = (ListView) layout.findViewById(R.id.school_friend_member);
        mSideBar.setTextView(mDialog);
        mSideBar.setOnTouchingLetterChangedListener(this);
        mSearchInput.addTextChangedListener(this);
        // 给listView设置adapter
        mFooterView = (TextView) View.inflate(getActivity(), R.layout.item_list_contact_count, null);
        mListView.addFooterView(mFooterView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String phone="";
                if(mAdapter.getList()!=null){
                    phone = mAdapter.getList().get(position).getPhone();
                }
                Log.d("reg","phone:"+phone);
                Log.d("reg","name:"+mAdapter.getList().get(position).getName());
                //用intent启动拨打电话
                if (phone != null&&!isRefresh) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

            }
        });
        //切换布局tabLayout初始化
        tableLayout1.setTabMode(TabLayout.MODE_SCROLLABLE);
        tableLayout1.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                groupsId = (String) tab.getTag();
                contactsControl.Contacts(groupsId);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    private void initdate() {
        datas = new ArrayList<>();
        mAdapter = new SortAdapter(getActivity().getApplicationContext(), datas);
        mListView.setAdapter(mAdapter);

//            Collections.sort(datas);
//            mAdapter = new ContactAdapter(mListView, datas);
//            mListView.setAdapter(mAdapter);
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        int position = 0;
        // 该字母首次出现的位置
        if (mAdapter != null) {
            position = mAdapter.getPositionForSection(s.charAt(0));
        }
        if (position != -1) {
            mListView.setSelection(position);
        } else if (s.contains("#")) {
            mListView.setSelection(0);
        }
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
            for (SortModel sortModel : datas) {
                //判断名字
                String name = sortModel.getName();
                if (name.indexOf(str.toString()) != -1 ||
                        PinYinKit.getPingYin(name).startsWith(str.toString()) || PinYinKit.getPingYin(name).startsWith(str.toUpperCase().toString())) {
                    fSortModels.add(sortModel);
                }
                //判断电话
                String phone = sortModel.getPhone();
                if (phone.indexOf(str.toString()) != -1) {
                    fSortModels.add(sortModel);
                }
            }
        }
        Collections.sort(fSortModels, comparator);
        mAdapter.updateListView(fSortModels);
        isRefresh=false;
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    boolean isRefresh;
    class ContactsControl extends Ok {
        int what = 0;

        public ContactsControl(Context context) {
            super(context);
        }

        public void Groups() {
            isRefresh=true;
            what = UPDATE_Groups;
            super.post(Constant.DOMAIN + "mobile/contacts/findListGroups.do");
        }

        public void Contacts(String groupsId) {
            isRefresh=true;
            what = UPDATE_Contacts;
            super.post(Constant.DOMAIN + "mobile/contacts/findListContacts.do", "groupsId", groupsId);
        }

        @Override
        public void onFailure(Call call, IOException e) {
            isRefresh=false;
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String re = response.body().string();
            Log.d("reg", "result:" + re);
            App.me().checklogin(getActivity(), re);
            // JSON串转用户对象列表
            try {
                //发消息更新UI
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
