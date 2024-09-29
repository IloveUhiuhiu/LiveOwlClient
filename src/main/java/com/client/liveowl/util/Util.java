package com.client.liveowl.util;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

public class Util {
    public static double getScreenWidth(){
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        return screenBounds.getWidth();
    }

    public static double getScreenHeight(){
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        return screenBounds.getHeight();
    }
}
