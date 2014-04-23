package de.adornis.bbsolver;

import java.util.concurrent.Callable;

public class BMap implements Cloneable {
    // x | y | entities
    private Entity[][][] fields;
    public Visualizer v;
    private final int sizeX;
    private final int sizeY;

    public BMap(Entity[][][] contents) {
        this.v = Main.v;
        this.sizeX = BMapHandler.getSizeX();
        this.sizeY = BMapHandler.getSizeY();

        fields = contents;

        // avoid running into nullpointer in the beginning when v isn't initialized
        if(v != null) {
            v.visualize(fields);
        }
    }

    public void completeCycle(long delay) {
        if(delay == 0) {
            while (nextCycle()) ;
            v.visualize(fields);
        } else {
            while (nextCycle()) {
                try {
                    Thread.currentThread().sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void touch(int x, int y) {
        if(fields[x][y][0] != null) {
            fields[x][y][1] = new Bubble(x, y, 0);
        } else {
            v.log("Don't you push into thin air!");
        }
        v.visualize(fields);
    }

    public boolean nextCycle() {
        activate();
        boolean cont = move();
        v.logSectionEnd();
        v.visualize(fields);
        return cont;
    }

    private void activate() {
        for(int x = 0; x < sizeX; x++) {
            for(int y = 0; y < sizeY; y++) {

                for(int j = 1; j <= 4; j++) {
                    if (fields[x][y][j] != null) {
                        if(fields[x][y][0] != null) {
                            v.log("Touching " + x + " " + y);
                            fields[x][y][j] = null;
                            if (((BField) fields[x][y][0]).touch()) {
                                for (int d = 0; d < 4; d++) {
                                    fields[x][y][d + 1] = new Bubble(x, y, d);
                                    v.log("Created bubble " + x + " " + y + " " + d);
                                }
                                fields[x][y][0] = null;
                            }
                        }
                    }
                }

            }
        }

        v.visualize(fields);

    }

    private boolean move() {
        for(int x = 0; x < sizeX; x++) {
            for(int y = 0; y < sizeY; y++) {

                for(int j = 1; j <= 4; j++) {
                    if(fields[x][y][j] != null) {
                        if(!((Bubble) fields[x][y][j]).isMoved()) {
                            Bubble b = (Bubble) fields[x][y][j];
                            v.log("Moving bubble " + b.getX() + " " + b.getY() + " " + b.getDirection());
                            if (b.move()) {

                                int k = 1;
                                while (fields[b.getX()][b.getY()][k] != null) {
                                    k++;
                                }
                                fields[b.getX()][b.getY()][k] = b;
                            }

                            fields[x][y][j] = null;
                        }
                    }
                }

            }
        }

        int count = 0;
        for(int x = 0; x < sizeX; x++) {
            for(int y = 0; y < sizeY; y++) {
                for(int z = 1; z < 5; z++) {
                    if (fields[x][y][z] != null) {
                        ((Bubble) fields[x][y][z]).resetMoved();
                        count++;
                    }
                }
            }
        }
        v.log(count + " bubbles remaining");

        v.visualize(fields);
        return count > 0;
    }

    @Deprecated
    public Entity[][][] getContent() {
        return fields;
    }

    public boolean isEmpty() {
        for(int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                for(int k = 0; k < 5; k++) {
                    if(fields[x][y][k] != null) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public BMap clone() {

        Entity[][][] newContents = new Entity[sizeX][sizeY][5];

        for(int x = 0; x < sizeX; x++) {
            for(int y = 0; y < sizeY; y++) {
                if(fields[x][y][0] != null) {
                    newContents[x][y][0] = new BField(((BField) fields[x][y][0]).getState());
                }
                for(int k = 1; k < 5; k++) {
                    if(fields[x][y][k] != null) {
                        Bubble b = ((Bubble) fields[x][y][k]);
                        newContents[x][y][k] = new Bubble(b.getX(), b.getY(), b.getDirection());
                    }
                }
            }
        }

        return new BMap(newContents);
    }
}
