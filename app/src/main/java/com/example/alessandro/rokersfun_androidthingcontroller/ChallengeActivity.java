package com.example.alessandro.rokersfun_androidthingcontroller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class ChallengeActivity extends Activity implements View.OnClickListener {

    //TODO: check url
    //TODO: static base URL and PORT class (maybe also other parameters)
    private static String URL="http://api.rokers.fun:8080/challenge";

    private Button mButtonRx, mButtonLx, mButtonReady;
    private View mLayoutChallenge;
    private WebView mWebViewRx, mWebViewLx;
    private Random random;
    private int pos;

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

        changeVisibility();

        //TODO: get challenge data

    }

    private void showChallenge(String urlFake, String urlReal) {
        //TODO: 3,2,1 count, take time

        pos=random.nextInt(2);

        mButtonLx.setClickable(true);
        mButtonRx.setClickable(true);

        mWebViewLx.loadUrl((pos==0) ? urlFake : urlReal);
        mWebViewLx.loadUrl((pos==1) ? urlFake : urlReal);

        changeVisibility();
    }

    private void changeVisibility() {
        if(mButtonReady.getVisibility() == View.GONE) {
            mButtonLx.setOnClickListener(this);
            mButtonRx.setOnClickListener(this);
            //show challenge
            mWebViewLx.setVisibility(View.VISIBLE);
            mButtonReady.setVisibility(View.GONE);
        } else {
            mButtonLx.setClickable(false);
            mButtonRx.setClickable(false);
            //show ready button
            mWebViewLx.setVisibility(View.GONE);
            mButtonReady.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onClick(View v) {
        //TODO: make more user friendly
        boolean change=false;
        if(v.equals(mButtonLx)) {
            mButtonLx.setBackgroundColor((pos == 0) ? Color.RED : Color.GREEN);
            change=true;
        } else if(v.equals(mButtonRx)) {
            mButtonRx.setBackgroundColor((pos==1) ? Color.RED : Color.GREEN);
            change=true;
        }
        else if(v.equals(mButtonReady))
            new HttpGetter_Challenge().execute(URL);

        if(change)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    changeVisibility();
                }
            },5*1000);
    }

    private class HttpGetter_Challenge extends HttpGetter {

        @Override
        protected void onPostExecute(String s) {
            JSONObject fakeNews = null;
            JSONObject realNews = null;
            try {
                JSONObject jsonObject = new JSONObject(s);
                //TODO: check field
                fakeNews = jsonObject.getJSONObject("fakeNews");
                //TODO: check field
                realNews = jsonObject.getJSONObject("realNews");
            } catch (NullPointerException e) {
                Log.d("ERROR", "NullPointerException");
            } catch (JSONException e) {
                Log.d("ERROR", "JSONException");
            }
            try {
                //call function for the challenge
                showChallenge(fakeNews.getString("url"), realNews.getString("url"));
            } catch (JSONException e) {
                Log.d("ERROR", "JSONException");
            } catch (NullPointerException e) {
                Log.d("ERROR", "NullPointerException");
            }
        }
    }

}

