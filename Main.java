import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.nio.file.*;

/**
 * Main class for Pnguin's Cyber Heist
 * A dark themed CLI game featuring a hacker penguin
 */
public class Main {
    // Color codes for terminal output
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String BOLD = "\u001B[1m";
    
    private static final Scanner scanner = new Scanner(System.in);
    private static Player player;
    private static final Map<String, Location> gameMap = new HashMap<>();
    private static final Random random = new Random();
    private static Location currentLocation;
    private static GameState gameState = new GameState();
    private static final Set<String> discoveredLocations = new HashSet<>();
    private static final Map<String, Quest> quests = new HashMap<>();
    private static final Map<String, Faction> factions = new HashMap<>();
    
    // Game settings
    private static boolean fastMode = false;
    private static int difficulty = 1; // 0=easy, 1=normal, 2=hard

    public static void main(String[] args) {
        for (String arg : args) {
            if (arg.equals("--fast")) {
                fastMode = true;
            }
        }
        
        // Display splash screen
        displaySplashScreen();
        mainMenu();
    }
    
    /**
     * Display the game's splash screen
     */
    private static void displaySplashScreen() {
        clearScreen();
        System.out.println(CYAN + BOLD);
        System.out.println("██████╗ ███╗   ██╗ ██████╗ ██╗   ██╗██╗███╗   ██╗███████╗");
        System.out.println("██╔══██╗████╗  ██║██╔════╝ ██║   ██║██║████╗  ██║██╔════╝");
        System.out.println("██████╔╝██╔██╗ ██║██║  ███╗██║   ██║██║██╔██╗ ██║███████╗");
        System.out.println("██╔═══╝ ██║╚██╗██║██║   ██║██║   ██║██║██║╚██╗██║╚════██║");
        System.out.println("██║     ██║ ╚████║╚██████╔╝╚██████╔╝██║██║ ╚████║███████║");
        System.out.println("╚═╝     ╚═╝  ╚═══╝ ╚═════╝  ╚═════╝ ╚═╝╚═╝  ╚═══╝╚══════╝");
        System.out.println("    ██████╗██╗   ██╗██████╗ ███████╗██████╗   ██╗  ██╗███████╗██╗███████╗████████╗");
        System.out.println("   ██╔════╝╚██╗ ██╔╝██╔══██╗██╔════╝██╔══██╗  ██║  ██║██╔════╝██║██╔════╝╚══██╔══╝");
        System.out.println("   ██║      ╚████╔╝ ██████╔╝█████╗  ██████╔╝  ███████║█████╗  ██║███████╗   ██║   ");
        System.out.println("   ██║       ╚██╔╝  ██╔══██╗██╔══╝  ██╔══██╗  ██╔══██║██╔══╝  ██║╚════██║   ██║   ");
        System.out.println("   ╚██████╗   ██║   ██████╔╝███████╗██║  ██║  ██║  ██║███████╗██║███████║   ██║   ");
        System.out.println("    ╚═════╝   ╚═╝   ╚═════╝ ╚══════╝╚═╝  ╚═╝  ╚═╝  ╚═╝╚══════╝╚═╝╚══════╝   ╚═╝   ");
        System.out.println(RESET);
        
        System.out.println(YELLOW + "\n\t\t\t\tPRESS ENTER TO CONTINUE..." + RESET);
        scanner.nextLine();
    }
    
    /**
     * Clear the screen
     */
    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    
    /**
     * Print text slowly for dramatic effect
     */
    private static void printSlow(String text) {
        if (fastMode) {
            System.out.println(text);
            return;
        }
        
        for (char c : text.toCharArray()) {
            System.out.print(c);
            sleep(30); // Adjust speed as needed
        }
        System.out.println();
    }
    
