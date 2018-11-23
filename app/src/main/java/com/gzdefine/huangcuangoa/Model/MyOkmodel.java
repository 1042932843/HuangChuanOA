package com.gzdefine.huangcuangoa.Model;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.gzdefine.huangcuangoa.util.ApiMsg;
import com.gzdefine.huangcuangoa.util.Constant;
import com.gzdefine.huangcuangoa.util.Ok;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Response;

/**
 * TODO：
 * Created by Max on 2017/8/15.
 */

public class MyOkmodel {
    Activity context;
    String seesion="";
    public MyOkmodel(Activity context){
        this.context=context;


    }


    public void testok2(){
        Pay testok=new Pay(context);
        testok.Post();
    }


    class Pay extends Ok{
        protected Pay(Context context){
            super(context);
        }
        public void Post(){
            super.post(Constant.DOMAIN + "mobile/bpm/test.do");

        }

        @Override
        public void onFailure(Call call, IOException e) {
            Log.i("info_call2fail", e.toString());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {

                String re = response.body().string();
                Log.d("reg", "My_Ok_Http.result" + re);

                Headers headers = response.headers();
                Log.i("info_response.headers", headers + "");
                String r = response.body().string();
                Log.e("reg", "r:" + r);
                final ApiMsg apiMsg = JSON.parseObject(r, ApiMsg.class);
                Log.d("reg", "result:"+apiMsg.getResult());
                if (apiMsg.getState().equals("0")){
                    try {
                        JSONObject result = new JSONObject(apiMsg.getResult());
                        final String tn = result.getString("tn");
                        Log.d("reg", "tn:"+tn);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                }
        }
    }

}
