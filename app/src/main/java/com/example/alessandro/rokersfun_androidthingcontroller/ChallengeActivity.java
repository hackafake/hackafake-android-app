package com.example.alessandro.rokersfun_androidthingcontroller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.things.contrib.driver.rainbowhat.RainbowHat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

public class ChallengeActivity extends Activity implements View.OnClickListener {

    private static String URL=Parameters.BASE_URL + "/challenge";

    private Button mButtonRx, mButtonLx, mButtonReady;
    private ProgressBar mSpinner;
    private View mLayoutChallenge, mLoaderSpiner;
    private WebView mWebViewRx, mWebViewLx;
    private Random random;
    private int pos, loading=0;
    private boolean wait=true;
    private com.google.android.things.contrib.driver.button.Button buttonA, buttonB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

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
        mLoaderSpiner = findViewById(R.id.loaderSpinner_layout);
        mSpinner = findViewById(R.id.progressBar);

        mWebViewLx.setWebViewClient(new MyWebViewClient());
        mWebViewRx.setWebViewClient(new MyWebViewClient());

        try {
            buttonA = RainbowHat.openButtonA();
            buttonA.setOnButtonEventListener(new com.google.android.things.contrib.driver.button.Button.OnButtonEventListener() {

                @Override
                public void onButtonEvent(com.google.android.things.contrib.driver.button.Button button, boolean pressed) {
                    if(!wait)
                        mButtonLx.callOnClick();
                }
            });
            buttonB = RainbowHat.openButtonB();
            buttonB.setOnButtonEventListener(new com.google.android.things.contrib.driver.button.Button.OnButtonEventListener() {
                @Override
                public void onButtonEvent(com.google.android.things.contrib.driver.button.Button button, boolean pressed) {
                    if(!wait)
                        mButtonRx.callOnClick();
                }
            });
        } catch (IOException e) {
            Log.d("ERROR","IOException Challenge");
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

        pos=random.nextInt(2);

        mWebViewLx.loadUrl((pos==0) ? urlFake : urlReal);
        mWebViewRx.loadUrl((pos==1) ? urlFake : urlReal);

    }

    private void changeVisibility() {
        if(mLoaderSpiner.getVisibility() == View.GONE) {
            mButtonReady.setClickable(true);
            //show ready button
            mLayoutChallenge.setVisibility(View.GONE);
            mLoaderSpiner.setVisibility(View.VISIBLE);
        } else {
            mButtonReady.setClickable(false);
            //show challenge
            mLayoutChallenge.setVisibility(View.VISIBLE);
            mLoaderSpiner.setVisibility(View.GONE);
        }
        //reset button background colors
        mButtonRx.setBackgroundResource(android.R.drawable.btn_default);
        mButtonLx.setBackgroundResource(android.R.drawable.btn_default);
    }


    @Override
    public void onClick(View v) {
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
            //show spinner
            mButtonReady.setVisibility(View.GONE);
            mSpinner.setVisibility(View.VISIBLE);

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
                fakeNews = jsonObject.getJSONObject(FAKE_FIELD).getString("url");
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

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if(++loading == 2) {
                //show ready button
                mButtonReady.setVisibility(View.VISIBLE);
                mSpinner.setVisibility(View.GONE);

                changeVisibility();
                loading=0;
            }
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            loading=0;
            mButtonReady.setVisibility(View.VISIBLE);
            mSpinner.setVisibility(View.GONE);
            if(mLoaderSpiner.getVisibility() == View.GONE)
                changeVisibility();
            mButtonReady.callOnClick();
        }
    }

}

