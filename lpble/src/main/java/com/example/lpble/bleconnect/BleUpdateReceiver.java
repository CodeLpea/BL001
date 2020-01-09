package com.example.lpble.bleconnect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static com.example.lpble.bleconnect.StringUtils.asciiToString;
import static com.example.lpble.bleconnect.StringUtils.bytesHexToString;

public class BleUpdateReceiver extends BroadcastReceiver {
    private static final String TAG = "BleUpdateReceiver";
    private IBleConnect.LpBleInfoListenner lpBleInfoListenner;
    private IBleConnect.BleDataType bleDataType;

    public BleUpdateReceiver() {
    }

    public void setLpBleInfoListenner(IBleConnect.LpBleInfoListenner lpBleInfoListenner) {
        this.lpBleInfoListenner = lpBleInfoListenner;
    }

    public void setBleDataType(IBleConnect.BleDataType bleDataType) {
        this.bleDataType = bleDataType;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (lpBleInfoListenner == null) {
            //如果没有设置监听，则直接返回
            return;
        }
        if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
            //当蓝牙连接状态发生变化时，会被调用。
        } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
            //当断开连接的时候再次请求当前现有的连接，当重新寻找到GATT的时候，就可以再次自动连接。
            lpBleInfoListenner.onDisConnect("已经断开连接");
        } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
            //特征值找到才代表连接成功
            lpBleInfoListenner.onConnect("连接成功");

        } else if (BluetoothLeService.ACTION_GATT_SERVICES_NO_DISCOVERED.equals(action)) {
            lpBleInfoListenner.onDisConnect("连接失败，不是BLE-SPS设备");

        }
        //接收到信息
        else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
            String data = reformatData(intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA));
            Log.e(TAG, "接收到信息: " + data);
            lpBleInfoListenner.receiveInfo(data);

        }
        //发送完之后,确认
        else if (BluetoothLeService.ACTION_WRITE_SUCCESSFUL.equals(action)) {
            //当写入数据发送完成则会调用此处来确认
            Log.e(TAG, "发送完之后: ");
        }

    }

    /**
     * 转换格式为String
     */
    private String reformatData(byte[] buf) {
        switch (bleDataType) {
            case ASCII:
                return asciiToString(buf);
            case HEX:
                return bytesHexToString(buf);
        }
        //如果是hex数据
        return bytesHexToString(buf);
    }
}
