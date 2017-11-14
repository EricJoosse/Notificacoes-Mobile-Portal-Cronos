package pt.truewind.cronostest.persistence.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pt.truewind.cronostest.R;
import pt.truewind.cronostest.constants.Constants;
import pt.truewind.cronostest.log.Logger;
import pt.truewind.cronostest.model.Configuration;
import pt.truewind.cronostest.model.Endpoint;
import pt.truewind.cronostest.persistence.DBHelper;
import pt.truewind.cronostest.persistence.RowMapper;
import pt.truewind.cronostest.util.system.SystemUtil;

/**
 * Created by mario.viegas on 08/11/2016.
 */

public class SQLiteHelper extends SQLiteOpenHelper implements DBHelper {

    private static final int VERSION = 1;
    public static final String DATABASE_NAME = "cronos";
    private static final String ACTIVATE_AUTO_VACUUM = "PRAGMA auto_vacuum = 1";
    private static final String OPTIMIZE_DATABASE = "VACUUM;";

    private SQLiteDatabase db;
    private final Context context;
    private static boolean createdDatabase = false;

    public SQLiteHelper(final Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.context = context;
    }

    public SQLiteHelper(final Context context, String dbName) {
        super(context, dbName, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        Logger.d(null, "Database created 0");
        try{
            loadDatabaseFromFile(R.raw.tables, db);
        }catch(Exception e) {
            Logger.e(null, e);
        }
        createdDatabase = true;
        Logger.d(null, "Database created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Endpoint.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Configuration.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public <T> T queryForSingle(final String tableName, final String[] columns, final String selection, final String[] selectionArgs, final String groupBy, final String having, final String orderBy, final RowMapper<T> rowMapper) {

        Cursor cursor = null;

        T entity = null;

        try {

            cursor = this.db.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy);

            if (cursor != null && cursor.moveToFirst()) {
                entity = rowMapper.map(cursor);
            }

        } catch (Exception e) {
            Logger.e(null, e);
            entity = null;

        }  finally {
            closeCursor(cursor);
        }

        return entity;
    }

    @Override
    public <T> List<T> queryForList(final String tableName, final String[] columns, final String selection, final String[] selectionArgs, final String groupBy, final String having, final String orderBy, final String limit, final RowMapper<T> rowMapper) {

        Cursor cursor = null;

        List<T> list = new ArrayList<T>();

        try {

            if (StringUtils.isEmpty(limit)) {
                cursor = this.db.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy);
            } else {
                cursor = this.db.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            }

            list = buildList(cursor, rowMapper);

        } catch (Exception e) {
            Logger.e(null, e);
            list = new ArrayList<T>();
        } finally {
            closeCursor(cursor);
        }

        return list;
    }

    private <T> List<T> buildList(Cursor cursor, final RowMapper<T> rowMapper) {

        List<T> list = new ArrayList<T>();

        if(cursor != null && cursor.moveToFirst()) {
            do {
                T t = rowMapper.map(cursor);
                list.add(t);
            } while (cursor.moveToNext());
        }

        return list;

    }

    private void closeCursor(Cursor cursor) {

        if(cursor != null) {
            cursor.close();

            if(!cursor.isClosed()) {
                Logger.e(null, "cursor.close() was called but cursor is still opened!");
            }
        }
    }

    @Override
    public int countAllRecords(final String tableName) {
        return getCount(null, tableName);
    }

    private int getCount(final String whereClause, final String tableName) {

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM ");
        sql.append(tableName);

        if (whereClause != null) {
            sql.append(whereClause);
        }

        int count = 0;

        SQLiteStatement statement = null;

        try {

            statement = this.db.compileStatement(sql.toString());
            statement.clearBindings();
            count = (int) statement.simpleQueryForLong();
        } catch (Exception e) {
            Logger.e(null, e);
            count = 0;
        }  finally {
            if(statement != null) {
                statement.close();
            }
        }

        return count;

    }

    @Override
    public void open() {
        this.db = super.getWritableDatabase();

        if(createdDatabase) {

            Logger.i(null, "Vacuum in progress");

            // Activate auto vacuum
            this.db.execSQL(ACTIVATE_AUTO_VACUUM);
            this.db.execSQL(OPTIMIZE_DATABASE);
            createdDatabase = false;
            Logger.i(null, "Vacuum done");
        }
    }

    @Override
    public long insert(String tableName, ContentValues cv) {
        return this.db.insert(tableName, null, cv);
    }

    @Override
    public int update(String tableName, ContentValues cv, String whereClause, String[] whereArgs) {
        return this.db.update(tableName, cv, whereClause, whereArgs);
    }

    @Override
    public int delete(String tableName, String whereClause, String[] whereArgs) {
        return this.db.delete(tableName, whereClause, whereArgs);
    }

    @Override
    public void begin() {
        open();
        this.db.beginTransaction();
    }

    @Override
    public void commit() {
        this.db.setTransactionSuccessful();
        this.db.endTransaction();
    }

    @Override
    public void rollback() {
        this.db.endTransaction();
    }

    @Override
    public SQLiteDatabase getSQLiteDatabase() {
        return this.db;
    }

    @Override
    public void close() {
        super.close();
    }

    private void loadDatabaseFromFile(final Integer resourceId, final SQLiteDatabase db) throws IOException{
        SystemUtil.runQueriesFromRawFolder(this.context, resourceId, db);
    }
}
