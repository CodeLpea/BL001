package com.example.lpble.blescan;

/**
 * Ble蓝牙搜索接口
 * */
public interface IBleScan {
    //设置搜索时间
    IBleScan setTimes(int times);
    //返回结果
    IBleScan setListener(BleScanListenner listener);
    //执行
    void execute();
    //突然停止
    void interrupt();

}
