package pt.truewind.cronostest.task;

import android.app.ProgressDialog;
import android.content.Context;

import pt.truewind.cronostest.R;
import pt.truewind.cronostest.constants.Constants;
import pt.truewind.cronostest.log.Logger;
import pt.truewind.cronostest.service.remote.RemoteAbstractService;
import pt.truewind.cronostest.util.ui.AlertPopupDialog;

/**
 * Created by mario.viegas on 11/05/2017.
 */

public class ProvisionalPasswordAsyncTask extends AbstractAsyncTask{

    public interface TaskListener {
        void onFinished(Boolean result);
    }

    // This is the reference to the associated listener
    private final TaskListener taskListener;

    private String username;
    private String password;
    private String url;

    public ProvisionalPasswordAsyncTask(final Context context, String username, String password,  String url, TaskListener taskListener) {
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
        Logger.d(null, "Authenticating");

        try {
            RemoteAbstractService service = new RemoteAbstractService(this.url);
            response = service.performPostCall(getPayload(), Constants.CONTENT_TYPE_FORM_DATA, Constants.POST);
            Logger.d(null, "Response Code da Senha Provisória: " + response);

            if(response.contains("sucesso")){
                status = true;
                return true;
            }
            else{
                return false;
            }
        } catch (Exception e) {
            // displayLoding(false);
            Logger.e(null, "Error ...");
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
        stringBuilder.append("UserName=").append(this.username)
                .append("&Password=").append(this.password);

        Logger.d(null, stringBuilder.toString());
        return stringBuilder.toString();
    }

}
