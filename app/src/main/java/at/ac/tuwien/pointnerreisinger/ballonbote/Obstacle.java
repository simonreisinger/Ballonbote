package at.ac.tuwien.pointnerreisinger.ballonbote;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Looper;

import java.util.IllegalFormatFlagsException;

/**
 * Obstacle combines all objects the can collide with the actor
 * @author Simon Reisinger
 */
public abstract class Obstacle extends MoveableObject {

    /**
     * Pases the image to the super class
     * @author Simon Reisinger
     */
    public Obstacle() {
        super();
    }

    /**
     * Method checking for collisions
     * @param canvas Canvas to get the screen size
     * @return if a collision occured
     * @author Simon Reisinger
     */
    @Override
    protected boolean collision(Canvas canvas) {
        Actor actor = GameSurfaceView.getActor();
        boolean collision = false;

        if(!(this.getX() > actor.getX()+actor.getDestWidth() || this.getX()+this.getDestWidth() < actor.getX() ||
                this.getY() > actor.getY()+actor.getDestHeight() || this.getY()+this.getDestHeight() < actor.getY())) {


            Box boxAct = actor.getCurrentBoundingBox();
            Box boxObs = getCurrentBoundingBox();

            if(boxAct == null || boxObs == null) return false;

            int offsetX = boxObs.getXOffset();
            int x = (int)((getX()-actor.getX())*canvas.getWidth())-offsetX;
            int y = (int)((getY()-actor.getY())*canvas.getHeight());

            collision = boxObs.intersect(boxAct, x, y);
        }
        return collision;
    }
}