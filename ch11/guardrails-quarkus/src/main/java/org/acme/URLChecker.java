package org.acme;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class URLChecker {

    public static boolean isNotURlReachable(URI targetURL) {
        return !isURLReachable(targetURL);
    }

    public static boolean isURLReachable(URI targetUrl) {
        try {
            URL url = targetUrl.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Optional: set timeout values
            connection.setConnectTimeout(5000); // 5 seconds
            connection.setReadTimeout(5000);

            connection.setRequestMethod("HEAD"); // Use HEAD to minimize data transfer
            int responseCode = connection.getResponseCode();

            return (responseCode >= 200 && responseCode < 400); // 2xx or 3xx means reachable
        } catch (IOException e) {
            return false;
        }
    }
}
