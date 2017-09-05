package pt.truewind.cronostest.task;

import android.app.ProgressDialog;
import android.content.Context;

import pt.truewind.cronostest.R;
import pt.truewind.cronostest.constants.Constants;
import pt.truewind.cronostest.log.Logger;
import pt.truewind.cronostest.service.remote.RemoteAbstractService;

/**
 * Created by mario.viegas on 12/05/2017.
 */

public class DefinitivePasswordAsyncTask extends AbstractAsyncTask {

    public interface TaskListener {
        void onFinished(Boolean result);
    }

    // This is the reference to the associated listener
    private final TaskListener taskListener;

    private String username;
    private String provisionalPassword;
    private String newPassword;
    private String confirmPassword;
    private String url;

    public DefinitivePasswordAsyncTask(final Context context, String username, String provisionalPassword, String newPassword, String confirmPassword, String url, TaskListener taskListener) {
        super(context);

        this.username = username;
        this.provisionalPassword = provisionalPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
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
        Logger.d("Authenticating");

        try {
            RemoteAbstractService service = new RemoteAbstractService(this.url);
            response = service.performPostCall(getPayload(), Constants.CONTENT_TYPE_FORM_DATA, Constants.POST);
            Logger.e("Response Code da Senha Definitiva: " + response);

            if(response.contains("sucesso")){
                status = true;
                return true;
            }
            else{
                return false;
            }
        } catch (Exception e) {
            // displayLoding(false);
            Logger.e("Error ...");
        }
        return status;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if(this.taskListener != null) {
            this.taskListener.onFinished(result);
        }
    }

    private String getPayload(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Login=").append(this.username)
                     .append("&Senha=").append(this.provisionalPassword)
                     .append("&NovaSenha=").append(this.newPassword)
                     .append("&ConfirmaSenha=").append(this.confirmPassword);

        Logger.d(stringBuilder.toString());
        return stringBuilder.toString();
    }
}
