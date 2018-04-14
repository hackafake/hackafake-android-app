package com.example.alessandro.rokersfun_androidthingcontroller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

public class ChallengeActivity extends Activity {

    private Button mButtonRx, mButtonLx;
    private WebView mWebViewRx, mWebViewLx;

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

        mButtonLx = findViewById(R.id.button_lx);
        mButtonRx = findViewById(R.id.button_rx);
        mWebViewLx = findViewById(R.id.webView_lx);
        mWebViewRx = findViewById(R.id.webView_rx);

        //TODO: get challenge data

        //TODO: 3,2,1 count, take time

        //TODO: take response

        //TODO: show result

        //TODO: all in a function!!
    }

}

