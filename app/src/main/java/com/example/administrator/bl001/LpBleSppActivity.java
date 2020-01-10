package com.example.administrator.bl001;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.lpble.LpBle;
import com.example.lpble.bleconnect.IBleConnect;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class LpBleSppActivity extends Activity implements View.OnClickListener {
    private final static String TAG = LpBleSppActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    static long recv_cnt = 0;

    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";


    private TextView mDataRecvText;
    private TextView mRecvBytes;
    private TextView mDataRecvFormat;
    private EditText mEditBox;
    private TextView mSendBytes;
    private TextView mDataSendFormat;
    private TextView mNotify_speed_text;

    private long recvBytes = 0;
    private long lastSecondBytes = 0;
    private long sendBytes;
    private StringBuilder mData;

    int sendIndex = 0;
    int sendDataLen = 0;
    byte[] sendBuf;

    //测速
    private Timer timer;
    private TimerTask task;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.gatt_services_characteristics);
        setContentView(R.layout.ble_spp);

        //获取蓝牙的名字和地址
        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        mDataRecvText = (TextView) findViewById(R.id.data_read_text);
        mRecvBytes = (TextView) findViewById(R.id.byte_received_text);
        mDataRecvFormat = (TextView) findViewById(R.id.data_received_format);
        mEditBox = (EditText) findViewById(R.id.data_edit_box);
        mSendBytes = (TextView) findViewById(R.id.byte_send_text);
        mDataSendFormat = (TextView) findViewById(R.id.data_sended_format);
        mNotify_speed_text = (TextView) findViewById(R.id.notify_speed_text);

        Button mSendBtn = (Button) findViewById(R.id.send_data_btn);
        Button mCleanBtn = (Button) findViewById(R.id.clean_data_btn);
        Button mCleanTextBtn = (Button) findViewById(R.id.clean_text_btn);

        mDataRecvFormat.setOnClickListener(this);
        mDataSendFormat.setOnClickListener(this);
        mRecvBytes.setOnClickListener(this);
        mSendBytes.setOnClickListener(this);

        mCleanBtn.setOnClickListener(this);
        mSendBtn.setOnClickListener(this);
        mCleanTextBtn.setOnClickListener(this);
        mDataRecvText.setMovementMethod(ScrollingMovementMethod.getInstance());
        mData = new StringBuilder();

        final int SPEED = 1;
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SPEED:
                        lastSecondBytes = recvBytes - lastSecondBytes;
                        mNotify_speed_text.setText(String.valueOf(lastSecondBytes) + " B/s");
                        lastSecondBytes = recvBytes;
                        break;
                }
            }
        };

        task = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = SPEED;
                message.obj = System.currentTimeMillis();
                handler.sendMessage(message);
            }
        };

        timer = new Timer();
        // 参数：
        // 1000，延时1秒后执行。
        // 1000，每隔2秒执行1次task。
        timer.schedule(task, 1000, 1000);

//        getActionBar().setTitle(mDeviceName);
//        getActionBar().setDisplayHomeAsUpEnabled(true);

        //连接
        LpBle.getInstance().ConnectBle()
                .setDataType(IBleConnect.BleDataType.ASCII)
                .setBleProgessListenner(new IBleConnect.LpBleInfoListenner() {
                    @Override
                    public void onConnectting(String info) {
                        mDataRecvText.setText(info.toString());
                    }
                    @Override
                    public void onConnect(String info) {
                        Log.e(TAG, "onConnect: " + info);
                        mDataRecvText.setText(info.toString());
                    }

                    @Override
                    public void onDisConnect(String info) {
                        Log.e(TAG, "onDisConnect: " + info);
                        mDataRecvText.setText(info.toString());
                    }

                    @Override
                    public void receiveInfo(String info) {
                        Log.e(TAG, "receiveInfo: " + info);
                        mDataRecvText.setText(info.toString());
                    }
                })
                .execute(mDeviceAddress);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        LpBle.getInstance().ConnectBle().disConnect();

    }


    //动态效果
    public void convertText(final TextView textView, final int convertTextId) {
        final Animation scaleIn = AnimationUtils.loadAnimation(this,
                R.anim.text_scale_in);
        Animation scaleOut = AnimationUtils.loadAnimation(this,
                R.anim.text_scale_out);
        scaleOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                textView.setText(convertTextId);
                textView.startAnimation(scaleIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        textView.startAnimation(scaleOut);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.data_received_format:
                if (mDataRecvFormat.getText().equals(getResources().getString(R.string.data_format_default))) {
                    //连接
                    LpBle.getInstance().ConnectBle()
                            .setDataType(IBleConnect.BleDataType.HEX);
                    convertText(mDataRecvFormat, R.string.data_format_hex);
                } else {
                    convertText(mDataRecvFormat, R.string.data_format_default);
                    //连接
                    LpBle.getInstance().ConnectBle()
                            .setDataType(IBleConnect.BleDataType.ASCII);
                }
                break;

            case R.id.data_sended_format:
                if (mDataSendFormat.getText().equals(getResources().getString(R.string.data_format_default))) {
                    //连接
                    LpBle.getInstance().ConnectBle()
                            .setDataType(IBleConnect.BleDataType.HEX);
                    convertText(mDataSendFormat, R.string.data_format_hex);
                } else {
                    convertText(mDataSendFormat, R.string.data_format_default);
                    //连接
                    LpBle.getInstance().ConnectBle()
                            .setDataType(IBleConnect.BleDataType.ASCII);
                }
                break;

            case R.id.byte_received_text:
                recvBytes = 0;
                lastSecondBytes = 0;
                convertText(mRecvBytes, R.string.zero);
                break;

            case R.id.byte_send_text:
                sendBytes = 0;
                convertText(mSendBytes, R.string.zero);
                break;

            case R.id.send_data_btn:
                Log.e(TAG, "onClick: " + mEditBox.getText().toString());
                LpBle.getInstance().ConnectBle().sendInfo(mEditBox.getText().toString());

                break;

            case R.id.clean_data_btn:
                mData.delete(0, mData.length());
                mDataRecvText.setText(mData.toString());
                break;

            case R.id.clean_text_btn:
                mEditBox.setText("");
                break;

            default:
                break;
        }
    }
}
