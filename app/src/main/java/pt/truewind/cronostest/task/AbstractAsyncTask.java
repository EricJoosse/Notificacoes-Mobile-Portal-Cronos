package pt.truewind.cronostest.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by vasco.caetano on 04/11/2016.
 */
public abstract class AbstractAsyncTask extends AsyncTask<Void, String, Boolean> {

    protected Context context;

    protected ProgressDialog progressDialog;

    public AbstractAsyncTask(final Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {}

    @Override
    protected abstract Boolean doInBackground(final Void... params);

    @Override
    protected void onPostExecute(final Boolean result) {}

    @Override
    protected void onProgressUpdate (String... values) {}

}