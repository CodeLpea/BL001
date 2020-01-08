package com.example.lpble;

import android.bluetooth.BluetoothDevice;

public class LpBleDevice {

    public LpBleDevice(BluetoothDevice device, int rssi) {
        this.device = device;
        this.rssi = rssi;
    }

    private BluetoothDevice device;
    private int rssi;

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
}
