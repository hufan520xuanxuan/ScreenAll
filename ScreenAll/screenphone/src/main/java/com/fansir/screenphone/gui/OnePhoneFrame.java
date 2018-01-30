package com.fansir.screenphone.gui;


import com.android.ddmlib.IDevice;
import com.fansir.screenphone.devices.AdbTools;
import com.fansir.screenphone.screen.AndroidScreenObserver;
import com.fansir.screenphone.screen.Banner;
import com.fansir.screenphone.screen.OperateAndroidPhone;
import com.fansir.screenphone.screen.ScreenUtil;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

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
    private Banner banner = new Banner();

    public static void main(String[] args) {
        new OnePhoneFrame();
    }

    public OnePhoneFrame() {
        AdbTools adbTools = new AdbTools();
        while (adbTools.getDevicesList().length <= 0) { //循环获取当前连接设备信息

        }
        IDevice device = adbTools.getDevicesList()[0];
        oaPhone = new OperateAndroidPhone(device); //创建操作android手机对象
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize(); //获取屏幕尺寸
        ScreenPanel screenPanel = new ScreenPanel(device, this);
        screenPanel.setBounds(100, 100, dim.height, dim.width);
        this.getContentPane().add(screenPanel);
        this.setBounds(100, 100, 800, 800); //设置初始化frame的尺寸
        this.setVisible(true);
        setPanelMouseListener(screenPanel);
    }

    /**
     * 设置面板的鼠标事件监听
     */
    private void setPanelMouseListener(JPanel panel) {
        //获取手机屏幕尺寸大小
        String size = (screenUtil.executeShell("wm size")).split(":")[1].trim();
        realWidth = Integer.parseInt(size.split("x")[0]);
        realHeight = Integer.parseInt(size.split("x")[1]);

        OnePhoneFrame.
                this.addKeyListener(new KeyListener() {
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

//        pack(); //调整当前窗口的大小的
    }

    /**
     * 获取屏幕数据面板
     */
    class ScreenPanel extends JPanel implements AndroidScreenObserver {
        BufferedImage bufferedImage;

        //构造方法
        public ScreenPanel(IDevice iDevice, OnePhoneFrame frame) {
            screenUtil = new ScreenUtil(iDevice, 12345, width, height);
            screenUtil.registerObserver(this);
            screenUtil.startScreenListener();
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
            OnePhoneFrame.this.setSize(width + 15, height + 40);
            graphics.drawImage(bufferedImage, 0, 0, width, height, null);
            this.setSize(width, height);
            bufferedImage.flush();
        }
    }
}
