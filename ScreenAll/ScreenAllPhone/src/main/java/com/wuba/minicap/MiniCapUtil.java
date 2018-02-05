package com.wuba.minicap;

import com.android.ddmlib.CollectingOutputReceiver;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.Log;
import com.wuba.utils.Constant;

import org.apache.log4j.Logger;

import java.awt.Image;
import java.awt.Toolkit;
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

import javax.imageio.ImageIO;

public class MiniCapUtil implements ScreenSubject {
    private static final Logger LOG = Logger.getLogger(MiniCapUtil.class);
    // CPU架构的种类
    public static final String ABIS_ARM64_V8A = "arm64-v8a";
    public static final String ABIS_ARMEABI_V7A = "armeabi-v7a";
    public static final String ABIS_X86 = "x86";
    public static final String ABIS_X86_64 = "x86_64";

    private Queue<byte[]> dataQueue = new LinkedBlockingQueue<byte[]>();
    private List<AndroidScreenObserver> observers = new ArrayList<AndroidScreenObserver>();

    private Banner banner = new Banner();

    //连接多台手机时 这个端口号要修改
    private int PORT;
    private Socket socket;
    private static IDevice d;
    private String REMOTE_PATH = "/data/local/tmp";
    private String ABI_COMMAND = "ro.product.cpu.abi"; //获取设备CPU版本
    private String SDK_COMMAND = "ro.build.version.sdk"; //获取设备系统版本
    private String MINICAP_BIN = "minicap";
    private String MINICAP_SO = "minicap.so";
    private String MINICAP_CHMOD_COMMAND = "chmod 777 %s/%s";
    private String MINICAP_WM_SIZE_COMMAND = "wm size";
    private String MINICAP_START_COMMAND = "LD_LIBRARY_PATH=/data/local/tmp /data/local/tmp/minicap -P %s@%s/0";
    private String MINICAP_TAKESCREENSHOT_COMMAND = "LD_LIBRARY_PATH=/data/local/tmp /data/local/tmp/minicap -P %s@%s/0 -s >%s";
    private String ADB_PULL_COMMAND = "adb -s %s pull %s %s";
    private boolean isRunning = false;
    private String size;
    int screenWidth, screenHeight;

    public MiniCapUtil(IDevice device, int port) {
        d = device;
        PORT = port;
        init();
    }

