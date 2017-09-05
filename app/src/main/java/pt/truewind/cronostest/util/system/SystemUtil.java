package pt.truewind.cronostest.util.system;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import pt.truewind.cronostest.log.Logger;
import pt.truewind.cronostest.persistence.sqlite.MultiThreadDbHelper;
import pt.truewind.cronostest.persistence.sqlite.SQLiteHelper;
import pt.truewind.cronostest.task.InitialSetupTask;

/**
 * Created by mario.viegas on 08/11/2016.
 */

public class SystemUtil {

    private static boolean dbLoaded = false;


    /**
     * Load database instance.
     *
     * @param context context activity
     */
    public static void loadDatabase(Context context){

        Logger.d("load DATABASE " + dbLoaded);
        if(!dbLoaded) {

            if (MultiThreadDbHelper.INSTANCE.getDbHelper() == null) {
                MultiThreadDbHelper.INSTANCE.init(context);
                //MultiThreadDbHelper.INSTANCE.getDbHelper().open();
            }

            if (!doesDatabaseExist(context)) {
                new InitialSetupTask(context).execute();
            }

            dbLoaded = true;
        }
    }

    /**
     * Check if db file exists.
     *
     * @param context context activity
     * @return indication that database file exists or not.
     */
    private static boolean doesDatabaseExist(Context context) {
        File dbFile = context.getDatabasePath(SQLiteHelper.DATABASE_NAME);

        Logger.i("DB file: " + dbFile.getPath());

        return dbFile.exists();
    }

    /**
     * Check is exists a network connection.
     *
     * @param context context activity
     * @return Has internet connection or not
     */
    public static boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * Unzip File
     *
     * @param zipFile Filename
     * @param filePath Filepath
     * @return indication that file is unzipped
     */
    public static boolean unZip(String zipFile, String filePath) {
        InputStream is = null;
        ZipInputStream zis = null;
        boolean result = false;
        FileOutputStream fout = null;

        try {

            is = new FileInputStream(zipFile);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            while ((ze = zis.getNextEntry()) != null) {

                if (ze.isDirectory()) {
                    continue;
                }

                final String fullFilePath = filePath + ze.getName();
                File downloadedFile = new File(fullFilePath);
                downloadedFile.createNewFile();

                fout = new FileOutputStream(fullFilePath);

                while ((count = zis.read(buffer)) != -1) {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();
            }

            result = true;

        } catch (IOException e) {
            Logger.e(e);
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    Logger.e(e);
                }
            }
            if (zis != null) {
                try {
                    zis.close();
                } catch (IOException e) {
                    Logger.e(e);
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Logger.e(e);
                }
            }
        }
        return result;
    }

    /**
     * Converts a stream to a string
     *
     * @param is
     * @return
     * @throws Exception
     */
    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        try {
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (Exception e) {
            Logger.e(e);
        } finally {
            reader.close();
        }

        return sb.toString();
    }

    /**
     * Converts a file to a string using convertStreamToString method
     *
     * @param filePath
     * @return
     */
    public static String getStringFromFile(String filePath) {

        FileInputStream fin = null;
        String result = null;

        try {

            File fl = new File(filePath);
            fin = new FileInputStream(fl);
            result = convertStreamToString(fin);

        } catch (Exception e) {
            Logger.e(e);
        } finally {

            try {

                if(fin != null){
                    fin.close();
                }

            } catch (IOException e) {
                Logger.e(e);
            }
        }

        return result;
    }

    /**
     * Check if file exists in filesystem
     *
     * @param filePath
     * @return
     */
    public static boolean fileExists(String filePath){
        return new File(filePath).exists();
    }

    /**
     * Delete file exists from filesystem
     *
     * @param filePath
     * @return
     */
    public static boolean deleteFile(String filePath) {

        File file = new File(filePath);

        return file.exists() && file.delete();
    }

    /**
     * Gets SharedPreferences
     *
     * @param context
     * @return
     */
    public static SharedPreferences getSharedPreferences(Context context){
        String packageName = context.getPackageName();
        return context.getSharedPreferences(packageName, 0);
    }

    /**
     * Creates a zip file
     *
     * @param fileNameToZip
     * @param zipFileName
     * @param InsideZipFileName
     * @return
     */
    public static Boolean zip(String fileNameToZip, String zipFileName,
                              String InsideZipFileName) {
        // input file
        FileInputStream in = null;
        ZipOutputStream out = null;

        try {
            in = new FileInputStream(fileNameToZip);

            // out put file
            out = new ZipOutputStream(new FileOutputStream(zipFileName));
        } catch (FileNotFoundException e1) {
            Logger.e(e1);
        }

        try {
            // name the file inside the zip file
            out.putNextEntry(new ZipEntry(InsideZipFileName));

            // buffer size
            byte[] b = new byte[1024];
            int count;

            while ((count = in.read(b)) > 0) {
                System.out.println();
                out.write(b, 0, count);
            }
            return true;
        } catch (IOException e) {
            Logger.e(e);
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                Logger.e(e);
            }
            try {
                in.close();
            } catch (IOException e) {
                Logger.e(e);
            }
        }
        return false;
    }

    /**
     * Write file
     *
     * @param filePath
     * @param value
     * @param encode
     * @throws Exception
     */
    public static void createFileByString(String filePath, String value, String encode) throws Exception{
        PrintWriter print = null;
        deleteFile(filePath);
        try {
            if (encode != null) {
                print = new PrintWriter(filePath,encode);
            }else{
                print = new PrintWriter(filePath);
            }
            print.print(value);
            print.flush();
        } catch (UnsupportedEncodingException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }finally{
            print.close();
        }

    }

    /**
     * Read a file in the assets folder
     *
     * @param context    Application context
     * @param resourceId Resource to be used
     * @param db         Database object
     * @throws IOException
     */
    public static void runQueriesFromRawFolder(final Context context, final Integer resourceId, final SQLiteDatabase db) throws IOException {
        readFile(context, null, resourceId, db);
    }

    /**
     * Read a file
     *
     * @param context    Application context
     * @param fileName   File to be used
     * @param resourceId Resource to be used
     * @param db         Database object
     * @throws IOException
     */
    private static void readFile(final Context context, final String fileName, final Integer resourceId, final SQLiteDatabase db) throws IOException {

        BufferedReader reader = null;
        try {

            if (resourceId != null) {
                reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(resourceId)));
            } else {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    db.execSQL(line);
                }
            }

        } catch (IOException e) {
            throw new IOException("Could not lock " + fileName, e);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

    }

}
