import java.io.Serializable;
import java.util.*;

/**
 * Player class for Pnguin's Cyber Heist
 * Represents the player character with stats, inventory, and skills
 */
public class Player implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String className;
    private int health;
    private int maxHealth;
    private int attackPower;
    private int defense;
    private int hackingSkill;
    private int coins;
    private int experience;
    private int level;
    private int securityClearance;
    private int skillPoints;
    
    private final List<Item> inventory;
    private final Map<String, Gear> equippedGear;
    private final Map<String, Integer> skills;
    private final Map<String, StatusEffect> activeEffects;
    
    private boolean stealthActive;
    private int stealthDuration;
    
    /**
     * Constructor
     */
    public Player(String name, String className, int maxHealth, int attackPower, int hackingSkill) {
        this.name = name;
        this.className = className;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.attackPower = attackPower;
        this.defense = 5;
        this.hackingSkill = hackingSkill;
        this.coins = 50; // Starting money
        this.experience = 0;
        this.level = 1;
        this.securityClearance = 1;
        this.skillPoints = 0;
        
        this.inventory = new ArrayList<>();
        this.equippedGear = new HashMap<>();
        this.skills = new HashMap<>();
        this.activeEffects = new HashMap<>();
        
        this.stealthActive = false;
        this.stealthDuration = 0;
    }
    
    /**
     * Initialize skills based on character class
     */
    public void initializeSkills() {
        // Default skills for all classes
        skills.put("hacking", 1);
        skills.put("stealth", 1);
        skills.put("combat", 1);
        skills.put("engineering", 0);
        skills.put("tech", 0);
        
        // Class-specific skill bonuses
        switch (className) {
            case "Infiltrator":
                skills.put("stealth", 3);
                skills.put("tech", 1);
                break;
            case "CyberPunk":
                skills.put("combat", 3);
                skills.put("engineering", 1);
                break;
            case "NetRunner":
                skills.put("hacking", 3);
                skills.put("tech", 1);
                break;
        }
    }
    
    /**
     * Update player status at the end of a turn
     */
    public void updateStatus() {
        // Update stealth duration
        if (stealthActive) {
            stealthDuration--;
            if (stealthDuration <= 0) {
                stealthActive = false;
                System.out.println("Your stealth mode has worn off.");
            }
        }
        
        // Update effect durations
        List<String> expiredEffects = new ArrayList<>();
        for (Map.Entry<String, StatusEffect> entry : activeEffects.entrySet()) {
            StatusEffect effect = entry.getValue();
            effect.decrementDuration();
            
            if (effect.getDuration() <= 0) {
                expiredEffects.add(entry.getKey());
            }
        }
        
        // Remove expired effects
        for (String effectName : expiredEffects) {
            StatusEffect effect = activeEffects.remove(effectName);
            System.out.println("The " + effect.getName() + " effect has worn off.");
        }
    }
    
    /**
     * Take damage
     */
    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) health = 0;
    }
    
    /**
     * Heal player
     */
    public void heal(int amount) {
        health += amount;
        if (health > maxHealth) health = maxHealth;
    }
    
    /**
     * Add item to inventory
     */
    public void addItem(Item item) {
        inventory.add(item);
    }
    
    /**
     * Remove item from inventory
     */
    public Item removeItem(String itemName) {
        for (Iterator<Item> iterator = inventory.iterator(); iterator.hasNext();) {
            Item item = iterator.next();
            if (item.getName().equalsIgnoreCase(itemName)) {
                iterator.remove();
                return item;
            }
        }
        return null;
    }
    
    /**
     * Check if player has an item
     */
    public boolean hasItem(String itemName) {
        for (Item item : inventory) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get an item by name
     */
    public Item getItemByName(String itemName) {
        for (Item item : inventory) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return item;
            }
        }
        return null;
    }
    
    /**
     * Equip gear
     */
    public boolean equipGear(Gear gear) {
        String slot = gear.getSlot();
        
        // Unequip existing gear in the same slot
        if (equippedGear.containsKey(slot)) {
            Gear oldGear = equippedGear.remove(slot);
            inventory.add(oldGear);
        }
        
        // Equip the new gear
        equippedGear.put(slot, gear);
        inventory.remove(gear);
        
        return true;
    }
    
    /**
     * Unequip gear
     */
    public boolean unequipGear(String slotOrName) {
        // Try to unequip by slot
        if (equippedGear.containsKey(slotOrName.toLowerCase())) {
            Gear gear = equippedGear.remove(slotOrName.toLowerCase());
            inventory.add(gear);
            return true;
        }
        
        // Try to unequip by item name
        for (Map.Entry<String, Gear> entry : equippedGear.entrySet()) {
            if (entry.getValue().getName().equalsIgnoreCase(slotOrName)) {
                Gear gear = equippedGear.remove(entry.getKey());
                inventory.add(gear);
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Get attack power including gear bonuses
     */
    public int getAttackPower() {
        int totalAttack = attackPower;
        
        // Add gear bonuses
        for (Gear gear : equippedGear.values()) {
            totalAttack += gear.getAttackBonus();
        }
        
        // Add effects
        for (StatusEffect effect : activeEffects.values()) {
            totalAttack += effect.getAttackMod();
        }
        
        // Add combat skill bonus
        totalAttack += getSkillLevel("combat") * 3;
        
        return Math.max(1, totalAttack);
    }
    
    /**
     * Get critical hit chance
     */
    public int getCriticalChance() {
        return getSkillLevel("combat") * 5;
    }
    
    /**
     * Get defense including gear bonuses
     */
    public int getDefense() {
        int totalDefense = defense;
        
        // Add gear bonuses
        for (Gear gear : equippedGear.values()) {
            totalDefense += gear.getDefenseBonus();
        }
        
        // Add effects
        for (StatusEffect effect : activeEffects.values()) {
            totalDefense += effect.getDefenseMod();
        }
        
        return Math.max(0, totalDefense);
    }
    
    /**
     * Get hacking skill including gear bonuses
     */
    public int getHackingSkill() {
        int totalHacking = hackingSkill;
        
        // Add gear bonuses
        for (Gear gear : equippedGear.values()) {
            totalHacking += gear.getHackingBonus();
        }
        
        // Add effects
        for (StatusEffect effect : activeEffects.values()) {
            totalHacking += effect.getHackingMod();
        }
        
        // Add hacking skill bonus
        totalHacking += getSkillLevel("hacking") * 5;
        
        return Math.max(1, totalHacking);
    }
    
    /**
     * Activate stealth mode
     */
    public boolean activateStealth() {
        if (stealthActive) {
            return false;
        }
        
        int stealthLevel = getSkillLevel("stealth");
        if (stealthLevel == 0) {
            return false;
        }
        
        stealthActive = true;
        stealthDuration = stealthLevel * 3;
        
        return true;
    }
    
    /**
     * Deactivate stealth mode
     */
    public void deactivateStealth() {
        stealthActive = false;
        stealthDuration = 0;
    }
    
    /**
     * Add experience
     */
    public void addExperience(int exp) {
        experience += exp;
        checkLevelUp();
    }
    
    /**
     * Check for level up
     */
    private void checkLevelUp() {
        int expForNextLevel = getExpForNextLevel();
        
        if (experience >= expForNextLevel && level < 10) {
            level++;
            skillPoints += 2;
            maxHealth += 10;
            health = maxHealth;
            attackPower += 2;
            defense += 1;
            hackingSkill += 2;
            
            System.out.println("LEVEL UP! You are now level " + level + "!");
            System.out.println("All stats increased and you gained 2 skill points.");
        }
    }
    
    /**
     * Get experience required for next level
     */
    public int getExpForNextLevel() {
        return level * 100;
    }
    
    /**
     * Add BitCoins to the player
     */
    public void addCoins(int amount) {
        coins += amount;
    }
    
    /**
     * Remove BitCoins from the player
     */
    public boolean removeCoins(int amount) {
        if (coins >= amount) {
            coins -= amount;
            return true;
        }
        return false;
    }
    
    /**
     * Apply a status effect
     */
    public void applyStatusEffect(String name, String description, int attackMod, int defenseMod, int hackingMod, int duration) {
        StatusEffect effect = new StatusEffect(name, description, attackMod, defenseMod, hackingMod, duration);
        activeEffects.put(name, effect);
    }
    
    /**
     * Remove a status effect
     */
    public void removeStatusEffect(String name) {
        activeEffects.remove(name);
    }
    
    /**
     * Check if player has a skill
     */
    public boolean checkSkill(String skillName) {
        return skills.containsKey(skillName.toLowerCase()) && skills.get(skillName.toLowerCase()) > 0;
    }
    
    /**
     * Get skill level
     */
    public int getSkillLevel(String skillName) {
        if (skills.containsKey(skillName.toLowerCase())) {
            return skills.get(skillName.toLowerCase());
        }
        return 0;
    }
    
    /**
     * Upgrade a skill
     */
    public boolean upgradeSkill(String skillName) {
        if (skillPoints <= 0) {
            return false;
        }
        
        if (!skills.containsKey(skillName)) {
            return false;
        }
        
        int currentLevel = skills.get(skillName);
        if (currentLevel >= 5) {
            return false;
        }
        
        skills.put(skillName, currentLevel + 1);
        skillPoints--;
        
        return true;
    }
    
    /**
     * Increase security clearance level
     */
    public void increaseSecurityClearance(int level) {
        if (level > securityClearance) {
            securityClearance = level;
        }
    }
    
    /**
     * Check if player has sufficient security clearance
     */
    public boolean hasSecurityClearance(int level) {
        return securityClearance >= level;
    }
    
    /**
     * Getters and setters
     */
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getClassName() { return className; }
    
    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }
    
    public int getMaxHealth() { return maxHealth; }
    
    public int getCoins() { return coins; }
    
    public int getExperience() { return experience; }
    
    public int getLevel() { return level; }
    
    public int getSecurityClearance() { return securityClearance; }
    
    public int getSkillPoints() { return skillPoints; }
    
    public List<Item> getInventory() { return new ArrayList<>(inventory); }
    
    public List<Gear> getEquippedGear() { return new ArrayList<>(equippedGear.values()); }
    
    public Map<String, Integer> getSkills() { return new HashMap<>(skills); }
    
    public Map<String, StatusEffect> getActiveEffects() { return new HashMap<>(activeEffects); }
    
    public boolean isStealthActive() { return stealthActive; }
}
