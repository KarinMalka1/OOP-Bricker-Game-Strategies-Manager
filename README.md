Q1) We chose the first option which is pass the object returned by
gameObjects() to the constructor of BasicCollisionStrategy, store
it as a field and use it inside onCollision to remove the brick.
The advantages are: 1-lower coupling BasicCollisionStrategy does
not depend on the whole BrickGameManager class. It only depends
on GameObjectCollection, which is xaclty what it needs in order
to remove objects. Making the strategy more general and easier
to remove objects. 2- No need to extend BrickerGameManager's API.
3- Fits the idea of the strategy pattern. The behaviour is
encapsulated inside CollisionStrategy. 4- Easier to test. The
disadvantagesare: 1- BrickergameManager is no longer the sole owner
of add or remove logic. 2- By giving the strategy a reference
to GameObjectCollection, it can theoretically remove any object
from the game not just bricks. 3- Harder to centralize global
logic around brick removal.
Q2) To display the player’s lives I used two elements managed
from BrickerGameManager: a graphical heart row and a numeric
counter. In BrickerGameManager I added fields for livesLeft,
a MAX_LIVES and INITIAL_LIVES constant, an array of heart GameObjects,
and a TextRenderable for the number. In initLifeDisplays I create
the hearts as UI layer GameObjects, all using the same heart
image and positioned in the bottom left corner above the numeric
display, and I store them in an array. The numeric display is another
UI GameObject that shows the current value of livesLeft. The method
updateLifeDisplays() synchronizes the visuals with the logic: it adds or
removes heart objects from the game according to livesLeft, updates the
text string, and changes its color based on how many lives remain.
Life loss is handled in checkForLossCondition() when the ball falls
below the screen, where livesLeft is decremented and updateLifeDisplays()
is called. For life gain I added a public method gainLife() in
BrickerGameManager, which increases livesLeft up to MAX_LIVES and again
calls updateLifeDisplays().
Q3) For each special behaviour we implemented a separate class that
implements CollisionStrategy, and each brick gets one of these strategies
from the CollisionStrategyFactory. Exploding brick: ExplodingBrickStrategy
removes the hit brick from the STATIC_OBJECTS layer (while decrementing the
brick counter) and then finds its 4 orthogonal neighbours using their row and
column indices. For each neighbour it calls that brick’s own collision strategy,
so exploding bricks can trigger chain reactions. Extra pucks: ExtraPucksStrategy
removes the brick and then creates additional ball-like objects (pucks) at the
brick’s center using the image/sound readers. These are added to the game objects
and move similarly to the main ball, increasing the difficulty. Extra paddle:
ExtraPaddleStrategy removes the brick and, if there is no extra paddle currently
active, creates an ExtraUserPaddle object. ExtraUserPaddle extends UserPaddle,
moves with the same keyboard control, is clamped between the walls, counts its
own collisions up to a fixed maximum, and then removes itself while updating a
static flag so only one extra paddle can exist at a time. Return life:
ReturnLifeStrategy removes the brick and spawns a FallingHeart object at the
brick’s center, with the same size and texture as the UI hearts. FallingHeart
falls downward at a constant speed, only collides with the main paddle using
shouldCollideWith and a tag, calls BrickerGameManager.gainLife() on collision,
and removes itself if it leaves the screen.
Q4) For the double behaviour I created a DoubleCollisionStrategy class that
implements CollisionStrategy and simply wraps two other strategies: it stores
two CollisionStrategy objects and in onCollision calls both of them one after
the other. This lets me reuse existing special behaviours without changing their
code (a simple decorator/composite design). In the CollisionStrategyFactory,
when a brick is chosen to have a double behaviour, I randomly pick two special
(non double) strategies and build a DoubleCollisionStrategy from them. With 50%
probability I also pick a third special strategy and wrap once more, so a brick
can end up with 2 or 3 special behaviours. Because the factory never creates a
“double of double of double” and the helper that selects inner behaviours does
not return double strategies, the total number of special behaviours per brick
is limited to at most three.
Q5) We only changed the API from part 1 by adding one new public method to
BrickerGameManager: gainLife(). In part 1, the number of lives was only
decreased internally inside BrickerGameManager, so no external code needed
to modify it. In part 2 we introduced the “return life” behaviour, where a
falling heart object must be able to increase the player’s lives when it
collides with the main paddle. To keep all life logic (the counter, the
hearts UI, the numeric display, and the max lives limit) in one place,
we exposed a single public method gainLife() that external classes like
FallingHeart can call, instead of letting them change the lives counter
directly. This small API change was therefore necessary to support the
new behaviour while keeping the design encapsulated and clear.
