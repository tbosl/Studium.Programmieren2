package thd.game.managers;

import thd.game.level.Difficulty;
import thd.game.level.Level;
import thd.game.utilities.GameView;

class GameManager extends LevelManager {

    GameManager(GameView gameView) {
        super(gameView);
    }

    @Override
    protected void gameLoopUpdate() {
        super.gameLoopUpdate();
        gameManagement();
    }

    private void gameManagement() {
        if (endOfGame()) {
            if (!overlay.isMessageShown()) {
                overlay.showMessage("Game Over");
                gameView.playSound("game_over.wav", false);
            }
            if (gameView.timer(2000, this)) {
                overlay.stopShowing();
                startNewGame();
            }
        } else if (endOfLevel()) {
            if (!overlay.isMessageShown()) {
                overlay.showMessage("Great Job!");
            }
            if (gameView.timer(2000, this)) {
                overlay.stopShowing();
                switchToNextLevel();
                initializeLevel();
            }
        }
    }

    /**
     * Start a new game.
     */
    void startNewGame() {
        Level.difficulty = Difficulty.EASY;
        initializeGame();
        gameView.playSound("theme.wav", true);
    }

    @Override
    protected void initializeLevel() {
        super.initializeLevel();
        overlay.showMessage(level.name, 2);
        gameView.playSound("level_begin.wav", false);
    }

    @Override
    protected void initializeGame() {
        super.initializeGame();
        initializeLevel();
    }

    private boolean endOfGame() {
        return lives == 0 || (!hasNextLevel() && endOfLevel()) || provideAllAstronauts().isEmpty();
    }

    private boolean endOfLevel() {
        return provideAllActiveEnemies().isEmpty();
    }
}
