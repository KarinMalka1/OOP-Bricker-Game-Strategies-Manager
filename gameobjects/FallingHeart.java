package bricker.gameobjects;

import bricker.main.BrickerGameManager;
import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Falling heart class.
 */
public class FallingHeart extends GameObject {

    // Class constants.
    private static final float FALL_SPEED = 100f;
    private static final String FALLING_HEART_TAG = "falling_heart";
    private static final String MAIN_PADDLE = "main_paddle";

    // Member variables.
    private final GameObjectCollection gameObjects;
    private final Vector2 windowDimensions;
    private final BrickerGameManager brickerGameManager;

    /**
     * Falling heart constructor.
     * @param topLeftCorner top left corner.
     * @param dimensions dimensions.
     * @param renderable renderable.
     * @param gameObjects game objects.
     * @param windowDimensions window dimensions.
     * @param gameManager game manager.
     */
    public FallingHeart(Vector2 topLeftCorner,
                        Vector2 dimensions,
                        Renderable renderable,
                        GameObjectCollection gameObjects,
                        Vector2 windowDimensions,
                        BrickerGameManager gameManager) {
        super(topLeftCorner, dimensions, renderable);
        this.gameObjects = gameObjects;
        this.windowDimensions = windowDimensions;
        this.brickerGameManager = gameManager;

        setVelocity(new Vector2(0, FALL_SPEED));  // fall straight down
        setTag(FALLING_HEART_TAG);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        // If the heart left the screen, remove it
        if (getTopLeftCorner().y() > windowDimensions.y()) {
            gameObjects.removeGameObject(this);
        }
    }

    /**
     * Only collide with the original paddle.
     * @param other The other GameObject.
     * @return true for main paddle and false otherwise.
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        return MAIN_PADDLE.equals(other.getTag());
    }

    /**
     * Overrides onCollisionEnter.
     * @param other The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        // We know other is the main paddle because of shouldCollideWith
        brickerGameManager.gainLife();
        gameObjects.removeGameObject(this);
    }
}


