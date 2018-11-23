

package com.gzdefine.huangcuangoa.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.gzdefine.huangcuangoa.App;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.net.SocketTimeoutException;

/**
 * Http请求工具类
 */
public abstract class HttpUtil extends RequestCallBack<String> implements DialogInterface.OnCancelListener {

    private static final HttpUtils HTTP = new HttpUtils();

    public static String getImageUrl(String url) {
        if (url != null && url.length() > 0 && !url.startsWith(Constant.DOMAIN) && !url.startsWith("http://")) {
            if (url.startsWith("/")) {
                url = Constant.DOMAIN + url;
            } else {
                url = Constant.DOMAIN + "/" + url;
            }
        }
        if (url != null && (url = url.trim()).length() > 0) {
            url = url.replaceAll("\\\\", "/");
            return url;
        }
        return null;
    }

    protected Handler dialogHandler;
    protected ProgressDialog dialog;
    protected long dialogShowTime;
    protected HttpHandler httpHandler;

    protected HttpUtil() {
        dialog = null;
        dialogHandler = null;
    }

    protected HttpUtil(Context context) {
        this(context, "加载中...");
    }

    protected HttpUtil(Context context, String message) {
        if (context != null) {
            dialogHandler = new Handler();
            dialog = new ProgressDialog(context);
            dialog.setMessage(message);
            dialog.setOnCancelListener(this);
        }
    }

    public void Setnullofdialog() {
        dialog = null;
    }

    public final HttpHandler getHttpHandler() {
        return httpHandler;
    }

    public final HttpHandler send(HttpRequest.HttpMethod method, String url, Object... paramsKeyAndValue) {
        if (!url.startsWith(Constant.DOMAIN) && !url.startsWith("http://")) {
            if (url.startsWith("/")) {
                url = Constant.DOMAIN + url;
            } else {
                url = Constant.DOMAIN + "/" + url;
            }
        }
        RequestParams params = null;
        StringBuilder builder = null;
        if (paramsKeyAndValue != null && paramsKeyAndValue.length != 0) {
            if (paramsKeyAndValue.length % 2 == 0) {
                params = new RequestParams();
                builder = new StringBuilder();
                for (int i = 0; i < paramsKeyAndValue.length; i += 2) {
                    Object key = paramsKeyAndValue[i];
                    Object value = paramsKeyAndValue[i + 1];
                    if (key != null && value != null) {
                        params.addBodyParameter(key.toString(), value.toString());
                        if (builder.length() > 0) {
                            builder.append("&");
                        }
                        builder.append(key);
                        builder.append("=");
                        builder.append(value);
                    }
                }
            } else {
                throw new IllegalArgumentException();
            }
        }
        if (httpHandler != null) {
            httpHandler.cancel();
        }
        if (builder != null && builder.length() > 0) {
            LogUtil1.d(getClass(), String.format("send：%s?%s", url, builder));
        } else {
            LogUtil1.d(getClass(), "send：" + url);
        }
        return httpHandler = HTTP.send(method, url, params, this);
    }

    @Override
    public final void onSuccess(ResponseInfo<String> result) {
        onSuccess(result.result);
        onFinished();
    }

    public void onSuccess(String result) {
        LogUtil1.d(getClass(), result);
        Log.d("reg", "result:" + result);
        try {
            ApiMsg apiMsg = JSON.parseObject(result, ApiMsg.class);
            String state = apiMsg.getState();
            if (state != null && state.equals("0001")) {
//                App.me().setUser(null);
            }
            onSuccess(apiMsg);
        } catch (Exception e) {
            onFailure(e, e.getMessage());
        }
    }

    public void onSuccess(ApiMsg apiMsg) {

    }

    @Override
    public final void onFailure(HttpException e, String s) {
        onFailure((Exception) e, s);
        onFinished();
    }

    public void onFailure(Exception e, String s) {
        LogUtil1.e(getClass(), s, e);

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

    @Override
    public void onStart() {
        showDialog();
    }

    @Override
    public void onCancelled() {
        onFinished();
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

    @Override
    public void onCancel(DialogInterface dialog) {
        if (httpHandler != null) {
            httpHandler.cancel();
        }
    }

    public void showDialog() {
        if (dialog != null) {
            try {
                dialogShowTime = System.currentTimeMillis();
                dialog.show();
            } catch (Exception e) {
                LogUtil1.e(HttpUtil.class, e.getMessage(), e);
            }
        }
    }

    public void hideDialog() {
        if (dialog != null) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                LogUtil1.e(HttpUtil.class, e.getMessage(), e);
            }
        }
    }


}
