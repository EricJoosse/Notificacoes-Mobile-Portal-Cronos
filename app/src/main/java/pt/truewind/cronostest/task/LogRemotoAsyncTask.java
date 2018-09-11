package pt.truewind.cronostest.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;

import pt.truewind.cronostest.BuildConfig;
import pt.truewind.cronostest.R;
import pt.truewind.cronostest.constants.Constants;
import pt.truewind.cronostest.log.Logger;
import pt.truewind.cronostest.model.Endpoint;
import pt.truewind.cronostest.service.local.EndpointService;
import pt.truewind.cronostest.service.remote.RemoteAbstractService;
import pt.truewind.cronostest.util.CronosUtil;

/**
 * Created by Eric Jooosse on 09/11/2017.
 */

public class LogRemotoAsyncTask extends AbstractAsyncTask {

    public interface TaskListener {
        void onFinished(Boolean result);
    }

    // This is the reference to the associated listener
    private final TaskListener taskListener;

    private String mensagem;
    private String url;

    public LogRemotoAsyncTask(final Context context, String  mensagem, TaskListener taskListener) {
        super(context);

        this.mensagem =  mensagem;

        this.taskListener = taskListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean status = true;

        try {
            Integer id = 0;
            EndpointService endpointService = new EndpointService();
            Endpoint endpoint = endpointService.findEndpointById(id);

            if (endpoint != null && endpoint.getUsername() != null && endpoint.getToken() != null && this.mensagem != null) {
                JSONObject tokenJSON = new JSONObject();
                tokenJSON.put("userName", endpoint.getUsername());
                tokenJSON.put("versaoAndroid", pt.truewind.cronostest.util.CronosUtil.getDescricaoAbreviadaVersaoAndroid(Build.VERSION.SDK_INT));
                tokenJSON.put("modeloMobile", Build.BRAND + "." + Build.MODEL);
                tokenJSON.put("versaoAPK", Constants.VERSAO_APK);
                tokenJSON.put("tokenId", endpoint.getToken());
                tokenJSON.put("linhaArqLog", this.mensagem);
                String url = BuildConfig.ENDPOINT + Constants.LOG_REMOTO;

                String response = "";
                RemoteAbstractService service = new RemoteAbstractService(url);
                response = service.performPostCall(tokenJSON.toString(), Constants.CONTENT_TYPE_APP_JSON, Constants.POST);
            }
        }
        catch (JSONException e) {
            // Logger.e(null, null, "Can´t format JSON", true);
        }
        catch (Exception e) {
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

}
