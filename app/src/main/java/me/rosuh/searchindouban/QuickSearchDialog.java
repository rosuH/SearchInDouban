package me.rosuh.searchindouban;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 此类的功能是：
 *      1. 接受用户通过 ACTION_SEND 和 ACTION_PROGRESS_TEXT 的输入数据
 *      2. 调用搜索并在当前类的 WebView 对象中显示出来
 * @author rosuh 2018-4-24 10:40:23
 * @version 2.1
 * @since 1.8
 *
 */
public class QuickSearchDialog extends AppCompatActivity {

    private WebView mWebView;
    private WebViewUtil mWebViewUtil;
    private ProgressBar mProgressBar;
    private ImageView mErrorImageView;
    private TextView mErrorTextView;

    private String mUserData;

    private IntentFilter mIntentFilter;
    private NetworkChangeReceiver networkChangeReceiver;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_search);

        String action = getIntent().getAction();
        boolean isActionProcessText = false;
        boolean isActionSearchText = false;
        boolean isActionSend = false;

        if (action != null){
            isActionProcessText = action.equals(Intent.ACTION_PROCESS_TEXT);
            isActionSearchText = action.equals(Intent.ACTION_SEARCH);
            isActionSend = action.equals(Intent.ACTION_SEND);
        }

        if (isActionSend){
            mUserData = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        }else if (isActionProcessText && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            mUserData = getIntent().getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString();
        }else if (isActionSearchText){
            mUserData = getIntent().getStringExtra(SearchManager.QUERY);
        }
        if(mUserData == null || mUserData.isEmpty()){
            Toast.makeText(this, R.string.error_empty_data, Toast.LENGTH_SHORT).show();
            return;
        }

        mProgressBar = findViewById(R.id.progress_bar_quick_search);
        mErrorImageView = findViewById(R.id.error_image_view_quick_search);
        mErrorTextView = findViewById(R.id.error_text_view_quick_search);
        mWebView = findViewById(R.id.web_view_quick_search);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, mIntentFilter);

        checkNetworkAndUpdateUI();

        mWebViewUtil = new WebViewUtil();
        mWebView = mWebViewUtil
                .with(this)
                .setWebView(mWebView)
                .setProgress(mProgressBar)
                .start();

        NetworkUtil.doSearch(mUserData, mWebView);
    }

    private void checkNetworkAndUpdateUI(){
        if (!NetworkUtil.isNetworkAvailableAndConnected(QuickSearchDialog.this)){
            mErrorTextView.setVisibility(View.VISIBLE);
            mErrorImageView.setVisibility(View.VISIBLE);
            mWebView.setVisibility(View.GONE);
        }else {
            mWebView.setVisibility(View.VISIBLE);
            mErrorTextView.setVisibility(View.GONE);
            mErrorImageView.setVisibility(View.GONE);
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
                    } else {
                        finish();
                    }
                    Log.d("KeyEvent", keyCode + "");
                    return true;
                default:
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWebView != null){
            mWebView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWebView != null){
            mWebView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebViewUtil.onDestroy(mWebView);
        unregisterReceiver(networkChangeReceiver);

    }

    private class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkNetworkAndUpdateUI();
        }
    }
}
