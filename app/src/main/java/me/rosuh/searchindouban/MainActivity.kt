package me.rosuh.searchindouban

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_main.*

/**
 * 这个类是用来显示 App 主界面的，包括执行用户输入、搜索等；
 * 类里包含主界面的所有组件，使用 setVisibility() 方法来控制部件显示或隐藏
 * @author rosuh
 * @date 2019-2-10
 */
class MainActivity : BaseActivity() {

    private var isMainUIVisible = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        image_button_search_main!!.setOnClickListener {
            checkInput()
        }
        edit_view_main!!.imeOptions = EditorInfo.IME_ACTION_SEND
        edit_view_main!!.setOnEditorActionListener { _, actionId, event ->
            val isActionDone = actionId == EditorInfo.IME_ACTION_DONE
            val isActionSend = actionId == EditorInfo.IME_ACTION_SEND
            val isEventAvailable = (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER
                    && event.action == KeyEvent.ACTION_DOWN)
            if (isActionDone || isActionSend || isEventAvailable) {
                checkInput()
                true
            } else {
                false
            }
        }

        about_image_button_main!!.setOnClickListener {
            AboutPageDialog().show(supportFragmentManager, "About Dialog")
        }
    }

    override fun bindWebView(): WebView {
        return web_view_content_main
    }

    override fun bindToolBar(): Toolbar {
        return tb_activity_main
    }

    override fun bindProgressBar(): ProgressBar {
        return progress_bar_main
    }

    override fun bindLayout(): Int {
        return R.layout.activity_main
    }

    override fun bindSwipeRefreshLayout(): androidx.swiperefreshlayout.widget.SwipeRefreshLayout {
        return srl_main
    }

    /**
     * 检查输入是否合法
     * @return
     */
    private fun checkInput(): Boolean {
        val str = edit_view_main!!.text.toString()
        return if (str.isEmpty()) {
            Toast.makeText(this@MainActivity, R.string.nothing, Toast.LENGTH_SHORT).show()
            false
        } else {
            hideSoftKeyBoard()
            setViewVisibility()
            NetworkUtil.doSearch(str, web_view_content_main)
            true
        }
    }

    private fun hideSoftKeyBoard() {
        (this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(edit_view_main.windowToken, 0)
    }

    /**
     * 设置视图可见性
     */
    private fun setViewVisibility() {
        isMainUIVisible = if (isMainUIVisible) {
            setVisible(web_view_content_main, abl_main, progress_bar_main, srl_main)
            setGone(edit_view_main, image_button_search_main, about_image_button_main)
            false
        } else {
            setVisible(edit_view_main, image_button_search_main, about_image_button_main)
            setGone(web_view_content_main, abl_main, progress_bar_main, srl_main)
            true
        }
    }

    private fun setVisible(vararg views: View) {
        views.forEach {
            it.visibility = VISIBLE
        }
    }

    private fun setGone(vararg views: View) {
        views.forEach {
            it.visibility = GONE
        }
    }

    /**
     * 返回键监听
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event != null && event.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    // 如果 webView 不可以返回 并且在浏览状态
                    if (!super.onKeyDown(keyCode, event)) {
                        if (web_view_content_main.visibility == VISIBLE) {
                            setViewVisibility()
                        } else {
                            super.exitAppByDoubleClick()
                        }
                    }
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onSupportNavigateUp(): Boolean {
        if (!super.onSupportNavigateUp()) {
            if (web_view_content_main.visibility == VISIBLE) {
                setViewVisibility()
            } else {
                super.exitAppByDoubleClick()
            }
        }
        return true
    }
}
