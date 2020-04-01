package at.ac.tuwien.pointnerreisinger.ballonbote;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;

/**
 * Provides the background for the game
 *
 * @author Michael Pointner
 */
public class Background extends MoveableObject {

    private static Bitmap orgImage;
    private static Bitmap image;
    private static float destWidth; // Prozent der Bildschirmgroesse
    private static float destHeight; // Prozent der Bildschirmgroesse

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
     * Creates an object of Background
     *
     * @author Michael Pointner
     */
    public Background() {
        x = this.creationX = 0f;
        y = 0f;
        this.frameWidth = image.getWidth();
        this.frameHeight = image.getHeight();
        this.sourceRect = new Rect(0, 0, frameWidth, frameHeight);
    }

    /**
     * Loads the image for this class
     *
     * @param view GameSurfaceView for the loading of the image
     * @author Simon Reisinger
     */
    public static void loadImage(GameSurfaceView view) {
        if (image == null) {
            orgImage = BitmapFactory.decodeResource(view.getResources(), R.drawable.background);
            destHeight = 1f;
            destWidth = 1f;

            image = resizeImage(orgImage, view.getWidth(), view.getHeight(), destWidth, destHeight);

            orgImage = null;
        }
    }

    /**
     * Returns the last object of this class
     *
     * @return Last object
     * @author Michael Pointner
     */
    public Background getLastObject() {
        return this;
    }

    /**
     * Loads the bounding box of this class if it has one
     *
     * @author Simon Reisinger
     */
    public static void loadBoundingBox() {
    }

    /**
     * Overrides the onDraw method of MoveableObject to just draw an unicolor background
     *
     * @param canvas Canvas to draw on
     * @author Simon Reisinger
     */
    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawColor(Color.rgb(144, 193, 226));
    }

    /**
     * Updates the X coordinate
     *
     * @author Simon Reisinger
     */
    @Override
    protected void updateX(float tpf) {
    }

    /**
     * Returns the destination height of this object
     *
     * @return destination height in precentage
     * @author Simon Reisinger
     */
    @Override
    public float getDestHeight() {
        return destHeight;
    }

    /**
     * Returns the destination width of this object
     *
     * @return destination width in precentage
     * @author Simon Reisinger
     */
    @Override
    public float getDestWidth() {
        return destWidth;
    }

    /**
     * Returns the image of this class
     *
     * @return Image
     * @author Simon Reisinger
     */
    @Override
    public Bitmap getImage() {
        return image;
    }

    /**
     * Returns the source rectangle
     *
     * @return source rectangle
     * @author Simon Reisinger
     */
    @Override
    public Rect getSourceRect() {
        return sourceRect;
    }

    /**
     * Updates the image if it has an animation
     *
     * @param tpf Time per frame
     * @author Simon Reisinger
     */
    @Override
    protected void updateImage(float tpf) {
    }


    /**
     * Returns the current bounding box if it has one
     *
     * @return current bounding box
     * @author Simon Reisinger
     */
    @Override
    public Box getCurrentBoundingBox() {
        return null;
    }

    /**
     * Returns the velocity of this object
     *
     * @return Velocity
     * @author Simon Reisinger
     */
    @Override
    public float getVelocity() {
        return 1;
    }
}
