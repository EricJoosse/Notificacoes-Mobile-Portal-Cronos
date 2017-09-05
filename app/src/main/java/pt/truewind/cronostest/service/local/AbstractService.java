package pt.truewind.cronostest.service.local;

import pt.truewind.cronostest.persistence.DBHelper;
import pt.truewind.cronostest.persistence.TransactionManager;
import pt.truewind.cronostest.persistence.sqlite.MultiThreadDbHelper;
import pt.truewind.cronostest.persistence.sqlite.SQLiteTransactionManager;

/**
 * Created by mario.viegas on 08/11/2016.
 */

public class AbstractService {

    protected DBHelper dbHelper;
    protected TransactionManager transactionManager;

    public AbstractService() {
        this.dbHelper = MultiThreadDbHelper.INSTANCE.getDbHelper();
        this.transactionManager = new SQLiteTransactionManager(this.dbHelper);
    }
}
