package de.adornis.bbsolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BMapHandler {
    private static final int sizeX = 5, sizeY = 6;
    private static int touches = 5;
    public static Visualizer v;
    // the original map and the map used to interact with
    private BMap initialMap, currentMap;
    private boolean updateVisualizer = true;

    /**
     * creates a new handler with its initial map and clones it to the current map
     *
     * @param v backward reference to its visualizer
     */
    public BMapHandler(Visualizer v) {
        this.v = v;
        initialMap = new BMap(generateExample(), this);
        // initialMap = new BMap(this);
        reset();
    }

    /**
     * @return the number of touches allowed for this map
     */
    public int getTouches() {
        return touches;
    }

    /**
     * sets the number of touches used to solve the map
     *
     * @param touches number of touches allowed
     */
    public void setTouches(int touches) {
        BMapHandler.touches = touches;
    }

    /**
     * resets the map to interact with with the data of the initial map
     */
    public void reset() {
        currentMap = initialMap.clone();
    }

    /**
     * set the initial map to the current state of the current map
     */
    public void overwrite() {
        initialMap = currentMap.clone();
    }

    /**
     * generates a preset in order to pass it to maps depending on this handler
     *
     * @return the field array with the preset
     */
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
        fields[1][3][0] = new BField(BField.BLUE);
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

    /**
     * @return the vertical size of the field
     */
    public static int getSizeY() {
        return sizeY;
    }

    /**
     * @return the horizontal size of the field
     */
    public static int getSizeX() {
        return sizeX;
    }

    /**
     * @return the map used to interact with in its current state
     */
    public BMap getCurrentMap() {
        return currentMap;
    }

    /**
     * is called on a field change on the map
     * orders the interface to visualize the new array
     *
     * @param fields array to visualize in the interface
     */
    public void notifyChangedFields(Entity[][][] fields) {
        if(updateVisualizer) {
            v.visualize(fields);
        }
    }

    /**
     * Figures out where you have to touch in order to clear the map
     *
     * @param delay to be used between events to view the results
     * @return an ArrayList of int[amountTouches] with int[2]s for each pair of coordinates for the results
     */
    public ArrayList<int[][]> bruteForceThatShit(int delay) {

        // temporarily return a list with only one element until I figure out a way to return all possible solutions

        HashMap<Integer, int[]> tempResults = doBruteForceLayer(initialMap, 0, delay);
        v.log(tempResults.size() + "");
        int[][] tempResultsArray = new int[touches][2];
        for(Map.Entry<Integer, int[]> entry : tempResults.entrySet()) {
            tempResultsArray[entry.getKey()] = entry.getValue();
        }

        ArrayList<int[][]> back = new ArrayList<int[][]>();
        back.add(tempResultsArray);

        v.logSectionEnd();
        return back;
    }

    /**
     * helper function used recursively which works on one level of touching the map
     * aka one touch of the n touches you're given to solve the puzzle
     *
     * // TODO make it return all possible results, not only the first it finds
     *
     * @param inputMap the map used to start with
     * @param touchesDone the amount of touches already done on the map
     * @param delay used between actions to view the results
     * @return a HashMap with all coordinate pairs from deeper recursions and this one
     *      each assigned to the correct level
     */
    private HashMap<Integer, int[]> doBruteForceLayer(BMap inputMap, int touchesDone, int delay) {

        HashMap<Integer, int[]> results = new HashMap<Integer, int[]>();

        // all touches used ==> return empty HashMap before doing any of the logic
        if(touchesDone == touches) {
            return new HashMap<Integer, int[]>();
        } else {
            // for each field, check if a click on that field would empty the map. If touches left, go deeper into recursion
            for (int x = 0; x < sizeX; x++) {
                for (int y = 0; y < sizeY; y++) {
                    BMap map = inputMap.clone();
                    try {
                        map.completeCycle(x, y, delay);
                        if (map.isEmpty()) {
                            results.put(touchesDone, new int[]{x, y});
                            v.logL(touchesDone, "That's it!");
                            return results;
                        } else {
                            HashMap<Integer, int[]> tempResults = doBruteForceLayer(map, touchesDone + 1, delay);
                            // if nothing was returned, omit and keep going
                            if (tempResults.size() != 0) {
                                results.putAll(tempResults);
                                v.logL(touchesDone, "Tracing back...");
                                results.put(touchesDone, new int[]{x, y});
                                return results;
                            }
                        }
                    } catch (TouchNotPossibleException e) {
                        // no bubble on this field, omitting
                    }
                }
            }
        }

        // went through the whole sub-map, found nothing, returning empty
        return new HashMap<Integer, int[]>();
    }
}
