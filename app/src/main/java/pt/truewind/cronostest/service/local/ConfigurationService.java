package pt.truewind.cronostest.service.local;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.List;

import pt.truewind.cronostest.model.Configuration;
import pt.truewind.cronostest.persistence.RowMapper;
import pt.truewind.cronostest.persistence.TransactionCallback;

/**
 * Created by mario.viegas on 10/11/2016.
 */

public class ConfigurationService extends AbstractService {

    /**
     * Insert multiple configurations Check if configuration can be inserted.
     *
     * @param configuration Configuration to insert in database.
     * @return result of insert operation.
     */
    public boolean insert(final Configuration configuration) {

        try{
            this.dbHelper.begin();

            // Try found record in database.
            Configuration configurationInDB = this.findConfigurationById(configuration.getId());
            if ( configurationInDB != null ) {
                this.dbHelper.update(Configuration.TABLE_NAME, buildCVObject(configuration), "id = ?", new String[]{String.valueOf(configurationInDB.getId())});
            }
            else {
                this.dbHelper.insert(Configuration.TABLE_NAME, buildCVObject(configuration));
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
     * Get Configuration by Identifier
     *
     * @param id Configuration identifier
     * @return Configuration found
     */
    public Configuration findConfigurationById( final Integer id ) {
        try{
            transactionManager.lockDatabase();

            return this.transactionManager.doInTransaction(new TransactionCallback<Configuration>() {
                @Override
                public Configuration execute() {
                    return ConfigurationService.this.dbHelper.queryForSingle(Configuration.TABLE_NAME, null, "id = ?", new String[]{id.toString()}, null, null, null, new RowMapper<Configuration>() {
                        @Override
                        public Configuration map(final Cursor cursor) {
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
     * Get Configuration by name
     *
     * @param name Configuration name
     * @return Configuration found
     */
    public Configuration findConfigurationByName( final String name ) {
        try{
            transactionManager.lockDatabase();

            return this.transactionManager.doInTransaction(new TransactionCallback<Configuration>() {
                @Override
                public Configuration execute() {
                    return ConfigurationService.this.dbHelper.queryForSingle(Configuration.TABLE_NAME, null, "name = ?", new String[]{name}, null, null, null, new RowMapper<Configuration>() {
                        @Override
                        public Configuration map(final Cursor cursor) {
                            return buildEntityObject(cursor);
                        }
                    });
                }
            });

        } finally {
            transactionManager.unLockDatabase();
        }
    }

    public List<Configuration> findAll() {
        try{
            transactionManager.lockDatabase();

            return this.transactionManager.doInTransaction(new TransactionCallback<List<Configuration>>() {
                @Override
                public List<Configuration> execute() {
                    return ConfigurationService.this.dbHelper.queryForList(Configuration.TABLE_NAME, null, null, null, null, null, null, null, new RowMapper<Configuration>(){
                        @Override
                        public Configuration map(final Cursor cursor) {
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
     * Counts the number of configurations in database
     *
     * @return
     */

    public Integer countConfigurations(){
        try{
            transactionManager.lockDatabase();

            return this.transactionManager.doInTransaction(new TransactionCallback<Integer>() {
                @Override
                public Integer execute() {
                    return ConfigurationService.this.dbHelper.countAllRecords(Configuration.TABLE_NAME);
                }
            });

        } finally {
            transactionManager.unLockDatabase();
        }
    }

    private static ContentValues buildCVObject(Configuration configuration) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("id"          , configuration.getId());
        contentValues.put("name"    , configuration.getName());
        contentValues.put("value"    , configuration.getValue());

        return contentValues;
    }

    private static Configuration buildEntityObject(final Cursor cursor) {
        int index = 0;
        Configuration configuration = new Configuration();

        configuration.setId(cursor.getInt(index++));
        configuration.setName(cursor.getString(index++));
        configuration.setValue(cursor.getString(index++));

        return configuration;
    }

}
