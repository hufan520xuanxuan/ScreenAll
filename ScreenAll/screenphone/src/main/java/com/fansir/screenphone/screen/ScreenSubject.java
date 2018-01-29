package com.fansir.screenphone.screen;

import java.awt.Image;

/**
 * Created by FanSir on 2018-01-29.
 */

public interface ScreenSubject {
    void registerObserver(AndroidScreenObserver observer);

    void notifyObservers(Image image);
}
