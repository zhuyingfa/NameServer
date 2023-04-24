package com.yvan.client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.yvan.clent.R;
import com.yvan.nameserver.interfaces.INameServer;
import com.yvan.nameserver.interfaces.SpeedState;
import com.yvan.yfdbus.IPCNameManager;
import com.yvan.yfdbus.NameConfig;

/**
 * @author yvan
 * @date 2023/4/22
 * @description 客户端页面
 */
public class ClientActivity extends AppCompatActivity {

    private static final String TAG = ClientActivity.class.getSimpleName();
    private INameServer iNameServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IPCNameManager.getInstance().init(this);
    }

    /**
     * 连接服务端
     *
     * @param view
     */
    public void connect(View view) {
        IPCNameManager.getInstance().connect(NameConfig.IP);
    }

    /**
     * 发现服务
     *
     * @param view
     */
    public void findServer(View view) {
        iNameServer = IPCNameManager.getInstance().getInstance(INameServer.class, "小明的车", "token666");
        Log.i(TAG, "findServer: 服务发现  发现远端设备的服务  ");
    }

    /**
     * 服务调用
     *
     * @param view
     */
    public void invekeServer(View view) {
        Log.i(TAG, "invekeServer: 服务调用 " + iNameServer);
        new Thread() {
            @Override
            public void run() {
                SpeedState speedState = iNameServer.getSpeed("小明的车", "token666");
                Log.i(TAG, "invekeServer: 结果远端设备的速度: " + speedState.getSpeed()
                        + " 远端设备的电池容量: " + speedState.getEnergy());
            }
        }.start();
    }
}