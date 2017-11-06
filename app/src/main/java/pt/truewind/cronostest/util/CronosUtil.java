package pt.truewind.cronostest.util;


import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import org.json.JSONException;
import org.json.JSONObject;

import pt.truewind.cronostest.BuildConfig;
import pt.truewind.cronostest.activity.LoginActivity;
import pt.truewind.cronostest.constants.Constants;
import pt.truewind.cronostest.log.Logger;
import pt.truewind.cronostest.model.Configuration;
import pt.truewind.cronostest.model.Endpoint;
import pt.truewind.cronostest.service.local.ConfigurationService;
import pt.truewind.cronostest.service.local.EndpointService;
import pt.truewind.cronostest.service.remote.RemoteAbstractService;
import pt.truewind.cronostest.util.ui.AlertPopupDialog;

/**
 * Created by vasco.caetano on 21/11/2016.
 */
public class CronosUtil {

    public static void doLogout(){
        Logger.d("LogOut");
        ConfigurationService configurationService = new ConfigurationService();
        Configuration configuration = configurationService.findConfigurationByName(Constants.AUTO_LOGIN_KEY);
        configuration.setValue(Integer.toString(Constants.AUTO_LOGIN_DISABLED));
        configurationService.insert(configuration);
    }

    public static void logarRemotamente(String mensagem, boolean toLogarApenasCartaoMemoria) {
        if (Constants.toGerarArquivoDebug) {
            boolean temCartaoMemoria = false;

            try {
                String statusCartaoMemoria = android.os.Environment.getExternalStorageState();
                if (statusCartaoMemoria.equals(android.os.Environment.MEDIA_MOUNTED)) {
                    File cartaoMemoria = android.os.Environment.getExternalStorageDirectory();
                    File dir = new File(cartaoMemoria.getAbsolutePath() + "/pcronos/log");
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    Date hoje = new Date();
                    BufferedWriter bWriter = new BufferedWriter(new FileWriter(dir + "/debug." + new SimpleDateFormat("yyyy.MM.dd").format(hoje) + ".log", true));
                    bWriter.append(mensagem);
                    bWriter.newLine();
                    bWriter.newLine();
                    bWriter.flush();
                    bWriter.close();
                }
            }
            catch (IOException e) {
                // e.printStackTrace();
            }
            catch (Exception e) {
            }


            if (!toLogarApenasCartaoMemoria) {
                try {
                    Integer id = 0;
                    EndpointService endpointService = new EndpointService();
                    Endpoint endpoint = endpointService.findEndpointById(id);

                    if (endpoint != null && endpoint.getUsername() != null && endpoint.getToken() != null && mensagem != null) {
                        JSONObject tokenJSON = new JSONObject();
                        tokenJSON.put("userName", endpoint.getUsername());
                        tokenJSON.put("versaoAndroid", Integer.toString(Build.VERSION.SDK_INT));
                        tokenJSON.put("modeloMobile", Build.BRAND + "." + Build.MODEL);
                        tokenJSON.put("tokenId", endpoint.getToken());
                        tokenJSON.put("linhaArqLog", mensagem);
                        //this.server = "http://10.123.175.136:8080/username/users";
                        String url = BuildConfig.ENDPOINT + Constants.LOG_REMOTO;

                        String response = "";

                        RemoteAbstractService service = new RemoteAbstractService(url);

                        response = service.performPostCall(tokenJSON.toString(), Constants.CONTENT_TYPE_APP_JSON, Constants.POST);
                    }
                }
                catch (JSONException e) {
                    // Logger.e("Can´t format JSON");
                }
                catch (Exception e) {
                }
            }
        }
    } // Fim logarRemotamente()

}
