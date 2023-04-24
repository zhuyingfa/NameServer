package com.yvan.yfdbus;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.yvan.yfdbus.annotion.ClassId;
import com.yvan.yfdbus.request.RequestBean;
import com.yvan.yfdbus.request.RequestParamter;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author yvan
 * @date 2023/4/22
 * @description IPC通讯管理类
 */
public class IPCNameManager {

    private static final String TAG = IPCNameManager.class.getSimpleName();
    private NameCenter nameCenter = new NameCenter();
    private static final Gson GSON = new Gson();
    private static final IPCNameManager ourInstance = new IPCNameManager();
    private Context sContext;

    public static IPCNameManager getInstance() {
        return ourInstance;
    }

    public NameCenter getNameCenter() {
        return nameCenter;
    }

    public final Lock lock = new ReentrantLock();
    private String responce = null;

    /**
     * 初始化（客户端）
     *
     * @param context
     */
    public void init(Context context) {
        sContext = context.getApplicationContext();
    }

    /**
     * 服务注册（服务端）
     *
     * @param clazz
     */
    public void register(Class<?> clazz) {
        nameCenter.register(clazz);
    }

    /**
     * WebSocket（客户端给服务端发消息用）
     */
    MyWebSocketClient myWebSocketClient;

    public void connect(String ip) {
        new Thread() {
            @Override
            public void run() {
                connectSocketServer(ip);
            }
        }.start();
    }

    /**
     * 服务发现（客户端）
     *
     * @param clazz
     * @param parameters
     * @param <T>
     * @return
     */
    public <T> T getInstance(Class<T> clazz, Object... parameters) {
        // 实例化  服务发现
        sendRequest(clazz, null, parameters, NameServerManager.TYPE_GET);
        return getProxy(clazz);
    }

    /**
     * 向服务端发送消息（客户端）
     * @param clazz
     * @param method
     * @param parameters
     * @param type
     * @param <T>
     * @return
     */
    public <T> String sendRequest(Class<T> clazz, Method method, Object[] parameters, int type) {
        // socket  协议
        RequestParamter[] requestParamters = null;
        if (parameters != null && parameters.length > 0) {
            requestParamters = new RequestParamter[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Object parameter = parameters[i];
                String parameterClassName = parameter.getClass().getName();
                String parameterValue = GSON.toJson(parameter);
                RequestParamter requestParamter = new RequestParamter(parameterClassName, parameterValue);
                requestParamters[i] = requestParamter;
            }
        }
        String className = clazz.getAnnotation(ClassId.class).value();
        String methodName = method == null ? "getInstance" : method.getName();
        RequestBean requestBean = new RequestBean(type, className, methodName, requestParamters);
        String request = GSON.toJson(requestBean);

        synchronized (lock) {
            try {
                // 客户端给服务端发消息
                myWebSocketClient.send(request.getBytes());
                Log.i(TAG, "sendRequest: 锁住线程:" + Thread.currentThread().getName());
                lock.wait();
                Log.i(TAG, "sendRequest: 锁住成功");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Log.i(TAG, "sendRequest: 唤醒线程");
        if (!TextUtils.isEmpty(responce)) {
            String data1 = responce;
            responce = null;
            return data1;
        }
        return null;
    }

    /**
     * 动态代理
     * @param clazz
     * @param <T>
     * @return
     */
    private <T> T getProxy(Class<T> clazz) {
        ClassLoader classLoader = sContext.getClassLoader();
        return (T) Proxy.newProxyInstance(classLoader, new Class<?>[]{clazz},
                new NameServerInvokeHandler(clazz));
    }

    /**
     * 连接服务端
     * @param ip
     */
    private void connectSocketServer(String ip) {
        try {
            URI url = new URI(ip + NameConfig.IP_PORT);
            myWebSocketClient = new MyWebSocketClient(url);
            myWebSocketClient.connect();
            Log.i(TAG, "connect: 连接 ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 客户端Socket
     */
    class MyWebSocketClient extends WebSocketClient {
        public MyWebSocketClient(URI serverUri) {
            super(serverUri);
            Log.i(TAG, "MyWebSocketClient create serverUri:" + serverUri);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            Log.i(TAG, "onOpen: ");
        }

        @Override
        public void onMessage(String message) {
            Log.i(TAG, "onMessage: " + message);
        }

        public void onMessage(ByteBuffer message) {
            byte[] buf = new byte[message.remaining()];
            message.get(buf);
            String data = new String(buf);
            Log.i(TAG, "客户端收到信息 onMessage: " + data);
            responce = data;
            synchronized (lock) {
                try {
                    lock.notify();
                } catch (Exception e) {
                    Log.e(TAG, "onMessage: 锁异常: " + e.getMessage());
                }
            }
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            Log.i(TAG, "onClose: code: " + code + ", reason: " + reason + ", remote: " + remote);
        }

        @Override
        public void onError(Exception ex) {
            Log.e(TAG, "onError: " + ex.getMessage());
        }
    }
}
