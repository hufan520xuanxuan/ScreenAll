package com.wuba.gui;

import com.android.ddmlib.IDevice;
import com.wuba.device.ADB;
import com.wuba.minicap.AndroidScreenObserver;
import com.wuba.minicap.MiniCapUtil;
import com.wuba.utils.PictureChangeSize;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ScreenMap {
    private int phoneW = 480;
    private int phoneH = 854;

    public static void main(String arg[]) {
        new ScreenMap();
    }

    public ScreenMap() {
        initWindow();
    }

    private void initWindow() {
        JFrame screenMap = new JFrame("ScreenMap");
        screenMap.setBounds(100, 100, phoneW, phoneH);
        screenMap.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //初始化设备
        ADB adbTool = new ADB();
        if (adbTool.getDevices().length <= 0) {
            return;
        }
        IDevice device = adbTool.getDevices()[0];
        ScreenPanel screenPanel = new ScreenPanel(device);
        screenMap.add(screenPanel);

        screenMap.setVisible(true);
    }


    class ScreenPanel extends JPanel implements AndroidScreenObserver {
        BufferedImage bufferedImage = null;

        public ScreenPanel(IDevice device) {
            MiniCapUtil miniCapUtil = new MiniCapUtil(device, 11111);
            miniCapUtil.registerObserver(this);
            miniCapUtil.startScreenListener(phoneW, phoneH);
        }

        @Override
        public void paint(Graphics graphics) {
            if (bufferedImage == null) {
                return;
            }
            graphics.drawImage(bufferedImage, 0, 0, phoneW, phoneH, null);
            bufferedImage.flush();
        }

        @Override
        public void frameImageChange(Image image) {
            PictureChangeSize.disposeImage((BufferedImage) image, phoneW, phoneH);
            bufferedImage = (BufferedImage) image;
            repaint();
        }
    }
}
