package com.gzdefine.huangcuangoa;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gzdefine.huangcuangoa.Service.WebSocketService;
import com.gzdefine.huangcuangoa.activity.BaseActivity;
import com.gzdefine.huangcuangoa.activity.CalendarInfoActivity;
import com.gzdefine.huangcuangoa.activity.LoginActivity;
import com.gzdefine.huangcuangoa.activity.MapActivity;
import com.gzdefine.huangcuangoa.activity.MeetingInfoActivity;
import com.gzdefine.huangcuangoa.activity.WebViewNoTitleActivity;
import com.gzdefine.huangcuangoa.entity.LoginResponse;
import com.gzdefine.huangcuangoa.fragment.Fragment_App;
import com.gzdefine.huangcuangoa.fragment.Fragment_Friends;
import com.gzdefine.huangcuangoa.fragment.Fragment_Msg;
import com.gzdefine.huangcuangoa.fragment.Fragment_Profile;
import com.gzdefine.huangcuangoa.listener.PgyUpdateManagerListener;
import com.gzdefine.huangcuangoa.util.LogUtil;
import com.gzdefine.huangcuangoa.util.SPUtils;
import com.pgyersdk.crash.PgyCrashManager;
import com.pgyersdk.update.PgyUpdateManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import static com.gzdefine.huangcuangoa.util.Constant.mail;
import static com.gzdefine.huangcuangoa.util.Constant.myToDo;

public class MainActivity extends BaseActivity {
    private MainActivity mainActivity;
    private int SDK_PERMISSION_REQUEST = 127;//百度地图SDK权限
    public static final int PERMISSION_REQUEST_CODE = 1;//系统授权管理页面时的结果参数
    private String permissionInfo;
    private TextView unreaMsgdLabel;// 未读消息textview
    private TextView unreadAddressLable;// 未读通讯录textview
    private TextView unreadFindLable;// 发现
    private Fragment[] fragments;
    public Fragment_Msg homefragment;
    private Fragment_Friends contactlistfragment;
    private Fragment_App findfragment;
    private Fragment_Profile profilefragment;
    private ImageView[] imagebuttons;
    private TextView[] textviews;
    private int index;
    private long onBackPressedTimeMillis; // 按下返回键的时间戳
    private int currentTabIndex;// 当前fragment的index
    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSIONS = 1;
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 2;
    public static final String MESSAGE_RECEIVED_ACTION = "com.gzdefine.huangcuangoa.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_MESSAGE = "message";
    public static boolean isForeground = false;
    private MessageReceiver mMessageReceiver;
    Handler handler = new Handler();
    //    static final String[] PERMISSION = new String[]{
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,// 写入权限
//            Manifest.permission.READ_EXTERNAL_STORAGE,  //读取权限
//            Manifest.permission.READ_PHONE_STATE,        //读取设备信息
//            Manifest.permission.ACCESS_COARSE_LOCATION, //百度定位
//            Manifest.permission.ACCESS_FINE_LOCATION,
//    };
    private List<Activity> activityList = new LinkedList<Activity>();

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
//                String extras = intent.getStringExtra(KEY_EXTRAS);
//                Log.d("reg","extras:"+extras);
                Log.d("reg", "messge:" + messge);
//                StringBuilder showMsg = new StringBuilder();
//                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
//                if (!ExampleUtil.isEmpty(extras)) {
//                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
//                }
//              setCostomMsg(showMsg.toString());
                setCostomMsg(messge);
            }
        }
    }

    private void setCostomMsg(String msg) {
    }


    @Override
    public int initContentID() {
        return R.layout.activity_main;
    }
