package com.rms_counter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import com.zj.usbsdk.UsbController;

import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortFinder;

public class PrinterUtils {
	private static Activity activity;
	private static int[][] u_infor;
	private static UsbController usbCtrl = null;
	private static UsbDevice dev = null;
	private static SerialPortFinder mSerialPortFinder = new SerialPortFinder();
	private static SerialPort mSerialPort = null;
	private static OutputStream mOutputStream;
	
	public PrinterUtils(Activity activity){
		this.activity = activity;
		
		u_infor = new int[6][2];
		u_infor[0][0] = 0x1CBE;
		u_infor[0][1] = 0x0003;
		u_infor[1][0] = 0x1CB0;
		u_infor[1][1] = 0x0003;
		u_infor[2][0] = 0x0483;
		u_infor[2][1] = 0x5740;
		u_infor[3][0] = 0x0493;
		u_infor[3][1] = 0x8760;
		u_infor[4][0] = 0x0416;
		u_infor[4][1] = 0x5011;
		u_infor[5][0] = 0xB000;
		u_infor[5][1] = 0x0412;

		usbCtrl = new UsbController(activity, mHandler);
	}
	
	public static void connectLED8() {
		String[] entryValues = mSerialPortFinder.getAllDevicesPath();
		String path = entryValues[4];
		int baudrate = 2400;
		try {
			mSerialPort = new SerialPort(new File(path), baudrate, 0);
			mOutputStream = mSerialPort.getOutputStream();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean connectPrinter() {
		boolean isConnect = false;
		usbCtrl.close();
		int i = 0;
		for (i = 0; i < 6; i++) {
			dev = usbCtrl.getDev(u_infor[i][0], u_infor[i][1]);
			if (dev != null)
				break;
		}

		if (dev != null) {
			if (!(usbCtrl.isHasPermission(dev))) {
				usbCtrl.getPermission(dev);
			} else {
				Log.d("USB", "Printer is connected.");
			}
			isConnect = true;
			
		}
		return isConnect;
	}
	
	public static void printData(String sendedData) {
		byte isHasPaper;
		isHasPaper = usbCtrl.revByte(dev);
		if (isHasPaper == 0x38) {
			Toast.makeText(activity.getBaseContext(),
					"The printer has no paper", Toast.LENGTH_SHORT).show();
			return;
		}
		usbCtrl.sendMsg(sendedData, "GBK", dev);

	}

	public static void printOpen() {
		byte[] cmd = new byte[5];
		cmd[0] = 0x1B;
		cmd[1] = 0x70;
		cmd[2] = 0x00;
		cmd[3] = 0x30;
		cmd[4] = 0x30;
		usbCtrl.sendByte(cmd, dev);
	}
	
	public static void showData(String str) {
		try {
			byte[] b1 = new byte[8];
			b1[0] = 0x0c;
			mOutputStream.write(b1);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			byte[] b2 = new byte[8];
			b2[0] = 0x1b;
			b2[1] = 0x73;
			b2[2] = 0x32;
			mOutputStream.write(b2);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d("print", str);
			mOutputStream.write(new String(str).getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UsbController.USB_CONNECTED:
				break;
			default:
				break;
			}
		}
	};
}
