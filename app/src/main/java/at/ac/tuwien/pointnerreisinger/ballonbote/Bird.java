package at.ac.tuwien.pointnerreisinger.ballonbote;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

/**
 * Handles the cloud objects
 *
 * @author Simon Reisinger
 */
public class Bird extends Obstacle {

    protected static Bitmap orgImage;
    protected static Bitmap image;
    protected static float destWidth; // Prozent der Bildschirmgroesse
    protected static float destHeight; // Prozent der Bildschirmgroesse
    private static Box[] boundingBoxes;
    private static Bird lastSameObject;
    private static final float velocity = 2f; // deut. Geschwindigkeit = Vielfaches der GameSpeed

    public static final float minDistanceSameAbs = 0.1f;

    /**
     * Returns the minimum distance to the last object of the same class
     *
     * @return Minimum distance to the last object of the same class
     * @author Michael Pointner
     */
    public static float getMinDistanceSameRel() {
        return (getLastSameObject() != null ? getLastSameObject().rightX() + minDistanceSameAbs : 0); // 5
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
        if (last != null && last instanceof Cloud) {
            if (last2 != null && last2 instanceof Mountain) {
                return Math.min(last.rightX(), last2.rightX()) + actor.getToleranzDestWidth() + actor.minChangeNivelX() * velocity; // 6
            }
            if (last2 != null && last2 instanceof House) {
                return Math.min(last.rightX(), last2.rightX()) + actor.getToleranzDestWidth() + actor.minChangeNivelX() * velocity; // 7
            }
            if (last2 != null && last2 instanceof Bird) {
                if (last3 != null && (last3 instanceof House || last3 instanceof Mountain)) {
                    return last2.rightX() + actor.getToleranzDestWidth() + 2 * actor.minChangeNivelX() * velocity;
                }
            }
            if (last2 != null && last2 instanceof Cloud) {
                if (last3 != null && (last3 instanceof House || last3 instanceof Mountain)) {
                    return last3.rightX() + actor.getToleranzDestWidth() + actor.minChangeNivelX() * velocity;
                }
            }
            return last.getCreationX();
        }
        if (last != null && last instanceof Mountain) {
            if (last2 != null && last2 instanceof Cloud) {
                return Math.min(last.rightX(), last2.rightX()) + actor.getToleranzDestWidth() + actor.minChangeNivelX() * velocity; // 6
            }
            return last.getCreationX();
        }
        if (last != null && last instanceof House) {
            return last.rightX() + 0f;
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
    private static final int numberOfFrames = 6;
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

    float Ylevel = 0;
    float Xoffset = 0;

    /**
     * Creates an object of Bird
     *
     * @author Simon Reisinger
     */
    private Bird() {
        Ylevel = 0.4f;
        Xoffset = (float) Math.random();
        this.currentFrame = (int) (Math.random() * 2) + 1;
        this.frameWidth = image.getWidth() / numberOfFrames;
        this.frameHeight = image.getHeight();
        this.sourceRect = new Rect(0, 0, frameWidth, frameHeight);

    }

    /**
     * Creates an object of Bird
     *
     * @author Simon Reisinger
     */
    public Bird(float x) {
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
            orgImage = BitmapFactory.decodeResource(view.getResources(), R.drawable.bird);

            destHeight = 0.08f;
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
            float factor = image.getWidth() / numberOfFrames;
            int right = (int) factor;
            for (int i = 0; i < numberOfFrames; i++) {
                boundingBoxes[i] = new Box(image, left, 0, right - 1, image.getHeight() - 1, 0, 4, left);
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
    public static void setLastSameObject(Bird sameObject) {
        Bird.lastSameObject = sameObject;
    }

    /**
     * Returns the last object of this class
     *
     * @return Last object of this class
     * @author Michael Pointner
     */
    public static Bird getLastSameObject() {
        return Bird.lastSameObject;
    }

    /**
     * Returns the last object of this class
     *
     * @return Last object of this class
     * @author Michael Pointner
     */
    public Bird getLastObject() {
        return Bird.lastSameObject;
    }

    /**
     * Updates the Y coordinate
     *
     * @param tpf Time per frame
     * @author Simon Reisinger
     */
    @Override
    protected void updateY(float tpf) {
        y = Ylevel + (float) Math.sin((Xoffset + 1 - x) * 2 * Math.PI * 4) / 30f;
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
        currentFrame += 10 * tpf;
        currentFrame %= (numberOfFrames - 1) * 2;
        sourceRect.left = ((int) (currentFrame < numberOfFrames ? currentFrame : (numberOfFrames - 1) * 2 - currentFrame) * frameWidth);
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
        return "Bird " + Math.round(creationX * 100f) / 100f;
    }
}
