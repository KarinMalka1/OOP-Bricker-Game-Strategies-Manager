package bricker.main;

import bricker.brick_strategies.*;
import bricker.gameobjects.Ball;
import bricker.gameobjects.Brick;
import bricker.gameobjects.Paddle;
import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.*;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Counter;
import danogl.util.Vector2;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

/**
 * The game manager class which controls the game.
 */
public class BrickerGameManager extends GameManager {

    // Class constants.
    private static final int WALL_THICKNESS = 6;
    private static final int DEFAULT_BRICKS_IN_ROW = 8;
    private static final int DEFAULT_BRICK_ROWS = 7;
    private static final float BRICK_HEIGHT = 15f;
    private static final float HORIZONTAL_GAP = 5f;
    private static final float VERTICAL_GAP = 5f;
    private static final int MAX_LIVES = 4;
    private static final int INITIAL_LIVES = 3;
    private static final int TARGET_FRAME = 80;
    private static final String BALL_IMAGE = "assets/ball.png" ;
    private static final String COLLISION_SOUND = "assets/blop.wav";
    private static final int BALL_X_DIMENSION = 20;
    private static final int BALL_Y_DIMENSION = 20;
    private static final float CENTER_FACTOR = 0.5f;
    private static final float BALL_SPEED = 200f;
    private static final int DIRECTION_CHANGE = -1;
    private static final String PADDLE_IMAGE = "assets/paddle.png";
    private static final int PADDLE_X_DIMENSION = 100;
    private static final int PADDLE_Y_DIMENSION = 15;
    private static final float PADDLE_CENTER_X_FACTOR = 2f;
    private static final float PADDLE_CENTER_Y_FACTOR = 30;
    private static final String MAIN_PADDLE_TAG = "main_paddle";
    private static final String BRICK_IMAGE = "assets/brick.png";
    private static final int TWO_WALLS = 2;
    private static final int NUM_ONE = 1;
    private static final int DISTANCE_FROM_ROOF = 10;
    private static final String BACKGROUND_IMAGE = "assets/DARK_BG2_small.jpeg";
    private static final String HEART_IMAGE = "assets/heart.png";
    private static final float HEART_SIZE = 25f;
    private static final float X_COORDINATE = 10f;
    private static final float TEXT_HEIGHT = 30f;
    private static final float TEXT_WIDTH = 30f;
    private static final float GAP_BETWEEN_HEARTS = 5f;
    private static final int THREE_LIVES = 3;
    private static final float TWO_LIVES = 2;
    private static final float ONE_LIFE = 1;
    private static final String LOSS_MESSAGE = "You lose! Play again?";
    private static final String WIN_MESSAGE = "You win! Play again?";
    private static final int SCREEN_WIDTH = 700;
    private static final int SCREEN_HEIGHT = 500;
    private static final int TWO_ARGUMENTS = 2;
    private static final String WINDOW_TITLE = "Bouncing Ball";

    // Member variables.
    private Ball ball;

    private final int bricksInRow;
    private final int brickRows;
    private Counter bricksCounter;
    private CollisionStrategyFactory collisionStrategyFactory;
    private Vector2 windowDimensions;
    private WindowController windowController;
    private UserInputListener inputListener;

    private int livesLeft = INITIAL_LIVES;
    private GameObject[] heartObjects;
    private boolean[] heartInGame;
    private TextRenderable livesTextRenderable;
    private GameObject livesTextObject;
    private Renderable heartImage;

    private final Random random = new Random();
    private boolean gameEnded = false;


    /**
     * Constructor with default brick layout.
     * @param windowTitle the window title.
     * @param windowDimensions the window dimensions.
     */
    public BrickerGameManager(String windowTitle, Vector2 windowDimensions) {
        this(windowTitle, windowDimensions, DEFAULT_BRICKS_IN_ROW,
                DEFAULT_BRICK_ROWS);
    }

    /**
     * Constructor with custom brick layout.
     * @param windowTitle the window title.
     * @param windowDimensions the window dimensions.
     * @param bricksInRow the number of bricks in a row.
     * @param brickRows the number of rows.
     */
    public BrickerGameManager(String windowTitle, Vector2 windowDimensions,
                              int bricksInRow, int brickRows) {
        super(windowTitle, windowDimensions);
        this.bricksInRow = bricksInRow;
        this.brickRows = brickRows;
    }

