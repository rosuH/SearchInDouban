package me.rosuh.searchindouban

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.Toast

import java.io.ByteArrayInputStream

/**
 * 此类的功能是：集成对 WebView 的初始化、设置和销毁工作
 *
 * @author rosuh 2018-4-24 10:40:23
 * @version 2.1
 * @since 1.8
 */

class WebViewUtil {

    private var mContext: Context? = null

    private lateinit var webView: WebView

    /**
     * 功能：保留 context
     *
     * @param context 接受当前的 context
     * @return 返回保存了 context 的 WebViewUtil 对象
     */
    fun with(context: Context): WebViewUtil {
        mContext = context
        return this
    }

    /**
     * 功能：完成对 WebView 的设置，处于链式调用中的最终环节
     *
     * @return 返回一个设置好了的 webView 对象
     */
    fun build(): WebView {
        return webView
    }

    /**
     * 功能：outWebView 设置方法，对传入的 outWebView 进行基础性的设置
     * 1. 调用 setSetting(Setting) 方法对 outWebView 进行 setting 设置
     * 2. 调用 setClient(WebView) 方法对 outWebView 进行 client 设置
     * 3. 将设置后的 outWebView 保存入 WebViewUtil 对象的实例变量中
     *
     * @return 返回一个更新了 outWebView 变量的 WebViewUtil 对象
     */
    fun setWebView(outWebView: WebView): WebViewUtil {
        setSetting(outWebView.settings)
        setClient(outWebView)
        this.webView = outWebView
        return this
    }

    /**
     * 功能：为 webView 设置进度条
     * 1. 先执行对 webView 的检查，如果不合法则返回并打印错误信息
     * 2. 使用 WebViewUtil 对象里的 webView 变量来设置
     *
     * @return 返回设置了进度条的 WebViewUtil 对象
     */

    fun setProgress(progressBar: ProgressBar): WebViewUtil {
        webView.webChromeClient = object : WebChromeClient() {

            override fun onProgressChanged(view: WebView, progress: Int) {
                if (progress >= VALUE_PROGRESS) {
                    progressBar.visibility = View.GONE
                } else {
                    progressBar.visibility = View.VISIBLE
                    progressBar.progress = progress
                }
            }
        }

        return this
    }

    /**
     * 功能： 私有的方法，用来执行对 webView Client 的常用设置
     * 1. 重写对 SSL 错误的接收方法，允许使用 HTTP 链接
     *
     * @return 返回一个更新了 webView 变量的 WebViewUtil 对象
     */
    private fun setClient(webView: WebView): WebViewUtil {
        webView.webViewClient = object : WebViewClient() {
            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
                handler.proceed()
            }

            override fun onPageFinished(view: WebView, url: String) {
                for (i in 0..4) {
                    webView.loadUrl("javascript:(function() { document.getElementsByClassName('Advertisement')[$i].style.display = 'none'; })()")
                }
            }
            override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
                if (url.isNullOrEmpty() || view == null) return super.shouldInterceptRequest(view, url)
                return if (NetworkUtil.isAdDomain(url.toString())) {
                    createEmptyResource()
                } else super.shouldInterceptRequest(view, url)
            }

            override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
                return if (NetworkUtil.isAdDomain(request.url.toString())) {
                    createEmptyResource()
                } else super.shouldInterceptRequest(view, request)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url.isNullOrEmpty() || view == null) return super.shouldOverrideUrlLoading(view, url)
                if (url.startsWith("douban:")) {
                    if (url.startsWith("douban:")) {
                        handleAppRequest(view, url)
                        return true
                    }
                }
                return super.shouldOverrideUrlLoading(view, url)
            }

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val url = request.url.toString()
                if (url.startsWith("douban:")) {
                    handleAppRequest(view, url)
                    return true
                }
                return super.shouldOverrideUrlLoading(view, request)
            }
        }
        return this
    }

    private fun handleAppRequest(webView: WebView, url: String) {
        Log.i(TAG, "shouldOverrideUrlLoading: $url")
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        if (intent.resolveActivity(mContext!!.packageManager) == null) {
            webView.post {
                Toast.makeText(mContext, "没有安装豆瓣鸭？", Toast.LENGTH_SHORT).show()
                if (webView.canGoBack()) {
                    webView.goBack()
                }
            }
        } else {
            mContext!!.startActivity(intent)
            webView.post {
                if (webView.canGoBack()) {
                    webView.goBack()
                }
            }
        }
    }

    /**
     * 功能：私有的方法，用来执行对 webView 的常用设置
     * 1. 不允许多窗口
     * 2. 不允许访问本地文件
     * 3. 允许使用 JavaScript
     */
    private fun setSetting(setting: WebSettings) {
        setting.setSupportMultipleWindows(false)
        setting.allowFileAccessFromFileURLs = false
        setting.allowFileAccess = false
        setting.allowUniversalAccessFromFileURLs = false
        setting.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        setting.javaScriptEnabled = true
    }

    /**
     * 功能：销毁 WebView
     */
    fun onDestroy(webView: WebView?) {
        if (webView != null) {
            webView.loadDataWithBaseURL(
                null, "", "text/html",
                "utf-8", null
            )
            (webView.parent as ViewGroup).removeView(webView)
            webView.destroy()
        }
    }

    companion object {

        private const val VALUE_PROGRESS = 75

        private const val TAG = "WebViewUtil"

        fun createEmptyResource(): WebResourceResponse {
            return WebResourceResponse("text/plain", "utf-8", ByteArrayInputStream("".toByteArray()))
        }
    }
}
