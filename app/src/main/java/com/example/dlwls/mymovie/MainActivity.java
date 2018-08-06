package com.example.dlwls.mymovie;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    TextView tvDirector, tvGenre, tvActor, tvRelease, tvPlot;
    EditText edTitle;
    Button btnSearch;
    ImageView imgPoster;
    MySearchTask mySearchTask;
    JSONObject js = null;
    MyImageTask myImageTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDirector = (TextView)findViewById(R.id.tvDirector);
        tvGenre = (TextView)findViewById(R.id.tvGenre);
        tvActor = (TextView)findViewById(R.id.tvActor);
        tvRelease = (TextView)findViewById(R.id.tvRelease);
        tvPlot = (TextView)findViewById(R.id.tvPlot);

        btnSearch = (Button)findViewById(R.id.btn_search);
        imgPoster = (ImageView)findViewById(R.id.imgPoster);

        edTitle = (EditText)findViewById(R.id.edTitle);

        mySearchTask = new MySearchTask();
        myImageTask = new MyImageTask();
        btnSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(edTitle.getText() != null && !edTitle.getText().toString().isEmpty()){
                    mySearchTask.execute("http://www.omdbapi.com/?apikey=8122ef34&t=" + edTitle.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), "제목을 입력해주세요", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    class MySearchTask extends AsyncTask<String, JSONObject, Boolean>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... values) {
           try {
               URL url = new URL(values[0]);
               HttpURLConnection conn = (HttpURLConnection) url.openConnection();
               conn.setConnectTimeout(100000);
               conn.setRequestMethod("GET");
               conn.setDoInput(true);
               conn.setDoOutput(true);

               final StringBuilder sb = new StringBuilder();

               int responseCode = conn.getResponseCode();
               if (responseCode == HttpURLConnection.HTTP_OK) {
                   BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                   String line = null;
                   while ((line = br.readLine()) != null) {
                       sb.append(line);
                   }
                   js = new JSONObject(sb.toString());
               } else {
                   sb.append("Connection Failed");
               }
               publishProgress(js);
               conn.disconnect();
           } catch (Exception e){
               e.printStackTrace();
               Log.d("BackGround", "Exception ocurred");
               return false;
           }



            return true;
        }

        @Override
        protected void onProgressUpdate(JSONObject... values) {
            super.onProgressUpdate();
            try {
                tvDirector.setText(values[0].getString("Director"));
                tvActor.setText(values[0].getString("Actors"));
                tvGenre.setText(values[0].getString("Genre"));
                tvRelease.setText(values[0].getString("Released"));
                tvPlot.setText(values[0].getString("Plot"));
                URL imgUrl = new URL(values[0].getString("Poster"));
                myImageTask.execute(imgUrl);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Boolean status) {
            super.onPostExecute(status);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    class MyImageTask extends AsyncTask<URL, Bitmap, Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
//안녕하세용 진구에용
        @Override
        protected Void doInBackground(URL... urls) {
            try {
                HttpURLConnection imgCon = (HttpURLConnection) urls[0].openConnection();
                Bitmap bitmap = BitmapFactory.decodeStream(imgCon.getInputStream());
                publishProgress(bitmap);
                imgCon.disconnect();
            } catch (Exception e){

            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Bitmap... values) {
            super.onProgressUpdate(values);
            imgPoster.setImageBitmap(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}
