package bricker.brick_strategies;

import bricker.gameobjects.Brick;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.Sound;
import danogl.gui.SoundReader;
import danogl.util.Counter;

/**
 * Exploding brick strategy class.
 */
public class ExplodingBrickStrategy implements CollisionStrategy {

    // Class constants.
    private static final String EXPLOSION_SOUND_PATH = "assets/explosion.wav";

    // Member variables.
    private final GameObjectCollection gameObjects;
    private final Counter bricksCounter;
    private final Sound explosionSound;


    /**
     * Constructor.
     * @param gameObjects game objects.
     * @param bricksCounter bricks counter.
     * @param soundReader sound renderer.
     */
    public ExplodingBrickStrategy(GameObjectCollection gameObjects,
                                  Counter bricksCounter,
                                  SoundReader soundReader) {
        this.gameObjects = gameObjects;
        this.bricksCounter = bricksCounter;
        this.explosionSound = soundReader.readSound(EXPLOSION_SOUND_PATH);
    }

    /**
     * Overrides onCollision method.
     * @param brickObj first object in collision.
     * @param other second object in collision.
     */
    @Override
    public void onCollision(GameObject brickObj, GameObject other) {
        if (!(brickObj instanceof Brick)) {
            // Fallback – behave like a simple brick
            if (gameObjects.removeGameObject(brickObj, Layer.STATIC_OBJECTS)) {
                bricksCounter.decrement();
            }
            return;
        }

        explodeBrick((Brick) brickObj, other);
    }

    /**
     * Brick explosion method.
     * @param centerBrick the brick that explodes.
     * @param trigger the object that collided with
     * the exploding brick.
     */
    private void explodeBrick(Brick centerBrick, GameObject trigger) {
        // If this brick is already gone, don't explode it again
        if (!gameObjects.removeGameObject(centerBrick, Layer.STATIC_OBJECTS)) {
            return;
        }

        bricksCounter.decrement();
        explosionSound.play();

        int row = centerBrick.getRow();
        int col = centerBrick.getCol();

        // Collect neighbours BEFORE triggering them.
        Brick[] neighbours = new Brick[4];
        int neighbourCount = 0;

        for (GameObject obj : gameObjects) {
            if (!(obj instanceof Brick)) {
                continue;
            }

            Brick brick = (Brick) obj;
            int brickRow = brick.getRow();
            int brickCol = brick.getCol();

            int difference = Math.abs(brickRow - row) + Math.abs(brickCol - col);
            if (difference == 1) { // up, down, left, right
                neighbours[neighbourCount] = brick;
                neighbourCount++;
            }
        }

        for (int k = 0; k < neighbourCount; k++) {
            Brick neighbour = neighbours[k];
            neighbour.getCollisionStrategy().onCollision(neighbour, trigger);
        }
    }
}
