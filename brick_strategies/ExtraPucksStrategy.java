package bricker.brick_strategies;

import bricker.gameobjects.Puck;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.Sound;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import java.util.Random;

/**
 * Extra puck strategy class.
 */
public class ExtraPucksStrategy implements CollisionStrategy {

    // Class constants.
    private static final String PUCK_IMAGE_PATH = "assets/mockBall.png";
    private static final String PUCK_SOUND_PATH = "assets/blop.wav";
    private static final int PUCK_X_DIMENSION = 15;
    private static final int PUCK_Y_DIMENSION = 15;
    private static final float CENTER_FACTOR = 0.5f;
    private static final int NUM_OF_PUCKS = 2;

    // Member variables.
    private final GameObjectCollection gameObjects;     // from BrickerGameManager.gameObjects()
    private final ImageReader imageReader;
    private final SoundReader soundReader;
    private Counter bricksCounter;
    private final float windowHeight;

    private final Random random = new Random();


    /**
     * Constructor.
     * @param gameObjects game objects.
     * @param imageReader image renderer.
     * @param soundReader sound reader.
     * @param bricksCounter bricks counter.
     * @param windowHeight window height.
     */
    public ExtraPucksStrategy(GameObjectCollection gameObjects,
                              ImageReader imageReader, SoundReader soundReader,
                              Counter bricksCounter, float windowHeight) {
        this.gameObjects = gameObjects;
        this.imageReader = imageReader;
        this.soundReader = soundReader;
        this.bricksCounter = bricksCounter;
        this.windowHeight = windowHeight;

    }

    /**
     * Overrides the onCollision method.
     * @param brick first object in collision.
     * @param other second object in collision.
     */
    @Override
    public void onCollision(GameObject brick, GameObject other) {

        Vector2 puckSize = new Vector2(PUCK_X_DIMENSION, PUCK_Y_DIMENSION);

        if (gameObjects.removeGameObject(brick, Layer.STATIC_OBJECTS)) {
            bricksCounter.decrement();
        }

        Renderable puckImage = imageReader.readImage(PUCK_IMAGE_PATH, true);
        Sound collisionSound = soundReader.readSound(PUCK_SOUND_PATH);

        Vector2 center = brick.getCenter();
        Vector2 puckTopLeft = center.subtract(puckSize.mult(CENTER_FACTOR));

        for (int i = 0; i < NUM_OF_PUCKS; i++) {
            Puck puck = new Puck(
                    puckTopLeft,
                    puckSize,
                    puckImage,
                    collisionSound,
                    gameObjects,
                    windowHeight
            );

            double angle = random.nextDouble() * Math.PI;
            float xDirection = (float) (Math.cos(angle) * 100);
            float yDirection = (float) (Math.sin(angle) * 100);
            Vector2 velocity = new Vector2(xDirection, yDirection);
            puck.setVelocity(velocity);

            gameObjects.addGameObject(puck, Layer.DEFAULT);
        }
    }
}
