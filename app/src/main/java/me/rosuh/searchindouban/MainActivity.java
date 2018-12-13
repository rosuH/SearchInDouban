package me.rosuh.searchindouban;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * 这个类是用来显示 App 主界面的，包括执行用户输入、搜索等；
 * 类里包含主界面的所有组件，使用 setVisibility() 方法来控制部件显示或隐藏
 * @author rosuh 2018-4-24 10:40:23
 * @version 2.1
 * @since 1.8
 *
 */
public class MainActivity extends AppCompatActivity {
    private boolean mIsVisible = true;
    private WebView mWebView;
    private WebViewUtil mWebViewUtil;
    private EditText mEditText;
    private ImageButton mImageButton;
    private ProgressBar mProgressBar;
    private TextView mTextView;
    private ImageView mErrorImageView;
    private TextView mErrorTextView;
    private int goBackCount = 0;
    private boolean isExit;

    private IntentFilter mIntentFilter;
    private NetworkChangeReceiver networkChangeReceiver;

    private ImageButton mAboutPageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAboutPageButton = findViewById(R.id.about_image_button_main);
        mImageButton = findViewById(R.id.image_button_search_main);
        mProgressBar = findViewById(R.id.progress_bar_main);
        mEditText = findViewById(R.id.edit_view_main);
        mTextView = findViewById(R.id.text_view_tip_main);
        mErrorImageView = findViewById(R.id.error_image_view_main);
        mErrorTextView = findViewById(R.id.error_text_view_main);
        mWebView = findViewById(R.id.web_view_content_main);
        mErrorTextView.setVisibility(View.GONE);
        mErrorImageView.setVisibility(View.GONE);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, mIntentFilter);

        mWebViewUtil = new WebViewUtil();
        mWebView = mWebViewUtil
                .with(this)
                .setWebView(mWebView)
                .setProgress(mProgressBar)
                .start();

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInput();
                checkNetworkAndUpdateUI();
            }
        });

        mEditText.setImeOptions(EditorInfo.IME_ACTION_SEND);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean isActionDone = actionId == EditorInfo.IME_ACTION_DONE;
                boolean isActionSend = actionId == EditorInfo.IME_ACTION_SEND;
                boolean isEventAvailable = (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                        && event.getAction() == KeyEvent.ACTION_DOWN);

                if (isActionDone || isActionSend || isEventAvailable) {
                    checkInput();
                    checkNetworkAndUpdateUI();
                }
                return false;
            }
        });

        mAboutPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AboutPageDialog.class));
            }
        });
    }

    /**
     * 检查网络可用性
     */
    private void checkNetworkAndUpdateUI(){
        // 网络可用，已经显示了网络错误图像，表示网络已经恢复正常
        if (NetworkUtil.isNetworkAvailableAndConnected(MainActivity.this) &&
                mErrorImageView.getVisibility() == VISIBLE){
            mErrorTextView.setVisibility(View.GONE);
            mErrorImageView.setVisibility(View.GONE);
            mWebView.setVisibility(View.VISIBLE);
            return;
        }
        // 网络不可用，错误图像没有显示，WebView 显示，则表示浏览过程出错
        if (!NetworkUtil.isNetworkAvailableAndConnected(MainActivity.this) &&
                mWebView.getVisibility() == VISIBLE){
            mErrorTextView.setVisibility(View.VISIBLE);
            mErrorImageView.setVisibility(View.VISIBLE);
            mWebView.setVisibility(GONE);
            return;
        }
        // 网络不可用，错误图像已显示，搜索栏没有显示，则表示回退到搜索页
        if (mErrorImageView.getVisibility() == VISIBLE){
            mErrorTextView.setVisibility(View.GONE);
            mErrorImageView.setVisibility(View.GONE);
        }
    }

    /**
     * 检查输入是否合法
     * @return
     */
    private boolean checkInput(){
        String str = mEditText.getText().toString();
        if (!str.isEmpty()){
            setViewVisibility();
            NetworkUtil.doSearch(str, mWebView);
            return true;
        }else {
            Toast.makeText(MainActivity.this, R.string.nothing, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * 设置视图可见性
     */
    private void setViewVisibility(){
        if (mIsVisible){
            mIsVisible = false;
            mWebView.setVisibility(VISIBLE);
            mProgressBar.setVisibility(VISIBLE);

            mEditText.setVisibility(View.GONE);
            mImageButton.setVisibility(View.GONE);
            mTextView.setVisibility(View.GONE);
            mAboutPageButton.setVisibility(View.GONE);
        }else {
            mIsVisible = true;
            mEditText.setVisibility(VISIBLE);
            mImageButton.setVisibility(VISIBLE);
            mTextView.setVisibility(VISIBLE);
            mAboutPageButton.setVisibility(View.VISIBLE);

            mWebView.setVisibility(GONE);
            mProgressBar.setVisibility(GONE);
        }
    }

    /**
     * 返回键监听
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((event != null) && (event.getAction() == KeyEvent.ACTION_DOWN)) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (mWebView.canGoBack()){
                        mWebView.goBack();
                        return true;
                    }
                    if (mWebView.getVisibility() == VISIBLE || mErrorImageView.getVisibility() == VISIBLE){
                        checkNetworkAndUpdateUI();
                        goBackCount = 1;
                    }
                    if (goBackCount == 1){
                        setViewVisibility();
                        goBackCount = 0;
                    }else {
                        exitAppByDoubleClick();
                    }
                    return true;
                default:
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 双击退出
     */
    private void exitAppByDoubleClick() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

        if (!isExit){
            isExit = true;
            Toast.makeText(MainActivity.this, R.string.exit, Toast.LENGTH_SHORT)
                    .show();
            scheduledExecutorService.schedule(new Runnable() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000, TimeUnit.MILLISECONDS);
        }else {
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
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
