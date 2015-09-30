package cs4720.cs.virginia.edu.cs4720androidradiostream;

import android.app.Activity;
import android.os.AsyncTask;
import android.webkit.WebView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by andrewkovalenko on 9/26/15.
 */
public class HTMLScrapingTask extends AsyncTask<Activity, String, String> {

    private String showName;
    private String djNames;
    private WebView playlistView;
    private TextView showInfo;
    private String playlistUrl;
    private String playlist;

    public HTMLScrapingTask(Activity context) {
        playlistView = (WebView) context.findViewById(R.id.playlistView);
        showInfo = (TextView) context.findViewById(R.id.show_info);
        playlistUrl = context.getString(R.string.playlist_url);
    }

    @Override
    protected String doInBackground(Activity...context) {

        // Use Jsoup to grab html info
        Document doc = null;
        try {
            doc = Jsoup.connect(playlistUrl).get();

        } catch (IOException e) {
            e.printStackTrace();
        }

        playlist = doc.select("div#playlist").toString();
        showName = doc.select("div.show-title").text();
        djNames = doc.select("span#host").text();

        System.out.println("doInBackground Playlist: " + playlist);

        publishProgress(playlist);

        return playlist;
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        playlistView.loadData(String.valueOf(progress), "text/html", "utf-8");
    }

    @Override
    protected void onPostExecute(String result) {
        System.out.println("Playlist: " + result);
        // Load the playlist data as a WebView
        playlistView.loadData(result, "text/html", "utf-8");
        showInfo.setText("You are listening to " + showName + " with " + djNames);
    }
}

