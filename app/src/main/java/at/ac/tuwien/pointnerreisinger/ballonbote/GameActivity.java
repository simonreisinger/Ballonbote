package at.ac.tuwien.pointnerreisinger.ballonbote;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Contains the Game Canvas
 * @author Simon Reisinger
 */
public class GameActivity extends AppCompatActivity implements SensorEventListener, SoundPool.OnLoadCompleteListener {

    private GameSurfaceView game;
    private ImageButton playButtonStart;
    private TextView playButtonStartLevelText;
    private ImageView packageDeliver1;
    private ImageView packageDeliver2;
    private ImageButton pauseButton;
    private ImageButton controlGameButton;
    private ImageButton muteVolumeButton;
    private ImageButton restartButton;
    private TextView textLevelScore;
    private LinearLayout gameoverMenu;
    private EditText nameTextField;
    private ProgressBar progressBar;
    private ImageView gameoverAnimation;
    private ImageButton restartGameOverButton;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private boolean gyroAvailable = true;

    private SoundPool mySoundPool;

    /**
     * Creates the Class
     * @param savedInstanceState last state of the activity
     * @author Simon Reisinger
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("GameActivity", "onCreate");

        GameLoop.resetValuesLastPlatform();

        // cut out title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // makes the Activity fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        loadButtons();

        game.resetLastObjects();
    }

    /**
     * Loads the buttons
     * @author Simon Reisinger
     */
    private void loadButtons() {
        game = (GameSurfaceView)findViewById(R.id.drawing_area);
        playButtonStart = (ImageButton) findViewById(R.id.playButtonStart);
        playButtonStartLevelText = (TextView) findViewById(R.id.playButtonStartLevelText);
        packageDeliver1 = (ImageView) findViewById(R.id.packageDeliver1);
        packageDeliver2 = (ImageView) findViewById(R.id.packageDeliver2);
        pauseButton = (ImageButton) findViewById(R.id.pauseButton);
        controlGameButton = (ImageButton) findViewById(R.id.controlGameButton);
        muteVolumeButton = (ImageButton) findViewById(R.id.muteVolumeButton);
        restartButton = (ImageButton) findViewById(R.id.restartButton);
        textLevelScore = (TextView) findViewById(R.id.textLevelScore);
        gameoverMenu = (LinearLayout) findViewById(R.id.gameoverMenu);
        nameTextField = (EditText) findViewById(R.id.nameTextField);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        restartGameOverButton = (ImageButton) findViewById(R.id.restartGameOverButton);
        gameoverAnimation = (ImageView) findViewById(R.id.gameoverAnimation);
    }

