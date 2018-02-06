package com.fansir.screenphone.touch;

import java.awt.Point;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by FanSir on 2018-02-05.
 */

public class TouchUtil {
    private OutputStream outputStream;

    public TouchUtil(int index) {
        try {
            Socket socket = new Socket("localhost", 1111 + index);
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void touchDown(Point point) {
        if (outputStream != null) {
            String command = String.format("d 0 %s %s 50\n", (int) point.getX(), (int) point.getY());
            executeTouch(command);
        }
    }

    public void touchUp() {
        if (outputStream != null) {
            String command = "u 0\n";
            executeTouch(command);
        }
    }

    public void touchMove(Point point) {
        if (outputStream != null) {
            String command = String.format("m 0 %s %s 50\n", (int) point.getX(), (int) point.getY());
            executeTouch(command);
        }
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
}
