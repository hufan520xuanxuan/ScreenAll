package com.fansir.screenphone.gui;


import com.android.ddmlib.IDevice;
import com.fansir.screenphone.devices.AdbTools;
import com.fansir.screenphone.screen.AndroidScreenObserver;
import com.fansir.screenphone.screen.Banner;
import com.fansir.screenphone.screen.OperateAndroidPhone;
import com.fansir.screenphone.screen.ScreenUtil;

import java.awt.BorderLayout;
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

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

    ScreenUtil screenUtil = null;
    private AdbTools adbTools;
    private JPanel jPanelScreenMenu;
    private OutputStream outputStream;

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
            jPanelScreen.setBounds((i % rowNum) * (screenWidth), (i / lineNum) * (screenHeight + 20), screenWidth, screenHeight + 20);

            JTextArea jTextArea = new JTextArea("1hao");
            jPanelScreen.add(jTextArea);
            jTextArea.setBounds(0, 0, screenWidth, 20);

            ScreenPanel screenPanel = new ScreenPanel(device, 12345 + i, i);
            jPanelScreen.add(screenPanel);
            screenPanel.setBounds(0, 20, screenWidth, screenHeight);

//            OperateAndroidPhone oaPhone = new OperateAndroidPhone(device, 12345 + i); //创建操作android手机对象
//            addMenuBar(oaPhone); //添加菜单栏
            setPanelMouseListener(jPanelScreen);

        }
    }

    private void initMainFrame() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize(); //获取电脑屏幕尺寸
        setBounds(0, 0, dim.width, dim.height);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //点击关闭直接关闭进程操作
        setLayout(null);

