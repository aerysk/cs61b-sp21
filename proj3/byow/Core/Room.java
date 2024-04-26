package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import java.util.Random;

public class Room {
    int height;
    int width;
    Position position;

    public Room(int h, int w, Position p, TETile[][] world) {
        height = h;
        width = w;
        position = p;
        new Rectangle(p, h + 2, w + 2, world, Tileset.WALL, false, null);
    }

    public void generateFloor(TETile[][] world, Random random, TETile tile) {
        new Rectangle(position.shift(1, -1), height, width, world, tile, true, random);
    }

    /** A randomly generated "center" within the room. Not necessarily right in the middle. */
    public Position findCenter(Random random) {
        int posX = position.getX();
        int posY = position.getY();
        int a = RandomUtils.uniform(random, posX, posX + width);
        int b = RandomUtils.uniform(random, posY - height + 1, posY + 1);
        return new Position(a, b);
    }

    public Position placeAvatar(TETile[][] world, Random random) {
        int posX = position.getX();
        int posY = position.getY();
        int i = RandomUtils.uniform(random, posX + 1, posX + width);
        int j = RandomUtils.uniform(random, posY - height, posY);
        world[i][j] = Tileset.AVATAR;
        return new Position(i, j);
    }

    public Position createDoor(TETile[][] world, Random random) {
        boolean isWall = false;
        int x = 0;
        int y = 0;
        while (!isWall) {
            int side = RandomUtils.uniform(random, 0, 4);
            if (side == 0) {
                // Door on left side of room
                x = position.getX();
                y = RandomUtils.uniform(random, position.getY() - height, position.getY());
            } else if (side == 1) {
                // Door on top side of room
                x = RandomUtils.uniform(random, position.getX() + 1, position.getX()
                        + width + 1);
                y = position.getY();
            } else if (side == 2) {
                // Door on right side of room
                x = position.getX() + width + 1;
                y = RandomUtils.uniform(random, position.getY() - height, position.getY());
            } else if (side == 3) {
                // Door on bottom side of room
                x = RandomUtils.uniform(random, position.getX() + 1, position.getX()
                        + width + 1);
                y = position.getY() - height - 1;
            }
            if (x != 0 && y != 0) {
                if (world[x][y] == Tileset.WALL) {
                    isWall = true;
                    world[x][y] = Tileset.LOCKED_DOOR;
                }
            }
        }
        return new Position(x, y);
    }
}
