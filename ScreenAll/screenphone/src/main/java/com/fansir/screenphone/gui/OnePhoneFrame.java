package com.fansir.screenphone.gui;


import com.android.ddmlib.IDevice;
import com.fansir.screenphone.devices.AdbTools;
import com.fansir.screenphone.screen.AndroidScreenObserver;
import com.fansir.screenphone.screen.OperateAndroidPhone;
import com.fansir.screenphone.screen.ScreenUtil;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
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

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * Created by FanSir on 2018-01-26.
 */

public class OnePhoneFrame extends JFrame {
    private int width = 360;
    private int height = 640;
    private int realWidth; //手机真实宽度
    private int realHeight; //手机真是高度

    ScreenUtil screenUtil = null;
    private OperateAndroidPhone oaPhone;
    private ScreenPanel screenPanel;
    private final IDevice device;

    public static void main(String[] args) {
        new OnePhoneFrame();
    }

    public OnePhoneFrame() {
        AdbTools adbTools = new AdbTools();
        while (adbTools.getDevicesList().length <= 0) { //循环获取当前连接设备信息

        }
        device = adbTools.getDevicesList()[0];
        oaPhone = new OperateAndroidPhone(device); //创建操作android手机对象
        initScreenPanel();
        addMenuBar(); //添加菜单栏
        this.setVisible(true);
    }

    private void initScreenPanel() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize(); //获取电脑屏幕尺寸
        screenPanel = new ScreenPanel(device, this);
        screenPanel.setBounds(100, 100, dim.height, dim.width);
        this.getContentPane().add(screenPanel);
        this.setBounds(100, 100, 800, 800); //设置初始化frame的尺寸
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //点击关闭直接关闭进程操作
        setPanelMouseListener(screenPanel);
        pack();
    }

    private void addMenuBar() {
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
        width = (int) (realWidth * ratio);
        height = (int) (realHeight * ratio);
        screenUtil.startScreenListener(width, height);
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
                int keyCode = keyEvent.getKeyCode();
                switch (keyCode) {
                    case KeyEvent.VK_BACK_SPACE:
                        oaPhone.press("KEYCODE_DEL");
                        break;
                    case KeyEvent.VK_SPACE:
                        oaPhone.press("KEYCODE_SPACE");
                        break;
                    case KeyEvent.VK_DELETE:
                        oaPhone.press("KEYCODE_FORWARD_DEL");
                        break;
                    case KeyEvent.VK_UP:
                        oaPhone.press("KEYCODE_DPAD_UP");
                        break;
                    case KeyEvent.VK_DOWN:
                        oaPhone.press("KEYCODE_DPAD_DOWN");
                        break;
                    case KeyEvent.VK_LEFT:
                        oaPhone.press("KEYCODE_DPAD_LEFT");
                        break;
                    case KeyEvent.VK_RIGHT:
                        oaPhone.press("KEYCODE_DPAD_RIGHT");
                        break;
                    case KeyEvent.VK_ENTER:
                        oaPhone.press("KEYCODE_ENTER");
                        break;
                    case KeyEvent.VK_CONTROL:
                        break;
                    case KeyEvent.VK_ALT:
                        break;
                    case KeyEvent.VK_SHIFT:
                        break;
                    default:
                        oaPhone.type(keyEvent.getKeyChar());
                }
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
                oaPhone.touchDown((mouseEvent.getX() * realWidth / width), (mouseEvent.getY() * realHeight / height));
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                oaPhone.touchUp((mouseEvent.getX() * realWidth / width), (mouseEvent.getY() * realHeight / height));
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
                oaPhone.touchMove((mouseEvent.getX() * realWidth / width), (mouseEvent.getY() * realHeight / height));
            }

            @Override
            public void mouseMoved(MouseEvent mouseEvent) {

            }
        });

        panel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
                if (mouseWheelEvent.getWheelRotation() == 1) {
                    oaPhone.press("KEYCODE_DPAD_DOWN");
                } else if (mouseWheelEvent.getPreciseWheelRotation() == -1) {
                    oaPhone.press("KEYCODE_DPAD_UP");
                }
            }
        });
    }

    /**
     * 获取屏幕数据面板
     */
    class ScreenPanel extends JPanel implements AndroidScreenObserver {
        BufferedImage bufferedImage;

        //构造方法
        public ScreenPanel(IDevice iDevice, OnePhoneFrame frame) {
            screenUtil = new ScreenUtil(iDevice, 12345);
            screenUtil.registerObserver(this);
            screenUtil.startScreenListener(width, height);
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
            OnePhoneFrame.this.setSize(width + 15, height + 60);
            graphics.drawImage(bufferedImage, 0, 0, width, height, null);
            this.setSize(width, height);
            bufferedImage.flush();
        }
    }
}
