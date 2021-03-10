package me.rosuh.searchindouban

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.webkit.WebView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 *
 * @author rosuh
 * @date 2019/2/10
 */
abstract class BaseActivity : AppCompatActivity() {

    private val webView by lazy {
        webViewUtil
            .with(this)
            .setWebView(bindWebView())
            .setProgress(progressBar)
            .setSwipeRefreshLayout(bindSwipeRefreshLayout())
            .build()
    }
    private val toolbar by lazy {
        bindToolBar()
    }
    private val progressBar by lazy {
        bindProgressBar()
    }
    private val webViewUtil = WebViewUtil()

    private var isExit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(bindLayout())
        initToolBar()
        if (0 != (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE)) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
    }

    abstract fun bindWebView(): WebView

    abstract fun bindToolBar(): Toolbar

    abstract fun bindProgressBar(): ProgressBar
    @LayoutRes
    abstract fun bindLayout(): Int

    abstract fun bindSwipeRefreshLayout(): SwipeRefreshLayout

    private fun initToolBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
    }

    override fun onSupportNavigateUp(): Boolean {
        return if (webView.canGoBack()) {
            webView.goBack()
            true
        } else {
            false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tool_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_item_link -> {
                val intentLink = Intent(Intent.ACTION_VIEW, Uri.parse(webView.url))
                startActivity(Intent.createChooser(intentLink, "在哪个浏览器打开..."))
            }
            R.id.menu_item_share -> {
                val intentShare = Intent(Intent.ACTION_SEND)
                intentShare.putExtra(Intent.EXTRA_TEXT, "我在豆瓣查到了这个：" + "\n" + webView.url)
                intentShare.type = "text/plain"
                startActivity(Intent.createChooser(intentShare, "分享链接到..."))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 返回键监听
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event != null && event.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    return if (webView.canGoBack()) {
                        webView.goBack()
                        true
                    } else {
                        false
                    }
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    fun exitAppByDoubleClick() {
        val scheduledExecutorService = Executors.newScheduledThreadPool(1)
        if (isExit) {
            finish()
        } else {
            isExit = true
            Toast.makeText(this, R.string.exit, Toast.LENGTH_SHORT)
                .show()
            scheduledExecutorService.schedule({ isExit = false }, 2000, TimeUnit.MILLISECONDS)
        }
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        webViewUtil.onDestroy(webView)
    }
}