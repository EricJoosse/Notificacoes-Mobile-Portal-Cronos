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
        Logger.d(this, "MainActivity: onCreate() entrado.");
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

        Logger.d(this, username);
        Logger.d(this, password);

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
                            Logger.i(null, "Token sent to server");
                        } else {
                            Logger.i(null, "send token FAIL");
                        }
                    }
                }
        ).execute();

        // O onResume() sempre executa após o onCreate(), então não duplicar o seguinte:
     // refreshWebView(Constants.PRINCIPAL_ENDPOINT);

        Logger.d(this, "MainActivity: onCreate() finalizado.");
    }

    @Override
    public void onBackPressed() {

        // Foi testado que se commentar TUDO dentro do onBackPressed(),
        // o botão "Voltar" de Android está desabilitado,
        // e não volta mais para a tela anterior nem para o aplicativo anterior.

        // Desabilitar o botão Voltar de Android pois isso está tratado dentro do web site:
//        if(webview.canGoBack()){
//            webview.goBack();
//        }
//        else {
//            finishAffinity();       // Volta para a activity anterior, fechando  também a mesma activity em outros apps, se tiver
//            finish();               // Volta para a activity anterior
//            super.onBackPressed();  // Volta para a activity anterior
//        }
    }


    // O seguinte serve apenas para Android < 2.0:
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        // Desabilitar o botão Voltar de Android pois isso está tratado dentro do web site:
        return (keyCode == KeyEvent.KEYCODE_BACK ? true : super.onKeyDown(keyCode, event));
    }


    //@TargetApi(23) // Foi testado que isso não tem nenhum efeito no runtime. Apenas serve para suprimir mensagens
                     // de compilação, então não serve para nada...
    @Override
    public void onResume() {
        super.onResume();

        Logger.d(this, "MainActivity: onResume() entrado.");

        int qtdNotificacoesExternasNaoLidas = 0;

        try {
            android.app.NotificationManager mNotificationManager =
                    (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Foi testado que em um Android 22 que se comentar "if (Build.VERSION.SDK_INT >= 23)"
            // o aplicativo dá erro "Portal Cronos parou" porém nem executa o catch,
            // então neste caso o try/catch não serve para nada...
            if (Build.VERSION.SDK_INT >= 23) {
                qtdNotificacoesExternasNaoLidas = mNotificationManager.getActiveNotifications().length;
                Logger.d(this, "MainActivity: onResume(): qtdNotificacoesExternasNaoLidas = " + qtdNotificacoesExternasNaoLidas);
            }
        }
        catch (Exception ex) {
            Logger.d(this, "MainActivity: onResume() - catch entrado: ex.getMessage() = " + ex.getMessage());
        }

        this.registerReceiver(mMessageReceiver, new IntentFilter("com.google.firebase.MESSAGING_EVENT"));

        Logger.d(this, "MainActivity: onResume(): Constants.tipoNotificacao = " + Constants.tipoNotificacao);
     // if (qtdNotificacoesExternasNaoLidas > 0) {
            if (Constants.tipoNotificacao == Constants.NOTIFICACAO_COTACAO)
                refreshWebView(Constants.SECONDARY_ENDPOINT);
            else if (Constants.tipoNotificacao == Constants.NOTIFICACAO_ORDEM)
                refreshWebView(Constants.PRINCIPAL_ENDPOINT);
     // }

        Logger.d(this, "MainActivity: onResume() finalizado.");
    }

    //Must unregister onPause()
    @Override
    protected void onPause() {
        super.onPause();
        Logger.d(this, "MainActivity: onPause() entrado.");
        this.unregisterReceiver(mMessageReceiver);
        Logger.d(this, "MainActivity: onPause() finalizado.");
    }


    //This is the handler that will manage to process the broadcast intent:
    //Este lugar trata os cliques nas notificações "INTERNAS":
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.d(context, "MainActivity: onReceive() entrado.");

            // Extract data included in the Intent
            String message = intent.getStringExtra("message");
         // String titulo = intent.getStringExtra("Title");  .... Não faz sentido pegar isso do MyFirebaseMessagingService
            showNotification("", message);

            refreshWebView("onReceive");

            //do other stuff here
            Logger.d(context, "MainActivity: onReceive() finalizado.");
        }
    };

    /**
     * Show error popup
     */
    public void showNotification(String title, String message ){
        new AlertPopupDialog(this, title, message).show();
        Logger.d(this, "MainActivity: showNotification(): message = " + message);
    }


    public void refreshWebView(String endpoint) {
        Logger.d(this, "MainActivity: refreshWebView() entrado.");

        // No caso que chegar uma notificação INTERNA de aviso de cotação ou de ordem,
        // atualizar as telas e os indicadores e navegar para a tela onde o usuário estava:
        if (endpoint.equals("onReceive")) {
            String urlAnterior = this.webview.getUrl().toLowerCase();
            Logger.d(this, "urlAnterior = " + urlAnterior);

            if (urlAnterior.indexOf("cotacao") > -1 && urlAnterior.indexOf("consulta") > -1 && urlAnterior.indexOf("activetab=1") > -1) {
                // O seguinte funciona, provavelmente por causa de this.webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE) no onCreate():
                this.webview.reload();

                // Se precisar alterar a configuração do cache no futuro, o refresh provavelmente
                // não vai funcionar mais usando webview.reload() pois a URL é a mesma.

                // Nem o seguinte vai funcionar mais, provavelmente: this.webview.loadUrl(BuildConfig.ENDPOINT + Constants.SECONDARY_ENDPOINT);

                // Alternativos:
                // this.webview.loadUrl("javascript:window.location.reload(true)");

                // Ou:
                // String urlNovo = BuildConfig.ENDPOINT + Constants.SECONDARY_ENDPOINT + "&dummy=" + Long.toString(Math.round(Math.random()));
                // Logger.d(this, "urlNovo = " + urlNovo);
                // this.webview.loadUrl(urlNovo);
            }
            else if (urlAnterior.indexOf("cotacao") > -1 && urlAnterior.indexOf("consulta") > -1 && urlAnterior.indexOf("activetab=2") > -1) {
                // Veja a comentário acima, substituindo Constants.SECONDARY_ENDPOINT por Constants.PRINCIPAL_ENDPOINT
                this.webview.reload();
            }
            else if (urlAnterior.indexOf("detalheordem") > -1) {
                // não precisa atualizar nada, pois não tem indicadores nesta tela
            }
        }
        else
            this.webview.loadUrl(BuildConfig.ENDPOINT + endpoint);

        Logger.d(this, "MainActivity: refreshWebView() finalizado.");
    }

}