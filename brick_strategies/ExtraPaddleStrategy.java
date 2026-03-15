package bricker.brick_strategies;

import bricker.gameobjects.ExtraPaddle;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;

/**
 * Extra paddle strategy class.
 */
public class ExtraPaddleStrategy implements CollisionStrategy {

    // Class constants.
    private static final String PADDLE_IMAGE_PATH = "assets/paddle.png";
    private static final Vector2 PADDLE_SIZE = new Vector2(100, 15);
    private static final float X_AXIS_FACTOR = 2f;
    private static final float Y_AXIS_FACTOR = 2f;
    private static final float CENTRE_FACTOR = 0.5f;

    // Member variables.
    private final GameObjectCollection gameObjects;
    private final ImageReader imageReader;
    private final SoundReader soundReader;
    private final Counter bricksCounter;
    private final UserInputListener inputListener;
    private final Vector2 windowDimensions;


    /**
     * Constructor
     * @param gameObjects game objects.
     * @param imageReader image renderer.
     * @param soundReader sound renderer.
     * @param bricksCounter bricks counter.
     * @param inputListener input listener for when user presses
     * a key.
     * @param windowDimensions window dimensions.
     */
    public ExtraPaddleStrategy(GameObjectCollection gameObjects,
                               ImageReader imageReader,
                               SoundReader soundReader,
                               Counter bricksCounter,
                               UserInputListener inputListener,
                               Vector2 windowDimensions) {
        this.gameObjects = gameObjects;
        this.imageReader = imageReader;
        this.soundReader = soundReader;
        this.bricksCounter = bricksCounter;
        this.inputListener = inputListener;
        this.windowDimensions = windowDimensions;
    }

    /**
     * Overrides the onCollision method.
     * @param brick first object in collision.
     * @param other second object in collision.
     */
    @Override
    public void onCollision(GameObject brick, GameObject other) {
        // Remove the brick and decrement the brick counter
        if (gameObjects.removeGameObject(brick, Layer.STATIC_OBJECTS)) {
            bricksCounter.decrement();
        }

        // If there is already an extra paddle, do NOT create another one
        if (ExtraPaddle.isExtraPaddleActive()) {
            return;
        }

        // Create a new extra paddle:
        // Same size and image as regular paddle,
        Renderable paddleImage = imageReader.readImage(PADDLE_IMAGE_PATH,
                false);
        float centerX = windowDimensions.x() / X_AXIS_FACTOR;
        float centerY = windowDimensions.y() / Y_AXIS_FACTOR;
        Vector2 center = new Vector2(centerX, centerY);
        Vector2 topLeft = center.subtract(PADDLE_SIZE.mult(CENTRE_FACTOR));

        ExtraPaddle extraPaddle = new ExtraPaddle(
                topLeft,
                PADDLE_SIZE,
                paddleImage,
                inputListener,
                windowDimensions,
                gameObjects
        );

        gameObjects.addGameObject(extraPaddle, Layer.DEFAULT);
    }
}
