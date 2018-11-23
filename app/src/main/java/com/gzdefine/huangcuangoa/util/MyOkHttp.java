package com.gzdefine.huangcuangoa.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.gzdefine.huangcuangoa.App;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import cz.msebera.android.httpclient.HttpException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/3/16 0016.
 */

public class MyOkHttp extends OkHttpClient implements DialogInterface.OnCancelListener{

    protected ProgressDialog dialog;
    protected Handler dialogHandler;
    protected long dialogShowTime;
    protected Call call;
    MyOkHttp myOkHttp;

    public final Call getCall() {
        return call;
    }

    public final Call call(String session, String url, Object... paramsKeyAndValue) {
        myOkHttp = new MyOkHttp();
        if (call == null) {
            try {
                call = myOkHttp.newCall(request(session, url, paramsKeyAndValue));
            }catch (Exception e){
                App.me().toast("网络错误");
                e.printStackTrace();
            }
        } else {
            try {
                call.cancel();
                call = myOkHttp.newCall(request(session, url, paramsKeyAndValue));
            }catch (Exception e){
                App.me().toast("网络错误");
                e.printStackTrace();
            }

        }
        try {
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    onFinished();
                    onFailed(e, e.getMessage());
                    LogUtil.e( e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String re = response.body().string();
                    Log.d("reg", "My_Ok_Http.result" + re);
                    //session
                    Headers headers = response.headers();
                    List<String> cookies = headers.values("Set-Cookie");
                    String session = cookies.get(0);
                    String seesion = session.substring(0, session.indexOf(";"));
                    Log.i("max", "session is  :" + seesion);
                    SPUtils.put(App.me(),"session",seesion);
                    onSuccess(re);
                    onFinished();
                }
            });
        }catch (Exception e){
            onFinished();
            App.me().toast("网络错误");
            e.printStackTrace();
        }
        return call;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
    }

    public MyOkHttp() {
        dialog = null;
        dialogHandler = null;

    }

    protected MyOkHttp(Context context) {
        this(context, "加载中");

    }

    public MyOkHttp(Context context, String message) {
        if (context != null) {
            dialogHandler = new Handler();
            dialog = new ProgressDialog(context);
            dialog.setMessage(message);
            dialog.setOnCancelListener(this);
            dialog.setCanceledOnTouchOutside(false);
        }
    }

    public Request request(String session, String url, Object... paramsKeyAndValue) {
        showDialog();
        if (!url.startsWith(Constant.DOMAIN) && !url.startsWith("http://")) {
            if (url.startsWith("/") || Constant.DOMAIN.endsWith("/")) {
                url = Constant.DOMAIN + url;
            } else {
                url = Constant.DOMAIN + "/" + url;
            }
        }
        StringBuilder builder = null;
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        if (paramsKeyAndValue != null && paramsKeyAndValue.length != 0) {
            if (paramsKeyAndValue.length % 2 == 0) {
                builder = new StringBuilder();
                for (int i = 0; i < paramsKeyAndValue.length; i += 2) {
                    Object key = paramsKeyAndValue[i];
                    Object value = paramsKeyAndValue[i + 1];
                    if (key != null && value != null) {
                        bodyBuilder.add(key.toString(), value.toString());
                        if (builder.length() > 0) {
                            builder.append("&");
                        }
                    }
                    builder.append(key);
                    builder.append("=");
                    builder.append(value);
                }
            } else {
                throw new IllegalArgumentException();
            }
        }
        FormBody body = bodyBuilder.build();
        Request request = new Request.Builder()
                .addHeader("cookie", session)
                .addHeader("JSESSIONID",session)
                .url(url)
                .post(body)
                .build();
        if (builder != null && builder.length() > 0) {
            LogUtil.d(String.format("send：%s&%s", url, builder));
        } else {
            LogUtil.d(url);
        }
        return request;
    }

    public void onSuccess(String result) {
        LogUtil.d( result);
        try {
            ApiMsg apiMsg = JSON.parseObject(result, ApiMsg.class);
            onSuccess(apiMsg);
        } catch (Exception e) {
            onFailed(e, e.getMessage());
        }
    }

    public void onSuccess(ApiMsg apiMsg) {
    }

    public void onFailed(Exception e, String s) {
        Looper.prepare();
        if (e instanceof HttpException) {
            App.me().toast("网络不可用");
        } else if (e instanceof SocketTimeoutException) {
            App.me().toast("网络请求超时");
        } else if (e instanceof JSONException) {
            App.me().toast("数据解析错误");
        } else if (e instanceof NullPointerException) {
            App.me().toast("程序错误");
        } else if (s != null) {
            App.me().toast("未知错误：" + s);
        } else {
            App.me().toast("未知错误");
        }
    }

    public void showDialog() {
        if (dialog != null) {
            try {
                Log.d("reg", "showDialog???");
                dialogShowTime = System.currentTimeMillis();
                dialog.show();
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            } catch (Exception e) {
                LogUtil.e( e.getMessage());
            }
        }
    }

    public void onFinished() {
        if (dialogHandler != null && dialog != null && dialog.isShowing()) {
            dialogHandler.removeCallbacks(null);
            long delayMillis = System.currentTimeMillis() - dialogShowTime;
            if (delayMillis < 1000) {
                delayMillis = 1000;
            } else {
                delayMillis = 0;
            }
            dialogHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideDialog();
                }
            }, delayMillis);
        }
    }

    public void hideDialog() {
        if (dialog != null) {
            try {
                Log.d("reg", "hideDialog");
                dialog.dismiss();
            } catch (Exception e) {
                LogUtil.e(e.getMessage());
            }
        }
    }


}
