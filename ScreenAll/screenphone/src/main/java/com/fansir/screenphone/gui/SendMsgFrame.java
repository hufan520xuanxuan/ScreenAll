package com.fansir.screenphone.gui;

import com.fansir.screenphone.screen.ScreenUtil;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

/**
 * Created by FanSir on 2018-02-10.
 */

public class SendMsgFrame extends JFrame {

    public SendMsgFrame(ScreenUtil screenUtil) {
        JFrame jFrame = new JFrame();
        jFrame.setTitle("配置参数");
        jFrame.setSize(500, 500);
        jFrame.setLocation(250, 250);
        jFrame.setLayout(new FlowLayout());

        JLabel jTextField = new JLabel("请输入文字:");
        jFrame.add(jTextField);

        JTextArea jTextArea = new JTextArea(14, 40);
        jTextArea.setBackground(new Color(187, 187, 187));
        jTextArea.setLineWrap(true);
        jFrame.add(jTextArea);

        JLabel jTextField1 = new JLabel("请输入数量:");
        jFrame.add(jTextField1);

        JTextArea jTextArea1 = new JTextArea(2, 40);
        jTextArea1.setBackground(new Color(187, 187, 187));
        jTextArea1.setLineWrap(true);
        jFrame.add(jTextArea1);

        JButton picButton = new JButton("选择图片");
        jFrame.add(picButton);

        JTextArea picArea = new JTextArea(4, 40);
        picArea.setBackground(new Color(187, 187, 187));
        picArea.setEditable(false);
        jFrame.add(picArea);

        JButton sendButton = new JButton("发送");
        jFrame.add(sendButton);

        JLabel jLabel1 = new JLabel();
        jFrame.add(jLabel1);

        jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        jFrame.setVisible(true);

        picButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser chooseFile = new JFileChooser();
                int returnVal = chooseFile.showOpenDialog(null);
                if (returnVal == chooseFile.APPROVE_OPTION) {
                    File f = chooseFile.getSelectedFile();
                    picArea.append(f.getName() + "\n");
                    System.out.println(f.getName());
                }
            }
        });

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                jFrame.dispose(); //销毁
            }
        });
    }
}