    /**
     * Loads the services
     * @author Michael Pointner
     */
    private void loadServices() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if ((mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)) == null) {
            gyroAvailable = false;
        }

        if(gyroAvailable) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        }

        mySoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        mySoundPool.load(getApplicationContext(), R.raw.sound, 1);

        mySoundPool.setOnLoadCompleteListener(this);
    }

    /**
     * Unloads the services
     * @author Michael Pointner
     */
    private void unloadServices() {
        if(gyroAvailable) {
            mSensorManager.unregisterListener(this);
        }

        if(GameLoop.getVolume() == GameLoop.VolumeOn) {
            mySoundPool.autoPause();
        }
        mySoundPool.release();
    }

    /**
     * Starts the music
     * @param soundPool SoundPool
     * @param sampleId SampleId
     * @param status Status
     * @author Michael Pointner
     */
    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        mySoundPool.play(sampleId, 1, 1, 1, -1, 1.0f);

        if(GameLoop.getVolume() == GameLoop.VolumeMute) {
            mySoundPool.autoPause();
        }
    }

    /**
     * Activity Start
     * @author Simon Reisinger
     */
    @Override
    protected void onStart() {
        super.onStart();

        Log.e("GameActivity", "onStart");
    }

    /**
     * Activity Resume
     * @author Simon Reisinger
     */
    @Override
    protected void onResume() {
        super.onResume();

        Log.e("GameActivity", "onResume");

        loadServices();

        game.play();
    }

    /**
     * Activity Pause
     * @author Michael Pointner
     */
    @Override
    protected void onPause() {
        super.onPause();

        GameLoop loop = GameSurfaceView.getLoop();
        if(!loop.getLanded()) {
            game.pause();
        }

        game.setRunning(false);

        unloadServices();

        Log.e("GameActivity", "onPause");
    }

    /**
     * Activity Stop
     * @author Simon Reisinger
     */
    @Override
    protected void onStop() {
        super.onStop();

        Log.e("GameActivity", "onStop");
    }

    /**
     * Activity Destroy
     * @author Simon Reisinger
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(GameSurfaceView.getLoop().getLifes() == 0) {
            GameLoop.resetValuesBeginning();
        }

        Log.e("GameActivity", "onDestroy");
    }

    /**
     * Updates the rotation sensor
     * @param event SensorEvent
     * @author Michael Pointner
     */
    public void onSensorChanged(SensorEvent event) {
        game.onSensorChanged(event);
    }

    /**
     * Accuracy of the sensor changed
     * @param sensor Sensor
     * @param i Accuracy
     * @author Michael Pointner
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    /**
     * Saves the score
     * @author Michael Pointner
     */
    private void saveScore() {
        String username = nameTextField.getText().toString();
        if(username.length() <= 0 || username == getString(R.string.nameInputHint)) {
            username = getString(R.string.nameDefault);
        }
        int score = (int)GameSurfaceView.getLoop().getScore();

        ContentValues values = new ContentValues();
        values.put(ScoreContract.ScoreEntry.COLUMN_NAME_USERNAME, username);
        values.put(ScoreContract.ScoreEntry.COLUMN_NAME_SCORE, score);

        getContentResolver().insert(ScoreProvider.CONTENT_URI, values);
    }

    /**
     * Pauses the game
     * @param view View
     * @author Michael Pointner
     */
    public void pause(View view) {
        game.pause();
    }

    /**
     * Plays the game
     * @param view View
     * @author Michael Pointner
     */
    public void play(View view) {
        GameLoop loop = GameSurfaceView.getLoop();
        if(loop.getLifes() > 0) {
            loop.setLanded(true);
        }
        game.startMovement(view.getY());
        String levelText = loop != null ? loop.getLevelText() : "";
        displayPlayButtonStart(false, levelText);
    }

    /**
     * Restarts the game from the last platform
     * @param view View
     * @author Michael Pointner
     */
    public void restartFromLastPlatform(View view) {
        game.restartFromLastPlatform();
    }

    /**
     * Restarts the game from the beginning
     * @param view View
     * @author Michael Pointner
     */
    public void restartFromBeginning(View view) {
        saveScore();
        game.restartFromBeginning();
    }

    /**
     * Continues the game
     * @param view View
     * @author Michael Pointner
     */
    public void continueGame(View view) {
        GameLoop loop = GameSurfaceView.getLoop();
        if(loop != null) {
            loop.setPaused(false);
            loop.setLanded(false);

            Log.e("GameActivity", System.currentTimeMillis() + " continueGame paused:" + loop.getPaused() + " running:" + loop.getRunning() + " landed:" + loop.getLanded());
        }
    }

    /**
     * Changes the volume of the game
     * @param view View
     * @author Michael Pointner
     */
    public void changeVolumeGame(View view) {
        if(GameLoop.getVolume() == GameLoop.VolumeOn) {
            muteVolumeButton.setBackgroundResource(R.drawable.mute);
            GameLoop.setVolume(GameLoop.VolumeMute);
            mySoundPool.autoPause();
        } else {
            muteVolumeButton.setBackgroundResource(R.drawable.volume);
            GameLoop.setVolume(GameLoop.VolumeOn);
            mySoundPool.autoResume();
        }
    }

    /**
     * Initializes the volume button
     * @author Michael Pointner
     */
    public void initVolumeGame() {
        if(GameLoop.getVolume() == GameLoop.VolumeOn) {
            muteVolumeButton.setBackgroundResource(R.drawable.volume);
        } else {
            muteVolumeButton.setBackgroundResource(R.drawable.mute);
        }
    }

    /**
     * Changes the control of the game
     * @param view View
     * @author Michael Pointner
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
     * @author Michael Pointner
     */
    public void initControlGame() {
        if(GameLoop.getControl() == GameLoop.ControlFinger) {
            controlGameButton.setBackgroundResource(R.drawable.controlfinger);
        } else {
            controlGameButton.setBackgroundResource(R.drawable.controltilt);
        }
    }

    /**
     * Displays the play button invokely
     * @param display Display
     * @param levelText Level text
     * @author Michael Pointner
     */
    public void displayPlayButtonStartInvoke(boolean display, String levelText) {
        mHandler.post(new MessageHandler(MessageHandler.PlayButtonStartVisibility, display, levelText));
        Log.e("GameActivity", "displayPlayButtonStart Invoke:" + display);
    }

    /**
     * Displays the pause button invokely
     * @param display Display
     * @author Michael Pointner
     */
    public void displayPauseButtonInvoke(boolean display) {
        mHandler.post(new MessageHandler(MessageHandler.PauseButtonVisibility, display));
    }

    /**
     * Displays the pause menu invokely
     * @param display Display
     * @author Michael Pointner
     */
    public void displayPauseMenuInvoke(boolean display) {
        mHandler.post(new MessageHandler(MessageHandler.PauseMenuVisibility, display));
    }

    /**
     * Displays the restart button invokely
     * @param display Display
     * @author Michael Pointner
     */
    public void displayRestartButtonInvoke(boolean display) {
        mHandler.post(new MessageHandler(MessageHandler.RestartButtonVisibility, display));
    }

    /**
     * Displays the level score text invokely
     * @param text Level score text
     * @author Michael Pointner
     */
    public void setTextLevelScoreInvoke(String text) {
        mHandler.post(new MessageHandler(MessageHandler.TextLevelScore, text));
    }

    /**
     * Displays the game over menu invokely
     * @param display Display
     * @author Michael Pointner
     */
    public void displayGameOverMenuInvoke(boolean display) {
        mHandler.post(new MessageHandler(MessageHandler.GameOverMenuVisibility, display));
    }

    /**
     * Displays the loading icon invokely
     * @param display Display
     * @author Michael Pointner
     */
    public void displayLoadingIconInvoke(boolean display) {
        mHandler.post(new MessageHandler(MessageHandler.LoadingIconVisibility, display));
    }

    /**
     * Displays the package deliver image invokely
     * @param display Display
     * @author Michael Pointner
     */
    public void displayPackageDeliverInvoke(boolean display) {
        mHandler.post(new MessageHandler(MessageHandler.PackageDeliverVisibility, display));
    }

    final Handler mHandler = new Handler();

    /**
     * Message handler for the invoking
     * @author Michael Pointner
     */
    private class MessageHandler implements Runnable {
        public static final int PlayButtonStartVisibility = 0;
        public static final int PauseButtonVisibility = 1;
        public static final int PauseMenuVisibility = 2;
        public static final int RestartButtonVisibility = 3;
        public static final int TextLevelScore = 4;
        public static final int GameOverMenuVisibility = 5;
        public static final int LoadingIconVisibility = 6;
        public static final int PackageDeliverVisibility = 7;
        public int dest;
        public boolean bool;
        public String text;

        /**
         * Constructor for boolean value
         * @param dest Destination
         * @param bool Boolean value
         * @author Michael Pointner
         */
        MessageHandler(int dest, boolean bool) {
            this.dest = dest;
            this.bool = bool;
        }

        /**
         * Constructor for String value
         * @param dest Destination
         * @param text String value
         * @author Michael Pointner
         */
        MessageHandler(int dest, String text) {
            this.dest = dest;
            this.text = text;
        }

        /**
         * Constructor for boolean and String values
         * @param dest Destination
         * @param bool Boolean value
         * @param text String value
         * @author Michael Pointner
         */
        MessageHandler(int dest, boolean bool, String text) {
            this.dest = dest;
            this.bool = bool;
            this.text = text;
        }

        /**
         * Runnable methode for invoke excecution
         * @author Michael Pointner
         */
        public void run() {
            switch (dest) {
                case PlayButtonStartVisibility: displayPlayButtonStart(bool, text); break;
                case PauseButtonVisibility: displayPauseButton(bool); break;
                case PauseMenuVisibility: displayPauseMenu(bool); break;
                case RestartButtonVisibility: displayRestartButton(bool); break;
                case TextLevelScore: setTextLevelScore(text); break;
                case GameOverMenuVisibility: displayGameOverMenu(bool); break;
                case LoadingIconVisibility: displayLoadingIcon(bool); break;
                case PackageDeliverVisibility: displayPackageDeliver(bool); break;
            }
        }
    }

    /**
     * Displays the play button
     * @param display Display
     * @param levelText Level text
     * @author Michael Pointner
     */
    private void displayPlayButtonStart(boolean display, String levelText) {
        playButtonStart.setVisibility(display ? View.VISIBLE : View.GONE);
        playButtonStartLevelText.setText(levelText);
        playButtonStartLevelText.setVisibility(display ? View.VISIBLE : View.GONE);
        Log.e("GameActivity", "displayPlayButtonStart:" + display);
    }

    /**
     * Displays the pause button
     * @param display Display
     * @author Michael Pointner
     */
    private void displayPauseButton(boolean display) {
        pauseButton.setVisibility(display ? View.VISIBLE : View.GONE);
        Log.e("GameActivity", "displayPauseButton:" + display);
    }

    /**
     * Displays the pause menu
     * @param display Display
     * @author Michael Pointner
     */
    private void displayPauseMenu(boolean display) {
        initVolumeGame();
        initControlGame();
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.pauseMenu);
        linearLayout.setVisibility(display ? View.VISIBLE : View.GONE);

        Log.e("GameActivity", "displayPauseMenu:"+display);
    }

    /**
     * Displays the restart button
     * @param display Display
     * @author Michael Pointner
     */
    private void displayRestartButton(boolean display) {
        restartButton.setVisibility(display ? View.VISIBLE : View.GONE);
        Log.e("GameActivity", "displayRestartButton:" + display);
    }

    /**
     * Displays the level score text
     * @param text Level score text
     * @author Michael Pointner
     */
    private void setTextLevelScore(String text) {
        textLevelScore.setText(text);
    }

    /**
     * Displays the game over menu
     * @param display Display
     * @author Michael Pointner
     */
    private void displayGameOverMenu(boolean display) {
        gameoverMenu.setVisibility(display ? View.VISIBLE : View.GONE);
        displayGameOverAnimation(display);
        Log.e("GameActivity", "displayGameOverMenu:" + display);
    }

    /**
     * Displays the game over animations
     * @param display display status
     * @author Michael Pointner
     */
    private void displayGameOverAnimation(boolean display) {
        if(display) {
            Animation scale = AnimationUtils.loadAnimation(getBaseContext(), R.anim.scale);
            Animation rotate = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
            gameoverAnimation.startAnimation(scale);
            restartGameOverButton.startAnimation(rotate);
        } else {
            restartGameOverButton.clearAnimation();
        }
    }

    /**
     * Displays the loading icon
     * @param display Display
     * @author Michael Pointner
     */
    public void displayLoadingIcon(boolean display) {
        progressBar.setVisibility(display ? View.VISIBLE : View.GONE);
    }

    /**
     * Displays the package deliver image
     * @param display Display
     * @author Michael Pointner
     */
    private void displayPackageDeliver(boolean display) {
        packageDeliver1.setVisibility(display ? View.VISIBLE : View.GONE);
        if(display) {
            packageDeliver2.setVisibility(View.GONE);
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    if(packageDeliver1.getVisibility() == View.VISIBLE && packageDeliver2.getVisibility() == View.GONE) {
                        packageDeliver1.setVisibility(View.GONE);
                        packageDeliver2.setVisibility(View.VISIBLE);
                    }
                }
            }, 500);
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    if(packageDeliver2.getVisibility() == View.VISIBLE) {
                        packageDeliver1.setVisibility(View.GONE);
                        packageDeliver2.setVisibility(View.GONE);
                    }
                }
            }, 1000);
        } else {
            packageDeliver2.setVisibility(display ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Increases the game speed manually
     * @param view View
     * @author Michael Pointner
     */
    public void increaseGameSpeedManual(View view) {
        GameLoop.increaseGameSpeedManual();
    }

    /**
     * Increases the lifes manually
     * @param view View
     * @author Michael Pointner
     */
    public void increaseLifes(View view) {
        GameLoop.increaseLifes();
    }
}
