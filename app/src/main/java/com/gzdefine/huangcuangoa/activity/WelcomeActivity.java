package com.gzdefine.huangcuangoa.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.gzdefine.huangcuangoa.MainActivity;
import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.util.SPUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WelcomeActivity extends AppCompatActivity {
    @Bind(R.id.welcome)
    ImageView iv;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            //取消标题栏
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            //取消状态栏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_welcome);
            ButterKnife.bind(this);
            Boolean firstStartAPP= (Boolean) SPUtils.get(this,"firstStartAPP",false);
            Log.d("reg","firstStartAPP:"+firstStartAPP);
            if (firstStartAPP ==false){
//                Glide.with(this).load(R.drawable.welcomegif).diskCacheStrategy(DiskCacheStrategy.NONE).into(new GlideDrawableImageViewTarget(iv, 1));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                            SPUtils.put(WelcomeActivity.this,"firstStartAPP",false);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                        finish();
                    }
                }).start();
            }else {
                SPUtils.put(WelcomeActivity.this,"firstStartAPP",false);
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                finish();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
