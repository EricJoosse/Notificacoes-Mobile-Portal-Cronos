package pt.truewind.cronostest.log;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.exception.ExceptionUtils;

import pt.truewind.cronostest.BuildConfig;
import pt.truewind.cronostest.constants.Constants;
import pt.truewind.cronostest.util.CronosUtil;

public class Logger {

    public static final int DEBUG = 0;
    public static final int INFO = 1;
    public static final int WARNING = 2;
    public static final int ERROR = 3;

    private static final List<Integer> logLevels = new ArrayList<>();

    static {
        logLevels.add(DEBUG);
        logLevels.add(INFO);
        logLevels.add(WARNING);
        logLevels.add(ERROR);
    }

    // Antes da primeira autenticação após a instalação do APK,
    // a chamada do web service do arquivo de log remoto não funciona,
    // porém também não dá erro não tratado para os usuários e também não trava o APK,
    // então antes da primeira autenticação não faz mau e simplesmente não faz nada.
    // Na segunda entrada funciona sim (devido ao autologin provavelmente):
    public static void d(Context context, final String msg) {
        if (shouldBeLogged(DEBUG)) {
            Log.d(Constants.TAG, msg);
            if (       msg.indexOf("MainActivity") > -1
                    || msg.indexOf("LoginActivity") > -1
                    || msg.indexOf("CotacaoActivity") > -1
                    || msg.indexOf("OrdemActivity") > -1
                    || msg.indexOf("CronosWebViewClient") > -1
                    || msg.indexOf("MyFirebaseMessagingService") > -1
               )
                CronosUtil.logarRemotamente(context, msg, false);
            else
                CronosUtil.logarRemotamente(context, msg, true);
        }
    }

    public static void d(Context context, final String msg, boolean toLogarApenasCartaoMemoria) {
        if (shouldBeLogged(DEBUG)) {
            Log.d(Constants.TAG, msg);
            CronosUtil.logarRemotamente(context, msg, toLogarApenasCartaoMemoria);
        }
    }

    // Veja o comentário acima de Logger.d() acima
    public static void i(Context context, final String msg) {
        if (shouldBeLogged(INFO)) {
            Log.i(Constants.TAG, msg);
            if (       msg.indexOf("MainActivity") > -1
                    || msg.indexOf("LoginActivity") > -1
                    || msg.indexOf("CotacaoActivity") > -1
                    || msg.indexOf("OrdemActivity") > -1
                    || msg.indexOf("CronosWebViewClient") > -1
                    || msg.indexOf("MyFirebaseMessagingService") > -1
               )
                CronosUtil.logarRemotamente(context, msg, false);
            else
                CronosUtil.logarRemotamente(context, msg, true);
        }
    }

    public static void i(Context context, final String msg, boolean toLogarApenasCartaoMemoria) {
        if (shouldBeLogged(INFO)) {
            Log.i(Constants.TAG, msg);
            CronosUtil.logarRemotamente(context, msg, toLogarApenasCartaoMemoria);
        }
    }

    public static void w(Context context, final String msg) {
        if (shouldBeLogged(WARNING)) {
            Log.w(Constants.TAG, msg);
            CronosUtil.logarRemotamente(context, msg, true);
        }
    }

    public static void e(Context context, final Throwable throwable) {
        String msg = "An exception has occurred:\n" + ExceptionUtils.getStackTrace(throwable);
        Log.e(Constants.TAG, msg);
        CronosUtil.logarRemotamente(context, msg, true);
    }

    public static void e(Context context, final String msg) {
        Log.e(Constants.TAG, msg);
        CronosUtil.logarRemotamente(context, msg, true);
    }

    public static boolean shouldBeLogged(final Integer level) {
        return (level >= logLevels.get(BuildConfig.LOGGER_LEVEL));
    }
}