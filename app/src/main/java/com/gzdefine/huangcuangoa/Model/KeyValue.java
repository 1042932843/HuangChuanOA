package com.gzdefine.huangcuangoa.Model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/12 0012.
 */
public class KeyValue implements Serializable {
    public String Jian;
    public String Zhi;

    public KeyValue(String k, String v)
    {
        Jian = k;
        Zhi = v;
    }
}
