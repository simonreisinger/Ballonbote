package at.ac.tuwien.pointnerreisinger.ballonbote;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

/**
 * Provides the platform for the landing of the ballon
 *
 * @author Michael Pointner
 */
public class Platform extends Obstacle {

    private static Bitmap orgImage;
    private static Bitmap image;
    private static float destWidth; // Prozent der Bildschirmgroesse
    private static float destHeight; // Prozent der Bildschirmgroesse
    private static Box boundingBox;
    private static Platform lastSameObject;
    private static Platform lastLanded;

    private static int platformCount = 0;
    private boolean platformLanded = false;


    public static final float minDistanceSameAbs = 1f;

    /**
     * Returns the minimum distance to the last object of the same class
     *
     * @return Minimum distance to the last object of the same class
     * @author Michael Pointner
     */
    public static float getMinDistanceSameRel() {
        return (getLastSameObject() != null ? getLastSameObject().rightX() + minDistanceSameAbs * GameLoop.sumIncreaseFunktionGameSpeed(platformCount) / GameLoop.getGameSpeedStart()
                : minDistanceSameAbs * GameLoop.sumIncreaseFunktionGameSpeed(platformCount) / GameLoop.getGameSpeedStart()); // 14
    }

    /**
     * Returns the minimum distance to the last object
     *
     * @param last  Last Object
     * @param last2 the Object before last
     * @param last3 the Object before last2
     * @return Minimum distance to the last object
     * @author Michael Pointner
     */
    public static float getMinDistance(MoveableObject last, MoveableObject last2, MoveableObject last3) {
        Actor actor = GameSurfaceView.getActor();
        if (last != null && (last instanceof House) || (last instanceof Mountain)) {
            return last.rightX() + 0.1f; // 15
        }
        if (last != null && (last instanceof Bird || last instanceof Cloud)) {
            if (last2 != null && (last2 instanceof Bird || last2 instanceof Cloud)) {
                if (last3 != null && (last3 instanceof House || last3 instanceof Mountain)) {
                    return last3.rightX() + 0.1f; // 16
                }
            }
            if (last2 != null && (last2 instanceof House || last2 instanceof Mountain)) {
                return last2.rightX() + 0.1f; // 15
            }
        }
        if (last != null && (last instanceof Bird)) {
            return last.rightX() + 2 * actor.minChangeNivelX();
        }
        if (last != null && (last instanceof Cloud)) {
            return last.rightX();
        }
        if (last != null) {
            return last.rightX();
        }
        return 0;
    }

    /**
     * The rectangle containing the sprite animation
     */
    private Rect sourceRect;
    /**
     * An frame's width of the sprite
     */
    private int frameWidth;
    /**
     * An frame's height in the sprite
     */
    private int frameHeight;

    /**
     * Creates an object of Platform
     *
     * @author Michael Pointner
     */
    public Platform() {
        y = 0.91f;
        this.frameWidth = image.getWidth();
        this.frameHeight = image.getHeight();
        this.sourceRect = new Rect(0, 0, frameWidth, frameHeight);

        platformCount++;
    }

    /**
     * Creates an object of Platform
     *
     * @author Michael Pointner
     */
    public Platform(float x) {
        this();
        this.creationX = x;
        this.x = x + Actor.posX;
    }

    /**
     * Loads the image for this class
     *
     * @param view GameSurfaceView for the image loading
     * @author Simon Reisinger
     */
    public static void loadImage(GameSurfaceView view) {
        if (image == null) {
            orgImage = BitmapFactory.decodeResource(view.getResources(), R.drawable.platform);

            destHeight = 0.01f;
            destWidth = calcDestWidth(orgImage, view.getWidth(), view.getHeight(), destHeight);

            image = resizeImage(orgImage, view.getWidth(), view.getHeight(), destWidth, destHeight);

            orgImage = null;
        }
    }

    /**
     * Creates the bounding boxes
     *
     * @author Simon Reisinger
     */
    public static void loadBoundingBox() {
        if (boundingBox == null) {
            boundingBox = new Box(image, 1);
        }
    }

    /**
     * Sets the last same object of this class
     *
     * @param sameObject Object of this class
     * @author Michael Pointner
     */
    public static void setLastSameObject(Platform sameObject) {
        Platform.lastSameObject = sameObject;
    }

    /**
     * Resets the values of this class
     *
     * @author Michael Pointner
     */
    public static void resetValues() {
        lastSameObject = null;
    }

    /**
     * Returns the last object of this class
     *
     * @return Last object of this class
     * @author Michael Pointner
     */
    public static Platform getLastSameObject() {
        return Platform.lastSameObject;
    }

    /**
     * Returns the last object of this class
     *
     * @return Last object of this class
     * @author Michael Pointner
     */
    public Platform getLastObject() {
        return Platform.lastSameObject;
    }

    /**
     * Performs a reaction the the collision of the actor with this platform
     *
     * @author Simon Reisinger
     */
    @Override
    protected void reactCollision() {
        Actor actor = GameSurfaceView.getActor();
        //GameSurfaceView.getLoop().setDebugText("Platform Collision");
        System.out.println("Platform Collision");
        actor.landing(this);

        lastLanded = this;
    }

    /**
     * Updates the X coordinate
     *
     * @param tpf Time per frame
     * @author Simon Reisinger
     */
    @Override
    protected void updateX(float tpf) {
        super.updateX(tpf);

        GameLoop loop = GameSurfaceView.getLoop();
        if (rightX() <= 0 && creationX != Float.NEGATIVE_INFINITY && !platformLanded) {
            loop.increaseLevel(false);
            platformLanded = true;
        }
    }

    /**
     * Returns the platform landed status
     *
     * @return Platform landed status
     * @author Michael Pointner
     */
    public boolean getPlatformLanded() {
        return platformLanded;
    }

    /**
     * Returns the destination height of this object
     *
     * @return Destination height of this object
     * @author Simon Reisinger
     */
    @Override
    public float getDestHeight() {
        return destHeight;
    }

    /**
     * Returns the destination width of this object
     *
     * @return Destination width of this object
     * @author Simon Reisinger
     */
    @Override
    public float getDestWidth() {
        return destWidth;
    }

    /**
     * Returns the image of this class
     *
     * @return Image of this class
     * @author Simon Reisinger
     */
    @Override
    public Bitmap getImage() {
        return image;
    }

    /**
     * Returns the source rectangle
     *
     * @return Source rectangle
     * @author Simon Reisinger
     */
    @Override
    public Rect getSourceRect() {
        return sourceRect;
    }

    /**
     * Updates the image in respect to the animation
     *
     * @param tpf Time per frame
     * @author Simon Reisinger
     */
    @Override
    protected void updateImage(float tpf) {
    }

    /**
     * Returns the current bounding box
     *
     * @return Current bounding box
     * @author Simon Reisinger
     */
    @Override
    public Box getCurrentBoundingBox() {
        return boundingBox;
    }

    /**
     * Returns the velocity
     *
     * @return velocity
     * @author Simon Reisinger
     */
    @Override
    public float getVelocity() {
        return 1;
    }

    /**
     * Returns a representation of this object
     *
     * @return String representing this object
     * @author Michael Pointner
     */
    @Override
    public String toString() {
        return "Platform " + Math.round(creationX * 100f) / 100f;
    }
}
