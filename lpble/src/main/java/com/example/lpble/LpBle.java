package com.example.lpble;

import android.bluetooth.BluetoothManager;
import android.content.Context;

import com.example.lpble.blescan.BleScan;
import com.example.lpble.blescan.IBleScan;

/**
 * 低功耗蓝牙入口
 * 包含搜索连接入口，连接入口
 * */
public class LpBle {
    private Context context;
    private  static class LpBleInner{
        private  static LpBle lpBle=new LpBle();
    }
    private BleScan bleScan;
    public static LpBle getInstance(){
        return LpBleInner.lpBle;
    }
    public void init(Context context){
        bleScan=new BleScan(context);
        this.context=context.getApplicationContext();
    }
    /**
     * 获取搜索对象，设置搜索时间和返回接口
     * */
    public IBleScan ScanBle(){
        return bleScan;
    }

}
