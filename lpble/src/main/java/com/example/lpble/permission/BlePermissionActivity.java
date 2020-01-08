package com.example.lpble.permission;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

public class BlePermissionActivity extends PermissionActivity {
    private static String TAG = "BlePermissionActivity";
    private PermisonListener permisonListener;
    private Context context;
    private static PermistionCallBack mPermistionCallBack;
    private String[] Permisons = {Manifest.permission.ACCESS_COARSE_LOCATION};
    private int BLUTOOTHCODE = 13456465;
    private int LOCATIONCODE = 12456467;

    public interface PermistionCallBack {
        void onSuccuss();

        void onFaild();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        permisonListener = new PermisonListener();
        performRequestPermissions("拒绝权限后请到设置中手动开启权限", Permisons, 101, permisonListener);
    }

    public static synchronized void request(Context context, PermistionCallBack mPermistionCallBack) {
        Log.e("BlePermissionActivity", mPermistionCallBack.toString());
        BlePermissionActivity.mPermistionCallBack = mPermistionCallBack;
        context.startActivity(new Intent(context, BlePermissionActivity.class));
    }

    private class PermisonListener implements PermissionsResultListener {
        @Override
        public void onPermissionGranted() {
            //检查是否权限和开关都打开了
            checkEable();
        }

        @Override
        public void onPermissionDenied() {
            finish();

        }
    }

    /**
     * 检查是否权限和开关都打开了
     * 没打开就反复询问
     * */
    private boolean checkEable() {
        //如果没有打开定位
        if (!isLocServiceEnable(context)) {
            //定位服务页面
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, LOCATIONCODE);
            return false;
        } else {
            /*如果没有打开蓝牙则打开*/
            if (!isBlutoothEnable(context)) {
                Log.e("BlePermissionActivity", "如果没有打开蓝牙则打开: ");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, BLUTOOTHCODE);
                return false;
            }
        }
        //都打开了就返回成功
        mPermistionCallBack.onSuccuss();
        finish();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult: ");
        checkEable();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 手机是否开启位置服务，如果没有开启那么所有app将不能使用定位功能
     */
    public static boolean isLocServiceEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    /**
     * 手机是否开启蓝牙，如果没有开启那么所有app将不能使用定位功能
     */
    public static boolean isBlutoothEnable(Context context) {
        BluetoothManager mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter mBluetoothAdapter = mBluetoothManager.getAdapter();
        /*如果没有打开蓝牙则打开*/
        if (!mBluetoothAdapter.isEnabled()) {
            return false;
        }
        return true;
    }

}
