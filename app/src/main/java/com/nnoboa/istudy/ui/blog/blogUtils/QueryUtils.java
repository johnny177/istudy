package com.nnoboa.istudy.ui.blog.blogUtils;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    public static List<BlogItem> fetchBlogData(String requestUrl) {
        //create URL Object
        URL url = createUrl(requestUrl);

        //Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        //extract the relevant fields from the json response and create an {@link Event} object
        List<BlogItem> blogItems = extractBlogDataFromJson(jsonResponse);
        return blogItems;
    }

    /**
     * Return new URL object from th given string URL
     */

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL", e);
        }

        return url;
    }

    /**
     * Make HTTP request to the given URL and return a string as the response
     */

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        //if the url is null, then return early
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(1000/*milliseconds*/);
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(15000 /*milliseconds*/);
            urlConnection.connect();


            //if the request was successful (response code 200)
            //then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.");
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader
                    inputStreamReader =
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of blogItems objects that has been built up from
     * parsing a JSON response.
     *
     * @return blog
     */

    public static List<BlogItem> extractBlogDataFromJson(String blogJSON) {
        //if the JSON string is empty or null, then return early
        if (TextUtils.isEmpty(blogJSON)) {
            return null;
        }

        //Create an empty ArrayList that we can start adding blogs to
        List<BlogItem> blog = new ArrayList<>();
        BlogItem blogItems;
        JSONObject root;
        {
            try {
                //convert jsonResponse String to JSON object
                root = new JSONObject(blogJSON);

                //Extract the Items Array
                JSONArray items = root.optJSONArray("items");


                //Loop through each item in the array
                for (int i = 0; i != items.length(); i++) {
                    assert items != null;
                    JSONObject jsonObject = items.getJSONObject(i);

                    //get the time published
                    String publishedDate = jsonObject.optString("published");

                    //get the url of the post
                    String url = jsonObject.optString("url");

                    //get the author object
                    JSONObject author = jsonObject.optJSONObject("author");

                    //get the author displayName
                    assert author != null;

                    String displayName = author.optString("displayName");

                    //get the blog post title
                    String title = jsonObject.optString("title");

                    Log.d("Item", "time " + publishedDate + " url " + url + " auth " + displayName + " title " + title);

                    blogItems = new BlogItem(displayName, title, url, publishedDate);
                    blog.add(blogItems);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "Problem parsing the blog JSON results", e);

            }
        }

        return blog;
    }
}
