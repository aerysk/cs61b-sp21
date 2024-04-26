package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import java.util.Random;

public class Rectangle {
    int height;
    int width;
    Position position;

    public Rectangle(Position p, int h, int w, TETile[][] world, TETile tileType,
                     boolean isRoom, Random random) {
        position = p;
        height = h;
        width = w;
        int placeFlowerOrBear = 0;
        for (int i = p.getX(); i < p.getX() + w; i += 1) {
            for (int j = p.getY(); j > p.getY() - h; j -= 1) {
                if (isRoom) {
                    placeFlowerOrBear = RandomUtils.uniform(random, 1, 50);
                }
                if (placeFlowerOrBear == 3 || placeFlowerOrBear == 5) {
                    world[i][j] = Tileset.FLOWER;
                } else if (placeFlowerOrBear == 10) {
                    world[i][j] = Tileset.BEAR;
                } else {
                    world[i][j] = tileType;
                }
            }
        }
    }
}
