package me.rosuh.searchindouban

import android.app.SearchManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.VISIBLE
import android.view.Window
import android.webkit.WebView
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_quick_search.*

/**
 * 此类的功能是：
 * 1. 接受用户通过 ACTION_SEND 和 ACTION_PROGRESS_TEXT 的输入数据
 * 2. 调用搜索并在当前类的 WebView 对象中显示出来
 * @author rosuh
 */
class QuickSearchDialogActivity : BaseActivity() {

    private var userData: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val action = intent.action
        var isActionProcessText = false
        var isActionSearchText = false
        var isActionSend = false

        if (action != null) {
            isActionProcessText = action == Intent.ACTION_PROCESS_TEXT
            isActionSearchText = action == Intent.ACTION_SEARCH
            isActionSend = action == Intent.ACTION_SEND
        }

        if (isActionSend) {
            userData = intent.getStringExtra(Intent.EXTRA_TEXT)
        } else if (isActionProcessText && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            userData = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString()
        } else if (isActionSearchText) {
            userData = intent.getStringExtra(SearchManager.QUERY)
        }
        if (userData == null || userData!!.isEmpty()) {
            Toast.makeText(this, R.string.error_empty_data, Toast.LENGTH_SHORT).show()
            return
        }
        NetworkUtil.doSearch(userData!!, web_view_quick_search!!)
    }

    override fun bindWebView(): WebView {
        return web_view_quick_search
    }

    override fun bindToolBar(): Toolbar {
        return tb_quick_search
    }

    override fun bindProgressBar(): ProgressBar {
        return progress_bar_quick_search
    }

    override fun bindLayout(): Int {
        return R.layout.activity_quick_search
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event != null && event.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    // 如果 webview 不可以返回 并且在浏览状态
                    if (!super.onKeyDown(keyCode, event)){
                        super.exitAppByDoubleClick()
                    }
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onSupportNavigateUp(): Boolean {
        return if (super.onSupportNavigateUp()){
            false
        }else {
            super.exitAppByDoubleClick()
            true
        }
    }
}
