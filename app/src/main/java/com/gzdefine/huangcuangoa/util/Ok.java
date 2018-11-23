package com.gzdefine.huangcuangoa.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.alibaba.fastjson.JSONException;
import com.gzdefine.huangcuangoa.App;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * TODO：
 * MJS团队
 * Created by Max on 2017/3/16.
 */

public abstract class Ok implements DialogInterface.OnCancelListener {

    protected ProgressDialog dialog;
    protected Handler dialogHandler;
    protected long dialogShowTime;
    protected RequestHandle requestHandle;
    OkHttpClient okHttpClient = new OkHttpClient();
    List<String> keys;
    List<String> values;
    private final static AsyncHttpClient ASYNC_HTTP_CLIENT = new AsyncHttpClient();

    //加seesion
    public final void post(String url, Object... paramsKeyAndValue) {
        showDialog();
        keys = new ArrayList<>();
        values = new ArrayList<>();
        StringBuilder builder = null;
        if (paramsKeyAndValue != null && paramsKeyAndValue.length != 0) {
            if (paramsKeyAndValue.length % 2 == 0) {
                builder = new StringBuilder();
                for (int i = 0; i < paramsKeyAndValue.length; i += 2) {
                    Object key = paramsKeyAndValue[i];
                    Object value = paramsKeyAndValue[i + 1];
                    if (key != null && value != null) {
                        builder.append(key);
                        builder.append("=");
                        builder.append(value);
                        keys.add("" + key);
                        values.add("" + value);
                    }
                }
            } else {
                throw new IllegalArgumentException();
            }
        }
        if (requestHandle != null) {
            requestHandle.cancel(true);
        }

        String seesion = (String) SPUtils.get(App.me(), "session", "");
        Log.i("info_Login", "得到了session：" + seesion);
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        for (int i = 0; i < keys.size(); i++) {
            bodyBuilder = bodyBuilder.add(keys.get(i), values.get(i));
        }
        FormBody body = bodyBuilder.build();



        Request request;
        if (url.indexOf("login.do") != -1) {
            Log.d("reg","1");
            request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
        } else {
            Log.d("reg","2");
            if (seesion != "") {
                request = new Request.Builder()
                        .addHeader("cookie", seesion)
                        .url(url)
                        .post(body)
                        .build();
            } else {
                request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
            }
        }
        String send = url;
        for (int i = 0; i < body.size(); i++) {
            if (i == 0) {
                send = send + "?" + body.encodedName(i) + "=" + body.encodedValue(i);
            } else {
                send = send + "&" + body.encodedName(i) + "=" + body.encodedValue(i);
            }
        }
        Log.d("max", "send: " + send);

        Call call2 = okHttpClient.newCall(request);

        call2.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onFinish();
                Ok.this.onFailure(e);
                Ok.this.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    onFinish();
                    Ok.this.onResponse(call, response);
                } catch (Exception e) {
                    Log.e("OK", "e" + e.getMessage());
                }
            }
        });
    }


    public abstract void onFailure(Call call, IOException e);

    public abstract void onResponse(Call call, Response response) throws IOException;


    public Ok() {
        dialog = null;
        dialogHandler = null;
    }

    protected Ok(Context context) {
        this(context, "加载中...");
    }

    protected Ok(Context context, String message) {
        if (context != null) {
            dialogHandler = new Handler();
            dialog = new ProgressDialog(context);
            dialog.setMessage(message);
            dialog.setOnCancelListener(this);
        }
    }


    public void onFailure(Exception e) {
        LogUtil.e(e.toString());
        Looper.prepare();
        if (e instanceof HttpException) {
            App.me().toast("网络不可用");
        } else if (e instanceof SocketTimeoutException) {
            App.me().toast("网络请求超时");
        } else if (e instanceof JSONException) {
            App.me().toast("数据解析错误");
        } else if (e instanceof NullPointerException) {
            App.me().toast("程序错误");
        } else if (e instanceof ConnectException) {
            App.me().toast("服务器异常或者检查你的网络是否正常");
        } else {
            App.me().toast("未知错误");
        }
        Looper.loop();
    }


    public void onFinish() {
        onFinished();
    }


    @Override
    public void onCancel(DialogInterface dialog) {
        onFinish();
    }

    public void showDialog() {
        if (dialog != null) {
            try {
                dialogShowTime = System.currentTimeMillis();
                dialog.show();
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            } catch (Exception e) {
                LogUtil.e(e.getMessage());
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
                dialog.dismiss();
            } catch (Exception e) {
                LogUtil.e(e.getMessage());
            }
        }
    }

}