    //初始化工具
    private void init() {
        String abi = d.getProperty(ABI_COMMAND);
        System.out.println("aaaaa" + abi);
        String sdk = d.getProperty(SDK_COMMAND);
        File mcbin = new File(Constant.getMiniCapBin(), abi + File.separator + MINICAP_BIN);
        File mcso = new File(Constant.getMiniCapSo(), "android-" + sdk + File.separator + abi + File.separator + MINICAP_SO);

        try {
            Log.e("aaa", mcso.getAbsolutePath());
            d.pushFile("E:\\StudioProject\\ScreenAll\\ScreenAllPhone\\minicap\\bin\\armeabi-v7a\\minicap", REMOTE_PATH + "/" + MINICAP_BIN);
            d.pushFile("E:\\StudioProject\\ScreenAll\\ScreenAllPhone\\minicap\\shared\\android-23\\armeabi-v7a\\minicap.so", REMOTE_PATH + "/" + MINICAP_SO);
            executeShell(String.format(MINICAP_CHMOD_COMMAND, REMOTE_PATH, MINICAP_BIN));
            //端口转发
            d.createForward(PORT, "minicap", IDevice.DeviceUnixSocketNamespace.ABSTRACT);
            //获取屏幕尺寸
            String out = executeShell(MINICAP_WM_SIZE_COMMAND);
            size = out.split(":")[1].trim();
            LOG.info("size" + size);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv + " ");
        }
        return stringBuilder.toString();
    }

    //通过adb运行shell命令
    public static String executeShell(String command) {
        CollectingOutputReceiver cor = new CollectingOutputReceiver();
        try {
            d.executeShellCommand(command, cor, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cor.getOutput();
    }

    public void startScreenListener(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        isRunning = true;
        Thread frame = new Thread(new ImageBinaryFrameCollector());
        frame.start();
        Thread convert = new Thread(new ImageConverter());
        convert.start();
    }

    public void stopScreenListener() {
        isRunning = false;
    }

    private BufferedImage createImageFromByte(byte[] binaryData) {
        BufferedImage bufferedImage = null;
        InputStream in = new ByteArrayInputStream(binaryData);
        try {
            bufferedImage = ImageIO.read(in);
            if (bufferedImage == null) {
                LOG.debug("bufferimage为空");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return bufferedImage;
    }

    private Image createImage(byte[] data) {
        Image image = Toolkit.getDefaultToolkit().createImage(data);
        LOG.info("创建成功");
        return image;
    }

    // java合并两个byte数组
    private static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

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

    class ImageBinaryFrameCollector implements Runnable {
        private InputStream stream = null;

        // private DataInputStream input = null;

        public void run() {
            LOG.debug("图片二进制数据收集器已经开启");
            try {

                final String startCommand = String.format(
                        MINICAP_START_COMMAND, size, screenWidth + "x" + screenHeight);
                // 启动minicap服务
                new Thread(new Runnable() {
                    public void run() {
                        LOG.info("minicap服务器启动 : " + startCommand);
                        executeShell(startCommand);
                    }
                }).start();
                try {
                    Thread.sleep(1 * 1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                socket = new Socket("localhost", PORT);
                LOG.info("socket=" + socket);
                stream = socket.getInputStream();
                // input = new DataInputStream(stream);
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
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (socket != null && socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

            LOG.debug("图片二进制数据收集器已关闭");
        }

    }

    class ImageConverter implements Runnable {
        private int readBannerBytes = 0;
        private int bannerLength = 2;
        private int readFrameBytes = 0;
        private int frameBodyLength = 0;
        private byte[] frameBody = new byte[0];

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Runnable#run()
         */
        public void run() {
            // TODO Auto-generated method stub
            long start = System.currentTimeMillis();
            while (isRunning) {
                if (dataQueue.isEmpty()) {
                    // LOG.info("数据队列为空");
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
                        // LOG.debug("解析图片大小 = " + readFrameBytes);
                    } else {
                        if (len - cursor >= frameBodyLength) {
                            LOG.debug("frameBodyLength = " + frameBodyLength);
                            byte[] subByte = subByteArray(buffer, cursor,
                                    cursor + frameBodyLength);
                            frameBody = byteMerger(frameBody, subByte);
                            if ((frameBody[0] != -1) || frameBody[1] != -40) {
                                LOG.error(String
                                        .format("Frame body does not start with JPG header"));
                                return;
                            }
                            final byte[] finalBytes = subByteArray(frameBody,
                                    0, frameBody.length);
                            // 转化成bufferImage
                            new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    Image image = createImageFromByte(finalBytes);
                                    notifyObservers(image);
                                }
                            }).start();

                            long current = System.currentTimeMillis();
//                            LOG.info("图片已生成,耗时: "
//                                    + TimeUtil.formatElapsedTime(current
//                                    - start));
                            start = current;
                            cursor += frameBodyLength;
                            restore();
                        } else {
                            LOG.debug("所需数据大小 : " + frameBodyLength);
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
                LOG.debug(banner.toString());
            }
            return cursor;
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.wuba.utils.screenshot.ScreenSubject#registerObserver(com.wuba.utils
     * .screenshot.AndroidScreenObserver)
     */
    public void registerObserver(AndroidScreenObserver o) {
        // TODO Auto-generated method stub
        observers.add(o);

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.wuba.utils.screenshot.ScreenSubject#removeObserver(com.wuba.utils
     * .screenshot.AndroidScreenObserver)
     */
    public void removeObserver(AndroidScreenObserver o) {
        // TODO Auto-generated method stub
        int index = observers.indexOf(o);
        if (index != -1) {
            observers.remove(o);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wuba.minicap.ScreenSubject#notifyObservers(java.awt.Image)
     */
    @Override
    public void notifyObservers(Image image) {
        for (AndroidScreenObserver observer : observers) {
            observer.frameImageChange(image);
        }
        // TODO Auto-generated method stub

    }
}
