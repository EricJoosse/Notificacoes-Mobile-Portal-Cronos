package pt.truewind.cronostest.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Build;
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
        Logger.d("MainActivity: onCreate() entrado.");
        setContentView(R.layout.activity_main);

        SharedPreferences userDetails = this.getSharedPreferences("user", MODE_PRIVATE);

        this.webview = (WebView) findViewById(R.id.webView);
        this.webview.getSettings().setJavaScriptEnabled(true);

        // Testado que no Android 7.0 o APK fica um pouquinho mais rápido se desabilitar hardware acceleration,
        // então o seguinte foi feito no AndroidManifest.xml para TODAS as versões de Android:
//      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//          // chromium, enable hardware acceleration
//          this.webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//      } else {
//          // older android version, disable hardware acceleration
//          this.webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//      }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2)
            this.webview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

        this.webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
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
        Logger.d("MainActivity: onCreate() finalizado.");
    }

    @Override
    public void onBackPressed() {

        // Desabilitar o botão Voltar de Android pois isso está tratado dentro do web site:
//        if(webview.canGoBack()){
//            webview.goBack();
//        }
//        else {
//            finishAffinity();
//        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        // Desabilitar o botão Voltar de Android pois isso está tratado dentro do web site:
        return (keyCode == KeyEvent.KEYCODE_BACK ? true : super.onKeyDown(keyCode, event));
    }


    //register your activity onResume()
    @Override
    public void onResume() {
        super.onResume();

        Logger.d("MainActivity: onResume() entrado.");

        int qtdNotificacoesExternasNaoLidas = 0;

        try {
            android.app.NotificationManager mNotificationManager =
                    (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            qtdNotificacoesExternasNaoLidas = mNotificationManager.getActiveNotifications().length;
            Logger.d("MainActivity: onResume(): qtdNotificacoesExternasNaoLidas = " + qtdNotificacoesExternasNaoLidas);
        }
        catch (Exception ex) {
            // Não fazer nada. No caso de Android < 6.0 vai dar erro pois getActiveNotifications() não existe
            Logger.d("MainActivity: onResume() - catch entrado: ex.getMessage() = " + ex.getMessage());
        }

        this.registerReceiver(mMessageReceiver, new IntentFilter("com.google.firebase.MESSAGING_EVENT"));

     // if (qtdNotificacoesExternasNaoLidas > 0)
            refreshWebView();

        Logger.d("MainActivity: onResume() finalizado.");
    }

    //Must unregister onPause()
    @Override
    protected void onPause() {
        super.onPause();
        Logger.d("MainActivity: onPause() entrado.");
        this.unregisterReceiver(mMessageReceiver);
        Logger.d("MainActivity: onPause() finalizado.");
    }


    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.d("MainActivity: onReceive() entrado.");

            // Extract data included in the Intent
            String message = intent.getStringExtra("message");
            showNotification("", message);
            refreshWebView();

            //do other stuff here
            Logger.d("MainActivity: onReceive() finalizado.");
        }
    };

    /**
     * Show error popup
     */
    public void showNotification(String title, String message ){
        new AlertPopupDialog(this, title, message).show();
        Logger.d("MainActivity: showNotification(): message = " + message);
    }

    public void refreshWebView(){
        Logger.d("MainActivity: refreshWebView() entrado.");
        this.webview.loadUrl(BuildConfig.ENDPOINT + Constants.PRINCIPAL_ENDPOINT);
        Logger.d("MainActivity: refreshWebView() finalizado.");
    }

}