    /**
     * Initializes the game.
     * @param imageReader Contains a single method: readImage, which reads an image from disk.
     *                 See its documentation for help.
     * @param soundReader Contains a single method: readSound, which reads a wav file from
     *                    disk. See its documentation for help.
     * @param inputListener Contains a single method: isKeyPressed, which returns whether
     *                      a given key is currently pressed by the user or not. See its
     *                      documentation.
     * @param windowController Contains an array of helpful, self explanatory methods
     *                         concerning the window.
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener,
                               WindowController windowController) {
        // Initialization
        super.initializeGame(imageReader, soundReader, inputListener,
                windowController);
        this.windowController = windowController;
        this.inputListener = inputListener;
        windowController.setTargetFramerate(TARGET_FRAME);
        this.windowDimensions = windowController.getWindowDimensions();

        // Reset game
        livesLeft = INITIAL_LIVES;
        gameEnded = false;

        // brick counter
        bricksCounter = new Counter(0);

        // collision strategy gets gameObjects + brick counter
        collisionStrategyFactory = new CollisionStrategyFactory(
                gameObjects(), imageReader,
                soundReader, inputListener,
                windowDimensions, this,
                bricksCounter);


        // Create game objects via helper functions
        ball = (Ball) createBall(imageReader, soundReader, windowDimensions);
        GameObject userPaddle = createUserPaddle(imageReader, inputListener,
                windowDimensions);
        createBricks(imageReader, windowDimensions);
        createWalls(windowDimensions);
        createBackground(imageReader, windowController);

        initLifeDisplays(imageReader);
        updateLifeDisplays();
    }

    /**
     * Updates the game.
     * @param deltaTime The time, in seconds, that passed since the last invocation
     *                  of this method (i.e., since the last frame). This is useful
     *                  for either accumulating the total time that passed since some
     *                  event, or for physics integration (i.e., multiply this by
     *                  the acceleration to get an estimate of the added velocity or
     *                  by the velocity to get an estimate of the difference in position).
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        checkForLossCondition();
        checkForWinCondition();
        checkForCheatWin();
    }

    /* Creates the ball, centers it, sets velocity, and adds it to the game objects. */
    private GameObject createBall(ImageReader imageReader,
                                  SoundReader soundReader,
                                  Vector2 windowDimensions) {
        Renderable ballImage = imageReader.readImage(BALL_IMAGE,
                true);
        Sound collisionSound = soundReader.readSound(
                COLLISION_SOUND);

        Ball ball = new Ball(
                Vector2.ZERO,
                new Vector2(BALL_X_DIMENSION, BALL_Y_DIMENSION),
                ballImage,
                collisionSound
        );

        resetBallVelocity(ball);
        ball.setCenter(windowDimensions.mult(CENTER_FACTOR));

        // Default layer so it collides with paddles and static walls/bricks
        gameObjects().addGameObject(ball);

        return ball;
    }

    /* Resets the ball velocity. */
    private void resetBallVelocity(Ball ball) {
        float speed = BALL_SPEED;
        float velX = speed;
        float velY = speed;

        if (random.nextBoolean()) {
            velX *= DIRECTION_CHANGE;
        }
        if (random.nextBoolean()) {
            velY *= DIRECTION_CHANGE;
        }
        ball.setVelocity(new Vector2(velX, velY));
    }

    /* Creates the user-controlled paddle at the bottom and
     * adds it to the game objects. */
    private GameObject createUserPaddle(ImageReader imageReader,
                                        UserInputListener inputListener,
                                        Vector2 windowDimensions) {
        Renderable paddleImage = imageReader.readImage(
                PADDLE_IMAGE, false);

        GameObject userPaddle = new Paddle(
                Vector2.ZERO,
                new Vector2(PADDLE_X_DIMENSION, PADDLE_Y_DIMENSION),
                paddleImage,
                inputListener,
                windowDimensions
        );

        userPaddle.setCenter(new Vector2(windowDimensions.x() /
                PADDLE_CENTER_X_FACTOR,
                windowDimensions.y() - PADDLE_CENTER_Y_FACTOR));

        userPaddle.setTag(MAIN_PADDLE_TAG);

        gameObjects().addGameObject(userPaddle);

        return userPaddle;
    }

