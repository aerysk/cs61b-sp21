package byow.Core;

public class Position implements Comparable<Position> {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position shift(int dx, int dy) {
        return new Position(x + dx, y + dy);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public int compareTo(Position p) {
        if (this.x < p.getX()) {
            if (this.y > p.getY()) {
                return -1;
            } else {
                return 1;
            }
        } else {
            if (this.y > p.getY()) {
                return 2;
            } else {
                return -2;
            }
        }
    }
}
