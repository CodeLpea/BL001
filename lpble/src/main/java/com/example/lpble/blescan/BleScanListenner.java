package com.example.lpble.blescan;

import com.example.lpble.LpBleDevice;

import java.util.ArrayList;

public interface BleScanListenner {
    void ScanDevice(ArrayList<LpBleDevice> lpBleDeviceList);
}
