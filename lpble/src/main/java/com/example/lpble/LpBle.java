package com.example.lpble;

import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.util.Log;

import com.example.lpble.bleconnect.BleConnect;
import com.example.lpble.bleconnect.IBleConnect;
import com.example.lpble.blescan.BleScan;
import com.example.lpble.blescan.IBleScan;
import com.example.lpble.permission.BlePermissionActivity;

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
    private BleConnect bleConnect;
    public static LpBle getInstance(){
        return LpBleInner.lpBle;
    }
    /**
     * 在Application中初始化
     * */
    public void init(Context context){
        this.context=context.getApplicationContext();
        bleScan=new BleScan(context);
        bleConnect=new BleConnect(context);

    }
    /**
     * 获取搜索对象，设置搜索时间和返回接口
     * */
    public IBleScan ScanBle(){
        return bleScan;
    }

    public IBleConnect ConnectBle(){
        return bleConnect;
    }

}
