package com.wuba.test;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Created by FanSir on 2018-02-02.
 */

public class MainFrameTest extends JFrame {
    public static void main(String arg[]) {
        new MainFrameTest();
    }

    public MainFrameTest() {
        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, width, height);
        setLayout(null);

        JCheckBox jcb = new JCheckBox("ALL");
        getContentPane().add(jcb);
        jcb.setBounds(width - 200, 0, 200, 20);

        JPanel jPanel = new JPanel(null);
        JScrollPane jScrollPane = new JScrollPane(jPanel);
        jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane.setSize(width - 200, height - 80);
        getContentPane().add(jScrollPane);
        jPanel.setBounds(0, 0, width, height - 80);
        //设置这个参数是移动滚动条的关键
        jPanel.setPreferredSize(new Dimension(width - 200, height + 10000));

        setVisible(true);
    }
}
