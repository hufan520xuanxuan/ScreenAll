package com.wuba.gui;

import com.android.ddmlib.IDevice;
import com.wuba.device.ADB;
import com.wuba.minicap.AndroidScreenObserver;
import com.wuba.minicap.MiniCapUtil;

import org.apache.log4j.Logger;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;


public class MinicapTest extends JFrame {
    private static final Logger LOG = Logger.getLogger("PageTest.class");

    private MyPanel mp = null;
    private IDevice device;
    private int width = 480;
    private int height = 853;
    private Thread thread = null;
    JLabel label;
    private OperateAndroid oa;
    private int w;
    private int h;

    public static void main(String[] args) {
        new MinicapTest();
    }


    public MinicapTest() {

//        label = new JLabel();
//        add(label);
//        label.setHorizontalAlignment(SwingConstants.CENTER);
//
//        JMenu fileMenu = new JMenu("shebei");
//        JMenu functionZoom = new JMenu("daxiao");
//        JMenuBar menuBar = new JMenuBar();
//        menuBar.add(fileMenu);
//        menuBar.add(functionZoom);
        //  setJMenuBar(menuBar);


//        this.setTitle("huSir");
        ADB adb = new ADB();  //创建adb对象
        if (adb.getDevices().length <= 0) {
            LOG.error("无连接设备,请检查");
            return;
        }
        device = adb.getDevices()[0];
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        mp = new MyPanel(device, this);
        mp.setBounds(0, 0, (dim.width / 6) - 10, (dim.height / 2) - 10);
        mp.setBackground(new Color(204, 204, 255));
        this.getContentPane().add(mp);


        this.setBounds(50, 50, 800, 800);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

            }
        });
        this.setVisible(true);


//        oa = new OperateAndroid(device);
//        LOG.info("oa=" + oa);
//        //oa = OperateAndroid.getOperateAndroid(device);
//
//        addMouseListener(new MouseListener() {
//
//            @Override
//            public void mouseClicked(MouseEvent mouseEvent) {
//
//            }
//
//            @Override
//            public void mousePressed(MouseEvent mouseEvent) {
//                try {
//                    LOG.info("ssss" + mouseEvent.getX() + mouseEvent.getY());
//
//
//                    oa.touchDown((mouseEvent.getX() * w / width) - 15, (mouseEvent.getY() * h / height) - 45);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                LOG.info("mousePressed");
//            }
//
//            @Override
//            public void mouseReleased(MouseEvent mouseEvent) {
//                try {
//                    oa.touchUp((mouseEvent.getX() * w / width) - 15, (mouseEvent.getY() * h / height) - 45);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                LOG.info("mouseReleased");
//            }
//
//            @Override
//            public void mouseEntered(MouseEvent mouseEvent) {
//
//            }
//
//            @Override
//            public void mouseExited(MouseEvent mouseEvent) {
//
//            }
//        });
//
//        addMouseMotionListener(new MouseMotionListener() {
//            @Override
//            public void mouseDragged(MouseEvent mouseEvent) {
//                try {
//                    oa.touchMove((mouseEvent.getX() * w / width) - 15, (mouseEvent.getY() * h / height) - 45);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void mouseMoved(MouseEvent mouseEvent) {
//
//            }
//        });
//
//        addMouseWheelListener(new MouseWheelListener() {
//            @Override
//            public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
//                if (mouseWheelEvent.getWheelRotation() == 1) {
//                    oa.press("KEYCODE_DPAD_DOWN");
//                } else if (mouseWheelEvent.getWheelRotation() == -1) {
//                    oa.press("KEYCODE_DPAD_UP");
//                }
//            }
//        });
//
//        this.addKeyListener(new KeyListener() {
//            @Override
//            public void keyTyped(KeyEvent keyEvent) {
//
//            }
//
//            @Override
//            public void keyPressed(KeyEvent keyEvent) {
//                int code = keyEvent.getKeyCode();
//                switch (code) {
//
//                    case KeyEvent.VK_BACK_SPACE:
//                        oa.press("KEYCODE_DEL");
//                        break;
//                    case KeyEvent.VK_SPACE:
//                        oa.press("KEYCODE_SPACE");
//                        break;
//                    case KeyEvent.VK_DELETE:
//                        oa.press("KEYCODE_FORWARD_DEL");
//                        break;
//                    case KeyEvent.VK_UP:
//                        oa.press("KEYCODE_DPAD_UP");
//                        break;
//                    case KeyEvent.VK_DOWN:
//                        oa.press("KEYCODE_DPAD_DOWN");
//                        break;
//                    case KeyEvent.VK_LEFT:
//                        oa.press("KEYCODE_DPAD_LEFT");
//                        break;
//                    case KeyEvent.VK_RIGHT:
//                        oa.press("KEYCODE_DPAD_RIGHT");
//                        break;
//                    case KeyEvent.VK_ENTER:
//                        oa.press("KEYCODE_ENTER");
//                        break;
//                    case KeyEvent.VK_CONTROL:
//                        break;
//                    case KeyEvent.VK_ALT:
//                        break;
//                    case KeyEvent.VK_SHIFT:
//                        break;
//                    default:
//                        oa.type(keyEvent.getKeyChar());
//                }
//
//            }
//
//            @Override
//            public void keyReleased(KeyEvent keyEvent) {
//
//            }
//        });


//         pack();


    }

    //写界面的面板
    class MyPanel extends JPanel implements AndroidScreenObserver {

        BufferedImage image = null;
        MiniCapUtil minicap = null;

        public MyPanel(IDevice device, MinicapTest frame) {
            minicap = new MiniCapUtil(device, 11111);
            minicap.registerObserver(this);
            minicap.startScreenListener(width, height);


        }


        public void paint(Graphics g) {
            try {
                if (image == null)
                    return;
                MinicapTest.this.setSize(width, height);
                g.drawImage(image, 0, 0, width - 15, height - 45, null);
                this.setSize(width, height);
                image.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        @Override
        public void frameImageChange(Image image) {
            this.image = (BufferedImage) image;

//            w = this.image.getWidth();
//            h = this.image.getHeight();
//            LOG.info("wwwwwwwwww" + w + h);
//            float radio = (float) width / (float) w;
//            LOG.info("radio" + radio);
//            height = (int) (radio * h);
//            LOG.info("hhh" + height);
//            setIconImage(image);
            this.repaint();
        }
    }

}
