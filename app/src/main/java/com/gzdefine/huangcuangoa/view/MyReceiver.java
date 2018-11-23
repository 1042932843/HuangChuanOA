package com.gzdefine.huangcuangoa.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.gzdefine.huangcuangoa.App;
import com.gzdefine.huangcuangoa.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by HFS on 2016/7/30.
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "MyReceiver";
    public final static String FLAG = "PAY_RECALL";
//    private Context contex;

//    public MyReceiver(Activity context) {
//        this.contex = context;
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(FLAG);
//        this.context.registerReceiver(this, intentFilter);
//    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        this.contex = context;
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "onReceive - " + intent.getAction());

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            System.out.println("收到了自定义消息。消息内容是：" + message);
            String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
            System.out.println("收到了自定义消息。extra是：" + extra);

//            case 0://考勤
//
//            case 1://会议
//
//            case 2://日程
//
//            case 3://流程
//
//            case 4://邮件
//
//                break;
            /**
             * 通过输出日志我们知道extra是JSON格式的字符串
             * 解析Json
             */

//            Data.data.add(map);
//            Toast.makeText(context,"Data.data:"+Data.data.size(), Toast.LENGTH_SHORT).show();

            // 自定义消息不会展示在通知栏，完全要开发者写代码去处理
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            System.out.println("收到了通知");
            /**
             * 用户接收SDK通知栏信息的intent
             */
            Log.e(TAG, "收到了自定义消息消息是3");
            // 在这里可以自己写代码去定义用户点击后的行为
            //保存服务器推送下来的消息的标题。
            String extra1 = bundle.getString(JPushInterface.EXTRA_TITLE);
            //消息内容
            String extra2 = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            //附加字段。这是个 JSON 字符串。
            String extra3 = bundle.getString(JPushInterface.EXTRA_EXTRA);
            //唯一标识消息的 ID, 可用于上报统计等。
            String extra4 = bundle.getString(JPushInterface.EXTRA_MSG_ID);
            //通知的标题
            String extra5 = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
            //通知内容
            String extra6 = bundle.getString(JPushInterface.EXTRA_ALERT);
            //通知栏的Notification ID，可以用于清除Notification
            //如果服务端内容（alert）字段为空，则notification id 为0
//            String extra7 = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_ID);
            //富媒体通知推送下载的HTML的文件路径,用于展现WebView
            String extra8 = bundle.getString(JPushInterface.EXTRA_RICHPUSH_HTML_PATH);
            //富媒体通知推送下载的图片资源的文件名,多个文件名用 “，” 分开。
            // 与 “JPushInterface.EXTRA_RICHPUSH_HTML_PATH” 位于同一个路径。
            String extra9 = bundle.getString(JPushInterface.EXTRA_RICHPUSH_HTML_RES);
            //JSON

            Log.e(TAG, "收到了1:" + extra1);
            Log.e(TAG, "收到了2:" + extra2);
            Log.e(TAG, "收到了3:" + extra3);
            Log.e(TAG, "收到了4:" + extra4);
            Log.e(TAG, "收到了5:" + extra5);
            Log.e(TAG, "收到了6:" + extra6);
//            Log.e(TAG, "收到了7:" + extra7);
            Log.e(TAG, "收到了8:" + extra8);
            Log.e(TAG, "收到了9:" + extra9);
            //调用Message接口的方法
//            Intent intent1 = new Intent(context, HomePageActivity.class);
//            intent1.putExtra("extra6", extra6);
//            context.sendBroadcast(intent1);

