package me.rosuh.searchindouban;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.daasuu.ahp.AnimateHorizontalProgressBar;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity {
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

        mWebView.setWebViewClient(new WebViewClient());
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
        setViewVisibility();
        mWebView.loadUrl("https://m.douban.com/search?query=" + data);
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
                        if (count == 0){
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
}
