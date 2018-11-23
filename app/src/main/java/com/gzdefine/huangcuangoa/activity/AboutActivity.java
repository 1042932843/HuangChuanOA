package com.gzdefine.huangcuangoa.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gzdefine.huangcuangoa.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutActivity extends BaseActivity {
    @Bind(R.id.txt_title)
    TextView title;
    @Bind(R.id.img_back)
    ImageView back;
    @Bind(R.id.version)
    TextView version;
    private TextView phone;
    private TextView wy;

    @Override
    public int initContentID() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        phone = (TextView) findViewById(R.id.phone);
        wy = (TextView) findViewById(R.id.wy);
        ButterKnife.bind(this);
        initview();
        setAppTitle("关于我们");
        phone.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View view) {
                                         Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "15839799596", null));
                                         intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                         startActivity(intent);
                                     }
                      }
        );
        wy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();//创建Intent对象
                intent.setAction(Intent.ACTION_VIEW);//为Intent设置动作
                intent.setData(Uri.parse("http://oa.dbsdata.com.cn:8088/login.jsp"));//为Intent设置数据
                startActivity(intent);//将Intent传递给Activity
            }
        });
    }


    @OnClick(R.id.img_back)
    void back() {
        finish();
    }

    void initview() {
        title.setText("关于我们");
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            String name = packageInfo.versionName;
            version.setText("软件版本：  " + name + "  (Android)");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
