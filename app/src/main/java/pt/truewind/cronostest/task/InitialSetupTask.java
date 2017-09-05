package pt.truewind.cronostest.task;

import android.content.Context;
import android.content.Intent;

import pt.truewind.cronostest.log.Logger;
import pt.truewind.cronostest.persistence.sqlite.MultiThreadDbHelper;

/**
 * Created by mario.viegas on 08/11/2016.
 */

public class InitialSetupTask extends AbstractAsyncTask {

    public static final String INITIAL_SETUP_DONE = "initialSetupDone";

    public InitialSetupTask(final Context context) {
        super(context);
    }

    @Override
    protected Boolean doInBackground(final Void... params) {

        MultiThreadDbHelper.INSTANCE.lock();
        MultiThreadDbHelper.INSTANCE.unlock();

        return true;
    }

    @Override
    protected void onPostExecute(final Boolean result) {
        super.onPostExecute(result);
        this.context.sendBroadcast(new Intent(INITIAL_SETUP_DONE));
    }

}
