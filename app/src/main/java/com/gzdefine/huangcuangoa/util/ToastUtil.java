package com.gzdefine.huangcuangoa.util;

/**
 * TODO：
 * Created by Max on 2017/7/6.
 */

import android.content.Context;
import android.text.Html;
import android.view.Gravity;
import android.widget.Toast;

import com.gzdefine.huangcuangoa.R;

/**
 * Toast统一管理类
 */
public class ToastUtil {

    private ToastUtil() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isShow = true;

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, CharSequence message) {
        if (isShow)
            toastShow(context,message);
    }

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, int message) {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, CharSequence message) {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, int message) {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, CharSequence message, int duration) {
        if (isShow)
            Toast.makeText(context, message, duration).show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, int message, int duration) {
        if (isShow)
            Toast.makeText(context, message, duration).show();
    }

    private static void toastShow(Context context, CharSequence message){
        int color = context.getResources().getColor(R.color.colorF);
        //设置toast字体颜色 蓝色
        String ToastStr = "<font color='"+color+"'>"+message+"</font>";
        Toast toast = Toast.makeText(context, Html.fromHtml(ToastStr) ,Toast.LENGTH_SHORT);
        //居中
        toast.setGravity(Gravity.CENTER, 0, 0);
        //弹出
        toast.show();
    }

}
