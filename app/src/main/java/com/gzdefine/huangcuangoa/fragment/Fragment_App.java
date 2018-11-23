package com.gzdefine.huangcuangoa.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.activity.BpmStyleActivity;
import com.gzdefine.huangcuangoa.activity.CalendarActivity;
import com.gzdefine.huangcuangoa.activity.CollectionActivity;
import com.gzdefine.huangcuangoa.activity.KaoQinActivity;
import com.gzdefine.huangcuangoa.activity.MapActivity;
import com.gzdefine.huangcuangoa.activity.MeetingTypeActivity;
import com.gzdefine.huangcuangoa.activity.StatisticalChartActivity;
import com.gzdefine.huangcuangoa.activity.WebViewNoTitleActivity;
import com.gzdefine.huangcuangoa.util.Constant;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.gzdefine.huangcuangoa.util.Constant.mail;
import static com.gzdefine.huangcuangoa.util.Constant.myToDo;


public class Fragment_App extends Fragment {

    // 发现
    private Activity ctx;
    private View layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (layout == null) {
            ctx = this.getActivity();
            layout = ctx.getLayoutInflater().inflate(R.layout.fragment_app,
                    null);
        } else {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null) {
                parent.removeView(layout);
            }
        }
        ButterKnife.bind(this, layout);
        return layout;
    }

    @OnClick(R.id.map)
    void map() {
        startActivity(new Intent(getActivity(), MapActivity.class));
    }

    @OnClick(R.id.calendar)
    void calendar() {
        startActivity(new Intent(getActivity(), CalendarActivity.class));
    }

    @OnClick(R.id.toDo)
    void toDo() {
//        ToastUtil.showShort(getActivity(),"正在努力开发中，敬请期待");
        Intent intent = new Intent(getActivity(), WebViewNoTitleActivity.class);
        intent.putExtra("url", myToDo);
        intent.putExtra("type", "2");
        startActivity(intent);
    }




    @OnClick(R.id.kaoqin)
    void kaoqin() {
        Intent intent = new Intent(getActivity(), KaoQinActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.email)
    void email() {
//        ToastUtil.showShort(getActivity(),"正在努力开发中，敬请期待");
        Intent intent = new Intent(getActivity(), WebViewNoTitleActivity.class);
        intent.putExtra("url", mail);
        intent.putExtra("type", "1");
        startActivity(intent);
    }

    @OnClick(R.id.meeting)
    void meetting() {
        Intent intent = new Intent(getActivity(), MeetingTypeActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.new_bpm)
    void new_bpm() {
        Intent intent = new Intent(getActivity(), BpmStyleActivity.class);
        startActivity(intent);

       /* Intent intent = new Intent(getActivity(), WebViewNoTitleActivity.class);
        intent.putExtra("url", "http://183.47.14.182:9079/jsaas/vuemobile/index.html#/solutionsType");
        intent.putExtra("type", "3");
        startActivity(intent);*/
    }


    @OnClick(R.id.wdsq)
    void wdsq(){
        Intent intent = new Intent(getActivity(), WebViewNoTitleActivity.class);
        intent.putExtra("url", Constant.DOMAIN+"/vuemobile/index.html#/myInitiate");
        intent.putExtra("type", "4");
        startActivity(intent);
    }

    @OnClick(R.id.wdcg)
    void wdcg(){
        Intent intent = new Intent(getActivity(), WebViewNoTitleActivity.class);
        intent.putExtra("url", Constant.DOMAIN+"vuemobile/index.html#/myDrafts");
        intent.putExtra("type", "5");
        startActivity(intent);
    }


//    @OnClick(R.id.my_bpm)
//    void bpm() {
//        Intent intent = new Intent(getActivity(), WebViewNoTitleActivity.class);
//        intent.putExtra("url", myInitiate);
//        intent.putExtra("type", "3");
//        startActivity(intent);
//    }

    @OnClick(R.id.gzgs)
    void gao() {
//        intent.putExtra("Pro", "1");
//        startActivity(intent);
        Intent intent = new Intent(getActivity(), CollectionActivity.class);
//        Intent intent = new Intent(getActivity(), WebViewNoTitleActivity.class);
//        intent.putExtra("type", "1");
//        intent.putExtra("url", "http://61.143.38.10:9034/hcdj/hcoah5/sick_leave.html");
        intent.putExtra("txt_title", "公告公示");
        intent.putExtra("colId", "2400000004011000");
        startActivity(intent);
    }

    @OnClick(R.id.dzgw)
    void dzgw() {
        Intent intent = new Intent(getActivity(), CollectionActivity.class);
        intent.putExtra("txt_title", "电子公文");
        intent.putExtra("colId", "2400000007219280");
        startActivity(intent);
    }

    @OnClick(R.id.gzzd)
    void gzzd() {
        Intent intent = new Intent(getActivity(), CollectionActivity.class);
        intent.putExtra("txt_title", "规章制度");
        intent.putExtra("colId", "2400000006921012");
        startActivity(intent);
    }

    @OnClick(R.id.zcfg)
    void zcfg() {
        Intent intent = new Intent(getActivity(), CollectionActivity.class);
        intent.putExtra("txt_title", "政策法规");
        intent.putExtra("colId", "2400000006231013");
        startActivity(intent);
    }

    @OnClick(R.id.my_kqjl)
    void kqjl() {
//        Intent intent = new Intent(getActivity(), AttendanceRecordActivity.class);
//        startActivity(intent);

        Intent intent = new Intent(getActivity(), StatisticalChartActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
