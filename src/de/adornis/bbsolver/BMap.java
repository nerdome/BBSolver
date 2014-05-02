package de.adornis.bbsolver;

public class BMap implements Cloneable {
    // x | y | entities
    private Entity[][][] fields;
    private BMapHandler handler;
    private final int sizeX;
    private final int sizeY;

    public BMap(Entity[][][] contents, BMapHandler handler) {
        this.handler = handler;
        this.sizeX = BMapHandler.getSizeX();
        this.sizeY = BMapHandler.getSizeY();

        fields = contents;

        handler.update(fields);
    }

    public void completeCycle(long delay) {
        if(delay == 0) {
            while (nextCycle()) ;
            handler.update(fields);
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

    public void touch(int x, int y) throws TouchNotPossibleException {
        if(fields[x][y][0] != null) {
            fields[x][y][1] = new Bubble(x, y, 0);
        } else {
            throw new TouchNotPossibleException();
        }
        handler.update(fields);
    }

    public boolean nextCycle() {
        activate();
        boolean cont = move();
        handler.update(fields);
        return cont;
    }

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

        handler.update(fields);

    }

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

        handler.update(fields);
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

        handler.update(fields);

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

        return new BMap(newContents, handler);
    }

    public void resetField(int x, int y) {
        fields[x][y][0] = null;
        handler.update(fields);
    }
}
