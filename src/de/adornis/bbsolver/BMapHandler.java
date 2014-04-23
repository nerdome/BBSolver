package de.adornis.bbsolver;

import java.util.ArrayList;

public class BMapHandler {
    private static final int sizeX = 5;
    private static final int sizeY = 6;
    public static final Visualizer v = Main.v;
    private BMap initialMap;
    private BMap head;
    private final ArrayList<BMap[]> maps = new ArrayList<BMap[]>();

    public BMapHandler() {
        initialMap = new BMap(generateExample());
        reset();
    }

    public void reset() {
        head = initialMap.clone();
    }

    private Entity[][][] generateExample() {
        Entity[][][] fields = new Entity[sizeX][sizeY][5];

        fields[0][0][0] = new BField(BField.GREEN);
        fields[0][1][0] = new BField(BField.GREEN);
        fields[0][2][0] = new BField(BField.BLUE);
        fields[0][3][0] = new BField(BField.YELLOW);
        fields[0][5][0] = new BField(BField.BLUE);

        fields[1][0][0] = new BField(BField.YELLOW);
        fields[1][1][0] = new BField(BField.RED);
        fields[1][2][0] = new BField(BField.RED);
        fields[1][3][0] = new BField(BField.RED);
        fields[1][4][0] = new BField(BField.BLUE);
        fields[1][5][0] = new BField(BField.GREEN);

        fields[2][0][0] = new BField(BField.GREEN);
        fields[2][1][0] = new BField(BField.YELLOW);
        fields[2][2][0] = new BField(BField.GREEN);
        fields[2][5][0] = new BField(BField.YELLOW);

        fields[3][1][0] = new BField(BField.GREEN);
        fields[3][3][0] = new BField(BField.RED);
        fields[3][5][0] = new BField(BField.RED);

        fields[4][1][0] = new BField(BField.GREEN);
        fields[4][3][0] = new BField(BField.RED);
        fields[4][4][0] = new BField(BField.GREEN);
        fields[4][5][0] = new BField(BField.RED);

        return fields;
    }

    public static int getSizeY() {
        return sizeY;
    }

    public static int getSizeX() {
        return sizeX;
    }

    public BMap getMap() {
        return head;
    }

    public ArrayList<int[]> bruteForceThisShit(int amountTouches) {
        amountTouches--;
        ArrayList<int[]> results = new ArrayList<int[]>();
        ArrayList<BMap> maps = new ArrayList<BMap>();
        for(int x = 0; x < sizeX; x++) {
            for(int y = 0; y < sizeY; y++) {
                v.cleanLog();
                BMap newMap = head.clone();
                newMap.touch(x, y);
                newMap.completeCycle(0);
                maps.add(newMap);
                if(newMap.isEmpty()) {
                    // success
                    results.add(new int[]{x, y});
                }
            }
        }

        // continue if enough touches left
        if(amountTouches > 0) {
            for(BMap current : maps) {
                v.logBackground("Testing new map");

                ArrayList<int[]> tempRes = bruteForceThisShit(amountTouches);

                for(int[] res : tempRes) {
                    v.logBackground(res[0] + " " + res[1]);
                }
                results.addAll(tempRes);
            }
        }

        return results;
    }

}
