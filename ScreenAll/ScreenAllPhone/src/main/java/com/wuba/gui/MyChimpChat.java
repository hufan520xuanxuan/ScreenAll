package com.wuba.gui;

import java.net.Socket;

public class MyChimpChat {

    public Socket socket;

    public MyChimpChat(final int port) {

        Thread th = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Runtime.getRuntime()
                            .exec("adb forward tcp:" + port + " tcp:" + port);
                    Runtime.getRuntime().exec("adb shell monkey --port " + port);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        th.start();

        try {
            Thread.sleep(1000);
            socket = new Socket("127.0.0.1", port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void touchDown(int x, int y) {
        try {
            socket.getOutputStream().write(
                    new String("touch down " + x + " " + y + "\n").getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void touchUp(int x, int y) {
        try {
            socket.getOutputStream().write(
                    new String("touch up " + x + " " + y + "\n").getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Press() {
        try {
            socket.getOutputStream().write(
                    new String("press KEYCODE_HOME\n").getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void touchMove(int x, int y) {
        try {
            socket.getOutputStream().write(
                    new String("touch move " + x + " " + y + "\n").getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
