package com.yvan.yfdbus;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.yvan.yfdbus.request.RequestBean;
import com.yvan.yfdbus.request.RequestParamter;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * @author yvan
 * @date 2023/4/22
 * @description 服务端管理类
 */
public class NameServerManager extends Service {

    private static final String TAG = NameServerManager.class.getSimpleName();

    private final Gson gson = new Gson();

    //服务发现
    public static final int TYPE_GET = 1;
    //服务调用
    public static final int TYPE_INVOKE = 2;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: 开始 ");
        new Thread(new TcpServer()).start();
    }

    private class TcpServer implements Runnable {
        @Override
        public void run() {
            // 服务端开启SocketServer，等待客户端的请求
            SocketServer socketServer = new SocketServer();
            socketServer.start();
        }
    }

    class SocketServer extends WebSocketServer {

        public SocketServer() {
            super(new InetSocketAddress(NameConfig.IP_PORT));
        }

        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            Log.i(TAG, "onOpen: " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            Log.i(TAG, "onClose: " + conn.getRemoteSocketAddress().getAddress().getHostAddress()
                    + ", code:" + code
                    + ", reason:" + reason
                    + ", remote:" + remote);
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
        }

        @Override
        public void onMessage(WebSocket conn, ByteBuffer message) {
            Log.i(TAG, "onMessage: " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
            byte[] buf = new byte[message.remaining()];
            message.get(buf);
            String request = new String(buf);
            Log.i(TAG, "onMessage 接收到客户端消息: " + request);
            String responce = dealRequest(request);
            Log.i(TAG, "onMessage 向客户端发送数据: " + responce);
            // 服务端 向 客户端 发送响应
            conn.send(responce.getBytes());
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            Log.e(TAG, "onError: " + conn.getRemoteSocketAddress().getAddress().getHostAddress()
                    + ", ex:" + ex);
        }

        @Override
        public void onStart() {
            Log.i(TAG, "onStart: ");
        }
    }

    /**
     * 服务端处理客户端发送过来的消息，返回响应
     *
     * @param request
     * @return
     */
    String dealRequest(String request) {
        RequestBean requestBean = gson.fromJson(request, RequestBean.class);
        int type = requestBean.getType();
        //服务发现， 服务初始化
        switch (type) {
            case TYPE_INVOKE:
                Object object = IPCNameManager.getInstance().getNameCenter().getObject(requestBean.getClassName());
                Method tempMethod = IPCNameManager.getInstance().getNameCenter().getMethod(requestBean);
                Object[] mParameters = makeParameterObject(requestBean);
                try {
                    Object result = tempMethod.invoke(object, mParameters);
                    String data = gson.toJson(result);
                    return data;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                break;
            case TYPE_GET:
                Method method = IPCNameManager.getInstance().getNameCenter().getMethod(requestBean);
                Object[] parameters = makeParameterObject(requestBean);
                try {
                    Object object1 = method.invoke(null, parameters);
                    IPCNameManager.getInstance().getNameCenter().putObject(requestBean.getClassName(), object1);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                return "success";
        }
        return null;
    }

    /**
     * 获取方法参数的值
     *
     * @param requestBean
     * @return
     */
    private Object[] makeParameterObject(RequestBean requestBean) {
        Object[] mParameters = null;
        RequestParamter[] requestParamters = requestBean.getRequestParamters();
        if (requestParamters != null && requestParamters.length > 0) {
            mParameters = new Object[requestBean.getRequestParamters().length];
            for (int i = 0; i < requestParamters.length; i++) {
                RequestParamter requestParamter = requestParamters[i];
                Class<?> clazz = IPCNameManager.getInstance().getNameCenter().getClassType(requestParamter.getParameterClassName());
                mParameters[i] = gson.fromJson(requestParamter.getParameterValue(), clazz);
            }

        } else {
            mParameters = new Object[0];
        }
        return mParameters;
    }

}
