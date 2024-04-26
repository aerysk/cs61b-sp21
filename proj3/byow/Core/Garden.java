package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Garden {
    int width;
    int height;
    TETile[][] gardenLayout;
    Position entrance;

    public Garden(int w, int h) {
        width = w;
        height = h;
        TETile[][] garden = new TETile[width][height];
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                garden[x][y] = Tileset.NOTHING;
            }
        }
        Position p = new Position(14, 22);
        new Rectangle(p, 20, 52, garden, Tileset.WALL, false, null);
        new Rectangle(p.shift(1, -1), 18, 50, garden, Tileset.GRASS, false, null);
        garden[40][22] = Tileset.UNLOCKED_DOOR;
        entrance = new Position(40, 21);
        gardenLayout = garden;
    }

    public TETile[][] returnGardenTiles() {
        return gardenLayout;
    }

    public Position entrance() {
        return entrance;
    }
}
