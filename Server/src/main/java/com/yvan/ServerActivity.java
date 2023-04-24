package com.yvan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.yvan.nameserver.NameServer;
import com.yvan.nameserver.R;
import com.yvan.yfdbus.IPCNameManager;
import com.yvan.yfdbus.NameServerManager;

/**
 * @author yvan
 * @date 2023/4/22
 * @description 服务端页面
 */
public class ServerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 管理服务的启动
        Intent intent = new Intent(this, NameServerManager.class);
        startService(intent);
        // 服务注册
        IPCNameManager.getInstance().register(NameServer.class);
    }
}