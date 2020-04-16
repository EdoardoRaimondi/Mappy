package com.example.app;


import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Class to download URL
 */
public class DownloadUrl {

    /**
     * It read the url (basically a Json file with all the places information such as position and name) and
     * return it under string format
     * @param urlPlace to download
     * @return String representing data to downloaded
     * @throws IOException if input streaming goes wrong
     */
    public String readTheUrl(String urlPlace) throws IOException {

        String data = "";
        InputStream inputStream = null;
        HttpURLConnection connection = null;

        URL url = new URL(urlPlace);
        connection = (HttpURLConnection) url.openConnection();
        connection.connect();

        inputStream = connection.getInputStream();
        BufferedReader bufferedReader =  new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer stringBuffer = new StringBuffer();

        //going to read line by line
        String line = "";
        while( (line = bufferedReader.readLine()) != null){
            stringBuffer.append(line);
        }

        //fill the data
        data = stringBuffer.toString();

        //close all
        bufferedReader.close();
        inputStream.close();
        connection.disconnect();

        return data;
    }
}
