package com.example.app;


import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class to download from URL
 */
class DownloadUrl {

    /**
     * It read the url (a Json file with all the places information such as position and name) and
     * return it under string format
     * @param urlPlace String representing URL to download from
     * @return String representing downloaded data
     * @throws IOException If input streaming goes wrong
     */
    String readTheUrl(@NonNull String urlPlace) throws IOException {

        String data;
        InputStream inputStream;
        HttpURLConnection connection;

        URL url = new URL(urlPlace);
        connection = (HttpURLConnection) url.openConnection();
        connection.connect();

        inputStream = connection.getInputStream();
        BufferedReader bufferedReader =  new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuffer = new StringBuilder();

        // Going to read line by line
        String line;
        while( (line = bufferedReader.readLine()) != null){
            stringBuffer.append(line);
        }

        // Fill the data
        data = stringBuffer.toString();

        // Close all
        bufferedReader.close();
        inputStream.close();
        connection.disconnect();

        return data;
    }
}
