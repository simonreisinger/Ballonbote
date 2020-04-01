package at.ac.tuwien.pointnerreisinger.ballonbote;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Provides the cyclic game logic
 *
 * @author Simon Reisinger
 */
public class GameLoop implements Runnable {
    private final SurfaceHolder holder;
    private GameSurfaceView view;
    private static boolean running = true;
    private long t0 = -1;
    private long t;

    /**
     * Creates the GameLoop
     *
     * @param holder          Holder for the game
     * @param gameSurfaceView SurfaceView for the game
     * @author Simon Reisinger
     */
    public GameLoop(SurfaceHolder holder, GameSurfaceView gameSurfaceView) {
        this.holder = holder;
        this.view = gameSurfaceView;
    }

    public boolean getRunning() {
        return running;
    }

    /**
     * Changes the Game Running Status
     *
     * @param running Running state
     * @author Simon Reisinger
     */
    public void setRunning(boolean running) {
        this.running = running;
        t0 = -1;
    }

    /**
     * Calculates the Time passed since the last painting cycle
     *
     * @author Simon Reisinger
     */
    public float calcTimePerFrame() {
        //Hier ausrechnen, wie lange das
        //letzte Frame zum Zeichnen benötigt hat!
        if (t0 == -1)
            t0 = System.currentTimeMillis(); // Damit die delta t = 0 ist, wenn noch kein t0 vorhanden
        t = System.currentTimeMillis();
        float tpf = (float) (t - t0) / 1000f;
        t0 = t;
        return tpf;
    }

    /**
     * Calls the draw methods cyclic
     *
     * @author Simon Reisinger
     */
    @SuppressLint("WrongCall")
    @Override
    public void run() {
        float tpf = 0;
        Canvas c;
        running = true;
        Log.e("GameLoop", "Thread started");
        while (running) {
            //Frame-Unabhängigkeit implementieren
            tpf = calcTimePerFrame();
            //Log.e("GameLoop", "run - drawing frame, tpf="+tpf);
            //Update Game Logic
            view.update(tpf);
            updateGUI();
            //"Render"
            c = null;
            try {
                c = holder.lockCanvas(null);
                synchronized (holder) {
                    if (c != null) {
                        view.onDraw(c);
                    }
                }
            } finally {
                if (c != null) {
                    holder.unlockCanvasAndPost(c);
                }
            }
        }
        //"Destroy"
    }

    private void updateGUI() {
        updateTextLevelScore();
    }

    private static boolean debugEnabled = false;
    private static boolean collisionEnabled = true;
    private static boolean landed = true;
    private static boolean paused = false;
    private static boolean stopped = false;
    public final static int ControlFinger = 0;
    public final static int ControlTilt = 1;
    private static int control = ControlFinger;
    public final static int VolumeOn = 0;
    public final static int VolumeMute = 1;
    private static int volume = VolumeOn;
    private static float initRotation = Integer.MIN_VALUE;

    private static int lifesStart = 3;
    private static int lifes = lifesStart;

    private static final float gameSpeedStart = 0.1f;//0.5f;//
    private static final float gameSpeedMax = 0.5f;
    private static float gameSpeed = gameSpeedStart; // Percentage of horizontal screen movement per second

    private static final int levelStart = 1;//82;//
    private static int level = levelStart;
    private static int levelLastPlatform = levelStart;

    private static float scoreStart = 0;
    private static float score = scoreStart;
    private static float scoreLastPlatform = scoreStart;
    private static String debugText;
    private static float toleranz = 1.5f;
    private static boolean levelIncreased = true;

    /**
     * Updates the text of the statistics
     *
     * @author Michael Pointner
     */
    private void updateTextLevelScore() {
        view.activity.setTextLevelScoreInvoke(" Ballons: " + lifes + " Level: " + level + " Speed: " + Math.round(gameSpeed / gameSpeedStart * 10f) / 10f + "x Score: " + ((int) score) + (debugEnabled && debugText != "" ? " Debug: " + debugText : ""));
    }

    /**
     * Sets the Debug text
     *
     * @param text Debug text
     * @author Michael Pointner
     */
    public void setDebugText(String text) {
        debugText = text;
    }

    /**
     * Resets the level increased flag
     *
     * @author Michael Pointner
     */
    public void resetLevelIncreased() {
        levelIncreased = true;
    }

    /**
     * Resets the values to the beginning
     *
     * @author Michael Pointner
     */
    public static void resetValuesBeginning() {
        resetGameSpeed();
        resetLevel();
        resetLifes();
        resetScore();
    }

