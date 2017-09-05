package pt.truewind.cronostest.persistence.sqlite;

import pt.truewind.cronostest.persistence.DBHelper;
import pt.truewind.cronostest.persistence.TransactionCallback;
import pt.truewind.cronostest.persistence.TransactionManager;

/**
 * Created by mario.viegas on 08/11/2016.
 */

public class SQLiteTransactionManager extends TransactionManager {

    private DBHelper dbHelper;

    public SQLiteTransactionManager(final DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public <T> T doInTransaction(final TransactionCallback<T> transactionCallback) {
        this.dbHelper.begin();
        try {
            T value = transactionCallback.execute();

            this.dbHelper.commit();

            return value;
        } catch (Throwable t) {
            this.dbHelper.rollback();
            throw new IllegalStateException(t);
        }
    }
}
