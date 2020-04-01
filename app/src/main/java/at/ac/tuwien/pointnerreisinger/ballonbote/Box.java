package at.ac.tuwien.pointnerreisinger.ballonbote;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;

/**
 * Contains a bounding box and subboxes for each object
 */
public class Box {
    public Rect boundingbox;
    private final int level; // starting with 0;
    private Box[] content;
    private boolean empty;
    private int xOffset = 0;

    /**
     * Initializes a new Box
     *
     * @param image    Image
     * @param maxDepth Maximum depth of splitting
     * @author Simon Reisinger
     */
    public Box(Bitmap image, int maxDepth) {
        this(image, 0, 0, image.getWidth() - 1, image.getHeight() - 1, 0, maxDepth);
    }

    /**
     * Initializes a new Box
     *
     * @param image    Image
     * @param left     Left border
     * @param top      Top border
     * @param right    Right border
     * @param bottom   Bottom border
     * @param level    Depth level
     * @param maxDepth Maximum depth
     * @param xOffset  xOffset of the bounding box
     * @author Simon Reisinger
     */
    public Box(Bitmap image, int left, int top, int right, int bottom, int level, int maxDepth, int xOffset) {
        this(image, left, top, right, bottom, level, maxDepth);
        this.xOffset = xOffset;
    }

    /**
     * Initializes a new Box
     *
     * @param image    Image
     * @param left     Left border
     * @param top      Top border
     * @param right    Right border
     * @param bottom   Bottom border
     * @param level    Depth level
     * @param maxDepth Maximum depth
     * @author Simon Reisinger
     */
    public Box(Bitmap image, int left, int top, int right, int bottom, int level, int maxDepth) {
        this.level = level;
        boundingbox = new Rect(left, top, right, bottom);
        content = null;
        if (maxDepth > level) {
            content = new Box[4];
            int lvl = level + 1;
            content[0] = new Box(image, left, top, left + (right - left) / 2, top + (bottom - top) / 2, lvl, maxDepth); // top left
            content[1] = new Box(image, left + (right - left) / 2, top, right, top + (bottom - top) / 2, lvl, maxDepth); // top right
            content[2] = new Box(image, left, top + (bottom - top) / 2, left + (right - left) / 2, bottom, lvl, maxDepth); // bottom left
            content[3] = new Box(image, left + (right - left) / 2, top + (bottom - top) / 2, right, bottom, lvl, maxDepth); // bottom right
            empty = true;
            for (Box i : content) {
                empty = !empty ? empty : i.isEmpty();
            }
            if (empty) {
                content = null;
            } else {
            }
        } else {
            empty = true;
            for (int i = left; i <= right && empty; i++) {
                for (int j = top; j <= bottom && empty; j++) {
                    if (i >= image.getWidth() || j >= image.getHeight()) {
                        System.out.println("x");
                    }
                    int pixelAlpha = image.getPixel(i, j);
                    empty = (Color.alpha(pixelAlpha) == 0);
                }
            }
        }
    }

    /**
     * Intersects the box with a other one
     *
     * @param box Other box
     * @param x   X coordinate difference
     * @param y   Y coordinate difference
     * @return Intersected
     * @author Simon Reisinger
     */
    public boolean intersect(Box box, int x, int y) {
        Rect actorBox = box.boundingbox;
        Rect currentBox = new Rect(boundingbox);
        shiftBox(currentBox, x, y);
        boolean intersection = false;
        if (Rect.intersects(actorBox, currentBox)) {
            if (content != null) {
                for (int i = 0; i < 4 && !intersection; i++) {
                    if (!(content[i].isEmpty())) {
                        if (box.content != null) {
                            for (int j = 0; j < 4 && !intersection; j++) {
                                if (!box.content[j].isEmpty()) {
                                    intersection = content[i].intersect(box.content[j], x, y);
                                }
                            }
                        } else {
                            intersection = content[i].intersect(box, x, y);
                        }
                    }
                }
            } else {
                if (box.content != null) {
                    for (int j = 0; j < 4 && !intersection; j++) {
                        if (!box.content[j].isEmpty()) {
                            intersection = this.intersect(box.content[j], x, y);
                        }
                    }
                } else {
                    intersection = !this.isEmpty() && !box.isEmpty();
                }
            }
        }
        return intersection;
    }

    /**
     * Returns true if this part of the Image is Empty (alpha == 0)
     *
     * @return Returns true if this part of the Image does not contain any colored pixels
     * @author Simon Reisinger
     */
    public boolean isEmpty() {
        return empty;
    }

    /**
     * Shifts the box
     *
     * @param rect Rectangle to shift
     * @param x    X value of shifting
     * @param y    Y value of shifting
     * @author Simon Reisinger
     */
    public static void shiftBox(Rect rect, int x, int y) {
        rect.left += x;
        rect.right += x;
        rect.top += y;
        rect.bottom += y;
    }

    /**
     * Returns the x offset
     *
     * @return X offset
     * @author Simon Reisinger
     */
    public int getXOffset() {
        return xOffset;
    }
}
