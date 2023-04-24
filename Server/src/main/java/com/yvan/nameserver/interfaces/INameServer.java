package com.yvan.nameserver.interfaces;

/**
 * @author yvan
 * @date 2023/4/22
 * @description 服务端的服务类
 */
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
