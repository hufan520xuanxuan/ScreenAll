package com.fansir.screenphone.screen;

import com.android.ddmlib.CollectingOutputReceiver;
import com.android.ddmlib.IDevice;
import com.fansir.screenphone.utils.ConstantTool;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

/**
 * 屏幕工具类
 * Created by FanSir on 2018-01-26.
 */

public class ScreenUtil implements ScreenSubject {

    private String MINITOUCH_FILE = "minitouch";
    private String MINITOUCH_START_COMMAND = "/data/local/tmp/%s";

    private String REMOTE_PATH = "/data/local/tmp"; //手机存放文件目录
    private String CPU_COMMAND = "ro.product.cpu.abi"; //手机存放文件目录
    private String SDK_COMMAND = "ro.build.version.sdk"; //手机存放文件目录
    private String SCREEN_BIN = "minicap"; //手机存放文件目录
    private String SCREENTOUCH_BIN = "minitouch"; //手机存放文件目录
    private String SCREEN_SO = "minicap.so"; //手机存放文件目录
    private String SCREEN_CHMOD_COMMAND = "chmod 777 %s/%s"; //改变文件权限
    private String SCREEN_SIZE_COMMAND = "wm size";
    private String SCREEN_START_COMMAND = "LD_LIBRARY_PATH=/data/local/tmp /data/local/tmp/minicap -P %s@%s/0";

    private List<AndroidScreenObserver> observerList = new ArrayList<AndroidScreenObserver>();
    private Queue<byte[]> dataQueue = new LinkedBlockingQueue<byte[]>();
    private Banner banner = new Banner();
    private boolean isRunning = false;

    private static IDevice device;
    private int portNum;
    private String size;
    private Socket socket;
    int screenWidth; //要显示的屏幕宽度
    int screenHeight; //要显示的屏幕高度

    public ScreenUtil(IDevice d, int p) {
        device = d;
        portNum = p;
        initParam();
    }

