/*
 * Copyright © 2015 珠海云集软件科技有限公司.
 * Website：http://www.YunJi123.com
 * Mail：dev@yunji123.com
 * Tel：+86-0756-8605060
 * QQ：340022641(dove)
 * Author：dove
 */

package com.gzdefine.huangcuangoa.util;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class StringKit implements ConfigKit {

    static MessageDigest digest;
    static SecretKeySpec keySpec;
    static Cipher encrypt;
    static Cipher decrypt;

    public static MessageDigest digest() {
        if (digest == null) {
            synchronized (StringKit.class) {
                if (digest == null) {
                    try {
                        digest = MessageDigest.getInstance(ALGORITHM_MD5);
                    } catch (NoSuchAlgorithmException e) {
                        LogUtil.e("StringKit.digest");
                    }
                }
            }
        }
        return digest;
    }

    public static SecretKeySpec keySpec() {
        if (keySpec == null) {
            synchronized (StringKit.class) {
                if (keySpec == null) {
                    keySpec = new SecretKeySpec(md5(ALGORITHM_KEY, false).getBytes(), ALGORITHM_AES);
                }
            }
        }
        return keySpec;
    }

    public static Cipher encrypt() {
        if (encrypt == null) {
            synchronized (StringKit.class) {
                if (encrypt == null) {
                    try {
                        encrypt = Cipher.getInstance(ALGORITHM_ECB);
                        encrypt.init(Cipher.ENCRYPT_MODE, keySpec());
                    } catch (Exception e) {
                        LogUtil.e("StringKit.encrypt");
                    }
                }
            }
        }
        return encrypt;
    }

    public static Cipher decrypt() {
        if (decrypt == null) {
            synchronized (StringKit.class) {
                if (decrypt == null) {
                    try {
                        decrypt = Cipher.getInstance(ALGORITHM_ECB);
                        decrypt.init(Cipher.DECRYPT_MODE, keySpec());
                    } catch (Exception e) {
                        LogUtil.e("StringKit.decrypt");
                    }
                }
            }
        }
        return decrypt;
    }

    public static String md5(String s, boolean full) {
        if (s == null) throw new NullPointerException("contact_black == null");
        byte[] bytes = digest().digest(s.getBytes());
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            int val = b & 0xFF;
            if (val < 16) {
                builder.append(0);
            }
            builder.append(Integer.toHexString(val));
        }
        return full ? builder.toString() : builder.substring(8, 24);
    }

    public static String encrypt(String s) {
        try {
            byte[] bytes = s.getBytes();
            bytes = encrypt().doFinal(bytes);
            bytes = Base64.encode(bytes, Base64.NO_WRAP);
            return new String(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String s) {
        try {
            byte[] bytes = s.getBytes();
            bytes = Base64.decode(bytes, Base64.NO_WRAP);
            bytes = decrypt().doFinal(bytes);
            return new String(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0 || s.trim().length() == 0;
    }

    public static boolean isEmpty(String... ss) {
        for (String s : ss) {
            if (isEmpty(s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matches(String s, String regular) {
        return s != null && s.matches(regular);
    }

    public static boolean matchesUserId(String s) {
        return matches(s, REGULAR_USER_ID);
    }

    // 匹配网址
    public static boolean matchesUrl(String s) {
        return matches(s, REGULAR_URL);
    }

    // 匹配md5
    public static boolean matchesMd5(String s) {
        return matches(s, REGULAR_MD5);
    }

    // 登录密码 6-20位字符
    public static boolean matchesPassword(String s) {
        return matches(s, REGULAR_PASSWORD);
    }

    // 匹配手机号码
    public static boolean matchesPhone(String s) {
        return matches(s, REGULAR_PHONE);
    }

    // 短信验证码 4位数字
    public static boolean matchesCode(String s) {
        return matches(s, REGULAR_SMS_CODE);
    }

    // 匹配中文
    public static boolean matchesChinese(String s) {
        return matches(s, REGULAR_CHINESE);
    }

    // 匹配身份证
    public static boolean matchesIdCard(String s) {
        return matches(s, REGULAR_ID_CARD);
    }

    // 匹配邮箱
    public static boolean matchesEmail(String s) {
        return matches(s, REGULAR_EMAIL);
    }

    // 匹配数字
    public static boolean matchesNumber(String s) {
        return matches(s, REGULAR_NUMBER);
    }

    // 匹配日期
    public static boolean matchesDate(String s) {
        return matches(s, REGULAR_DATE);
    }

    // 匹配日期时间
    public static boolean matchesDateTime(String s) {
        return matches(s, REGULAR_DATETIME);
    }

}
