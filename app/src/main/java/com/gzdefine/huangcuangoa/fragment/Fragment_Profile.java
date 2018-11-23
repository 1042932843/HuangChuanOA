package com.gzdefine.huangcuangoa.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gzdefine.huangcuangoa.App;
import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.activity.AboutActivity;
import com.gzdefine.huangcuangoa.activity.SettingActivity;
import com.gzdefine.huangcuangoa.activity.UpdatePasswordActivity;
import com.gzdefine.huangcuangoa.activity.UserInfoActivity;
import com.gzdefine.huangcuangoa.entity.LoginResponse;
import com.gzdefine.huangcuangoa.util.Constant;
import com.gzdefine.huangcuangoa.util.Ok;
import com.gzdefine.huangcuangoa.util.SPUtils;
import com.gzdefine.huangcuangoa.view.CircleImageView;

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
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.gzdefine.huangcuangoa.util.ConfigKit.FILEPATH;
import static com.gzdefine.huangcuangoa.util.ConfigKit.FILEPATHIMG;
import static com.gzdefine.huangcuangoa.util.ConfigKit.IMGNAME;


//我
public class Fragment_Profile extends Fragment {

    private static final int REQUESTCODE_PICK = 0;        // 相册选图标记
    private static final int REQUESTCODE_CUTTING = 2;    // 图片裁切标记
    public Drawable drawable;
    private OkHttpClient okHttpClient;
    private Activity ctx;
    private View layout;
    @Bind(R.id.user_head)
    CircleImageView user_head;
    @Bind(R.id.user_name)
    TextView user_name;
    //    private  ProfileApi profileApi;
    private BpmControl bpmControl;
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
                            LoginResponse login = App.me().login();
                            if (login != null) {
                                App.me().login().setPhoto(data);
                            }
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
    private String data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (layout == null) {
            ctx = this.getActivity();
            layout = ctx.getLayoutInflater().inflate(R.layout.fragment_profile,
                    null);
//            profileApi = new ProfileApi(getActivity());
            bpmControl = new BpmControl(getActivity());
            okHttpClient = new OkHttpClient();
        } else {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null) {
                parent.removeView(layout);
            }
        }

        ButterKnife.bind(this, layout);
        final LoginResponse login = App.me().login();
        if (login != null) {
            initDate(login.getPhoto());
        }
