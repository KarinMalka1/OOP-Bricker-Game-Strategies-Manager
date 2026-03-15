package bricker.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.util.Counter;

/**
 * The basic collision class where no special collision happens
 * only the removal of the brick which the ball collided with.
 */
public class BasicCollisionStrategy implements CollisionStrategy {

    // Member variables.
    private final GameObjectCollection gameObjects;
    private final Counter bricksCounter;

    /**
     * Constructor.
     * @param gameObjects game objects.
     * @param bricksCounter bricks counter.
     */
    public BasicCollisionStrategy(GameObjectCollection gameObjects, Counter bricksCounter) {
        this.gameObjects = gameObjects;
        this.bricksCounter = bricksCounter;
    }


    /**
     * Overrides the onCollision method and decides what
     * happens upon collision.
     * @param object1 first object in collision.
     * @param object2 second object in collision.
     */
    @Override
    public void onCollision(GameObject object1, GameObject object2) {
        if (gameObjects.removeGameObject(object1, Layer.STATIC_OBJECTS)) {
            bricksCounter.decrement();
        }
    }

}
