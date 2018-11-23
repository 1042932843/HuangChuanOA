/*
 * Copyright © 2015 珠海云集软件科技有限公司.
 * Website：http://www.YunJi123.com
 * Mail：dev@yunji123.com
 * Tel：+86-0756-8605060
 * QQ：340022641(dove)
 * Author：dove
 */

package com.gzdefine.huangcuangoa.util;

/**
 * 接口
 */
public class ApiMsg {
    public Exception exception;
    private String attributes;
    private String msg;
    private String obj;
    private String success;
    private String result;
    private String state;
    private String status;
    private String list;
    private String dataList;
    private String resultInfo;
    private String callTime;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getObj() {
        return obj;
    }

    public void setObj(String obj) {
        this.obj = obj;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public boolean hasException() {
        return exception != null;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
    public void setState(String state) {
        this.state=state;
    }
    public String getState() {
        return state;
    }

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }

    public String getDataList() {
        return dataList;
    }

    public void setDataList(String dataList) {
        this.dataList = dataList;
    }

    public String getResultInfo() {
        return resultInfo;
    }

    public void setResultInfo(String resultInfo) {
        this.resultInfo = resultInfo;
    }

    public String getCallTime() {
        return callTime;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ApiMsg{" +
                "exception=" + exception +
                ", attributes='" + attributes + '\'' +
                ", msg='" + msg + '\'' +
                ", obj='" + obj + '\'' +
                ", success='" + success + '\'' +
                ", result='" + result + '\'' +
                ", state='" + state + '\'' +
                ", status='" + status + '\'' +
                ", list='" + list + '\'' +
                ", dataList='" + dataList + '\'' +
                ", resultInfo='" + resultInfo + '\'' +
                ", callTime='" + callTime + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
