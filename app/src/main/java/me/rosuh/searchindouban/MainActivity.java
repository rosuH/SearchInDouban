package me.rosuh.searchindouban;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.daasuu.ahp.AnimateHorizontalProgressBar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity {
    private final static String DOUBAN_URL = "https://m.douban.com/search?query=";
    private boolean KEY_LAUNCH_STATUS = true;     // true 为分享启动，false 为手动启动
    private WebView mWebView;
    private String mUserData;
    private WebSettings mWebSettings;
    private EditText mEditText;
    private ImageButton mImageButton;
    private AnimateHorizontalProgressBar progressBar;
    private TextView mTextView;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView = findViewById(R.id.web_view_content_main);
        mWebSettings = mWebView.getSettings();
        mUserData = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        mImageButton = findViewById(R.id.image_button_search_main);
        progressBar = findViewById(R.id.progress_bar_main);
        mEditText = findViewById(R.id.edit_view_main);
        mTextView = findViewById(R.id.text_view_tip_main);

        progressBar.setMax(1000);
        progressBar.setProgress(400);

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed();
                Toast.makeText(MainActivity.this, "等待 HTTPS 响应...", Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
        mWebSettings.setSupportMultipleWindows(false);
        mWebSettings.setAllowFileAccessFromFileURLs(false);
        mWebSettings.setAllowFileAccess(false);
        mWebSettings.setAllowUniversalAccessFromFileURLs(false);


        if (mUserData == null) {
            KEY_LAUNCH_STATUS = false;
            setViewVisibility();
        }else {
            doSearch(mUserData);
        }

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = mEditText.getText().toString();
                if (!str.isEmpty()){
                    doSearch(str);
                }else {
                    Toast.makeText(MainActivity.this, R.string.nothing, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void doSearch(String data){
        KEY_LAUNCH_STATUS = true;
        // global search REGEX
        String regex="[^\\u4e00-\\u9fa5\\w]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);
        data = matcher.replaceAll("");
        setViewVisibility();
        mWebView.loadUrl(DOUBAN_URL + data);
        mWebSettings.setSupportMultipleWindows(true);
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgressWithAnim(progress*40);
            }


        });
    }
    
    private void setViewVisibility(){
        if (KEY_LAUNCH_STATUS){
            // 分享启动
            mWebView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);

            mEditText.setVisibility(GONE);
            mImageButton.setVisibility(GONE);
            mTextView.setVisibility(GONE);
        }else if (!KEY_LAUNCH_STATUS){
            // 启动器启动
            mEditText.setVisibility(View.VISIBLE);
            mImageButton.setVisibility(View.VISIBLE);
            mTextView.setVisibility(View.VISIBLE);
            mWebView.setVisibility(GONE);
            progressBar.setVisibility(GONE);
        }
    }

    /**
     * 返回键监听
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (mWebView.canGoBack()){
                        mWebView.goBack();
                        count = 0;
                    } else {
                        if (count == 0 && mUserData == null){
                            KEY_LAUNCH_STATUS = false;
                            setViewVisibility();
                            count++;
                        }else {
                            finish();
                        }

                    }
                    Log.d("KeyEvent", keyCode + "");
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebSettings.setJavaScriptEnabled(true);
    }

    @Override
    protected void onStop(){
        super.onStop();
        mWebSettings.setJavaScriptEnabled(false);
    }

    @Override
    protected void onDestroy() {
        if(mWebView !=null ){
            mWebView.loadDataWithBaseURL(null, "", "text/html",
                    "utf-8", null);
            ((ViewGroup)mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }
}
