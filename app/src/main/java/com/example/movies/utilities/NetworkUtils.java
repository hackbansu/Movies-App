package com.example.movies.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.InputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    public static URL buildUrl(String baseUrl,
                               String path,
                               String[] queryKeys,
                               String[] queryValues) {
        if ((queryKeys != null && queryValues != null && queryKeys.length != queryValues.length)
                || (queryKeys != null && queryValues == null)
                || (queryKeys == null && queryValues != null)) {
            return null;
        }

        Uri.Builder uriBuilder = Uri.parse(baseUrl)
                .buildUpon()
                .appendPath(path);

        for (int i = 0; queryKeys != null && i < queryKeys.length; i++) {
            uriBuilder.appendQueryParameter(queryKeys[i], queryValues[i]);
        }

        URL webAddress = null;
        try {
            webAddress = new URL(uriBuilder.build().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return webAddress;
    }

    public static String getResponseFromHttpUrl(URL url, Context context) throws IOException {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE
        );
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                InputStream in = urlConnection.getInputStream();

                Scanner scanner = new Scanner(in);
                scanner.useDelimiter("\\A");

                if (scanner.hasNext()) {
                    return scanner.next();
                } else {
                    return null;
                }

            } finally {
                urlConnection.disconnect();
            }
        } else {
            return null;
        }
    }

    public static Uri prepareMovieDBImageUri(String poster_path) {
        String baseUrl = "http://image.tmdb.org/t/p/";
        String imageSize = "w185";
        String correct_poster_path = poster_path.substring(1);

        return Uri.parse(baseUrl)
                .buildUpon()
                .appendPath(imageSize)
                .appendPath(correct_poster_path)
                .build();
    }
}
