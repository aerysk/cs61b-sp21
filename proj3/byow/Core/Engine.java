package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.Core.Input.*;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;
import java.awt.*;
import java.io.File;
import static byow.Core.Utils.*;

public class Engine {
    public static final File SAVE_FILE = new File("savefile.txt");
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    TERenderer ter = new TERenderer();
    World worldObject;
    long currentSeed;
    TETile environment = Tileset.FLOOR;
    StringBuilder actionsHistory = new StringBuilder();

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        displayMenu();
        KeyboardInputSource keyboardInput = new KeyboardInputSource();
        while (keyboardInput.possibleNextInput()) {
            char c = keyboardInput.getNextKey();
            if (c == 'N' || c == 'n') {
                StdDraw.clear(Color.BLACK);
                StdDraw.setPenColor(Color.LIGHT_GRAY);
                Font optionsFont = new Font("Monaco", Font.ITALIC, 15);
                StdDraw.setFont(optionsFont);
                StdDraw.text(WIDTH / 2, HEIGHT / 2 + 3, "Enter a numerical seed:");
                StdDraw.text(WIDTH / 2, HEIGHT / 2 - 3, "(S)tart");
                StdDraw.show();
                long seed = enterSeed(keyboardInput);
                currentSeed = seed;
                World world = new World(WIDTH, HEIGHT, environment);
                worldObject = world;
                world.generateWorld(seed);
                ter.renderFrame(worldObject.getWorldArray());
                StdDraw.setPenColor(Color.LIGHT_GRAY);
                Font flowerCounter = new Font("Monaco", Font.PLAIN, 15);
                StdDraw.setFont(flowerCounter);
                StdDraw.textLeft(1, HEIGHT - 1, "Flowers collected: "
                        + worldObject.getFlowerCount());
                StdDraw.show();
                actionsHistory.append(c + Long.toString(currentSeed) + 's');
                enterWorld(keyboardInput);
            } else if (c == 'L' || c == 'l') {
                String history = readContentsAsString(SAVE_FILE);
                // actionsHistory.append(history);
                TETile[][] savedWorld = interactWithInputString(history);
                ter.renderFrame(savedWorld);
                /**
                StdDraw.setPenColor(Color.LIGHT_GRAY);
                Font flowerCounter = new Font("Monaco", Font.PLAIN, 15);
                StdDraw.setFont(flowerCounter);
                StdDraw.textLeft(1, HEIGHT - 1, "Flowers collected: "
                        + worldObject.getFlowerCount());
                StdDraw.show(); */
                enterWorld(keyboardInput);
            } else if (c == 'E' || c == 'e') {
                actionsHistory.append(c);
                displayEnvironments();
                chooseEnvironment();
            } else if (c == 'Q' || c == 'q') {
                System.exit(0);
            }
        }
    }

    private void displayMenu() {
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font titleFont = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(titleFont);
        StdDraw.text(WIDTH / 2, 3 * HEIGHT / 4, "CS61B: The Dungeon");
        StdDraw.setPenColor(Color.LIGHT_GRAY);
        Font optionsFont = new Font("Monaco", Font.ITALIC, 15);
        StdDraw.setFont(optionsFont);
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 3, "(N)ew Game");
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "(L)oad Game");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 3, "(E)nvironment");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 6, "(Q)uit");
        StdDraw.show();
    }

    private long enterSeed(KeyboardInputSource keyboardInput) {
        StringBuilder seedStringBuilder = new StringBuilder();
        while (keyboardInput.possibleNextInput()) {
            char c = keyboardInput.getNextKey();
            StdDraw.clear(Color.BLACK);
            if (c == 'S' || c == 's') {
                if (seedStringBuilder.toString().isEmpty()) {
                    StdDraw.text(WIDTH / 2, HEIGHT / 2 - 9, "Please enter a number.");
                } else {
                    String seedString = seedStringBuilder.toString();
                    seedString = seedString.replaceAll(" ", "");
                    return Long.parseLong(seedString);
                }
            } else if (Character.isAlphabetic(c)) {
                StdDraw.text(WIDTH / 2, HEIGHT / 2 - 9, "Please enter a number.");
            } else {
                seedStringBuilder.append(c);
            }
            StdDraw.setPenColor(Color.LIGHT_GRAY);
            Font optionsFont = new Font("Monaco", Font.ITALIC, 15);
            StdDraw.setFont(optionsFont);
            StdDraw.text(WIDTH / 2, HEIGHT / 2 + 3, "Enter a numerical seed:");
            StdDraw.text(WIDTH / 2, HEIGHT / 2, seedStringBuilder.toString());
            StdDraw.text(WIDTH / 2, HEIGHT / 2 - 3, "(S)tart");
            StdDraw.show();
        }
        return 0;
    }

    private void chooseEnvironment() {
        StringBuilder choice = new StringBuilder("d");
        StdDraw.enableDoubleBuffering();
        environmentMenuText();
        KeyboardInputSource keyboardInput = new KeyboardInputSource();
        while (keyboardInput.possibleNextInput()) {
            char c = keyboardInput.getNextKey();
            if (c == 'D' || c == 'd') {
                setEnvironment(true, false, false, false, false);
                choice.setCharAt(0, c);
                environment = Tileset.FLOOR;
            } else if (c == 'G' || c == 'g') {
                setEnvironment(false, true, false, false, false);
                choice.setCharAt(0, c);
                environment = Tileset.GRASS;
            } else if (c == 'W' || c == 'w') {
                setEnvironment(false, false, true, false, false);
                choice.setCharAt(0, c);
                environment = Tileset.WATER;
            } else if (c == 'S' || c == 's') {
                setEnvironment(false, false, false, true, false);
                choice.setCharAt(0, c);
                environment = Tileset.SAND;
            } else if (c == 'M' || c == 'm') {
                setEnvironment(false, false, false, false, true);
                choice.setCharAt(0, c);
                environment = Tileset.MOUNTAIN;
            } else if (c == 'C' || c == 'c') {
                choice.append(c);
                actionsHistory.append(choice.toString());
                interactWithKeyboard();
            }
        }
    }

    private void displayEnvironments() {
        StdDraw.clear();
        Font font = new Font("Monaco", Font.PLAIN, 15);
        StdDraw.setFont(font);
        TETile[][] envTiles = new TETile[WIDTH][HEIGHT];
        for (int i = 0; i < WIDTH; i++) {
            for (int j = HEIGHT - 1; j >= 0; j--) {
                envTiles[i][j] = Tileset.NOTHING;
            }
        }
        for (int i = 19; i < 21; i++) {
            for (int j = 21; j > 19; j--) {
                envTiles[i][j] = Tileset.FLOOR;
            }
        }
        for (int i = 39; i < 41; i++) {
            for (int j = 21; j > 19; j--) {
                envTiles[i][j] = Tileset.GRASS;
            }
        }
        for (int i = 59; i < 61; i++) {
            for (int j = 21; j > 19; j--) {
                envTiles[i][j] = Tileset.WATER;
            }
        }
        for (int i = 25; i < 27; i++) {
            for (int j = 11; j > 9; j--) {
                envTiles[i][j] = Tileset.SAND;
            }
        }
        for (int i = 51; i < 53; i++) {
            for (int j = 11; j > 9; j--) {
                envTiles[i][j] = Tileset.MOUNTAIN;
            }
        }
        ter.renderFrame(envTiles);
    }

    private void environmentMenuText() {
        StdDraw.setPenColor(Color.LIGHT_GRAY);
        Font normal = new Font("Monaco", Font.ITALIC, 15);
        StdDraw.setFont(normal);
        StdDraw.text(WIDTH / 4, 2 * HEIGHT / 3 - 1, "(D)efault Floor");
        StdDraw.text(2 * WIDTH / 4, 2 * HEIGHT / 3 - 1, "(G)rass");
        StdDraw.text(3 * WIDTH / 4, 2 * HEIGHT / 3 - 1, "(W)ater");
        StdDraw.text(WIDTH / 3, HEIGHT / 3 - 1, "(S)and");
        StdDraw.text(2 * WIDTH / 3 - 1, HEIGHT / 3 - 1, "(M)ountain");
        StdDraw.text(WIDTH / 2, HEIGHT / 5, "(C)ontinue");
        StdDraw.show();
    }

    private void setEnvironment(boolean d, boolean g, boolean w, boolean s, boolean m) {
        displayEnvironments();
        Font chosen = new Font("Monaco", Font.BOLD, 15);
        Font normal = new Font("Monaco", Font.ITALIC, 15);
        StdDraw.setPenColor(Color.LIGHT_GRAY);
        StdDraw.setFont(normal);
        StdDraw.text(WIDTH / 2, HEIGHT / 5, "(C)ontinue");
        if (d) {
            StdDraw.setPenColor(Color.BLUE);
            StdDraw.setFont(chosen);
        } else {
            StdDraw.setPenColor(Color.LIGHT_GRAY);
            StdDraw.setFont(normal);
        }
        StdDraw.text(WIDTH / 4, 2 * HEIGHT / 3 - 1, "(D)efault Floor");
        if (g) {
            StdDraw.setPenColor(Color.BLUE);
            StdDraw.setFont(chosen);
        } else {
            StdDraw.setPenColor(Color.LIGHT_GRAY);
            StdDraw.setFont(normal);
        }
        StdDraw.text(2 * WIDTH / 4, 2 * HEIGHT / 3 - 1, "(G)rass");
        if (w) {
            StdDraw.setPenColor(Color.BLUE);
            StdDraw.setFont(chosen);
        } else {
            StdDraw.setPenColor(Color.LIGHT_GRAY);
            StdDraw.setFont(normal);
        }
        StdDraw.text(3 * WIDTH / 4, 2 * HEIGHT / 3 - 1, "(W)ater");
        if (s) {
            StdDraw.setPenColor(Color.BLUE);
            StdDraw.setFont(chosen);
        } else {
            StdDraw.setPenColor(Color.LIGHT_GRAY);
            StdDraw.setFont(normal);
        }
        StdDraw.text(WIDTH / 3, HEIGHT / 3 - 1, "(S)and");
        if (m) {
            StdDraw.setPenColor(Color.BLUE);
            StdDraw.setFont(chosen);
        } else {
            StdDraw.setPenColor(Color.LIGHT_GRAY);
            StdDraw.setFont(normal);
        }
        StdDraw.text(2 * WIDTH / 3 - 1, HEIGHT / 3 - 1, "(M)ountain");
        StdDraw.show();
    }

    private void enterWorld(InputSource inputSource) {
        StdDraw.enableDoubleBuffering();
        boolean quit = false;
        char c;
        while (true) {
            if (inputSource.possibleNextInput()) {
                if (inputSource.getClass() == KeyboardInputSource.class) {
                    c = ((KeyboardInputSource) inputSource).getNextKeyWithMouse();
                } else {
                    c = inputSource.getNextKey();
                }
                Position playerPos = worldObject.getPlayerPosition();
                if (c == 'W' || c == 'w') {
                    // Move avatar up
                    movePlayer(playerPos, 0, 1);
                    actionsHistory.append(c);
                    quit = false;
                } else if (c == 'S' || c == 's') {
                    // Move avatar down
                    movePlayer(playerPos, 0, -1);
                    actionsHistory.append(c);
                    quit = false;
                } else if (c == 'D' || c == 'd') {
                    // Move avatar right
                    movePlayer(playerPos, 1, 0);
                    actionsHistory.append(c);
                    quit = false;
                } else if (c == 'A' || c == 'a') {
                    // Move avatar left
                    movePlayer(playerPos, -1, 0);
                    actionsHistory.append(c);
                    quit = false;
                } else if (c == ':') {
                    quit = true;
                } else if (c == 'Q' || c == 'q') {
                    if (quit) {
                        writeContents(SAVE_FILE, actionsHistory.toString());
                        System.exit(0);
                    }
                } else if (c == 'P' || c == 'p') {
                    if (worldObject.playerInGarden()) {
                        actionsHistory.append(c);
                        plantOrPickUpFlower(playerPos);
                    }
                    quit = false;
                }
                if (worldObject.playerInGarden()) {
                    ter.renderFrame(worldObject.getGardenArray());
                    gardenText();
                } else {
                    ter.renderFrame(worldObject.getWorldArray());
                }
                displayFlowerCount();
                displayTileAtMousePointer();
                StdDraw.show();
            }
        }
    }

    private void displayTileAtMousePointer() {
        int mouseX = (int) Math.floor(StdDraw.mouseX());
        int mouseY = (int) Math.floor(StdDraw.mouseY());
        if (mouseX < 1 || mouseX > WIDTH - 1) {
            return;
        } else if (mouseY < 1 || mouseY > HEIGHT - 1) {
            return;
        }
        TETile[][] worldArray = worldObject.getWorldArray();
        /**
        if (worldObject.playerInGarden()) {
            worldArray = worldObject.getGardenArray();
        } */
        StdDraw.setPenColor(Color.LIGHT_GRAY);
        Font optionsFont = new Font("Monaco", Font.PLAIN, 15);
        StdDraw.setFont(optionsFont);
        StdDraw.text(WIDTH / 2, HEIGHT - 1, worldArray[mouseX][mouseY].description());
        // StdDraw.show();
    }

    private void displayFlowerCount() {
        StdDraw.setPenColor(Color.LIGHT_GRAY);
        Font optionsFont = new Font("Monaco", Font.PLAIN, 15);
        StdDraw.setFont(optionsFont);
        StdDraw.textLeft(1, HEIGHT - 1, "Flowers collected: "
                + worldObject.getFlowerCount());
        // StdDraw.show();
    }

    public void movePlayer(Position playerPos, int dx, int dy) {
        Position newPosition = playerPos.shift(dx, dy);
        TETile[][] currentWorld = worldObject.getWorldArray();
        if (worldObject.playerInGarden()) {
            movePlayerInGarden(newPosition);
            return;
        }
        int newX = newPosition.getX();
        int newY = newPosition.getY();
        if (currentWorld[newX][newY] == environment
                || currentWorld[newX][newY] == Tileset.FLOWER) {
            worldObject.updatePlayerPosition(newPosition);
        } else if (currentWorld[newX][newY] == Tileset.UNLOCKED_DOOR) {
            worldObject.enterGarden();
        } else if (currentWorld[newX][newY] == Tileset.BEAR) {
            bearEncounter();
            worldObject.updatePlayerPosition(newPosition);
        }
    }

    private void movePlayerInGarden(Position newPos) {
        TETile[][] currentWorld = worldObject.getGardenArray();
        int newX = newPos.getX();
        int newY = newPos.getY();
        if (currentWorld[newX][newY] == Tileset.GRASS) {
            worldObject.updatePlayerPosition(newPos);
        } else if (currentWorld[newX][newY] == Tileset.UNLOCKED_DOOR) {
            worldObject.exitGarden();
        }
    }

    private void plantOrPickUpFlower(Position playerPos) {
        KeyboardInputSource keyboardInput = new KeyboardInputSource();
        Position flower = playerPos;
        boolean valid = false;
        while (keyboardInput.possibleNextInput()) {
            char c = keyboardInput.getNextKey();
            if (c == 'W' || c == 'w') {
                flower = flower.shift(0, 1);
                valid = true;
            } else if (c == 'S' || c == 's') {
                flower = flower.shift(0, -1);
                valid = true;
            } else if (c == 'D' || c == 'd') {
                flower = flower.shift(1, 0);
                valid = true;
            } else if (c == 'A' || c == 'a') {
                flower = flower.shift(-1, 0);
                valid = true;
            }
            if (valid) {
                actionsHistory.append(c);
                worldObject.plantOrPickUpFlower(flower);
                return;
            }
        }
    }

    private void gardenText() {
        StdDraw.setPenColor(Color.LIGHT_GRAY);
        Font optionsFont = new Font("Monaco", Font.PLAIN, 15);
        StdDraw.setFont(optionsFont);
        StdDraw.text(WIDTH / 2, HEIGHT - 3, "You've found a secret garden in "
                + "the dungeon... How serene!");
        StdDraw.text(WIDTH / 2, HEIGHT - 4.5, "Plant or pick up flowers by stepping "
                + "next to the target location, pressing p, ");
        StdDraw.text(WIDTH / 2, HEIGHT - 6, "then pressing w/a/s/d for the direction "
                + "you'd like to plant or remove your flower.");
        StdDraw.show();
    }

    private void bearEncounter() {
        StdDraw.clear(Color.BLACK);
        Font font = new Font("Monaco", Font.PLAIN, 15);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.filledCircle(WIDTH / 2 - 3.5, 5 * HEIGHT / 8 + 3, 2);
        StdDraw.filledCircle(WIDTH / 2 + 3.5, 5 * HEIGHT / 8 + 3, 2);
        StdDraw.text(WIDTH / 2, HEIGHT / 4 + 2, "An albino bear blocks your path...");
        StdDraw.text(WIDTH / 2, HEIGHT / 4, "Keep throwing food (f) and petting the bear (p)");
        StdDraw.text(WIDTH / 2, HEIGHT / 4 - 2, "until it leaves happily!!");
        StdDraw.setPenColor(Color.PINK);
        StdDraw.filledCircle(WIDTH / 2 - 3.5, 5 * HEIGHT / 8 + 3, 1);
        StdDraw.filledCircle(WIDTH / 2 + 3.5, 5 * HEIGHT / 8 + 3, 1);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.filledCircle(WIDTH / 2, 5 * HEIGHT / 8, 5);
        StdDraw.setPenColor(Color.PINK);
        StdDraw.filledEllipse(WIDTH / 2, 5 * HEIGHT / 8 - 1.5, 1.25, 0.75);
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.filledEllipse(WIDTH / 2 - 2, 5 * HEIGHT / 8 - 0.25, 0.5, 0.75);
        StdDraw.filledEllipse(WIDTH / 2 + 2, 5 * HEIGHT / 8 - 0.25, 0.5, 0.75);
        StdDraw.show();
        KeyboardInputSource keyboardInput = new KeyboardInputSource();
        int actions = 0;
        boolean endEncounter = false;
        while (keyboardInput.possibleNextInput()) {
            char c = keyboardInput.getNextKey();
            if (c == 'F' || c == 'f') {
                actions++;
            } else if (c == 'P' || c == 'p') {
                actions++;
            } else if (c == 'C' || c == 'c') {
                if (endEncounter) {
                    return;
                }
            }
            if (actions == 8) {
                endEncounter = true;
                happyBear();
            }
        }
    }

    private void happyBear() {
        StdDraw.clear(Color.BLACK);
        Font font = new Font("Monaco", Font.PLAIN, 15);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.filledCircle(WIDTH / 2 - 3.5, 5 * HEIGHT / 8 + 3, 2);
        StdDraw.filledCircle(WIDTH / 2 + 3.5, 5 * HEIGHT / 8 + 3, 2);
        StdDraw.text(WIDTH / 2, HEIGHT / 4, "The albino bear leaves happily!");
        StdDraw.text(WIDTH / 2, HEIGHT / 4 - 2, "(C)ontinue exploring the dungeon");
        StdDraw.setPenColor(Color.PINK);
        StdDraw.filledCircle(WIDTH / 2 - 3.5, 5 * HEIGHT / 8 + 3, 1);
        StdDraw.filledCircle(WIDTH / 2 + 3.5, 5 * HEIGHT / 8 + 3, 1);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.filledCircle(WIDTH / 2, 5 * HEIGHT / 8, 5);
        StdDraw.setPenColor(Color.PINK);
        StdDraw.filledEllipse(WIDTH / 2, 5 * HEIGHT / 8 - 1.5, 1.25, 0.75);
        StdDraw.setPenColor(Color.BLACK);
        Font eyes = new Font("Monaco", Font.PLAIN, 50);
        StdDraw.setFont(eyes);
        StdDraw.text(WIDTH / 2 - 2, 5 * HEIGHT / 8 - 1, "^");
        StdDraw.text(WIDTH / 2 + 2, 5 * HEIGHT / 8 - 1, "^");
        StdDraw.show();
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quit/save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        StringInputDevice stringInput = new StringInputDevice(input);
        actionsHistory = new StringBuilder();
        StringBuilder chooseEnv = new StringBuilder();
        boolean inEnvMenu = false;
        while (stringInput.possibleNextInput()) {
            char c = stringInput.getNextKey();
            if (c == 'C' || c == 'c') {
                chooseEnv.append(c);
                inEnvMenu = false;
                chooseEnvironmentWithString(chooseEnv.toString());
            }
            if (inEnvMenu) {
                chooseEnv.append(c);
            }
            if (c == 'N' || c == 'n') {
                World world = new World(WIDTH, HEIGHT, environment);
                worldObject = world;
                currentSeed = extractSeed(stringInput);
                world.generateWorld(currentSeed);
                actionsHistory.append(c);
                actionsHistory.append(currentSeed);
                actionsHistory.append('s');
                enterWorldWithString(stringInput);
            } else if (c == 'L' || c == 'l') {
                String loadSave = readContentsAsString(SAVE_FILE);
                StringBuilder completeString = new StringBuilder();
                completeString.append(loadSave);
                completeString.append(input.substring(1));
                // actionsHistory.append(loadInputString);
                // return interactWithInputString(actionsHistory.toString());
                return interactWithInputString(completeString.toString());
            } else if (c == 'Q' || c == 'q') {
                return worldObject.getWorldArray();
            } else if (c == 'E' || c == 'e') {
                actionsHistory.append(c);
                inEnvMenu = true;
            }
        }
        writeContents(SAVE_FILE, actionsHistory.toString());
        return worldObject.getWorldArray();
    }

    private long extractSeed(StringInputDevice inputDevice) {
        StringBuilder seedString = new StringBuilder();
        while (inputDevice.possibleNextInput()) {
            char c = inputDevice.getNextKey();
            if (c == 'S' || c == 's') {
                break;
            }
            seedString.append(c);
        }
        return Long.parseLong(seedString.toString());
    }

    private void chooseEnvironmentWithString(String input) {
        StringInputDevice chooseEnv = new StringInputDevice(input);
        StringBuilder choice = new StringBuilder("d");
        while (chooseEnv.possibleNextInput()) {
            char c = chooseEnv.getNextKey();
            if (c == 'D' || c == 'd') {
                choice.setCharAt(0, c);
                environment = Tileset.FLOOR;
            } else if (c == 'G' || c == 'g') {
                choice.setCharAt(0, c);
                environment = Tileset.GRASS;
            } else if (c == 'W' || c == 'w') {
                choice.setCharAt(0, c);
                environment = Tileset.WATER;
            } else if (c == 'S' || c == 's') {
                choice.setCharAt(0, c);
                environment = Tileset.SAND;
            } else if (c == 'M' || c == 'm') {
                choice.setCharAt(0, c);
                environment = Tileset.MOUNTAIN;
            } else if (c == 'C' || c == 'c') {
                choice.append(c);
                actionsHistory.append(choice.toString());
            }
        }
    }

    private void enterWorldWithString(StringInputDevice stringInput) {
        boolean quit = false;
        while (stringInput.possibleNextInput()) {
            char c = stringInput.getNextKey();
            Position playerPos = worldObject.getPlayerPosition();
            if (c == 'W' || c == 'w') {
                // Move avatar up
                movePlayerWithString(playerPos, 0, 1);
                actionsHistory.append(c);
                quit = false;
            } else if (c == 'S' || c == 's') {
                // Move avatar down
                movePlayerWithString(playerPos, 0, -1);
                actionsHistory.append(c);
                quit = false;
            } else if (c == 'D' || c == 'd') {
                // Move avatar right
                movePlayerWithString(playerPos, 1, 0);
                actionsHistory.append(c);
                quit = false;
            } else if (c == 'A' || c == 'a') {
                // Move avatar left
                movePlayerWithString(playerPos, -1, 0);
                actionsHistory.append(c);
                quit = false;
            } else if (c == ':') {
                quit = true;
            } else if (c == 'Q' || c == 'q') {
                if (quit) {
                    writeContents(SAVE_FILE, actionsHistory.toString());
                    return;
                }
            } else if (c == 'P' || c == 'p') {
                if (worldObject.playerInGarden()) {
                    actionsHistory.append(c);
                    plantOrPickUpFlowerWithString(playerPos, stringInput);
                }
                quit = false;
            }
        }
    }

    public void movePlayerWithString(Position playerPos, int dx, int dy) {
        Position newPosition = playerPos.shift(dx, dy);
        TETile[][] currentWorld = worldObject.getWorldArray();
        if (worldObject.playerInGarden()) {
            movePlayerInGarden(newPosition);
            return;
        }
        int newX = newPosition.getX();
        int newY = newPosition.getY();
        if (currentWorld[newX][newY] == environment
                || currentWorld[newX][newY] == Tileset.FLOWER
                || currentWorld[newX][newY] == Tileset.BEAR) {
            worldObject.updatePlayerPosition(newPosition);
        } else if (currentWorld[newX][newY] == Tileset.UNLOCKED_DOOR) {
            worldObject.enterGarden();
        }
    }

    private void plantOrPickUpFlowerWithString(Position playerPos,
                                               StringInputDevice inputDevice) {
        Position flower = playerPos;
        boolean valid = false;
        while (inputDevice.possibleNextInput()) {
            char c = inputDevice.getNextKey();
            if (c == 'W' || c == 'w') {
                flower = flower.shift(0, 1);
                valid = true;
            } else if (c == 'S' || c == 's') {
                flower = flower.shift(0, -1);
                valid = true;
            } else if (c == 'D' || c == 'd') {
                flower = flower.shift(1, 0);
                valid = true;
            } else if (c == 'A' || c == 'a') {
                flower = flower.shift(-1, 0);
                valid = true;
            }
            if (valid) {
                actionsHistory.append(c);
                worldObject.plantOrPickUpFlower(flower);
                return;
            }
        }
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(80, 30);
        Engine engine = new Engine();
        // engine.interactWithKeyboard();
        TETile[][] interactWithStringWorld = engine.interactWithInputString("L");
        ter.renderFrame(interactWithStringWorld);
        StdDraw.show();
    }
}
