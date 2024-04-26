package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;

    private static final long SEED = 21;
    private static final Random RANDOM = new Random(SEED);

    /**
     * Fills the given 2D array of tiles with blank tiles.
     * @param tiles
     */
    public static void fillWithNothing(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(5);
        switch (tileNum) {
            case 0: return Tileset.GRASS;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.SAND;
            case 3: return Tileset.MOUNTAIN;
            case 4: return Tileset.TREE;
            default: return Tileset.NOTHING;
        }
    }

    private static class Position {
        int x;
        int y;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Position shift(int dx, int dy) {
            return new Position(this.x + dx, this.y + dy);
        }
    }

    public static void drawRow(TETile[][] tiles, Position p, TETile tile, int length) {
        for (int dx = 0; dx < length; dx++) {
            tiles[p.x + dx][p.y] = tile;
        }
    }

    public static void addHexagon(TETile[][] tiles, Position p, TETile t, int size) {
        if (size < 2) {
            return;
        }
        addHexHelper(tiles, p, t, size - 1, size);
    }

    public static void addHexColumn(TETile[][] tiles, Position p, int size, int num) {
        if (num < 1) {
            return;
        }
        addHexagon(tiles, p, randomTile(), size);
        if (num > 1) {
            Position bottomNeighbor = getBottomNeighbor(p, size);
            addHexColumn(tiles, bottomNeighbor, size, num - 1);
        }
    }

    public static Position getTopRightNeighbor(Position p, int size) {
        return p.shift(2*size - 1, size);
    }

    public static Position getBottomRightNeighbor(Position p, int size) {
        return p.shift(2*size - 1, -size);
    }

    public static Position getBottomNeighbor(Position p, int size) {
        return p.shift(0, -2*size);
    }

    /** Input b is the number of blank spaces before printing a row.
     *  Input t is the number of tiles we need to print in a row. */
    public static void addHexHelper(TETile[][] tiles, Position p, TETile tile, int b, int t) {
        Position startOfRow = p.shift(b, 0);
        drawRow(tiles, startOfRow, tile, t);
        if (b > 0) {
            Position nextP = p.shift(0, -1);
            addHexHelper(tiles, nextP, tile,b - 1, t + 2);
        }
        Position startOfReflectedRow = p.shift(b, -(2*b + 1));
        drawRow(tiles, startOfReflectedRow, tile, t);
    }

    public static void drawWorld(TETile[][] tiles, Position p, int hexSize, int tessSize) {
        // Draw first hexagon
        addHexColumn(tiles, p, hexSize, tessSize);
        // Expand up and to the right
        for (int i = 1; i < tessSize; i++) {
            p = getTopRightNeighbor(p, hexSize);
            addHexColumn(tiles, p, hexSize, tessSize + i);
        }
        // Expand down and to the right
        for (int i = tessSize - 2; i >= 0; i--) {
            p = getBottomRightNeighbor(p, hexSize);
            addHexColumn(tiles, p, hexSize, tessSize + i);
        }
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][HEIGHT];
        fillWithNothing(world);
        Position anchor = new Position(12, 34);
        drawWorld(world, anchor, 3, 3);

        ter.renderFrame(world);
    }

}
