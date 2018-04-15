package com.example.alessandro.rokersfun_androidthingcontroller;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpGetter extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... strings) {
        HttpURLConnection connection;
        BufferedReader reader;
        try {
            URL url = new URL(strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream input = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(input));

            StringBuilder buffer = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null)
                buffer.append(line).append("\n");

            return buffer.toString();
        } catch (MalformedURLException e) {
            Log.d("ERROR", "MalformedURLException");
        } catch (IOException e) {
            Log.d("ERROR", "IOException Getter");
        }
        return null;
    }

}
