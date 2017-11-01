package pt.truewind.cronostest.service.push;

import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import pt.truewind.cronostest.log.Logger;
import pt.truewind.cronostest.model.Endpoint;
import pt.truewind.cronostest.service.local.EndpointService;

/**
 * Created by vasco.caetano on 04/11/2016.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        Logger.d("MyFirebaseInstanceIDService: onTokenRefresh() entrado.");
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Logger.d("Refreshed token: " + refreshedToken);

        EndpointService endpointService = new EndpointService();
        String token = refreshedToken;
        Endpoint endpoint = new Endpoint(token);

        endpointService.insert(endpoint);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken);
        Logger.d("MyFirebaseInstanceIDService: onTokenRefresh() finalizado.");
    }
}
