package at.ac.tuwien.pointnerreisinger.ballonbote;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

/**
 * Provides an actor for the user to play with
 * @author Michael Pointner
 */
public class Actor extends MoveableObject {

    private static Bitmap orgImage;
    private static Bitmap image;
    private static float destWidth; // Prozent der Bildschirmgroesse
    private static float destHeight; // Prozent der Bildschirmgroesse
    public static final float posX = 0.3f;
    private float worldX = 0;

    private float userinputGradient = 0;
    private float gradientMax = 1;
    private float minY = 0.92f;
    private float maxY = 0.01f;
    private static Box boundingBox;
    private static Actor lastSameObject;

    /** The rectangle containing the sprite animation */
    private Rect sourceRect;
    /** An frame's width of the sprite */
    private int frameWidth;
    /** An frame's height in the sprite */
    private int frameHeight;

    /**
     * Creates an object of Actor
     * @author Michael Pointner
     */
    public Actor() {
        x = this.creationX = posX;
        y = 0.61f;

        this.frameWidth = image.getWidth();
        this.frameHeight = image.getHeight();
        this.sourceRect = new Rect(0,0,frameWidth, frameHeight);

        lastSameObject = this;
    }

    /**
     * Loads the image for this class
     * @param view GameSurfaceView for the image loading
     * @author Michael Pointner
     */
    public static void loadImage(GameSurfaceView view) {
        orgImage = BitmapFactory.decodeResource(view.getResources(), R.drawable.ballon);

        destHeight = 0.3f;
        destWidth = calcDestWidth(orgImage, view.getWidth(), view.getHeight(), destHeight);

        image = resizeImage(orgImage, view.getWidth(), view.getHeight(), destWidth, destHeight);

        orgImage = null;
    }

    /**
     * Creates the bounding boxes
     * @author Simon Reisinger
     */
    public static void loadBoundingBox() {
        if(boundingBox == null) {
            boundingBox = new Box(image, 7);
        }
    }

    /**
     * Returns the last created object of this class
     * @return Last created object of this class
     * @author Michael Pointner
     */
    public Actor getLastObject() {
        return Actor.lastSameObject;
    }

    /**
     * Updates the X coordinate
     * @author Michael Pointner
     */
    @Override
    protected void updateX(float tpf) {
        GameLoop loop = GameSurfaceView.getLoop();
        float gameSpeed = loop.getGameSpeed();
        worldX += getVelocity() * (loop.getRunningWorld() ? gameSpeed : 0) * tpf;

        float increasement = (loop.getRunningWorld() ? gameSpeed : 0) * tpf;
        loop.increaseScore(increasement * 100.0f);
    }

    /**
     * Updates the Y coordinate in respect to the user input
     * @author Michael Pointner
     */
    @Override
    protected void updateY(float tpf) {
        GameLoop loop = GameSurfaceView.getLoop();
        if(!loop.getRunningWorld()) return;
        gradient += userinputGradient * tpf;
        if(gradient > gradientMax) gradient = gradientMax;
        if(gradient < -gradientMax) gradient = -gradientMax;

        y += gradient * 10f * loop.getGameSpeed();
        if(y < maxY) {
            y = maxY;
            gradient = 0;
        }
        if(y > minY - destHeight) {
            y = minY - destHeight;
            loop.setStopped(true);
        }
    }

    /**
     * Returns the minimum distance the actor needs to change a nivel of height
     * @return minimum distance
     * @author Michael Pointner
     */
	public float minChangeNivelX() {
		return 0.2f;
	}

    /**
     * Sets the user input gradient
     * @param userinputGradient User input gradient
     * @author Michael Pointner
     */
    public void setUserinputGradient(float userinputGradient) {
        GameLoop loop = GameSurfaceView.getLoop();
        if(loop.getLanded()) {

            userinputGradient = -0.01f;
            loop.setLanded(false);
        }
        if(loop.getPaused()) {
            loop.setPaused(false);
        }
        this.userinputGradient = userinputGradient;
    }

    /**
     * Sets the gradient of the actors altitude
     * @param gradient Gradient of the altitude
     * @author Michael Pointner
     */
    public void setGradient(float gradient) {
        this.gradient = gradient;
        this.userinputGradient = 0;
    }

    /**
     * Sets the actors status to landed
     * @author Michael Pointner
     */
    public void landing(Platform platform) {
        GameLoop loop = GameSurfaceView.getLoop();
        if(!platform.getPlatformLanded() && !loop.getLevelIncreased()) {
            loop.displayPackageDeliver(true);

            if(!loop.getLanded()) {
                loop.increaseLevel(true);
            }

            float scoreLandingBonus = 100;
            loop.increaseScore(scoreLandingBonus);
            loop.setScoreLastPlatform();
            loop.setLevelLastPlatform(loop.getLevel());
        }
        loop.setLanded(true);
        y = platform.getY() - destHeight;
        gradient = 0;
        userinputGradient = 0;
    }

    /**
     * Returns the destination height in percentage
     * @return destination height in percentage
     * @author Simon Reisinger
     */
    @Override
    public float getDestHeight() {
        return destHeight;
    }

    /**
     * Returns the destination width in percentage
     * @return destination width in percentage
     * @author Simon Reisinger
     */
    @Override
    public float getDestWidth() {
        return destWidth;
    }

    /**
     * Returns the toleranz destination width in percentage
     * @return toleranz destination width in percentage
     * @author Michael Pointner
     */
    public float getToleranzDestWidth() {
        return destWidth * GameLoop.getToleranz();
    }

    /**
     * Returns the image of this class
     * @return Image
     * @author Simon Reisinger
     */
    @Override
    public Bitmap getImage() {
        return image;
    }

    /**
     * Returns the source rectangle of this class
     * @return Source rectangle
     * @author Simon Reisinger
     */
    @Override
    public Rect getSourceRect() {
        return sourceRect;
    }

    /**
     * Updates the image if it has a animation
     * @param tpf Times per frame
     * @author Simon Reisinger
     */
    @Override
    protected void updateImage(float tpf) {}

    /**
     * Returns the current bounding box
     * @return current bounding box
     * @author Simon Reisinger
     */
    @Override
    public Box getCurrentBoundingBox() {
        return boundingBox;
    }

    /**
     * Returns the velocity of the object
     * @return velocity
     * @author Simon Reisinger
     */
    @Override
    public float getVelocity() {
        return 1;
    }
}
