package com.yvan.yfdbus;

import com.yvan.yfdbus.request.RequestBean;
import com.yvan.yfdbus.request.RequestParamter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yvan
 * @date 2023/4/22
 * @description 类和方法管理中心
 */
public class NameCenter {

    /**
     * 类集合
     */
    private ConcurrentHashMap<String, Class<?>> mClassMap;

    /**
     * 方法集合
     */
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Method>> mAllMethodMap;

    /**
     * 方法参数值
     */
    private final ConcurrentHashMap<String, Object> mInstanceObjectMap;

    public NameCenter() {
        mClassMap = new ConcurrentHashMap<String, Class<?>>();
        mAllMethodMap = new ConcurrentHashMap<String, ConcurrentHashMap<String, Method>>();
        mInstanceObjectMap = new ConcurrentHashMap<String, Object>();
    }

    public void register(Class clazz) {
        mClassMap.put(clazz.getName(), clazz);
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            ConcurrentHashMap<String, Method> map = mAllMethodMap.get(clazz.getName());
            if (map == null) {
                map = new ConcurrentHashMap<String, Method>();
                mAllMethodMap.put(clazz.getName(), map);
            }
            // java重载   方法名+参数
            String key = getMethodParameters(method);
            map.put(key, method);
        }
    }

    public void putObject(String className, Object instance) {
        mInstanceObjectMap.put(className, instance);
    }

    public Object getObject(String className) {
        return mInstanceObjectMap.get(className);
    }

    public Method getMethod(RequestBean requestBean) {
        ConcurrentHashMap<String, Method> map = mAllMethodMap.get(requestBean.getClassName());
        if (map != null) {
            String key = getMethodParameters(requestBean);
            return map.get(key);
        }
        return null;
    }

    public Class<?> getClassType(String parameterClassName) {
        try {
            Class clazz = Class.forName(parameterClassName);
            return clazz;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 拼装方法（方法名-参数类型）
     *
     * @param requestBean
     * @return
     */
    public static String getMethodParameters(RequestBean requestBean) {
        List<String> parameterClassName = new ArrayList<>();
        for (RequestParamter c : requestBean.getRequestParamters()) {
            parameterClassName.add(c.getParameterClassName());
        }
        return getMethodParameters(requestBean.getMethodName(), parameterClassName);
    }

    /**
     * 拼装方法（方法名-参数类型）
     *
     * @param method
     * @return
     */
    public static String getMethodParameters(Method method) {
        List<String> parameterClassName = new ArrayList<>();
        for (Class<?> c : method.getParameterTypes()) {
            parameterClassName.add(c.getName());
        }
        return getMethodParameters(method.getName(), parameterClassName);
    }

    /**
     * 拼装方法（方法名-参数类型）
     *
     * @param methodName
     * @param parameterClassName
     * @return
     */
    public static String getMethodParameters(String methodName, List<String> parameterClassName) {
        // 方法签名
        StringBuilder result = new StringBuilder();
        result.append(methodName);
        int size = parameterClassName.size();
        if (size == 0) {
            return result.toString();
        }
        for (int i = 0; i < size; ++i) {
            result.append("-").append(parameterClassName.get(i));
        }
        return result.toString();
    }

}
