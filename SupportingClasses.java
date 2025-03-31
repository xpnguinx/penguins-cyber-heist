import java.io.Serializable;
import java.util.*;

/**
 * Quest class for Pnguin's Cyber Heist
 * Represents missions and objectives for the player
 */
class Quest implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private String type; // "main" or "side"
    private boolean active;
    private boolean completed;
    private int currentObjectiveIndex;
    
    private final List<Objective> objectives;
    private int expReward;
    private int coinReward;
    private Item itemReward;
    private String unlocksQuestId;
    private Map<String, Integer> factionChanges;
    
    /**
     * Constructor
     */
    public Quest(String name, String description, String type) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.active = false;
        this.completed = false;
        this.currentObjectiveIndex = 0;
        this.objectives = new ArrayList<>();
        this.factionChanges = new HashMap<>();
    }
    
    /**
     * Inner class for quest objectives
     */
    private static class Objective implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String description;
        private String type; // "location", "item", "enemy", "hack"
        private String target;
        private boolean completed;
        
        public Objective(String description, String type, String target) {
            this.description = description;
            this.type = type;
            this.target = target;
            this.completed = false;
        }
        
        public String getDescription() { return description; }
        
        public String getType() { return type; }
        
        public String getTarget() { return target; }
        
        public boolean isCompleted() { return completed; }
        
        public void setCompleted(boolean completed) { this.completed = completed; }
    }
    
    /**
     * Add an objective to the quest
     */
    public void addObjective(String description, String type, String target) {
        objectives.add(new Objective(description, type, target));
    }
    
    /**
     * Activate the quest
     */
    public void activate() {
        active = true;
    }
    
    /**
     * Check if all objectives are completed
     */
    public boolean checkCompletion() {
        if (!active || completed) {
            return false;
        }
        
        for (Objective objective : objectives) {
            if (!objective.isCompleted()) {
                return false;
            }
        }
        
        completed = true;
        return true;
    }
    
    /**
     * Update quest for location visit
     */
    public void updateLocationVisit(String locationName) {
        if (!active || completed || currentObjectiveIndex >= objectives.size()) {
            return;
        }
        
        Objective currentObjective = objectives.get(currentObjectiveIndex);
        if (currentObjective.getType().equals("location") && 
            currentObjective.getTarget().equalsIgnoreCase(locationName) && 
            !currentObjective.isCompleted()) {
            
            currentObjective.setCompleted(true);
            System.out.println("Objective completed: " + currentObjective.getDescription());
            currentObjectiveIndex++;
            
            if (currentObjectiveIndex < objectives.size()) {
                System.out.println("New objective: " + objectives.get(currentObjectiveIndex).getDescription());
            }
        }
    }
    
    /**
     * Update quest for item collection
     */
    public void updateItemCollection(String itemName) {
        if (!active || completed || currentObjectiveIndex >= objectives.size()) {
            return;
        }
        
        Objective currentObjective = objectives.get(currentObjectiveIndex);
        if (currentObjective.getType().equals("item") && 
            currentObjective.getTarget().equalsIgnoreCase(itemName) && 
            !currentObjective.isCompleted()) {
            
            currentObjective.setCompleted(true);
            System.out.println("Objective completed: " + currentObjective.getDescription());
            currentObjectiveIndex++;
            
            if (currentObjectiveIndex < objectives.size()) {
                System.out.println("New objective: " + objectives.get(currentObjectiveIndex).getDescription());
            }
        }
    }
    
    /**
     * Update quest for enemy defeat
     */
    public void updateEnemyDefeat(String enemyName) {
        if (!active || completed || currentObjectiveIndex >= objectives.size()) {
            return;
        }
        
        Objective currentObjective = objectives.get(currentObjectiveIndex);
        if (currentObjective.getType().equals("enemy") && 
            currentObjective.getTarget().equalsIgnoreCase(enemyName) && 
            !currentObjective.isCompleted()) {
            
            currentObjective.setCompleted(true);
            System.out.println("Objective completed: " + currentObjective.getDescription());
            currentObjectiveIndex++;
            
            if (currentObjectiveIndex < objectives.size()) {
                System.out.println("New objective: " + objectives.get(currentObjectiveIndex).getDescription());
            }
        }
    }
    
    /**
     * Update quest for successful hack
     */
    public void updateHackTarget(String targetName) {
        if (!active || completed || currentObjectiveIndex >= objectives.size()) {
            return;
        }
        
        Objective currentObjective = objectives.get(currentObjectiveIndex);
        if (currentObjective.getType().equals("hack") && 
            currentObjective.getTarget().equalsIgnoreCase(targetName) && 
            !currentObjective.isCompleted()) {
            
            currentObjective.setCompleted(true);
            System.out.println("Objective completed: " + currentObjective.getDescription());
            currentObjectiveIndex++;
            
            if (currentObjectiveIndex < objectives.size()) {
                System.out.println("New objective: " + objectives.get(currentObjectiveIndex).getDescription());
            }
        }
    }
    
    /**
     * Get current objective description
     */
    public String getCurrentObjective() {
        if (!active || completed || currentObjectiveIndex >= objectives.size()) {
            return "No active objective";
        }
        
        return objectives.get(currentObjectiveIndex).getDescription();
    }
    
    /**
     * Getters and setters
     */
    public String getName() { return name; }
    
    public String getDescription() { return description; }
    
    public String getType() { return type; }
    
    public boolean isActive() { return active; }
    
    public boolean isCompleted() { return completed; }
    
    public int getExpReward() { return expReward; }
    public void setExpReward(int expReward) { this.expReward = expReward; }
    
    public int getCoinReward() { return coinReward; }
    public void setCoinReward(int coinReward) { this.coinReward = coinReward; }
    
    public Item getItemReward() { return itemReward; }
    public void setItemReward(Item itemReward) { this.itemReward = itemReward; }
    
    public String getUnlocksQuestId() { return unlocksQuestId; }
    public void setUnlocksQuestId(String unlocksQuestId) { this.unlocksQuestId = unlocksQuestId; }
    
    public Map<String, Integer> getFactionChanges() { return factionChanges; }
    public void setFactionChanges(Map<String, Integer> factionChanges) { this.factionChanges = factionChanges; }
}

