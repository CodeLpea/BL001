package com.example.lpble.bleconnect;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.example.lpble.permission.BlePermissionActivity;

import static android.content.Context.BIND_AUTO_CREATE;
import static com.example.lpble.bleconnect.StringUtils.getHexString;

public class BleConnect implements IBleConnect {
    private static final String TAG = "BleConnect";
    private Context context;
    private LpBleInfoListenner lpBleInfoListenner;
    private BleUpdateReceiver receiver;
    private BluetoothLeService mBluetoothLeService;
    //数据类型，默认为HEX
    private BleDataType bleDataType= BleDataType.HEX;
    private long sendBytes;

    int sendIndex = 0;
    int sendDataLen = 0;
    byte[] sendBuf;

    public BleConnect(Context context) {
        this.context = context;
        init();
    }

    @Override
    public boolean execute(String mac) {
        boolean connect = mBluetoothLeService.connect(mac);
        return connect;
    }

    @Override
    public boolean disConnect() {
        mBluetoothLeService.close();
        return false;
    }

    @Override
    public void sendInfo(String messge) {
        sendInfoToBle(messge);
    }

    @Override
    public IBleConnect setDataType(BleDataType type) {
        bleDataType=type;
        receiver.setBleDataType(bleDataType);
        return this;
    }

    @Override
    public IBleConnect setBleProgessListenner(LpBleInfoListenner lpBleInfoListenner) {
        this.lpBleInfoListenner = lpBleInfoListenner;
        receiver.setLpBleInfoListenner(lpBleInfoListenner);
        receiver.setBleDataType(bleDataType);
        return this;
    }

    /*初始化*/
    private void init() {
        receiver = new BleUpdateReceiver();
        context.registerReceiver(receiver, makeGattUpdateIntentFilter());
        context.bindService(new Intent(context, BluetoothLeService.class), bleServiceConnect, BIND_AUTO_CREATE);

    }

    private IntentFilter makeGattUpdateIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_WRITE_SUCCESSFUL);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_NO_DISCOVERED);
        return intentFilter;
    }

    private ServiceConnection bleServiceConnect = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
//                finish();
                lpBleInfoListenner.onDisConnect("Unable to initialize Bluetooth");
            }
            // Automatically connects to the device upon successful start-up initialization.
//            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void sendInfoToBle(String str) {
        switch (bleDataType){
            case HEX:
                //如果是hex数据则需要转换
                sendBuf = StringUtils.stringToHexBytes(getHexString(str));
                break;
            case ASCII:
                //AscII直接转
                sendBuf = str.getBytes();
                break;
        }
        sendIndex = 0;
        sendDataLen = sendBuf.length;
        //如果大于20个字节需要分包
        if (sendDataLen > 20) {
            sendBytes += 20;
            final byte[] buf = new byte[20];
            for (int i = 0; i < 20; i++) {
                buf[i] = sendBuf[sendIndex + i];
            }
            sendIndex += 20;
            mBluetoothLeService.writeData(buf);
            sendDataLen -= 20;
        } else {
            sendBytes += sendDataLen;
            final byte[] buf = new byte[sendDataLen];
            for (int i = 0; i < sendDataLen; i++) {
                buf[i] = sendBuf[sendIndex + i];
            }
            mBluetoothLeService.writeData(buf);
            sendDataLen = 0;
            sendIndex = 0;
        }
    }

}
