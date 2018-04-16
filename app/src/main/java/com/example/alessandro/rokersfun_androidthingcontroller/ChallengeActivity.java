package com.example.alessandro.rokersfun_androidthingcontroller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
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

    private final String URL=Parameters.BASE_URL + Parameters.CHALLENGE_TOPIC;

    private Button mButtonRx, mButtonLx, mButtonReady;
    private ProgressBar mSpinner;
    private View mLayoutChallenge, mLoaderSpiner;
    private WebView mWebViewRx, mWebViewLx;
    private Random random;
    private int pos, loading=0;
    private boolean wait=true;
    private com.google.android.things.contrib.driver.button.Button buttonA, buttonB;
    private MediaPlayer mediaPlayer_success, mediaPlayer_fail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        //go fullscreen
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        findViewById(R.id.button_dashboard).setOnClickListener(new View.OnClickListener() {
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
        mediaPlayer_success = MediaPlayer.create(this,R.raw.success);
        mediaPlayer_fail = MediaPlayer.create(this,R.raw.fail);

        mWebViewLx.setWebViewClient(new MyWebViewClient());
        mWebViewRx.setWebViewClient(new MyWebViewClient());

        //handle physical button press (RainbowHAT)
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
            Log.d("ERROR",e.getMessage());
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
        } catch (IOException | NullPointerException e) {
            Log.d("ERROR",e.getMessage());
        }

        try {
            buttonB.close();
        } catch (IOException | NullPointerException e) {
            Log.d("ERROR",e.getMessage());
        }
    }

    private void showChallenge(String urlFake, String urlReal) {
        //get a random position for real and fake news
        pos=random.nextInt(2);
        //load the news in the position chosed randomly
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
            //if button left was clicked
            //setup the backgound color accordingly if the news was fake (RED) or not (GREEN)
            mButtonLx.setBackgroundColor((pos == 0) ? Color.RED : Color.GREEN);
            //play a success sound or a fail sound
            if(pos==0 && !mediaPlayer_fail.isPlaying() && !mediaPlayer_success.isPlaying())
                mediaPlayer_fail.start();
            else if(!mediaPlayer_fail.isPlaying() && !mediaPlayer_success.isPlaying())
                mediaPlayer_success.start();
            //setting variable to prevent other click and to return to the ready button "page"
            change=true;
            wait=true;
        } else if(v.equals(mButtonRx) && !wait) {
            //same as above but for the right button
            mButtonRx.setBackgroundColor((pos==1) ? Color.RED : Color.GREEN);
            if(pos==1 && !mediaPlayer_fail.isPlaying() && !mediaPlayer_success.isPlaying())
                mediaPlayer_fail.start();
            else if(!mediaPlayer_fail.isPlaying() && !mediaPlayer_success.isPlaying())
                mediaPlayer_success.start();
            change=true;
            wait=true;
        }
        else if(v.equals(mButtonReady) && wait) {
            //show spinner
            mButtonReady.setVisibility(View.GONE);
            mSpinner.setVisibility(View.VISIBLE);
            //get challenge data
            new HttpGetter_Challenge().execute(URL);
            wait=false;
        }

        if(change)
            //wait for some time and the show the ready button for another challenge
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    changeVisibility();
                }
            },Parameters.CHALLENGE_COMPLETED_DELAY);
    }

    @SuppressLint("StaticFieldLeak")
    private class HttpGetter_Challenge extends HttpGetter {

        @Override
        protected void onPostExecute(String s) {
            String fakeNews = "";
            String realNews = "";
            try {
                JSONObject jsonObject = new JSONObject(s);
                //get fake news url
                fakeNews = jsonObject.getJSONObject(Parameters.FAKE_NEWS_CHALLENGE_FIELD).getString(Parameters.NEWS_URL_FIELD);
                //get real news url
                realNews = jsonObject.getJSONObject(Parameters.REAL_NEWS_CHALLENGE_FIELD).getString(Parameters.NEWS_URL_FIELD);
            } catch (NullPointerException | JSONException e) {
                Log.d("ERROR",e.getMessage());
            }
            try {
                //call function for show the challenge
                showChallenge(fakeNews, realNews);
            } catch (NullPointerException e) {
                Log.d("ERROR",e.getMessage());
            }
        }
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //when both webView have finished to load, changeVisibility (show the challenge) and reset the button (hide he spinner)
            if(++loading >= 2) {
                //show ready button (hide spinner)
                mButtonReady.setVisibility(View.VISIBLE);
                mSpinner.setVisibility(View.GONE);

                changeVisibility();
                //reset loading for next challenge
                loading=0;
            }
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            //if some webView encounter some error on loading
            //reset loaded counter
            loading=0;
            //shoe button (hide spinner)
            mButtonReady.setVisibility(View.VISIBLE);
            mSpinner.setVisibility(View.GONE);
            //change visibility to the ready button if needed
            if(mLoaderSpiner.getVisibility() == View.GONE)
                changeVisibility();
            //restart the challenge
            mButtonReady.callOnClick();
        }
    }

}


