package at.ac.tuwien.pointnerreisinger.ballonbote;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import at.ac.tuwien.pointnerreisinger.ballonbote.ScoreContract.ScoreEntry;

/**
 * Access Point to the database
 *
 * @author Michael Pointner
 */
public class MySQLiteHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ScoreGame.db";

    private final String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS " + ScoreEntry.TABLE_NAME +
            " (" + ScoreEntry._ID + " " + ScoreEntry.COLUMN_TYPE_ID + " autoincrement," +
            ScoreEntry.COLUMN_NAME_USERNAME + " " + ScoreEntry.COLUMN_TYPE_USERNAME + "," +
            ScoreEntry.COLUMN_NAME_SCORE + " " + ScoreEntry.COLUMN_TYPE_SCORE + ");";

    private final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + ScoreEntry.TABLE_NAME;

    /**
     * Initializes the MySQLiteHelper
     *
     * @param context Context
     * @author Michael Pointner
     */
    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Wird beim ersten DB Zugriff aufgerufen
     *
     * @param db SQLiteDatabase
     * @author Michael Pointner
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(MySQLiteHelper.class.getName(), "Creating tables in DB");
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    /**
     * Updates the database
     *
     * @param db         SQLiteDatabase
     * @param oldVersion Old version
     * @param newVersion New version
     * @author Michael Pointner
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(), "Upgrading database from version " +
                oldVersion + " to " + newVersion);
        dropTables(db);
        onCreate(db);
    }

    /**
     * Drops the table
     *
     * @param db SQLiteDatabase
     * @author Michael Pointner
     */
    private void dropTables(SQLiteDatabase db) {
        Log.d(MySQLiteHelper.class.getName(), "Dropping all tables");
        db.execSQL(SQL_DELETE_ENTRIES);
    }
}
