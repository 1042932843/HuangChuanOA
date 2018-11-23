/*
 * Copyright © 2015 珠海云集软件科技有限公司.
 * Website：http://www.YunJi123.com
 * Mail：dev@yunji123.com
 * Tel：+86-0756-8605060
 * QQ：340022641(dove)
 * Author：dove
 */

package com.gzdefine.huangcuangoa.util;

import android.os.Environment;

public interface ConfigKit {

    String PAY_MODE = "01"; // 00:银联正式环境 01:银联测试环境
    String CONFIG = "config.xml";
    String DB_NAME = "ePay.db";
    int DB_VERSION = 1603231600;

    String DATE_FORMAT = "yyyy-MM-dd";
    String TIME_FORMAT = "HH:mm:ss";
    String SHORT_TIME_FORMAT = "HH:mm";
    String DATE_TIME_FORMAT = DATE_FORMAT + " " + TIME_FORMAT;
    String DATE_SHORT_TIME_FORMAT = DATE_FORMAT + " " + SHORT_TIME_FORMAT;
    String[] MONTHS = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"};
    String[] WEEKS = {"日", "一", "二", "三", "四", "五", "六"};

    String ALGORITHM_MD5 = "MD5";
    String ALGORITHM_KEY = "0123456789123456"; // md5(16) -> 1cda5fa95f238c39
    String ALGORITHM_AES = "AES";
    String ALGORITHM_ECB = "AES/ECB/PKCS5Padding";

    String HTTP = "http://";
    String HOST = "61.143.38.73";
    String PORT = "8181";
    String DOMAIN = HTTP + HOST + ":" + PORT;
    String SERVICE = DOMAIN + "/services/";
    String NAME_SPACE = ".pay.define.com";
    String PARAMS = "in0";
    String SYS_NO = "888888888";
    String RESULT = "result";
    String DATA = "data";
    String RESULT2 = "Result";
    String INFO = "info";
    String INFOS = "infos";
    String RESPONSE = "response";
    String STATE_CODE = "stateCode";
    String ERR_CODE = "state";
    String RESULT_CODE = "ResultCode";
    String MESSAGE = "message";
    String ERR_MSG = "message";
    String ERROR_MSG = "ErrorMsg";
    String ERROR_MESSAGE = "errormessage";

    String REGULAR_USER_ID = "^[a-z0-9]{32}$";
    String REGULAR_URL = "(i?)^https?://.+$";
    String REGULAR_MD5 = "^[a-f0-9A-F]{32}$";
    String REGULAR_PASSWORD = "^.{6,20}$";
    String REGULAR_PHONE = "^1[34578]\\d{9}$";
    String REGULAR_SMS_CODE = "^\\d{4}$";
    String REGULAR_CHINESE = "^.*[\\u4e00-\\u9fa5]+.*$";
    String REGULAR_ID_CARD = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)";
    String REGULAR_EMAIL = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
    String REGULAR_NUMBER = "^\\d+$";
    String REGULAR_DATE = "^\\d{4}-\\d{1,2}-\\d{1,2}$";
    String REGULAR_DATETIME = "^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$";


    String FILEPATH = Environment.getExternalStorageDirectory().getPath();
    String IMGNAME = "mypic";
    String FILEPATHIMG = FILEPATH + "/" + IMGNAME+".jpg";
}
