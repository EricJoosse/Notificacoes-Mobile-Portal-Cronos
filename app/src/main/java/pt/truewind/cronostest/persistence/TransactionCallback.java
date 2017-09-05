package pt.truewind.cronostest.persistence;

/**
 * Created by mario.viegas on 08/11/2016.
 */
public interface TransactionCallback<T> {
    T execute();
}
