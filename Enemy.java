import java.io.Serializable;
import java.util.Random;

/**
 * Enemy class for Pnguin's Cyber Heist
 * Represents hostile NPCs that can be fought
 */
public class Enemy implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private int health;
    private int attackPower;
    private static final Random random = new Random();
    
    /**
     * Constructor
     */
    public Enemy(String name, int health, int attackPower) {
        this.name = name;
        this.health = health;
        this.attackPower = attackPower;
    }
    
    /**
     * Take damage
     */
    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) health = 0;
    }
    
    /**
     * Attack the player
     * @return the amount of damage dealt
     */
    public int attack() {
        int damageMultiplier = 80 + random.nextInt(41); // 80% to 120% damage
        return (attackPower * damageMultiplier) / 100;
    }
    
    /**
     * Getters
     */
    public String getName() { return name; }
    
    public int getHealth() { return health; }
    
    public int getAttackPower() { return attackPower; }
}
