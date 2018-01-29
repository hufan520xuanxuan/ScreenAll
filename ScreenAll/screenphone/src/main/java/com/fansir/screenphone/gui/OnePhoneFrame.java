package com.fansir.screenphone.gui;


import com.android.ddmlib.IDevice;
import com.fansir.screenphone.devices.AdbTools;
import com.fansir.screenphone.screen.AndroidScreenObserver;
import com.fansir.screenphone.screen.ScreenUtil;
import com.fansir.screenphone.utils.PictureChangeSize;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Created by FanSir on 2018-01-26.
 */

public class OnePhoneFrame extends JFrame {
    private int width = 480;
    private int height = 853;

    public static void main(String[] args) {
        new OnePhoneFrame();
    }

    public OnePhoneFrame() {
        AdbTools adbTools = new AdbTools();
        while (adbTools.getDevicesList().length <= 0) { //循环获取当前连接设备信息

        }
        IDevice device = adbTools.getDevicesList()[0];
        System.out.println("devicesList=" + device.getSerialNumber());
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize(); //获取屏幕尺寸
        System.out.println("dim" + dim.width + "--" + dim.height);

        ScreenPanel screenPanel = new ScreenPanel(device, this);
        screenPanel.setBounds(100, 100, dim.height, dim.width);
        this.getContentPane().add(screenPanel);
        this.setBounds(100, 100, dim.height, dim.width); //设置frame的尺寸
        this.setVisible(true);
    }

    /**
     * 获取屏幕数据面板
     */
    class ScreenPanel extends JPanel implements AndroidScreenObserver {
        ScreenUtil screenUtil = null;
        BufferedImage bufferedImage;
        private List<BufferedImage> imageList = new ArrayList<BufferedImage>();

        //构造方法
        public ScreenPanel(IDevice iDevice, OnePhoneFrame frame) {
            screenUtil = new ScreenUtil(iDevice, 12345);
            screenUtil.registerObserver(this);
            screenUtil.startScreenListener();
        }

        @Override
        public void frameImageChange(Image image) {
            bufferedImage = PictureChangeSize.disposeImage((BufferedImage) image, width, height);
            imageList.add(bufferedImage);
//            this.bufferedImage = (BufferedImage) image;
            this.repaint();
        }

        @Override
        public void paint(Graphics graphics) {
            if (imageList == null) {
                return;
            }
            OnePhoneFrame.this.setSize(width, height);
            graphics.drawImage(imageList.get(0), 0, 0, width, height, null);
            this.setSize(width, height);
            imageList.clear();
//            bufferedImage.flush();
        }
    }
}
