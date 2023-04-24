package com.yvan.nameserver;

import android.util.Log;

import com.yvan.nameserver.interfaces.INameServer;
import com.yvan.nameserver.interfaces.SpeedState;

/**
 * @author yvan
 * @date 2023/4/22
 * @description 服务端的服务实现类
 */
public class NameServer implements INameServer {

    private final static String TAG = NameServer.class.getSimpleName();
    private static NameServer sInstance = null;

    public static synchronized NameServer getInstance(String userId, String token) {
        Log.i(TAG, "getInstance:userId  " + userId + " token  " + token);
        if (sInstance == null) {
            sInstance = new NameServer();
        }
        return sInstance;
    }

    @Override
    public SpeedState getSpeed(String userId, String token) {
        return new SpeedState(180, 55);
    }
}
