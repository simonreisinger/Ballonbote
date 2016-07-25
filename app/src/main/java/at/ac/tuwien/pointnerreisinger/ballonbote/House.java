package at.ac.tuwien.pointnerreisinger.ballonbote;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

/**
 * House next to the landing platform
 * @author Michael Pointner
 */
public class House extends Obstacle {
    private static Bitmap orgImage;
    private static Bitmap image;
    private static float destWidth; // Prozent der Bildschirmgroesse
    private static float destHeight; // Prozent der Bildschirmgroesse
    private static Box boundingBox;
    private static House lastSameObject;

    /** The rectangle containing the sprite animation */
    private Rect sourceRect;
    /** The current frame in use */
    private float currentFrame;
    /** An frame's width of the sprite */
    private int frameWidth;
    /** An frame's height in the sprite */
    private int frameHeight;

    /**
     * Creates an object of Actor
     * @author Simon Reisinger
     */
    private House() {
        y = 0.77f;
        this.frameWidth = image.getWidth();
        this.frameHeight = image.getHeight();
        this.sourceRect = new Rect(0,0,frameWidth, frameHeight);

        lastSameObject = this;
    }

    /**
     * Creates an object of Actor
     * @param x X coordinate of this object
     * @author Simon Reisinger
     */
    public House(float x) {
        this();
        this.creationX = x;
        this.x = x + Actor.posX;
    }

    /**
     * Resets the values of this class
     * @author Michael Pointner
     */
    public static void resetValues() {
        lastSameObject = null;
    }

    /**
     * Loads the image for this class
     * @param view GameSurfaceView for the loading of the image
     * @author Simon Reisinger
     */
    public static void loadImage(GameSurfaceView view) {
        if(image == null) {
            orgImage = BitmapFactory.decodeResource(view.getResources(), R.drawable.house);

            destHeight = 0.15f;
            destWidth = calcDestWidth(orgImage, view.getWidth(), view.getHeight(), destHeight);

            image = resizeImage(orgImage, view.getWidth(), view.getHeight(), destWidth, destHeight);

            orgImage = null;
        }
    }

    /**
     * Loads the bounding box of this class
     * @author Simon Reisinger
     */
    public static void loadBoundingBox() {
        if(boundingBox == null) {
            boundingBox = new Box(image, 6);
        }
    }

    /**
     * Returns the last object of this class
     * @return Last object of this class
     * @author Michael Pointner
     */
    public House getLastObject() {
        return House.lastSameObject;
    }

    /**
     * Returns the destination height of this object
     * @return Destination height of this object
     * @author Simon Reisinger
     */
    @Override
    public float getDestHeight() {
        return destHeight;
    }

    /**
     * Returns the destination width of this object
     * @return Destination width of this object
     * @author Simon Reisinger
     */
    @Override
    public float getDestWidth() {
        return destWidth;
    }

    /**
     * Returns the image of this class
     * @return Image of this class
     * @author Simon Reisinger
     */
    @Override
    public Bitmap getImage() {
        return image;
    }

    /**
     * Returns the source rectangle
     * @return Source rectangle
     * @author Simon Reisinger
     */
    @Override
    public Rect getSourceRect() {
        return sourceRect;
    }

    /**
     * Updates the image in respect to the animation
     * @param tpf Time per frame
     * @author Simon Reisinger
     */
    @Override
    protected void updateImage(float tpf) {}

    /**
     * Returns the current bounding box
     * @return Current bounding box
     * @author Simon Reisinger
     */
    @Override
    public Box getCurrentBoundingBox() {
        return boundingBox;
    }

    /**
     * Returns the velocity
     * @return velocity
     * @author Simon Reisinger
     */
    @Override
    public float getVelocity() {
        return 1;
    }

    /**
     * Returns a representation of this object
     * @return String representing this object
     * @author Michael Pointner
     */
    @Override
    public String toString() {
        return "House "+Math.round(creationX*10f)/10f;
    }
}
