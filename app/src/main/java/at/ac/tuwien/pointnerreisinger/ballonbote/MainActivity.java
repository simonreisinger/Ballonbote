package at.ac.tuwien.pointnerreisinger.ballonbote;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

/**
 * Provides a Main Menu to the user
 * @author Simon Reisinger
 */
public class MainActivity extends AppCompatActivity {

    private ImageButton controlGameButton;
    private ImageButton muteVolumeButton;

    /**
     * Creates the class
     * @author Simon Reisinger
     * @param savedInstanceState last state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // cut out title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // makes the Activity fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        controlGameButton = (ImageButton) findViewById(R.id.controlGameButton);
        muteVolumeButton = (ImageButton) findViewById(R.id.muteVolumeButton);

        //openGame(null);
    }

    /**
     * Opens the game
     * @param view View
     * @author Simon Reisinger
     */
    public void openGame(View view) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.pauseMenu);
        if(linearLayout.getVisibility() == View.GONE) {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Opens the settings
     * @param view View
     * @author Simon Reisinger
     */
    public void openSettings(View view) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.pauseMenu);
        if(linearLayout.getVisibility() == View.GONE) {
            initVolumeGame();
            initControlGame();
            linearLayout.setVisibility(View.VISIBLE);
            ((ImageButton) findViewById(R.id.playButtonContinue)).setBackgroundResource(R.drawable.restart);
        }
    }

    /**
     * Opens the highscore
     * @param view View
     * @author Simon Reisinger
     */
    public void openHighscore(View view) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.pauseMenu);
        if(linearLayout.getVisibility() == View.GONE) {
            Intent intent = new Intent(MainActivity.this, HighscoreActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Continues the game
     * @param view View
     * @author Simon Reisinger
     */
    public void continueGame(View view) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.pauseMenu);
        linearLayout.setVisibility(View.GONE);
    }

    /**
     * Changes the volume status of the game
     * @param view View
     * @author Simon Reisinger
     */
    public void changeVolumeGame(View view) {
        if(GameLoop.getVolume() == GameLoop.VolumeOn) {
            muteVolumeButton.setBackgroundResource(R.drawable.mute);
            GameLoop.setVolume(GameLoop.VolumeMute);
        } else {
            muteVolumeButton.setBackgroundResource(R.drawable.volume);
            GameLoop.setVolume(GameLoop.VolumeOn);
        }
    }

    /**
     * Initializes the volume button
     * @author Simon Reisinger
     */
    public void initVolumeGame() {
        if(GameLoop.getVolume() == GameLoop.VolumeOn) {
            muteVolumeButton.setBackgroundResource(R.drawable.volume);
        } else {
            muteVolumeButton.setBackgroundResource(R.drawable.mute);
        }
    }

    /**
     * Changes the control status of the game
     * @param view View
     * @author Simon Reisinger
     */
    public void changeControlGame(View view) {
        if(GameLoop.getControl() == GameLoop.ControlFinger) {
            controlGameButton.setBackgroundResource(R.drawable.controltilt);
            GameLoop.setControl(GameLoop.ControlTilt);
        } else {
            controlGameButton.setBackgroundResource(R.drawable.controlfinger);
            GameLoop.setControl(GameLoop.ControlFinger);
        }
    }

    /**
     * Initializes the control button
     * @author Simon Reisinger
     */
    public void initControlGame() {
        if(GameLoop.getControl() == GameLoop.ControlFinger) {
            controlGameButton.setBackgroundResource(R.drawable.controlfinger);
        } else {
            controlGameButton.setBackgroundResource(R.drawable.controltilt);
        }
    }

}
