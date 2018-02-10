package com.fansir.screenphone.gui;


import com.android.ddmlib.IDevice;
import com.fansir.screenphone.devices.AdbTools;
import com.fansir.screenphone.screen.AndroidScreenObserver;
import com.fansir.screenphone.screen.Banner;
import com.fansir.screenphone.screen.ScreenUtil;
import com.fansir.screenphone.touch.TouchUtil;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import sun.misc.BASE64Encoder;

/**
 * Created by FanSir on 2018-01-26.
 */

public class OnePhoneFrame extends JFrame {
    private int screenWidth = 720 / 2; //720
    private int screenHeight = 1280 / 2; //1280
    private int rowNum = 5; //排
    private int lineNum = 6; //列
    private int realWidth; //手机真实宽度
    private int realHeight; //手机真是高度
    private Banner banner = new Banner();
    private List<TouchUtil> touchList = new ArrayList<TouchUtil>();

    ScreenUtil screenUtil = null;
    private AdbTools adbTools;
    private JPanel jPanelScreenMenu;
    private JCheckBox jcb;
    private JButton jb, jb1;
    private JFileChooser jFileChooser;

    public static void main(String[] args) {
        new OnePhoneFrame();
    }

    public OnePhoneFrame() {
        initMainFrame();
        initDeviceList();
        this.setVisible(true);
    }

    private void initDeviceList() {
        adbTools = new AdbTools();
        while (adbTools.getDevicesList().length <= 0) { //循环获取当前连接设备信息
            System.out.println("adbLength=" + adbTools.getDevicesList().length);
        }
        for (int i = 0; i < adbTools.getDevicesList().length; i++) {
            IDevice device = adbTools.getDevicesList()[i];

            JPanel jPanelScreen = new JPanel(null);
            jPanelScreenMenu.add(jPanelScreen);
            System.out.println((i % rowNum) * (screenWidth) + "--" + (i / lineNum) * (screenHeight + 20));
            jPanelScreen.setBounds((i % rowNum) * (screenWidth), ((i / lineNum) * (screenHeight + 20)) + 20, screenWidth, screenHeight + 20);

            JTextArea jTextArea = new JTextArea(i + "hao");
            jPanelScreen.add(jTextArea);
            jTextArea.setBounds(0, 0, screenWidth, 20);

            ScreenPanel screenPanel = new ScreenPanel(device, 12345 + i, i);
            jPanelScreen.add(screenPanel);
            screenPanel.setBounds(0, 20, screenWidth, screenHeight);

            TouchUtil touchUtil = new TouchUtil(i);
            touchList.add(touchUtil);
            addMenuBar(touchUtil, screenUtil);
            setPanelMouseListener(jPanelScreen, touchUtil);

        }
    }

    private void initMainFrame() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize(); //获取电脑屏幕尺寸
        setBounds(0, 0, dim.width, dim.height);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //点击关闭直接关闭进程操作
        setLayout(null);
        ImageIcon imageIcon = new ImageIcon("screenphone/src/main/res/drawable/icon.png");
        setIconImage(imageIcon.getImage());

        jcb = new JCheckBox("----ALL----");
        getContentPane().add(jcb);
        jcb.setBounds(dim.width - 200, 0, 200, 20);

        jb = new JButton("start");
        getContentPane().add(jb);
        jb.setBounds(dim.width - 200, 30, 200, 20);

