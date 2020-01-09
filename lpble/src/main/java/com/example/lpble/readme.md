这是一个Ble使用模块
低功耗蓝牙的搜索，连接，收发数据

1.初始化：init(Application)
在Application中进行初始化，自动实例化，搜索和连接模块
```
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        LpBle.getInstance().init(this);
    }
}
```

2.搜索蓝牙信息ScanBle()
搜索出包括蓝牙名称，蓝牙地址，蓝牙型号等。方便使用数据进行预览。
搜索时，可以设定具体的搜索时间(默认6秒)，搜索提示框等。
搜索出来的设备信息已经按照信号强度进行了排序
```
 //显示
  loadProgressDialog.show();
  LpBle.getInstance().ScanBle()
  .setListener(bleScanListenner)
  .setTimes(5*1000)(
  .execute();
  
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
```
3.连接蓝牙设备ConnectBle()
可以设置收发信息的格式HEX/ASCII
设置回调监听，返回连接情况，和信息
连接设备execute(mDeviceAddress);    
```
  //连接
        LpBle.getInstance().ConnectBle()
                .setDataType(IBleConnect.BleDataType.HEX)
                .setBleProgessListenner(new IBleConnect.LpBleInfoListenner() {
                    @Override
                    public void onConnect(String info) {
                        Log.e(TAG, "onConnect:连接成功 " + info);
                    }

                    @Override
                    public void onDisConnect(String info) {
                        Log.e(TAG, "onDisConnect: 连接断开" + info);
                    }

                    @Override
                    public void receiveInfo(String info) {
                        Log.e(TAG, "receiveInfo:收到的信息 " + info);
                        mDataRecvText.setText(info.toString());
                    }
                })
                .execute(mDeviceAddress);

```
4.发送信息
收到连接成功的回调之后，就可以进行信息的发送
```
   LpBle.getInstance().ConnectBle().sendInfo("132456");

```

5.断开连接
```
  LpBle.getInstance().ConnectBle().disConnect();
  
```

备注：已经增加了权限的申请和蓝牙开关的控制。