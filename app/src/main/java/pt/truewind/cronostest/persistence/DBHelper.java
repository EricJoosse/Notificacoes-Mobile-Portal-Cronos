package pt.truewind.cronostest.persistence;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import pt.truewind.cronostest.model.Endpoint;

/**
 * Created by mario.viegas on 08/11/2016.
 */

public interface DBHelper {

    <T> T queryForSingle(String tableName, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, RowMapper<T> rowMapper);

    <T> List<T> queryForList(String tableName, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit, RowMapper<T> rowMapper);

    long insert(String tableName, ContentValues cv);

    int update(String tableName, ContentValues cv, String whereClause, String[] whereArgs);

    int delete(String tableName, String whereClause, String[] whereArgs);

    int countAllRecords(String tableName);

    void begin();

    void commit();

    void rollback();

    void close();

    void open();

    SQLiteDatabase getSQLiteDatabase();
}
