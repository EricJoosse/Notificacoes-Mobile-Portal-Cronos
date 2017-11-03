package pt.truewind.cronostest.log;

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

    public static void d(final String msg) {
        if (shouldBeLogged(DEBUG)) {
            Log.d(Constants.TAG, msg);
            CronosUtil.logarRemotamente(msg, false);
        }
    }

    public static void d(final String msg, boolean toLogarApenasCartaoMemoria) {
        if (shouldBeLogged(DEBUG)) {
            Log.d(Constants.TAG, msg);
            CronosUtil.logarRemotamente(msg, toLogarApenasCartaoMemoria);
        }
    }

    public static void i(final String msg) {
        if (shouldBeLogged(INFO)) {
            Log.i(Constants.TAG, msg);
            CronosUtil.logarRemotamente(msg, false);
        }
    }

    public static void i(final String msg, boolean toLogarApenasCartaoMemoria) {
        if (shouldBeLogged(INFO)) {
            Log.i(Constants.TAG, msg);
            CronosUtil.logarRemotamente(msg, toLogarApenasCartaoMemoria);
        }
    }

    public static void w(final String msg) {
        if (shouldBeLogged(WARNING)) {
            Log.w(Constants.TAG, msg);
        }
    }

    public static void e(final Throwable throwable) {
        String msg = "An exception has occurred:\n" + ExceptionUtils.getStackTrace(throwable);
        Log.e(Constants.TAG, msg);
    }

    public static void e(final String msg) {
        Log.e(Constants.TAG, msg);
    }

    public static boolean shouldBeLogged(final Integer level) {
        return (level >= logLevels.get(BuildConfig.LOGGER_LEVEL));
    }
}