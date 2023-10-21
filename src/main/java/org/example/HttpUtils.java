package org.example;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {
    public static HttpURLConnection getHttpURLConnection(String url) {
        try {
            URL httpUrl = new URL(url);

            HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();

            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36");
            return connection;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getFileName(String url) {
        String[] strings = url.split("/");
        return strings[strings.length - 1];
    }



}