//            Intent msgIntent = new Intent(HomePageActivity.MESSAGE_RECEIVED_ACTION);
//            msgIntent.setAction(FLAG);
//            msgIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
//            msgIntent.putExtra("name", extra6);
//             context.sendBroadcast(msgIntent);
            processCustomMessage(context, extra6);

            // 在这里可以做些统计，或者做些其他工作
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            System.out.println("用户点击打开了通知");
            // 在这里可以自己写代码去定义用户点击后的行为
            //保存服务器推送下来的消息的标题。
            String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);

            String extra1 = bundle.getString(JPushInterface.EXTRA_TITLE);
            //消息内容
            String extra2 = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            //附加字段。这是个 JSON 字符串。
            //唯一标识消息的 ID, 可用于上报统计等。
            String extra4 = bundle.getString(JPushInterface.EXTRA_MSG_ID);
            //通知的标题
            String extra5 = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
            //通知内容
            String extra6 = bundle.getString(JPushInterface.EXTRA_ALERT);
            //通知栏的Notification ID，可以用于清除Notification
            //如果服务端内容（alert）字段为空，则notification id 为0
//            String extra7 = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_ID);
            //富媒体通知推送下载的HTML的文件路径,用于展现WebView
            String extra8 = bundle.getString(JPushInterface.EXTRA_RICHPUSH_HTML_PATH);
            //富媒体通知推送下载的图片资源的文件名,多个文件名用 “，” 分开。
            // 与 “JPushInterface.EXTRA_RICHPUSH_HTML_PATH” 位于同一个路径。
            String extra9 = bundle.getString(JPushInterface.EXTRA_RICHPUSH_HTML_RES);
            //JSON

            Log.e(TAG, "收到了自定义消息消息内容是1:" + extra1);
            Log.e(TAG, "收到了自定义消息消息extra是2:" + extra2);
            Log.e(TAG, "收到了自定义消息消息extra是4:" + extra4);
            Log.e(TAG, "收到了自定义消息消息extra是5:" + extra5);
            Log.e(TAG, "收到了自定义消息消息extra是6:" + extra6);
//            Log.e(TAG, "收到了自定义消息消息extra是7:" + extra7);
            Log.e(TAG, "收到了自定义消息消息extra是8:" + extra8);
            Log.e(TAG, "收到了自定义消息消息extra是9:" + extra9);
            Log.e(TAG, "打开的消息:" + extra);

            try {
                JSONObject object = new JSONObject(extra);
                String p = object.getString("json");
                Log.d("reg", "p:" + p);
                JSONObject object1 = new JSONObject(p);
                String mark = object1.getString("mark");
                Log.d("reg", "mark:" + mark);
                Intent intent1 = new Intent(context, MainActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.putExtra("mark", mark);
                if (null != mark) {
                    if (mark.equals("0")) {
                        if (null != object1.getString("insid")) {
                            intent1.putExtra("insid", object1.getString("insid"));
                        }
                    } else if (mark.equals("1")) {
                        if (null != object1.getString("cid")) {
                            intent1.putExtra("cid", object1.getString("cid"));
                        }
                    } else if (mark.equals("2")) {
                        if (null != object1.getString("pid")) {
                            intent1.putExtra("pid", object1.getString("pid"));
                        }
                    }
                }
                context.startActivity(intent1);
                App.me().finishAll();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            // 在这里可以自己写代码去定义用户点击后的行为
//            Intent i = new Intent(context, MainActivity.class);  //自定义打开的界面
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(i);
        } else {
            Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
    }


    private void processCustomMessage(Context context, String extra6) {
        if (MainActivity.isForeground) {
//            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
//            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Log.d("reg", "extra6:" + extra6);
//            Log.d("reg","message:"+message);
            Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
            msgIntent.putExtra(MainActivity.KEY_MESSAGE, extra6);
//            if (!ExampleUtil.isEmpty(extra6)) {
//                try {
//                    JSONObject extraJson = new JSONObject(extra6);
//                    if (null != extraJson && extraJson.length() > 0) {
//                        msgIntent.putExtra(MainActivity.KEY_EXTRAS, extra6);
//                    }
//                } catch (JSONException e) {
//
//                }
//
//            }
            context.sendBroadcast(msgIntent);
        }
    }


}
