package at.ac.tuwien.pointnerreisinger.ballonbote;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Displays the Highscores to the user
 *
 * @author Michael Pointner
 */
public class HighscoreActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter cursorAdapter;
    private ListView scoreListView;
    private View progressView;

    private static final int DELETE_ID = Menu.FIRST + 1;

    /**
     * Creates the Class
     *
     * @param savedInstanceState last state of the activity
     * @author Michael Pointner
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // cut out title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // makes the Activity fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        scoreListView = (ListView) findViewById(R.id.scoreListView);
        progressView = findViewById(R.id.progressView);

        fillData();
        registerForContextMenu(scoreListView);
    }

    /**
     * Resumes the activity
     *
     * @author Michael Pointner
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Files the List View with data
     *
     * @author Michael Pointner
     */
    private void fillData() {

        showProgress(true);

        // Fields from the database (projection)
        // Must include the _id column for the adapter to work
        String[] from = new String[]{ScoreContract.ScoreEntry.COLUMN_NAME_USERNAME, ScoreContract.ScoreEntry.COLUMN_NAME_SCORE};
        // Fields on the UI to which we map
        int[] to = new int[]{R.id.textViewName, R.id.textViewScore};

        //start a new loader or re-connect to existing one
        getLoaderManager().initLoader(0, null, this);
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.list_item_score, null, from, to, 0);

        scoreListView.setAdapter(cursorAdapter);
    }

    /**
     * Displays the progress
     *
     * @param progress Prograss flag
     * @author Michael Pointner
     */
    private void showProgress(boolean progress) {
        scoreListView.setVisibility(progress ? View.GONE : View.VISIBLE);
        progressView.setVisibility(progress ? View.VISIBLE : View.GONE);
    }

    /**
     * Creates the context menu
     *
     * @param menu     Menu
     * @param v        View
     * @param menuInfo ContextMenuInfo
     * @author Michael Pointner
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.Delete);
    }

    /**
     * Sets the selected context item
     *
     * @param item MenuItem
     * @return Selected
     * @author Michael Pointner
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                        .getMenuInfo();
                Uri uri = Uri.parse(ScoreProvider.CONTENT_URI + "/"
                        + info.id);
                getContentResolver().delete(uri, null, null);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Sets the option item as selected
     *
     * @param item MenuItem
     * @return Selected
     * @author Michael Pointner
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates a loader
     *
     * @param id   Id
     * @param args Bundle arguments
     * @return Loader
     * @author Michael Pointner
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {ScoreContract.ScoreEntry._ID, ScoreContract.ScoreEntry.COLUMN_NAME_USERNAME, ScoreContract.ScoreEntry.COLUMN_NAME_SCORE};
        CursorLoader cursorLoader = new CursorLoader(this,
                ScoreProvider.CONTENT_URI, projection, null, null, ScoreContract.ScoreEntry.COLUMN_NAME_SCORE + " DESC");
        return cursorLoader;
    }

    /**
     * Load finished
     *
     * @param loader Loader
     * @param data   Cursor
     * @author Michael Pointner
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);//notifies the SimpleCursorAdapter about new data
        showProgress(false);
    }

    /**
     * Resets the Loader
     *
     * @param loader Loader
     * @author Michael Pointner
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // data is not available anymore, delete reference
        cursorAdapter.swapCursor(null);
    }
}
