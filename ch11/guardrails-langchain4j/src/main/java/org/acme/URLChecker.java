package org.acme;

import org.nibor.autolink.LinkExtractor;
import org.nibor.autolink.LinkSpan;
import org.nibor.autolink.LinkType;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.StreamSupport;

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

    public static boolean areLinksReachable(String msg) {
        var linkExtractor = LinkExtractor.builder()
                .linkTypes(EnumSet.of(LinkType.URL)) // limit to URLs
                .build();

        Iterable<LinkSpan> extractedLinks = linkExtractor.extractLinks(msg);

        List<URI> notReachable = StreamSupport.stream(extractedLinks.spliterator(), false)
                .map(link -> msg.substring(link.getBeginIndex(), link.getEndIndex()))
                .map(URI::create)
                .filter(URLChecker::isNotURlReachable)
                .toList();

        return notReachable.isEmpty();
    }
}
