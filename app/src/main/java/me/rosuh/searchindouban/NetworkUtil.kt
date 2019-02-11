package me.rosuh.searchindouban

import android.webkit.WebView
import java.util.regex.Pattern

/**
 * 这个类的功能是：
 * 1. 接受输入的数据并执行搜索
 * 2. 检查网络是否可用
 * * @author rosuh 2018-4-24 10:40:23
 * @version 2.1
 * @since 1.8
 */
class NetworkUtil {

    companion object {
        private const val DOUBAN_URL = "https://m.douban.com/search?query="
        private const val REGEX = "[^\\u4e00-\\u9fa5\\w]"

        /**
         * 功能：静态方法，调用 WebView.loadUrl(Url) 执行搜索
         * @param data 用户数据
         * @param webView  接收已初始化好的 WebView
         */
        fun doSearch(data: String, webView: WebView) {
            var data = data
            // global search REGEX
            val pattern = Pattern.compile(REGEX)
            val matcher = pattern.matcher(data)
            data = matcher.replaceAll("")
            webView.loadUrl(DOUBAN_URL + data)
        }

        fun isAdDomain(url: String): Boolean {
            return !url.contains("douban")
        }
    }
}
