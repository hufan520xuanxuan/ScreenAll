package com.fansir.screenphone.utils;

import java.io.File;

/**
 * 常量工具类
 * Created by FanSir on 2018-01-26.
 */

public class ConstantTool {
    private static final String ROOT = System.getProperty("user.dir"); //根目录

    //获取minicap文件
    public static File getScreen() {
        return new File(ROOT, "screen");
    }

    public static File getScreenBin() {
        return new File(ROOT, "screenphone/screen/bin");
    }

    public static File getScreenSo() {
        return new File(ROOT, "screenphone/screen/shared");
    }

    public static File getScreenTouchBin() {
        return new File(ROOT, "screenphone/screentouch");
    }

}
