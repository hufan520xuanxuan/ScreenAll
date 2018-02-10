package com.fansir.controlapplication;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;

import java.io.File;
import java.util.ArrayList;

public class MainService extends Service {
    String sendText;
    Uri uri;
    ArrayList<Uri> uriList = new ArrayList<Uri>();

    public MainService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sendText = "woshidashabi";
        readImageAndConvert();
        sendMsgToFriends("all");
        System.out.println("111=serviceoncreate");
    }

    private void readImageAndConvert() {
        for (int i = 1; i < 2; i++) {
            uri = Uri.fromFile(new File("/storage/emulated/0/Control/" + i + ".jpg"));
            uriList.add(uri);
        }
        System.out.println("uri=" + uri);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        System.out.println("111=serviceonstart");
    }

    private void sendMsgToFriends(String type) {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("com.facebook.orca", "com.facebook.messenger.intents.ShareIntentHandler");
        intent.setComponent(componentName);
        switch (type) {
            case "text": //文字模式
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, sendText);
                break;
            case "image": //图片模式
                intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                intent.setType("*/*");
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
                break;
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("111=serviceondestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
