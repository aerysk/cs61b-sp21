package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import java.util.ArrayList;
import java.util.Random;

/**
 * The following sources gave me some inspiration.
 * @source https://gamedev.stackexchange.com/questions/114767/procedural-dungeon-generation-
 * connect-rooms-with-passageways
 * @source https://www.reddit.com/r/gamedev/comments/1dlwc4/procedural_dungeon_generation_
 * algorithm_explained/
 */
public class World {
    int width;
    int height;
    int maxRooms = 15;
    TETile[][] completeWorld;
    TETile environment;
    Position playerPosition;
    Position doorPosition;
    int flowerCount = 0;
    boolean doorLocked = false;
    TETile[][] garden;
    boolean inGarden = false;
    Position playerInGarden;
    Position gardenEntrance;

    public World(int w, int h, TETile e) {
        width = w;
        height = h;
        environment = e;
        completeWorld = null;
        garden = null;
    }

    public TETile[][] generateWorld(long seed) {
        TETile[][] world = new TETile[width][height];
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        flowerCount = 0;
        ArrayList<Position> centerPoints = new ArrayList<>();
        Random randomization = new Random(seed);
        int numRooms = RandomUtils.uniform(randomization, 5, maxRooms);
        ArrayList<Room> roomsList = generateRooms(world, numRooms, randomization, centerPoints);
        ArrayList<Hallway> hallwaysList = generateHallways(world, roomsList, randomization,
                centerPoints);
        generateFlooring(world, roomsList, hallwaysList, randomization);
        placeAvatar(world, roomsList, randomization);
        placeDoor(world, roomsList, randomization);
        completeWorld = world;
        Garden newGarden = new Garden(width, height);
        garden = newGarden.returnGardenTiles();
        gardenEntrance = newGarden.entrance();
        return world;
    }

    private ArrayList<Room> generateRooms(TETile[][] world, int rooms, Random random,
                                                 ArrayList<Position> centers) {
        ArrayList<Room> roomsList = new ArrayList<>();
        for (int i = 0; i < rooms; i++) {
            int x = RandomUtils.uniform(random, 1, width - 6);
            int y = RandomUtils.uniform(random, 5, height - 1);
            Position p = new Position(x, y);
            int roomHeight = RandomUtils.uniform(random, 3, Math.min(6, y - 1));
            int roomWidth = RandomUtils.uniform(random, 3, Math.min(6, width - x + 1));
            Room currentRoom = new Room(roomHeight, roomWidth, p, world);
            roomsList.add(currentRoom);
            centers.add(currentRoom.findCenter(random));
        }
        return roomsList;
    }



    private ArrayList<Hallway> generateHallways(TETile[][] world, ArrayList<Room> rooms,
                                                Random random, ArrayList<Position> centers) {
        ArrayList<Hallway> hallwaysList = new ArrayList<>();
        int hallHeight = 0;
        int hallWidth = 0;
        for (int i = 0; i < rooms.size(); i++) {
            Position current = centers.get(i);
            int currentX = current.getX();
            int currentY = current.getY();
            Position next = centers.get(Math.floorMod(i + 1, rooms.size()));
            int nextX = next.getX();
            int nextY = next.getY();
            if (currentX == nextX) {
                hallHeight = Math.abs(currentY - nextY);
                hallWidth = 1;
            } else if (currentY == nextY) {
                hallHeight = 1;
                hallWidth = Math.abs(currentX - nextX);
            } else {
                int caseType = current.compareTo(next);
                if (caseType < 0) {
                    hallwayHelper1(current, next, world, random, hallwaysList);
                } else {
                    hallwayHelper2(current, next, world, random, hallwaysList);
                }
                continue;
            }
            Position p = new Position(Math.min(currentX, nextX), Math.max(currentY, nextY));
            hallwaysList.add(new Hallway(hallHeight, hallWidth, p, world));
        }
        return hallwaysList;
    }

    /** Helps create L-shaped hallways when the left center has a larger y but smaller x
     *  than the right center. */
    private void hallwayHelper1(Position p1, Position p2, TETile[][] world,
                                       Random random, ArrayList<Hallway> hallways) {
        int direction = RandomUtils.uniform(random, 0, 2);
        if (p1.getX() > p2.getX()) {
            Position temp = p2;
            p2 = p1;
            p1 = temp;
        }
        int p1X = p1.getX();
        int p1Y = p1.getY();
        int p2X = p2.getX();
        int p2Y = p2.getY();
        switch (direction) {
            case 0:
                Position hall1 = p1;
                int hall1Height = 1;
                int hall1Width = p2X - p1X;
                hallways.add(new Hallway(hall1Height, hall1Width, hall1, world));
                Position hall2 = new Position(p2X, p1Y);
                int hall2Height = p1Y - p2Y;
                int hall2Width = 1;
                hallways.add(new Hallway(hall2Height, hall2Width, hall2, world));
                break;
            case 1:
                hall1 = p1;
                hall1Height = p1Y - p2Y;
                hall1Width = 1;
                hallways.add(new Hallway(hall1Height, hall1Width, hall1, world));
                hall2 = new Position(p1X, p2Y);
                hall2Height = 1;
                hall2Width = p2X - p1X;
                hallways.add(new Hallway(hall2Height, hall2Width, hall2, world));
                break;
            default:
                break;
        }
    }

