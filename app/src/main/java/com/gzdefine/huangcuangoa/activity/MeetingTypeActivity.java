package com.gzdefine.huangcuangoa.activity;

import android.content.Intent;
import android.os.Bundle;

import com.gzdefine.huangcuangoa.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MeetingTypeActivity extends BaseActivity {

    @Override
    public int initContentID() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metting_type);
        ButterKnife.bind(this);
        setAppTitle("会议");
    }
    @OnClick(R.id.type_has)
    void has(){
        startActivity(new Intent(this,MeetingActivity.class));
    }
    @OnClick(R.id.type_none)
    void none(){
        startActivity(new Intent(this,MeetingNoneActivity.class));
    }
    @OnClick(R.id.img_back)
    void back(){
        finish();
    }
}
