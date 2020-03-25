package com.example.upr3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {
    int value = 0;

    private class TaskThreadDoUI extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            for (int i = 0; i <= 10; i++) {
                //final int value = i;
                value = i;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress();
                // Извикване на метода onProgressUpdate
            }
            return "Изпълнена ";
        }

        @Override
        protected void onPostExecute(String result) {
            TextView tex;
            ProgressBar bar;
            bar = findViewById(R.id.progressBar1);
            tex = findViewById(R.id.SecondsTEXT);
            String temp = String.valueOf(value);
            tex.setText(temp);
            bar.setProgress(value);
            Log.d(" onPostExecute", "Приключила ли е ? " + result);
        }

        @Override
        protected void onPreExecute() {
            TextView tex;
            ProgressBar bar;
            bar = findViewById(R.id.progressBar1);
            tex = findViewById(R.id.SecondsTEXT);
            String temp = String.valueOf(value);
            tex.setText(temp);
            bar.setProgress(value);
            Log.d(" PreExceute", "Преди да бъде изпълнена doInBackground ......");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            TextView tex;
            ProgressBar bar;
            bar = findViewById(R.id.progressBar1);
            tex = findViewById(R.id.SecondsTEXT);
            String temp = String.valueOf(value);
            tex.setText(temp);
            bar.setProgress(value);
            Log.d(" onProgressUpdate", "\n" + "Вие сте в процес на обновяване след извикване на publishProgress(); ... ");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        new TaskThreadDoUI ().execute("");
    }
}
