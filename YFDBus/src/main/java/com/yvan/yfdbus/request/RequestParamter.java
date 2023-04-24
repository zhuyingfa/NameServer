package com.yvan.yfdbus.request;

/**
 * @author yvan
 * @date 2023/4/22
 * @description 请求方法参数类
 */
public class RequestParamter {

    private String parameterClassName;
    private String parameterValue;

    public RequestParamter() {
    }

    public RequestParamter(String parameterClassName, String parameterValue) {
        this.parameterClassName = parameterClassName;
        this.parameterValue = parameterValue;
    }

    public String getParameterClassName() {
        return parameterClassName;
    }

    public void setParameterClassName(String parameterClassName) {
        this.parameterClassName = parameterClassName;
    }

    public String getParameterValue() {
        return parameterValue;
    }

    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue;
    }
}
