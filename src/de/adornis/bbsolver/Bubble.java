package de.adornis.bbsolver;

public class Bubble extends Entity {
    // direction: 0, 1, 2, 3: north, east, south, west
    private final int direction;
    private int x;
    private int y;
    private boolean moved = false;

    Bubble(int x, int y, int direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    public boolean move() {

        moved = true;

        switch (direction) {
            case 0:
                y--;
                break;
            case 1:
                x++;
                break;
            case 2:
                y++;
                break;
            case 3:
                x--;
                break;
        }

        return !(x < 0 || x >= BMapHandler.getSizeX() || y < 0 || y >= BMapHandler.getSizeY());
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
