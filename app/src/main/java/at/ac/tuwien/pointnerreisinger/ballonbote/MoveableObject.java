package at.ac.tuwien.pointnerreisinger.ballonbote;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.audiofx.AcousticEchoCanceler;

/**
 * Combines all moving Objects
 * @author Michael Pointner
 */
public abstract class MoveableObject {

    protected float x = 1;
    protected float y = 0; // Prozent der Bildschirmgroesse
    protected float gradient = 0f; // deut. Steigung
    protected int repeat = 1;
    protected float creationX = 1;

    protected float minDistanceToSame = 0f;

    private MoveableObject lastObj;

    /**
     * Creates an Object of MoveableObject
     * @author Michael Pointner
     */
    protected MoveableObject() {}

    /**
     * Resizes the passed image
     * @param orgImage Image to resize
     * @param screenWidth Screen width
     * @param screenHeight Screen height
     * @param destWidth Destination width in percentage
     * @param destHeight Destination height in percentage
     * @return Resized image
     * @author Simon Reisinger
     */
    public static Bitmap resizeImage(Bitmap orgImage, int screenWidth, int screenHeight, float destWidth, float destHeight){
        int srcWidth = (int)(destWidth * (float)screenWidth);
        int srcHeight = (int)(destHeight * (float)screenHeight);

        return Bitmap.createScaledBitmap(orgImage, srcWidth, srcHeight, false);
    }

    /**
     * Calculates the destination width
     * @param image Image
     * @param screenWidth Screen width
     * @param screenHeight Screen height
     * @param destHeight Destination height in percentage
     * @return Calculated destination width
     * @author Simon Reisinger
     */
    public static float calcDestWidth(Bitmap image, int screenWidth, int screenHeight, float destHeight) {
        float f = ((float)image.getWidth() / (float)image.getHeight()) * ((float)screenHeight / (float)screenWidth);
        return destHeight*f;
    }

    /**
     * Updates the position of this object
     * @param tpf Time per frame
     * @author Michael Pointner
     */
    public void update(float tpf) {
        updateX(tpf);
        updateY(tpf);
        GameLoop loop = GameSurfaceView.getLoop();
        if(tpf != 0 && loop.getRunningWorld()) {
            updateImage(tpf);
        }
    }

    /**
     * Draws the image of the object on the scene
     * @param canvas Canvas to draw on
     * @author Michael Pointner
     */
    public void onDraw(Canvas canvas){
        if(x > 1 || x < -1) return;

        GameLoop loop = GameSurfaceView.getLoop();

        Bitmap image = getImage();
        if(image == null) {
            return;
        }
        float destWidth = getDestWidth();
        float destHeight = getDestHeight();

        float areaActorL = GameSurfaceView.getActor().getX();
        float areaActorR = areaActorL + GameSurfaceView.getActor().getDestWidth();

        for(int repeatIndex=0; repeatIndex<repeat; repeatIndex++) {
            canvas.drawBitmap(image,
                    getSourceRect(),
                    new Rect(Math.round((x + destWidth * repeatIndex) * (float) canvas.getWidth()),
                             Math.round(y * (float) canvas.getHeight()),
                             Math.round((x + destWidth * (repeatIndex + 1)) * (float) canvas.getWidth()),
                             Math.round((y + destHeight) * (float) canvas.getHeight())
                    ), null);

            if(loop.getDebug()) {
                Paint paint = new Paint();
                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(1);
                canvas.drawRect(Math.round((x + destWidth * repeatIndex) * (float) canvas.getWidth()),
                        Math.round(y * (float) canvas.getHeight()),
                        Math.round((x + destWidth * (repeatIndex + 1)) * (float) canvas.getWidth()),
                        Math.round((y + destHeight) * (float) canvas.getHeight()),
                        paint);
            }
        }
        if((loop.getCollision() || this instanceof Platform) && collision(canvas) && !loop.getLanded()){
            reactCollision();
        }
    }

    /**
     * Method checking for collisions. Should be overriden by obstacle
     * @param canvas Canvas to get the screen size
     * @return if a collision occured
     * @author Michael Pointner
     */
    protected boolean collision(Canvas canvas) {
        return false;
    }

    /**
     * Defines the reaction to collision
     * @author Simon Reisinger
     */
    protected void reactCollision() {
        GameLoop loop = GameSurfaceView.getLoop();
        loop.setStopped(true);
    }

    /**
     * Updates the X coordinate
     * @param tpf Time per frame
     * @author Michael Pointner
     */
    protected void updateX(float tpf) {
        GameLoop loop = GameSurfaceView.getLoop();
        float gameSpeed = loop.getGameSpeed();
        x -= getVelocity() * (loop.getRunningWorld() ? gameSpeed : 0) * tpf;
        creationX -= (loop.getRunningWorld() ? gameSpeed : 0) * tpf;
    }

    /**
     * Updates the Y coordinate
     * @param tpf Time per frame
     * @author Michael Pointner
     */
    protected void updateY(float tpf) {
        y += gradient;
    }

    /**
     * Returns if the object can be removed
     * @return Boolean indicating the removability
     * @author Michael Pointner
     */
    public boolean removeable() {
        return (creationX < -1);
    }

    /**
     * Returns the last object of the class
     * @return last object of the class
     * @author Michael Pointner
     */
    public abstract MoveableObject getLastObject();

    /**
     * Returns the x coordinate of the object
     * @return x coordinate
     * @author Simon Reisinger
     */
    public float getX() {
        return x;
    }

    /**
     * Sets the creation x coordinate
     * @param x Creation x coordinate
     * @author Michael Pointner
     */
    public void setCreationX(float x) {
        creationX = x;
    }

    /**
     * Returns the creation x
     * @return creation x
     * @author Michael Pointner
     */
    public float getCreationX() {
        return creationX;
    }

    /**
     * Returns the y coordinate of the object
     * @return y coordinate
     * @author Simon Reisinger
     */
    public float getY() {
        return y;
    }

    /**
     * Returns the x coordinate of the right end of the object
     * @return X coordinate of the right end of the object
     * @author Michael Pointner
     */
    public float rightX() {
        float r = getCreationX() + getDestWidth() / getVelocity();
        return r;
    }

    /**
     * Returns the source width of the objects image
     * @return destination height
     * @author Simon Reisinger
     */
    public abstract float getDestHeight();

    /**
     * Returns the destination width of the objects image
     * @return destination width
     * @author Simon Reisinger
     */
    public abstract float getDestWidth();

    /**
     * Returns the objects image
     * @return Image
     * @author Simon Reisinger
     */
    public abstract Bitmap getImage();

    /**
     * Returns the source rectangle
     * @return Source rectangle
     * @autor Simon Reisinger
     */
    public abstract Rect getSourceRect();

    /**
     * Aktuallisiert ueberall wo noetig das Bild
     * @param tpf Time per frame
     * @author Simon Reisinger
     */
    protected abstract void updateImage(float tpf);

    /**
     * Returns the current bounding box
     * @return Current bounding box
     * @author Simon Reisinger
     */
    public abstract Box getCurrentBoundingBox();

    /**
     * Returns the velocity
     * @return velocity
     * @author Simon Reisinger
     */
    public abstract float getVelocity();
}
