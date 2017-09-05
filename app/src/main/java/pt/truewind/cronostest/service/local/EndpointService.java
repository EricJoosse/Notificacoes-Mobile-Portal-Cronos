package pt.truewind.cronostest.service.local;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.List;

import pt.truewind.cronostest.model.Endpoint;
import pt.truewind.cronostest.persistence.RowMapper;
import pt.truewind.cronostest.persistence.TransactionCallback;
import pt.truewind.cronostest.persistence.sqlite.SQLiteHelper;

/**
 * Created by mario.viegas on 08/11/2016.
 */

public class EndpointService extends AbstractService {

    /**
     * Insert multiple endpoints. Check if endpoint can be inserted.
     *
     * @param endpoint Endpoint to insert in database.
     * @return result of insert operation.
     */
    public boolean insert(final Endpoint endpoint) {

        try{
            this.dbHelper.begin();

            // Try found record in database.
            Endpoint endpointInDB = this.findEndpointById(endpoint.getId());
            if ( endpointInDB != null ) {
                this.dbHelper.update(Endpoint.TABLE_NAME, buildCVObject(endpoint), "id = ?", new String[]{String.valueOf(endpointInDB.getId())});
            }
            else {
                this.dbHelper.insert(Endpoint.TABLE_NAME, buildCVObject(endpoint));
            }

            this.dbHelper.commit();

            return true;
        }
        catch (Exception e) {
            this.dbHelper.rollback();
            return false;
        }
    }

    /**
     * Get Endpoint by Identifier
     *
     * @param id Endpoint identifier
     * @return Endpoint found
     */
    public Endpoint findEndpointById( final Integer id ) {
        try{
            transactionManager.lockDatabase();

            return this.transactionManager.doInTransaction(new TransactionCallback<Endpoint>() {
                @Override
                public Endpoint execute() {
                    return EndpointService.this.dbHelper.queryForSingle(Endpoint.TABLE_NAME, null, "id = ?", new String[]{id.toString()}, null, null, null, new RowMapper<Endpoint>() {
                        @Override
                        public Endpoint map(final Cursor cursor) {
                            return buildEntityObject(cursor);
                        }
                    });
                }
            });

        } finally {
            transactionManager.unLockDatabase();
        }
    }

    public List<Endpoint> findAll() {
        try{
            transactionManager.lockDatabase();

            return this.transactionManager.doInTransaction(new TransactionCallback<List<Endpoint>>() {
                @Override
                public List<Endpoint> execute() {
                    return EndpointService.this.dbHelper.queryForList(Endpoint.TABLE_NAME, null, null, null, null, null, null, null, new RowMapper<Endpoint>(){
                        @Override
                        public Endpoint map(final Cursor cursor) {
                            return buildEntityObject(cursor);
                        }
                    });
                }
            });

        } finally {
            transactionManager.unLockDatabase();
        }
    }
    /**
     * Counts the number of endpoints in database
     *
     * @return
     */

    public Integer countEndpoints(){
        try{
            transactionManager.lockDatabase();

            return this.transactionManager.doInTransaction(new TransactionCallback<Integer>() {
                @Override
                public Integer execute() {
                    return EndpointService.this.dbHelper.countAllRecords(Endpoint.TABLE_NAME);
                }
            });

        } finally {
            transactionManager.unLockDatabase();
        }
    }

    private static ContentValues buildCVObject(Endpoint endpoint) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("id"          , endpoint.getId());
        contentValues.put("token"    , endpoint.getToken());
        contentValues.put("username"    , endpoint.getUsername());
        contentValues.put("password"    , endpoint.getPassword());

        return contentValues;
    }

    private static Endpoint buildEntityObject(final Cursor cursor) {
        int index = 0;
        Endpoint endpoint = new Endpoint();

        endpoint.setId(cursor.getInt(index++));
        endpoint.setToken(cursor.getString(index++));
        endpoint.setUsername(cursor.getString(index++));
        endpoint.setPassword(cursor.getString(index++));

        return endpoint;
    }
}
