package de.adornis.bbsolver;


public class BField extends Entity {

    public static final int BLUE = 4;
    public static final int YELLOW = 3;
    public static final int GREEN = 2;
    public static final int RED = 1;

    private int state;

    public BField(int state) {
        this.state = state;
    }

    public boolean touch() {
        state--;
        if (state == 0) {
            return true;
        } else {
            return false;
        }
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }
}
