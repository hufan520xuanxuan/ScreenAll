package com.wuba.gui;

import com.android.ddmlib.IDevice;
import com.wuba.device.ADB;
import com.wuba.minicap.AndroidScreenObserver;
import com.wuba.minicap.MiniCapUtil;
import com.wuba.utils.PictureChangeSize;

import java.awt.Container;
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
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


//显示界面的GUI
public class MyMinicap {
    private static final Logger log = Logger.getLogger("PageTest.class");
    private int phoneW = 360; //手机窗口初始化宽
    private int phoneH = 640;
    private static JFrame mainF; //主窗口
    private MyChimpChat mn;
    private Container mainC;
    private int width;
    private int height;
    private int PRW;
    private int PRH;
    private JButton jb0;
    private JButton jb1;
    private JButton jb2;
    private JButton jb3;
    private JButton size;
    private MyPanel mp;
    private JCheckBox jcb;
    private int adbCount;
    private ADB adb;
    private JPanel jp;


    public static void main(String arg[]) {
        new MyMinicap();
    }

    public MyMinicap() {
        initWindow();
    }

    //初始化窗口
    private void initWindow() {
        width = Toolkit.getDefaultToolkit().getScreenSize().width;
        height = Toolkit.getDefaultToolkit().getScreenSize().height;

        mainF = new JFrame("big");
        mainC = mainF.getContentPane();
        //mainF.setBounds(0, 0, width - 200, height - 200);
        mainF.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //如果给布局设置成空布局 则需要先添加组件 再设置bounds
        mainF.setLayout(null);
        //主窗口添加组件的容器

        jcb = new JCheckBox("ALL");
        mainC.add(jcb);
        jcb.setBounds(0, 0, 100, 50);


        initDevices();

        mainF.setVisible(true);
        //    new AddShebei().start();

    }

    class AddShebei extends Thread {
        @Override
        public void run() {
            while (true) {
                int length = adb.getDevices().length;
                if (length > adbCount) {
                    addShebei();
                    adbCount = length;
                }
            }
        }
    }

