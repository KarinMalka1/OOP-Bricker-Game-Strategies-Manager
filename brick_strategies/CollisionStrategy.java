package bricker.brick_strategies;

import danogl.GameObject;

/**
 * Interface for the collision strategy.
 */
public interface CollisionStrategy {

    /**
     * Method for collision between two game objects.
     * @param object1 first object in collision.
     * @param object2 second object in collision.
     */
    public void onCollision(GameObject object1, GameObject object2);

}
