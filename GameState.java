import java.io.Serializable;
import java.util.*;

/**
 * GameState class for Pnguin's Cyber Heist
 * Manages the overall state of the game
 */
public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int alertLevel;
    private int turnCount;
    private final Map<String, Bonus> temporaryBonuses;
    private final Map<String, Integer> gameFlags;
    
    /**
     * Constructor
     */
    public GameState() {
        this.alertLevel = 0;
        this.turnCount = 0;
        this.temporaryBonuses = new HashMap<>();
        this.gameFlags = new HashMap<>();
    }
    
    /**
     * Inner class for temporary bonuses
     */
    private static class Bonus implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private int value;
        private int duration;
        
        public Bonus(int value, int duration) {
            this.value = value;
            this.duration = duration;
        }
        
        public int getValue() { return value; }
        
        public int getDuration() { return duration; }
        
        public void decrementDuration() { duration--; }
    }
    
    /**
     * Increment the turn counter
     */
    public void incrementTurn() {
        turnCount++;
        
        // Update temporary bonuses
        List<String> expiredBonuses = new ArrayList<>();
        for (Map.Entry<String, Bonus> entry : temporaryBonuses.entrySet()) {
            Bonus bonus = entry.getValue();
            bonus.decrementDuration();
            if (bonus.getDuration() <= 0) {
                expiredBonuses.add(entry.getKey());
            }
        }
        
        // Remove expired bonuses
        for (String bonusType : expiredBonuses) {
            temporaryBonuses.remove(bonusType);
        }
    }
    
    /**
     * Get temporary bonus value
     */
    public int getTemporaryBonus(String type) {
        if (temporaryBonuses.containsKey(type)) {
            return temporaryBonuses.get(type).getValue();
        }
        return 0;
    }
    
    /**
     * Set temporary bonus
     */
    public void setTemporaryBonus(String type, int value, int duration) {
        temporaryBonuses.put(type, new Bonus(value, duration));
    }
    
    /**
     * Increase alert level
     */
    public void increaseAlertLevel(int amount) {
        alertLevel += amount;
        if (alertLevel > 5) {
            alertLevel = 5;
        }
    }
    
    /**
     * Decrease alert level
     */
    public void decreaseAlertLevel(int amount) {
        alertLevel -= amount;
        if (alertLevel < 0) {
            alertLevel = 0;
        }
    }
    
    /**
     * Set a game flag
     */
    public void setFlag(String key, int value) {
        gameFlags.put(key, value);
    }
    
    /**
     * Get a game flag
     */
    public int getFlag(String key) {
        return gameFlags.getOrDefault(key, 0);
    }
    
    /**
     * Getters
     */
    public int getAlertLevel() { return alertLevel; }
    
    public int getTurnCount() { return turnCount; }
}
