package pt.truewind.cronostest.util;


import android.os.Build;
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
                    //  tokenJSON.put("versaoAndroid", Integer.toString(Build.VERSION.SDK_INT));
                        tokenJSON.put("versaoAndroid", getDescricaoAbreviadaVersaoAndroid(Build.VERSION.SDK_INT));
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

    public  static  String getDescricaoAbreviadaVersaoAndroid(int apiLevel) {
        switch (apiLevel) {
            case 1: return "Android 1.0";
            case 2: return "Android 1.1";
            case 3: return "Android 1.5";
            case 4: return "Android 1.6";
            case 5: return "Android 2.0";
            case 6: return "Android 2.0.1";
            case 7: return "Android 2.1";
            case 8: return "Android 2.2";
            case 9: return "Android 2.3";
            case 10: return "Android 2.3.3";
            case 11: return "Android 3.0";
            case 12: return "Android 3.1";
            case 13: return "Android 3.2";
            case 14: return "Android 4.0";
            case 15: return "Android 4.0.3";
            case 16: return "Android 4.1";
            case 17: return "Android 4.2";
            case 18: return "Android 4.3";
            case 19: return "Android 4.4";
            case 20: return "Android 4.4";
            case 21: return "Android 5.0";
            case 22: return "Android 5.1";
            case 23: return "Android 6.0";
            case 24: return "Android 7.0";
            case 25: return "Android 7.1.1";
            case 26: return "Android 8.0";
            case 27: return "Android 8.1";
            case 10000: return "Current Development Version";
            default: return "VersãoAndroidDesconhecida";
        }
    }

    public  static  String getDescricaoVersaoAndroid(int apiLevel) {
        switch (apiLevel) {
            case 1: return "Android 1.0";
            case 2: return "Petit Four (Android 1.1)";
            case 3: return "Cupcake (Android 1.5)";
            case 4: return "Donut (Android 1.6)";
            case 5: return "Eclair (Android 2.0)";
            case 6: return "Eclair (Android 2.0.1)";
            case 7: return "Eclair (Android 2.1)";
            case 8: return "Froyo (Android 2.2)";
            case 9: return "Gingerbread (Android 2.3)";
            case 10: return "Gingerbread (Android 2.3.3)";
            case 11: return "Honeycomb (Android 3.0)";
            case 12: return "Honeycomb (Android 3.1)";
            case 13: return "Honeycomb (Android 3.2)";
            case 14: return "Ice Cream Sandwich (Android 4.0)";
            case 15: return "Ice Cream Sandwich (Android 4.0.3)";
            case 16: return "Jelly Bean (Android 4.1)";
            case 17: return "Jelly Bean (Android 4.2)";
            case 18: return "Jelly Bean (Android 4.3)";
            case 19: return "KitKat (Android 4.4)";
            case 20: return "KitKat Watch (Android 4.4)";
            case 21: return "Lollipop (Android 5.0)";
            case 22: return "Lollipop (Android 5.1)";
            case 23: return "Marshmallow (Android 6.0)";
            case 24: return "Nougat (Android 7.0)";
            case 25: return "Nougat (Android 7.1.1)";
            case 26: return "Oreo (Android 8.0)";
            case 27: return "Oreo (Android 8.1)";
            case 10000: return "Current Development Version";
            default: return "VersãoAndroidDesconhecida";
        }
    }
}