//    @Override
//    protected void process(Bundle savedInstanceState) {
//        super.process(savedInstanceState);
//
//        //如果有什么需要初始化的，在这里写就好～
//
//    }
//
//    @Override
//    public void getAllGrantedPermission() {
//        //当获取到所需权限后，进行相关业务操作
//
//        super.getAllGrantedPermission();
//    }
//
//    @Override
//    public String[] getPermissions() {
//        return PERMISSION;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTagAndAlias();// 绑定极光推送别名
        try {
            PgyUpdateManager.register(this, new PgyUpdateManagerListener(this));
        } catch (Exception e) {
            PgyCrashManager.reportCaughtException(this, e);
            LogUtil.e(MainActivity.class, "检查更新失败", e);
            App.me().toast("检查更新失败");
        }
        registerMessageReceiver();
        ButterKnife.bind(this);
        setAppTitle("消息");
        //checkLogin();

        initTabView();
        getPersimmions();
        initView();
        Service();//开启Websocket服务
        final String mark = getIntent().getStringExtra("mark");
        final String insid = getIntent().getStringExtra("insid");
        final String cid = getIntent().getStringExtra("cid");
        final String pid = getIntent().getStringExtra("pid");
        //结束后调用
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mark != null) {
                    if (mark.equals("0")) {
                        Log.d("reg", "考勤");
                        Intent intent = new Intent(MainActivity.this, MapActivity.class);
                        SPUtils.put(MainActivity.this, "insid", insid);
                        startActivityForResult(intent, MapActivity.REQUEST_CODE);
                    } else if (mark.equals("1")) {
                        Log.d("reg", "会议");
                        Intent intent0 = new Intent(MainActivity.this, MeetingInfoActivity.class);
                        intent0.putExtra("id", cid);
                        startActivityForResult(intent0, MeetingInfoActivity.REQUEST_CODE);
                    } else if (mark.equals("2")) {
                        Log.d("reg", "日程");
                        Intent intent = new Intent(MainActivity.this, CalendarInfoActivity.class);
                        intent.putExtra("id", pid);
                        startActivityForResult(intent, CalendarInfoActivity.REQUEST_CODE);
                    } else if (mark.equals("3")) {
                        Log.d("reg", "流程");
                        Intent intent1 = new Intent(MainActivity.this, WebViewNoTitleActivity.class);
                        intent1.putExtra("url", myToDo);
                        intent1.putExtra("type", "2");
                        startActivityForResult(intent1, WebViewNoTitleActivity.REQUEST_CODE);
                    } else if (mark.equals("4")) {
                        Log.d("reg", "邮件");
                        Intent intent2 = new Intent(MainActivity.this, WebViewNoTitleActivity.class);
                        intent2.putExtra("url", mail);
                        intent2.putExtra("type", "1");
                        startActivityForResult(intent2, WebViewNoTitleActivity.REQUEST_CODE);
                    }
                }
            }
        }, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MapActivity.REQUEST_CODE:
            case MeetingInfoActivity.REQUEST_CODE:
            case CalendarInfoActivity.REQUEST_CODE:
            case WebViewNoTitleActivity.REQUEST_CODE:
                Log.d("reg", "推送详情返回的");
                if (App.me().login() != null) {
                    //Intent intent = new Intent(this, WebSocketService.class);
                    //startService(intent);
                }
                break;
        }
    }

    /**
     * 设置标签与别名
     */
    private void setTagAndAlias() {
        /**
         *这里设置了别名，在这里获取的用户登录的信息
         *并且此时已经获取了用户的userId,然后就可以用用户的userId来设置别名了
         **/
        //false状态为未设置标签与别名成功
        //if (UserUtils.getTagAlias(getHoldingActivity()) == false) {
        Set<String> tags = new HashSet<String>();
        //这里可以设置你要推送的人，一般是用户uid 不为空在设置进去 可同时添加多个
        LoginResponse user = App.me().login();
        if (user != null) {
            Log.d("reg", "user.getUuid():" + user.getUserId());
            Log.d("reg", "user.getIdcard():" + user.getIdcard());
            if (!TextUtils.isEmpty(user.getUserId())) {
                tags.add(user.getUserId());//设置tag
            }
            //上下文、别名【Sting行】、标签【Set型】、回调
            JPushInterface.setAliasAndTags(this, user.getUserId(), tags,
                    mAliasCallback);
        }
    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            Log.d("reg", "注册别名返回:" + code);

            String logs;
            switch (code) {
                case 0:
                    logs = "别名设置成功";
                    Log.i("reg", logs);
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i("reg", logs);
                    // 延迟 60 秒来调用 Handler 设置别名
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 30);
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e("reg", logs);
            }
        }
    };
    private static final int MSG_SET_ALIAS = 1001;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.d("reg", "Set alias in handler.");
                    // 调用 JPush 接口来设置别名。
                    LoginResponse user = App.me().login();
                    if (user != null) {
                        Log.d("reg", "user.getUuid():" + user.getUserId());
                        Log.d("reg", "user.getIdcard():" + user.getIdcard());
                        Set<String> tags = new HashSet<String>();
                        if (!TextUtils.isEmpty(user.getUserId())) {
                            tags.add(user.getUserId());//设置tag
                        }
                        //上下文、别名【Sting行】、标签【Set型】、回调
                        JPushInterface.setAliasAndTags(MainActivity.this, user.getUserId(), null,
                                mAliasCallback);
                    }
                    break;
                default:
                    Log.i("reg", "Unhandled msg - " + msg.what);
            }
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override // 参见信鸽推送文档 用于控制器单例时接受推送参数
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
    }


    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }


    private void checkLogin() {
        LoginResponse login = App.me().login();
        if (null == login) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void Service() {
        if (App.me().login() != null) {
            //Intent intent = new Intent(this, WebSocketService.class);
            //startService(intent);
        }
    }


    @Override
    protected void onDestroy() {
        //Intent intent = new Intent(this, WebSocketService.class);
        //stopService(intent);
        PgyUpdateManager.unregister(); // 注销蒲公英更新监听
        unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

//    @Subscriber(mode = ThreadMode.ASYNC)
//    public void s(String s) {
//        Log.d("reg", "mian" + s);
//        VibratorUtil.Vibrate(MainActivity.this, 3000);
//        try {
//
//            JSONObject data = new JSONObject(s);
//            String contentString = data.getString("content");
//            JSONObject content = new JSONObject(contentString);
//            if (content.getString("type").contains("考勤抽查")) {
//                dialog(content.getString("msg"), "抽查签到", 0, content.getString("insid"));
//            } else if (content.getString("type").contains("会议通知")) {
//                dialog(content.getString("msg"), "会议通知", 1, content.getString("cid"));
//            } else if (content.getString("type").contains("邮件通知")) {
//                dialog(content.getString("msg"), "邮件通知", 4, null);
//            } else if (content.getString("type").contains("日程安排")) {
//                dialog(content.getString("msg"), "日程安排", 2, content.getString("pid"));
//            } else if (content.getString("type").contains("流程通知")) {
//                dialog(content.getString("msg"), "流程通知", 3, null);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    protected void dialog(String msg, String lx, final int type, final String insid) {

        Dialog dialog = new AlertDialog.Builder(MainActivity.this).setIcon(
                android.R.drawable.btn_star).setTitle(lx).setMessage(msg).setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (type == 0) {
                            SPUtils.put(MainActivity.this, "insid", insid);
                            Intent intent = new Intent(MainActivity.this, MapActivity.class);
                            startActivity(intent);
                        } else if (type == 1) {
                            Intent intent = new Intent(MainActivity.this, MeetingInfoActivity.class);
                            intent.putExtra("id", insid);
                            startActivity(intent);
                        } else if (type == 4) {
                            Intent intent0 = new Intent(MainActivity.this, WebViewNoTitleActivity.class);
                            intent0.putExtra("url", mail);
                            intent0.putExtra("type", "1");
                            startActivity(intent0);
                        } else if (type == 2) {
                            Intent intent1 = new Intent(MainActivity.this, CalendarInfoActivity.class);
                            intent1.putExtra("id", insid);
                            startActivity(intent1);
                        } else if (type == 3) {
                            Intent intent2 = new Intent(MainActivity.this, WebViewNoTitleActivity.class);
                            intent2.putExtra("url", myToDo);
                            intent2.putExtra("type", "2");
                            startActivity(intent2);
                        }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
//                Toast.makeText(MainActivity.this, "取消", Toast.LENGTH_LONG)
//                        .show();
            }
        }).create();
        dialog.show();
    }

    private void initView() {
        ImageView back = (ImageView) findViewById(R.id.img_back);
        back.setVisibility(View.GONE);
    }

    /**
     * 检测权限是否授权
     *
     * @return
     */
    private boolean checkPermission(Context context, String permission) {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, permission);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //得到了授权
                    Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
                    try {
                        PgyUpdateManager.register(MainActivity.this, new PgyUpdateManagerListener(this));
                    } catch (Exception e) {
                        PgyCrashManager.reportCaughtException(this, e);
                        App.me().toast("检查更新失败");
                    }
                } else {
                    //未授权
                    Toast.makeText(this, "您已禁止自动更新", Toast.LENGTH_SHORT).show();
                    Uri uri = Uri.parse("https://www.pgyer.com/RrIO");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            /*
             * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
			 */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }
            // 读取网络状态权限
            if (addPermission(permissions, Manifest.permission.INTERNET)) {
                permissionInfo += "android.permission.INTERNET Deny \n";
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }
        } else {
            return true;
        }
    }

    private void initTabView() {
        unreaMsgdLabel = (TextView) findViewById(R.id.unread_msg_number);
        unreadAddressLable = (TextView) findViewById(R.id.unread_address_number);
        unreadFindLable = (TextView) findViewById(R.id.unread_find_number);
        homefragment = new Fragment_Msg();
        contactlistfragment = new Fragment_Friends();
        findfragment = new Fragment_App();
        profilefragment = new Fragment_Profile();
        fragments = new Fragment[]{homefragment, contactlistfragment,
                findfragment, profilefragment};
        imagebuttons = new ImageView[4];
        imagebuttons[0] = (ImageView) findViewById(R.id.ib_weixin);
        imagebuttons[1] = (ImageView) findViewById(R.id.ib_contact_list);
        imagebuttons[2] = (ImageView) findViewById(R.id.ib_find);
        imagebuttons[3] = (ImageView) findViewById(R.id.ib_profile);
        imagebuttons[0].setSelected(true);
        textviews = new TextView[4];
        textviews[0] = (TextView) findViewById(R.id.tv_weixin);
        textviews[1] = (TextView) findViewById(R.id.tv_contact_list);
        textviews[2] = (TextView) findViewById(R.id.tv_find);
        textviews[3] = (TextView) findViewById(R.id.tv_profile);
        textviews[0].setTextColor(0xFF2B96FA);
        // 添加显示第一个fragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, homefragment)
                .add(R.id.fragment_container, contactlistfragment)
                .add(R.id.fragment_container, profilefragment)
                .add(R.id.fragment_container, findfragment)
                .hide(contactlistfragment).hide(profilefragment)
                .hide(findfragment).show(homefragment).commit();
    }

    public void onTabClicked(View view) {
        switch (view.getId()) {
            case R.id.re_weixin:
                index = 0;
                setAppTitle("消息");
                break;
            case R.id.re_contact_list:
                index = 1;
                setAppTitle("通讯录");
                break;
            case R.id.re_find:
                index = 2;
                setAppTitle("应用");
                break;
            case R.id.re_profile:
                index = 3;
                setAppTitle("我的");
                break;
        }
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager()
                    .beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commitAllowingStateLoss();
        }
        imagebuttons[currentTabIndex].setSelected(false);
        // 把当前tab设为选中状态
        imagebuttons[index].setSelected(true);
        textviews[currentTabIndex].setTextColor(0xFF999999);
        textviews[index].setTextColor(0xFF2B96FA);
        currentTabIndex = index;
    }

    //登陆
