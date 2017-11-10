package pt.truewind.cronostest;

/**
 * Created by Eric on 09/11/2017.
 *
 * Referência: https://www.mobomo.com/2011/05/how-to-use-application-object-of-android/
 *
 */

import android.app.Application;
import android.content.res.Configuration;

import pt.truewind.cronostest.util.Foreground;

public class PortalConosApp extends Application {
    private static PortalConosApp singleton;

    public static PortalConosApp getInstance(){
        return singleton;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Foreground.init(this);
        singleton = this;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}