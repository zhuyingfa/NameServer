package com.yvan.yfdbus.request;

/**
 * @author yvan
 * @date 2023/4/22
 * @description 请求数据类
 */
public class RequestBean {

    private int type;
    private String className;
    private String methodName;
    private RequestParamter[] requestParamters;

    public RequestBean() {
    }

    public RequestBean(int type, String className, String methodName, RequestParamter[] requestParamters) {
        this.type = type;
        this.className = className;
        this.methodName = methodName;
        this.requestParamters = requestParamters;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public RequestParamter[] getRequestParamters() {
        return requestParamters;
    }

    public void setRequestParamters(RequestParamter[] requestParamters) {
        this.requestParamters = requestParamters;
    }
}
