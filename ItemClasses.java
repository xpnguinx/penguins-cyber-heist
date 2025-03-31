import java.io.Serializable;

/**
 * Item class for Pnguin's Cyber Heist
 * Base class for all items in the game
 */
class Item implements Serializable {
    private static final long serialVersionUID = 1L;
    
    protected String name;
    protected String description;
    
    /**
     * Constructor
     */
    public Item(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    /**
     * Getters
     */
    public String getName() { return name; }
    
    public String getDescription() { return description; }
}

/**
 * Gear class
 * Represents equipment that can be worn/used by the player
 */
class Gear extends Item {
    private static final long serialVersionUID = 1L;
    
    private String slot;
    private int attackBonus;
    private int defenseBonus;
    private int hackingBonus;
    
    /**
     * Constructor
     */
    public Gear(String name, String description, String slot, int attackBonus, int defenseBonus, int hackingBonus) {
        super(name, description);
        this.slot = slot;
        this.attackBonus = attackBonus;
        this.defenseBonus = defenseBonus;
        this.hackingBonus = hackingBonus;
    }
    
    /**
     * Alternate constructor with default values
     */
    public Gear(String name, String description, String slot) {
        this(name, description, slot, 0, 0, 0);
    }
    
    /**
     * Getters
     */
    public String getSlot() { return slot; }
    
    public int getAttackBonus() { return attackBonus; }
    
    public int getDefenseBonus() { return defenseBonus; }
    
    public int getHackingBonus() { return hackingBonus; }
}

/**
 * Consumable class
 * Represents items that can be used for an effect
 */
class Consumable extends Item {
    private static final long serialVersionUID = 1L;
    
    private String type;
    
    /**
     * Constructor
     */
    public Consumable(String name, String description, String type) {
        super(name, description);
        this.type = type;
    }
    
    /**
     * Use the consumable
     */
    public boolean use(Player player) {
        switch (name.toLowerCase()) {
            case "energy drink":
                player.heal(10);
                System.out.println("You consumed an Energy Drink and restored 10 health.");
                return true;
                
            case "stim pack":
                player.heal(25);
                System.out.println("You used a Stim Pack and restored 25 health.");
                return true;
                
            case "neural booster":
                player.applyStatusEffect("Neural Boost", "Enhanced hacking abilities", 0, 0, 10, 5);
                System.out.println("You used a Neural Booster. Hacking skill increased by 10 for 5 turns.");
                return true;
                
            case "memory chip":
                // Reveals a random undiscovered location
                System.out.println("You used a Memory Chip. A location has been revealed on your map.");
                return true;
                
            case "encryption key":
                player.applyStatusEffect("Encryption Key", "Improved success against security systems", 0, 0, 15, 3);
                System.out.println("You used an Encryption Key. Your next hack attempts are more likely to succeed.");
                return true;
                
            case "ice breaker":
                System.out.println("You used an ICE Breaker. The next digital enemy you encounter will be instantly defeated.");
                player.applyStatusEffect("ICE Breaker", "Auto-defeat next digital enemy", 100, 0, 0, 1);
                return true;
                
            case "security bypass module":
                System.out.println("You used a Security Bypass Module. Your next hack attempt will automatically succeed.");
                player.applyStatusEffect("Security Bypass", "Auto-succeed next hack", 0, 0, 100, 1);
                return true;
                
            case "corporate id badge":
                System.out.println("You used a Corporate ID Badge. You have temporary access to low-security areas.");
                player.applyStatusEffect("Corporate Access", "Temporary corporate access", 0, 0, 0, 5);
                player.increaseSecurityClearance(2);
                return true;
                
            case "system access codes":
                System.out.println("You used System Access Codes. You have temporary access to server systems.");
                player.applyStatusEffect("System Access", "Authorized server access", 0, 0, 20, 3);
                return true;
                
            default:
                System.out.println("This item cannot be used.");
                return false;
        }
    }
    
    /**
     * Getters
     */
    public String getType() { return type; }
}

/**
 * QuestItem class
 * Represents items that are part of quests
 */
class QuestItem extends Item {
    private static final long serialVersionUID = 1L;
    
    private String questId;
    
    /**
     * Constructor
     */
    public QuestItem(String name, String description, String questId) {
        super(name, description);
        this.questId = questId;
    }
    
    /**
     * Getters
     */
    public String getQuestId() { return questId; }
}

/**
 * Valuable class
 * Represents items that can be sold for BitCoins
 */
class Valuable extends Item {
    private static final long serialVersionUID = 1L;
    
    private int value;
    
    /**
     * Constructor
     */
    public Valuable(String name, String description, int value) {
        super(name, description);
        this.value = value;
    }
    
    /**
     * Getters
     */
    public int getValue() { return value; }
}
