package at.ac.tuwien.pointnerreisinger.ballonbote;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

/**
 * Handles the cloud objects
 *
 * @author Simon Reisinger
 */
public class Cloud extends Obstacle {

    private static Bitmap orgImage;
    private static Bitmap image;
    private static float destWidth; // Prozent der Bildschirmgroesse
    private static float destHeight; // Prozent der Bildschirmgroesse
    private static Box[] boundingBoxes;
    private static Cloud lastSameObject;
    private static final float velocity = 1.5f; // deut. Geschwindigkeit = Vielfaches der GameSpeed

    public static final float minDistanceSameAbs = 0.1f;

    /**
     * Resets the values of this class
     *
     * @author Michael Pointner
     */
    public static float getMinDistanceSameRel() {
        return (getLastSameObject() != null ? getLastSameObject().rightX() + minDistanceSameAbs : 0); // 1
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
        if (last != null && last instanceof Bird) {
            if (last2 != null && (last2 instanceof Mountain || last2 instanceof House)) {
                return last.rightX() + actor.getToleranzDestWidth() + actor.minChangeNivelX() * velocity; // 2
            }
            if (last2 != null && last2 instanceof Bird) {
                if (last3 != null && (last3 instanceof Mountain || last3 instanceof House)) {
                    return last.rightX() + actor.getToleranzDestWidth() + actor.minChangeNivelX() * velocity; // 2
                }
            }
            return last.getCreationX();
        }
        if (last != null && last instanceof Mountain) {
            if (last2 != null && last2 instanceof Bird) {
                return last2.rightX() + actor.getToleranzDestWidth() + actor.minChangeNivelX() * velocity; // 4
            }
            return last.getCreationX();
        }
        if (last != null && last instanceof House) {
            return last.rightX();
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
     * Number of frames in the sprite animation
     */
    private static final int numberOfFrames = 2;
    /**
     * The current frame in use
     */
    private float currentFrame;
    /**
     * An frame's width of the sprite
     */
    private int frameWidth;
    /**
     * An frame's height in the sprite
     */
    private int frameHeight;

    /**
     * Creates an object of cloud
     *
     * @author Simon Reisinger
     */
    private Cloud() {
        y = 0.1f;
        this.currentFrame = 0;
        this.frameWidth = (image.getWidth() - 1) / numberOfFrames;
        this.frameHeight = (image.getHeight() - 1);
        this.sourceRect = new Rect(0, 0, frameWidth, frameHeight);
    }

    public Cloud(float x) {
        this();

        this.creationX = x;
        this.x = Actor.posX + x * velocity;
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
            orgImage = BitmapFactory.decodeResource(view.getResources(), R.drawable.clouds);

            destHeight = 0.16f;
            destWidth = calcDestWidth(orgImage, view.getWidth(), view.getHeight(), destHeight);

            image = resizeImage(orgImage, view.getWidth(), view.getHeight(), destWidth, destHeight);

            destWidth /= numberOfFrames;

            orgImage = null;
        }
    }

    /**
     * Loads the bounding box of this class
     *
     * @author Simon Reisinger
     */
    public static void loadBoundingBox() {
        if (boundingBoxes == null) {
            boundingBoxes = new Box[numberOfFrames];
            int left = 0;
            int factor = image.getWidth() / numberOfFrames;
            int right = factor;
            for (int i = 0; i < numberOfFrames; i++) {
                boundingBoxes[i] = new Box(image, left, 0, right - 1, image.getHeight() - 1, 0, 5, left);
                left = right;
                right += factor;
            }
        }
    }

    /**
     * Sets the last same object of this class
     *
     * @param sameObject Object of this class
     * @author Michael Pointner
     */
    public static void setLastSameObject(Cloud sameObject) {
        Cloud.lastSameObject = sameObject;
    }

    /**
     * Returns the last object of this class
     *
     * @return Last object of this class
     * @author Michael Pointner
     */
    public static Cloud getLastSameObject() {
        return Cloud.lastSameObject;
    }

    /**
     * Returns the last object of this class
     *
     * @return Last object of this class
     * @author Michael Pointner
     */
    public Cloud getLastObject() {
        return Cloud.lastSameObject;
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
        currentFrame = 1 / (3000 * (float) Math.random() * tpf);
        sourceRect.left = ((int) currentFrame) % (numberOfFrames) * frameWidth;
        sourceRect.right = sourceRect.left + frameWidth;
    }

    /**
     * Returns the current bounding box
     *
     * @return Current bounding box
     * @author Simon Reisinger
     */
    @Override
    public Box getCurrentBoundingBox() {
        int frameSelect = ((int) currentFrame) % (numberOfFrames);
        GameSurfaceView.getLoop().setDebugText("Frameselect " + frameSelect);
        return boundingBoxes[frameSelect];
    }

    /**
     * Returns the velocity
     *
     * @return velocity
     * @author Simon Reisinger
     */
    @Override
    public float getVelocity() {
        return velocity;
    }

    /**
     * Returns a representation of this object
     *
     * @return String representing this object
     * @author Michael Pointner
     */
    @Override
    public String toString() {
        return "Cloud " + Math.round(creationX * 100f) / 100f;
    }
}