//            LoginResponse login = App.me().login();
//            if (login.getUserId() != null) {
//                if (bpmControl != null) {
//                    bpmControl.gettype(login.getUserId());
//                }
//            }

        return layout;
    }


    @OnClick(R.id.user_head)
    public void updataHead() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
        // 如果要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(pickIntent, REQUESTCODE_PICK);

    }


    private void initDate(String data) {
        final LoginResponse login = App.me().login();
        if (login != null) {
            user_name.setText(login.getFullname());
            final FormBody body = new FormBody.Builder()
                    .add("thumb", "true")
                    .add("fileId", data)
                    .build();
            Request request = new Request.Builder()
                    .addHeader("cookie", (String) SPUtils.get(getActivity(), "session", ""))
                    .url(Constant.DOMAIN + "sys/core/file/imageView.do")
                    .post(body)
                    .build();
            Log.d("reg", "request:" + request);
            Call call2 = okHttpClient.newCall(request);
            String send = Constant.DOMAIN + "sys/core/file/imageView.do";
            for (int i = 0; i < body.size(); i++) {
                if (i == 0) {
                    send = send + "?" + body.encodedName(i) + "=" + body.encodedValue(i);
                } else {
                    send = send + "&" + body.encodedName(i) + "=" + body.encodedValue(i);
                }
            }
            Log.d("reg", "send: " + send);
            call2.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i("info_call2fail", e.toString());
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final byte bo[] = response.body().bytes();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                user_head.setImageBitmap(Bytes2Bimap(bo));
                            }
                        });
                    }
                    Headers headers = response.headers();
                    Log.i("info_respons.headers", headers + "");

                }
            });
        }
    }

    public Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

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
        super.onActivityResult(requestCode, resultCode, data);
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


    class BpmControl extends Ok {
        int what = 0;

        public BpmControl(Context context) {
            super(context);
        }


        public void getbpm(String file, String userId) {
            what = 0;
            super.post(Constant.DOMAIN + "mobileOlineController/imgUpload.do", "file", file, "userId", userId, "name", "android.jpg", "type", "tx");


        }

        public void gettype(String user) {
            what = 1;
            super.post(Constant.DOMAIN + "sys/core/file/imageView.do", "thumb", "true", "fileId", user);
//                String imageUrl = Constant.DOMAIN + "sys/core/file/imageView.do?thumb=true&fileId=" + login.getPhoto();
//                Log.d("reg","imageUrl:"+imageUrl);
//                user_name.setText(login.getFullname());
//                Glide.with(getActivity()).load(imageUrl).error(R.mipmap.big_default_photo).into(user_head);
        }

        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String re = response.body().string();
            Log.d("reg", "result:" + re);
            App.me().checklogin(getActivity(), re);
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

    // 预约护士信息接口
//    private class ProfileApi extends HttpUtil {
//
//
//        private ProfileApi(Context context) {
//            super(context);
//        }
//
//        private void Profile(String file, String userId) {
//            send(
//                    HttpRequest.HttpMethod.POST,
//                    "mobileOlineController/imgUpload.do",
//                    "file", file,
//                    "userId", userId,
//                    "name", "android.jpg"
//            );
//        }
//
//        @Override
//        public void onSuccess(ApiMsg apiMsg) {
//
//        }
//
//    }


    // 保存裁剪之后的图片数据
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        Log.d("reg", "extras:" + extras);
        if (extras != null) {
            // 取得SDCard图片路径做显示
            final Bitmap photo = extras.getParcelable("data");

            drawable = new BitmapDrawable(null, photo);
            Log.d("reg", "photo:" + photo);
            user_head.setImageDrawable(drawable);

            Log.d("RegOne", "FILEPATHIMG:" + FILEPATHIMG);
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
                //photo=URLEncoder.encode(photo,"UTF-8");
//                RequestParams params = new RequestParams();
//                params.put("file", mphoto);
//                params.put("userId", App.me().login().getUserId());
//                params.put("name", "android.jpg");
//                String url = Constant.DOMAIN + "mobileOlineController/imgUpload.do";
//                Log.d("url", "url:" + url);
                LoginResponse user = App.me().login();
                if (user.getUserId() != null) {
                    if (bpmControl != null) {
                        bpmControl.getbpm(mphoto, user.getUserId());
                    }
//                    if (profileApi!=null){
//                        profileApi.Profile(mphoto,user.getUserId());
//                    }
                }


//                AsyncHttpClient client = new AsyncHttpClient();
//                client.post(url, params, new AsyncHttpResponseHandler() {
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                        String responseData = new String();
//                        responseData = new String(responseBody);
//                        Log.d("RegOne", "responseData:" + responseData);
//                        EventBus.getDefault().post(FILEPATHIMG, "UserCenterActivity.onChangeHeader");
//                        try {
//                            JSONObject jsonObject = new JSONObject(responseData);
//                            int status = jsonObject.getInt("status");
//                            Log.d("RegOne", "status:" + status);
//                            if (status == 0) {
//                                Log.d("RegOne", "上传成功:" + status);
//                            }
//                        } catch (Exception e) {
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                        Log.i("RegOne", "upload failed");
//                        String responseData = new String();
//                        Log.d("reg","responseData:"+responseData);
//                        if (responseBody!=null){
//                            responseData = new String(responseBody);
//                            App.me().toast(responseData);
//                        }
//                    }
//                });

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

    @OnClick(R.id.user_info)
    void userinfo() {
        startActivity(new Intent(getActivity(), UserInfoActivity.class));
    }

    @OnClick(R.id.about)
    void about() {
        startActivity(new Intent(getActivity(), AboutActivity.class));
    }

    @OnClick(R.id.setting)
    void setting() {
        startActivity(new Intent(getActivity(), SettingActivity.class));
    }

    @OnClick(R.id.update_password)
    void update() {
        startActivity(new Intent(getActivity(), UpdatePasswordActivity.class));
    }


}