//        JCheckBox jcb = new JCheckBox("----ALL----");
//        getContentPane().add(jcb);
//        jcb.setBounds(screenWidth - 200, 0, 200, 20);

        jPanelScreenMenu = new JPanel(null);
        JScrollPane jScrollPane = new JScrollPane(jPanelScreenMenu);
        jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane.setSize(dim.width - 200, dim.height - 80);
        getContentPane().add(jScrollPane);
        jPanelScreenMenu.setBounds(0, 0, dim.width - 200, dim.height - 80);
        jPanelScreenMenu.setPreferredSize(new Dimension(dim.width - 200, dim.height + 1000));
    }

    private void addMenuBar(OperateAndroidPhone oaPhone) {
        //添加菜单栏
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("basic");
        JMenuItem menuItem1 = new JMenuItem("lock");
        JMenuItem menuItem2 = new JMenuItem("unlock");
        JMenuItem menuItem3 = new JMenuItem("home");
        JMenuItem menuItem4 = new JMenuItem("back");
        JMenuItem menuItem5 = new JMenuItem("menu");
        JMenuItem menuItem6 = new JMenuItem("restart");
        JMenuItem menuItem7 = new JMenuItem("shutdown");
        JMenuItem menuItem8 = new JMenuItem("volAdd");
        JMenuItem menuItem9 = new JMenuItem("volMinus");

        menuBar.add(menu);
        menu.add(menuItem1);
        menu.add(menuItem2);
        menu.add(menuItem3);
        menu.add(menuItem4);
        menu.add(menuItem5);
        menu.add(menuItem6);
        menu.add(menuItem7);
        menu.add(menuItem8);
        menu.add(menuItem9);

        JMenu menu1 = new JMenu("zoom");
        JMenuItem menuItem11 = new JMenuItem("100%");
        JMenuItem menuItem12 = new JMenuItem("80%");
        JMenuItem menuItem13 = new JMenuItem("60%");
        JMenuItem menuItem14 = new JMenuItem("40%");
        JMenuItem menuItem15 = new JMenuItem("20%");
        menuBar.add(menu1);
        menu1.add(menuItem11);
        menu1.add(menuItem12);
        menu1.add(menuItem13);
        menu1.add(menuItem14);
        menu1.add(menuItem15);

        add(menuBar, BorderLayout.NORTH);

        menuItem1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                oaPhone.press(OperateAndroidPhone.POWER);
            }
        });
        menuItem2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
            }
        });

        menuItem11.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                reStartScreenPanel(1);
            }
        });
        menuItem12.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                reStartScreenPanel(0.8);
            }
        });
        menuItem13.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                reStartScreenPanel(0.6);
            }
        });
        menuItem14.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                reStartScreenPanel(0.4);
            }
        });
        menuItem15.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                reStartScreenPanel(0.2);
            }
        });
    }

    /**
     * 重新开启屏幕共享面板
     *
     * @param ratio
     */
    private void reStartScreenPanel(double ratio) {
        screenUtil.stopScreenListener();
        screenUtil.killProcess("minicap");
        screenWidth = (int) (realWidth * ratio);
        screenHeight = (int) (realHeight * ratio);
        screenUtil.startScreenListener(screenWidth, screenHeight);
    }

    /**
     * 设置面板的鼠标事件监听
     */
    private void setPanelMouseListener(JPanel panel) {
        //获取手机屏幕尺寸大小
        String size = (screenUtil.executeShell("wm size")).split(":")[1].trim();
        realWidth = Integer.parseInt(size.split("x")[0]);
        realHeight = Integer.parseInt(size.split("x")[1]);

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

        OnePhoneFrame.this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                super.windowClosing(windowEvent);
                screenUtil.windowClose();
            }
        });

        panel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {

            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                Point point = pointConvert(mouseEvent.getPoint());
                if (outputStream != null) {
                    String command = String.format("d 0 %s %s 50\n", (int) point.getX(), (int) point.getY());
                    executeTouch(command);
                }
//                screenUtil.exeShellQuickly("touch down " + (mouseEvent.getX() * realWidth / width) + " " + (mouseEvent.getY() * realHeight / height));
//                screenUtil.exeShellQuickly("input tap " + (mouseEvent.getX() * realWidth / width) + " " + (mouseEvent.getY() * realHeight / height));
//                oaPhone.touchDown((mouseEvent.getX() * realWidth / screenWidth), (mouseEvent.getY() * realHeight / screenHeight));
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
//                screenUtil.exeShellQuickly("touch up " + (mouseEvent.getX() * realWidth / width) + " " + (mouseEvent.getY() * realHeight / height));
//                screenUtil.exeShellQuickly("input tap " + (mouseEvent.getX() * realWidth / width) + " " + (mouseEvent.getY() * realHeight / height));
                if (outputStream != null) {
                    String command = "u 0\n";
                    executeTouch(command);
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
                if (outputStream != null) {
                    String command = String.format("m 0 %s %s 50\n", (int) point.getX(), (int) point.getY());
                    executeTouch(command);
                }
//                screenUtil.exeShellQuickly("touch move " + (mouseEvent.getX() * realWidth / width) + " " + (mouseEvent.getY() * realHeight / height));
//                screenUtil.exeShellQuickly("input swipe " + (mouseEvent.getX() * realWidth / width) + " " + (mouseEvent.getY() * realHeight / height));
//                oaPhone.touchMove((mouseEvent.getX() * realWidth / screenWidth), (mouseEvent.getY() * realHeight / screenHeight));
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

    private void executeTouch(String command) {
        if (outputStream != null) {
            try {
                System.out.println("command" + command);
                outputStream.write(command.getBytes());
                outputStream.flush();
                String endCommand = "c\n";
                outputStream.write(endCommand.getBytes());
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取屏幕数据面板
     */
    class ScreenPanel extends JPanel implements AndroidScreenObserver {
        BufferedImage bufferedImage;
        int index;

        //构造方法
        public ScreenPanel(IDevice iDevice, int port, int index) {
            this.index = index;
            screenUtil = new ScreenUtil(iDevice, port + index);
            screenUtil.registerObserver(this);
            screenUtil.startScreenListener(screenWidth, screenHeight);

            try {
                Thread.sleep(4 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                Socket socket = new Socket("localhost", 1111);
                InputStream stream = socket.getInputStream();
                outputStream = socket.getOutputStream();
                int len = 4096;
                byte[] buffer;
                buffer = new byte[len];
                int realLen = stream.read(buffer);
                if (buffer.length != realLen) {
                    buffer = subByteArray(buffer, 0, realLen);
                }
                String result = new String(buffer);
                String[] array = result.split("|\n");
                banner.setVersion(1);
                banner.setMaxPoint(10);
                banner.setMaxPress(0);
                banner.setMaxX(720);
                banner.setMaxY(1280);
//                banner.setVersion(Integer.parseInt(array[1]));
//                banner.setMaxPoint(Integer.parseInt(array[3]));
//                banner.setMaxPress(Integer.parseInt(array[6]));
//                banner.setMaxX(Integer.parseInt(array[4]));
//                banner.setMaxY(Integer.parseInt(array[5]));
            } catch (IOException e) {
                e.printStackTrace();
            }
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