    private void addShebei() {
        for (int i = adbCount; i < adb.getDevices().length; i++) {

            IDevice device = adb.getDevices()[i];
            JPanel p = new JPanel(null);
            jp.add(p);
            p.setBounds(200 + (i % 6) * (phoneW + 20), 0 + (i / 6) * (phoneH + 60), phoneW + 20, phoneH + 60);

            size = new JButton("size");
            p.add(size);
            size.setBounds(0, 0, phoneW, 20);

            mp = new MyPanel(device, 0, 20, phoneW, phoneH, 1111 + i);
            p.add(mp);
            mp.setBounds(0, 20, phoneW, phoneH);

            jb0 = new JButton("menu");
            p.add(jb0);
            jb0.setBounds(0, phoneH + 20, phoneW / 4, 40);
            jb1 = new JButton("home");
            p.add(jb1);
            jb1.setBounds(phoneW / 4, phoneH + 20, phoneW / 4, 40);
            jb2 = new JButton("back");
            p.add(jb2);
            jb2.setBounds(2 * phoneW / 4, phoneH + 20, phoneW / 4, 40);
            jb3 = new JButton("power");
            p.add(jb3);
            jb3.setBounds(3 * phoneW / 4, phoneH + 20, phoneW / 4, 40);

            OperateAndroid oa = new OperateAndroid(device, 12345 + i);
            list.add(oa);

            addListen(device, mp, 12345, oa);


            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    ArrayList<OperateAndroid> list = new ArrayList<OperateAndroid>();

    //初始化连接设备
    private void initDevices() {
        ADB adb = new ADB();
        if (adb.getDevices().length <= 0) {
            return;
        }
        JPanel jp = new JPanel(null);

        JScrollPane jsp = new JScrollPane(jp);
        jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jsp.setSize(width, height - 80);
        mainC.add(jsp);
        jp.setBounds(0, 0, width, height - 80);
        //设置这个参数是移动滚动条的关键
        jp.setPreferredSize(new Dimension(width + 10000, height + 10000));

        for (int i = 0; i < adb.getDevices().length; i++) {

            IDevice device = adb.getDevices()[i];
            JPanel p = new JPanel(null);
            jp.add(p);
            p.setBounds(200 + (i % 6) * (phoneW + 20), 0 + (i / 6) * (phoneH + 60), phoneW + 20, phoneH + 60);

            size = new JButton("size");
            p.add(size);
            size.setBounds(0, 0, phoneW, 20);

            mp = new MyPanel(device, 0, 20, phoneW, phoneH, 1111 + i);
            p.add(mp);
            mp.setBounds(0, 20, phoneW, phoneH);

            jb0 = new JButton("menu");
            p.add(jb0);
            jb0.setBounds(0, phoneH + 20, phoneW / 4, 40);
            jb1 = new JButton("home");
            p.add(jb1);
            jb1.setBounds(phoneW / 4, phoneH + 20, phoneW / 4, 40);
            jb2 = new JButton("back");
            p.add(jb2);
            jb2.setBounds(2 * phoneW / 4, phoneH + 20, phoneW / 4, 40);
            jb3 = new JButton("power");
            p.add(jb3);
            jb3.setBounds(3 * phoneW / 4, phoneH + 20, phoneW / 4, 40);

            OperateAndroid oa = new OperateAndroid(device, 12345 + i);
            list.add(oa);

            addListen(device, mp, 12346, oa);


            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private void addListen(IDevice d, JPanel p, int port, final OperateAndroid oa) {
        log.info("oa=" + oa);
        jb0.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (jcb.isSelected()) {
                    for (int i = 0; i < list.size(); i++) {
                        OperateAndroid o = list.get(i);
                        o.press(OperateAndroid.MENU);
                    }
                } else {
                    oa.press(OperateAndroid.MENU);
                }
            }
        });
        jb1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (jcb.isSelected()) {
                    for (int i = 0; i < list.size(); i++) {
                        OperateAndroid o = list.get(i);
                        o.press(OperateAndroid.HOME);
                    }
                } else {
                    oa.press(OperateAndroid.HOME);
                }
            }
        });
        jb2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (jcb.isSelected()) {
                    for (int i = 0; i < list.size(); i++) {
                        OperateAndroid o = list.get(i);
                        o.press(OperateAndroid.BACK);
                    }
                } else {
                    oa.press(OperateAndroid.BACK);
                }
            }
        });
        jb3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (jcb.isSelected()) {
                    for (int i = 0; i < list.size(); i++) {
                        OperateAndroid o = list.get(i);
                        o.press(OperateAndroid.POWER);
                    }
                } else {
                    oa.press(OperateAndroid.POWER);
                }
            }
        });

        p.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent mouseEvent) {

            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                try {
                    if (jcb.isSelected()) {
                        for (int i = 0; i < list.size(); i++) {
                            OperateAndroid o = list.get(i);
                            o.touchDown(mouseEvent.getX() * PRW / phoneW, mouseEvent.getY() * PRH / phoneH);
                        }
                    } else {
                        oa.touchDown(mouseEvent.getX() * PRW / phoneW, mouseEvent.getY() * PRH / phoneH);
                    }
                    log.info("aaaaaa-----" + mouseEvent.getX() * PRW / phoneW + mouseEvent.getY() * PRH / phoneH);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                try {
                    if (jcb.isSelected()) {
                        for (int i = 0; i < list.size(); i++) {
                            OperateAndroid o = list.get(i);
                            o.touchUp(mouseEvent.getX() * PRW / phoneW, mouseEvent.getY() * PRH / phoneH);
                        }
                    } else {
                        oa.touchUp(mouseEvent.getX() * PRW / phoneW, mouseEvent.getY() * PRH / phoneH);
                    }
                    log.info("aaaaaa-----" + mouseEvent.getX() * PRW / phoneW + mouseEvent.getY() * PRH / phoneH);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        });

        p.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                try {
                    if (jcb.isSelected()) {
                        for (int i = 0; i < list.size(); i++) {
                            OperateAndroid o = list.get(i);
                            o.touchMove(mouseEvent.getX() * PRW / phoneW, mouseEvent.getY() * PRH / phoneH);
                        }
                    } else {
                        oa.touchMove(mouseEvent.getX() * PRW / phoneW, mouseEvent.getY() * PRH / phoneH);
                    }
                    log.info("aaaaaa-----" + mouseEvent.getX() * PRW / phoneW + mouseEvent.getY() * PRH / phoneH);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void mouseMoved(MouseEvent mouseEvent) {

            }
        });

        p.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
                if (mouseWheelEvent.getWheelRotation() == 1) {
                    if (jcb.isSelected()) {
                        for (int i = 0; i < list.size(); i++) {
                            OperateAndroid o = list.get(i);
                            o.press("KEYCODE_DPAD_DOWN");
                        }
                    } else {
                        oa.press("KEYCODE_DPAD_DOWN");
                    }
                } else if (mouseWheelEvent.getWheelRotation() == -1) {
                    if (jcb.isSelected()) {
                        for (int i = 0; i < list.size(); i++) {
                            OperateAndroid o = list.get(i);
                            o.press("KEYCODE_DPAD_UP");
                        }
                    } else {
                        oa.press("KEYCODE_DPAD_UP");
                    }
                }
            }
        });

        p.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {

            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                int code = keyEvent.getKeyCode();
                switch (code) {

                    case KeyEvent.VK_BACK_SPACE:
                        if (jcb.isSelected()) {
                            for (int i = 0; i < list.size(); i++) {
                                OperateAndroid o = list.get(i);
                                o.press("KEYCODE_DEL");
                            }
                        } else {
                            oa.press("KEYCODE_DEL");
                        }
                        break;
                    case KeyEvent.VK_SPACE:
                        if (jcb.isSelected()) {
                            for (int i = 0; i < list.size(); i++) {
                                OperateAndroid o = list.get(i);
                                o.press("KEYCODE_SPACE");
                            }
                        } else {
                            oa.press("KEYCODE_SPACE");
                        }
                        break;
                    case KeyEvent.VK_DELETE:
                        if (jcb.isSelected()) {
                            for (int i = 0; i < list.size(); i++) {
                                OperateAndroid o = list.get(i);
                                o.press("KEYCODE_FORWARD_DEL");
                            }
                        } else {
                            oa.press("KEYCODE_FORWARD_DEL");
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if (jcb.isSelected()) {
                            for (int i = 0; i < list.size(); i++) {
                                OperateAndroid o = list.get(i);
                                o.press("KEYCODE_DPAD_UP");
                            }
                        } else {
                            oa.press("KEYCODE_DPAD_UP");
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (jcb.isSelected()) {
                            for (int i = 0; i < list.size(); i++) {
                                OperateAndroid o = list.get(i);
                                o.press("KEYCODE_DPAD_DOWN");
                            }
                        } else {
                            oa.press("KEYCODE_DPAD_DOWN");
                        }
                        break;
                    case KeyEvent.VK_LEFT:
                        if (jcb.isSelected()) {
                            for (int i = 0; i < list.size(); i++) {
                                OperateAndroid o = list.get(i);
                                o.press("KEYCODE_DPAD_LEFT");
                            }
                        } else {
                            oa.press("KEYCODE_DPAD_LEFT");
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (jcb.isSelected()) {
                            for (int i = 0; i < list.size(); i++) {
                                OperateAndroid o = list.get(i);
                                o.press("KEYCODE_DPAD_RIGHT");
                            }
                        } else {
                            oa.press("KEYCODE_DPAD_RIGHT");
                        }
                        break;
                    case KeyEvent.VK_ENTER:
                        if (jcb.isSelected()) {
                            for (int i = 0; i < list.size(); i++) {
                                OperateAndroid o = list.get(i);
                                o.press("KEYCODE_ENTER");
                            }
                        } else {
                            oa.press("KEYCODE_ENTER");
                        }
                        break;
                    case KeyEvent.VK_CONTROL:
                        break;
                    case KeyEvent.VK_ALT:
                        break;
                    case KeyEvent.VK_SHIFT:
                        break;
                    default:
                        if (jcb.isSelected()) {
                            for (int i = 0; i < list.size(); i++) {
                                OperateAndroid o = list.get(i);
                                o.type(keyEvent.getKeyChar());
                            }
                        } else {
                            oa.type(keyEvent.getKeyChar());
                        }
                }

            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {

            }
        });
    }


    class MyPanel extends JPanel implements AndroidScreenObserver {
        MiniCapUtil minicap = null;
        BufferedImage bi, bi1 = null;
//        int x, y, w, h;
//        IDevice d;

        public MyPanel(IDevice device, int x, int y, int w, int h, int p) {
            minicap = new MiniCapUtil(device, p);
            minicap.registerObserver(this);
            minicap.startScreenListener(phoneW, phoneH);

//            d = device;
//            this.x = x;
//            this.y = y;
//            this.w = w;
//            this.h = h;
        }

        @Override
        public void paint(Graphics graphics) {
            if (bi1 == null) {
                return;
            }
            graphics.drawImage(bi1, 0, 0, phoneW, phoneH, null);
            bi1.flush();
        }

        @Override
        public void frameImageChange(Image image) {
            bi = (BufferedImage) image;
            PRW = bi.getWidth(); //720
            PRH = bi.getHeight(); //1280

            //压缩图片
            bi1 = PictureChangeSize.disposeImage(bi, phoneW, phoneH);
            repaint();
        }
    }
}
