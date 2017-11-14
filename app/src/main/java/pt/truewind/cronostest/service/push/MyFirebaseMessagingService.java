package pt.truewind.cronostest.service.push;

import android.content.Context;
import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;

import pt.truewind.cronostest.constants.Constants;
import pt.truewind.cronostest.log.Logger;
import pt.truewind.cronostest.util.Foreground;

/**
 * Created by vasco.caetano on 04/11/2016.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    // There are two types of messages in FCM (Firebase Cloud Messaging):
    //       Display Messages: These messages trigger the onMessageReceived() callback
    //                         only when your app is in foreground
    //       Data Messages:    These messages trigger the onMessageReceived() callback
    //                         even if your app is in foreground/background/killed

    // Se nosso aplicativo estiver em segundo plano, ou fechado (arrastado para fora
    // da lista multitarefa de Android no terceiro botão (chamada de "Overview")
    // do sistema operacional),
    // ou se nosso aplicativo estiver em primeiro plano porém o celular estiver
    //  no modo "descanso" (tela cinza ou preta),
    // nossas notificações (de tipo Display Message) vão para a "bandeja de sistema"
    // do celular Android, então em todos estes casos as notificações são "EXTERNAS".

    // O onMessageReceived() do FirebaseMessagingService dispara apenas se o aplicativo
    // estiver aberto e no primeiro plano e o celular estiver ativo, então apenas neste
    // caso as notificações ficam "INTERNAS".
    // As notificações novas que chegam enquanto que o aplicativo estiver em primeiro plano e ativo,
    // ficariam perdidas se o desenvolvedor não fazer nada no onMessageReceived() do
    // MyFirebaseMessagingService e no onReceive() do BroadcastReceiver no MainActivity.java.
    //
    // Em todas as versões atuais do sistema operacional Android é totalmente impossível
    // redirecionar notificações para a "bandeja do sistema" de Android no caso que o aplicativo
    // estiver em primeiro plano.  Referência: por exemplo:
    // https://www.b4x.com/android/forum/threads/fcm-push-messages-foreground-background-data-notification.73356/

    // Se o usuário clicar em "Forçar Parada" nas configurações do nosso aplicativo,
    // as notificações externas param de chegar.

    // Se o usuário desinstalar nosso aplicativo, as notificações externas param de chegar,
    // mesmo se desinstalar sem forçar a parada primeiro e sem limpar dados primeiro.
    // Todos os casos acima foram testados com um celular real Motorolla Android 7.0.
    @Override
    public void onMessageReceived(com.google.firebase.messaging.RemoteMessage remoteMessage) {
        // Por enquanto não enviar para o arquivo de LOG remoto no site antes da autenticação:
        if (remoteMessage.getData() == null || remoteMessage.getData().size() == 0)
            Logger.d(this, "MyFirebaseMessagingService: onMessageReceived() entrado.");

        if(remoteMessage.getNotification() != null){
            String notification = remoteMessage.getNotification().getBody().toString();
            Logger.d(this, "MyFirebaseMessagingService: Notification = " + notification);
            updateMyActivity(this, notification);
        }
        if(!remoteMessage.getData().toString().equals("{}")) {
            Logger.d(this, "MyFirebaseMessagingService: remoteMessage.getData().toString() = " + remoteMessage.getData().toString());
        }

        if (remoteMessage.getData() == null || remoteMessage.getData().size() == 0)
            Logger.d(this, "MyFirebaseMessagingService: onMessageReceived() finalizado.");
    }

    // handleIntent() method is called everytime whether app is in foreground, background or killed state:
    @Override
    public void handleIntent(Intent intent) {
//      super.handleIntent(intent);
        Logger.d(null, null, "MyFirebaseMessagingService - handleIntent() entrado ", true);
        Logger.d(null, null, "MyFirebaseMessagingService - handleIntent(): Foreground.get().isBackground() = " + Foreground.get().isBackground(), true);

        if (Foreground.get().isBackground()) {
            for (String key : intent.getExtras().keySet()) {
                Object value = intent.getExtras().get(key);
                // Apenas logar para SD card:
                Logger.d(null, null, "MyFirebaseMessagingService - handleIntent(): key = " + key + ", value = " + value.toString(), true);
            }
            Logger.d(null, null, intent.getExtras().getString("key"), true);
            Logger.d(null, null, intent.getExtras().getString("gcm.notification.body"), true);
            Logger.d(null, null, intent.getExtras().getString("stuff"), true);

            if (intent.getExtras() != null) {
                String notification_msg = intent.getExtras().getString("gcm.notification.body");
                if (notification_msg.toLowerCase().indexOf("cotação") > -1)
                    Constants.tipoNotificacao = Constants.NOTIFICACAO_COTACAO;
                else if (notification_msg.toLowerCase().indexOf("ordem") > -1)
                    Constants.tipoNotificacao = Constants.NOTIFICACAO_ORDEM;

                Logger.d(null, null, "MyFirebaseMessagingService - handleIntent(): Constants.tipoNotificacao = " + Constants.tipoNotificacao, true);
            }
        }
        Logger.d(null, null, "MyFirebaseMessagingService - handleIntent() finalizado ", true);
    }


    // This function will create an intent. This intent must take as parameter the "unique_name" that you registered your activity with
    static void updateMyActivity(Context context, String message) {

        Intent intent = new Intent("com.google.firebase.MESSAGING_EVENT");

        //put whatever data you want to send, if any
        intent.putExtra("message", message);
     // intent.putExtra("Title", "Portal Cronos");  .....Testado que isso não tem efeito nas notificações externas. Apenas deve ter efeito nas notificações internas
     //                                             .....(se fazer intent.getStringExtra("Title") no BroadcastReceiver - onReceive() no MainActivity.java).

        //send broadcast
        context.sendBroadcast(intent);
    }

}
