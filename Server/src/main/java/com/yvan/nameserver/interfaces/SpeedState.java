package com.yvan.nameserver.interfaces;

/**
 * @author yvan
 * @date 2023/4/22
 * @description 服务端速度，能源容量信息类
 */
public class SpeedState {

    int speed;
    int energy;

    public SpeedState() {
    }

    public SpeedState(int speed, int energy) {
        this.speed = speed;
        this.energy = energy;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }
}
