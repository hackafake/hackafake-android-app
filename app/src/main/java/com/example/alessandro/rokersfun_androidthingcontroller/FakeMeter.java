package com.example.alessandro.rokersfun_androidthingcontroller;


import android.graphics.Color;
import android.util.Log;

import com.google.android.things.contrib.driver.apa102.Apa102;
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat;

import java.io.IOException;
import java.nio.file.AccessMode;

@SuppressWarnings("WeakerAccess")
public class FakeMeter {

    private FakeMeter_handler fakeMeter_handler;
    static final int HW_RAINBOW_HAT=1;
    static final int HW_RPI3=0;

    FakeMeter(int hw_type)  {
        if(hw_type == HW_RAINBOW_HAT)
            fakeMeter_handler = new RainbowHat_handler();
        else if(hw_type == HW_RPI3)
            fakeMeter_handler = new RPI3_handler();
    }

    public void updateCount(double density) {
        fakeMeter_handler.displayDensiy(density);
    }

    public void close() {
        fakeMeter_handler.close();
    }

    private abstract class FakeMeter_handler {
        public abstract void displayDensiy(double density);
        public abstract void close();
    }

    private int getColor(int pos) {
        int p = pos/2;
        if(p < 1)
            return Color.GREEN;
        else if(p < 2)
            //orange ?
            return Color.RED | Color.YELLOW;
        else
            return Color.RED;
    }

    private class RainbowHat_handler extends FakeMeter_handler {
        private Apa102 ledStrip = null;
        private final int color = Color.RED;

        @Override
        public void displayDensiy(double density) {
            if (ledStrip == null)
                try {
                    ledStrip = RainbowHat.openLedStrip();
                    ledStrip.setBrightness(2);
                } catch (IOException e) {
                    Log.d("ERROR",e.getMessage());
                    return;
                }
            int[] rainbow = new int[RainbowHat.LEDSTRIP_LENGTH];
            int n=rainbow.length - (int)Math.round(density * (rainbow.length * 1.0));
            for(int i=rainbow.length-1;i>=n;i--) {
                rainbow[i] = getColor(rainbow.length-1-i);
            }
            try {
                ledStrip.write(rainbow);
            } catch (IOException e) {
                Log.d("ERROR",e.getMessage());
            }
        }

        public void close() {
            try {
                ledStrip.close();
            } catch (IOException | NullPointerException e) {
                Log.d("ERROR", e.getMessage());
            }
        }
    }

    private class RPI3_handler extends FakeMeter_handler {
        @Override
        public void displayDensiy(double density) {

        }

        @Override
        public void close() {

        }
    }
}
