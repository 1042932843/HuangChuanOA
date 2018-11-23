package com.gzdefine.huangcuangoa.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.gzdefine.huangcuangoa.App;
import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.entity.LoginResponse;
import com.gzdefine.huangcuangoa.util.Constant;
import com.gzdefine.huangcuangoa.util.Ok;
import com.gzdefine.huangcuangoa.util.SPUtils;
import com.gzdefine.huangcuangoa.view.AppWebView;

import org.json.JSONObject;
import org.simple.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

import static com.gzdefine.huangcuangoa.util.ConfigKit.FILEPATH;
import static com.gzdefine.huangcuangoa.util.ConfigKit.FILEPATHIMG;
import static com.gzdefine.huangcuangoa.util.ConfigKit.IMGNAME;


public class WebViewActivity extends BaseActivity {

    @Bind(R.id.webView)
    AppWebView mWebView;
    @Bind(R.id.txt_title)
    TextView mTitle;
    private Dialog progressDialog;

    @Override
    public int initContentID() {
        return 0;
    }

    private String endIds;
    private String sendNamess;
    private static final int REQUESTCODE_PICK = 0;        // 相册选图标记
    private static final int REQUESTCODE_CUTTING = 2;    // 图片裁切标记
    private BpmControl bpmControl;
    private String data;
    public Drawable drawable;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    EventBus.getDefault().post(FILEPATHIMG, "UserCenterActivity.onChangeHeader");
                    try {
                        JSONObject jsonObject = new JSONObject("" + msg.obj);
                        String success = jsonObject.getString("success");
                        String message = jsonObject.getString("message");
                        if (success.equals("true")) {
                            Log.d("reg", "上传:" + jsonObject);
                            data = jsonObject.getString("data");
                            mWebView.post(new Runnable() {
                                @Override
                                public void run() {
                                    // 注意调用的JS方法名要对应上
                                    // 调用javascript的callJS()方法
//                mWebView.loadUrl("javascript:showAndroid(1)");
//                    mWebView.loadUrl("javascript:showAndroid('" + endIds + "'," + sendNamess + ")");
                                    //该注解一定要加让Javascript可以访问
//                    mWebView.loadUrl("javascript:showAndroid('" + endIds + "'," + sendNamess + ")");
                                    mWebView.loadUrl("javascript:data('" + data + "')");
                                }
                            });
                        } else {
                            App.me().toast(message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
//                    LoginResponse login = App.me().login();
//                    if (login != null) {
//                        String imageUrl = Constant.DOMAIN + "sys/core/file/imageView.do?thumb=true&fileId=" + login.getPhoto();
//                        user_name.setText(login.getFullname());
//                        Glide.with(getActivity()).load(imageUrl).error(R.mipmap.big_default_photo).into(user_head);
//                    }
                    break;
            }
            return false;
        }
    }

    );
    String typeID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new Dialog(WebViewActivity.this, R.style.progress_dialog);
        progressDialog.setContentView(R.layout.progress);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView msg = (TextView) progressDialog.findViewById(R.id.id_tv_loadingmsg);
        msg.setText("正在加载中");
        progressDialog.show();
        bpmControl = new BpmControl(WebViewActivity.this);

        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);
        String mark = getIntent().getStringExtra("mark");
        //cookies同一个会话
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        String cookieString = (String) SPUtils.get(this, "session", "");
        Log.d("max", "cookie:" + cookieString);
        String url = getIntent().getStringExtra("url");
        typeID=getIntent().getStringExtra("typeID");
        cookieManager.setCookie(url, cookieString);
        CookieSyncManager.getInstance().sync();
        Log.d("maxWebUrl", "url:" + url);