//    class Login extends Ok {
//        public Login(Context context) {
//            super(context);
//        }
//
//        public void login(String username, String password) {
//
//
//            super.post(Constant.DOMAIN + "login.do", "acc", username, "pd", password, "rememberMe", "0","from","mobile");
//        }
//
//        @Override
//        public void onFailure(Call call, IOException e) {
//            Log.i("info_callFailure", e.toString());
//        }
//
//        @Override
//        public void onResponse(Call call, Response response) throws IOException {
//            final String re = response.body().string();
//            Log.d("reg", "My_Ok_Http.result" + re);
//            //请求头
//            Headers headers = response.headers();
//            //从请求头获取cookies组合
//            List<String> cookies = headers.values("Set-Cookie");
//            //遍历获取含JSESSIONID的字段
//            for (int i=0;i<cookies.size();i++){
//                String session = cookies.get(i);
//                seesion = session.substring(0, session.indexOf(";"));
//                if (seesion.contains("JSESSIONID")) {
//                    Log.i("info_s", "放了session  :" + seesion);
//                    SPUtils.put(App.me(), "session", session);//（就是我们要的会话）
//                }
//            }
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(1200);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                        loginSucees(re);
//                }
//            });
//        }
//    }
//    void loginSucees(String re) {
//        try {
//            JSONObject object=new JSONObject(re);
//            String s=  object.getString("data");
//            String message=  object.getString("message");
//            ToastUtil.showShort(this, message);
//            JSONObject data=new JSONObject(s);
//            LoginResponse login = new LoginResponse();
//            login.address=data.getString("address");
//            login.fullname=data.getString("fullname");
//            if (data.has("photo")){
//                login.photo=data.getString("photo");
//            }
//            login.username=data.getString("username");
//            login.phone=data.getString("mobile");
//            login.userId=data.getString("userId");
//            App.me().login(login);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
    @Override
    public void onBackPressed() { // 连续按下两次返回键才退出App
        long currentTimeMillis = System.currentTimeMillis();
        if (onBackPressedTimeMillis != 0 && currentTimeMillis - onBackPressedTimeMillis < 3000) {
            super.onBackPressed();
//            exit();
        } else {
            App.me().toast("再按一次返回键退出");
        }
        onBackPressedTimeMillis = currentTimeMillis;
    }
/*    //结束整个应用程序
    public void exit() {
        //遍历 链表，依次杀掉各个Activity
        for (Activity activity : activityList) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        //杀掉，这个应用程序的进程，释放 内存
        int id = android.os.Process.myPid();
        if (id != 0) {
            android.os.Process.killProcess(id);
        }
    }*/


}
