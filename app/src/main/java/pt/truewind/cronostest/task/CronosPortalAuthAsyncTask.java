package pt.truewind.cronostest.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;

import pt.truewind.cronostest.BuildConfig;
import pt.truewind.cronostest.R;
import pt.truewind.cronostest.activity.LoginActivity;
import pt.truewind.cronostest.activity.MainActivity;
import pt.truewind.cronostest.constants.Constants;
import pt.truewind.cronostest.log.Logger;
import pt.truewind.cronostest.model.Configuration;
import pt.truewind.cronostest.model.Endpoint;
import pt.truewind.cronostest.service.local.ConfigurationService;
import pt.truewind.cronostest.service.local.EndpointService;
import pt.truewind.cronostest.service.remote.RemoteAbstractService;
import pt.truewind.cronostest.util.ui.AlertPopupDialog;

public class CronosPortalAuthAsyncTask extends AbstractAsyncTask{

    public interface TaskListener {
        void onFinished(Boolean result);
    }

    // This is the reference to the associated listener
    private final TaskListener taskListener;

    private String username;
    private String password;
    private String url;

    public CronosPortalAuthAsyncTask(final Context context, String username, String password, String url, TaskListener taskListener) {
        super(context);

        this.username = username;
        this.password = password;
        this.url = url;

        this.taskListener = taskListener;
    }

    @Override
    protected void onPreExecute() {
        // Show dialog and block UI
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(this.context.getString(R.string.authenticating));

        //progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean status = false;

        String response = "";
        String responseSwitchToMobile = "";
        Logger.d(null, "CronosPortalAuthAsyncTask: Authenticating");

        try {
            // O seguinte funciona sincronamente antes do login, então nunca vai ter o risco
            // que o SwitchToMobile acontece tarde demais resultando em uma tela desktop no APK.
            // O seguinte executa no primeiro login, e também no autologin.
            // O seguinte é necessário para direcionar browsers em celulares e tablets para o ambiente
            // desktop e apenas APK´s para o ambiente mobile.
            if (this.url.endsWith("DoLogin")) {
                Logger.d(null, "CronosPortalAuthAsyncTask: if (this.url.endsWith(DoLogin)) entrado");
                RemoteAbstractService serviceAntes = new RemoteAbstractService(BuildConfig.ENDPOINT + Constants.SwitchToMobile);
                responseSwitchToMobile = serviceAntes.performPostCall(getPayload(), Constants.CONTENT_TYPE_FORM_DATA, Constants.POST);
                Logger.d(null, "CronosPortalAuthAsyncTask: Response Code de SwitchToMobile: " + responseSwitchToMobile);
                Logger.d(null, "CronosPortalAuthAsyncTask: serviceAntes.performPostCall(SwitchToMobile) passado");
            }

            RemoteAbstractService service = new RemoteAbstractService(this.url);
            response = service.performPostCall(getPayload(), Constants.CONTENT_TYPE_FORM_DATA, Constants.POST);
            Logger.d(null, "CronosPortalAuthAsyncTask: Response Code de DoLogin: " + response);

            if (response.contains("No momento o aplicativo para celular")) {
                status = false;
                ConfigurationService configurationService = new ConfigurationService();
                String value = Integer.toString(Constants.BLOCKED_PERFIL);
                Configuration configuration = new Configuration(Constants.AUTO_LOGIN_KEY, value);
                configurationService.insert(configuration);
                Logger.d(null, new ConfigurationService().findAll().toString() + " dentro do CronosPortalAuthAsyncTask");
                return false;
            }
            else if(response.equals(Constants.LOGIN_OK)){

                RemoteAbstractService serviceCot = new RemoteAbstractService(BuildConfig.ENDPOINT + Constants.COT_ACCESS);
                Logger.d(null, BuildConfig.ENDPOINT + Constants.COT_ACCESS);
                response = serviceCot.performPostCall(getPayloadCotacoes(), Constants.CONTENT_TYPE_FORM_DATA, Constants.POST);
                Logger.d(null, "CronosPortalAuthAsyncTask: Response Code de DoLogin: " + response);

                if(response.equals(Constants.LOGIN_OK)) {
                    status = true;
                    return true;
                }
            }
            else if(response.contains(Constants.NEW_PASSWORD_OK)){
                status = true;
                ConfigurationService configurationService = new ConfigurationService();
                String value = Integer.toString(Constants.PROVISIONAL_PASSWORD);
                Configuration configuration = new Configuration(Constants.AUTO_LOGIN_KEY, value);
                configurationService.insert(configuration);
                Logger.d(null, new ConfigurationService().findAll().toString() + " dentro do CronosPortalAuthAsyncTask");
                return true;
            }
            else if (response.contains("gerar uma nova senha provisória")) {
                status = false;
                ConfigurationService configurationService = new ConfigurationService();
                String value = Integer.toString(Constants.PROVISIONAL_PASSWORD);
                Configuration configuration = new Configuration(Constants.AUTO_LOGIN_KEY, value);
                configurationService.insert(configuration);
                Logger.d(null, new ConfigurationService().findAll().toString() + " dentro do CronosPortalAuthAsyncTask");
                return false;
            }
            else if (response.contains("Seu usuário foi bloqueado")) {
                status = false;
                ConfigurationService configurationService = new ConfigurationService();
                String value = Integer.toString(Constants.BLOCKED_USER);
                Configuration configuration = new Configuration(Constants.AUTO_LOGIN_KEY, value);
                configurationService.insert(configuration);
                Logger.d(null, new ConfigurationService().findAll().toString() + " dentro do CronosPortalAuthAsyncTask");
                return false;
            }
            else{
                return false;
            }
        } catch (Exception e) {
        // displayLoding(false);
        Logger.e(null, "CronosPortalAuthAsyncTask: Error ...");
    }

        return status;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if(this.taskListener != null) {
            this.taskListener.onFinished(result);
        }

        //progressDialog.dismiss();
    }

    private String getPayload(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("UserName=").append(this.username)
                        .append("&Password=").append(this.password);

        Logger.d(null, "CronosPortalAuthAsyncTask - getPayload() : " + stringBuilder.toString());
        return stringBuilder.toString();
    }

    private String getPayloadCotacoes(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("UserName=").append(this.username);
        Logger.d(null, "CronosPortalAuthAsyncTask - getPayloadCotacoes() : " + stringBuilder.toString());
        return stringBuilder.toString();
    }
}
