package pt.truewind.cronostest.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import pt.truewind.cronostest.BuildConfig;
import pt.truewind.cronostest.R;
import pt.truewind.cronostest.constants.Constants;
import pt.truewind.cronostest.log.Logger;
import pt.truewind.cronostest.task.RegisterUserAppAsyncTask;
import pt.truewind.cronostest.util.client.CronosWebViewClient;
import pt.truewind.cronostest.util.ui.AlertPopupDialog;

public class MainActivity extends AppCompatActivity {

    private WebView webview;
    private String username;
    private String password;
    private ImageView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences userDetails = this.getSharedPreferences("user", MODE_PRIVATE);

        this.webview = (WebView) findViewById(R.id.webView);
        this.webview.getSettings().setJavaScriptEnabled(true);
        this.webview.setFocusable(true);

        this.loading = (ImageView) findViewById(R.id.loading);
        Glide.with(this).load(R.mipmap.loading1).into(loading);
        //this.loading = (ProgressBar) findViewById(R.id.loading);
        loading.setVisibility(View.GONE);

        //this.webview.setWebViewClient(new CronosWebViewClient());

        this.username = userDetails.getAll().get("username").toString();
        this.password = userDetails.getAll().get("password").toString();

        CronosWebViewClient cronosWebViewClient = new CronosWebViewClient() {};

        Logger.d(username);
        Logger.d(password);

        cronosWebViewClient.setUsername(username);
        cronosWebViewClient.setPassword(password);
        cronosWebViewClient.setProgressBar(loading);
        cronosWebViewClient.setContext(this);

        this.webview.setWebViewClient(cronosWebViewClient);

        new RegisterUserAppAsyncTask(this, username, password,
                new RegisterUserAppAsyncTask.TaskListener() {

                    @Override
                    public void onFinished(Boolean result) {
                        if (result) {
                            Logger.i("Token sent to server");
                        } else {
                            Logger.i("send token FAIL");
                        }
                    }
                }
        ).execute();

        this.webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        refreshWebView();
    }

    @Override
    public void onBackPressed() {

        if(webview.canGoBack()){
            webview.goBack();
        }
        else {
            finishAffinity();
        }
    }

    //register your activity onResume()
    @Override
    public void onResume() {
        super.onResume();
        this.registerReceiver(mMessageReceiver, new IntentFilter("com.google.firebase.MESSAGING_EVENT"));
    }

    //Must unregister onPause()
    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(mMessageReceiver);
    }


    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract data included in the Intent
            String message = intent.getStringExtra("message");
            showNotification("Notification", message);
            refreshWebView();

            //do other stuff here
        }
    };

    /**
     * Show error popup
     */
    public void showNotification(String title, String message ){
        new AlertPopupDialog(this, title, message).show();
    }

    public void refreshWebView(){
        this.webview.loadUrl(BuildConfig.ENDPOINT + Constants.PRINCIPAL_ENDPOINT);
    }

}