package bricker.gameobjects;

import bricker.brick_strategies.CollisionStrategy;
import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Brick class.
 */
public class Brick extends GameObject {

    // Member variables.
    private final CollisionStrategy collisionStrategy;
    private int row;
    private int col;
    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     */
    public Brick(Vector2 topLeftCorner, Vector2 dimensions,
                 Renderable renderable, CollisionStrategy collisionStrategy,
                 int row, int col) {
        super(topLeftCorner, dimensions, renderable);
        this.collisionStrategy = collisionStrategy;
        this.row = row;
        this.col = col;
    }

    /**
     * Overrides the onCollisionEnter method.
     * @param other The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        collisionStrategy.onCollision(this, other);
    }

    /**
     * Getter for the number of rows.
     * @return the number of rows.
     */
    public int getRow() {
        return row;
    }

    /**
     * Getter for the number of columns.
     * @return the number of columns.
     */
    public int getCol() {
        return col;
    }

    /**
     * Getter for the collision strategy.
     * @return the collision strategy.
     */
    public CollisionStrategy getCollisionStrategy() {
        return collisionStrategy;
    }
}



