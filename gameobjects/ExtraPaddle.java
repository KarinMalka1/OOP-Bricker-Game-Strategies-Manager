package bricker.gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Extra user paddle class.
 */
public class ExtraPaddle extends Paddle {

    // Class constants.
    private static final int MAX_HITS = 4;
    private static int activeExtraPaddles = 0;

    // Member variables
    private final GameObjectCollection gameObjects;
    private int hitCount = 0;

    /**
     * Extra user paddle constructor.
     * @param topLeftCorner top left corner.
     * @param dimensions dimensions.
     * @param renderable renderable.
     * @param inputListener input listener to detect when
     * the user presses a key.
     * @param windowDimensions window dimensions.
     * @param gameObjects game objects.
     */
    public ExtraPaddle(Vector2 topLeftCorner,
                       Vector2 dimensions,
                       Renderable renderable,
                       UserInputListener inputListener,
                       Vector2 windowDimensions,
                       GameObjectCollection gameObjects) {
        super(topLeftCorner, dimensions, renderable, inputListener,  windowDimensions);
        this.gameObjects = gameObjects;
        activeExtraPaddles++;
    }

    /**
     * Overrides the onCOliisionEnter method.
     * @param other The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        hitCount++;
        if (hitCount >= MAX_HITS) {
            gameObjects.removeGameObject(this);
            activeExtraPaddles--;
        }
    }

    /**
     * Tells us if the extra paddle is active.
     * @return true if the extra paddle is active and
     * false otherwise.
     */
    public static boolean isExtraPaddleActive() {
        return activeExtraPaddles > 0;
    }
}
