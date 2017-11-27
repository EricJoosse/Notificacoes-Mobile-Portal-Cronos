package pt.truewind.cronostest.util.client;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.webkit.ClientCertRequest;
import android.webkit.HttpAuthHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import pt.truewind.cronostest.BuildConfig;
import pt.truewind.cronostest.activity.MainActivity;
import pt.truewind.cronostest.activity.LoginActivity;
import pt.truewind.cronostest.constants.Constants;
import pt.truewind.cronostest.log.Logger;
import pt.truewind.cronostest.model.Configuration;
import pt.truewind.cronostest.service.local.ConfigurationService;
import pt.truewind.cronostest.util.CronosUtil;
import pt.truewind.cronostest.util.system.SystemUtil;

/**
 * Created by vasco.caetano on 02/11/2016.
 */
public class CronosWebViewClient extends WebViewClient {

    private String username;
    private String password;
    private Context context;
    private String logoutURL;
    private ImageView loading;

    @Override
    @TargetApi(21)
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        Logger.d(this.context, "CronosWebViewClient: onReceivedHttpError() entrado: errorResponse.getStatusCode() = " + errorResponse.getStatusCode());
        if (errorResponse.getStatusCode() == 401 || errorResponse.getStatusCode() == 403) {
            LoginActivity telaLogin = new LoginActivity();
            telaLogin.showError("Queda da Autenticação", "Favor digitar seu usuário/senha novamente.");
            telaLogin.showLogin();
        }
    }

    @TargetApi(23)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, android.webkit.WebResourceError error) {
        Logger.d(this.context, "CronosWebViewClient: onReceivedError() entrado: error.getErrorCode() = " + error.getErrorCode());
        if (error.getErrorCode() == ERROR_TIMEOUT) {
            view.stopLoading();  // may not be needed
            Toast.makeText(this.context, "A Internet ou o WiFi caiu. Favor tentar mais tarde.", Toast.LENGTH_LONG).show();
            // view.loadData("A Internet ou o WiFi caiu. Favor tentar mais tarde.", "text/html", "utf-8");
            LoginActivity telaLogin = new LoginActivity();
            telaLogin.showLogin();
        }
    }


    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        Logger.d(this.context, "CronosWebViewClient: onReceivedHttpAuthRequest() entrado: host = " + host);
        Logger.d(this.context, "CronosWebViewClient: onReceivedHttpAuthRequest() entrado: realm = " +realm);
        handler.proceed(this.username, this.password);
    }

    @Override
    public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
    }

    @Override
    public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
        request.cancel();
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        loading.setVisibility(View.VISIBLE);
        Logger.d(this.context, "CronosWebViewClient: onPageStarted() entrado");
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        loading.setVisibility(View.GONE);
        Logger.d(this.context, "CronosWebViewClient: onPageStarted() finalizado");
    }

//    @Override
//    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//
//        if (url.contains("http://exitme")){
//            CronosUtil.doLogout();
//
//            ((Activity) this.context).finish();
//            return true;
//        }
//
//        Logger.d(this.context, url);
//        if( url.startsWith("http:") || url.startsWith("https:") ) {
//            return false;
//        }
//        // Otherwise allow the OS to handle things like tel, mailto, etc.
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//        context.startActivity(intent);
//        return true;
//    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        Logger.d(this.context, "CronosWebViewClient: shouldOverrideUrlLoading() entrado: SystemUtil.isOnline(this.context) = " + SystemUtil.isOnline(this.context));
        if (SystemUtil.isOnline(this.context)) {
            // return false to let the WebView handle the URL
            return false;
        } else {
            // show the proper "not connected" message
            Toast.makeText(this.context, "A Internet ou o WiFi caiu. Favor tentar mais tarde.", Toast.LENGTH_LONG).show();
            //  view.loadData("A Internet ou o WiFi caiu. Favor tentar mais tarde.", "text/html", "utf-8");
            ((Activity) this.context).finish();
            // return true if the host application wants to leave the current
            // WebView and handle the url itself
            return true;
        }
    }


    public void setUsername(String username){
        this.username = username;
    }

    public String getUsername(){
        return this.username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword(){
        return this.password;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getLogoutURL() {
        return logoutURL;
    }

    public void setLogoutURL(String logoutURL) {
        this.logoutURL = logoutURL;
    }

    public ImageView getProgressBar() {
        return loading;
    }

    public void setProgressBar(ImageView loading) {
        this.loading = loading;
    }
}