    /**
     * 初始化参数设置
     */
    private void initParam() {
        String cpu, sdk;
        cpu = device.getProperty(CPU_COMMAND); //获取手机CPU版本
        if (cpu == null) { //当通过设备获取不到时
            cpu = executeShell("getprop ro.product.cpu.abi").replace(" ", "");
        }
        sdk = device.getProperty(SDK_COMMAND); //获取手机SDK版本
        if (sdk == null) { //当通过设备获取不到时
            sdk = executeShell("getprop ro.build.version.sdk");
        }
        if (Integer.parseInt(sdk) > 23) { //当超过23(Android6.0时)
            sdk = "23";
        }
        //创建本地必须文件对象
        File scBin = new File(ConstantTool.getScreenBin(), cpu + File.separator + SCREEN_BIN);
        File scTouchBin = new File(ConstantTool.getScreenTouchBin(), cpu + File.separator + SCREENTOUCH_BIN);
        File scSo = new File(ConstantTool.getScreenSo(), "android-" + sdk + File.separator + cpu + File.separator + SCREEN_SO);
        try {
            //拷贝必要文件到手机根目录
            device.pushFile(scBin.getAbsolutePath(), REMOTE_PATH + "/" + SCREEN_BIN);
            device.pushFile(scTouchBin.getAbsolutePath(), REMOTE_PATH + "/" + SCREENTOUCH_BIN);
            device.pushFile(scSo.getAbsolutePath(), REMOTE_PATH + "/" + SCREEN_SO);
            //chmod 777  data/local/tmp
            executeShell(String.format(SCREEN_CHMOD_COMMAND, REMOTE_PATH, SCREEN_BIN));
            executeShell(String.format(SCREEN_CHMOD_COMMAND, REMOTE_PATH, SCREENTOUCH_BIN));
            //端口转发
            device.createForward(1111, "minitouch", IDevice.DeviceUnixSocketNamespace.ABSTRACT);
            device.createForward(portNum, "minicap", IDevice.DeviceUnixSocketNamespace.ABSTRACT);
            //获取屏幕尺寸
            String out = executeShell(SCREEN_SIZE_COMMAND);
            size = out.split(":")[1].trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册屏幕观察者
     *
     * @param observer
     */
    public void registerObserver(AndroidScreenObserver observer) {
        observerList.add(observer);
    }

    public void removeObserver(AndroidScreenObserver observer) {
        int index = observerList.indexOf(observer);
        if (index != -1) {
            observerList.remove(observer);
        }
    }

    /**
     * 通知观察者发生了变化
     *
     * @param image 变化后的image
     */
    @Override
    public void notifyObservers(Image image) {
        for (AndroidScreenObserver observer : observerList) {
            observer.frameImageChange(image);
        }
    }

    /**
     * 开始屏幕监测
     */
    public void startScreenListener(int width, int height) {
        screenWidth = width;
        screenHeight = height;
        isRunning = true;
        Thread frame = new Thread(new ImageBinaryFrameCollector());
        frame.start();
        Thread convert = new Thread(new ImageConverter());
        convert.start();
        Thread touch = new Thread(new TouchThread());
        touch.start();
    }

    public void stopScreenListener() {
        isRunning = false;
    }

    /**
     * 当窗口关闭时 需要关闭的操作
     */
    public void windowClose() {
        killProcess("minicap");
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (device != null) {
            device = null;
        }
    }

    /**
     * 图片帧数收集器
     */
    class ImageBinaryFrameCollector implements Runnable {
        private InputStream stream = null;

        public void run() {
            try {
                final String startCommand = String.format(
                        SCREEN_START_COMMAND, size, screenWidth + "x" + screenHeight);
                // 启动screen服务
                new Thread(new Runnable() {
                    public void run() {
                        executeShell(startCommand);
                    }
                }).start();
                Thread.sleep(1 * 1000);
                socket = new Socket("localhost", portNum);
                stream = socket.getInputStream();
                int len = 4096;
                while (isRunning) {
                    byte[] buffer;
                    buffer = new byte[len];
                    int realLen = stream.read(buffer);
                    if (buffer.length != realLen) {
                        buffer = subByteArray(buffer, 0, realLen);
                    }
                    dataQueue.add(buffer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (socket != null && socket.isConnected()) {
                        socket.close();
                    }
                    if (stream != null) {
                        stream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 图像转换器
     */
    class ImageConverter implements Runnable {
        private int readBannerBytes = 0;
        private int bannerLength = 2;
        private int readFrameBytes = 0;
        private int frameBodyLength = 0;
        private byte[] frameBody = new byte[0];

        public void run() {
            while (isRunning) {
                if (dataQueue.isEmpty()) {
                    continue;
                }
                byte[] buffer = dataQueue.poll();
                int len = buffer.length;
                for (int cursor = 0; cursor < len; ) {
                    int byte10 = buffer[cursor] & 0xff;
                    if (readBannerBytes < bannerLength) {
                        cursor = parserBanner(cursor, byte10);
                    } else if (readFrameBytes < 4) {
                        // 第二次的缓冲区中前4位数字和为frame的缓冲区大小
                        frameBodyLength += (byte10 << (readFrameBytes * 8)) >>> 0;
                        cursor += 1;
                        readFrameBytes += 1;
                    } else {
                        if (len - cursor >= frameBodyLength) {
                            byte[] subByte = subByteArray(buffer, cursor,
                                    cursor + frameBodyLength);
                            frameBody = byteMerger(frameBody, subByte);
                            if ((frameBody[0] != -1) || frameBody[1] != -40) {
                                return;
                            }
                            final byte[] finalBytes = subByteArray(frameBody,
                                    0, frameBody.length);
                            // 转化成bufferImage
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    //byte[] --> image
                                    Image image = createImageFromByte(finalBytes);
                                    notifyObservers(image);
                                }
                            }).start();
                            cursor += frameBodyLength;
                            restore();
                        } else {
                            byte[] subByte = subByteArray(buffer, cursor, len);
                            frameBody = byteMerger(frameBody, subByte);
                            frameBodyLength -= (len - cursor);
                            readFrameBytes += (len - cursor);
                            cursor = len;
                        }
                    }
                }
            }
        }

        private void restore() {
            frameBodyLength = 0;
            readFrameBytes = 0;
            frameBody = new byte[0];
        }

        private int parserBanner(int cursor, int byte10) {
            switch (readBannerBytes) {
                case 0:
                    // version
                    banner.setVersion(byte10);
                    break;
                case 1:
                    // length
                    bannerLength = byte10;
                    banner.setLength(byte10);
                    break;
                case 2:
                case 3:
                case 4:
                case 5:
                    // pid
                    int pid = banner.getPid();
                    pid += (byte10 << ((readBannerBytes - 2) * 8)) >>> 0;
                    banner.setPid(pid);
                    break;
                case 6:
                case 7:
                case 8:
                case 9:
                    // real width
                    int realWidth = banner.getReadWidth();
                    realWidth += (byte10 << ((readBannerBytes - 6) * 8)) >>> 0;
                    banner.setReadWidth(realWidth);
                    break;
                case 10:
                case 11:
                case 12:
                case 13:
                    // real height
                    int realHeight = banner.getReadHeight();
                    realHeight += (byte10 << ((readBannerBytes - 10) * 8)) >>> 0;
                    banner.setReadHeight(realHeight);
                    break;
                case 14:
                case 15:
                case 16:
                case 17:
                    // virtual width
                    int virtualWidth = banner.getVirtualWidth();
                    virtualWidth += (byte10 << ((readBannerBytes - 14) * 8)) >>> 0;
                    banner.setVirtualWidth(virtualWidth);
                    break;
                case 18:
                case 19:
                case 20:
                case 21:
                    // virtual height
                    int virtualHeight = banner.getVirtualHeight();
                    virtualHeight += (byte10 << ((readBannerBytes - 18) * 8)) >>> 0;
                    banner.setVirtualHeight(virtualHeight);
                    break;
                case 22:
                    // orientation
                    banner.setOrientation(byte10 * 90);
                    break;
                case 23:
                    // quirks
                    banner.setQuirks(byte10);
                    break;
            }

            cursor += 1;
            readBannerBytes += 1;

            if (readBannerBytes == bannerLength) {
                System.out.println("image信息=" + banner.toString());
            }
            return cursor;
        }
    }

    class TouchThread implements Runnable {
        @Override
        public void run() {
            String startCmd = String.format(MINITOUCH_START_COMMAND, MINITOUCH_FILE);
            System.out.println(startCmd);
            executeShell(startCmd);
        }
    }

    /**
     * java合并两个byte数组
     */
    private static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    /**
     * 拼接byte成byte[]
     *
     * @param byte1 要拼接的byte[]
     * @param start 拼接的开始位置
     * @param end   拼接的结束位置
     * @return 拼接之后的byte[]
     */
    private byte[] subByteArray(byte[] byte1, int start, int end) {
        byte[] byte2 = new byte[0];
        try {
            byte2 = new byte[end - start];
        } catch (NegativeArraySizeException e) {
            e.printStackTrace();
        }
        System.arraycopy(byte1, start, byte2, 0, end - start);
        return byte2;
    }

    /**
     * byte -> image
     *
     * @param binaryData 要转的byte[]
     * @return 返回bufferedimage
     */
    private BufferedImage createImageFromByte(byte[] binaryData) {
        BufferedImage bufferedImage = null;
        InputStream in = new ByteArrayInputStream(binaryData);
        try {
            bufferedImage = ImageIO.read(in);
            if (bufferedImage == null) {
                System.out.println("bufferimage为空");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bufferedImage;
    }

    /**
     * 执行shell脚本
     *
     * @param command 要执行的shell命令
     * @return 返回执行后的结果
     */
    public String executeShell(String command) {
        CollectingOutputReceiver cor = new CollectingOutputReceiver();
        try {
            device.executeShellCommand(command, cor, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return replaceBlank(cor.getOutput().toString());
    }

    /**
     * 格式化字符串(去除换位符等)
     *
     * @param str 要去除的字符串
     * @return 去除后的结果
     */
    public String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n"); // 去除多个空格，去除制表符，回车，换行
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * 结束android后台运行的某个进程
     */
    public void killProcess(String name) {
        String psGrep = executeShell("ps |grep " + name);
        String pidFour = psGrep.substring(5, 9);
        String resultFour = executeShell("kill " + pidFour);
        if (!resultFour.equals("")) { //没有成功停止了
            String pidFive = psGrep.substring(5, 10);
            executeShell("kill " + pidFive);
        }
    }

}
