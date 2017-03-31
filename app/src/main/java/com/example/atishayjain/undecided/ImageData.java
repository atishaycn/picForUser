package com.example.atishayjain.undecided;

import android.net.Credentials;
import android.os.AsyncTask;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by atishayjain on 31/03/17.
 */

public class ImageData extends AsyncTask<String, Void, String> {


    private final ImagesLoaded iLoaded;
    private int responseCode = 0;

    public interface ImagesLoaded
    {
        void getImages(String stringBuilder, int responseCode);
    }

    public ImageData(ImagesLoaded imagesLoaded){
        iLoaded = imagesLoaded;
    }

    @Override
    protected String doInBackground(String... params) {
        try{
            String urlFromParams = params[0] + "?next_cursor=";
           // HashMap<String, String> headerMap = new HashMap<>();
           // headerMap.put("", "");
            //headerMap.put("", "");

            String username = "736494723855755";
            String password = "batDo73eEW8gENOMWzB7hhJf_X8";
            byte[] loginCredentaials = (username + ":" + password).getBytes();
            StringBuilder loginBuilder = new StringBuilder().append("Basic ").append(Base64.encodeToString(loginCredentaials, Base64.DEFAULT));

            URL url = new URL(urlFromParams);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //BasicScheme.authenticate( new UsernamePasswordCredentials("user", "password"), "UTF-8", false);
            connection.addRequestProperty("Authorization", loginBuilder.toString());
            //connection.setRequestProperty("API_KEY", "736494723855755");
            //connection.setRequestProperty("API_SECRET", "batDo73eEW8gENOMWzB7hhJf_X8");
            connection.setReadTimeout(5000);
            responseCode = connection.getResponseCode();
            try{
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while((line = bufferedReader.readLine()) != null){
                    stringBuilder.append(line);
                }
                bufferedReader.close();
                return stringBuilder.toString();
            }
            finally {
                connection.disconnect();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(s == null){
            s = "Error";
        }
        else{
            iLoaded.getImages(s, responseCode);
        }
    }
}
