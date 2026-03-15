package bricker.brick_strategies;

import danogl.GameObject;

/**
 * Double collision strategy class.
 */
public class DoubleCollisionStrategy implements CollisionStrategy {

    // Member variables.
    private final CollisionStrategy firstStrategy;
    private final CollisionStrategy secondStrategy;

    /**
     * Constructor.
     * @param firstStrategy the first collision strategy.
     * @param secondStrategy the second collision strategy.
     */
    public DoubleCollisionStrategy(CollisionStrategy firstStrategy,
                                   CollisionStrategy secondStrategy) {
        this.firstStrategy = firstStrategy;
        this.secondStrategy = secondStrategy;
    }

    /**
     * Overrides the collision method and decides
     * what happens upon collision.
     * @param brick first object in collision.
     * @param other second object in collision.
     */
    @Override
    public void onCollision(GameObject brick, GameObject other) {
        firstStrategy.onCollision(brick, other);
        secondStrategy.onCollision(brick, other);
    }
}
