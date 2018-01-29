package com.fansir.screenphone.devices;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;

import java.io.File;

/**
 * ADB工具类
 * Created by FanSir on 2018-01-26.
 */

public class AdbTools {
    private static boolean hasInitAdb = false; //是否初始化的标识
    private String adbPath = null; //ADB的路径
    private String adbPlatformTools = "platform-tools"; //platform-tools路径
    private AndroidDebugBridge mBridge; //ADB对象

    public AdbTools() {
        boolean success = false;
        while (!success) { //循环初始化设备
            success = initAdb();
        }
    }

    /**
     * 初始化ADB
     */
    private boolean initAdb() {
        boolean success = false;
        if (!hasInitAdb) {
            String adbPath = getAdbPath();
            if (adbPath != null) {
                AndroidDebugBridge.init(false);
                mBridge = AndroidDebugBridge.createBridge(adbPath, true);
                if (mBridge != null) { //初始化成功
                    success = true;
                    hasInitAdb = true;
                }
                //延时获取adb信息
                if (success) {
                    int loopCount = 0;
                    while (mBridge.hasInitialDeviceList() == false) { //循环初始化设备
                        try {
                            Thread.sleep(100);
                            loopCount++;
                        } catch (InterruptedException e) {
                        }
                        if (loopCount > 120) {
                            success = false;
                            break;
                        }
                    }
                }
            }
        }
        return success;
    }

    /**
     * 获取ADB路径
     */
    private String getAdbPath() {
        if (adbPath == null) {
            adbPath = System.getenv("ANDROID_HOME");
            if (adbPath != null) { //根据环境变量获取路径
                adbPath += File.separator + adbPlatformTools;
            } else { //自定义路径

            }
        }
        adbPath += File.separator + "adb.exe";
        return adbPath;
    }

    /**
     * 获取设备连接列表
     *
     * @return 设备列表
     */
    public IDevice[] getDevicesList() {
        IDevice[] devices = null;
        if (mBridge != null) {
            devices = mBridge.getDevices();
        }
        return devices;
    }
}
