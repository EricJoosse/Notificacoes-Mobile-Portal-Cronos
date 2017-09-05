package pt.truewind.cronostest.persistence;

import pt.truewind.cronostest.persistence.sqlite.MultiThreadDbHelper;

/**
 * Created by mario.viegas on 08/11/2016.
 */

public abstract class TransactionManager {

    public abstract <T> T doInTransaction(TransactionCallback<T> transactionCallback);

    public void lockDatabase(){
        MultiThreadDbHelper.INSTANCE.lock();
    }

    public void unLockDatabase(){
        MultiThreadDbHelper.INSTANCE.unlock();
    }
}