    /**
     * Resets the values the the values of the last platform
     *
     * @author Michael Pointner
     */
    public static void resetValuesLastPlatform() {
        level = levelLastPlatform;
        gameSpeed = sumIncreaseFunktionGameSpeed(level);
        gameSpeed = gameSpeed > gameSpeedMax ? gameSpeedMax : gameSpeed;
        score = scoreLastPlatform;
    }

    /**
     * Returns the toleranz for the ballon width
     *
     * @return toleranz
     * @author Michael Pointner
     */
    public static float getToleranz() {
        return toleranz;
    }

    /**
     * Resets the Gamespeed to the start value
     *
     * @author Michael Pointner
     */
    public static void resetGameSpeed() {
        gameSpeed = gameSpeedStart;
    }

    /**
     * Increases the Gamespeed
     *
     * @author Michael Pointner
     */
    public void increaseGameSpeed() {
        gameSpeed += GameLoop.increaseFunktion(getLevel());
        gameSpeed = gameSpeed > gameSpeedMax ? gameSpeedMax : gameSpeed;
    }

    /**
     * Calculates the increasement on the gamespeed for the given level
     *
     * @param level actual Level
     * @return increase value
     * @author Michael Pointner
     */
    public static float increaseFunktion(int level) {
        return 0.01f * (float) Math.pow(Math.E, -level / 50f);
    }

    /**
     * Sums up the gamespeed for the given level
     *
     * @param level Level
     * @return Gamespeed
     * @author Michael Pointner
     */
    public static float sumIncreaseFunktionGameSpeed(int level) {
        float sum = gameSpeedStart;
        for (int x = 1; x < level; x++) {
            sum += increaseFunktion(x);
        }
        return sum;
    }

    /**
     * Returns the start gamespeed
     *
     * @return start gamespeed
     * @author Michael Pointner
     */
    public static float getGameSpeedStart() {
        return gameSpeedStart;
    }

    /**
     * Increses the gamespeed manual
     *
     * @author Michael Pointner
     */
    public static void increaseGameSpeedManual() {
        gameSpeed += 0.05f;
        gameSpeed = gameSpeed > gameSpeedMax ? gameSpeedMax : gameSpeed;
    }

    /**
     * Returns the current level
     *
     * @return current level
     * @author Michael Pointner
     */
    public static int getLevel() {
        return level;
    }

    /**
     * Sets the the level of the last platform
     *
     * @param level Level of the last platform
     * @author Michael Pointner
     */
    public void setLevelLastPlatform(int level) {
        levelLastPlatform = level;
    }

    /**
     * Increases the level
     *
     * @param isPlatform defines if the caller is a platform
     * @author Michael Pointner
     */
    public void increaseLevel(boolean isPlatform) {
        if (!levelIncreased) {
            level++;
            increaseGameSpeed();
        }
        levelIncreased = isPlatform ? true : false;
    }

    /**
     * Returns if the level is already incresed
     *
     * @return level increased
     * @author Michael Pointner
     */
    public boolean getLevelIncreased() {
        return levelIncreased;
    }

    /**
     * Resets the level
     *
     * @author Michael Pointner
     */
    public static void resetLevel() {
        level = levelStart;
        levelLastPlatform = levelStart;
    }

    /**
     * Resets the lifes
     *
     * @author Michael Pointner
     */
    public static void resetLifes() {
        lifes = lifesStart;
    }

    /**
     * Increases the lifes by one
     *
     * @author Michael Pointner
     */
    public static void increaseLifes() {
        lifes++;
    }

    /**
     * Resets the score
     *
     * @author Michael Pointner
     */
    public static void resetScore() {
        score = scoreStart;
        scoreLastPlatform = scoreStart;
    }

    /**
     * Returns the amount of lifes
     *
     * @return current lifes
     * @author Michael Pointner
     */
    public int getLifes() {
        return lifes;
    }

    /**
     * Decreases the lifes
     *
     * @author Michael Pointner
     */
    private void decreaseLifes() {
        lifes = lifes > 0 ? (lifes - 1) : 0;
    }

    /**
     * Returns the Score
     *
     * @return score
     * @author Michael Pointner
     */
    public float getScore() {
        return score;
    }

    /**
     * Sets the score
     *
     * @param score Score
     * @author Michael Pointner
     */
    public void setScore(float score) {
        this.score = this.scoreLastPlatform = score;
    }

    /**
     * Increases the score by the given value
     *
     * @param incresement Increasement of the score
     * @author Michael Pointner
     */
    public void increaseScore(float incresement) {
        this.score += incresement;
    }

    /**
     * Resets the score to the value of the last platform
     *
     * @author Michael Pointner
     */
    public void setScoreLastPlatform() {
        scoreLastPlatform = score;
    }

