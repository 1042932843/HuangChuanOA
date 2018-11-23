package com.gzdefine.huangcuangoa.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.gzdefine.huangcuangoa.App;
import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.entity.LoginResponse;
import com.gzdefine.huangcuangoa.util.Constant;
import com.gzdefine.huangcuangoa.util.Ok;
import com.gzdefine.huangcuangoa.util.SPUtils;
import com.gzdefine.huangcuangoa.view.CustomWebView;
import com.gzdefine.huangcuangoa.view.PullToRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/6/9 0009.
 */
public class NewsdetailActivity extends BaseActivity implements View.OnClickListener, PtrHandler {
    public static final String REQUEST_ID = "id";
    public static final int REQUEST_CODE = 'n' + 'e' + 'w' + 's' + 'd' + 'e' + 't';
    private ContactsControl contactsControl;
    private ImageView back;
    private PullToRefreshLayout pull;
    private CustomWebView webView;
//    private TextView createDate;
//    private TextView title;
    private TextView txt_title;
//    private TextView authr;
//    private String newsDetail;
    private String id;
    public static final int UPDATE_Groups = 0;
    //handler
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case UPDATE_Groups:
                        //            // 登录成功, 保存用户登录信息, 持久化
                        try {
                            String re = (String) msg.obj;
                            JSONObject jsonObect = new JSONObject(re);
                            String success = jsonObect.getString("success");
                            String message = jsonObect.getString("message");

                            if (success.equals("true")) {
                                JSONObject data = jsonObect.getJSONObject("data");

                                String newsTitle = data.getString("SUBJECT_");
                                String createDateText = data.getString("CREATE_TIME_");
//                                newsDetail = data.getString("CONTENT_").replaceAll("src=\"/", "src=\"" + Constant.DOMAIN);
//                                Log.d("reg", "newsDetail:" + newsDetail);
                                String AUTHOR_ = data.getString("AUTHOR_");
                                if (null != AUTHOR_) {
//                                    authr.setText("发布人：" + AUTHOR_);
                                }

                                if (null != createDateText) {
//                                    createDate.setText(DateUtil.times(createDateText));
                                }
                                if (null != newsTitle) {
//                                    title.setText(newsTitle);
                                }
                                pull.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        pull.autoRefresh();
                                    }
                                });
                            } else {
                                App.me().toast(message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    });

    @Override
    public int initContentID() {
        return 0;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsdetail);
        contactsControl = new ContactsControl(this);
        assignViews();
        initViews();
        txt_title.setText("详情");
     /*   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            setTranslucentStatus(this, true);

        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        // 使用颜色资源

        tintManager.setStatusBarTintResource(R.color.zhuti);*/
        id = getIntent().getStringExtra(REQUEST_ID);
        User(id);

    }

    private void initViews() {
        back.setOnClickListener(this);
        pull.setPtrHandler(this);
        WebSettings settings = webView.getSettings();
        if (android.os.Build.VERSION.SDK_INT < 18) {
            settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                pull.refreshComplete();
            }
        });


    }

    private void assignViews() {
//        title = (TextView) findViewById(R.id.title);
        txt_title = (TextView) findViewById(R.id.txt_title);
//        createDate = (TextView) findViewById(R.id.createDate);
//        authr = (TextView) findViewById(R.id.authr);
        back = (ImageView) findViewById(R.id.img_back);
        pull = (PullToRefreshLayout) findViewById(R.id.pull);
        webView = (CustomWebView) findViewById(R.id.webView);

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

    @Override
    public void finish() {
        if (webView != null) {
            webView.loadUrl("about:blank");
        }
        super.finish();
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.isCustomViewShowing()) {
            webView.hideCustomView();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
        }
    }

    private void User(String id) {
        LoginResponse user = App.me().login();
        if (null != user) {
//            if (null != id) {
            contactsControl.Contacts(id);
//            }
//        } else {
//            if (null != id) {
//                newsdetailApi.Newsdetail("", id);
//            }
        }

    }


    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout ptrFrameLayout, View content, View header) {
        return webView.getScrollY() == 0 && PtrDefaultHandler.checkContentCanBePulledDown(ptrFrameLayout, content, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {


        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebChromeClient(new WebChromeClient());

        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        webView.setHorizontalScrollBarEnabled(false);

        webView.getSettings().setSupportZoom(false);

        webView.getSettings().setBuiltInZoomControls(false);

        webView.setHorizontalScrollbarOverlay(true);

        String seesion = (String) SPUtils.get(App.me(), "session", "");
        syncCookie(this, Constant.DOMAIN+"oa/info/insNews/get.do?permit=no&pkId="+id, seesion);
        webView.getSettings().setBlockNetworkImage(false);
//        webView.loadDataWithBaseURL(null, newsDetail, "text/html", "utf-8", null);
        webView.loadUrl(Constant.DOMAIN+"oa/info/insNews/get.do?permit=no&pkId="+id);
    }

    public void syncCookie(Context context, String url, String jessionid) {
        try {
            CookieSyncManager.createInstance(context);
            CookieSyncManager.getInstance().startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            //有测试发现，清空cookie的操作会造成，cookie偶然性失效的现象
            cookieManager.removeSessionCookie();// 移除
            cookieManager.removeAllCookie();
            String oldCookie = cookieManager.getCookie(url);
       /* StringBuilder sbCookie = new StringBuilder();
       sbCookie.append(String.format("JSESSIONID=%s", jessionid));
        String cookieValue = sbCookie.toString();*/


            cookieManager.setCookie(Constant.DOMAIN + "mobile/contacts/findNewById.do?newsId=" + id, jessionid);

            String newCookie = cookieManager.getCookie(Constant.DOMAIN + "mobile/contacts/findNewById.do?newsId=" + id);
            if (newCookie != null) {
                //这里讲newCookie本地保存
            }
            CookieSyncManager.getInstance().sync();
        } catch (Exception e) {
        }
    }
//    private class NewsdetailApi extends HttpUtil {
//
//        private NewsdetailApi(Context context) {
//            super(context); // 传递上下文, 初始化进度对话框
//        }
//
//
//        public void Newsdetail(String uuid, String newsId) {
//
//            send(HttpRequest.HttpMethod.POST,
//                    "http://192.168.1.42:9090/jsaas/mobile/contacts/findNewById.do",
//                    "newsId", "2400000007220290"
//            );
//
//
//        }
//
//        @Override
//        public void onSuccess(ApiMsg apiMsg) {
//
//            // 登录成功, 保存用户登录信息, 持久化
//            String resultInfo = apiMsg.getResult();
//            try {
//                JSONObject jsonObect = new JSONObject(resultInfo);
//                String success = jsonObect.getString("success");
//
//                if (success.equals("true")) {
//                    JSONObject data = jsonObect.getJSONObject("data");
//
//                    String newsTitle = data.getString("SUBJECT_");
//                    String createDateText = data.getString("CREATE_TIME_");
//                    newsDetail = data.getString("CONTENT_");
//
//                    String AUTHOR_ = data.getString("AUTHOR_");
//                    if (null != AUTHOR_) {
//                        authr.setText("发布人：" + AUTHOR_);
//                    }
//
//                    if (null != createDateText) {
//                        createDate.setText(DateUtil.times(createDateText));
//                    }
//                    if (null != newsTitle) {
//                        title.setText(newsTitle);
//                    }
//                    pull.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            pull.autoRefresh();
//                        }
//                    });
//                } else {
//                    App.me().toast(apiMsg.getMessage());
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
//    }

    class ContactsControl extends Ok {
        int what = 0;

        public ContactsControl(Context context) {
            super(context);
        }


        public void Contacts(String newsId) {
            what = UPDATE_Groups;
            super.post(Constant.DOMAIN + "mobile/contacts/findNewById.do?", "newsId", newsId);
        }

        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String re = response.body().string();
            Log.d("reg", "result:" + re);
            App.me().checklogin(NewsdetailActivity.this, re);
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





