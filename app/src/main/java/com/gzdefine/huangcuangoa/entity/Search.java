package com.gzdefine.huangcuangoa.entity;

/**
 * Created by Administrator on 2018/1/8 0008.
 */
public class Search {
    private String Q_regFlag_SN_EQ; //考勤结果
    private String Q_registerTime_D_GE;//开始日期
    private String Q_registerTime_D_LE;//结束日期
    private String departOrUser;//姓名/部门名称

    public String getQ_regFlag_SN_EQ() {
        return Q_regFlag_SN_EQ;
    }

    public void setQ_regFlag_SN_EQ(String q_regFlag_SN_EQ) {
        Q_regFlag_SN_EQ = q_regFlag_SN_EQ;
    }

    public String getQ_registerTime_D_GE() {
        return Q_registerTime_D_GE;
    }

    public void setQ_registerTime_D_GE(String q_registerTime_D_GE) {
        Q_registerTime_D_GE = q_registerTime_D_GE;
    }

    public String getQ_registerTime_D_LE() {
        return Q_registerTime_D_LE;
    }

    public void setQ_registerTime_D_LE(String q_registerTime_D_LE) {
        Q_registerTime_D_LE = q_registerTime_D_LE;
    }

    public String getDepartOrUser() {
        return departOrUser;
    }

    public void setDepartOrUser(String departOrUser) {
        this.departOrUser = departOrUser;
    }
}
