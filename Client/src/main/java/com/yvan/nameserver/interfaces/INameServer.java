package com.yvan.nameserver.interfaces;

import com.yvan.yfdbus.annotion.ClassId;

/**
 * @author yvan
 * @date 2023/4/22
 * @description 客户端的服务类（对应服务端com.yvan.nameserver.NameServer类）
 */
@ClassId("com.yvan.nameserver.NameServer")
public interface INameServer {
    /**
     * 速度，能源 信息获取
     *
     * @param userId
     * @param token
     * @return
     */
    SpeedState getSpeed(String userId, String token);
}
