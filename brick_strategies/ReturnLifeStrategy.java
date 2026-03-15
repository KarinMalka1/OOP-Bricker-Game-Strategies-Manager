package bricker.brick_strategies;

import bricker.gameobjects.FallingHeart;
import bricker.main.BrickerGameManager;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;

/**
 * Return a life strategy class.
 */
public class ReturnLifeStrategy implements CollisionStrategy{

    // Class constants.
    private static final String HEART_IMAGE_PATH = "assets/heart.png";
    private static final float HEART_SIZE = 25f;
    private static final float CENTER_FACTOR = 0.5f;

    // Member variables.
    private final GameObjectCollection gameObjects;
    private final ImageReader imageReader;
    private final Counter bricksCounter;
    private final Vector2 windowDimensions;
    private final BrickerGameManager gameManager;

    /**
     * Constructor.
     * @param gameObjects game objects.
     * @param imageReader image reader.
     * @param bricksCounter bricks counter.
     * @param windowDimensions window dimensions.
     * @param gameManager game manager.
     */
    public ReturnLifeStrategy(GameObjectCollection gameObjects,
                              ImageReader imageReader,
                              Counter bricksCounter,
                              Vector2 windowDimensions,
                              BrickerGameManager gameManager) {
        this.gameObjects = gameObjects;
        this.imageReader = imageReader;
        this.bricksCounter = bricksCounter;
        this.windowDimensions = windowDimensions;
        this.gameManager = gameManager;
    }

    /**
     * Overrides the onCollision method.
     * @param brick first object in collision.
     * @param other second object in collision.
     */
    @Override
    public void onCollision(GameObject brick, GameObject other) {

        if (gameObjects.removeGameObject(brick, Layer.STATIC_OBJECTS)) {
            bricksCounter.decrement();
        }

        Renderable heartImage = imageReader.readImage(HEART_IMAGE_PATH,
                true);
        Vector2 heartSize = new Vector2(HEART_SIZE, HEART_SIZE);

        Vector2 heartTopLeft =
                brick.getCenter().subtract(heartSize.mult(CENTER_FACTOR));

        FallingHeart heart = new FallingHeart(
                heartTopLeft,
                heartSize,
                heartImage,
                gameObjects,
                windowDimensions,
                gameManager
        );

        gameObjects.addGameObject(heart, Layer.DEFAULT);
    }
}
