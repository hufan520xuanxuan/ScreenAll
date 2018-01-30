package com.fansir.screenphone.screen;

import com.android.chimpchat.ChimpManager;
import com.android.chimpchat.adb.AdbChimpDevice;
import com.android.chimpchat.core.IChimpDevice;
import com.android.chimpchat.core.TouchPressType;
import com.android.ddmlib.IDevice;

import java.io.IOException;

/**
 * 电脑对安卓手机进行操作
 * Created by FanSir on 2018-01-30.
 */

public class OperateAndroidPhone {
    private AdbChimpDevice device;
    private IChimpDevice iChimpDevice;
    private ChimpManager manager;

    public OperateAndroidPhone(IDevice iDevice) {
        if (device == null) {
            iChimpDevice = new AdbChimpDevice(iDevice);
            device = (AdbChimpDevice) iChimpDevice;
            manager = device.getManager();
        }
    }

    public void touchDown(int x, int y) {
        try {
            manager.touchDown(x, y);
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    public void touchUp(int x, int y) {
        try {
            manager.touchUp(x, y);
        } catch (IOException e) {
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
}
