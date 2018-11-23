/*
 * Copyright © 2015 珠海云集软件科技有限公司.
 * Website：http://www.YunJi123.com
 * Mail：dev@yunji123.com
 * Tel：+86-0756-8605060
 * QQ：340022641(dove)
 * Author：dove
 */

package com.gzdefine.huangcuangoa.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class AppWebView extends WebView {

    private View customView;
    private WebChromeClient.CustomViewCallback customCallback;

    public AppWebView(Context context) {
        super(context);
        setup();
    }

    public AppWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public AppWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public AppWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
        setup();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setup() {
        WebSettings settings = getSettings();
        //支持javascript
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
// 设置可以支持缩放
        settings.setSupportZoom(false);
// 设置出现缩放工具
        settings.setBuiltInZoomControls(false);
//自适应屏幕
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setLoadWithOverviewMode(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        if (Build.VERSION.SDK_INT < 18) {
            settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        }
        setWebViewClient(new CustomWebViewClient());
        setWebChromeClient(new CustomWebChromeClient());
    }

    public void showCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        if (customCallback != null) {
            customCallback.onCustomViewHidden();
            customCallback = null;
        } else {
            // 设置全屏
            Activity activity = (Activity) getContext();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);
            }
            Window window = activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            // 视频置顶
            view.setBackgroundColor(Color.BLACK);
            window.addContentView(view, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));

            customView = view;
            customCallback = callback;
        }
    }

    public void hideCustomView() {
        if (customView != null) {
            // 取消全屏
            Activity activity = (Activity) getContext();
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            Window window = activity.getWindow();
            WindowManager.LayoutParams attrs = window.getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.setAttributes(attrs);
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            // 恢复布局
            ViewParent parent = customView.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(customView);
            }
            customView = null;
        }

        if (customCallback != null) {
            customCallback.onCustomViewHidden();
            customCallback = null;
        }
    }

    // 允许点击外链跳转浏览器
    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String temp = url.toLowerCase();
            if (!temp.startsWith("http://") && !temp.startsWith("https://")) {
                url = "http://" + url;
            }else if (url.indexOf("tel:")!=-1){
                Uri uri = Uri.parse(url);
                Intent it = new Intent(Intent.ACTION_DIAL, uri);
                //startActivity(it);
            }
            view.loadUrl(url);
            return true;
        }
    }

    // 允许全屏播放视频
    private class CustomWebChromeClient extends WebChromeClient {
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            showCustomView(view, callback);
        }

        @Override
        public void onHideCustomView() {
            hideCustomView();
        }
    }

}
