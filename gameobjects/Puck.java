package bricker.gameobjects;

import danogl.collisions.GameObjectCollection;
import danogl.gui.Sound;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Puck class.
 */
public class Puck extends Ball {

    // Member variables.
    private final GameObjectCollection gameObjects;
    private final float windowHeight;

    /**
     * Puck constructor.
     * @param topLeftCorner top left corner.
     * @param dimensions dimensions.
     * @param renderable renderable.
     * @param collisionSound collision sound.
     * @param gameObjects game objects.
     * @param windowHeight window height.
     */
    public Puck(Vector2 topLeftCorner,
                Vector2 dimensions,
                Renderable renderable,
                Sound collisionSound,
                GameObjectCollection gameObjects,
                float windowHeight) {
        super(topLeftCorner, dimensions, renderable, collisionSound);
        this.gameObjects = gameObjects;
        this.windowHeight = windowHeight;
    }

    /**
     * Updates the game.
     * @param deltaTime The time elapsed, in seconds, since the last frame. Can
     *                  be used to determine a new position/velocity by multiplying
     *                  this delta with the velocity/acceleration respectively
     *                  and adding to the position/velocity:
     *                  velocity += deltaTime*acceleration
     *                  pos += deltaTime*velocity
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        // If the puck leaves the bottom of the screen, remove it.
        // Important: this does NOT change lives – only removes the puck.
        if (getCenter().y() > windowHeight) {
            gameObjects.removeGameObject(this);
        }
    }
}
