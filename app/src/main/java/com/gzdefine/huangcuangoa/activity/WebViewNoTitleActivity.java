package com.gzdefine.huangcuangoa.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.gzdefine.huangcuangoa.App;
import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.entity.LoginResponse;
import com.gzdefine.huangcuangoa.util.Constant;
import com.gzdefine.huangcuangoa.util.SPUtils;
import com.gzdefine.huangcuangoa.view.AppWebView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WebViewNoTitleActivity extends BaseActivity {
    public static final int REQUEST_CODE = 'w' + 'b' + 'e' + 'v' + 'i' + 'e' + 'w' + 't' + 'i' + 't' + 'l';
    @Bind(R.id.webView)
    AppWebView mWebView;
    //    Handler handler = new Handler();
    ProgressDialog dialog = null;
    @Bind(R.id.img_back)
    ImageView imgBack;

    boolean islogin;

    @Override
    public int initContentID() {
        return 0;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_title_web_view);
        ButterKnife.bind(this);
        LoginResponse login = App.me().login();
        if (null != login) {
            String type = getIntent().getStringExtra("type"); // 1是邮件 2是待办
            if (type.equals("1")) {
                App.me().clearmsgcount("4");
            } else if (type.equals("2")) {
                App.me().clearmsgcount("3");
            }
            ButterKnife.bind(this);
            String mark = getIntent().getStringExtra("mark");
            String url = getIntent().getStringExtra("url");

            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mWebView.canGoBack()) {
                        mWebView.goBack();
                    } else {
                        onBackPressed();
                    }
                }
            });
            //cookies同一个会话
//cookies同一个会话
            CookieSyncManager.createInstance(this);
            // 设置可以支持缩放
            mWebView.getSettings().setSupportZoom(false);
// 设置出现缩放工具
            mWebView.getSettings().setBuiltInZoomControls(false);
//扩大比例的缩放
            mWebView.getSettings().setUseWideViewPort(false);
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.getSettings().setDomStorageEnabled(true);
            CookieManager cookieManager = CookieManager.getInstance();
            String cookieString = (String) SPUtils.get(this, "session", "");
            if(cookieString!=null&&!cookieString.equals("")){
                islogin=true;
            }
            cookieManager.setCookie(url, cookieString);
            CookieSyncManager.getInstance().sync();
            mWebView.loadUrl(url);
            mWebView.setDownloadListener(new MyWebViewDownLoadListener());
            mWebView.addJavascriptInterface(new Object() {
                @JavascriptInterface
                public void goBack() {
                    Log.d("reg", "goBack");
                    finish();
                }


                @JavascriptInterface
                public void reload() {
                    startActivity(new Intent(WebViewNoTitleActivity.this, LoginActivity.class));
                    App.me().finishAll();
                }
            }, "android");

            mWebView.addJavascriptInterface(new Object() {
                @JavascriptInterface
                public void back() {
                    finish();
                }
            }, "ba");

            mWebView.addJavascriptInterface(new Object() {
                @JavascriptInterface
                public void jump() {
                    Intent i = new Intent(WebViewNoTitleActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }, "dd");

            mWebView.setWebChromeClient(new WebChromeClient() {

                @Override
                public void onReceivedTitle(WebView view, String title) {
                }

                @Override
                public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                    App.me().toast(message);
                    result.confirm();
                    return true;
                }

            });
            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                // 在点击请求的是链接是才会调用，重写此方法返回true表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边。这个函数我们可以做很多操作，比如我们读取到某些特殊的URL，于是就可以不打开地址，取消这个操作，进行预先定义的其他操作，这对一个程序是非常必要的。
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                    // 判断url链接中是否含有某个字段，如果有就执行指定的跳转（不执行跳转url链接），如果没有就加载url链接
                   /* if (url.equals("http://218.29.203.38:8088/vuemobile/index.html#/")||url.equals("http://183.47.14.182:9079/jsaas/vuemobile/index.html#/")) {
                        Intent i = new Intent(WebViewNoTitleActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                        return true;
                    } else {
                        return true;
                    }*/
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {

                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public void onLoadResource(WebView view, String url){

                }

                //页面加载完成
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    String realUrl = url;
                    /*if (url.equals(Constant.DOMAIN+"vuemobile/index.html#/")||url.equals(Constant.DOMAIN+"vuemobile/index.html#/")) {
                        Intent i = new Intent(WebViewNoTitleActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }*/
                    CookieManager cm = CookieManager.getInstance();
                    String cookies = cm.getCookie(realUrl);
                   if(cookies!=null&&!cookies.equals("")){

                    }else{
                        //这个realUrl即为重定向之后的地址

                    }

                }

            });

        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }


//要实现WebView文件下载，实现这个监听就ok
//        mWebView.setDownloadListener(new DownloadListener() {
//                                                @Override
//                                                public void onDownloadStart(String
//                                                                                    url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
//                                                    Log.v("ldm", url);
//                                                    if (url.endsWith(".apk")) {//判断是否是.apk结尾的文件路径
//                                                        new DownLoadThread(url, WebViewNoTitleActivity.this).start();
//                                                    }
//                                                }
//                                            });
//        mWebView.setDownloadListener(new DownloadListener() {
//
//                                                @Override
//                                                public
//                                                void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
//                                                    Log.d("reg",url);
//                                                    try {
//                                                       InputStream in = null;
//                                                        URL httpUrl = new URL(url);
//                                                        HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
//                                                        conn.setDoInput(true);// 如果打算使用 URL 连接进行输入，则将 DoInput 标志设置为 true；如果不打算使用，则设置为 false。默认值为 true。
//                                                        conn.setDoOutput(true);// 如果打算使用URL 连接进行输出，则将 DoOutput 标志设置为 true；如果不打算使用，则设置为 false。默认值为 false。
//                                                        in = conn.getInputStream();
//                                                        in.
////                                                    Uri  uri= Uri.parse(url);
////                                                    Intent   intent=new Intent(Intent.ACTION_VIEW, uri);
////                                                    startActivity(intent);
//                                                    }catch (Exception e) {
//                                                        e.printStackTrace();
//                                                    }
//                                                }
//                                            });
//        try {
//            String[] strarray = url.split("fj=");
//            String str = new String(strarray[1].getBytes(), "UTF-8");
//            String str11 = URLEncoder.encode(str, "UTF-8");
//            String urltrue=strarray[0]+"fj="+str11;
//            Log.d("reg", "urltrue:" + urltrue);
//
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }


    }

    @Override
    public void finish() {
        if (mWebView != null) {
            mWebView = null;
        }
        super.finish();
    }

    private void checkLogin() {
        LoginResponse login = App.me().login();
        if (null == login) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    //内部类
    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Toast t = Toast.makeText(App.me(), "需要SD卡。", Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
                return;
            }
            Log.d("reg", "urlurl:" + url);
            DownloaderTask task = new DownloaderTask();
            task.execute(url);
        }

    }


    //内部类
    private class DownloaderTask extends AsyncTask<String, Void, String> {

        public DownloaderTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            HttpURLConnection conn = null;
            String fileName = "";
            try {
                final String url = params[0];
                Log.d("reg", "url:" + url);
//            downFile(url);
                URL myURL = new URL(url);
                conn = (HttpURLConnection) myURL.openConnection();
                String seesion = (String) SPUtils.get(App.me(), "session", "");
                conn.addRequestProperty("Cookie", seesion);
                conn.connect();
                conn.getResponseCode();
                URL absUrl = conn.getURL();
                fileName = conn.getHeaderField("Content-Disposition");
                if (fileName == null || fileName.length() < 1) {
                    fileName = absUrl.getFile();
                }
                InputStream in = conn.getInputStream();

                fileName = URLDecoder.decode(fileName, "UTF-8");
                fileName = fileName.substring(fileName.indexOf("filename=") + "filename=".length());

//
//            File directory=Environment.getExternalStorageDirectory();
//            File file=new File(directory,fileName);
//            if(file.exists()){
//                Log.i("tag", "The file has already exists.");
//                return fileName;
//            }
//                downLoadFile(url, Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
//                        "FileDownloader", fileName);
                writeToSDCard(fileName, in);
                in.close();

                return fileName;
//                HttpClient client = new DefaultHttpClient();
//                client.getParams().setIntParameter("http.socket.timeout",3000);//设置超时
//                HttpGet get = new HttpGet(url);
//                HttpResponse response = client.execute(get);
//                if(HttpStatus.SC_OK==response.getStatusLine().getStatusCode()){
//                    HttpEntity entity = response.getEntity();
//                    InputStream input = entity.getContent();
//
//
//
//                    input.close();
//                  entity.consumeContent();
//                    return fileName;
//                }else{
//                    return null;
//                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onCancelled() {
            // TODO Auto-generated method stub
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            closeProgressDialog();
            if (result == null) {
                Toast t = Toast.makeText(App.me(), "连接错误！请稍后再试！", Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
                return;
            }

//            Toast t = Toast.makeText(App.me(), "已保存到SD卡。", Toast.LENGTH_LONG);
//            t.setGravity(Gravity.CENTER, 0, 0);
//            t.show();
            File directory = Environment.getExternalStorageDirectory();
            Log.d("reg", "result:" + result);
            File file = new File(directory, result);
            Log.d("reg", "file:" + file);
            openFile(file);

//            startActivity(intent);

        }
//
//        @Override
//        protected void onPreExecute() {
//            // TODO Auto-generated method stub
//            super.onPreExecute();
//            showProgressDialog();
//        }

        @Override
        protected void onProgressUpdate(Void... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
        }


    }

    public static void makeRootDirectory(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {

        }
    }

    /**
     * 下载文件方法
     *
     * @param fileUrl     文件url
     * @param destFileDir 存储目标目录
     */
    public void downLoadFile(String fileUrl, final String destFileDir, String fileName) throws IOException {

        makeRootDirectory(destFileDir);

        int index = fileUrl.lastIndexOf("/");

        File file = new File(destFileDir + fileUrl.substring(index, fileUrl.length()));


        Log.d("reg", "file:" + file);
        //文件判断
        if (file.exists()) {
            file.delete();
            file = new File(destFileDir, fileName);
        }
        //下载
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(fileUrl).build();
        Call call = okHttpClient.newCall(request);
        final File finalFile = file;
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("reg", "下载失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = -1;
                FileOutputStream fos = null;
                try {
                    long total = response.body().contentLength();
                    long current = 0;
                    is = response.body().byteStream();
                    fos = new FileOutputStream(finalFile);
                    while ((len = is.read(buf)) != -1) {
                        current += len;
                        fos.write(buf, 0, len);
                        onDown(1, (int) (current / (float) total * 100));
//                            Log.d("reg", "current ： " + current);
//                            Log.d("reg", "total ： " + (int) (current / (float) total * 100));
                    }
                    fos.flush();
                    onDown(2, 0);
                    Log.d("reg", "下载完成");
                    openFile(finalFile);
                } catch (IOException e) {
//                        Log.e(TAG, e.toString());
                    onDown(-1, 0);
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        Log.e("err", e.toString());
                    }
                }
            }
        });


    }


    public void onDown(int state, long num) {
        Message message = new Message();
        message.arg1 = state;
        message.arg2 = (int) num;
        handler.sendMessage(message);


    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == 1) {
//                dialog = ProgressDialog.show(WebViewNoTitleActivity.this, null, "下载中，请稍后..");
                showProgressDialog();
            } else if (msg.arg1 == 2) {
//                App.me().toast("下载完成");
            } else if (msg.arg1 == -1) {
                App.me().toast("下载失败");
            }

        }
    };
    private ProgressDialog mDialog;

    private void showProgressDialog() {
        if (mDialog == null) {
            mDialog = new ProgressDialog(this);
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置风格为圆形进度条
            mDialog.setMessage("下载中，请稍后..");
            mDialog.setIndeterminate(false);//设置进度条是否为不明确
            mDialog.setCancelable(true);//设置进度条是否可以按退回键取消
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    // TODO Auto-generated method stub
                    mDialog = null;
                }
            });
            mDialog.show();

        }
    }

    private void closeProgressDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    public void openFile(File file) {
        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //设置intent的Action属性
            intent.setAction(Intent.ACTION_VIEW);
            //获取文件file的MIME类型
            String type = getMIMEType(file);
            //设置intent的data和Type属性。
            Uri docUri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                docUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
            }else{
                docUri=Uri.fromFile(file);
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(/*uri*/docUri, type);
            //跳转
            startActivity(intent);
//
        } catch (ActivityNotFoundException e) {
            // TODO: handle exception
            App.me().toast("附件不能打开，请下载office相关软件！");
        }
    }


    public Intent getFileIntent(File file) {
//       Uri uri = Uri.parse("http://m.ql18.com.cn/hpf10/1.pdf");
        Uri uri = Uri.fromFile(file);
        String type = getMIMEType(file);
        Log.i("tag", "type=" + type);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uri, type);
        return intent;
    }

    public void writeToSDCard(String fileName, InputStream input) {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File directory = Environment.getExternalStorageDirectory();
            File file = new File(directory, fileName);
            if (file.exists()) {
                file.delete();
                file = new File(directory, fileName);
            }
            try {

                FileOutputStream fos = new FileOutputStream(file);
                long current = 0;
                byte[] b = new byte[2048];
                int j = 0;
                while ((j = input.read(b)) != -1) {
                    fos.write(b, 0, j);
                    fos.flush();
                    onDown(1, (int) (current / (float) b.length * 100));
                }
                onDown(2, 0);
                fos.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                onDown(-1, 0);
            }
        } else {
            Log.i("tag", "NO SDCard.");
        }
    }

    private static String getMIMEType(File file) {

        String type = "*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
       /* 获取文件的后缀名*/
        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "") return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < MIME_MapTable.length; i++) {


            if (end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    // 可以自己随意添加
    private static String[][] MIME_MapTable = {
            //{后缀名，MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.Android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop", "text/plain"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/x-zip-compressed"},
            {"", "*/*"}
    };

}
