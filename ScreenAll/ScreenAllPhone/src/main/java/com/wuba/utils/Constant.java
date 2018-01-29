/**
 *
 */
package com.wuba.utils;

import java.io.File;

/**
 * @author hui.qian qianhui@58.com
 * @date 2015年8月17日 下午12:02:25
 */
public class Constant {
    private static final String ROOT = System.getProperty("user.dir");

    public static File getMinicap() {
        return new File(ROOT, "minicap");
    }

    public static File getMiniCapBin() {
        return new File(ROOT, "ScreenAllPhone/minicap/bin");
    }

    public static File getMiniCapSo() {
        return new File(ROOT, "ScreenAllPhone/minicap/shared");
    }
}
