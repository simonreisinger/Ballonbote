package at.ac.tuwien.pointnerreisinger.ballonbote;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.SensorEvent;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

/**
 * Provides the canvas for the game
 * @author Michael Pointner
 */
public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback{

    private Context context;
    private static GameLoop loop;
    public GameActivity activity;

    public static GameLoop getLoop(){
        return loop;
    }

    private static Thread loopThread;
    private float pressStartY = 0;
    private static Background background;
    private static Ground ground;
    private static Actor actor;
    private LinkedList<MoveableObject> objects = new LinkedList<>();
    private boolean objectsLocked = false;
    private boolean initialized = false;

    private boolean prepered = false;

    private static Bitmap loadBackgroundImage = null;

    /**
     * Creates the GameSurfaceView
     * @author Michael Pointner
     * @param context Context to draw on
     * @param attrSet Some attributs
     */
    public GameSurfaceView(Context context, AttributeSet attrSet) {
        super(context, attrSet);

        Log.e("GameSurfaceView", "Konstruktor");
        getHolder().addCallback(this);
        setFocusable(true);
        this.context = context;
        activity = ((GameActivity)context);
        if(loadBackgroundImage == null) {
            loadBackgroundImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.background2);
        }
    }

    /**
     * Starts the game
     * @author Michael Pointner
     */
    public void play() {
        if(loop != null) {
            loop.resetLevelIncreased();
            Log.e("GameSurfaceView", "play - Paused:"+loop.getPaused()+" Landed:"+loop.getLanded()+" Stopped:"+loop.getStopped());
            if(loop.getPaused() && loop.getLanded()) {
                Log.e("GameSurfaceView", "loop.setPaused(false)");
                loop.setPaused(false);
            }
            if(loop.getStopped()) {
                init();
                loop.setStopped(false);
                Log.e("GameSurfaceView", "loop.setStopped(false)");
            }
            if(loop.getLifes() == 0) {
                loop.setStopped(true);
            }
            Log.e("GameSurfaceView", "play: initialized:"+initialized+" loopThread:"+loopThread+" !loop.getRunning()"+!loop.getRunning());
            if(initialized && (loopThread == null ||!loop.getRunning())) {
                loopThread = new Thread(loop);
                loopThread.start();
                Log.e("GameSurfaceView", "loopThread.start()");
            }
        }
    }

    /**
     * Sets the game status to paused
     * @author Michael Pointner
     */
    public void pause() {
        loop.setPaused(true);
    }

    /**
     * Changes the Game Running Status
     * @param running Running state
     * @author Michael Pointner
     */
    public void setRunning(boolean running) {
        if(loop != null) loop.setRunning(running);
    }

    /**
     * Starts the Thread
     * @param holder Holder of the game canvas
     * @author Michael Pointner
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        Log.e("GameSurfaceView", "surfaceCreated");
        loop = new GameLoop(holder, this);

        if(!initialized) {
            prepare();
            Log.e("GameSurfaceView", "preper");
        }

        initialized = true;
        play();
        Log.e("GameSurfaceView", "surfaceCreated play");
    }

    /**
     * Changes the game size
     * @param holder Holder of the game canvas
     * @param format new format
     * @param width new width
     * @param height new height
     * @author Michael Pointner
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    /**
     * Terminates the Game when the SurfaceView is destroyed
     * @param holder Surface holder
     * @author Michael Pointner
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        endGame();
    }


    /**
     * Terminates the game
     * @author Michael Pointner
     */
    private void endGame() {
        loop.setRunning(false);
        try
        {
            if(loopThread != null) {
                loopThread.join();
            }
        }
        catch (InterruptedException e)
        {
            Log.e("Error", e.getMessage());
        }
    }

    /**
     * Restarts the game from the last platform
     * @author Michael Pointner
     */
    public void restartFromLastPlatform() {
        resetValues();
        init();

        // TODO: Not From Beginning
        loop.setScore(loop.getScoreLastPlatform());
        loop.setLanded(true);
    }

    /**
     * Resets the game from the beginning
     * @author Michael Pointner
     */
    public void restartFromBeginning() {
        loop.resetLevel();
        resetValues();
        init();
        loop.setScore(0);
        loop.setLanded(true);
        loop.setStopped(false);
        loop.resetLifes();
        loop.resetGameSpeed();
        if(loopThread == null) {
            loopThread = new Thread(loop);
            loopThread.start();
        }
    }

    /**
     * Updates the game to the new sensor value
     * @param event Sensor event
     * @author Michael Pointner
     */
    public void onSensorChanged(SensorEvent event) {
        if(loop != null && loop.getControl() == loop.ControlTilt) {
            float rotationY = event.values[2];
            if(loop.getRunningWorld()) {

                if (loop.getInitRotation() == Integer.MIN_VALUE) {
                    loop.setInitRotation(rotationY);
                }

                float actualRotation = rotationY - loop.getInitRotation();

                actor.setGradient(actualRotation / 1000f);
            } else {
                if(loop.getLanded()) {
                    loop.setInitRotation(rotationY+2);
                } else {
                    loop.setInitRotation(rotationY);
                }
            }
        }
    }

    /**
     * Executes actions for user touch inputs
     * @param e Motion Event
     * @return Action Performed
     * @author Michael Pointner
     */
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent e) {
        if(!loop.getPaused() && !loop.getLanded()) {// && loop.getControl() == loop.ControlFinger) {
            if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_BUTTON_PRESS) {
                startMovement(e.getY());
            }
            if (e.getAction() == MotionEvent.ACTION_MOVE) {
                setMovement(e.getY());
            }
            if (e.getAction() == MotionEvent.ACTION_UP) {
                endMovement(e.getY());
            }
        }
        return true;
    }

    /**
     * Starts the movement of the finger on the screen
     * @param y Y position of the finger on the screen
     * @author Michael Pointner
     */
    public void startMovement(float y) {
        pressStartY = y;
        actor.setUserinputGradient(0);
    }

    /**
     * Updates the finger movement
     * @param y Y coordinate of the finger
     * @author Michael Pointner
     */
    public void setMovement(float y) {
        actor.setUserinputGradient((y - pressStartY) / (float) getScreenSize().y);

        pressStartY = y;
    }

    /**
     * Ends the movement of the finger on the screen
     * @param y Y coordinate of the finger
     * @author Michael Pointner
     */
    public void endMovement(float y) {
        actor.setUserinputGradient(0);
    }

    /**
     * Updates the positions of the objects
     * @param tpf Time per frame
     * @author Michael Pointner
     */
    public void update(float tpf) {
        //Log.e("GameSurfaceView", "update - prepared:" + prepered);
        if(prepered) {
            background.update(tpf);
            ground.update(tpf);

            // update Obstacles
            if(!objectsLocked) {
                Iterator<MoveableObject> iterator = objects.iterator();
                while (iterator.hasNext()) {
                    MoveableObject obj = iterator.next();
                    obj.update(tpf);
                }
            }
            actor.update(tpf);

            // remove Obstacle which are out of screen
            removeObjects();

            createObjects();
        } else {
            getLoop().setDebugText(System.currentTimeMillis() + " Not Prepared Update");
        }
    }

    /**
     * Draw the whole scene
     * @param canvas Canvas to draw on
     * @author Michael Pointner
     */
    @Override
    public void onDraw(Canvas canvas){
        //Log.e("GameSurfaceView", "onDraw - prepared:" + prepered);
        if(prepered) {
            // draw Background
            background.onDraw(canvas);
            // draw Ground
            ground.onDraw(canvas);

            // draw Obstacles
            if (!objectsLocked) {
                Iterator<MoveableObject> iterator = objects.iterator();
                while (iterator.hasNext()) {
                    MoveableObject obj = iterator.next();
                    obj.onDraw(canvas);
                }
            }

            // draw Actor
            actor.onDraw(canvas);
        } else {
            canvas.drawBitmap(loadBackgroundImage, new Rect(0, 0, loadBackgroundImage.getWidth(), loadBackgroundImage.getHeight()), new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), null);
        }
    }

    /**
     * Starts the prepare worker
     * @author Michael Pointner
     */
    private void prepare() {
        PrepareWorker prepareWorker = new PrepareWorker();
        Thread prepareThread = new Thread(prepareWorker);
        prepareThread.start();
    }

    /**
     * Prepare worker for the initial loading work
     * @author Michael Pointner
     */
    private class PrepareWorker implements Runnable {
        /**
         * Runnable method for the prepare work
         * @author Michael Pointner
         */
        @Override
        public void run() {
            prepareWork();
        }
    }

    /**
     * Initial prepare work
     * @author Michael Pointner
     */
    private void prepareWork() {
        loadImages();
        init();
        loadBoundingBoxes();
        activity.displayLoadingIconInvoke(false);
        if(loop.getLifes() > 0) {
            activity.displayPlayButtonStartInvoke(true, loop.getLevelText());
        }
        prepered = true;
    }

    /**
     * Resets the values of the game
     * @author Michael Pointner
     */
    private void resetValues() {
        Bird.resetValues();
        Cloud.resetValues();
        Platform.resetValues();
        Mountain.resetValues();
        House.resetValues();
        loop.resetLevelIncreased();
    }

    /**
     * Loads the images of the game
     * @author Michael Pointner
     */
    private void loadImages() {
        Background.loadImage(this);
        Ground.loadImage(this);
        Actor.loadImage(this);
        Bird.loadImage(this);
        Cloud.loadImage(this);
        Platform.loadImage(this);
        Mountain.loadImage(this);
        House.loadImage(this);
    }

    /**
     * Loads the bounding boxes of the game
     * @author Michael Pointner
     */
    private void loadBoundingBoxes() {
        Background.loadBoundingBox();
        Actor.loadBoundingBox();
        Bird.loadBoundingBox();
        Cloud.loadBoundingBox();
        Platform.loadBoundingBox();
        Mountain.loadBoundingBox();
        House.loadBoundingBox();
    }

    /**
     * Initialisizes all objects that are just existing once
     * @author Michael Pointner
     */
    private void init() {
        background = new Background();
        ground = new Ground();
        actor = new Actor();

        // delete all Objects
        if(objects.size() > 0) {
            for (MoveableObject obj : objects) {
                obj.setCreationX(Float.NEGATIVE_INFINITY);
            }
        }

        last = null;
        last2 = null;
        last3 = null;
    }

    private MoveableObject last = null;
    private MoveableObject last2 = null;
    private MoveableObject last3 = null;

    /**
     * Creates new objects for the game
     * @author Michael Pointner
     */
    private void createObjects() {

        String objString = "";
        for(MoveableObject obj : objects) {
            objString += obj + " ";
        }

        getLoop().setDebugText(System.currentTimeMillis()+" Objects size:" + objects.size() + " Objects: " + objString);

        if(last == null) {
            initCreateObjects();
            manualGenerationCounter = 0;
        }


        if (last.getCreationX() < 3f) {
            createObject();
        }
    }

    /**
     * Creates the initial objects
     * @author Michael Pointner
     */
    private void initCreateObjects() {
        last = null;
        last2 = null;
        last3 = null;
        //Startplatform
        newPlatform(0f);
    }

    private int generieren = 0;
    private int randomModulo = 4;

    private Random random = null;

    private boolean randomGeneration = true;

    private final int CLOUD = 0;
    private final int BIRD = 1;
    private final int MOUNTAIN = 2;
    private final int PLATFORM = 3;

    private int manualGenerationCounter = 0;

    private int manualSelection = 0;

    private final int manualLast2 = manualSelection %4;//BIRD;
    private final int manualLast = (manualSelection / 4)%4;//CLOUD;
    private final int manualNow = (manualSelection / 16)%4;//MOUNTAIN;
    private final int manualSpace = PLATFORM;

    private int[] manualArray = new int[]{manualLast2, manualLast, manualNow, manualSpace};

    /**
     * Creates a new object (platform or obstacle) to display on the screen
     * @author Michael Pointner
     */
    private void createObject() {

        if(randomGeneration) {
            if (random == null) {
                random = new Random();
            }

            int oldGenerieren = generieren;
            do {
                generieren = random.nextInt(randomModulo);

            } while(oldGenerieren == generieren); // TODO Sollen 2 Gleiche Objekte hintereinander kommen?
        } else {
            generieren = manualArray[manualGenerationCounter % manualArray.length];
        }

        if(generieren == CLOUD) {

            float distanceLast          = Cloud.getMinDistance(last, last2, last3);
            float distanceSame          = Cloud.getMinDistanceSameRel();

            Log.e("cloud distance", distanceLast+"      "+distanceSame);

            newCloud(Math.max(distanceLast, distanceSame));
        }
        if(generieren == BIRD) {

            float distanceLast          = Bird.getMinDistance(last, last2, last3);
            float distanceSame          = Bird.getMinDistanceSameRel();

            Log.e("bird distance", distanceLast+"      "+distanceSame);

            newBird(Math.max(distanceLast, distanceSame));
        }
        if(generieren == MOUNTAIN) {

            float distanceLast          = Mountain.getMinDistance(last, last2, last3);
            float distanceSame          = Mountain.getMinDistanceSameRel();

            Log.e("mountain distance", distanceLast+"      "+distanceSame);

            newMountain(Math.max(distanceLast, distanceSame));
        }
        if(generieren == PLATFORM) {

            float distanceLast          = Platform.getMinDistance(last, last2, last3);
            float distanceSame          = Platform.getMinDistanceSameRel();

            if(distanceLast < distanceSame) return;

            Log.e("platform distance", distanceLast+"      "+distanceSame);

            newPlatform(distanceLast);
        }

        manualGenerationCounter++;
    }

    /**
     * Creates a new platform
     * @param x X coordinate
     * @author Michael Pointner
     */
    private void newPlatform(float x) {
        Platform platform = new Platform(x);
        Platform.setLastSameObject(platform);
        addObject(platform);

        House house = new House(x+ 0.2f);
        addObject(house);
    }

    /**
     * Creates a new mountain
     * @param x X coordinate
     * @author Michael Pointner
     */
    private void newMountain(float x) {
        Mountain mountain = new Mountain(x);
        Mountain.setLastSameObject(mountain);
        addObject(mountain);
    }

    /**
     * Creates a new bird
     * @param x X coordinate
     * @author Michael Pointner
     */
    private void newBird(float x) {
        Bird bird = new Bird(x);
        Bird.setLastSameObject(bird);
        addObject(bird);
    }

    /**
     * Creates a new cloud
     * @param x X coordinate
     * @author Michael Pointner
     */
    private void newCloud(float x) {
        Cloud cloud = new Cloud(x);
        Cloud.setLastSameObject(cloud);
        addObject(cloud);
    }

    /**
     * Adds a new object
     * @param obj Object
     * @author Michael Pointner
     */
	private void addObject(MoveableObject obj) {
		last3 = last2;
		last2 = last;
		last = obj;
		objects.add(obj);
	}

    /**
     * Old method to create objects by loading them from a level file
     * @deprecated This is the old method to create objects
     * @author Michael Pointner
     */
    private void createOldObjects() {
        try {

            AssetManager am = context.getAssets();
            InputStream instream = getResources().openRawResource(R.raw.levels);
            InputStreamReader inputreader = new InputStreamReader(instream);
            BufferedReader buffreader = new BufferedReader(inputreader);

            String line;
            while ((line = buffreader.readLine()) != null) {
                if(line.startsWith(" ") || line.length() == 0 || line.startsWith("//")) continue;
                String[] att = line.split(" ");
                String name = att[0];
                String flo = att[1].trim();
                float pos = Float.valueOf(flo).floatValue();

                switch (name) {
                    case "Mountain": objects.add(new Mountain(pos)); break;
                    case "Cloud": objects.add(new Cloud(pos)); break;
                    case "Bird": objects.add(new Bird(pos)); break;
                    case "Platform": objects.add(new Platform(pos)); objects.add(new House(pos+0.2f)); break;
                }

            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes objects that are out of the scene
     * @author Michael Pointner
     */
    private void removeObjects() {
        objectsLocked = true;
        Iterator<MoveableObject> iterator = objects.iterator();
        while(iterator.hasNext()) {
            MoveableObject obj = iterator.next();
            if(obj.removeable()) {
                iterator.remove();
            }
        }
        objectsLocked = false;
    }

    /**
     * Resets the last object variables
     * @author Michael Pointner
     */
    public void resetLastObjects() {
        last = null;
        last2 = null;
        last3 = null;

        Platform.setLastSameObject(null);
        Bird.setLastSameObject(null);
        Cloud.setLastSameObject(null);
        Mountain.setLastSameObject(null);
    }

    /**
     * Returns the screen size of the device
     * @return screen size
     * @author Michael Pointner
     */
    public Point getScreenSize() {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    /**
     * Returns the actor of the game
     * @return Returns the actor object
     * @author Simon Reisinger
     */
    public static Actor getActor() {
        return actor;
    }
}