    /* Creates a grid of bricks at the top and adds them to the STATIC_OBJECTS layer. */
    private void createBricks(ImageReader imageReader,
                              Vector2 windowDimensions) {
        Renderable brickImage = imageReader.readImage(
                BRICK_IMAGE, false);

        float windowWidth = windowDimensions.x();

        float totalHorizontalGaps = (bricksInRow + NUM_ONE) * HORIZONTAL_GAP;
        float availableWidth = windowWidth - TWO_WALLS * WALL_THICKNESS
                - totalHorizontalGaps;
        float brickWidth = availableWidth / bricksInRow;

        for (int row = 0; row < brickRows; row++) {
            for (int col = 0; col < bricksInRow; col++) {

                float x = WALL_THICKNESS + HORIZONTAL_GAP + col *
                        (brickWidth + HORIZONTAL_GAP);
                float y = WALL_THICKNESS + DISTANCE_FROM_ROOF + row * (BRICK_HEIGHT + VERTICAL_GAP);
                Vector2 topLeftCorner = new Vector2(x, y);

                CollisionStrategy strategy = collisionStrategyFactory.
                        createStrategyForBrick();

                GameObject brick = new Brick(
                        topLeftCorner,
                        new Vector2(brickWidth, BRICK_HEIGHT),
                        brickImage,
                        strategy,
                        row,
                        col
                );

                gameObjects().addGameObject(brick, Layer.STATIC_OBJECTS);
                bricksCounter.increment();

            }
        }
    }

    /* Creates left, right, and top walls and adds them to the STATIC_OBJECTS layer. */
    private void createWalls(Vector2 windowDimensions) {
        float windowWidth = windowDimensions.x();
        float windowHeight = windowDimensions.y();

        RectangleRenderable wallRenderable = new RectangleRenderable(Color.green);

        GameObject leftWall = new GameObject(
                Vector2.ZERO,
                new Vector2(WALL_THICKNESS, windowHeight),
                wallRenderable
        );

        GameObject roof = new GameObject(
                Vector2.ZERO,
                new Vector2(windowWidth, WALL_THICKNESS),
                wallRenderable
        );

        GameObject rightWall = new GameObject(
                new Vector2(windowWidth - WALL_THICKNESS, 0),
                new Vector2(WALL_THICKNESS, windowHeight),
                wallRenderable
        );

        gameObjects().addGameObject(leftWall, Layer.STATIC_OBJECTS);
        gameObjects().addGameObject(roof, Layer.STATIC_OBJECTS);
        gameObjects().addGameObject(rightWall, Layer.STATIC_OBJECTS);
    }

    /* Creates the background, sets it to camera coordinates,
     *  and adds it to BACKGROUND layer. */
    private void createBackground(ImageReader imageReader,
                                  WindowController windowController) {
        Renderable bgImage =
                imageReader.readImage(BACKGROUND_IMAGE, false);

        GameObject background = new GameObject(
                Vector2.ZERO,
                windowController.getWindowDimensions(),
                bgImage
        );

        background.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects().addGameObject(background, Layer.BACKGROUND);
    }


    /* Initialize hearts and numeric lives display (UI layer). */
    private void initLifeDisplays(ImageReader imageReader) {
        heartImage = imageReader.readImage(HEART_IMAGE, true);
        heartObjects = new GameObject[MAX_LIVES];
        heartInGame = new boolean[MAX_LIVES];

        float heartSize = HEART_SIZE;
        float startX = X_COORDINATE;

        // numeric display at the bottom-left
        livesTextRenderable = new TextRenderable(Integer.toString(livesLeft));
        livesTextRenderable.setColor(Color.GREEN);

        float textHeight = TEXT_HEIGHT;
        float windowHeight = windowDimensions.y();

        // number sits at the bottom, above the margin
        Vector2 textPos = new Vector2(
                startX,
                windowHeight - startX - textHeight
        );

        livesTextObject = new GameObject(
                textPos,
                new Vector2(TEXT_WIDTH, textHeight),
                livesTextRenderable
        );
        gameObjects().addGameObject(livesTextObject, Layer.UI);

        // hearts row ABOVE the number
        float heartsY = textPos.y() - heartSize;

        for (int i = 0; i < MAX_LIVES; i++) {
            Vector2 topLeft = new Vector2(startX + i * (heartSize +
                    GAP_BETWEEN_HEARTS), heartsY);
            GameObject heart = new GameObject(
                    topLeft,
                    new Vector2(heartSize, heartSize),
                    heartImage
            );
            heartObjects[i] = heart;
            heartInGame[i] = true;
            gameObjects().addGameObject(heart, Layer.UI);
        }
    }


