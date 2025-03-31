import java.io.Serializable;
import java.util.*;

/**
 * Location class for Pnguin's Cyber Heist
 * Represents a location in the game world
 */
public class Location implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private int securityLevel;
    private boolean secured;
    private final List<String> connections;
    private final List<Item> items;
    private final List<Enemy> enemies;
    private final List<Hackable> hackables;
    
    /**
     * Constructor
     */
    public Location(String name, String description) {
        this.name = name;
        this.description = description;
        this.securityLevel = 0;
        this.secured = false;
        this.connections = new ArrayList<>();
        this.items = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.hackables = new ArrayList<>();
    }
    
    /**
     * Add a connection to another location
     */
    public void addConnection(String locationName) {
        if (!connections.contains(locationName)) {
            connections.add(locationName);
        }
    }
    
    /**
     * Add an item to this location
     */
    public void addItem(Item item) {
        items.add(item);
    }
    
    /**
     * Add an enemy to this location
     */
    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }
    
    /**
     * Add a hackable object to this location
     */
    public void addHackable(Hackable hackable) {
        hackables.add(hackable);
    }
    
    /**
     * Getters and setters
     */
    public String getName() { return name; }
    
    public String getDescription() { return description; }
    
    public int getSecurityLevel() { return securityLevel; }
    public void setSecurityLevel(int securityLevel) { this.securityLevel = securityLevel; }
    
    public boolean isSecured() { return secured; }
    public void setSecured(boolean secured) { this.secured = secured; }
    
    public List<String> getConnections() { return new ArrayList<>(connections); }
    
    public List<Item> getItems() { return items; }
    
    public List<Enemy> getEnemies() { return enemies; }
    
    public List<Hackable> getHackables() { return hackables; }
}