        jPanelScreenMenu = new JPanel(null);
        JScrollPane jScrollPane = new JScrollPane(jPanelScreenMenu);
        jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane.setSize(dim.width - 200, dim.height - 80);
        getContentPane().add(jScrollPane);
        jPanelScreenMenu.setBounds(0, 0, dim.width - 200, dim.height - 80);
        jPanelScreenMenu.setPreferredSize(new Dimension(dim.width - 200, dim.height + 1000));
    }

    private void addMenuBar(TouchUtil touchUtil, ScreenUtil screenUtil) {

        jb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new SendMsgFrame(screenUtil);
            }
        });

    }

    /**
     * 设置面板的鼠标事件监听
     */
    private void setPanelMouseListener(JPanel panel, TouchUtil touchUtil) {
        OnePhoneFrame.this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {

            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
//                int keyCode = keyEvent.getKeyCode();
//                switch (keyCode) {
//                    case KeyEvent.VK_BACK_SPACE:
//                        oaPhone.press("KEYCODE_DEL");
//                        break;
//                    case KeyEvent.VK_SPACE:
//                        oaPhone.press("KEYCODE_SPACE");
//                        break;
//                    case KeyEvent.VK_DELETE:
//                        oaPhone.press("KEYCODE_FORWARD_DEL");
//                        break;
//                    case KeyEvent.VK_UP:
//                        oaPhone.press("KEYCODE_DPAD_UP");
//                        break;
//                    case KeyEvent.VK_DOWN:
//                        oaPhone.press("KEYCODE_DPAD_DOWN");
//                        break;
//                    case KeyEvent.VK_LEFT:
//                        oaPhone.press("KEYCODE_DPAD_LEFT");
//                        break;
//                    case KeyEvent.VK_RIGHT:
//                        oaPhone.press("KEYCODE_DPAD_RIGHT");
//                        break;
//                    case KeyEvent.VK_ENTER:
//                        oaPhone.press("KEYCODE_ENTER");
//                        break;
//                    case KeyEvent.VK_CONTROL:
//                        break;
//                    case KeyEvent.VK_ALT:
//                        break;
//                    case KeyEvent.VK_SHIFT:
//                        break;
//                    default:
//                        oaPhone.type(keyEvent.getKeyChar());
//                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {

            }
        });

        panel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {

            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                Point point = pointConvert(mouseEvent.getPoint());
                if (jcb.isSelected()) {
                    for (TouchUtil touchUtil : touchList) {
                        touchUtil.touchDown(point);
                    }
                } else {
                    touchUtil.touchDown(point);
                }
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                if (jcb.isSelected()) {
                    for (TouchUtil touchUtil : touchList) {
                        touchUtil.touchUp();
                    }
                } else {
                    touchUtil.touchUp();
                }
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        });

        panel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                Point point = pointConvert(mouseEvent.getPoint());
                if (jcb.isSelected()) {
                    for (TouchUtil touchUtil : touchList) {
                        touchUtil.touchMove(point);
                    }
                } else {
                    touchUtil.touchMove(point);
                }
            }

            @Override
            public void mouseMoved(MouseEvent mouseEvent) {

            }
        });

        panel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
//                if (mouseWheelEvent.getWheelRotation() == 1) {
//                    oaPhone.press("KEYCODE_DPAD_DOWN");
//                } else if (mouseWheelEvent.getPreciseWheelRotation() == -1) {
//                    oaPhone.press("KEYCODE_DPAD_UP");
//                }
            }
        });
    }

    private Point pointConvert(Point point) {
        Point realpoint = new Point((int) ((point.getX() * 1.0 / screenWidth) * banner.getMaxX()), (int) ((point.getY() * 1.0 / screenHeight) * banner.getMaxY()));
        return realpoint;
    }

    public static String getImageStr(String imgFile) {
        InputStream inputStream = null;
        byte[] data = null;
        try {
            inputStream = new FileInputStream(imgFile);
            data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 加密
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
    }

    /**
     * 获取屏幕数据面板
     */
    class ScreenPanel extends JPanel implements AndroidScreenObserver {
        BufferedImage bufferedImage;
        int index;

        //构造方法
        public ScreenPanel(IDevice iDevice, int port, int index) {
            //获取手机屏幕尺寸大小
            this.index = index;
            screenUtil = new ScreenUtil(iDevice, port + index, index);
            screenUtil.registerObserver(this);
            screenUtil.startScreenListener(screenWidth, screenHeight);

            String size = (screenUtil.executeShell("wm size")).split(":")[1].trim();
            realWidth = Integer.parseInt(size.split("x")[0]);
            realHeight = Integer.parseInt(size.split("x")[1]);
//                Socket socket = new Socket("localhost", 1111 + index);
//                InputStream stream = socket.getInputStream();
//                outputStream = socket.getOutputStream();
//                int len = 4096;
//                byte[] buffer;
//                buffer = new byte[len];
//                int realLen = stream.read(buffer);
//                if (buffer.length != realLen) {
//                    buffer = subByteArray(buffer, 0, realLen);
//                }
//                String result = new String(buffer);
//                String[] array = result.split("|\n");
            System.out.println("width=" + realWidth + "height=" + realHeight);
            banner.setVersion(1);
            banner.setMaxPoint(10);
            banner.setMaxPress(0);
            banner.setMaxX(realWidth);
            banner.setMaxY(realHeight);
//                banner.setVersion(Integer.parseInt(array[1]));
//                banner.setMaxPoint(Integer.parseInt(array[3]));
//                banner.setMaxPress(Integer.parseInt(array[6]));
//                banner.setMaxX(Integer.parseInt(array[4]));
//                banner.setMaxY(Integer.parseInt(array[5]));
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

        @Override
        public void frameImageChange(Image image) {
            bufferedImage = (BufferedImage) image;
            this.repaint();
        }

        @Override
        public void paint(Graphics graphics) {
            if (bufferedImage == null) {
                return;
            }
            //因为frame边界也是有尺寸的 所以重新绘制时需要加上frame的边界
//            setSize(width + 15, height + 60);
            graphics.drawImage(bufferedImage, 0, 0, screenWidth, screenHeight, null);
//            this.setBounds(0, 0, screenWidth, screenHeight);
            bufferedImage.flush();
        }
    }
}
