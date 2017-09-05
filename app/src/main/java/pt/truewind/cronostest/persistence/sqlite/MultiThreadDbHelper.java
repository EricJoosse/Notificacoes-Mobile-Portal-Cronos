package pt.truewind.cronostest.persistence.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import pt.truewind.cronostest.log.Logger;
import pt.truewind.cronostest.persistence.DBHelper;

/**
 * Created by mario.viegas on 08/11/2016.
 */

public class MultiThreadDbHelper {
    private DBHelper dbHelper;
    private Context context;
    private boolean isLocked = false;
    private Long blockingThread = null;
    private int waitingThreads = 0;

    public static final MultiThreadDbHelper INSTANCE = new MultiThreadDbHelper();

    private MultiThreadDbHelper() {
    }

    public synchronized void init(final Context context) {
        this.context = context;
        this.dbHelper = new SQLiteHelper(this.context);
    }

    public synchronized void changeDB(String dbName) {
        this.dbHelper = new SQLiteHelper(context, dbName);
    }

    public synchronized DBHelper getDbHelper() {
        return this.dbHelper;
    }

    /**
     * Get the lock on the database
     */
    public synchronized void open() {
        lock();
        this.dbHelper.open();
    }


    /**
     * Lock the database
     */
    public void lock() {
        // Debug code
        this.waitingThreads++;

        if(this.waitingThreads > 2) {
            Logger.w("Waiting threads: "+ this.waitingThreads);
        }
        // ------------------

        while(this.isLocked && this.blockingThread != null && this.blockingThread != Thread.currentThread().getId()){
            try {
                wait();
            } catch (InterruptedException e) {
                Logger.e(e);
            }
        }

        this.waitingThreads--;

        if(this.blockingThread != null && this.blockingThread == Thread.currentThread().getId()) {
            Logger.e("The same Thread is locking the db twice! Fix it!");
        }

        this.blockingThread = Thread.currentThread().getId();
        this.isLocked = true;
    }

    public synchronized void close() {
        if(this.dbHelper != null) {
            this.dbHelper.close();
        }

        // Database was closed lets get a new SQLiteHelper object
        this.dbHelper = null;
        this.dbHelper = new SQLiteHelper(this.context);
        unlock();
    }

    public synchronized void shutdownDatabase() {
        if(this.dbHelper != null) {
            this.dbHelper.close();
            this.dbHelper = null;
        }
        unlock();
    }

    /**
     * Unlock the database
     */
    public synchronized void unlock(){
        if(this.blockingThread != null && this.blockingThread != Thread.currentThread().getId()) {
            Logger.e("MultiThreadDbHelper.INSTANCE.unlock() was called by a thread that did not have the lock! Fix it!");
        }

        this.isLocked = false;
        this.blockingThread = null;
        notify();
    }

    public static void releaseMemory() {

        int releasedMemory = SQLiteDatabase.releaseMemory();
        Logger.i("SQLite released " + (releasedMemory/1024) + " KB in memory");
    }
}
