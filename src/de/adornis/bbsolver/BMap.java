package de.adornis.bbsolver;

import java.util.ArrayList;



// get rid of origFields, make new BMap every time



public class BMap {
    // x | y | entities
    private Entity[][][] origFields;
    private Entity[][][] fields;
    public static Visualizer v = null;
    public final int sizeX;
    public final int sizeY;

    public BMap(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        v = new Visualizer(this);

        origFields = new Entity[sizeX][sizeY][5];
        fields = new Entity[sizeX][sizeY][5];
        fillExample();
        restoreMap();

        v.visualize();
    }

    public BMap(BMap parent) {
        Entity[][][] parentFields = parent.getMap();
        this.sizeX = parentFields.length;
        this.sizeY = parentFields[0].length;
        v.shiftFocus(this);

        this.origFields = parentFields;
        fields = new Entity[sizeX][sizeY][5];
        restoreMap();

        v.visualize();
    }

    public void restoreMap() {

        for(int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                for(int k = 0; k < 5; k++) {
                    fields[x][y][k] = null;
                }
            }
        }
        for(int x = 0; x < sizeX; x++) {
            for(int y = 0; y < sizeY; y++) {
                BField old = (BField) origFields[x][y][0];
                if(origFields[x][y][0] != null) {
                    fields[x][y][0] = new BField(old.getState());
                }
            }
        }
        v.visualize();
    }

    private void fillExample() {
        origFields[0][0][0] = new BField(BField.GREEN);
        origFields[0][2][0] = new BField(BField.GREEN);
        origFields[0][4][0] = new BField(BField.RED);
        origFields[0][5][0] = new BField(BField.RED);
    }

    public void completeCycle(int x, int y) {
        touch(x, y);
        while(nextCycle());
        v.visualize();
    }

    public void touch(int x, int y) {
        if(fields[x][y][0] != null) {
            fields[x][y][1] = new Bubble(x, y, 0, this);
        } else {
            v.log("Don't you push into thin air!");
        }
        v.visualize();
    }

    public boolean nextCycle() {
        activate();
        boolean cont = move();
        v.logSectionEnd();
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
                                    fields[x][y][d + 1] = new Bubble(x, y, d, this);
                                    v.log("Created bubble " + x + " " + y + " " + d);
                                }
                                fields[x][y][0] = null;
                            }
                        }
                    }
                }

            }
        }

        v.visualize();

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

        v.visualize();
        if(count > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Entity[][][] getMap() {
        return fields;
    }

    private boolean isEmpty() {
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

    public ArrayList<int[]> bruteForceThisShit(int amountTouches) {
        amountTouches--;
        ArrayList<int[]> results = new ArrayList<int[]>();
        ArrayList<BMap> maps = new ArrayList<BMap>();
        for(int x = 0; x < sizeX; x++) {
            for(int y = 0; y < sizeY; y++) {
                v.cleanLog();
                BMap newMap = new BMap(this);
                newMap.completeCycle(x, y);
                maps.add(newMap);
                if(newMap.isEmpty()) {
                    // success
                    results.add(new int[]{x, y});
                }
                restoreMap();
            }
        }

        // continue if enough touches left
        if(amountTouches > 0) {
            for(BMap current : maps) {
                v.logBackground("Testing new map");

                try {
                    Thread.currentThread().sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                ArrayList<int[]> tempRes = current.bruteForceThisShit(amountTouches);

                for(int[] res : tempRes) {
                    v.logBackground(res[0] + " " + res[1]);
                }
                results.addAll(tempRes);
            }
        }

        return results;
    }
}
