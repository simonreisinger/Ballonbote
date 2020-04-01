package at.ac.tuwien.pointnerreisinger.ballonbote;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;


/**
 * Implemented for lecture Multimedia UE.
 * References:
 * http://www.vogella.com/tutorials/AndroidSQLite/article.html#tutorialusecp_overview
 */
public class ScoreProvider extends ContentProvider {

    private MySQLiteHelper mySQLiteHelper;

    // used for the UriMacher
    private static final int SCORE = 10;
    private static final int SCORE_ID = 20;

    private static final String AUTHORITY = "at.ac.tuwien.pointnerreisinger.ballonbote";

    private static final String BASE_PATH = ScoreContract.ScoreEntry.TABLE_NAME;
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, SCORE); //Matches a content URI for all rows in table
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", SCORE_ID); //Matches a content URI for single rows in table
    }


    /**
     * Initialize your provider. The Android system calls this method immediately after it creates your provider.
     * Notice that your provider is not created until a ContentResolver object tries to access it.
     *
     * @author Michael Pointner
     */
    @Override
    public boolean onCreate() {
        mySQLiteHelper = new MySQLiteHelper(getContext());
        return true;
    }

    /**
     * Retrieve data from your provider.
     * Use the arguments to select the table to query, the rows and columns to return, and the sort order of the result.
     * Return the data as a Cursor object.
     *
     * @param projection    Projection
     * @param selection     Selection
     * @param selectionArgs SelectionArgs
     * @param sortOrder     SortOrder
     * @param uri           Uri
     * @author Michael Pointner
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // SQLiteQueryBuilder is a helper class that creates the proper SQL syntax for us.
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // check if the caller has requested a column which does not exists
        checkColumns(projection);

        // Set the table
        queryBuilder.setTables(ScoreContract.ScoreEntry.TABLE_NAME);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            // If the incoming URI was for all rows in score table
            case SCORE:
                break;
            // If the incoming URI was for a single row
            case SCORE_ID:
                // adding the ID to the original query
                queryBuilder.appendWhere(ScoreContract.ScoreEntry._ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    /**
     * Returns the type
     *
     * @param uri Uri
     * @return Type
     * @author Michael Pointner
     */
    @Override
    public String getType(Uri uri) {

        //Log.d(ScoreProvider.class.getName(), "ScoreProvider getType");
        return null;
    }

    /**
     * Insert a new row into your provider. Use the arguments to select the destination
     * table and to get the column values to use.
     * Return a content URI for the newly-inserted row.
     *
     * @param uri    Uri
     * @param values Values
     * @author Michael Pointner
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mySQLiteHelper.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case SCORE:
                id = sqlDB.insert(ScoreContract.ScoreEntry.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    /*
     * Delete rows from your provider. Use the arguments to select the table and the rows to delete. Return the number of rows deleted.
     * @param uri Uri
     * @param selection Selection
     * @param SelectionArgs
     * @author Michael Pointner
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mySQLiteHelper.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case SCORE:
                rowsDeleted = sqlDB.delete(ScoreContract.ScoreEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case SCORE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(ScoreContract.ScoreEntry.TABLE_NAME,
                            ScoreContract.ScoreEntry._ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(ScoreContract.ScoreEntry.TABLE_NAME,
                            ScoreContract.ScoreEntry._ID
                                    + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    /**
     * Update
     *
     * @param uri           Uri
     * @param values        Values
     * @param selection     Selection
     * @param selectionArgs Selection arguments
     * @return Identifier
     * @author Michael Pointner
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Check columns
     *
     * @param projection Projection
     * @author Michael Pointner
     */
    private void checkColumns(String[] projection) {
        String[] available = {ScoreContract.ScoreEntry._ID,
                ScoreContract.ScoreEntry.COLUMN_NAME_USERNAME,
                ScoreContract.ScoreEntry.COLUMN_NAME_SCORE};
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }

}
