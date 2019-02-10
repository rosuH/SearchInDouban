package me.rosuh.searchindouban

import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.webkit.WebView
import android.widget.ProgressBar
import kotlinx.android.synthetic.main.activity_main.*

/**
 * 这个类是用来显示 App 主界面的，包括执行用户输入、搜索等；
 * 类里包含主界面的所有组件，使用 setVisibility() 方法来控制部件显示或隐藏
 * @author rosuh
 * @date 2019-2-10
 */
class MainActivity : BaseActivity() {

    private var isMainUIVisible = true
    private var goBackCount = 0

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
            }
            false
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
            setViewVisibility()
            NetworkUtil.doSearch(str, web_view_content_main)
            true
        }
    }

    /**
     * 设置视图可见性
     */
    private fun setViewVisibility() {
        if (isMainUIVisible) {
            web_view_content_main.visibility = VISIBLE
            abl_main.visibility = VISIBLE

            edit_view_main.visibility = GONE
            image_button_search_main.visibility = GONE
            text_view_tip_main.visibility = GONE
            about_image_button_main.visibility = GONE
            isMainUIVisible = false
        } else {
            edit_view_main!!.visibility = VISIBLE
            image_button_search_main!!.visibility = VISIBLE
            text_view_tip_main!!.visibility = VISIBLE
            about_image_button_main!!.visibility = VISIBLE

            web_view_content_main.visibility = GONE
            abl_main.visibility = GONE
            isMainUIVisible = true
        }
    }

    /**
     * 返回键监听
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event != null && event.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    // 如果 webview 不可以返回 并且在浏览状态
                    if (!super.onKeyDown(keyCode, event)){
                        if (web_view_content_main.visibility == VISIBLE){
                            setViewVisibility()
                        }else {
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
        if (!super.onSupportNavigateUp()){
            if (web_view_content_main.visibility == VISIBLE){
                setViewVisibility()
            }else {
                super.exitAppByDoubleClick()
            }
        }
        return true
    }
}
