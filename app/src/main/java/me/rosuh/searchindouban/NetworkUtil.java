package me.rosuh.searchindouban;

import android.content.Context;
import android.net.ConnectivityManager;
import android.webkit.WebView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class NetworkUtil {
    private final static String DOUBAN_URL = "https://m.douban.com/search?query=";
    private final static String regex="[^\\u4e00-\\u9fa5\\w]";


    public static void doSearch(String data, WebView webView){
        // global search REGEX
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);
        data = matcher.replaceAll("");
        webView.loadUrl(DOUBAN_URL + data);
    }

    public static boolean isNetworkAvailableAndConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(CONNECTIVITY_SERVICE);

        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;
    }
}
