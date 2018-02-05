package com.wuba.gui;

import com.android.chimpchat.ChimpManager;
import com.android.chimpchat.adb.AdbChimpDevice;
import com.android.chimpchat.core.IChimpDevice;
import com.android.chimpchat.core.TouchPressType;
import com.android.ddmlib.IDevice;

/**
 * Created by Administrator on 2017-07-20.
 */

public class OperateAndroid {
    private AdbChimpDevice device;
    private ChimpManager manager;
    private static OperateAndroid oa;

    public static String HOME = "KEYCODE_HOME";
    public static String BACK = "KEYCODE_BACK";
    public static String MENU = "KEYCODE_MENU";
    public static String POWER = "KEYCODE_POWER";
    private IChimpDevice chimpDevice;


    public OperateAndroid(IDevice dev, int port) {
        if (device == null) {
            chimpDevice = new AdbChimpDevice(dev);
            device = (AdbChimpDevice) chimpDevice;
            manager = device.getManager();
//            chimpDevice = new AdbChimpTest(dev, port);
//            device = (AdbChimpTest) chimpDevice;
//            manager = device.getManager();
        }
    }

//    public OperateAndroid(IDevice dev, int port) {
//        if (device == null) {
//            IChimpDevice chimpDevice = new AdbChimpDevice(dev, port);
//            device = (AdbChimpDevice) chimpDevice;
//            manager = device.getManager();
//
//            Logger.getLogger("PageTest.class").info("mang===" + manager);
//        }
//
//    }

    public void touch(int x, int y) {
        device.touch(x, y,
                com.android.chimpchat.core.TouchPressType.DOWN_AND_UP);
    }

    public void touch(int x, int y, TouchPressType type) {
        device.touch(x, y, type);
    }

    public void press(String str) {
        device.press(str, com.android.chimpchat.core.TouchPressType.DOWN_AND_UP);
    }

    public void press_DOWN(String str) {
        device.press(str, com.android.chimpchat.core.TouchPressType.DOWN);
    }

    public void press_UP(String str) {
        device.press(str, com.android.chimpchat.core.TouchPressType.UP);
    }

    public void drag(int startX, int startY, int endX, int endY, int time,
                     int step) {
        device.drag(startX, startY, endX, endY, time, step);

    }

    public void type(char c) {
        device.type(Character.toString(c));
    }

    public void touchDown(int x, int y) throws Exception {
        manager.touchDown(x, y);
    }

    public void touchUp(int x, int y) throws Exception {
        manager.touchUp(x, y);
    }

    public void touchMove(int x, int y) throws Exception {
        manager.touchMove(x, y);
    }

    public int getScreenWidth() {
        return Integer.parseInt(device.getProperty("display.width"));
    }

    public int getScreenHeight() {
        return Integer.parseInt(device.getProperty("display.height"));
    }
}