//        mWebView.loadUrl("http://192.168.1.152/DY_GRID/webpage/hcoah5/sick_leave.html");
        mWebView.loadUrl(url);

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            // 在点击请求的是链接是才会调用，重写此方法返回true表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边。这个函数我们可以做很多操作，比如我们读取到某些特殊的URL，于是就可以不打开地址，取消这个操作，进行预先定义的其他操作，这对一个程序是非常必要的。
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d("max", "将加载:" + url);
                super.onPageStarted(view, url, favicon);
            }



            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("max", "加载完成:" + url);
                mTitle.setText(view.getTitle());
                progressDialog.dismiss();
                if (url.equals(Constant.DOMAIN+"vuemobile/index.html#/")||url.equals(Constant.DOMAIN+"vuemobile/index.html#/")) {
                    Intent i = new Intent(WebViewActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });

        mWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void txl() {
                Intent intent = new Intent(WebViewActivity.this, ProcessActivity.class);
                intent.putExtra("Pro", "1");
                intent.putExtra("typeID",typeID);
                startActivityForResult(intent, ProcessActivity.REQUEST_CODE);
            }
        }, "Android");


        mWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void choice() {
                updataHead();
            }
        }, "And");

        mWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void goBack() {
                finish();
            }

            @JavascriptInterface
            public void reload() {
                startActivity(new Intent(WebViewActivity.this, LoginActivity.class));
                finish();
            }
        }, "android");
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                mTitle.setText(title);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                App.me().toast(message);
                result.confirm();
                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.img_back)
    void onBack() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            onBackPressed();
        }
    }

    public void updataHead() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
        // 如果要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(pickIntent, REQUESTCODE_PICK);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ProcessActivity.REQUEST_CODE && resultCode == RESULT_OK) {
            endIds = data.getStringExtra("id");
            sendNamess = data.getStringExtra("name");
            Log.d("reg", "返回endIds:" + endIds);
            Log.d("reg", "返回sendNamess:" + sendNamess);
            // 必须另开线程进行JS方法调用(否则无法调用)
            // 必须另开线程进行JS方法调用(否则无法调用)
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    // 注意调用的JS方法名要对应上
                    // 调用javascript的callJS()方法
//                mWebView.loadUrl("javascript:showAndroid(1)");
//                    mWebView.loadUrl("javascript:showAndroid('" + endIds + "'," + sendNamess + ")");
                    //该注解一定要加让Javascript可以访问
//                    mWebView.loadUrl("javascript:showAndroid('" + endIds + "'," + sendNamess + ")");
                    mWebView.loadUrl("javascript:showAndroid('" + sendNamess + "','" + endIds + "')");
                }
            });
        }
        switch (requestCode) {
            case REQUESTCODE_PICK:// 直接从相册获取
                try {
                    startPhotoZoom(data.getData());
                } catch (NullPointerException e) {
                    e.printStackTrace();// 用户点击取消操作
                }
                break;
            case REQUESTCODE_CUTTING:// 取得裁剪后的图片
                if (data != null) {
                    setPicToView(data);
                }
                break;
        }
    }

    // 裁剪图片方法实现
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    // 保存裁剪之后的图片数据
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        Log.d("reg", "extras:" + extras);
        if (extras != null) {
            // 取得SDCard图片路径做显示
            final Bitmap photo = extras.getParcelable("data");
            drawable = new BitmapDrawable(null, photo);
            storeImageToSDCARD(photo, IMGNAME, FILEPATH);
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                //将bitmap一字节流输出 Bitmap.CompressFormat.PNG 压缩格式，100：压缩率，baos：字节流
                photo.compress(Bitmap.CompressFormat.PNG, 60, baos);
                baos.close();
                byte[] buffer = baos.toByteArray();
                Log.d("RegOne", "图片的大小：" + buffer.length);

                //将图片的字节流数据加密成base64字符输出
                String mphoto = Base64.encodeToString(buffer, 0, buffer.length, Base64.DEFAULT);
                Log.d("reg", "mphoto:" + mphoto);
                LoginResponse user = App.me().login();
                if (user.getUserId() != null) {
                    if (bpmControl != null) {
                        bpmControl.getbpm(mphoto, user.getUserId());
                    }

                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // storeImageToSDCARD 将bitmap存放到sdcard中
    public void storeImageToSDCARD(Bitmap colorImage, String ImageName, String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        File imagefile = new File(file, ImageName + ".jpg");
        Log.d("reg", "imagefile:" + imagefile);
        try {
            imagefile.createNewFile();
            FileOutputStream fos = new FileOutputStream(imagefile);
            colorImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class BpmControl extends Ok {
        int what = 0;

        public BpmControl(Context context) {
            super(context);
        }


        public void getbpm(String file, String userId) {
            what = 0;
            super.post(Constant.DOMAIN + "mobileOlineController/imgUpload.do", "file", file, "userId", userId, "name", "android_" + getTime() + ".jpg", "type", "tp");
        }

        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String re = response.body().string();
            Log.d("reg", "result:" + re);
            App.me().checklogin(WebViewActivity.this, re);
            //发消息更新UI
            try {
                Message msg = new Message();
                msg.what = what;
                msg.obj = re;
                handler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public String getTime() {
        long time = System.currentTimeMillis() / 1000;//获取系统时间的10位的时间戳
        String str = String.valueOf(time);
        return str;
    }
}
