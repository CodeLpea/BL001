package com.example.lpble.bleconnect;

/**
 * ble连接接口
 * */
public interface IBleConnect {
    //输入mac地址连接
    boolean execute(String mac);
    //断开连接
    boolean disConnect();
    void sendInfo(String messge);

    IBleConnect setDataType(BleDataType type);

    IBleConnect setBleProgessListenner(LpBleInfoListenner lpBleInfoListenner);

    //回调监听
    public interface LpBleInfoListenner{
        //连接成功回调
        void onConnect(String info);
        //连接断开回调
        void onDisConnect(String info);
        //接收到蓝牙模块信息的回调
        void receiveInfo(String info);
    }

    //传输数据类型
    public enum  BleDataType{
        ASCII,
        HEX
    }
}
