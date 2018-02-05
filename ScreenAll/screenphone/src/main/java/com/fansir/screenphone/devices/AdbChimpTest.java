package com.fansir.screenphone.devices;

import com.android.chimpchat.ChimpManager;
import com.android.chimpchat.adb.AdbChimpDevice;
import com.android.chimpchat.adb.LoggingOutputReceiver;
import com.android.chimpchat.core.IChimpDevice;
import com.android.chimpchat.core.IChimpImage;
import com.android.chimpchat.core.TouchPressType;
import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by FanSir on 2018-02-01.
 */

public class AdbChimpTest implements IChimpDevice {
    private IDevice iDevice;
    private ChimpManager manager;
    private int port;
    private static final Logger LOG = Logger.getLogger(AdbChimpDevice.class.getName());
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    Socket monkeySocket;
    ChimpManager mm = null;


    public AdbChimpTest(IDevice iDevice, int port) {
        this.iDevice = iDevice;
        this.port = port;
        this.CreateManager();
    }

    public void CreateManager() {
        this.manager = this.createManager("127.0.0.1", port);
        Preconditions.checkNotNull(this.manager);
    }

    public ChimpManager getManager() {
        return this.manager;
    }

    private ChimpManager createManager(String address, int port) {

        try {
            this.iDevice.createForward(port, port);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (AdbCommandRejectedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean success = false;
        long start = System.currentTimeMillis();
        while (true) {
            while (true) {
                if (!success) {
                    String command = "monkey --port " + port;
                    this.executeAsyncCommand(command, new LoggingOutputReceiver(LOG, Level.FINE));
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    InetAddress addr;
                    try {
                        addr = InetAddress.getByName(address);
                    } catch (UnknownHostException var16) {
                        LOG.log(Level.SEVERE, "Unable to convert address into InetAddress: " + address, var16);
                        return null;
                    }

                    long now = System.currentTimeMillis();
                    long diff = now - start;
                    if (diff > 2 * 10000) {
                        LOG.severe("Timeout while trying to create chimp mananger");
                        return null;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException var15) {
                        LOG.log(Level.SEVERE, "Unable to sleep", var15);
                    }
                    try {
                        monkeySocket = new Socket(addr, port);
                    } catch (Exception var21) {
                        LOG.log(Level.FINE, "Unable to connect socket", var21);
                        success = false;
                        continue;
                    }
                    try {
                        mm = new ChimpManager(monkeySocket);
                    } catch (IOException var20) {
                        LOG.log(Level.SEVERE, "Unable to open writer and reader to socket");
                        continue;
                    }

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    try {

                        boolean e = mm.wake();
                        LOG.info("wake是否成功--->" + e);
                        if (!e) {
                            success = false;
                            continue;
                        }

//                        boolean e = mm.wake();
//                        LOG.info("cccccc====" + e);
//                        if (!e) {
//                            break;
//                        }
//                        mm = new ChimpManager(monkeySocket);
//                        e = mm.wake();
                    } catch (IOException var22) {
                        System.out.println("unable to wake up device");
                        LOG.log(Level.FINE, "Unable to wake up device", var22);
                        success = false;
                        continue;
                    }
                }
                return mm;
            }
//            success = true;
        }
    }

    private void executeAsyncCommand(final String command, final LoggingOutputReceiver logger) {
        this.executor.submit(new Runnable() {
            public void run() {
                try {
                    AdbChimpTest.this.iDevice.executeShellCommand(command, logger);
                } catch (TimeoutException var2) {
                    AdbChimpTest.LOG.log(Level.SEVERE, "Error starting command: " + command, var2);
                    throw new RuntimeException(var2);
                } catch (AdbCommandRejectedException var3) {
                    AdbChimpTest.LOG.log(Level.SEVERE, "Error starting command: " + command, var3);
                    throw new RuntimeException(var3);
                } catch (ShellCommandUnresponsiveException var4) {
                    AdbChimpTest.LOG.log(Level.INFO, "Error starting command: " + command, var4);
                    throw new RuntimeException(var4);
                } catch (IOException var5) {
                    AdbChimpTest.LOG.log(Level.SEVERE, "Error starting command: " + command, var5);
                    throw new RuntimeException(var5);
                }
            }
        });
    }

    @Override
    public void dispose() {

    }

    @Override
    public IChimpImage takeSnapshot() {
        return null;
    }

    @Override
    public void reboot(String s) {

    }

    @Override
    public Collection<String> getPropertyList() {
        return null;
    }

    @Override
    public String getProperty(String s) {
        return null;
    }

    @Override
    public String getSystemProperty(String s) {
        return null;
    }

    @Override
    public void touch(int i, int i1, TouchPressType touchPressType) {

    }

    @Override
    public void press(String s, TouchPressType touchPressType) {

    }

    @Override
    public void drag(int i, int i1, int i2, int i3, int i4, long l) {

    }

    @Override
    public void type(String s) {

    }

    @Override
    public String shell(String s) {
        return null;
    }

    @Override
    public boolean pushFile(String s, String s1) {
        return false;
    }

    @Override
    public boolean pullFile(String s, String s1) {
        return false;
    }

    @Override
    public boolean installPackage(String s) {
        return false;
    }

    @Override
    public boolean removePackage(String s) {
        return false;
    }

    @Override
    public void startActivity(String s, String s1, String s2, String s3, Collection<String> collection, Map<String, Object> map, String s4, int i) {

    }

    @Override
    public void broadcastIntent(String s, String s1, String s2, String s3, Collection<String> collection, Map<String, Object> map, String s4, int i) {

    }

    @Override
    public Map<String, Object> instrument(String s, Map<String, Object> map) {
        return null;
    }

    @Override
    public void wake() {

    }

    public void closeWindow() {
        try {
            if (mm != null) {
                mm.done();
                mm.quit();
                mm.close();
            }
            monkeySocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
