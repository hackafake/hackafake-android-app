package com.example.alessandro.rokersfun_androidthingcontroller;


import android.graphics.Color;
import android.util.Log;

import com.google.android.things.contrib.driver.apa102.Apa102;
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat;

import java.io.IOException;

public class FakeMeter {

    private FakeMeter_handler fakeMeter_handler;
    public static int HW_RAINBOW_HAT=1;
    public static int HW_RPI3=0;

    public FakeMeter(int hw_type)  {
        if(hw_type == HW_RAINBOW_HAT)
            fakeMeter_handler = new RainbowHat_handler();
        else if(hw_type == HW_RPI3)
            fakeMeter_handler = new RainbowHat_handler();
    }

    public void updateCount(double density) {
        fakeMeter_handler.displayDensiy(density);
    }

    private abstract class FakeMeter_handler {
        public abstract void displayDensiy(double density);
    }

    private class RainbowHat_handler extends FakeMeter_handler {
        private Apa102 ledStrip = null;
        private final int color = Color.RED;

        @Override
        public void displayDensiy(double density) {
            if (ledStrip == null)
                try {
                    ledStrip = RainbowHat.openLedStrip();
                    ledStrip.setBrightness(31);
                } catch (IOException e) {
                    return;
                }
            int[] rainbow = new int[RainbowHat.LEDSTRIP_LENGTH];
            int n=(int)(density * (rainbow.length * 1.0));
            for(int i=0;i<n;i++) {
                rainbow[i] = color;
            }
            try {
                ledStrip.write(rainbow);
            } catch (IOException e) {
                Log.d("ERROR", "Unable to comunicate with strip led");
            }
        }

        public void close() {
            try {
                ledStrip.close();
            } catch (IOException e) {

            } catch (NullPointerException e) {

            }
        }
    }

    private class RPI3_handler extends FakeMeter_handler {
        @Override
        public void displayDensiy(double density) {

        }
    }
}