    /**
     * Sleep for a specified time
     */
    private static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            // Ignore
        }
    }
    
    /**
     * Capitalize first letter of each word
     */
    private static String capitalize(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        StringBuilder result = new StringBuilder();
        String[] words = text.split("\\s+");
        
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1).toLowerCase())
                      .append(" ");
            }
        }
        
        return result.toString().trim();
    }
    
    /**
     * Confirm an action with the user
     */
    private static boolean confirmAction(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim().toLowerCase();
        return input.startsWith("y");
    }
    
    private static void mainMenu() {
        boolean inMainMenu = true;
        
        while (inMainMenu) {
            clearScreen();
            System.out.println(CYAN + BOLD);
            System.out.println("        .---.        .-----------");
            System.out.println("       /     \\  __  /    ------");
            System.out.println("      / /     \\(  )/    -----");
            System.out.println("     //////   ' \\/ `   ---     ");
            System.out.println("    //// / // :    : ---      ");
            System.out.println("   // /   /  /`    '--        ");
            System.out.println("  //          //..\\\\          ");
            System.out.println(" ===========UU====UU====      ");
            System.out.println("             '//||\\\\`         ");
            System.out.println("               ''``           ");
            System.out.println(RESET);
            
            System.out.println(GREEN + "╔══════════════════════════════════════════╗");
            System.out.println("║             MAIN MENU                    ║");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.println("║  1. New Game                             ║");
            System.out.println("║  2. Load Game                            ║");
            System.out.println("║  3. Options                              ║");
            System.out.println("║  4. Credits                              ║");
            System.out.println("║  5. Exit                                 ║");
            System.out.println("╚══════════════════════════════════════════╝" + RESET);
            
            System.out.print(YELLOW + "Select an option [1-5]: " + RESET);
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    initGame();
                    gameLoop();
                    inMainMenu = false;
                    break;
                case "2":
                    if (loadGame()) {
                        gameLoop();
                        inMainMenu = false;
                    }
                    break;
                case "3":
                    showOptions();
                    break;
                case "4":
                    showCredits();
                    break;
                case "5":
                    System.out.println(RED + "Shutting down systems..." + RESET);
                    sleep(1000);
                    System.exit(0);
                    break;
                default:
                    System.out.println(RED + "Invalid option. Press Enter to try again." + RESET);
                    scanner.nextLine();
            }
        }
    }
    
    /**
     * Show game options
     */
    private static void showOptions() {
        boolean inOptionsMenu = true;
        
        while (inOptionsMenu) {
            clearScreen();
            System.out.println(GREEN + "╔══════════════════════════════════════════╗");
            System.out.println("║             OPTIONS                      ║");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.println("║  1. Typing Speed: " + (fastMode ? "Fast" : "Normal") + "                   ║");
            System.out.println("║  2. Difficulty: " + getDifficultyName() + "                  ║");
            System.out.println("║  3. Back to Main Menu                    ║");
            System.out.println("╚══════════════════════════════════════════╝" + RESET);
            
            System.out.print(YELLOW + "Select an option [1-3]: " + RESET);
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    fastMode = !fastMode;
                    System.out.println(YELLOW + "Typing speed set to " + (fastMode ? "Fast" : "Normal") + RESET);
                    sleep(1000);
                    break;
                case "2":
                    difficulty = (difficulty + 1) % 3;
                    System.out.println(YELLOW + "Difficulty set to " + getDifficultyName() + RESET);
                    sleep(1000);
                    break;
                case "3":
                    inOptionsMenu = false;
                    break;
                default:
                    System.out.println(RED + "Invalid option. Press Enter to try again." + RESET);
                    scanner.nextLine();
            }
        }
    }
    
    /**
     * Get difficulty name
     */
    private static String getDifficultyName() {
        switch (difficulty) {
            case 0: return "Easy";
            case 1: return "Normal";
            case 2: return "Hard";
            default: return "Unknown";
        }
    }
    
    /**
     * Show credits
     */
    private static void showCredits() {
        clearScreen();
        System.out.println(PURPLE + "╔══════════════════════════════════════════╗");
        System.out.println("║             CREDITS                      ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║                                          ║");
        System.out.println("║  PNGUIN'S CYBER HEIST                    ║");
        System.out.println("║                                          ║");
        System.out.println("║  Programming: Lalo                       ║");
        System.out.println("║  Story: Lalo                             ║");
        System.out.println("║  ASCII Art: Various Artists              ║");
        System.out.println("║                                          ║");
        System.out.println("║  Special Thanks:                         ║");
        System.out.println("║  - Claude for simplification             ║");
        System.out.println("║  - The Open Source Community             ║");
        System.out.println("║                                          ║");
        System.out.println("║  © 2025 - All rights reserved            ║");
        System.out.println("║                                          ║");
        System.out.println("╚══════════════════════════════════════════╝" + RESET);
        
        System.out.println(YELLOW + "\nPress Enter to return to the main menu..." + RESET);
        scanner.nextLine();
    }
    
    /**
     * Initialize the game
     */
    private static void initGame() {
        clearScreen();
        System.out.println(CYAN + BOLD);
        System.out.println("                  ..");
        System.out.println("                .:::.");
        System.out.println("            ..::::::::.");
        System.out.println("          .:::::::::::::.");
        System.out.println("        .::::::::::::::::..");
        System.out.println("       .:::::::::::::::::::.");
        System.out.println("      .::::::::::::::::::::: ");
        System.out.println("      :::::::::::::::::::::: ");
        System.out.println("      :::::::::::::::::::::: ");
        System.out.println("      `::::::::::::::::::::'");
        System.out.println("       `:::::::::::::::::::'");
        System.out.println("        `:::::::::::::::'   ,");
        System.out.println("         `:::::::::::::' .'//.");
        System.out.println("         ,yyyy--.   ,yy//  //");
        System.out.println("       .'`-._  `---'  //  //,");
        System.out.println("      //     `-.....-'//.//.");
        System.out.println("      \\_    _    .:: //'.'\n");
        System.out.println(RESET);
        
        printSlow(YELLOW + "\n\n========== " + CYAN + "PNGUIN'S CYBER HEIST" + YELLOW + " ==========\n" + RESET);
        printSlow(GREEN + "In a world of corporate surveillance and digital tyranny..." + RESET);
        printSlow(BLUE + "One penguin stands against the system." + RESET);
        printSlow(PURPLE + "You are PNGUIN, the legendary hacker penguin with a dark hood and a darker past.\n" + RESET);
        
        // Create player
        System.out.print(YELLOW + "Enter your hacker alias: " + RESET);
        String name = scanner.nextLine();
        if (name.trim().isEmpty()) {
            name = "Pnguin";
        }
        
        // Let player choose a class
        printSlow(YELLOW + "\nChoose your hacker class:" + RESET);
        System.out.println(CYAN + "1. Infiltrator" + RESET + " - Specializes in stealth and bypassing security");
        System.out.println(RED + "2. CyberPunk" + RESET + " - A balanced class with combat and hacking skills");
        System.out.println(GREEN + "3. NetRunner" + RESET + " - Elite hacker with powerful digital abilities");
        
        int classChoice = 0;
        while (classChoice < 1 || classChoice > 3) {
            System.out.print(YELLOW + "Enter your choice (1-3): " + RESET);
            try {
                classChoice = Integer.parseInt(scanner.nextLine());
                if (classChoice < 1 || classChoice > 3) {
                    System.out.println(RED + "Invalid choice. Please enter 1, 2, or 3." + RESET);
                }
            } catch (NumberFormatException e) {
                System.out.println(RED + "Invalid input. Please enter a number." + RESET);
            }
        }
        
        // Create player based on class choice
        switch (classChoice) {
            case 1:
                player = new Player(name, "Infiltrator", 80, 15, 25);
                break;
            case 2:
                player = new Player(name, "CyberPunk", 100, 20, 15);
                break;
            case 3:
                player = new Player(name, "NetRunner", 70, 10, 30);
                break;
        }
        
        // Create game world
        createGameWorld();
        currentLocation = gameMap.get("hideout");
        discoveredLocations.add("hideout");
        
        // Create quests
        createQuests();
        
        // Create factions
        createFactions();
        
        // Initialize skills
        player.initializeSkills();
        
        // Tutorial and story introduction
        printSlow(YELLOW + "\nThe world's most secure server - The Gibson - awaits your infiltration." + RESET);
        printSlow(RED + "Your mission: steal the CryptoCoin algorithm and escape undetected." + RESET);
        printSlow(PURPLE + "Fail, and you'll be erased from existence.\n" + RESET);
        
        // Show a basic tutorial
        printSlow(CYAN + "\n=== TUTORIAL ===" + RESET);
        printSlow(GREEN + "Welcome to Pnguin's Cyber Heist! Here's how to play:" + RESET);
        printSlow(YELLOW + "- Navigate using 'go [location]' to move between areas" + RESET);
        printSlow(YELLOW + "- Use 'hack [target]' to break into digital systems" + RESET);
        printSlow(YELLOW + "- Collect items with 'take [item]' to build your arsenal" + RESET);
        printSlow(YELLOW + "- Type 'help' for more commands" + RESET);
        
        printSlow(PURPLE + "\nReady to begin your cyber heist? (Press Enter)" + RESET);
        scanner.nextLine();
        
        // Activate first quest
        activateQuest("main_quest_1");
    }

    /**
     * Main game loop
     */
    private static void gameLoop() {
        boolean running = true;
        
        while (running) {
            // Check if player is dead
            if (player.getHealth() <= 0) {
                gameOver("You have been defeated...");
                return;
            }
            
            // Process random events
            if (random.nextInt(100) < 5) {
                triggerRandomEvent();
            }
            
            // Display current location
            displayLocationInfo();
            
            // Process user input
            System.out.print(YELLOW + "\n> " + RESET);
            String input = scanner.nextLine().toLowerCase().trim();
            
            if (input.equalsIgnoreCase("quit")) {
                if (confirmAction("Are you sure you want to quit? Unsaved progress will be lost (y/n): ")) {
                    System.out.println(RED + "Shutting down systems..." + RESET);
                    running = false;
                }
                continue;
            }
            
            // Process command
            running = processCommand(input);
            
            // Update game state
            gameState.incrementTurn();
            
            // Check win conditions
            if (checkWinCondition()) {
                gameWon();
                running = false;
            }
            
            // Check quest updates
            updateQuests();
        }
    }
    
    /**
     * Trigger a random event
     */
    private static void triggerRandomEvent() {
        int eventType = random.nextInt(4);
        
        switch (eventType) {
            case 0: // Security sweep
                if (!currentLocation.getName().equalsIgnoreCase("hideout")) {
                    printSlow(RED + "\n[SECURITY ALERT] A security sweep is in progress!" + RESET);
                    if (player.checkSkill("stealth")) {
                        printSlow(GREEN + "You use your stealth skills to hide from the security patrol." + RESET);
                    } else {
                        Enemy guard = new Enemy("Security Patrol", 30 + difficulty * 10, 10 + difficulty * 3);
                        currentLocation.addEnemy(guard);
                        printSlow(RED + "A security guard has spotted you!" + RESET);
                    }
                }
                break;
            case 1: // System glitch
                printSlow(PURPLE + "\n[SYSTEM GLITCH] You notice a temporary vulnerability in nearby systems." + RESET);
                gameState.setTemporaryBonus("hack", 10, 3);
                printSlow(GREEN + "Hacking skill temporarily boosted by 10 for 3 turns!" + RESET);
                break;
            case 2: // Digital trace
                if (gameState.getAlertLevel() < 5) {
                    printSlow(YELLOW + "\n[DIGITAL TRACE] Your activities have left some traces in the system." + RESET);
                    gameState.increaseAlertLevel(1);
                    printSlow(RED + "Security alert level increased to " + gameState.getAlertLevel() + "!" + RESET);
                }
                break;
            case 3: // Equipment malfunction
                printSlow(RED + "\n[EQUIPMENT MALFUNCTION] Your gear is malfunctioning!" + RESET);
                if (player.checkSkill("tech")) {
                    printSlow(GREEN + "You quickly repair it using your technical skills." + RESET);
                } else {
                    player.applyStatusEffect("Equipment Malfunction", "Your gear is malfunctioning", -5, 0, -5, 3);
                    printSlow(RED + "Your effectiveness is reduced for 3 turns!" + RESET);
                }
                break;
        }
    }
    
    /**
     * Display location information
     */
    private static void displayLocationInfo() {
        clearScreen();
        System.out.println(CYAN + BOLD + "╔═══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║ LOCATION: " + padRight(currentLocation.getName().toUpperCase(), 67) + " ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════════════════════╣");
        
        // Display alert level
        int alertLevel = gameState.getAlertLevel();
        String alertText = "ALERT LEVEL: ";
        System.out.print("║ " + alertText);
        
        for (int i = 1; i <= 5; i++) {
            if (i <= alertLevel) {
                System.out.print(RED + "■ " + CYAN);
            } else {
                System.out.print("□ ");
            }
        }
        
        // Display stats on the right side
        String statsText = "HP: " + player.getHealth() + "/" + player.getMaxHealth() + 
                           " | ATK: " + player.getAttackPower() + 
                           " | HACK: " + player.getHackingSkill();
        int padding = 80 - alertText.length() - statsText.length() - 10 - 3;
        for (int i = 0; i < padding; i++) {
            System.out.print(" ");
        }
        System.out.println(statsText + " ║");
        
        System.out.println("╠═══════════════════════════════════════════════════════════════════════════════╣" + RESET);
        
        // Display description
        System.out.println(YELLOW + "║ " + wordWrap(currentLocation.getDescription(), 77) + " ║" + RESET);
        
        System.out.println(CYAN + "╠═══════════════════════════════════════════════════════════════════════════════╣" + RESET);
        
        // Display items
        if (!currentLocation.getItems().isEmpty()) {
            System.out.println(GREEN + "║ ITEMS:                                                                         ║");
            for (Item item : currentLocation.getItems()) {
                System.out.println("║ - " + padRight(item.getName() + ": " + item.getDescription(), 77) + " ║");
            }
            System.out.println(CYAN + "╠═══════════════════════════════════════════════════════════════════════════════╣" + RESET);
        }
        
        // Display enemies
        if (!currentLocation.getEnemies().isEmpty()) {
            System.out.println(RED + "║ ENEMIES:                                                                       ║");
            for (Enemy enemy : currentLocation.getEnemies()) {
                System.out.println("║ - " + padRight(enemy.getName() + " (Health: " + enemy.getHealth() + ")", 77) + " ║");
            }
            System.out.println(CYAN + "╠═══════════════════════════════════════════════════════════════════════════════╣" + RESET);
        }
        
        // Display hackable objects
        List<Hackable> hackables = currentLocation.getHackables();
        if (!hackables.isEmpty()) {
            System.out.println(PURPLE + "║ HACKABLE SYSTEMS:                                                              ║");
            for (Hackable hackable : hackables) {
                System.out.println("║ - " + padRight(hackable.getName() + " (Difficulty: " + hackable.getDifficulty() + ")", 77) + " ║");
            }
            System.out.println(CYAN + "╠═══════════════════════════════════════════════════════════════════════════════╣" + RESET);
        }
        
        // Display connections
        System.out.println(BLUE + "║ CONNECTIONS:                                                                   ║");
        for (String connection : currentLocation.getConnections()) {
            if (discoveredLocations.contains(connection.toLowerCase())) {
                System.out.println("║ - " + padRight(connection, 77) + " ║");
            } else {
                System.out.println("║ - " + padRight(connection + " (Undiscovered)", 77) + " ║");
            }
        }
        
        System.out.println(CYAN + "╚═══════════════════════════════════════════════════════════════════════════════╝" + RESET);
        
        // Display active objectives
        displayActiveQuests();
    }
    
    /**
     * Display active quests
     */
    private static void displayActiveQuests() {
        List<Quest> activeQuests = new ArrayList<>();
        for (Quest quest : quests.values()) {
            if (quest.isActive() && !quest.isCompleted()) {
                activeQuests.add(quest);
            }
        }
        
        if (!activeQuests.isEmpty()) {
            System.out.println(PURPLE + "\nACTIVE OBJECTIVES:" + RESET);
            for (Quest quest : activeQuests) {
                System.out.println(YELLOW + "- " + quest.getName() + ": " + quest.getCurrentObjective() + RESET);
            }
        }
    }
    
    /**
     * Pad a string with spaces to the right
     */
    private static String padRight(String s, int n) {
        if (s.length() > n) {
            return s.substring(0, n);
        }
        return String.format("%1$-" + n + "s", s);
    }
    
    /**
     * Word wrap a string to fit within a specific width
     */
    private static String wordWrap(String text, int width) {
        StringBuilder wrapped = new StringBuilder();
        StringBuilder line = new StringBuilder();
        
        for (String word : text.split("\\s+")) {
            if (line.length() + word.length() + 1 <= width) {
                if (line.length() > 0) {
                    line.append(" ");
                }
                line.append(word);
            } else {
                wrapped.append(line).append("\n║ ");
                line = new StringBuilder(word);
            }
        }
        
        if (line.length() > 0) {
            wrapped.append(line);
        }
        
        return wrapped.toString();
    }
    
    /**
     * Process a user command
     */
    private static boolean processCommand(String input) {
        String[] parts = input.split("\\s+", 2);
        String command = parts[0].toLowerCase();
        String argument = parts.length > 1 ? parts[1].toLowerCase() : "";
        
        switch (command) {
            case "go":
                commandGo(argument);
                break;
            case "look":
                // Already showing description
                break;
            case "take":
                commandTake(argument);
                break;
            case "drop":
                commandDrop(argument);
                break;
            case "hack":
                commandHack(argument);
                break;
            case "inventory":
            case "inv":
            case "i":
                commandInventory();
                break;
            case "equip":
                commandEquip(argument);
                break;
            case "unequip":
                commandUnequip(argument);
                break;
            case "attack":
            case "fight":
                commandAttack(argument);
                break;
            case "stealth":
                commandStealth();
                break;
            case "use":
                commandUse(argument);
                break;
            case "skills":
                commandSkills();
                break;
            case "upgrade":
                commandUpgradeSkill(argument);
                break;
            case "map":
                displayMap();
                break;
            case "quests":
                commandQuests();
                break;
            case "status":
                commandStatus();
                break;
            case "save":
                commandSave();
                break;
            case "load":
                return commandLoad();
            case "help":
                commandHelp();
                break;
            case "clear":
                clearScreen();
                break;
            default:
                System.out.println(RED + "Unknown command. Type 'help' for a list of commands." + RESET);
        }
        
        return true;
    }
    
    /**
     * Command to move to a different location
     */
    private static void commandGo(String argument) {
        if (argument.isEmpty()) {
            System.out.println(YELLOW + "Go where? Type a location name." + RESET);
            System.out.println(BLUE + "Available connections from here:" + RESET);
            for (String connection : currentLocation.getConnections()) {
                System.out.println(CYAN + "- " + connection + RESET);
            }
            return;
        }
        
        // Check if there are enemies preventing movement
        if (!currentLocation.getEnemies().isEmpty() && !player.isStealthActive()) {
            System.out.println(RED + "You can't leave while enemies are present! Defeat them or use stealth." + RESET);
            return;
        }
        
        Location newLocation = null;
        String targetLocationName = "";
        
        // Try to find the location by name (case insensitive)
        for (Map.Entry<String, Location> entry : gameMap.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(argument)) {
                newLocation = entry.getValue();
                targetLocationName = entry.getKey().toLowerCase();
                break;
            }
        }
        
        if (newLocation != null && currentLocation.getConnections().contains(argument.toLowerCase())) {
            // Check if this is a secured location
            if (newLocation.isSecured() && !player.hasSecurityClearance(newLocation.getSecurityLevel())) {
                System.out.println(RED + "Access denied! You need a higher security clearance level." + RESET);
                return;
            }
            
            // Make security checks in high-security areas
            if (newLocation.getSecurityLevel() > 2 && gameState.getAlertLevel() > 3) {
                System.out.println(RED + "Security checkpoint detected!" + RESET);
                
                if (player.isStealthActive()) {
                    System.out.println(GREEN + "You use your stealth to bypass the checkpoint." + RESET);
                } else if (player.hasItem("Fake ID")) {
                    System.out.println(YELLOW + "You show your fake ID. The guard looks suspicious..." + RESET);
                    if (random.nextInt(100) < 70) {
                        System.out.println(GREEN + "The guard lets you pass." + RESET);
                    } else {
                        System.out.println(RED + "Your fake ID failed inspection! Security is alerted!" + RESET);
                        gameState.increaseAlertLevel(1);
                        Enemy guard = new Enemy("Security Officer", 40, 15);
                        currentLocation.addEnemy(guard);
                        return;
                    }
                } else {
                    System.out.println(RED + "You need stealth or a fake ID to pass this checkpoint!" + RESET);
                    return;
                }
            }
            
            // Transition to new location
            currentLocation = newLocation;
            System.out.println(GREEN + "You move to " + CYAN + newLocation.getName() + RESET);
            
            // Discover the location
            if (!discoveredLocations.contains(targetLocationName)) {
                discoveredLocations.add(targetLocationName);
                System.out.println(PURPLE + "New location discovered: " + CYAN + newLocation.getName() + RESET);
                player.addExperience(20);
            }
            
            // Check for random encounter
            if (!player.isStealthActive() && random.nextInt(100) < 15 + gameState.getAlertLevel() * 5) {
                triggerRandomEncounter();
            }
            
            // Update quest objectives that involve visiting locations
            for (Quest quest : quests.values()) {
                if (quest.isActive() && !quest.isCompleted()) {
                    quest.updateLocationVisit(targetLocationName);
                }
            }
        } else {
            System.out.println(RED + "You can't go there from here." + RESET);
            System.out.println(BLUE + "Available connections from here:" + RESET);
            for (String connection : currentLocation.getConnections()) {
                System.out.println(CYAN + "- " + connection + RESET);
            }
        }
    }
    
    /**
     * Trigger a random encounter
     */
    private static void triggerRandomEncounter() {
        int encounterType = random.nextInt(3);
        
        switch (encounterType) {
            case 0: // Security patrol
                if (currentLocation.getSecurityLevel() > 0) {
                    System.out.println(RED + "\nYou encounter a security patrol!" + RESET);
                    Enemy guard = new Enemy("Security Patrol", 30 + difficulty * 10, 10 + difficulty * 2);
                    currentLocation.addEnemy(guard);
                }
                break;
            case 1: // Dropped item
                System.out.println(GREEN + "\nYou find something on the ground..." + RESET);
                Item item = generateRandomItem();
                currentLocation.addItem(item);
                System.out.println(CYAN + "You found: " + item.getName() + RESET);
                break;
            case 2: // Digital contraband
                System.out.println(PURPLE + "\nYou find a data cache hidden in plain sight." + RESET);
                int bitcoins = random.nextInt(30) + 10;
                player.addCoins(bitcoins);
                System.out.println(YELLOW + "You extracted " + bitcoins + " BitCoins from the cache." + RESET);
                break;
        }
    }
    
    /**
     * Generate a random item
     */
    private static Item generateRandomItem() {
        String[] itemTypes = {"gear", "consumable", "valuable"};
        String type = itemTypes[random.nextInt(itemTypes.length)];
        
        switch (type) {
            case "gear":
                String[] gearNames = {"Encrypted Drive", "Neural Interface", "Signal Jammer", "Stealth Module", "Security Badge"};
                String[] gearDescriptions = {
                    "Stores data with military-grade encryption",
                    "Enhances your connection to the digital world",
                    "Prevents wireless tracking for short periods",
                    "Masks your heat signature from sensors",
                    "A forged security credential"
                };
                int idx = random.nextInt(gearNames.length);
                return new Gear(gearNames[idx], gearDescriptions[idx], "utility", random.nextInt(3) + 1, 0, 0);
            case "consumable":
                String[] consumableNames = {"Stim Pack", "Neural Booster", "Memory Chip", "Encryption Key", "ICE Breaker"};
                String[] consumableEffects = {
                    "Restores 25 health points",
                    "Increases hacking skill by 10 for 5 turns",
                    "Reveals a random undiscovered location",
                    "Bypasses a single encryption challenge",
                    "Instantly defeats a single digital enemy"
                };
                idx = random.nextInt(consumableNames.length);
                return new Consumable(consumableNames[idx], consumableEffects[idx], "utility");
            default: // valuable
                String[] valuableNames = {"Corporate Data Fragment", "Crypto Wallet", "Proprietary Algorithm", "Access Codes", "Blackmail Material"};
                int value = (random.nextInt(5) + 1) * 10;
                return new Valuable(valuableNames[random.nextInt(valuableNames.length)], "Worth " + value + " BitCoins", value);
        }
    }
    
    /**
     * Command to take an item
     */
    private static void commandTake(String argument) {
        if (argument.isEmpty()) {
            System.out.println(YELLOW + "Take what? Type an item name." + RESET);
            return;
        }
        
        boolean found = false;
        Iterator<Item> iterator = currentLocation.getItems().iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            if (item.getName().equalsIgnoreCase(argument)) {
                player.addItem(item);
                iterator.remove();
                System.out.println(GREEN + "You took the " + CYAN + item.getName() + RESET);
                
                // Update quest objectives that involve collecting items
                for (Quest quest : quests.values()) {
                    if (quest.isActive() && !quest.isCompleted()) {
                        quest.updateItemCollection(item.getName());
                    }
                }
                
                found = true;
                break;
            }
        }
        
        if (!found) {
            System.out.println(RED + "There's no " + argument + " here." + RESET);
        }
    }
    
    /**
     * Command to drop an item
     */
    private static void commandDrop(String argument) {
        if (argument.isEmpty()) {
            System.out.println(YELLOW + "Drop what? Type an item name." + RESET);
            return;
        }
        
        Item itemToDrop = player.removeItem(argument);
        if (itemToDrop != null) {
            currentLocation.addItem(itemToDrop);
            System.out.println(YELLOW + "You dropped the " + CYAN + itemToDrop.getName() + RESET);
        } else {
            System.out.println(RED + "You don't have that item." + RESET);
        }
    }
    
    /**
     * Command to hack a target
     */
    private static void commandHack(String argument) {
        if (argument.isEmpty()) {
            System.out.println(YELLOW + "Hack what? Type a target name." + RESET);
            System.out.println(PURPLE + "Hackable targets in this area:" + RESET);
            
            List<Hackable> hackables = currentLocation.getHackables();
            if (hackables.isEmpty()) {
                System.out.println(RED + "There are no hackable targets here." + RESET);
            } else {
                for (Hackable hackable : hackables) {
                    System.out.println(CYAN + "- " + hackable.getName() + " (Difficulty: " + hackable.getDifficulty() + ")" + RESET);
                }
            }
            return;
        }
        
        // Check for hackable targets in the current location
        Hackable target = null;
        for (Hackable hackable : currentLocation.getHackables()) {
            if (hackable.getName().equalsIgnoreCase(argument)) {
                target = hackable;
                break;
            }
        }
        
        if (target == null) {
            System.out.println(RED + "There's nothing to hack here with that name." + RESET);
            return;
        }
        
        // Check for required items
        List<String> requiredItems = target.getRequiredItems();
        boolean hasRequiredItems = true;
        for (String itemName : requiredItems) {
            if (!player.hasItem(itemName)) {
                hasRequiredItems = false;
                System.out.println(RED + "You need a " + itemName + " to hack this target." + RESET);
            }
        }
        
        if (!hasRequiredItems) {
            return;
        }
        
        // Start hack attempt
        System.out.println(PURPLE + "INITIATING HACK ATTEMPT: " + target.getName() + RESET);
        
        // Apply any temporary bonuses
        int hackingSkill = player.getHackingSkill() + gameState.getTemporaryBonus("hack");
        
        // Calculate success chance
        int difficulty = target.getDifficulty() + (difficulty * 5);
        int successChance = Math.min(95, Math.max(5, 50 + hackingSkill - difficulty));
        
        System.out.println(YELLOW + "Hack attempt difficulty: " + difficulty + RESET);
        System.out.println(YELLOW + "Your hacking skill: " + hackingSkill + RESET);
        System.out.println(YELLOW + "Success chance: " + successChance + "%" + RESET);
        
        // Perform hack
        boolean success = random.nextInt(100) < successChance;
        animateHacking();
        
        if (success) {
            System.out.println(GREEN + BOLD + "HACK SUCCESSFUL!" + RESET);
            
            // Process the hack rewards
            target.onSuccessfulHack(player);
            
            // Update quest objectives
            for (Quest quest : quests.values()) {
                if (quest.isActive() && !quest.isCompleted()) {
                    quest.updateHackTarget(target.getName());
                }
            }
            
            // Award experience
            int expGain = target.getDifficulty() * 5;
            player.addExperience(expGain);
            System.out.println(PURPLE + "Gained " + expGain + " experience!" + RESET);
            
            // Special logic for mainframe hacking
            if (argument.equalsIgnoreCase("mainframe") && currentLocation.getName().equalsIgnoreCase("server room")) {
                if (!player.hasItem("CryptoCoin Algorithm")) {
                    System.out.println(GREEN + "You've obtained the CryptoCoin algorithm!" + RESET);
                    player.addItem(new Item("CryptoCoin Algorithm", "The secret to unlimited digital wealth"));
                    gameState.increaseAlertLevel(2);
                    System.out.println(RED + "SECURITY ALERT! Your intrusion has been detected!" + RESET);
                    currentLocation.addEnemy(new Enemy("Elite Security", 60, 20));
                }
            }
        } else {
            System.out.println(RED + BOLD + "HACK FAILED!" + RESET);
            
            // Process the hack failure
            target.onFailedHack(player);
            
            // Increase alert level
            gameState.increaseAlertLevel(1);
            System.out.println(RED + "Security alert level increased to " + gameState.getAlertLevel() + "!" + RESET);
            
            // Possible security response
            if (random.nextInt(100) < 30 + gameState.getAlertLevel() * 10) {
                System.out.println(RED + "Your hack attempt triggered a security response!" + RESET);
                if (target.getTargetType().equals("physical")) {
                    currentLocation.addEnemy(new Enemy("Security Response", 40, 15));
                } else {
                    currentLocation.addEnemy(new Enemy("Security Protocol", 30, 10));
                }
            }
        }
    }
    
    /**
     * Check inventory
     */
    private static void commandInventory() {
        List<Item> inventory = player.getInventory();
        
        if (inventory.isEmpty()) {
            System.out.println(YELLOW + "Your inventory is empty." + RESET);
            return;
        }
        
        System.out.println(CYAN + "╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                        INVENTORY                              ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣" + RESET);
        
        // Group items by type
        Map<String, List<Item>> groupedItems = new HashMap<>();
        groupedItems.put("Gear", new ArrayList<>());
        groupedItems.put("Consumable", new ArrayList<>());
        groupedItems.put("Quest", new ArrayList<>());
        groupedItems.put("Valuable", new ArrayList<>());
        groupedItems.put("Other", new ArrayList<>());
        
        for (Item item : inventory) {
            if (item instanceof Gear) {
                groupedItems.get("Gear").add(item);
            } else if (item instanceof Consumable) {
                groupedItems.get("Consumable").add(item);
            } else if (item instanceof QuestItem) {
                groupedItems.get("Quest").add(item);
            } else if (item instanceof Valuable) {
                groupedItems.get("Valuable").add(item);
            } else {
                groupedItems.get("Other").add(item);
            }
        }
        
        // Display equipped items
        List<Gear> equippedItems = player.getEquippedGear();
        if (!equippedItems.isEmpty()) {
            System.out.println(GREEN + "║ EQUIPPED ITEMS:                                                 ║" + RESET);
            for (Gear gear : equippedItems) {
                String stats = "";
                if (gear.getAttackBonus() > 0) stats += " ATK+" + gear.getAttackBonus();
                if (gear.getDefenseBonus() > 0) stats += " DEF+" + gear.getDefenseBonus();
                if (gear.getHackingBonus() > 0) stats += " HACK+" + gear.getHackingBonus();
                
                System.out.println(CYAN + "║ - " + padRight(gear.getName() + " (" + gear.getSlot() + ")" + stats, 62) + "║" + RESET);
            }
            System.out.println(CYAN + "╠══════════════════════════════════════════════════════════════╣" + RESET);
        }
        
        // Display items by category
        for (Map.Entry<String, List<Item>> entry : groupedItems.entrySet()) {
            List<Item> items = entry.getValue();
            if (!items.isEmpty()) {
                System.out.println(YELLOW + "║ " + padRight(entry.getKey().toUpperCase() + ":", 62) + "║" + RESET);
                
                for (Item item : items) {
                    String itemInfo = item.getName();
                    
                    if (item instanceof Gear) {
                        Gear gear = (Gear) item;
                        itemInfo += " (" + gear.getSlot() + ")";
                        
                        String stats = "";
                        if (gear.getAttackBonus() > 0) stats += " ATK+" + gear.getAttackBonus();
                        if (gear.getDefenseBonus() > 0) stats += " DEF+" + gear.getDefenseBonus();
                        if (gear.getHackingBonus() > 0) stats += " HACK+" + gear.getHackingBonus();
                        
                        if (!stats.isEmpty()) {
                            itemInfo += stats;
                        }
                    } else if (item instanceof Valuable) {
                        Valuable valuable = (Valuable) item;
                        itemInfo += " (Value: " + valuable.getValue() + " BitCoins)";
                    }
                    
                    System.out.println(BLUE + "║ - " + padRight(itemInfo, 62) + "║" + RESET);
                }
                
                System.out.println(CYAN + "╠══════════════════════════════════════════════════════════════╣" + RESET);
            }
        }
        
        // Display money
        System.out.println(YELLOW + "║ BitCoins: " + padRight(String.valueOf(player.getCoins()), 54) + "║" + RESET);
        
        System.out.println(CYAN + "╚══════════════════════════════════════════════════════════════╝" + RESET);
    }
    
    /**
     * Equip gear
     */
    private static void commandEquip(String argument) {
        if (argument.isEmpty()) {
            System.out.println(YELLOW + "Equip what? Type an item name." + RESET);
            return;
        }
        
        // Find the item in inventory
        Item item = player.getItemByName(argument);
        
        if (item == null) {
            System.out.println(RED + "You don't have an item called \"" + argument + "\" in your inventory." + RESET);
            return;
        }
        
        if (!(item instanceof Gear)) {
            System.out.println(RED + "You can't equip that. Only gear items can be equipped." + RESET);
            return;
        }
        
        Gear gear = (Gear) item;
        boolean success = player.equipGear(gear);
        
        if (success) {
            System.out.println(GREEN + "You equipped the " + gear.getName() + "." + RESET);
            
            // Display bonuses
            if (gear.getAttackBonus() > 0) {
                System.out.println(YELLOW + "Attack power increased by " + gear.getAttackBonus() + "." + RESET);
            }
            if (gear.getDefenseBonus() > 0) {
                System.out.println(YELLOW + "Defense increased by " + gear.getDefenseBonus() + "." + RESET);
            }
            if (gear.getHackingBonus() > 0) {
                System.out.println(YELLOW + "Hacking skill increased by " + gear.getHackingBonus() + "." + RESET);
            }
        } else {
            System.out.println(RED + "Failed to equip " + gear.getName() + "." + RESET);
        }
    }
    
    /**
     * Unequip gear
     */
    private static void commandUnequip(String argument) {
        if (argument.isEmpty()) {
            System.out.println(YELLOW + "Unequip what? Type an item name or slot." + RESET);
            return;
        }
        
        boolean success = player.unequipGear(argument);
        
        if (success) {
            System.out.println(YELLOW + "You unequipped the item." + RESET);
        } else {
            System.out.println(RED + "You don't have anything equipped with that name or in that slot." + RESET);
        }
    }
    
    /**
     * Attack an enemy
     */
    private static void commandAttack(String argument) {
        if (currentLocation.getEnemies().isEmpty()) {
            System.out.println(RED + "There are no enemies here to attack." + RESET);
            return;
        }
        
        if (argument.isEmpty() && currentLocation.getEnemies().size() == 1) {
            // Attack the only enemy present
            argument = currentLocation.getEnemies().get(0).getName().toLowerCase();
        } else if (argument.isEmpty()) {
            System.out.println(YELLOW + "Attack which enemy? Type an enemy name." + RESET);
            System.out.println(RED + "Enemies present:" + RESET);
            for (Enemy enemy : currentLocation.getEnemies()) {
                System.out.println(CYAN + "- " + enemy.getName() + " (Health: " + enemy.getHealth() + ")" + RESET);
            }
            return;
        }
        
        Enemy target = null;
        for (Enemy enemy : currentLocation.getEnemies()) {
            if (enemy.getName().toLowerCase().contains(argument.toLowerCase())) {
                target = enemy;
                break;
            }
        }
        
        if (target == null) {
            System.out.println(RED + "There's no enemy named \"" + argument + "\" here." + RESET);
            return;
        }
        
        // Exit stealth mode if active
        if (player.isStealthActive()) {
            System.out.println(YELLOW + "You break out of stealth mode to attack!" + RESET);
            player.deactivateStealth();
        }
        
        // Player attacks enemy
        int damage = player.getAttackPower();
        
        // Check for critical hit
        boolean criticalHit = random.nextInt(100) < player.getCriticalChance();
        if (criticalHit) {
            damage *= 2;
            System.out.println(GREEN + BOLD + "CRITICAL HIT!" + RESET);
        }
        
        target.takeDamage(damage);
        System.out.println(YELLOW + "You attack " + target.getName() + " for " + damage + " damage!" + RESET);
        
        if (target.getHealth() <= 0) {
            defeatedEnemy(target);
        } else {
            // Enemy counter-attacks
            int counterDamage = target.attack();
            
            // Apply player defense
            int defense = player.getDefense();
            counterDamage = Math.max(1, counterDamage - defense);
            
            player.takeDamage(counterDamage);
            System.out.println(RED + target.getName() + " counter-attacks for " + counterDamage + " damage!" + RESET);
            System.out.println(YELLOW + "Your health: " + player.getHealth() + "/" + player.getMaxHealth() + RESET);
            
            if (player.getHealth() <= 0) {
                gameOver("You have been defeated...");
            }
        }
    }
    
    /**
     * Process enemy defeat
     */
    private static void defeatedEnemy(Enemy enemy) {
        System.out.println(GREEN + "You defeated " + enemy.getName() + "!" + RESET);
        currentLocation.getEnemies().remove(enemy);
        
        // Gain experience
        int expGain = 30;
        player.addExperience(expGain);
        System.out.println(PURPLE + "Gained " + expGain + " experience!" + RESET);
        
        // BitCoin drop
        int coinDrop = random.nextInt(20) + 5;
        player.addCoins(coinDrop);
        System.out.println(YELLOW + "Looted " + coinDrop + " BitCoins!" + RESET);
        
        // Chance to drop an item
        if (random.nextInt(100) < 40) {
            Item drop = generateRandomItem();
            currentLocation.addItem(drop);
            System.out.println(GREEN + enemy.getName() + " dropped: " + CYAN + drop.getName() + RESET);
        }
        
        // Update quest objectives
        for (Quest quest : quests.values()) {
            if (quest.isActive() && !quest.isCompleted()) {
                quest.updateEnemyDefeat(enemy.getName());
            }
        }
    }
    
    /**
     * Activate stealth mode
     */
    private static void commandStealth() {
        if (player.isStealthActive()) {
            System.out.println(YELLOW + "You are already in stealth mode." + RESET);
            return;
        }
        
        if (!player.checkSkill("stealth")) {
            System.out.println(RED + "You don't have the stealth skill." + RESET);
            return;
        }
        
        // Activate stealth
        boolean success = player.activateStealth();
        
        if (success) {
            System.out.println(GREEN + "You activate stealth mode, becoming harder to detect." + RESET);
            System.out.println(BLUE + "While in stealth, you can move past enemies but can't attack." + RESET);
        } else {
            System.out.println(RED + "Failed to activate stealth mode." + RESET);
        }
    }
    
    /**
     * Use an item
     */
    private static void commandUse(String argument) {
        if (argument.isEmpty()) {
            System.out.println(YELLOW + "Use what? Type an item name." + RESET);
            return;
        }
        
        Item item = player.getItemByName(argument);
        
        if (item == null) {
            System.out.println(RED + "You don't have an item called \"" + argument + "\" in your inventory." + RESET);
            return;
        }
        
        if (!(item instanceof Consumable)) {
            System.out.println(RED + "You can't use that item. Only consumable items can be used directly." + RESET);
            return;
        }
        
        Consumable consumable = (Consumable) item;
        boolean success = consumable.use(player);
        
        if (success) {
            player.removeItem(consumable.getName());
            System.out.println(GREEN + "You used the " + consumable.getName() + "." + RESET);
        } else {
            System.out.println(RED + "You can't use that item right now." + RESET);
        }
    }
    
    /**
     * View and upgrade skills
     */
    private static void commandSkills() {
        Map<String, Integer> skills = player.getSkills();
        int skillPoints = player.getSkillPoints();
        
        System.out.println(CYAN + "╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                        SKILLS                                 ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣" + RESET);
        
        System.out.println(YELLOW + "║ Skill Points Available: " + padRight(String.valueOf(skillPoints), 40) + "║" + RESET);
        System.out.println(CYAN + "╠══════════════════════════════════════════════════════════════╣" + RESET);
        
        for (Map.Entry<String, Integer> entry : skills.entrySet()) {
            String skillName = entry.getKey();
            int level = entry.getValue();
            
            String skillDesc = getSkillDescription(skillName, level);
            
            System.out.println(BLUE + "║ " + padRight(capitalize(skillName) + " (Level " + level + ")", 62) + "║" + RESET);
            System.out.println(GREEN + "║ " + padRight(skillDesc, 62) + "║" + RESET);
            
            // Show upgrade info if skill points available
            if (skillPoints > 0 && level < 5) {
                System.out.println(PURPLE + "║ " + padRight("Upgrade cost: 1 point", 62) + "║" + RESET);
                String upgradeDesc = getSkillDescription(skillName, level + 1);
                System.out.println(YELLOW + "║ " + padRight("Next level: " + upgradeDesc, 62) + "║" + RESET);
            }
            
            System.out.println(CYAN + "╠══════════════════════════════════════════════════════════════╣" + RESET);
        }
        
        System.out.println(PURPLE + "║ Type 'upgrade [skill name]' to spend your skill points        ║" + RESET);
        System.out.println(CYAN + "╚══════════════════════════════════════════════════════════════╝" + RESET);
    }
    
    /**
     * Get skill description
     */
    private static String getSkillDescription(String skillName, int level) {
        switch (skillName.toLowerCase()) {
            case "hacking":
                return "Hacking ability +" + (level * 5) + ". Improves success chance with digital systems.";
            case "stealth":
                return "Stealth duration: " + (level * 3) + " turns. Ability to move undetected.";
            case "combat":
                return "Attack power +" + (level * 3) + ". Critical hit chance: " + (level * 5) + "%.";
            case "engineering":
                return "Repair and craft items. Can create " + level + " items per day.";
            case "tech":
                return (level > 0 ? "Can repair malfunctioning equipment. " : "") + "Tech skill level " + level + ".";
            default:
                return "Unknown skill";
        }
    }
    
    /**
     * Upgrade skill
     */
    private static void commandUpgradeSkill(String argument) {
        if (argument.isEmpty()) {
            System.out.println(YELLOW + "Upgrade which skill? Type a skill name." + RESET);
            return;
        }
        
        int skillPoints = player.getSkillPoints();
        
        if (skillPoints <= 0) {
            System.out.println(RED + "You don't have any skill points to spend." + RESET);
            return;
        }
        
        boolean success = player.upgradeSkill(argument.toLowerCase());
        
        if (success) {
            System.out.println(GREEN + "You upgraded your " + argument + " skill!" + RESET);
            
            // Show the new skill level and description
            int newLevel = player.getSkillLevel(argument.toLowerCase());
            System.out.println(CYAN + capitalize(argument) + " is now level " + newLevel + RESET);
            System.out.println(BLUE + getSkillDescription(argument.toLowerCase(), newLevel) + RESET);
        } else {
            System.out.println(RED + "You don't have a skill called \"" + argument + "\" or it's already at maximum level." + RESET);
        }
    }
    
    /**
     * Display the map
     */
    private static void displayMap() {
        clearScreen();
        System.out.println(CYAN + BOLD + "╔═══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                  NETWORK MAP                                    ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════════════════════╣" + RESET);
        
        // ASCII art map
        String[] mapLines = {
            "  +---------------+     +---------------+",
            "  |    HIDEOUT    |     |  NEON DISTRICT|-----+",
            "  +-------+-------+     +-------+-------+     |",
            "          |                     |             |",
            "          |                     |             |",
            "          |    +---------------++            ++---------------+",
            "          |    |DATA MARKET    +------------+|DARKNET        |",
            "          |    +-------+-------+             +-------+-------+",
            "          |            |                             |",
            "          |            |                             |",
            "  +-------v-------+    |              +-------------v-+",
            "  |CORPORATE PLAZA+----+              |BLACK MARKET   |",
            "  +-------+-------+                   +-------+-------+",
            "          |                                   |",
            "    +-----+------+                     +-----v------+",
            "    |            |                     |            |",
            "  +-v----------+ |                     | +----------v-+",
            "  |TECH SECTOR +-+                     +-+ FIXER DEN  |",
            "  +-----+------+                         +------+-----+",
            "        |                                       |",
            "  +-----v------+                         +------v-----+",
            "  |RESEARCH LAB|                         |ESCAPE ROUTE|",
            "  +-----+------+                         +------+-----+",
            "        |                                       ^",
            "  +-----v------+    +---------------+    +------+-----+",
            "  |SERVER FARM |    |SECURITY CHKPT |    | THE GIBSON  |",
            "  +------------+    +-------+-------+    +------------+",
            "                            |                   ^",
            "                     +------v------+           |",
            "                     |SERVER ROOM  +-----------+",
            "                     +-------+-----+           |",
            "                             |                 |",
            "                    +--------v--------+        |",
            "                    |EXECUTIVE SUITE  +--------+",
            "                    +-----------------+"
        };
        
        for (String line : mapLines) {
            System.out.println(BLUE + "║ " + line + " ║" + RESET);
        }
        
        System.out.println(CYAN + "╠═══════════════════════════════════════════════════════════════════════════════╣" + RESET);
        
        // Display location list
        System.out.println(BLUE + "║ DISCOVERED LOCATIONS:                                                         ║" + RESET);
        
        List<String> discoveredList = new ArrayList<>(discoveredLocations);
        Collections.sort(discoveredList);
        
        for (String location : discoveredList) {
            if (gameMap.containsKey(location)) {
                String locName = gameMap.get(location).getName();
                String secLevel = "Security Level: " + gameMap.get(location).getSecurityLevel();
                
                String marker = location.equals(currentLocation.getName().toLowerCase()) ? " << YOU ARE HERE" : "";
                System.out.println(GREEN + "║ - " + padRight(locName + " (" + secLevel + ")" + marker, 77) + "║" + RESET);
            }
        }
        
        System.out.println(CYAN + "╚═══════════════════════════════════════════════════════════════════════════════╝" + RESET);
        
        System.out.print(YELLOW + "Press Enter to continue..." + RESET);
        scanner.nextLine();
    }
    
    /**
     * View quests
     */
    private static void commandQuests() {
        System.out.println(PURPLE + "╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                        QUESTS                                 ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣" + RESET);
        
        boolean hasQuests = false;
        
        // Active quests
        List<Quest> activeQuests = new ArrayList<>();
        for (Quest quest : quests.values()) {
            if (quest.isActive() && !quest.isCompleted()) {
                activeQuests.add(quest);
            }
        }
        
        if (!activeQuests.isEmpty()) {
            hasQuests = true;
            System.out.println(GREEN + "║ ACTIVE QUESTS:                                                ║" + RESET);
            
            for (Quest quest : activeQuests) {
                System.out.println(YELLOW + "║ " + padRight(quest.getName() + " (" + quest.getType() + ")", 62) + "║" + RESET);
                System.out.println(BLUE + "║ " + padRight(quest.getDescription(), 62) + "║" + RESET);
                System.out.println(CYAN + "║ " + padRight("Current objective: " + quest.getCurrentObjective(), 62) + "║" + RESET);
                System.out.println(PURPLE + "╠══════════════════════════════════════════════════════════════╣" + RESET);
            }
        }
        
        // Completed quests
        List<Quest> completedQuests = new ArrayList<>();
        for (Quest quest : quests.values()) {
            if (quest.isCompleted()) {
                completedQuests.add(quest);
            }
        }
        
        if (!completedQuests.isEmpty()) {
            hasQuests = true;
            System.out.println(GREEN + "║ COMPLETED QUESTS:                                             ║" + RESET);
            
            for (Quest quest : completedQuests) {
                System.out.println("║ " + padRight(quest.getName() + " (" + quest.getType() + ")", 62) + "║" + RESET);
            }
            
            System.out.println(PURPLE + "╠══════════════════════════════════════════════════════════════╣" + RESET);
        }
        
        if (!hasQuests) {
            System.out.println(YELLOW + "║ No quests available yet. Explore the world to find missions.   ║" + RESET);
            System.out.println(PURPLE + "╠══════════════════════════════════════════════════════════════╣" + RESET);
        }
        
        System.out.println(PURPLE + "╚══════════════════════════════════════════════════════════════╝" + RESET);
        
        System.out.print(YELLOW + "Press Enter to continue..." + RESET);
        scanner.nextLine();
    }
    
    /**
     * View player status
     */
    private static void commandStatus() {
        System.out.println(CYAN + "╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                     HACKER STATUS                              ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣" + RESET);
        
        // Basic stats
        System.out.println(GREEN + "║ " + padRight("Name: " + player.getName(), 62) + "║" + RESET);
        System.out.println(GREEN + "║ " + padRight("Class: " + player.getClassName(), 62) + "║" + RESET);
        System.out.println(GREEN + "║ " + padRight("Level: " + player.getLevel() + " (XP: " + player.getExperience() + "/" + player.getExpForNextLevel() + ")", 62) + "║" + RESET);
        System.out.println(GREEN + "║ " + padRight("BitCoins: " + player.getCoins(), 62) + "║" + RESET);
        System.out.println(CYAN + "╠══════════════════════════════════════════════════════════════╣" + RESET);
        
        // Character stats
        System.out.println(YELLOW + "║ STATISTICS:                                                   ║" + RESET);
        System.out.println(BLUE + "║ " + padRight("Health: " + player.getHealth() + "/" + player.getMaxHealth(), 62) + "║" + RESET);
        System.out.println(BLUE + "║ " + padRight("Attack Power: " + player.getAttackPower(), 62) + "║" + RESET);
        System.out.println(BLUE + "║ " + padRight("Defense: " + player.getDefense(), 62) + "║" + RESET);
        System.out.println(BLUE + "║ " + padRight("Hacking Skill: " + player.getHackingSkill(), 62) + "║" + RESET);
        System.out.println(BLUE + "║ " + padRight("Critical Chance: " + player.getCriticalChance() + "%", 62) + "║" + RESET);
        System.out.println(BLUE + "║ " + padRight("Security Clearance: Level " + player.getSecurityClearance(), 62) + "║" + RESET);
        System.out.println(CYAN + "╠══════════════════════════════════════════════════════════════╣" + RESET);
        
        // Active effects
        Map<String, StatusEffect> activeEffects = player.getActiveEffects();
        if (!activeEffects.isEmpty()) {
            System.out.println(PURPLE + "║ ACTIVE EFFECTS:                                               ║" + RESET);
            
            for (StatusEffect effect : activeEffects.values()) {
                System.out.println(RED + "║ " + padRight(effect.getName() + " (" + effect.getDuration() + " turns remaining)", 62) + "║" + RESET);
                System.out.println(YELLOW + "║ " + padRight(effect.getDescription(), 62) + "║" + RESET);
            }
            
            System.out.println(CYAN + "╠══════════════════════════════════════════════════════════════╣" + RESET);
        }
        
        // Faction standings
        System.out.println(GREEN + "║ FACTION STANDINGS:                                            ║" + RESET);
        
        for (Faction faction : factions.values()) {
            int standing = faction.getStanding();
            String standingName = getStandingName(standing);
            String color = getStandingColor(standing);
            
            System.out.println(color + "║ " + padRight(faction.getName() + ": " + standingName + " (" + standing + ")", 62) + "║" + RESET);
        }
        
        System.out.println(CYAN + "╚══════════════════════════════════════════════════════════════╝" + RESET);
        
        System.out.print(YELLOW + "Press Enter to continue..." + RESET);
        scanner.nextLine();
    }
    
    /**
     * Get faction standing name
     */
    private static String getStandingName(int standing) {
        if (standing <= -75) return "Hunted";
        if (standing <= -50) return "Hostile";
        if (standing <= -25) return "Unfriendly";
        if (standing <= 25) return "Neutral";
        if (standing <= 50) return "Friendly";
        if (standing <= 75) return "Honored";
        return "Exalted";
    }
    
    /**
     * Get faction standing color
     */
    private static String getStandingColor(int standing) {
        if (standing <= -50) return RED;
        if (standing <= -25) return YELLOW;
        if (standing <= 25) return BLUE;
        if (standing <= 75) return GREEN;
        return PURPLE;
    }
    
    /**
     * Save the game
     */
    private static void commandSave() {
        System.out.print(YELLOW + "Enter a name for your save file (or press Enter for auto-name): " + RESET);
        String saveName = scanner.nextLine().trim();
        
        if (saveName.isEmpty()) {
            saveName = "save_" + player.getName() + "_" + System.currentTimeMillis();
        }
        
        boolean success = saveCurrentGame(saveName);
        
        if (success) {
            System.out.println(GREEN + "Game saved successfully as '" + saveName + "'." + RESET);
        } else {
            System.out.println(RED + "Failed to save the game. Please try again." + RESET);
        }
    }
    
    /**
     * Save current game
     */
    private static boolean saveCurrentGame(String saveName) {
        try {
            // Create saves directory if it doesn't exist
            File saveDir = new File("saves");
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            
            // Create save data
            SaveData saveData = new SaveData(
                player,
                currentLocation.getName().toLowerCase(),
                gameState,
                discoveredLocations
            );
            
            // Write save data to file
            File saveFile = new File(saveDir, saveName + ".sav");
            FileOutputStream fileOut = new FileOutputStream(saveFile);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(saveData);
            out.close();
            fileOut.close();
            
            return true;
        } catch (Exception e) {
            System.out.println(RED + "Error saving game: " + e.getMessage() + RESET);
            return false;
        }
    }
    
    /**
     * Load a saved game
     */
    private static boolean loadGame() {
        clearScreen();
        System.out.println(GREEN + "╔══════════════════════════════════════════╗");
        System.out.println("║             LOAD GAME                     ║");
        System.out.println("╚══════════════════════════════════════════╝" + RESET);
        
        File saveDir = new File("saves");
        if (!saveDir.exists() || !saveDir.isDirectory()) {
            System.out.println(RED + "No save files found." + RESET);
            System.out.println(YELLOW + "\nPress Enter to return to the main menu..." + RESET);
            scanner.nextLine();
            return false;
        }
        
        File[] saveFiles = saveDir.listFiles((dir, name) -> name.endsWith(".sav"));
        if (saveFiles == null || saveFiles.length == 0) {
            System.out.println(RED + "No save files found." + RESET);
            System.out.println(YELLOW + "\nPress Enter to return to the main menu..." + RESET);
            scanner.nextLine();
            return false;
        }
        
        System.out.println(YELLOW + "Available save files:\n" + RESET);
        for (int i = 0; i < saveFiles.length; i++) {
            String fileName = saveFiles[i].getName();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String lastModified = sdf.format(new Date(saveFiles[i].lastModified()));
            
            System.out.println(CYAN + (i + 1) + ". " + fileName.replace(".sav", "") + 
                               " - Last modified: " + lastModified + RESET);
        }
        
        System.out.println("\n" + YELLOW + "Enter the number of the save file to load (or 0 to cancel): " + RESET);
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0) {
                return false;
            }
            if (choice < 1 || choice > saveFiles.length) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            System.out.println(RED + "Invalid choice. Press Enter to return to the main menu." + RESET);
            scanner.nextLine();
            return false;
        }
        
        File selectedSave = saveFiles[choice - 1];
        try {
            FileInputStream fileIn = new FileInputStream(selectedSave);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            SaveData saveData = (SaveData) in.readObject();
            in.close();
            fileIn.close();
            
            player = saveData.getPlayer();
            currentLocation = gameMap.get(saveData.getCurrentLocationName());
            gameState = saveData.getGameState();
            discoveredLocations = saveData.getDiscoveredLocations();
            
            System.out.println(GREEN + "Game loaded successfully!" + RESET);
            sleep(1000);
            return true;
        } catch (Exception e) {
            System.out.println(RED + "Failed to load the save file: " + e.getMessage() + RESET);
            System.out.println(YELLOW + "\nPress Enter to return to the main menu..." + RESET);
            scanner.nextLine();
            return false;
        }
    }
    
    /**
     * Load game command
     */
    private static boolean commandLoad() {
        if (confirmAction("Loading a game will lose unsaved progress. Continue? (y/n): ")) {
            return loadGame();
        }
        return true;
    }
    
    /**
     * Display help
     */
    private static void commandHelp() {
        System.out.println(CYAN + "╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                       HELP MENU                                ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣" + RESET);
        
        // Movement and exploration
        System.out.println(YELLOW + "║ MOVEMENT & EXPLORATION:                                        ║" + RESET);
        System.out.println(GREEN + "║ - go [location]     : Move to a connected location             ║");
        System.out.println("║ - look               : Look around current location               ║");
        System.out.println("║ - map                : Display the network map                    ║");
        System.out.println("║ - stealth            : Enter stealth mode to avoid enemies        ║" + RESET);
        
        // Items and inventory
        System.out.println(YELLOW + "║ ITEMS & INVENTORY:                                             ║" + RESET);
        System.out.println(BLUE + "║ - take [item]       : Pick up an item                           ║");
        System.out.println("║ - drop [item]       : Drop an item from inventory                 ║");
        System.out.println("║ - inventory/inv/i   : Check your inventory                        ║");
        System.out.println("║ - equip [item]      : Equip a gear item                           ║");
        System.out.println("║ - unequip [item]    : Unequip a gear item                         ║");
        System.out.println("║ - use [item]        : Use a consumable item                       ║" + RESET);
        
        // Combat and hacking
        System.out.println(YELLOW + "║ COMBAT & HACKING:                                              ║" + RESET);
        System.out.println(RED + "║ - attack/fight [enemy] : Attack an enemy                       ║");
        System.out.println("║ - hack [target]     : Attempt to hack a system                  ║" + RESET);
        
        // Character
        System.out.println(YELLOW + "║ CHARACTER:                                                     ║" + RESET);
        System.out.println(CYAN + "║ - status             : Check your character status               ║");
        System.out.println("║ - skills             : View and manage your skills                 ║");
        System.out.println("║ - upgrade [skill]    : Upgrade a skill using skill points          ║" + RESET);
        
        // Quests and system
        System.out.println(YELLOW + "║ QUESTS & SYSTEM:                                               ║" + RESET);
        System.out.println(PURPLE + "║ - quests             : View active and completed quests         ║");
        System.out.println("║ - save               : Save your game                           ║");
        System.out.println("║ - load               : Load a saved game                        ║");
        System.out.println("║ - clear              : Clear the screen                         ║");
        System.out.println("║ - help               : Show this help menu                      ║");
        System.out.println("║ - quit               : Exit the game                            ║" + RESET);
        
        System.out.println(CYAN + "╚══════════════════════════════════════════════════════════════╝" + RESET);
        
        System.out.print(YELLOW + "Press Enter to continue..." + RESET);
        scanner.nextLine();
    }
    
    /**
     * Animate hacking
     */
    private static void animateHacking() {
        if (fastMode) {
            System.out.println(PURPLE + "HACKING IN PROGRESS..." + RESET);
            sleep(500);
            return;
        }
        
        String[] hackingMessages = {
            "Bypassing firewall...",
            "Cracking encryption...",
            "Exploiting vulnerability...",
            "Accessing mainframe..."
        };
        
        for (String message : hackingMessages) {
            System.out.print(PURPLE + message + RESET);
            for (int i = 0; i < 3; i++) {
                System.out.print(".");
                sleep(150);
            }
            System.out.println(" " + (random.nextBoolean() ? GREEN + "OK" : YELLOW + "BYPASSED") + RESET);
            sleep(200);
        }
    }
    
    /**
     * Update quests
     */
    private static void updateQuests() {
        for (Quest quest : quests.values()) {
            if (quest.isActive() && !quest.isCompleted() && quest.checkCompletion()) {
                completeQuest(quest);
            }
        }
    }
    
    /**
     * Complete a quest
     */
    private static void completeQuest(Quest quest) {
        System.out.println(GREEN + BOLD + "QUEST COMPLETED: " + quest.getName() + RESET);
        System.out.println(YELLOW + "You have completed the objective: " + quest.getCurrentObjective() + RESET);
        
        // Give rewards
        int expReward = quest.getExpReward();
        player.addExperience(expReward);
        System.out.println(PURPLE + "Experience gained: " + expReward + RESET);
        
        int coinReward = quest.getCoinReward();
        if (coinReward > 0) {
            player.addCoins(coinReward);
            System.out.println(YELLOW + "BitCoins gained: " + coinReward + RESET);
        }
        
        Item itemReward = quest.getItemReward();
        if (itemReward != null) {
            player.addItem(itemReward);
            System.out.println(CYAN + "Item gained: " + itemReward.getName() + RESET);
        }
        
        // Update faction standings
        Map<String, Integer> factionChanges = quest.getFactionChanges();
        for (Map.Entry<String, Integer> entry : factionChanges.entrySet()) {
            if (factions.containsKey(entry.getKey())) {
                Faction faction = factions.get(entry.getKey());
                faction.changeStanding(entry.getValue());
                
                String changeText = entry.getValue() > 0 ? "increased" : "decreased";
                System.out.println(BLUE + "Reputation with " + faction.getName() + " " + changeText + 
                                 " by " + Math.abs(entry.getValue()) + RESET);
            }
        }
        
        // Check if this quest unlocks any others
        String unlockedQuestId = quest.getUnlocksQuestId();
        if (unlockedQuestId != null && !unlockedQuestId.isEmpty() && quests.containsKey(unlockedQuestId)) {
            activateQuest(unlockedQuestId);
        }
    }
    
    /**
     * Activate a quest
     */
    private static void activateQuest(String questId) {
        if (!quests.containsKey(questId)) {
            return;
        }
        
        Quest quest = quests.get(questId);
        if (!quest.isActive() && !quest.isCompleted()) {
            quest.activate();
            
            System.out.println(PURPLE + BOLD + "NEW QUEST: " + quest.getName() + RESET);
            System.out.println(BLUE + quest.getDescription() + RESET);
            System.out.println(YELLOW + "Objective: " + quest.getCurrentObjective() + RESET);
        }
    }
    
    /**
     * Check win condition
     */
    private static boolean checkWinCondition() {
        return player.hasItem("CryptoCoin Algorithm") && 
               currentLocation.getName().equalsIgnoreCase("escape route") &&
               player.hasItem("Gibson Access Codes");
    }
    
    /**
     * Game over
     */
    private static void gameOver(String message) {
        clearScreen();
        System.out.println(RED + BOLD + "╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                         GAME OVER                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝" + RESET);
        
        System.out.println(YELLOW + message + RESET);
        System.out.println(RED + "Your digital presence fades into the void..." + RESET);
        
        // Save the game for post-mortem analysis
        saveCurrentGame("game_over_" + System.currentTimeMillis());
        
        System.out.println(YELLOW + "\nPress Enter to return to the main menu..." + RESET);
        scanner.nextLine();
        
        mainMenu();
    }
    
    /**
     * Game won
     */
    private static void gameWon() {
        clearScreen();
        
        System.out.println(GREEN + BOLD + "╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                         VICTORY                               ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝" + RESET);
        
        printSlow(CYAN + "CONGRATULATIONS, " + player.getName() + "!" + RESET);
        printSlow(YELLOW + "You've successfully stolen the CryptoCoin algorithm and escaped!" + RESET);
        printSlow(PURPLE + "Your legend grows in the digital underground..." + RESET);
        
        // Display player stats
        System.out.println("\n" + BLUE + "Final Stats:" + RESET);
        System.out.println(GREEN + "Level: " + player.getLevel() + RESET);
        System.out.println(YELLOW + "BitCoins: " + player.getCoins() + RESET);
        System.out.println(PURPLE + "Total Experience: " + player.getExperience() + RESET);
        
        // Display faction standings
        System.out.println("\n" + BLUE + "Faction Standings:" + RESET);
        for (Faction faction : factions.values()) {
            int standing = faction.getStanding();
            String standingName = getStandingName(standing);
            String color = getStandingColor(standing);
            
            System.out.println(color + faction.getName() + ": " + standingName + " (" + standing + ")" + RESET);
        }
        
        printSlow(GREEN + "\nTHE END" + RESET);
        
        System.out.println(CYAN + "\nPress Enter to return to the main menu..." + RESET);
        scanner.nextLine();
        
        mainMenu();
    }
    
    /**
     * Create the game world
     */
    private static void createGameWorld() {
        // Create all game locations
        createLocations();
        
        // Add items to locations
        addItemsToLocations();
        
        // Add enemies to locations
        addEnemiesToLocations();
        
        // Add hackable objects to locations
        addHackablesToLocations();
    }
    
    /**
     * Add items to locations
     */
    private static void addItemsToLocations() {
        // Hideout items
        gameMap.get("hideout").addItem(new Item("Hacker Manual", "A beginner's guide to hacking"));
        gameMap.get("hideout").addItem(new Consumable("Energy Drink", "Restores 10 health points", "health"));
        gameMap.get("hideout").addItem(new Gear("Basic Interface", "Standard neural interface for hackers", "head", 0, 0, 5));
        
        // Neon District items
        gameMap.get("neon_district").addItem(new Consumable("Stim Pack", "Restores 25 health points", "health"));
        gameMap.get("neon_district").addItem(new Gear("Street Jacket", "Armored jacket with digital camo pattern", "body", 0, 5, 0));
        
        // Data Market items
        gameMap.get("data_market").addItem(new Consumable("Memory Chip", "Reveals a random undiscovered location", "utility"));
        gameMap.get("data_market").addItem(new Item("Market Directory", "Digital catalog of available goods"));
        
        // Corporate Plaza items
        gameMap.get("corporate_plaza").addItem(new Consumable("Corporate ID Badge", "Temporary access to low-security areas", "access"));
        
        // Black Market items
        gameMap.get("black_market").addItem(new Gear("Combat Gloves", "Reinforced gloves for combat", "hands", 5, 0, 0));
        gameMap.get("black_market").addItem(new Consumable("ICE Breaker", "Instantly defeats a single digital enemy", "combat"));
        
        // Tech Sector items
        gameMap.get("tech_sector").addItem(new Gear("Tech Specs", "Advanced digital interface glasses", "eyes", 0, 0, 10));
        
        // Research Lab items
        gameMap.get("research_lab").addItem(new Valuable("Research Data", "Valuable corporate research information", 35));
        gameMap.get("research_lab").addItem(new Consumable("Neural Booster", "Enhanced hacking abilities", "hacking"));
        
        // Server Farm items
        gameMap.get("server_farm").addItem(new Item("Server Maintenance Guide", "Contains login credentials and server maps"));
        
        // Fixer Den items
        gameMap.get("fixer_den").addItem(new Gear("Stealth Suit", "Full-body suit for infiltration", "body", 0, 8, 5));
        gameMap.get("fixer_den").addItem(new Gear("Hacking Deck", "Advanced personal computing device", "utility", 0, 0, 15));
        
        // Executive Suite items
        gameMap.get("executive_suite").addItem(new QuestItem("Gibson Access Codes", "Top-level access codes for The Gibson", "main_quest"));
        gameMap.get("executive_suite").addItem(new Valuable("Executive Data", "Blackmail material on corporate executives", 100));
    }
    
    /**
     * Add enemies to locations
     */
    private static void addEnemiesToLocations() {
        // Neon District enemies
        gameMap.get("neon_district").addEnemy(new Enemy("Street Thug", 30, 8));
        
        // Corporate Plaza enemies
        gameMap.get("corporate_plaza").addEnemy(new Enemy("Security Guard", 40, 10));
        
        // Black Market enemies
        gameMap.get("black_market").addEnemy(new Enemy("Mercenary", 50, 15));
        
        // Tech Sector enemies
        gameMap.get("tech_sector").addEnemy(new Enemy("Security Drone", 35, 12));
        
        // Security Checkpoint enemies
        gameMap.get("security_checkpoint").addEnemy(new Enemy("Elite Guard", 60, 18));
        
        // Research Lab enemies
        gameMap.get("research_lab").addEnemy(new Enemy("Lab Security", 45, 13));
        
        // Server Farm enemies
        gameMap.get("server_farm").addEnemy(new Enemy("System Guardian", 55, 16));
        
        // Server Room enemies
        gameMap.get("server_room").addEnemy(new Enemy("ICE Protocol", 65, 20));
        
        // The Gibson enemies
        gameMap.get("the_gibson").addEnemy(new Enemy("Firewall Sentinel", 80, 25));
    }
    
    /**
     * Add hackable objects to locations
     */
    private static void addHackablesToLocations() {
        // Hideout hackables
        List<String> emptyList = new ArrayList<>();
        gameMap.get("hideout").addHackable(new Hackable("Training System", "A simple system for hacking practice", 10, "digital", emptyList));
        
        // Neon District hackables
        gameMap.get("neon_district").addHackable(new Hackable("Public Terminal", "A public access point to the city network", 15, "digital", emptyList));
        
        // Corporate Plaza hackables
        List<String> securityItems = new ArrayList<>();
        securityItems.add("Corporate ID Badge");
        gameMap.get("corporate_plaza").addHackable(new Hackable("Security Cameras", "Surveillance system monitoring the plaza", 25, "physical", securityItems));
        
        // Data Market hackables
        gameMap.get("data_market").addHackable(new Hackable("Information Database", "Contains valuable market intelligence", 30, "digital", emptyList));
        gameMap.get("data_market").addHackable(new Hackable("Merchant Network", "Financial network for marketplace vendors", 35, "digital", emptyList));
        
        // Tech Sector hackables
        List<String> techItems = new ArrayList<>();
        techItems.add("Tech Specs");
        gameMap.get("tech_sector").addHackable(new Hackable("Research Database", "Corporate R&D repository", 40, "digital", techItems));
        
        // Server Farm hackables
        gameMap.get("server_farm").addHackable(new Hackable("Cooling Control", "Systems that regulate server temperatures", 30, "physical", emptyList));
        gameMap.get("server_farm").addHackable(new Hackable("Network Router", "Central router controlling data flow", 45, "digital", emptyList));
        
        // Research Lab hackables
        gameMap.get("research_lab").addHackable(new Hackable("Experimental AI", "Advanced artificial intelligence program", 50, "digital", emptyList));
        
        // Security Checkpoint hackables
        List<String> accessItems = new ArrayList<>();
        accessItems.add("System Access Codes");
        gameMap.get("security_checkpoint").addHackable(new Hackable("Access Control", "System that manages security clearances", 55, "digital", accessItems));
        
        // Executive Suite hackables
        gameMap.get("executive_suite").addHackable(new Hackable("Executive Terminal", "Personal computer of a corporate executive", 60, "digital", emptyList));
        
        // Server Room hackables
        List<String> serverItems = new ArrayList<>();
        serverItems.add("Server Maintenance Guide");
        gameMap.get("server_room").addHackable(new Hackable("Mainframe", "Core server containing the CryptoCoin algorithm", 70, "digital", serverItems));
        
        // The Gibson hackables
        List<String> gibsonItems = new ArrayList<>();
        gibsonItems.add("Gibson Access Codes");
        gameMap.get("the_gibson").addHackable(new Hackable("Gibson Core", "The legendary core of The Gibson", 80, "digital", gibsonItems));
    }
    
    /**
     * Create locations
     */
    private static void createLocations() {
        // Player's base
        Location hideout = new Location("Hideout", "Your secret base. Dimly lit with multiple screens showing surveillance feeds and network traffic. The worn couch and fridge full of energy drinks make this place feel like home.");
        hideout.setSecurityLevel(0);
        gameMap.put("hideout", hideout);
        
        // Main hub areas
        Location neonDistrict = new Location("Neon District", "The underground digital black market. Hackers, mercenaries, and information brokers gather here under the glow of neon signs. A safe haven from corporate eyes.");
        neonDistrict.setSecurityLevel(1);
        neonDistrict.addConnection("hideout");
        neonDistrict.addConnection("corporate_plaza");
        neonDistrict.addConnection("darknet");
        gameMap.put("neon_district", neonDistrict);
        
        Location corporatePlaza = new Location("Corporate Plaza", "The gleaming public face of mega-corporations. Clean streets, security drones, and advertisements everywhere. Corporate security keeps a watchful eye on all activity.");
        corporatePlaza.setSecurityLevel(2);
        corporatePlaza.addConnection("neon_district");
        corporatePlaza.addConnection("tech_sector");
        corporatePlaza.addConnection("security_checkpoint");
        corporatePlaza.addConnection("data_market");
        gameMap.put("corporate_plaza", corporatePlaza);
        
        Location darknet = new Location("Darknet", "A virtual gathering place outside normal network protocols. Digital shadows obscure users' identities. Information, services, and digital contraband flow freely here.");
        darknet.setSecurityLevel(0);
        darknet.addConnection("neon_district");
        darknet.addConnection("black_market");
        gameMap.put("darknet", darknet);
        
        // Corporate zone locations
        Location techSector = new Location("Tech Sector", "Research and development district for cutting-edge technology. Labs, server farms, and prototype testing facilities occupy towering buildings.");
        techSector.setSecurityLevel(3);
        techSector.addConnection("corporate_plaza");
        techSector.addConnection("research_lab");
        techSector.addConnection("server_farm");
        gameMap.put("tech_sector", techSector);
        
        Location securityCheckpoint = new Location("Security Checkpoint", "Heavily guarded entry point to restricted corporate areas. Armed guards, biometric scanners, and automated defense systems ensure only authorized personnel gain access.");
        securityCheckpoint.setSecurityLevel(4);
        securityCheckpoint.addConnection("corporate_plaza");
        securityCheckpoint.addConnection("executive_suite");
        securityCheckpoint.addConnection("server_room");
        securityCheckpoint.setSecured(true);
        gameMap.put("security_checkpoint", securityCheckpoint);
        
        // Specialized locations
        Location dataMarket = new Location("Data Market", "Legitimate marketplace for digital goods and information. Corporate-sanctioned data brokers operate in plain sight, though underground dealings happen behind the scenes.");
        dataMarket.setSecurityLevel(1);
        dataMarket.addConnection("corporate_plaza");
        gameMap.put("data_market", dataMarket);
        
        Location blackMarket = new Location("Black Market", "Hidden marketplace for illegal tech, weapons, and hacking tools. Accessible only to those with the right connections. Guards with itchy trigger fingers ensure disputes don't escalate.");
        blackMarket.setSecurityLevel(2);
        blackMarket.addConnection("darknet");
        blackMarket.addConnection("fixer_den");
        gameMap.put("black_market", blackMarket);
        
        Location researchLab = new Location("Research Lab", "Corporate lab developing next-gen technology. Sterile environment filled with experimental prototypes and sensitive data. Scientists work under heavy surveillance.");
        researchLab.setSecurityLevel(3);
        researchLab.addConnection("tech_sector");
        gameMap.put("research_lab", researchLab);
        
        Location serverFarm = new Location("Server Farm", "Massive facility housing thousands of servers. The constant hum of cooling systems and blinking lights fill the space. A digital goldmine protected by advanced security systems.");
        serverFarm.setSecurityLevel(3);
        serverFarm.addConnection("tech_sector");
        gameMap.put("server_farm", serverFarm);
        
        Location executiveSuite = new Location("Executive Suite", "Opulent offices of corporate executives. Expensive furnishings and panoramic views of the city contrast with the sterile corporate aesthetics elsewhere.");
        executiveSuite.setSecurityLevel(4);
        executiveSuite.addConnection("security_checkpoint");
        executiveSuite.setSecured(true);
        gameMap.put("executive_suite", executiveSuite);
        
        Location serverRoom = new Location("Server Room", "Heart of the corporate network infrastructure. Racks of high-security servers contain the most valuable data. Elite security personnel and automated systems guard this critical location.");
        serverRoom.setSecurityLevel(5);
        serverRoom.addConnection("security_checkpoint");
        serverRoom.addConnection("the_gibson");
        serverRoom.setSecured(true);
        gameMap.put("server_room", serverRoom);
        
        Location fixerDen = new Location("Fixer Den", "Hideout of the city's most connected fixer. Underground bunker filled with contraband tech and valuable intel. The fixer's personal guards ensure unwanted visitors don't leave alive.");
        fixerDen.setSecurityLevel(2);
        fixerDen.addConnection("black_market");
        fixerDen.addConnection("escape_route");
        gameMap.put("fixer_den", fixerDen);
        
        Location theGibson = new Location("The Gibson", "The legendary mainframe. Most secure server in existence with unprecedented security measures. No hacker has ever successfully infiltrated this system and escaped alive.");
        theGibson.setSecurityLevel(5);
        theGibson.addConnection("server_room");
        theGibson.addConnection("escape_route");
        theGibson.setSecured(true);
        gameMap.put("the_gibson", theGibson);
        
        Location escapeRoute = new Location("Escape Route", "Emergency exit system leading out of the corporate zone. Rarely used except during actual emergencies, making it perfect for a discreet getaway. Your ticket to freedom after the heist.");
        escapeRoute.setSecurityLevel(2);
        escapeRoute.addConnection("the_gibson");
        escapeRoute.addConnection("fixer_den");
        gameMap.put("escape_route", escapeRoute);

        // The hideout connects to the neon district
        hideout.addConnection("neon_district");
    }
    
    /**
     * Create quests
     */
    private static void createQuests() {
        // Main quest line
        Quest mainQuest1 = new Quest("The CryptoCoin Heist", "Infiltrate corporate systems to steal the CryptoCoin algorithm", "main");
        mainQuest1.addObjective("Find your way to the Corporate Plaza", "location", "corporate_plaza");
        mainQuest1.addObjective("Acquire a Corporate ID Badge", "item", "Corporate ID Badge");
        mainQuest1.addObjective("Access the Tech Sector", "location", "tech_sector");
        mainQuest1.setExpReward(200);
        mainQuest1.setCoinReward(100);
        mainQuest1.setUnlocksQuestId("main_quest_2");
        quests.put("main_quest_1", mainQuest1);
        
        Quest mainQuest2 = new Quest("Server Farm Infiltration", "Gain access to the server farm and locate server credentials", "main");
        mainQuest2.addObjective("Find the Server Farm", "location", "server_farm");
        mainQuest2.addObjective("Hack the Network Router", "hack", "Network Router");
        mainQuest2.addObjective("Acquire Server Maintenance Guide", "item", "Server Maintenance Guide");
        mainQuest2.setExpReward(300);
        mainQuest2.setCoinReward(150);
        mainQuest2.setUnlocksQuestId("main_quest_3");
        quests.put("main_quest_2", mainQuest2);
        
        Quest mainQuest3 = new Quest("Security Clearance", "Bypass the security checkpoint to reach restricted areas", "main");
        mainQuest3.addObjective("Reach the Security Checkpoint", "location", "security_checkpoint");
        mainQuest3.addObjective("Hack the Access Control system", "hack", "Access Control");
        mainQuest3.addObjective("Defeat the Elite Guard", "enemy", "Elite Guard");
        mainQuest3.setExpReward(400);
        mainQuest3.setCoinReward(200);
        mainQuest3.setUnlocksQuestId("main_quest_4");
        quests.put("main_quest_3", mainQuest3);
        
        Quest mainQuest4 = new Quest("Executive Access", "Break into the executive suite to find Gibson access codes", "main");
        mainQuest4.addObjective("Enter the Executive Suite", "location", "executive_suite");
        mainQuest4.addObjective("Hack the Executive Terminal", "hack", "Executive Terminal");
        mainQuest4.addObjective("Obtain Gibson Access Codes", "item", "Gibson Access Codes");
        mainQuest4.setExpReward(500);
        mainQuest4.setCoinReward(250);
        mainQuest4.setUnlocksQuestId("main_quest_5");
        quests.put("main_quest_4", mainQuest4);
        
        Quest mainQuest5 = new Quest("The Final Hack", "Infiltrate the server room and the legendary Gibson", "main");
        mainQuest5.addObjective("Enter the Server Room", "location", "server_room");
        mainQuest5.addObjective("Hack the Mainframe", "hack", "Mainframe");
        mainQuest5.addObjective("Access The Gibson", "location", "the_gibson");
        mainQuest5.addObjective("Hack the Gibson Core", "hack", "Gibson Core");
        mainQuest5.addObjective("Escape through the Escape Route", "location", "escape_route");
        mainQuest5.setExpReward(1000);
        mainQuest5.setCoinReward(500);
        mainQuest5.setItemReward(new Gear("Legendary Neural Interface", "The most advanced neural interface in existence", "head", 10, 10, 20));
        quests.put("main_quest_5", mainQuest5);
        
        // Side quests
        Quest sideQuest1 = new Quest("Dark Market Connection", "Establish connections in the underground market", "side");
        sideQuest1.addObjective("Discover the Darknet", "location", "darknet");
        sideQuest1.addObjective("Find the Black Market", "location", "black_market");
        sideQuest1.setExpReward(100);
        sideQuest1.setCoinReward(50);
        sideQuest1.setItemReward(new Consumable("Security Bypass Module", "Your next hack attempt will automatically succeed", "utility"));
        quests.put("side_quest_1", sideQuest1);
        
        Quest sideQuest2 = new Quest("Corporate Espionage", "Steal valuable research data for a mysterious client", "side");
        sideQuest2.addObjective("Enter the Research Lab", "location", "research_lab");
        sideQuest2.addObjective("Hack the Experimental AI", "hack", "Experimental AI");
        sideQuest2.addObjective("Acquire Research Data", "item", "Research Data");
        sideQuest2.setExpReward(150);
        sideQuest2.setCoinReward(75);
        Map<String, Integer> factionChanges = new HashMap<>();
        factionChanges.put("corporate", -20);
        factionChanges.put("underground", 15);
        sideQuest2.setFactionChanges(factionChanges);
        quests.put("side_quest_2", sideQuest2);
        
        Quest sideQuest3 = new Quest("Fixer's Favor", "Help the Fixer with a delicate matter", "side");
        sideQuest3.addObjective("Visit the Fixer Den", "location", "fixer_den");
        sideQuest3.addObjective("Defeat a Mercenary", "enemy", "Mercenary");
        sideQuest3.setExpReward(200);
        sideQuest3.setCoinReward(100);
        sideQuest3.setItemReward(new Gear("Enhanced Neural Link", "Superior hacking interface", "utility", 0, 0, 15));
        factionChanges = new HashMap<>();
        factionChanges.put("underground", 25);
        sideQuest3.setFactionChanges(factionChanges);
        quests.put("side_quest_3", sideQuest3);
    }
    
    /**
     * Create factions
     */
    private static void createFactions() {
        // Corporate faction
        Faction corporate = new Faction("MegaCorp Inc.", "The dominant technology corporation, controls most of the city's digital infrastructure", 0);
        factions.put("corporate", corporate);
        
        // Underground faction
        Faction underground = new Faction("Digital Underground", "Network of hackers, data couriers, and digital rebels opposing corporate control", 25);
        factions.put("underground", underground);
        
        // Security faction
        Faction security = new Faction("NetSec", "Private security force employed by corporations to protect digital assets", -10);
        factions.put("security", security);
        
        // Fixer faction
        Faction fixers = new Faction("Fixers' Guild", "Information brokers and middlemen who facilitate deals in the shadows", 10);
        factions.put("fixers", fixers);
    }
    
    /**
     * Create a new Gear with just attack bonus for convenience
     */
    private static Gear createAttackGear(String name, String description, String slot, int attackBonus) {
        return new Gear(name, description, slot, attackBonus, 0, 0);
    }
}