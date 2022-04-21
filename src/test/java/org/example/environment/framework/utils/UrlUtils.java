package org.example.environment.framework.utils;

import java.net.HttpURLConnection;
import java.net.URL;

public class UrlUtils {

    public static void validate(String url) {
        if(!(url.startsWith("http://") || url.startsWith("https://")))
            throw new RuntimeException("Provided url is not in FQDN format! Correct url should start with protocol indicator like 'http://' or 'https://'!");
    }

    public static boolean isUrlReachable(String url) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("HEAD");

            return connection.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT;

        } catch (Exception e) {
            return false;
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }
}
