package pt.truewind.cronostest.persistence;

import android.database.Cursor;

/**
 * Created by mario.viegas on 08/11/2016.
 */

public interface RowMapper<T> {
    T map(Cursor cursor);
}
