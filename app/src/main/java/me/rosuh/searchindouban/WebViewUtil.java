package me.rosuh.searchindouban;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;

/**
 * 此类的功能是：集成对 WebView 的初始化、设置和销毁工作
 * @author rosuh 2018-4-24 10:40:23
 * @version 2.1
 * @since 1.8
 *
 */

public class WebViewUtil{
    private static final int VALUE_PROGRESS = 75;
    private Context mContext;
    private WebView mWebView;

    /**
     * 功能：保留 context
     * @param context 接受当前的 context
     * @return  返回保存了 context 的 WebViewUtil 对象
     */
    public WebViewUtil with(Context context){
        mContext = context;
        return this;
    }

    /**
     * 功能：完成对 WebView 的设置，处于链式调用中的最终环节
     * @return 返回一个设置好了的 webView 对象
     */
    public WebView start(){
        return mWebView;
    }

    /**
     * 功能：webView 设置方法，对传入的 webView 进行基础性的设置
     *      1. 调用 setSetting(Setting) 方法对 webView 进行 setting 设置
     *      2. 调用 setClient(WebView) 方法对 webView 进行 client 设置
     *      3. 将设置后的 webView 保存入 WebViewUtil 对象的实例变量中
     * @param webView
     * @return  返回一个更新了 webView 变量的 WebViewUtil 对象
     */
    public WebViewUtil setWebView(WebView webView){

        setSetting(webView.getSettings());
        setClient(webView);
        mWebView = webView;
        return this;
    }

    /**
     * 功能：为 webView 设置进度条
     *      1. 先执行对 webView 的检查，如果不合法则返回并打印错误信息
     *      2. 使用 WebViewUtil 对象里的 mWebView 变量来设置
     * @param mProgress
     * @return 返回设置了进度条的 WebViewUtil 对象
     */

    public WebViewUtil setProgress(final ProgressBar mProgress){
        if (mWebView == null){
            Log.i("WebViewUtil", "The webView is null");
            return null;
        }

        mWebView.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress >= VALUE_PROGRESS){
                    mProgress.setVisibility(View.GONE);
                }else {
                    mProgress.setVisibility(View.VISIBLE);
                    mProgress.setProgress(progress);
                }
            }
        });

        return this;
    }

    /**
     * 功能： 私有的方法，用来执行对 mWebView Client 的常用设置
     *      1. 重写对 SSL 错误的接收方法，允许使用 HTTP 链接
     * @param webView
     * @return  返回一个更新了 mWebView 变量的 WebViewUtil 对象
     */
    private WebViewUtil setClient(WebView webView){
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed();
                Toast.makeText(mContext, R.string.wait_ssl_response, Toast.LENGTH_SHORT)
                        .show();
            }

//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                try {
//                    Document document = Jsoup.connect(request.getUrl().toString()).get();
//                    boolean isContainAppHref = document.body().getElementsByTag("a").hasAttr("onclick");
//                    if (isContainAppHref){
//                        Elements elements = document.body().getElementsByTag("a");
//                        for (Element element: elements){
//                            if (element.hasAttr("onclick")){
//                                elements.remove(element);
//                            }
//                        }
//                    }
//                }catch (IOException ioe){
//                    ioe.printStackTrace();
//                    return false;
//                }
//                return true;
//            }
        });
        return this;
    }


    /**
     * 功能：私有的方法，用来执行对 mWebView 的常用设置
     *      1. 不允许多窗口
     *      2. 不允许访问本地文件
     *      3. 允许使用 JavaScript
     * @param setting
     */
    private void setSetting(WebSettings setting){
        setting.setSupportMultipleWindows(false);
        setting.setAllowFileAccessFromFileURLs(false);
        setting.setAllowFileAccess(false);
        setting.setAllowUniversalAccessFromFileURLs(false);
        setting.setJavaScriptEnabled(true);
    }

    /**
     * 功能：销毁 WebView
     * @param webView
     */
    public void onDestroy(WebView webView){
        if(webView !=null ){
            webView.loadDataWithBaseURL(null, "", "text/html",
                    "utf-8", null);
            ((ViewGroup)webView.getParent()).removeView(webView);
            webView.destroy();
        }
    }



}
