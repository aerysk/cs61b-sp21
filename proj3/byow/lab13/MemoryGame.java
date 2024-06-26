package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        /**
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        } */
        long seed = Long.parseLong("1234463");
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        rand = new Random(seed);
    }

    public String generateRandomString(int n) {
        StringBuilder answer = new StringBuilder();
        while (answer.length() < n) {
            answer.append(CHARACTERS[RandomUtils.uniform(this.rand, CHARACTERS.length)]);
        }
        return answer.toString();
    }

    public void drawFrame(String s) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(fontBig);
        StdDraw.text(this.width / 2, this.height / 2, s);
        if (!gameOver) {
            Font fontSmall = new Font("Monaco", Font.BOLD, 20);
            StdDraw.setFont(fontSmall);
            StdDraw.line(0, this.height - 2, this.width, this.height - 2);
            StdDraw.textLeft(0, this.height - 1, "Round: " + this.round);
            if (this.playerTurn) {
                StdDraw.text(this.width / 2, this. height - 1, "Type!");
            } else {
                StdDraw.text(this.width / 2, this.height - 1, "Watch!");
            }
            int randomEncouragement = RandomUtils.uniform(this.rand, ENCOURAGEMENT.length);
            StdDraw.textRight(this.width, this.height - 1, ENCOURAGEMENT[randomEncouragement]);
        }
        StdDraw.show();
    }

    public void flashSequence(String letters) {
        //TODO: Display each character in letters, making sure to blank the screen between letters
        for (char c: letters.toCharArray()) {
            this.drawFrame(c + ""); // adding a char to an empty string turns it into a string
            // Character.toString(c)
            StdDraw.pause(1000);
            this.drawFrame("");
            StdDraw.pause(1000);
        }
    }

    public String solicitNCharsInput(int n) {
        //TODO: Read n letters of player input
        String userInput = "";
        while (userInput.length() < n) {
            if (StdDraw.hasNextKeyTyped()) {
                char current = StdDraw.nextKeyTyped();
                userInput += current;
                drawFrame(userInput);
            }
        }
        return userInput;
    }

    public void startGame() {
        //TODO: Set any relevant variables before the game starts
        //TODO: Establish Engine loop
        this.round = 1;
        this.gameOver = false;
        String randomString;
        String userInput;
        while (!gameOver) {
            this.playerTurn = false;
            StdDraw.pause(1000);
            this.drawFrame("Round: " + this.round);
            StdDraw.pause(1000);
            randomString = this.generateRandomString(this.round);
            this.flashSequence(randomString);
            this.playerTurn = true;
            this.drawFrame("");
            userInput = this.solicitNCharsInput(this.round);
            if (!userInput.equals(randomString)) {
                gameOver = true;
                break;
            }
            this.round += 1;
        }
        this.drawFrame("Game Over! You made it to round: " + this.round);
    }
}
