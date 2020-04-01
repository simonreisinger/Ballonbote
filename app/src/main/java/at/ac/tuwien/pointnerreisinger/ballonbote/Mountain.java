package at.ac.tuwien.pointnerreisinger.ballonbote;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

/**
 * Handles the mountain objects
 *
 * @author Simon Reisinger
 */
public class Mountain extends Obstacle {

    private static Bitmap orgImage;
    private static Bitmap image;
    private static float destWidth; // Prozent der Bildschirmgroesse
    private static float destHeight; // Prozent der Bildschirmgroesse
    private static Box boundingBox;
    private static Mountain lastSameObject;

    public static final float minDistanceSameAbs = 0.1f;

    /**
     * Returns the minimum distance to the last object of the same class
     *
     * @return Minimum distance to the last object of the same class
     * @author Michael Pointner
     */
    public static float getMinDistanceSameRel() {
        return (getLastSameObject() != null ? getLastSameObject().rightX() + minDistanceSameAbs : 0); // 10
    } // DONE

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
        if (last != null && last instanceof Cloud) {
            if (last2 != null && last2 instanceof Bird) {
                if (last3 != null && last3 instanceof House) {
                    return last3.rightX() + 0.1f; // 13
                }
                return last2.rightX() + actor.getToleranzDestWidth() + actor.minChangeNivelX(); // 11
            }
            if (last2 != null && last2 instanceof House) {
                return last2.rightX() + 0.1f; // 13
            }
            return last.getCreationX();
        }
        if (last != null && last instanceof Bird) {
            if (last2 != null && last2 instanceof Cloud) {
                if (last3 != null && last3 instanceof House) {
                    return last3.rightX() + 0.1f; // 13
                }
                return last.rightX() + actor.getToleranzDestWidth() + actor.minChangeNivelX(); // 12
            }
            if (last2 != null && last2 instanceof House) {
                return last2.rightX() + 0.1f; // 13
            }
            return last.getCreationX();
        }
        if (last != null && last instanceof House) {
            return last.rightX() + 0.1f; // 13
        }
        if (last != null) {
            return last.rightX();
        }
        return 0f;
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
     * Creates an object of Mountain
     *
     * @author Simon Reisinger
     */
    public Mountain() {
        x = 1f;
        y = 0.62f;
        this.frameWidth = image.getWidth();
        this.frameHeight = image.getHeight();
        this.sourceRect = new Rect(0, 0, frameWidth, frameHeight);

    }

    /**
     * Creates an object of Actor
     *
     * @author Simon Reisinger
     */
    public Mountain(float x) {
        this();
        this.creationX = x;
        this.x = x + Actor.posX;
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
     * Loads the image for this class
     *
     * @param view GameSurfaceView for the loading of the image
     * @author Simon Reisinger
     */
    public static void loadImage(GameSurfaceView view) {
        if (image == null) {
            orgImage = BitmapFactory.decodeResource(view.getResources(), R.drawable.mountain);

            destHeight = 0.3f;
            destWidth = calcDestWidth(orgImage, view.getWidth(), view.getHeight(), destHeight);

            image = resizeImage(orgImage, view.getWidth(), view.getHeight(), destWidth, destHeight);

            orgImage = null;
        }
    }

    /**
     * Loads the bounding box of this class
     *
     * @author Simon Reisinger
     */
    public static void loadBoundingBox() {
        if (boundingBox == null) {
            boundingBox = new Box(image, 7);
        }
    }

    /**
     * Sets the last same object of this class
     *
     * @param sameObject Object of this class
     * @author Michael Pointner
     */
    public static void setLastSameObject(Mountain sameObject) {
        Mountain.lastSameObject = sameObject;
    }

    /**
     * Returns the last object of this class
     *
     * @return Last object of this class
     * @author Michael Pointner
     */
    public static Mountain getLastSameObject() {
        return Mountain.lastSameObject;
    }

    /**
     * Returns the last object of this class
     *
     * @return Last object of this class
     * @author Michael Pointner
     */
    public Mountain getLastObject() {
        return Mountain.lastSameObject;
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
     * Updates the image in respect to the animation if it has one
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
        return "Mountain " + Math.round(creationX * 100f) / 100f;
    }
}