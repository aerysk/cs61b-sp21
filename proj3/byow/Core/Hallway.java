package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import java.util.Random;

public class Hallway {
    int height;
    int width;
    Position position;

    public Hallway(int h, int w, Position p, TETile[][] world) {
        height = h;
        width = w;
        position = p;
        new Rectangle(p, h + 2, w + 2, world, Tileset.WALL, false, null);
    }

    public void generateFloor(TETile[][] world, Random random, TETile tile) {
        new Rectangle(position.shift(1, -1), height, width, world, tile, true, random);
    }
}
