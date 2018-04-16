package com.example.alessandro.rokersfun_androidthingcontroller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
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
    private int pFake_count=0,pReal_count=0;
    private MediaPlayer mp;

    private final String URL=Parameters.BASE_URL + Parameters.COUNTER_TOPIC;


    Counter(Context appContext, MediaPlayer m) {
        this.handler=new Handler();
        try {
            this.display = RainbowHat.openDisplay();
            this.display.setBrightness(Ht16k33.HT16K33_BRIGHTNESS_MAX);
        } catch (IOException e) {
            Log.d("ERROR",e.getMessage());
        }
        mp = m;
        fakeMeter = new FakeMeter(FakeMeter.HW_RAINBOW_HAT);
        handler.post(this);
    }

    private void updateCount(int count) {
        try {
            display.display(count);
            display.setEnabled(true);
        } catch (IOException e) {
            Log.d("ERROR",e.getMessage());
        }
    }

    public void close() {
        handler.removeCallbacks(this);
        fakeMeter.close();
    }

    @Override
    public void run() {
        new HttpGetter_counter().execute(URL);
        handler.postDelayed(this,Parameters.COUNTER_DELAY_MILLIS);
    }

    @SuppressLint("StaticFieldLeak")
    private class HttpGetter_counter extends HttpGetter {

        @Override
        protected void onPostExecute(String s) {
            int fake_count=0,real_count=0;
            try {
                JSONObject jsonObject = new JSONObject(s);
                fake_count=jsonObject.getInt(Parameters.FAKE_NEWS_COUNTER_FIELD);
                real_count=jsonObject.getInt(Parameters.REAL_NEWS_COUNTER_FIELD);
            } catch (NullPointerException | JSONException e) {
                Log.d("ERROR",e.getMessage());
            }
            //display counter of fake news
            updateCount(fake_count);
            //calc fake news denisty
            double density;
            try {
                density = ((double)fake_count)/((double)(real_count+fake_count));
            } catch (ArithmeticException e) {
                density = 0;
            }
            //display density of fake news
            fakeMeter.updateCount(density);
            //play sound if new fake news
            if(fake_count-pFake_count > 0 && !mp.isPlaying() && pFake_count > 0) {
                mp.start();
                pFake_count = fake_count;
                pReal_count = real_count;
            } else if (pFake_count <= 0) {
                pFake_count = fake_count;
                pReal_count = real_count;
            }
        }
    }
}
