package me.rosuh.searchindouban;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    private String userData;

    private WebSettings webSettings;

    private SweetAlertDialog pDialog;

    private TextView textView;

    private Button finishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = (WebView)findViewById(R.id.web_view);
        textView = (TextView)findViewById(R.id.text_view);
        webSettings = webView.getSettings();
        userData = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setCancelable(true);
        finishButton = (Button)findViewById(R.id.finish_button);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        webView.setWebViewClient(new WebViewClient());
        if (userData == null) {
            webView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            finishButton.setVisibility(View.VISIBLE);
            textView.setText("1. 选中文字\n" +
                    "2. 长按后选中分享\n" +
                    "3. 选中 ·查豆瓣· 进行查找\n");
        }else {
            webView.loadUrl("https://m.douban.com/search?query=" + userData);
            pDialog.setTitleText("加载中...");
            pDialog.setContentText("");
            webSettings.setSupportMultipleWindows(true);
            webView.setWebChromeClient(new WebChromeClient(){
                @Override
                public void onProgressChanged(WebView view, int progress) {
                    if (progress <= 70) {
                        pDialog.show();
                    }else{
                        pDialog.dismiss();
                    }
                }
            });
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
                    if (webView.canGoBack()) {
                        webView.goBack();
                    }else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        webSettings.setJavaScriptEnabled(true);
    }

    @Override
    protected void onStop(){
        super.onStop();
        webSettings.setJavaScriptEnabled(false);
    }
}