    /**
     * Returns the score of the last platform
     *
     * @return Score of the last platform
     * @author Michael Pointner
     */
    public float getScoreLastPlatform() {
        return scoreLastPlatform;
    }

    /**
     * Returns the game landed status
     *
     * @return Landed Landed status
     * @author Michael Pointner
     */
    public boolean getLanded() {
        return landed;
    }

    /**
     * Returns the game paused status
     *
     * @return Paused status
     * @author Michael Pointner
     */
    public boolean getPaused() {
        return paused;
    }

    /**
     * Returns the game stopped status
     *
     * @return Stopped status
     * @author Michael Pointner
     */
    public boolean getStopped() {
        return stopped;
    }

    /**
     * Returns if the world is running
     *
     * @return World running status
     * @author Michael Pointner
     */
    public boolean getRunningWorld() {
        return !landed && !paused && !stopped;
    }

    /**
     * Returns the current control mode
     *
     * @return Control mode
     * @author Michael Pointner
     */
    public static int getControl() {
        return control;
    }

    /**
     * Returns the current volume mode
     *
     * @return Volume mode
     * @author Michael Pointner
     */
    public static int getVolume() {
        return volume;
    }

    /**
     * Returns the initial rotation of the device
     *
     * @return initial rotation of the device
     * @author Michael Pointner
     */
    public float getInitRotation() {
        return initRotation;
    }

    /**
     * Sets the initial rotation of the device
     *
     * @param rotation Initial rotation of the device
     * @author Michael Pointner
     */
    public void setInitRotation(float rotation) {
        initRotation = rotation;
    }

    /**
     * Sets the paused status
     *
     * @param paused Paused status
     * @author Michael Pointner
     */
    public void setPaused(boolean paused) {
        this.paused = paused;
        view.activity.displayPauseButtonInvoke(!paused);
        view.activity.displayPauseMenuInvoke(paused);
    }

    /**
     * Sets the landed status
     *
     * @param landed Landed status
     * @author Michael Pointner
     */
    public void setLanded(boolean landed) {
        this.landed = landed;
        if (landed) {
            setStopped(false);
        }
        view.activity.displayPlayButtonStartInvoke(landed, getLevelText());
        view.activity.displayPauseButtonInvoke(!landed);
        if (!landed) {
            displayPackageDeliver(false);
        }
    }

    /**
     * Displays the package deliver window
     *
     * @param display Display status
     * @author Michael Pointner
     */
    public void displayPackageDeliver(boolean display) {
        view.activity.displayPackageDeliverInvoke(display);
    }

    /**
     * Returns the level text
     *
     * @return Level text
     * @author Michael Pointner
     */
    public String getLevelText() {
        return "Level " + getLevel();
    }

    /**
     * Sets the stopped status
     *
     * @param stopped Stopped status
     * @author Michael Pointner
     */
    public void setStopped(boolean stopped) {
        if (stopped && landed) return;
        if (stopped == GameLoop.stopped) return;
        if (stopped) {
            view.activity.displayPlayButtonStartInvoke(false, getLevelText());
            view.activity.displayPauseButtonInvoke(false);
            if (!this.stopped) {
                decreaseLifes();
            }
            if (paused) {
                setPaused(false);
            }
        }
        if (getLifes() > 0) {
            view.activity.displayRestartButtonInvoke(stopped);
        } else {
            view.activity.displayGameOverMenuInvoke(stopped);
        }
        this.stopped = stopped;
    }

    /**
     * Sets the game control mode
     *
     * @param control Game control mode
     * @author Michael Pointner
     */
    public static void setControl(int control) {
        GameLoop.control = control;
    }

    /**
     * Sets the game volume mode
     *
     * @param volume Game volume mode
     * @author Michael Pointner
     */
    public static void setVolume(int volume) {
        GameLoop.volume = volume;
    }

    /**
     * Returns the game speed
     *
     * @return game speed
     * @author Michael Pointner
     */
    public float getGameSpeed() {
        Actor actor = GameSurfaceView.getActor();
        float heightFactor = actor != null ? (1f - actor.getY() * actor.getY()) : 1f;
        return gameSpeed * heightFactor;
    }

    /**
     * Returns if debug is enabled
     *
     * @return debug enabled
     * @author Michael Pointner
     */
    public boolean getDebug() {
        return debugEnabled;
    }

    /**
     * Returns if collision detection is enabled
     *
     * @return collision detection enabled
     * @author Michael Pointner
     */
    public boolean getCollision() {
        return collisionEnabled;
    }
}
