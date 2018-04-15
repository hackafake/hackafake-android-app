package com.example.alessandro.rokersfun_androidthingcontroller;

import android.os.Handler;
import android.util.Log;

import com.google.android.things.contrib.driver.ht16k33.Ht16k33;
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat;

import com.google.android.things.contrib.driver.ht16k33.AlphanumericDisplay;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Counter implements Runnable {
    private Handler handler;
    private AlphanumericDisplay display;
    private FakeMeter fakeMeter;

    private static long DELAY_MILLIS=2*1000;
    //TODO: set right url address
    private static String URL="http://api.rokers.fun:8080/counter";

    private static String COUNTER_FAKE_FIELD = "fake";
    private static String COUNTER_REAL_FIELD = "real";


    public Counter() {
        this.handler=new Handler();
        try {
            this.display = RainbowHat.openDisplay();
            this.display.setBrightness(Ht16k33.HT16K33_BRIGHTNESS_MAX);
        } catch (IOException e) {
            //TODO: handle exception
            Log.d("ERROR","Unable to open alphanumeric dispaly");
        }
        handler.post(this);
    }

    public void updateCount(int count) {
        try {
            display.display(count);
            display.setEnabled(true);
        } catch (IOException e) {
            Log.d("ERROR","IOException");
        }
    }

    public void close() {
        handler.removeCallbacks(this);
    }

    @Override
    public void run() {
        new HttpGetter_counter().execute(URL);
        handler.postDelayed(this,DELAY_MILLIS);
    }

    private class HttpGetter_counter extends HttpGetter {

        @Override
        protected void onPostExecute(String s) {
            int fake_count=0,real_count=0;
            try {
                JSONObject jsonObject = new JSONObject(s);
                fake_count=jsonObject.getInt(COUNTER_FAKE_FIELD);
                real_count=jsonObject.getInt(COUNTER_REAL_FIELD);
            } catch (NullPointerException e) {
                Log.d("ERROR","NullPointerException");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            updateCount(fake_count);
            double density;
            try {
                density = fake_count/(real_count+fake_count);
            } catch (ArithmeticException e) {
                density = 0;
            }
            fakeMeter.updateCount(density);
        }
    }
}
