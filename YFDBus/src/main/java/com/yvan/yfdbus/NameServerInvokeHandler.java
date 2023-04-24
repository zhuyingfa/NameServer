package com.yvan.yfdbus;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author yvan
 * @date 2023/4/22
 * @description 服务代理方法类
 */
public class NameServerInvokeHandler implements InvocationHandler {
    private static final String TAG = NameServerInvokeHandler.class.getSimpleName();
    private static final Gson GSON = new Gson();
    private Class clazz;

    public NameServerInvokeHandler(Class clazz) {
        this.clazz = clazz;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        if (method.getName().contains("toString")) {
            return "proxy";
        }
        Log.i(TAG, "invoke: " + method.getName());
        // 发送请求
        String data = IPCNameManager.getInstance().sendRequest(clazz, method, objects, NameServerManager.TYPE_INVOKE);
        Log.i(TAG, "invoke: data " + data);
        if (!TextUtils.isEmpty(data)) {
            Object object = GSON.fromJson(data, method.getReturnType());
            return object;
        }
        return null;
    }
}
