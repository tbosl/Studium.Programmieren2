package thd.game.managers;

import thd.game.utilities.GameView;
import thd.gameobjects.base.EnemyGameObject;
import thd.gameobjects.base.GameObject;
import thd.gameobjects.movable.Astronaut;
import thd.gameobjects.unmovable.RemainingLive;
import thd.gameobjects.unmovable.SmartBomb;

import java.util.List;

/**
 * Manages the creation and destruction of game objects.
 */
public class GamePlayManager extends WorldShiftManager {

    protected int smartBombs;
    protected int lives;
    protected int points;
    private int livesToAdd;
    static final int POINTS_REQUIRED_FOR_NEW_LIFE_EASY = 1_000;
    static final int POINTS_REQUIRED_FOR_NEW_LIFE_STANDARD = 2_500;
    int pointsRequiredForNewLife;
    private final GameObjectManager gameObjectManager;
    /**
     * The absolute length of the game world.
     */
    public static final int ABSOLUTE_WORLD_LENGTH = 6400;

    protected GamePlayManager(GameView gameView) {
        super(gameView);
        this.gameObjectManager = new GameObjectManager();
        livesToAdd = 0;
    }

    /**
     * Adds a new game object to the game.
     *
     * @param gameObject The game object to be added.
     */
    @Override
    public void spawnGameObject(GameObject gameObject) {
        super.spawnGameObject(gameObject);
        gameObjectManager.add(gameObject);
    }

    /**
     * Removes a new game object to the game.
     *
     * @param gameObject The game object to be removed.
     */
    @Override
    public void destroyGameObject(GameObject gameObject) {
        super.destroyGameObject(gameObject);
        gameObjectManager.remove(gameObject);
    }

    @Override
    protected void destroyAllGameObjects() {
        super.destroyAllGameObjects();
        gameObjectManager.removeAll();
    }

    @Override
    protected void gameLoopUpdate() {
        super.gameLoopUpdate();
        gameObjectManager.gameLoopUpdate();
        gamePlayManagement();
    }


    /**
     * Provides a list of all enemies that are currently active.
     *
     * @return A LinkedList with all currently active enemies.
     */
    public List<EnemyGameObject> provideAllActiveEnemies() {
        return gameObjectManager.provideAllActiveEnemies();
    }

    List<EnemyGameObject> provideAllEnemiesInToBeAdded() {
        return gameObjectManager.provideAllEnemiesInToBeAdded();
    }

    /**
     * Provides a list of all astronauts that are currently active.
     *
     * @return A LinkedList with all currently active astronauts.
     */
    public List<Astronaut> provideAllAstronauts() {
        return gameObjectManager.provideAllAstronauts();
    }

    private void gamePlayManagement() {
        while (livesToAdd > 0) {
            lifeGained();
            livesToAdd--;
        }
    }

    /**
     * Add points to the score of the player.
     *
     * @param amount The amount of points to be added.
     */
    public void addPoints(int amount) {
        updateLivesToAdd(amount);
        points += amount;
    }

    private void updateLivesToAdd(int amount) {
        int desiredAmountToAdd = calculateAmountOfLivesToAdd(amount);
        if (livesToAdd == 0) {
            livesToAdd = desiredAmountToAdd;
        } else {
            livesToAdd += desiredAmountToAdd;
        }
    }

    private int calculateAmountOfLivesToAdd(int amount) {
        int currentLiveLevel = points / pointsRequiredForNewLife;
        int upcomingLiveLevel = (points + amount) / pointsRequiredForNewLife;
        return upcomingLiveLevel - currentLiveLevel;
    }

    /**
     * Get the current points.
     *
     * @return The current amount of points.
     */
    public int getPoints() {
        return points;
    }

    /**
     * Removes a life of the player's spaceship.
     */
    public void lifeLost() {
        destroyGameObject(findLastAddedLive());
        lives--;
        if (lives <= 0) {
            spaceship.selfDestruction();
        }
    }

    private RemainingLive findLastAddedLive() {
        for (RemainingLive remainingLive : gameObjectManager.provideAllRemainingLives()) {
            if (remainingLive.getLiveIndex() == lives - 1) {
                return remainingLive;
            }
        }
        return null;
    }

    void lifeGained() {
        if (lives < RemainingLive.MAXIMUM_AMOUNT_OF_LIVES) {
            lives++;
            spawnGameObject(new RemainingLive(gameView, this, lives - 1));
            gameView.playSound("life_gained.wav", false);
        }
    }

    void lifeGained(int lifeIndex) {
        spawnGameObject(new RemainingLive(gameView, this, lifeIndex));
    }

    /**
     * Get the current amount of lives the player's spaceship has remaining.
     *
     * @return The amount of lives.
     */
    public int getLives() {
        return lives;
    }

    void smartBombGained() {
        smartBombs++;
        spawnGameObject(new SmartBomb(gameView, this));
    }

    /**
     * Removes the detonated smart bomb from the available smart bombs.
     */
    public void detonateSmartBomb() {
        destroyGameObject(findLastAddedSmartBomb());
        smartBombs--;
    }

    private SmartBomb findLastAddedSmartBomb() {
        for (SmartBomb smartBomb : gameObjectManager.provideAllRemainingSmartBombs()) {
            if (smartBomb.getSmartBombIndex() + 1 == smartBombs) {
                return smartBomb;
            }
        }
        return null;
    }

    /**
     * Get the current amount of remaining smart bombs.
     *
     * @return The current amount of remaining smart bombs.
     */
    public int getSmartBombs() {
        return smartBombs;
    }
}
