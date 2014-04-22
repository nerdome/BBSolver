package de.adornis.bbsolver;

public class Bubble extends Entity {
    // direction: 0, 1, 2, 3: north, east, south, west
    private final int direction;
    private int x;
    private int y;
    private BMap map;
    private boolean moved = false;

    Bubble(int x, int y, int direction, BMap map) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.map = map;
    }

    public boolean move() {

        moved = true;

        switch (direction) {
            case 0:
                y--;
                map.v.log("moved north");
                break;
            case 1:
                x++;
                map.v.log("moved east");
                break;
            case 2:
                y++;
                map.v.log("moved south");
                break;
            case 3:
                x--;
                map.v.log("moved west");
                break;
        }

        if (x < 0 || x >= map.sizeX || y < 0 || y >= map.sizeY) {
            return false;
        } else {
            return true;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getDirection() {
        return direction;
    }

    public boolean isMoved() {
        return moved;
    }

    public void resetMoved() {
        moved = false;
    }
}
