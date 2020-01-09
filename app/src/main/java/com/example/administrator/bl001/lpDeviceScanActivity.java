/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.administrator.bl001;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lpble.blescan.BleScanListenner;
import com.example.lpble.LpBle;
import com.example.lpble.LpBleDevice;
import com.example.lpble.view.LoadProgressDialog;

import java.util.ArrayList;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class lpDeviceScanActivity extends ListActivity {
    private static final String TAG = "lpDeviceScanActivity";
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private LoadProgressDialog loadProgressDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initializes list view adapter.
        mLeDeviceListAdapter = new lpDeviceScanActivity.LeDeviceListAdapter();
        loadProgressDialog=new LoadProgressDialog(this,"蓝牙搜索中……");
        setListAdapter(mLeDeviceListAdapter);
        LpBle.getInstance().ScanBle().setListener(bleScanListenner).execute();
        //显示
        loadProgressDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
        mLeDeviceListAdapter.notifyDataSetInvalidated();
//        LpBle.getInstance().ScanBle().setListener(bleScanListenner).execute();

    }

    private BleScanListenner bleScanListenner = new BleScanListenner() {
        @Override
        public void ScanDevice(ArrayList<LpBleDevice> lpBleDeviceList) {
            Log.i(TAG, "扫描到的蓝牙 " + lpBleDeviceList.size());
            mLeDeviceListAdapter.mLeDevices = lpBleDeviceList;
            //销毁
            loadProgressDialog.dismiss();
            mLeDeviceListAdapter.notifyDataSetChanged();

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LpBle.getInstance().ScanBle().interrupt();
        mLeDeviceListAdapter.clear();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final LpBleDevice device = mLeDeviceListAdapter.getDevice(position);
        if (device == null) return;
        final Intent intent = new Intent(this, LpBleSppActivity.class);
        intent.putExtra(BleSppActivity.EXTRAS_DEVICE_NAME, device.getDevice().getName());
        intent.putExtra(BleSppActivity.EXTRAS_DEVICE_ADDRESS, device.getDevice().getAddress());
        LpBle.getInstance().ScanBle().interrupt();
        startActivity(intent);
    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<LpBleDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<LpBleDevice>();
            mInflator = lpDeviceScanActivity.this.getLayoutInflater();
        }


        public LpBleDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                viewHolder.deviceRssi = (TextView) view.findViewById(R.id.device_rssi);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            LpBleDevice device = mLeDevices.get(i);
            final String deviceName = device.getDevice().getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getDevice().getAddress());
            viewHolder.deviceRssi.setText(String.valueOf(device.getRssi()));

            return view;
        }
    }


    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceRssi;
    }


}