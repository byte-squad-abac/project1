package gdd;

public class GameState {
    private static GameState instance;
    
    private boolean level1Completed = false;
    private boolean level2Unlocked = true; // TEMPORARILY UNLOCKED FOR TESTING
    private int highScore = 0;
    private int currentLevel = 1;
    
    private GameState() {}
    
    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }
    
    public boolean isLevel1Completed() {
        return level1Completed;
    }
    
    public void setLevel1Completed(boolean completed) {
        this.level1Completed = completed;
        if (completed) {
            this.level2Unlocked = true;
        }
    }
    
    public boolean isLevel2Unlocked() {
        return level2Unlocked;
    }
    
    public void unlockLevel2() {
        this.level2Unlocked = true;
    }
    
    public int getHighScore() {
        return highScore;
    }
    
    public void updateHighScore(int score) {
        if (score > highScore) {
            highScore = score;
        }
    }
    
    public int getCurrentLevel() {
        return currentLevel;
    }
    
    public void setCurrentLevel(int level) {
        this.currentLevel = level;
    }
    
    public void reset() {
        currentLevel = 1;
    }
}