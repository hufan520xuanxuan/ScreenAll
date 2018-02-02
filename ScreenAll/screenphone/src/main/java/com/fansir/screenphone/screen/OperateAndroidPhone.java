package com.fansir.screenphone.screen;

import com.android.chimpchat.ChimpManager;
import com.android.chimpchat.core.IChimpDevice;
import com.android.chimpchat.core.TouchPressType;
import com.android.ddmlib.IDevice;
import com.fansir.screenphone.devices.AdbChimpTest;

import java.io.IOException;

/**
 * 电脑对安卓手机进行操作
 * Created by FanSir on 2018-01-30.
 */

public class OperateAndroidPhone {
    public static String POWER = "KEYCODE_POWER";
    public static String HOME = "KEYCODE_HOME";
    public static String BACK = "KEYCODE_BACK";
    public static String MENU = "KEYCODE_MENU";
    public static String VOLUP = "KEYCODE_VOLUME_UP";
    public static String VOLDOWN = "KEYCODE_VOLUME_DOWN";

    private AdbChimpTest device;
    private IChimpDevice iChimpDevice;
    private ChimpManager manager;

    public OperateAndroidPhone(IDevice iDevice, int port) {
        if (device == null) {
            iChimpDevice = new AdbChimpTest(iDevice, port);
            device = (AdbChimpTest) iChimpDevice;
            manager = device.getManager();
        }
    }

    public void touchDown(int x, int y) {
        try {
//            System.out.println("shell=" + "touch down " + x + " " + y);
//            screenUtil.exeShellQuickly("touch down " + x + " " + y);
            manager.touchDown(x, y);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void touchUp(int x, int y) {
        try {
//            screenUtil.exeShellQuickly("touch up " + x + " " + y);
            manager.touchUp(x, y);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void touchMove(int x, int y) {
        try {
            manager.touchMove(x, y);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void press(String str) {
        device.press(str, TouchPressType.DOWN_AND_UP);
    }

    public void type(char c) {
        device.type(Character.toString(c));
    }

    public void disPose() {
        device.closeWindow();
        iChimpDevice.dispose();
    }

}
