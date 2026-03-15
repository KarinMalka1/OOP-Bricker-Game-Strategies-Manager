package bricker.brick_strategies;

import bricker.main.BrickerGameManager;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.util.Counter;
import danogl.util.Vector2;
import java.util.Random;

/**
 * The factory which assigns the collision strategy to each brick.
 */
public class CollisionStrategyFactory {

    // Member variables.
    private final GameObjectCollection gameObjects;
    private final ImageReader imageReader;
    private final SoundReader soundReader;
    private final UserInputListener inputListener;
    private final Vector2 windowDimensions;
    private final BrickerGameManager gameManager;
    private final Counter bricksCounter;
    private final Random random;

    /**
     * Constructor.
     * @param gameObjects game objects.
     * @param imageReader image renderer.
     * @param soundReader sound renderer.
     * @param inputListener input listener for when user
     * presses a key.
     * @param windowDimensions window dimensions.
     * @param gameManager game manager
     * @param bricksCounter bricks counter.
     */
    public CollisionStrategyFactory(GameObjectCollection gameObjects,
                                    ImageReader imageReader,
                                    SoundReader soundReader,
                                    UserInputListener inputListener,
                                    Vector2 windowDimensions,
                                    BrickerGameManager gameManager,
                                    Counter bricksCounter) {
        this.gameObjects = gameObjects;
        this.imageReader = imageReader;
        this.soundReader = soundReader;
        this.inputListener = inputListener;
        this.windowDimensions = windowDimensions;
        this.gameManager = gameManager;
        this.bricksCounter = bricksCounter;
        this.random = new Random();
    }

    /**
     * creates the strategy for the brick.
     * @return a collision strategy.
     */
    public CollisionStrategy createStrategyForBrick() {
        // shared base so removal of brick happens only once
        CollisionStrategy basic = new BasicCollisionStrategy(gameObjects, bricksCounter);

        int randomStrategy = random.nextInt(10);

        // 0-4 => basic
        if (randomStrategy < 5) {
            return basic;
        }

        // 9 => double behaviour
        if (randomStrategy == 9) {
            return createDoubleStrategy();
        }

        // 5,6,7,8 => four different single special behaviours
        int specialIndex = randomStrategy - 5;
        return createSpecificSpecial(specialIndex);
    }

    /*
    Creates the double strategy.
     */
    private CollisionStrategy createDoubleStrategy() {

        CollisionStrategy firstSpecial  = createRandomNonDoubleSpecial();
        CollisionStrategy secondSpecial = createRandomNonDoubleSpecial();

        CollisionStrategy combined = new DoubleCollisionStrategy(firstSpecial, secondSpecial);

        // 50% chance to add a third special behaviour
        if (random.nextBoolean()) {
            CollisionStrategy thirdSpecial = createRandomNonDoubleSpecial();
            combined = new DoubleCollisionStrategy(combined, thirdSpecial);
        }

        return combined;
    }

    /*
    Creates the random non double special collision.
     */
    private CollisionStrategy createRandomNonDoubleSpecial() {
        int which = random.nextInt(4);
        return createSpecificSpecial(which);
    }

    /*
    Creates one of four special collisions
     */
    private CollisionStrategy createSpecificSpecial(int index) {
        switch (index) {
            case 0:
                // “extra balls” behaviour
                return new ExtraPucksStrategy(
                        gameObjects,
                        imageReader,
                        soundReader,
                        bricksCounter,
                        windowDimensions.y());

            case 1:
                // extra paddle behaviour
                return new ExtraPaddleStrategy(
                        gameObjects, imageReader,soundReader,
                        bricksCounter, inputListener, windowDimensions);

            case 2:
                // exploding brick behaviour
                return new ExplodingBrickStrategy(
                        gameObjects, bricksCounter, soundReader);

            default:
                // return-life / falling heart behaviour
                return new ReturnLifeStrategy(
                        gameObjects, imageReader,bricksCounter,
                        windowDimensions, gameManager);
        }
    }
}