    /** Helps create L-shaped hallways when the left center has a smaller y and smaller x
     *  than the right center. */
    private void hallwayHelper2(Position p1, Position p2, TETile[][] world,
                                       Random random, ArrayList<Hallway> hallways) {
        int direction = RandomUtils.uniform(random, 0, 2);
        if (p1.getX() > p2.getX()) {
            Position temp = p2;
            p2 = p1;
            p1 = temp;
        }
        int p1X = p1.getX();
        int p1Y = p1.getY();
        int p2X = p2.getX();
        int p2Y = p2.getY();
        switch (direction) {
            case 0:
                Position hall1 = new Position(p1X, p2Y);
                int hall1Height = p2Y - p1Y;
                int hall1Width = 1;
                hallways.add(new Hallway(hall1Height, hall1Width, hall1, world));
                Position hall2 = new Position(p1X + 1, p2Y);
                int hall2Height = 1;
                int hall2Width = p2X - p1X;
                hallways.add(new Hallway(hall2Height, hall2Width, hall2, world));
                break;
            case 1:
                hall1 = p1;
                hall1Height = 1;
                hall1Width = p2X - p1X;
                hallways.add(new Hallway(hall1Height, hall1Width, hall1, world));
                hall2 = new Position(p2X - 1, p2Y);
                hall2Height = p2Y - p1Y;
                hall2Width = 1;
                hallways.add(new Hallway(hall2Height, hall2Width, hall2, world));
                break;
            default:
                break;
        }
    }

    private void generateFlooring(TETile[][] world, ArrayList<Room> rooms,
                                  ArrayList<Hallway> hallways, Random random) {
        for (int i = 0; i < rooms.size(); i++) {
            rooms.get(i).generateFloor(world, random, environment);
        }
        for (int i = 0; i < hallways.size(); i++) {
            hallways.get(i).generateFloor(world, random, environment);
        }
    }

    private void placeAvatar(TETile[][] world, ArrayList<Room> rooms, Random random) {
        int index = RandomUtils.uniform(random, 0, rooms.size());
        Room startRoom = rooms.get(index);
        playerPosition = startRoom.placeAvatar(world, random);
    }

    private void placeDoor(TETile[][] world, ArrayList<Room> rooms, Random random) {
        int index = RandomUtils.uniform(random, 0, rooms.size());
        Room doorRoom = rooms.get(index);
        doorPosition = doorRoom.createDoor(world, random);
    }

    public Position getPlayerPosition() {
        if (inGarden) {
            return playerInGarden;
        }
        return playerPosition;
    }

    public void updatePlayerPosition(Position p) {
        int x = p.getX();
        int y = p.getY();
        if (inGarden) {
            garden[playerInGarden.getX()][playerInGarden.getY()] = Tileset.GRASS;
            playerInGarden = p;
            garden[x][y] = Tileset.AVATAR;
        } else {
            completeWorld[playerPosition.getX()][playerPosition.getY()] = environment;
            playerPosition = p;
            if (completeWorld[x][y] == Tileset.FLOWER) {
                flowerCount++;
                if (flowerCount > 0 && !doorLocked) {
                    doorLocked = true;
                    unlockDoor();
                }
            }
            completeWorld[x][y] = Tileset.AVATAR;
        }
    }

    private void unlockDoor() {
        int x = doorPosition.getX();
        int y = doorPosition.getY();
        completeWorld[x][y] = Tileset.UNLOCKED_DOOR;
    }

    public TETile[][] getWorldArray() {
        if (inGarden) {
            return garden;
        }
        return completeWorld;
    }

    public int getFlowerCount() {
        return flowerCount;
    }

    public void enterGarden() {
        playerInGarden = gardenEntrance;
        garden[playerInGarden.getX()][playerInGarden.getY()] = Tileset.AVATAR;
        inGarden = true;
    }

    public void plantOrPickUpFlower(Position p) {
        int x = p.getX();
        int y = p.getY();
        if (garden[x][y] == Tileset.FLOWER) {
            garden[x][y] = Tileset.GRASS;
            flowerCount++;
        } else {
            if (flowerCount > 0) {
                garden[x][y] = Tileset.FLOWER;
                flowerCount--;
            }
        }
    }

    public void exitGarden() {
        inGarden = false;
    }

    public TETile[][] getGardenArray() {
        return garden;
    }

    public boolean playerInGarden() {
        return inGarden;
    }
}
