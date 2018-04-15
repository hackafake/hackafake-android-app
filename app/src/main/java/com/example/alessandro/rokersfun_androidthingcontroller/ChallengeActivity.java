package com.example.alessandro.rokersfun_androidthingcontroller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;

import com.google.android.things.contrib.driver.ht16k33.Ht16k33;
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

public class ChallengeActivity extends Activity implements View.OnClickListener {

    //TODO: change touch button with physical ones

    //TODO: check url
    //TODO: static base URL and PORT class (maybe also other parameters)
    private static String URL="http://api.rokers.fun:8080/challenge";

    private Button mButtonRx, mButtonLx, mButtonReady;
    private View mLayoutChallenge;
    private ViewGroup mMainLayout;
    private WebView mWebViewRx, mWebViewLx;
    private Random random;
    private int pos;
    private boolean wait=true;
    private com.google.android.things.contrib.driver.button.Button buttonA, buttonB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);
        ((Button)findViewById(R.id.button_dashboard)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChallengeActivity.this,DashboardActivity.class));
            }
        });

        random = new Random();

        mButtonLx = findViewById(R.id.button_lx);
        mButtonRx = findViewById(R.id.button_rx);
        mButtonReady = findViewById(R.id.button_ready);
        mWebViewLx = findViewById(R.id.webView_lx);
        mWebViewRx = findViewById(R.id.webView_rx);
        mLayoutChallenge = findViewById(R.id.challenge_layout);
        mMainLayout = findViewById(R.id.mainLayout);

        try {
            buttonA = RainbowHat.openButtonA();
            buttonA.setOnButtonEventListener(new com.google.android.things.contrib.driver.button.Button.OnButtonEventListener() {

                @Override
                public void onButtonEvent(com.google.android.things.contrib.driver.button.Button button, boolean pressed) {
                    mButtonLx.callOnClick();
                }
            });
            buttonB = RainbowHat.openButtonB();
            buttonB.setOnButtonEventListener(new com.google.android.things.contrib.driver.button.Button.OnButtonEventListener() {
                @Override
                public void onButtonEvent(com.google.android.things.contrib.driver.button.Button button, boolean pressed) {
                    mButtonRx.callOnClick();
                }
            });
        } catch (IOException e) {
            Log.d("ERROR","IOException");
        }

        mButtonLx.setOnClickListener(this);
        mButtonRx.setOnClickListener(this);
        mButtonReady.setOnClickListener(this);

        changeVisibility();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            buttonA.close();
        } catch (IOException e) {

        } catch (NullPointerException e) {

        }

        try {
            buttonB.close();
        } catch (IOException e) {

        } catch (NullPointerException e) {

        }
    }

    private void showChallenge(String urlFake, String urlReal) {
        //TODO: 3,2,1 count, take time

        pos=random.nextInt(2);

        mWebViewLx.loadUrl((pos==0) ? urlFake : urlReal);
        mWebViewLx.loadUrl((pos==1) ? urlFake : urlReal);

        changeVisibility();
    }

    private void changeVisibility() {
        Log.d("INFO", "Try changing visibility - " + ((mButtonReady.getVisibility() == View.GONE) ? "GONE" : "VISIBLE"));
        if(mButtonReady.getVisibility() == View.GONE) {
            mButtonReady.setClickable(true);
            //show ready button
            mLayoutChallenge.setVisibility(View.GONE);
            mButtonReady.setVisibility(View.VISIBLE);
        } else {
            mButtonReady.setClickable(false);
            //show challenge
            mLayoutChallenge.setVisibility(View.VISIBLE);
            mButtonReady.setVisibility(View.GONE);
        }
        //reset button background colors
        mButtonRx.setBackgroundResource(android.R.drawable.btn_default);
        mButtonLx.setBackgroundResource(android.R.drawable.btn_default);
    }


    @Override
    public void onClick(View v) {
        //TODO: make more user friendly
        boolean change=false;
        if(v.equals(mButtonLx) && !wait) {
            mButtonLx.setBackgroundColor((pos == 0) ? Color.RED : Color.GREEN);
            change=true;
            wait=true;
        } else if(v.equals(mButtonRx) && !wait) {
            mButtonRx.setBackgroundColor((pos==1) ? Color.RED : Color.GREEN);
            change=true;
            wait=true;
        }
        else if(v.equals(mButtonReady) && wait) {
            Log.d("INFO", "Button READY clicked");
            new HttpGetter_Challenge().execute(URL);
            wait=false;
        }

        if(change)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    changeVisibility();
                }
            },5*1000);
    }

    private class HttpGetter_Challenge extends HttpGetter {

        private final String FAKE_FIELD = "fake";
        private final String REAL_FIELD = "real";

        @Override
        protected void onPostExecute(String s) {
            Log.d("INFO","Post Execute Challenge");
            String fakeNews = "";
            String realNews = "";
            try {
                JSONObject jsonObject = new JSONObject(s);
                //TODO: check field
                fakeNews = jsonObject.getJSONObject(FAKE_FIELD).getString("url");
                //TODO: check field
                realNews = jsonObject.getJSONObject(REAL_FIELD).getString("url");
            } catch (NullPointerException e) {
                Log.d("ERROR", "NullPointerException");
            } catch (JSONException e) {
                Log.d("ERROR", "JSONException");
            }
            try {
                //call function for the challenge
                showChallenge(fakeNews, realNews);
            } catch (NullPointerException e) {
                Log.d("ERROR", "NullPointerException");
            }
        }
    }

}

