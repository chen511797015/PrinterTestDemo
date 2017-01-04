package cn.pax.util;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Parcelable;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by chendd on 2016/10/27.
 */

public class UsbUtil {

    private Context mContext;//上下文对象

    private String TAG = "TAG";

    private UsbManager mUsbManager;//USB管理器

    private UsbDevice mUsbDevice;//USB设备

    private PendingIntent mPendingIntent;//USB意图对象

    private UsbDeviceConnection mConnection;

    private UsbEndpoint mUsbEndpoint;//传输数据所需要的端口号

    StringBuilder sb;

    public StringBuilder getSb() {
        return sb;
    }

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    public UsbUtil(Context mContext) {
        this.mContext = mContext;

        sb = new StringBuilder();

        //要获得usb设备,需要先注册广播接收者
        IntentFilter usbFilter = new IntentFilter();
        usbFilter.addAction(ACTION_USB_PERMISSION);
        mContext.registerReceiver(mBroadcastReceiver, usbFilter);

        mPendingIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);

        //获取usb管理者
        mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);

        //获取usb设备
        GetUsbDevice();

    }

    /**
     * 获取usb设备
     */
    private void GetUsbDevice() {
        if (mUsbManager != null) {
            HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();//获取usb设备队列
            Iterator<UsbDevice> iterator = deviceList.values().iterator();//迭代器
            while (iterator.hasNext()) {
                UsbDevice device = iterator.next();
                mUsbManager.requestPermission(device, mPendingIntent);
            }
        }
    }

    /**
     * 需要广播接收者来接收usb插入信息
     */
    public final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (ACTION_USB_PERMISSION.equals(action)) {
                Log.d(TAG, "USB检测成功: " + action);
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);//获取usb设备
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {//是否取得usb权限信息
                        Log.d(TAG, "取得usb权限信息 ");
                        sb.append("\r\n取得usb权限信息 " + "\r\n");
                        if (device != null) {
                            setDevice(device);
                        } else {
                            mUsbDevice = device;
                        }
                    } else {
                        Log.d(TAG, "没有取得usb权限信息 ");
                    }
                }

            }
        }
    };

    /**
     * 设置USB信息,获取USB具体信息,判断其是否为打印机
     *
     * @param device
     */
    private void setDevice(UsbDevice device) {
        if (device != null) {
            UsbInterface mUsbInterface = null;
            UsbEndpoint usbEndpoint = null;
            mUsbDevice = device;

            int interfaceCount = device.getInterfaceCount();//获取接口参数
            Log.d(TAG, "接口参数: " + interfaceCount);
            sb.append("接口参数: " + interfaceCount + "\r\n");

            int i;
            for (i = 0; i < interfaceCount; i++) {
                mUsbInterface = device.getInterface(i);//usb接口信息
                Log.d(TAG, "接口是: " + i + ",类是:" + mUsbInterface.getInterfaceClass());
                sb.append("接口是: " + i + ",类是:" + mUsbInterface.getInterfaceClass() + "\r\n");
                if (mUsbInterface.getInterfaceClass() == 7) {//7表示是当前打印机接口
                    int endpointCount = mUsbInterface.getEndpointCount();//获取端点数
                    Log.i(TAG, "端点数: " + endpointCount);
                    sb.append("端点数: " + endpointCount + "\r\n");
                    int j;
                    for (j = 0; j < endpointCount; j++) {
                        usbEndpoint = mUsbInterface.getEndpoint(j);
                        Log.i(TAG, "端点是: " + j + ",方向是:" + usbEndpoint.getDirection() + ",类型是:" + usbEndpoint.getType());
                        sb.append("端点是: " + j + ",方向是:" + usbEndpoint.getDirection() + ",类型是:" + usbEndpoint.getType() + "\r\n");
                        if (usbEndpoint.getDirection() == 0 && usbEndpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {//方向为0,类型为2(大部分端点类型)
                            Log.i(TAG, "接口是: " + i + "端点是:" + j);
                            break;
                        }

                    }
                    if (j != endpointCount) {
                        break;
                    }
                }
            }

            //没有打印机接口
            if (i == interfaceCount) {
                Log.e(TAG, "没有打印机接口 ");

            }

            mUsbEndpoint = usbEndpoint;

            try {
                //打开usb连接
                if (device != null) {
                    UsbDeviceConnection connection = mUsbManager.openDevice(device);
                    if (connection != null & connection.claimInterface(mUsbInterface, true)) {//传输数据之前必须申明接口
                        Log.d(TAG, "USB设备打开成功");
                        mConnection = connection;
                    } else {
                        Log.d(TAG, "USB设备打开失败");
                        mConnection = null;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 传输数据
     *
     * @param mContent
     * @return
     */
    public boolean sendCommand(byte[] mContent) {
        boolean result;
        synchronized (this) {
            int transfer = -1;
            if (mConnection != null) {
                transfer = mConnection.bulkTransfer(mUsbEndpoint, mContent, mContent.length, 5000);
            }

            //返回值大于等于0,发送成功
            if (transfer >= 0) {
                result = true;
                Log.d(TAG, "发送成功: " + transfer + ",个字节");
            } else {
                result = false;
                Log.d(TAG, "发送失败");
            }

        }
        return result;

    }

}