/**
 * Faction class for Pnguin's Cyber Heist
 * Represents a group/organization that the player can have a reputation with
 */
class Faction implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private int standing; // -100 to 100
    
    /**
     * Constructor
     */
    public Faction(String name, String description, int initialStanding) {
        this.name = name;
        this.description = description;
        this.standing = initialStanding;
    }
    
    /**
     * Change faction standing
     */
    public void changeStanding(int change) {
        standing += change;
        if (standing > 100) {
            standing = 100;
        } else if (standing < -100) {
            standing = -100;
        }
    }
    
    /**
     * Getters
     */
    public String getName() { return name; }
    
    public String getDescription() { return description; }
    
    public int getStanding() { return standing; }
}

/**
 * StatusEffect class for Pnguin's Cyber Heist
 * Represents temporary effects on the player
 */
class StatusEffect implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private int attackMod;
    private int defenseMod;
    private int hackingMod;
    private int duration;
    
    /**
     * Constructor
     */
    public StatusEffect(String name, String description, int attackMod, int defenseMod, int hackingMod, int duration) {
        this.name = name;
        this.description = description;
        this.attackMod = attackMod;
        this.defenseMod = defenseMod;
        this.hackingMod = hackingMod;
        this.duration = duration;
    }
    
    /**
     * Decrement duration
     */
    public void decrementDuration() {
        duration--;
    }
    
    /**
     * Getters
     */
    public String getName() { return name; }
    
    public String getDescription() { return description; }
    
    public int getAttackMod() { return attackMod; }
    
    public int getDefenseMod() { return defenseMod; }
    
    public int getHackingMod() { return hackingMod; }
    
    public int getDuration() { return duration; }
}

/**
 * SaveData class for Pnguin's Cyber Heist
 * Container for game save data
 */
class SaveData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Player player;
    private String currentLocationName;
    private GameState gameState;
    private Set<String> discoveredLocations;
    
    /**
     * Constructor
     */
    public SaveData(Player player, String currentLocationName, GameState gameState, Set<String> discoveredLocations) {
        this.player = player;
        this.currentLocationName = currentLocationName;
        this.gameState = gameState;
        this.discoveredLocations = new HashSet<>(discoveredLocations);
    }
    
    /**
     * Getters
     */
    public Player getPlayer() { return player; }
    
    public String getCurrentLocationName() { return currentLocationName; }
    
    public GameState getGameState() { return gameState; }
    
    public Set<String> getDiscoveredLocations() { return discoveredLocations; }
}