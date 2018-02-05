package com.fansir.screenphone.devices;

import com.android.chimpchat.adb.AdbChimpDevice;
import com.android.chimpchat.adb.LoggingOutputReceiver;
import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by FanSir on 2018-02-03.
 */

public class OperateAndroidTest {
    private static final Logger LOG = Logger.getLogger(AdbChimpDevice.class.getName());
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private IDevice device;
    private int portNum;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;

    public OperateAndroidTest(IDevice iDevice, int port) {
        device = iDevice;
        portNum = port;
        init();
    }

    private void init() {
        InetAddress inetAddress = null;
        Socket socket = null;
        try {
            device.createForward(portNum, portNum);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (AdbCommandRejectedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String command = "monkey --port " + portNum;
        executeAsyncCommand(command, new LoggingOutputReceiver(LOG, Level.FINE));

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            inetAddress = InetAddress.getByName("127.0.0.1");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            socket = new Socket(inetAddress, portNum);
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendMonkeyEvent("wake");

        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void touchDown(int x, int y) {
        sendMonkeyEvent("touch down " + x + " " + y);
    }

    public void touchUp(int x, int y) {
        sendMonkeyEvent("touch up " + x + " " + y);
    }

    public void touchMove(int x, int y) {
        sendMonkeyEvent("touch move " + x + " " + y);
    }


    private void sendMonkeyEvent(String command) {
        synchronized (this) {
            String trim = command.trim();
            try {
                bufferedWriter.write(trim + "\n");
                bufferedWriter.flush();
                System.out.println("返回值=" + bufferedReader.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void executeAsyncCommand(final String command, final LoggingOutputReceiver logger) {
        this.executor.submit(new Runnable() {
            public void run() {
                try {
                    device.executeShellCommand(command, logger);
                } catch (TimeoutException var2) {
                    OperateAndroidTest.LOG.log(Level.SEVERE, "Error starting command: " + command, var2);
                    throw new RuntimeException(var2);
                } catch (AdbCommandRejectedException var3) {
                    OperateAndroidTest.LOG.log(Level.SEVERE, "Error starting command: " + command, var3);
                    throw new RuntimeException(var3);
                } catch (ShellCommandUnresponsiveException var4) {
                    OperateAndroidTest.LOG.log(Level.INFO, "Error starting command: " + command, var4);
                    throw new RuntimeException(var4);
                } catch (IOException var5) {
                    OperateAndroidTest.LOG.log(Level.SEVERE, "Error starting command: " + command, var5);
                    throw new RuntimeException(var5);
                }
            }
        });
    }
}
