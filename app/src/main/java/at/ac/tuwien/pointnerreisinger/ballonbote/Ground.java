package at.ac.tuwien.pointnerreisinger.ballonbote;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

/**
 * Provides the ground for the game
 * @author Michael Pointner
 */
public class Ground extends Obstacle {

    private static Bitmap orgImage;
    private static Bitmap image;
    private static float destWidth; // Prozent der Bildschirmgroesse
    private static float destHeight; // Prozent der Bildschirmgroesse
    private static Box boundingBox;
    private static Ground lastSameObject;

    /** The rectangle containing the sprite animation */
    private Rect sourceRect;
    /** An frame's width of the sprite */
    private int frameWidth;
    /** An frame's height in the sprite */
    private int frameHeight;

    /**
     * Creates an object of Ground
     * @author Michael Pointner
     */
    public Ground() {
        x = this.creationX = 0f;
        y = 0.92f;
        repeat = 40;
        this.frameWidth = image.getWidth();
        this.frameHeight = image.getHeight();
        this.sourceRect = new Rect(0,0,frameWidth, frameHeight);

        lastSameObject = this;
    }

    /**
     * Loads the image for this class
     * @param view GameSurfaceView for the loading of the image
     * @author Simon Reisinger
     */
    public static void loadImage(GameSurfaceView view) {
        if(image == null) {
            orgImage = BitmapFactory.decodeResource(view.getResources(), R.drawable.ground);

            destHeight = 0.08f;
            destWidth = calcDestWidth(orgImage, view.getWidth(), view.getHeight(), destHeight);

            image = resizeImage(orgImage, view.getWidth(), view.getHeight(), destWidth, destHeight);

            orgImage = null;
        }
    }

    /**
     * Returns the last object of this class
     * @return Last object of this class
     * @author Michael Pointner
     */
    public Ground getLastObject() {
        return this;
    }

    /**
     * Updates the X coordinate
     * @param tpf Time per frame
     * @author Michael Pointner
     */
    @Override
    protected void updateX(float tpf) {
        super.updateX(tpf);

        if(x < -destWidth) {
            x += destWidth;
        }
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
}
