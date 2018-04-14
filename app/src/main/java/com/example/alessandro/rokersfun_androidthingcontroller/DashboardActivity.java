package com.example.alessandro.rokersfun_androidthingcontroller;

import android.app.Activity;
import android.content.Intent;
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


public class DashboardActivity extends Activity implements Runnable {

    private Counter counter;
    private WebView webView;
    private TextView mCounter;
    private Handler handler;
    private String previousUrl="";

    private static long DELAY_MILLIS = 10 * 1000;

    private static String TOPIC = "fakenews";
    private static int QUANTITY = 1;
    private static String URL = "http://52.212.172.20:8080/" + TOPIC + "?qnt=" + Integer.toString(QUANTITY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        counter = new Counter();
        mCounter = findViewById(R.id.textView_counter);
        webView = findViewById(R.id.webView);
        handler = new Handler();
        handler.post(this);
        ((Button)findViewById(R.id.button_challenge)).setOnClickListener(new View.OnClickListener() {
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
        new HttpGetter_TopFakeNews().execute(URL);
        handler.postDelayed(this,DELAY_MILLIS);
    }


    private class HttpGetter_TopFakeNews extends HttpGetter {

        @Override
        protected void onPostExecute(String s) {
            JSONObject topFake = null;
            try {
                JSONArray jsonArray = new JSONArray(s);
                //get first and only element sorted by counter
                topFake = jsonArray.getJSONObject(0);
            } catch (NullPointerException e) {
                Log.d("ERROR", "NullPointerException");
            } catch (JSONException e) {
                Log.d("ERROR", "JSONException");
            }
            try {
                String url=topFake.getString("url");
                if(url==null || url.isEmpty()) return;
                mCounter.setText("This fake news was shared " + Integer.toString(topFake.getInt("counter")) + " times");
                if(!url.equals(previousUrl)) {
                    webView.loadUrl(url);
                    previousUrl=url;
                }
            } catch (JSONException e) {
                Log.d("ERROR", "JSONException");
            } catch (NullPointerException e) {
                Log.d("ERROR", "NullPointerException");
            }
        }
    }
}
