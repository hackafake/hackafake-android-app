package com.example.alessandro.rokersfun_androidthingcontroller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Parameter;


public class DashboardActivity extends Activity implements Runnable {

    private Counter counter;
    private WebView webView;
    private TextView mCounter;
    private Handler handler;
    private String previousUrl="";

    private final String URL = Parameters.BASE_URL + Parameters.FAKENEWS_TOPIC + "?qnt=" + Integer.toString(Parameters.FAKENEWS_QUANTITY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);
        //go fullscreen
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.alarm);
        counter = new Counter(this, mediaPlayer);
        mCounter = findViewById(R.id.textView_counter);
        webView = findViewById(R.id.webView);
        handler = new Handler();
        handler.post(this);
        findViewById(R.id.button_challenge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this ,ChallengeActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        counter.close();
    }

    @Override
    public void run() {
        //get top fake news
        new HttpGetter_TopFakeNews().execute(URL);
        //recheck after some seconds
        handler.postDelayed(this,Parameters.BEST_NEWS_DELAY_CHECK_MILLIS);
    }


    @SuppressLint("StaticFieldLeak")
    private class HttpGetter_TopFakeNews extends HttpGetter {

        @Override
        protected void onPostExecute(String s) {
            JSONObject topFake = null;
            try {
                //get array of fake news
                JSONArray jsonArray = new JSONArray(s);
                //get first and only element sorted by counter
                topFake = jsonArray.getJSONObject(0);
            } catch (NullPointerException | JSONException e) {
                Log.d("ERROR", e.getMessage());
            }
            try {
                //get fake news url
                String url= topFake != null ? topFake.getString(Parameters.NEWS_URL_FIELD) : null;
                if(url==null || url.isEmpty()) return;
                mCounter.setText(Parameters.getTopFakeText(topFake.getInt("counter")));
                if(!url.equals(previousUrl)) {
                    webView.loadUrl(url);
                    previousUrl=url;
                }
            } catch (NullPointerException | JSONException e) {
                Log.d("ERROR", e.getMessage());
            }
        }
    }
}