    /* Update hearts visibility and numeric text/color according to livesLeft. */
    private void updateLifeDisplays() {
        for (int i = 0; i < MAX_LIVES; i++) {
            GameObject heart = heartObjects[i];

            if (i < livesLeft) {
                if (!heartInGame[i]) {
                    gameObjects().addGameObject(heart, Layer.UI);
                    heartInGame[i] = true;
                }
            } else {
                if (heartInGame[i]) {
                    gameObjects().removeGameObject(heart, Layer.UI);
                    heartInGame[i] = false;
                }
            }
        }


        // Update numeric text
        livesTextRenderable.setString(Integer.toString(livesLeft));

        // Update color:
        if (livesLeft >= THREE_LIVES) {
            livesTextRenderable.setColor(Color.GREEN);
        } else if (livesLeft == TWO_LIVES) {
            livesTextRenderable.setColor(Color.YELLOW);
        } else if (livesLeft == ONE_LIFE) {
            livesTextRenderable.setColor(Color.RED);
        }
    }

    /* Check if the ball fell below the screen; handle life loss or game over. */
    private void checkForLossCondition() {
        if (ball == null) {
            return;
        }

        float ballY = ball.getCenter().y();
        float windowHeight = windowDimensions.y();

        if (ballY > windowHeight) {
            // ball fell
            livesLeft--;

            if (livesLeft <= 0) {
                showGameOverDialog();
                gameEnded = true;
            } else {
                // reset ball to center and random diagonal
                ball.setCenter(windowDimensions.mult(CENTER_FACTOR));
                resetBallVelocity(ball);
                updateLifeDisplays();
            }
        }
    }

    /* Checks if the game is won. */
    private void checkForWinCondition() {
        if (bricksCounter.value() <= 0) {
            gameEnded = true;
            showWinDialog();
        }
    }

    /* Pressing 'W' triggers win. */
    private void checkForCheatWin() {
        if (inputListener.isKeyPressed(KeyEvent.VK_W)) {
            gameEnded = true;
            showWinDialog();
        }
    }


    /* Shows game is over message. */
    private void showGameOverDialog() {
        String prompt = LOSS_MESSAGE;
        boolean playAgain = windowController.openYesNoDialog(prompt);

        if (playAgain) {
            windowController.resetGame();
        } else {
            windowController.closeWindow();
        }
    }

    /* Shows game is won message */
    private void showWinDialog() {
        String prompt = WIN_MESSAGE;
        boolean playAgain = windowController.openYesNoDialog(prompt);

        if (playAgain) {
            windowController.resetGame();
        } else {
            windowController.closeWindow();
        }
    }

    /**
     * A life is gained by catching a heart.
     */
    public void gainLife() {
        if (livesLeft < MAX_LIVES) {
            livesLeft++;
            updateLifeDisplays();
        }
    }


    /**
     * The main function.
     * @param args The number of rows and columns can be
     * entered as arguments if not the default number will
     * be taken.
     */
    public static void main(String[] args) {
        Vector2 windowDimensions = new Vector2(SCREEN_WIDTH,
                SCREEN_HEIGHT);
        BrickerGameManager game;

        if (args.length == TWO_ARGUMENTS) {
            int bricksInRow = Integer.parseInt(args[0]);
            int brickRows = Integer.parseInt(args[1]);
            game = new BrickerGameManager(WINDOW_TITLE,
                    windowDimensions, bricksInRow, brickRows);
        } else {
            // Default: 8 bricks in a row, 7 rows
            game = new BrickerGameManager(WINDOW_TITLE,
                    windowDimensions);
        }

        game.run();
    }
}
