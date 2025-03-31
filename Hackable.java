import java.io.Serializable;
import java.util.List;
import java.util.Random;

/**
 * Hackable class for Pnguin's Cyber Heist
 * Represents systems that can be hacked by the player
 */
public class Hackable implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private int difficulty;
    private String targetType; // "digital" or "physical"
    private List<String> requiredItems;
    private static final Random random = new Random();
    
    /**
     * Constructor
     */
    public Hackable(String name, String description, int difficulty, String targetType, List<String> requiredItems) {
        this.name = name;
        this.description = description;
        this.difficulty = difficulty;
        this.targetType = targetType;
        this.requiredItems = requiredItems;
    }
    
    /**
     * Process successful hack
     */
    public void onSuccessfulHack(Player player) {
        // Default rewards
        int baseReward = 10 + (difficulty / 2);
        int coinReward = baseReward + random.nextInt(baseReward);
        
        System.out.println("Hack successful! You gained " + coinReward + " BitCoins.");
        player.addCoins(coinReward);
        
        // Special rewards based on target
        switch (name) {
            case "Training System":
                System.out.println("The training system provides useful practice but no special rewards.");
                break;
                
            case "Public Terminal":
                System.out.println("You found some unprotected data worth a few BitCoins.");
                player.addCoins(random.nextInt(10) + 5);
                break;
                
            case "Security Cameras":
                System.out.println("You've disabled the security cameras, reducing the alert level.");
                GameState gameState = new GameState(); // Temporary instance for demonstration
                gameState.decreaseAlertLevel(1);
                break;
                
            case "Information Database":
                System.out.println("You've accessed valuable intelligence about corporate security measures.");
                if (random.nextInt(100) < 50) {
                    player.addItem(new Item("Security Layout", "Detailed map of corporate security systems"));
                }
                break;
                
            case "Merchant Network":
                System.out.println("You've accessed merchant accounts and transferred some funds to yourself.");
                player.addCoins(random.nextInt(30) + 20);
                break;
                
            case "Research Database":
                System.out.println("You've accessed classified research data with significant market value.");
                player.addItem(new Valuable("Research Archives", "Valuable corporate research data", 50));
                break;
                
            case "Cooling Control":
                System.out.println("You've sabotaged the cooling system, creating a distraction.");
                // Reduce security in server farm area specifically
                break;
                
            case "Network Router":
                System.out.println("You've gained control of network traffic, allowing you to monitor communications.");
                player.applyStatusEffect("Network Access", "Improved hacking against all targets", 0, 0, 10, 5);
                break;
                
            case "Experimental AI":
                System.out.println("You've extracted valuable AI algorithms from the research project.");
                player.addItem(new Valuable("AI Algorithm", "Cutting-edge artificial intelligence code", 100));
                break;
                
            case "Access Control":
                System.out.println("You've hacked the access control system, granting yourself higher security clearance.");
                player.increaseSecurityClearance(3);
                break;
                
            case "Executive Terminal":
                System.out.println("You've hacked into an executive's terminal, gaining access to sensitive information.");
                player.addItem(new QuestItem("Executive Secrets", "Compromising information about corporate executives", "side_quest"));
                break;
                
            case "Mainframe":
                System.out.println("You've successfully infiltrated the mainframe and found the CryptoCoin algorithm!");
                // Item will be added in the main game logic
                break;
                
            case "Gibson Core":
                System.out.println("You've hacked the legendary Gibson core! Your name will go down in hacker history.");
                player.addCoins(500);
                player.addExperience(1000);
                break;
                
            default:
                System.out.println("You successfully hacked the system.");
        }
    }
    
    /**
     * Process failed hack
     */
    public void onFailedHack(Player player) {
        System.out.println("Hack failed!");
        
        // Possible negative consequences
        int consequenceType = random.nextInt(3);
        
        switch (consequenceType) {
            case 0:
                // Minor system damage
                int minorDamage = random.nextInt(5) + 1;
                System.out.println("The system's security protocols caused " + minorDamage + " damage to your gear.");
                player.takeDamage(minorDamage);
                break;
                
            case 1:
                // Temporary debuff
                System.out.println("The system's countermeasures have temporarily reduced your hacking ability.");
                player.applyStatusEffect("System Lockout", "Reduced hacking ability", 0, 0, -10, 3);
                break;
                
            case 2:
                // Resource loss
                int coinLoss = (random.nextInt(10) + 1) * difficulty / 10;
                if (coinLoss > 0 && player.getCoins() > coinLoss) {
                    System.out.println("The hack attempt cost you " + coinLoss + " BitCoins in resources.");
                    player.removeCoins(coinLoss);
                }
                break;
        }
    }
    
    /**
     * Getters
     */
    public String getName() { return name; }
    
    public String getDescription() { return description; }
    
    public int getDifficulty() { return difficulty; }
    
    public String getTargetType() { return targetType; }
    
    public List<String> getRequiredItems() { return requiredItems; }
}
