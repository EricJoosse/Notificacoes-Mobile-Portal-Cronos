package pt.truewind.cronostest.task;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import pt.truewind.cronostest.BuildConfig;

import pt.truewind.cronostest.constants.Constants;
import pt.truewind.cronostest.log.Logger;
import pt.truewind.cronostest.model.Endpoint;
import pt.truewind.cronostest.service.local.EndpointService;
import pt.truewind.cronostest.service.remote.RemoteAbstractService;

/**
 * Created by mario.viegas on 09/11/2016.
 */

public class RegisterUserAppAsyncTask extends AbstractAsyncTask {

    public interface TaskListener {
        void onFinished(Boolean result);
    }

    // This is the reference to the associated listener
    private final TaskListener taskListener;

    private String username;
    private String password;
    private String url;


    public RegisterUserAppAsyncTask(final Context context, String username, String password, TaskListener taskListener) {
        super(context);

        this.username = username;
        this.password = password;
        this.taskListener = taskListener;
    }

    @Override
    protected void onPreExecute() {
        // Show dialog and block UI

        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean status = false;

        Integer id = 0;
        EndpointService endpointService = new EndpointService();
        Endpoint endpoint = endpointService.findEndpointById(id);
        endpoint.setUsername(username);
        endpoint.setPassword(password);
        endpointService.insert(endpoint);

        JSONObject tokenJSON = new JSONObject();
        try {
            tokenJSON.put("tokenId", endpoint.getToken());
            tokenJSON.put("userName", username);
        } catch (JSONException e) {
            Logger.e("Can´t format JSON");
        }
        Logger.d(tokenJSON.toString());
        //this.server = "http://10.123.175.136:8080/username/users";
        this.url = BuildConfig.ENDPOINT + Constants.TOKEN_ACCESS;

        String response = "";
        Logger.e("Send token to server");

        RemoteAbstractService service = new RemoteAbstractService(this.url);

        try {
            response = service.performPostCall(tokenJSON.toString(), Constants.CONTENT_TYPE_APP_JSON, Constants.POST);
            Logger.d("Response: " + response);
            status = true;
        }catch (Exception e) {
            // displayLoding(false);
            Logger.e("Error ...");
        }
        Logger.d("Status: " + String.valueOf(status));
        return status;
    }


    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if(this.taskListener != null) {
            this.taskListener.onFinished(result);
        }
    }
}
