package de.adornis.bbsolver;

public class BMap implements Cloneable {

    // format: x-coordinate | y-coordinate | array of max 5 entities (1 Field, 4 Bubbles)
    private Entity[][][] fields;
    private BMapHandler handler;
    private final int sizeX;
    private final int sizeY;

    /**
     * creates a BMap
     *
     * @param contents a 3 dimensional array to prefill the map
     * @param handler the handler this map will belong to
     */
    public BMap(Entity[][][] contents, BMapHandler handler) {
        this.handler = handler;
        this.sizeX = BMapHandler.getSizeX();
        this.sizeY = BMapHandler.getSizeY();

        fields = contents;

        handler.notifyChangedFields(fields);
    }

    /**
     * creates a BMap and makes an empty map for its fields
     *
     * @param handler the handler this map will belong to
     */
    public BMap(BMapHandler handler) {
        Entity[][][] emptyEntity = new Entity[BMapHandler.getSizeX()][BMapHandler.getSizeY()][5];

        this.handler = handler;
        this.sizeX = BMapHandler.getSizeX();
        this.sizeY = BMapHandler.getSizeY();

        fields = emptyEntity;

        handler.notifyChangedFields(fields);
    }

    /**
     * starts the next cycle until no moves are possible anymore with the bubbles provided probably from touch(x, y)
     *
     * @param delay the amount of milliseconds before the next cycle is started
     */
    public void doCycle(long delay) {
        // save some code in case there is no delay (useful for brute forcing many maps at once)
        if(delay == 0) {
            while (nextIteration()) ;
        } else {
            while (nextIteration()) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        handler.notifyChangedFields(fields);
    }

    /**
     * combine touch() and doCycle()
     *
     * @param x x-coordinate of the field
     * @param y y-coordinate of the field
     * @param delay the amount of milliseconds before the next cycle is started
     * @throws TouchNotPossibleException
     */
    public void completeCycle(int x, int y, long delay) throws TouchNotPossibleException {
        touch(x, y);
        doCycle(delay);
    }

    /**
     * create the first bubble on the touched field in order to be activated and moved in the next cycle
     *
     * @param x x-coordinate of the first bubble
     * @param y y-coordinate of the first bubble
     * @throws TouchNotPossibleException
     */
    public void touch(int x, int y) throws TouchNotPossibleException {
        if(fields[x][y][0] != null) {
            fields[x][y][1] = new Bubble(x, y, 0);
        } else {
            throw new TouchNotPossibleException();
        }

        handler.notifyChangedFields(fields);
    }

    /**
     * first activates all bubbles on the map, then moves all of them according to their direction
     *
     * @return true or false, depending on whether there are more possible moves to do
     */
    public boolean nextIteration() {
        // trigger all bubbles on a non-empty field
        activate();
        // move all bubbles by one. If nothing is movable, move() returns false
        boolean doNextCycle = move();
        handler.notifyChangedFields(fields);
        return doNextCycle;
    }

    /**
     * looks for all bubbles that are on the same field as a non-empty BField
     * and triggers them, decreasing the state of the field by one
     */
    private void activate() {
        for(int x = 0; x < sizeX; x++) {
            for(int y = 0; y < sizeY; y++) {

                for(int j = 1; j <= 4; j++) {
                    if (fields[x][y][j] != null) {
                        if(fields[x][y][0] != null) {
                            fields[x][y][j] = null;
                            if (((BField) fields[x][y][0]).touch()) {
                                for (int d = 0; d < 4; d++) {
                                    fields[x][y][d + 1] = new Bubble(x, y, d);
                                }
                                fields[x][y][0] = null;
                            }
                        }
                    }
                }

            }
        }

        handler.notifyChangedFields(fields);

    }

    /**
     * moves all bubbles in the direction they're heading using the moved tag in the Bubble class
     * in order to make sure they're not moved twice
     *
     * @return whether there are bubbles left to move in the next cycle
     */
    private boolean move() {
        for(int x = 0; x < sizeX; x++) {
            for(int y = 0; y < sizeY; y++) {

                for(int j = 1; j <= 4; j++) {
                    if(fields[x][y][j] != null) {
                        if(!((Bubble) fields[x][y][j]).isMoved()) {
                            Bubble b = (Bubble) fields[x][y][j];
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

        handler.notifyChangedFields(fields);
        return count > 0;
    }

    /**
     * checks if the field array is empty
     *
     * @return whether the field array is empty
     */
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

    /**
     * sets a new state of the BField given
     *
     * @param x x-coordinate of the field
     * @param y y-coordinate of the field
     * @param amount to add to the state (negative values encouraged)
     * @throws TouchNotPossibleException
     */
    public void modifyState(int x, int y, int amount) throws TouchNotPossibleException {
        BField f = (BField)fields[x][y][0];

        if(f != null) {
            if(f.getState() + amount >= 1 && f.getState() + amount <= 4) {
                f.setState(f.getState() + amount);
            } else if(f.getState() + amount == 0) {
                fields[x][y][0] = null;
            } else {
                throw new TouchNotPossibleException();
            }
        } else if(amount >= 1 && amount <= 4) {
            fields[x][y][0] = new BField(amount);
        } else {
            throw new TouchNotPossibleException();
        }

        handler.notifyChangedFields(fields);

    }

    /**
     * creates a cloned copy of the whole map
     *
     * @return the copy of the map
     */
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

        return new BMap(newContents, handler);
    }

    /**
     * resets the field with the given coordinates
     *
     * @param x x-coordinate of the field to reset
     * @param y y-coordinate of the field to reset
     */
    public void resetField(int x, int y) {
        fields[x][y][0] = null;
        handler.notifyChangedFields(fields);
    }

    /**
     * returns a string of the lines of code needed to create the map currently in here
     *
     * @return output string to reuse in the code
     */
    public String getDevOutput() {
        String back = "";
        for(int x = 0; x < sizeX; x++) {
            for(int y = 0; y <sizeY; y++) {
                if(fields[x][y][0] != null) {
                    String color = "";
                    switch(((BField) fields[x][y][0]).getState()) {
                        case 1:
                            color = "RED";
                            break;
                        case 2:
                            color = "GREEN";
                            break;
                        case 3:
                            color = "YELLOW";
                            break;
                        case 4:
                            color = "BLUE";
                            break;
                    }
                    back += "fields[" + x + "][" + y + "][0] = new BField(BField." + color + ");\n";
                }
            }
        }
        return back;
    }
}