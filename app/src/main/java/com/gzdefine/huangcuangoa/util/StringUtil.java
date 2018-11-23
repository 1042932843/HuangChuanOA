/*
 * Copyright © 2015 珠海云集软件科技有限公司.
 * Website：http://www.YunJi123.com
 * Mail：dev@yunji123.com
 * Tel：+86-0756-8605060
 * QQ：340022641(dove)
 * Author：dove
 */

package com.gzdefine.huangcuangoa.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class StringUtil {

    public static String md5(String s) {
        if (s == null) return null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(s.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                int val = b & 0xFF;
                if (val < 16) {
                    sb.append(0);
                }
                sb.append(Integer.toHexString(val));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isEmpty(CharSequence s) {
        return s == null || s.length() == 0;
    }

    // 匹配md5
    public static boolean matchesMd5(String s) {
        return s != null && s.matches("^[a-f0-9A-F]{32}$");
    }

    // 登录密码 6-20位字符
    public static boolean matchesPassword(String s) {
        return s != null && s.matches("^.{6,20}$");
    }

    // 匹配手机号码
    public static boolean matchesPhone(String s) {
        return s != null && s.matches("^1[34578]\\d{9}$");
    }

    // 短信验证码
    public static boolean matchesCode(String s) {
        return s != null && s.matches("^\\w{4}$");
    }

    // 匹配中文
    public static boolean matchesChinese(String s) {
        return s != null && s.matches("^.*[\\u4e00-\\u9fa5]+.*$");
    }

    // 匹配身份证
    public static boolean matchesIdCard(String s) {
        return s != null && s.matches("(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)");
    }

    // 匹配邮箱
    public static boolean matchesEmail(String s) {
        return s != null && s.matches("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
    }

    // 匹配邮编
    public static boolean matchesZipcode(String s) {
        return s != null && s.matches("^\\d{6}$");
    }

    // 匹配数字
    public static boolean matchesNumber(String s) {
        return s != null && s.matches("^\\d+$");
    }

}
