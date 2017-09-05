package pt.truewind.cronostest.service.remote;

import android.webkit.CookieManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import pt.truewind.cronostest.log.Logger;

/**
 * Created by vasco.caetano on 04/11/2016.
 */
public class RemoteAbstractService {

    private String server;
    private CookieManager cookieManager;

    public RemoteAbstractService(String server) {
        this.cookieManager = CookieManager.getInstance();
        this.server = server;
    }

    public String performPostCall(String payload, String contentType, String requestMethod) {

        URL url;
        String response = "";
        try {
            url = new URL(this.server);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(30000);
            conn.setConnectTimeout(30000);
            conn.setRequestMethod(requestMethod);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("content-type", contentType);


            byte[] outputBytes = payload.getBytes("UTF-8");
            OutputStream os = conn.getOutputStream();
            os.write(outputBytes);

            int responseCode = conn.getResponseCode();
            Logger.d("Response Meassage: " + conn.getResponseMessage());

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response += line;
                }
                Logger.e("COOKIE: " + conn.getHeaderField("Set-Cookie"));
                this.cookieManager.setCookie(this.server, conn.getHeaderField("Set-Cookie"));
            }
            else {
                response="";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }


    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
