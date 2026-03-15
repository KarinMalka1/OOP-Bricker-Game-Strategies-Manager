package bricker.gameobjects;

import danogl.GameObject;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;


/**
 * User paddle class.
 */
public class Paddle extends GameObject {

    // Class constants.
    private static final float MOVEMENT_SPEED = 300;
    private static final float WALL_THICKNESS = 6;

    // Member variables.
    private UserInputListener inputListener;
    private Vector2 windowDimensions;


    /**
     * User paddle constructor.
     * @param topLeftCorner top left corner.
     * @param dimensions dimnesions.
     * @param renderable renderable.
     * @param inputListener input listener for when user presses keys.
     * @param windowDimensions window dimensions.
     */
    public Paddle(Vector2 topLeftCorner, Vector2 dimensions,
                  Renderable renderable,
                  UserInputListener inputListener,
                  Vector2 windowDimensions) {
        super(topLeftCorner, dimensions, renderable);
        this.inputListener = inputListener;
        this.windowDimensions = windowDimensions;
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
        Vector2 movementDirection = Vector2.ZERO;
        if(inputListener.isKeyPressed(KeyEvent.VK_LEFT)){
            movementDirection = movementDirection.add(Vector2.LEFT);
        }
        if(inputListener.isKeyPressed(KeyEvent.VK_RIGHT)){
            movementDirection = movementDirection.add(Vector2.RIGHT);
        }
        setVelocity(movementDirection.mult(MOVEMENT_SPEED));

        Vector2 topLeft = getTopLeftCorner();
        float x = topLeft.x();
        float y = topLeft.y();

        float paddleWidth = getDimensions().x();
        float minX = WALL_THICKNESS;
        float maxX = windowDimensions.x() - WALL_THICKNESS - paddleWidth;

        if (x < minX) {
            x = minX;
        }
        if (x > maxX) {
            x = maxX;
        }

        setTopLeftCorner(new Vector2(x, y));
    }
}
