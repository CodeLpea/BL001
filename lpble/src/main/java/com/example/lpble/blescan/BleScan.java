package com.example.lpble.blescan;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.lpble.permission.BlePermissionActivity;
import com.example.lpble.LpBleDevice;
import com.example.lpble.view.LoadProgressDialog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

public class BleScan implements IBleScan {
    private static final String TAG = "BleScan";
    private Handler handler = new Handler(Looper.getMainLooper());
    private Context context;
    //默认6秒
    private int times = 6 * 1000;
    private BleScanListenner listenner;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<LpBleDevice> bleDeviceArrayList = new ArrayList<>();

    public BleScan(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public IBleScan setTimes(int times) {
        this.times = times;
        return this;
    }

    @Override
    public IBleScan setListener(BleScanListenner listener) {
        this.listenner = listener;
        return this;
    }

    /**
     * 该方法不能在onResume，onStart中执行
     * 否则会造成一直处于检测权限的循环中。
     */
    @Override
    public void execute() {
        /*实例化*/
        mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        checkPermistion();
        /*开始搜索，准备计时*/
//        startScan();
    }

    private void checkPermistion() {
        //请求定位权限
        if (Build.VERSION.SDK_INT >= 23) {
            BlePermissionActivity.request(context, new BlePermissionActivity.PermistionCallBack() {
                @Override
                public void onSuccuss() {
                    /*开始搜索，准备计时*/
                    startScan();
                    Log.e(TAG, "同意了权限申请: ");
                }

                @Override
                public void onFaild() {
                    Log.e(TAG, "拒绝了权限申请: ");
                }

            });
        }

    }

    /**
     * 临时突然暂停
     */
    @Override
    public void interrupt() {
        if (scanTimer == null) {
            return;
        }
        scanTimer.cancel();
        timerTask.cancel();
        scanTimer = null;
        timerTask = null;
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            /*将搜索到的设备排序保存*/
            addDevice(new LpBleDevice(device, rssi));
        }
    };

    private void addDevice(LpBleDevice device) {
        for (int i = 0; i < bleDeviceArrayList.size(); i++) {
            //如果已经存在相同的地址，就返回
            if (bleDeviceArrayList.get(i).getDevice().getAddress().equals(device.getDevice().getAddress())) {
                return;
            }
        }
        bleDeviceArrayList.add(device);
        SortList(bleDeviceArrayList);
    }

    /**
     * 排序
     */
    private void SortList(ArrayList<LpBleDevice> list) {
        list.sort(new Comparator<LpBleDevice>() {
            @Override
            public int compare(LpBleDevice o1, LpBleDevice o2) {
                //rssi为负数大的在前面
                int i = o1.getRssi() > o2.getRssi() ? -1 : 1;
                return i;
            }
        });
    }

    /*开始搜索*/
    private void startScan() {
        mBluetoothAdapter.startLeScan(mLeScanCallback);
        StartTimer(times);

    }

    /**
     * 停止搜索
     */
    private void stopScan() {
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        //返回
        backToMainThread();
        if (scanTimer == null) {
            return;
        }
        scanTimer.cancel();
        timerTask.cancel();
        scanTimer = null;
        timerTask = null;

    }

    /**
     * 将结果返回到主线程中
     */
    private void backToMainThread() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                listenner.ScanDevice(bleDeviceArrayList);
            }
        });
    }

    private Timer scanTimer;
    private TimerTask timerTask;

    private void StartTimer(int times) {
        scanTimer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                //时间到则停止
                stopScan();
            }
        };
        scanTimer.schedule(timerTask, times);//times毫秒后执行
    }


}
