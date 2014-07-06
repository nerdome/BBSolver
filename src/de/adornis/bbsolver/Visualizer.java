package de.adornis.bbsolver;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Visualizer extends JFrame {

    // disable or enable gui as a whole
    public static boolean gui = true;
    private final BMapHandler handler;
    public int delay;

    private JPanel visualizerRootPanel;
    private JLabel[][] bubbleFields;
    private JPanel fieldsPanel;
    private JButton restart;
    private JButton exit;
    private JButton run;
    private JTextArea output;
    private JButton bruteForce;
    private JTextField touches;
    private JComboBox mode;
    private JTextField delayField;
    private JButton save;

    /**
     * sets UI components and initiates listeners
     */
    public Visualizer() {
        // sets title
        super("BBSolver");
        bubbleFields = new JLabel[BMapHandler.getSizeX()][BMapHandler.getSizeY()];
        setContentPane(visualizerRootPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        // auto scroll down
        ((DefaultCaret) output.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        for(int i = 0; i < bubbleFields[0].length; i++) {
            for(int j = 0; j < bubbleFields.length; j++) {
                bubbleFields[j][i] = new JLabel("akakaka");
                bubbleFields[j][i].setMinimumSize(new Dimension(86, 86));
                bubbleFields[j][i].setMaximumSize(new Dimension(86, 86));

                // in order to see background color
                bubbleFields[j][i].setOpaque(true);
            }
        }

        fieldsPanel.setLayout(new GridLayout(bubbleFields.length, bubbleFields[0].length));
        for( JLabel[] currentOuter : bubbleFields ) {
            for( JLabel current : currentOuter ) {
                fieldsPanel.add(current);
            }
        }

        delay = Integer.parseInt(delayField.getText());

        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handler.overwrite();
                output.append(handler.getCurrentMap().getDevOutput());
            }
        });
        restart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cleanLog();
                handler.reset();
                touches.setText(handler.getTouches() + "");
            }
        });
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Visualizer.this.dispatchEvent(new WindowEvent(Visualizer.this, WindowEvent.WINDOW_CLOSING));
            }
        });
        run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handler.getCurrentMap().doCycle(Long.parseLong(delayField.getText()));
            }
        });
        bruteForce.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                SwingWorker<ArrayList<int[][]>, Void> worker = new SwingWorker<ArrayList<int[][]>, Void>() {

                    @Override
                    protected ArrayList<int[][]> doInBackground() throws Exception {
                        ArrayList<int[][]> results = handler.bruteForceThatShit(delay);
                        return results;
                    }

                    @Override
                    protected void done() {

                        ArrayList<int[][]> results = new ArrayList<int[][]>();
                        try {
                            results = get();
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        } catch (ExecutionException e1) {
                            e1.printStackTrace();
                        }

                        // print results
                        log("Results: ");
                        String out = "";
                        if (results.size() != 0) {
                            for (int[][] result : results) {
                                for (int[] aResult : result) {
                                    if (aResult != null) {
                                        out += aResult[0] + " - " + aResult[1] + ", ";
                                    } else {
                                        out += "x - x, ";
                                    }
                                }
                                out = out.substring(0, out.length() - 2);
                                log(" --> " + out);
                                out = "";
                            }
                        } else {
                            log(" --> no results, sorry");
                        }
                        logSectionEnd();
                    }
                };
                worker.execute();
            }
        });
        delayField.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                if (!delayField.getText().equals("")) {
                    delay = Integer.parseInt(delayField.getText());
                }
            }
        });
        touches.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                if (!touches.getText().equals("")) {
                    handler.setTouches(Integer.parseInt(touches.getText()));
                }
            }
        });

        /**
         * changes the availability of buttons and changes how the fields behave on click
         * according to the selected mode
         */
        mode.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                switch (e.getItem().toString().toLowerCase().charAt(0)) {
                    case 'a': // automatic
                        run.setEnabled(true);
                        bruteForce.setEnabled(true);
                        save.setEnabled(false);
                        setFieldHandlers(false);
                        break;
                    case 'c': // create
                        bruteForce.setEnabled(false);
                        run.setEnabled(false);
                        save.setEnabled(true);
                        setFieldHandlers(true);
                        break;
                    case 'n': // nothing whatsoever
                        bruteForce.setEnabled(false);
                        run.setEnabled(false);
                        save.setEnabled(false);
                        setFieldHandlers(false);

                        for (JLabel[] column : bubbleFields) {
                            for (JLabel current : column) {
                                current.setText("");
                            }
                        }

                        break;
                }
                handler.reset();
            }
        });

        this.handler = new BMapHandler(this);

        touches.setText(handler.getTouches() + "");

        // TODO improve dirty workaround to get the event to fire
        mode.setSelectedIndex(0);

        pack();
    }

    /**
     * sets the listeners for the bubble fields to edit mode listeners or run mode listeners
     *
     * @param editMode whether to allow edit mode or run mode actions
     *                 - edit mode for changing the value of the fields
     *                 - run mode for solely letting the user click on the field to start the blast cycle with
     */
    public void setFieldHandlers(final boolean editMode) {

        for (int x = 0; x < BMapHandler.getSizeX(); x++) {
            for (int y = 0; y < BMapHandler.getSizeY(); y++) {
                final int currentX = x;
                final int currentY = y;

                for (MouseListener l : bubbleFields[x][y].getMouseListeners()) {
                    bubbleFields[x][y].removeMouseListener(l);
                }
                if (editMode) {
                    bubbleFields[x][y].addMouseListener(new EditModeMouseListener(currentX, currentY));
                } else {
                    bubbleFields[x][y].addMouseListener(new RunModeMouseListener(currentX, currentY));
                }
            }
        }
    }

    /**
     * draw the 3D array on the interface
     *
     * @param fields
     */
    public void visualize(Entity[][][] fields) {

        if (!gui) {

            // console output

            String res = "\n";
            for (int i = 0; i < BMapHandler.getSizeY(); i++) {
                for (int j = 0; j < BMapHandler.getSizeX(); j++) {
                    if (fields[j][i][0] != null && fields[j][i][1] != null) {
                        res += " " + ((BField) fields[j][i][0]).getState() + "b";
                    } else if (fields[j][i][0] == null && fields[j][i][1] == null) {
                        res += " - ";
                    } else if (fields[j][i][0] != null && fields[j][i][1] == null) {
                        res += " " + ((BField) fields[j][i][0]).getState() + " ";
                    } else if (fields[j][i][0] == null && fields[j][i][1] != null) {
                        res += "  b";
                    }
                }
                res += "\n";
            }

            System.out.println(res);

        } else {

            // gui output

            for (int y = 0; y < BMapHandler.getSizeY(); y++) {
                for (int x = 0; x < BMapHandler.getSizeX(); x++) {
                    JLabel currentComponent = (JLabel) (fieldsPanel.getComponent(x * BMapHandler.getSizeY() + y));
                    if (fields[x][y][0] != null) {
                        int state = ((BField) fields[x][y][0]).getState();
                        currentComponent.setText("=" + state + "= ");

                        Color c;
                        switch (state) {
                            case 1:
                                c = Color.RED;
                                break;
                            case 2:
                                c = Color.GREEN;
                                break;
                            case 3:
                                c = Color.YELLOW;
                                break;
                            case 4:
                                c = Color.BLUE;
                                break;
                            default:
                                c = Color.WHITE;
                                break;
                        }
                        currentComponent.setBackground(c);
                    } else {
                        currentComponent.setText("=-= ");
                        currentComponent.setBackground(Color.WHITE);
                    }
                    for (int z = 1; z <= 4; z++) {
                        if (fields[x][y][z] != null) {
                            currentComponent.setText(currentComponent.getText() + "(O)");
                        }
                    }
                }
            }
        }
    }

    /**
     * mouse listener for run mode
     */
    private class RunModeMouseListener implements MouseListener {

        private final int currentX;
        private final int currentY;

        public RunModeMouseListener(int currentX, int currentY) {
            this.currentX = currentX;
            this.currentY = currentY;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (!touches.getText().equals("0")) {
                try {
                    handler.getCurrentMap().touch(currentX, currentY);
                } catch (TouchNotPossibleException e1) {
                    log("Touch not possible here!");
                }

                SwingWorker<Void, Void> cycleWorker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        handler.getCurrentMap().doCycle(delay);
                        return null;
                    }
                };
                cycleWorker.execute();
                touches.setText((Integer.parseInt(touches.getText()) - 1) + "");
            } else {
                log("No more touches left! Restart to restart!");
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    /**
     * mouse listener for edit mode
     */
    private class EditModeMouseListener implements MouseListener {

        private int x;
        private int y;

        public EditModeMouseListener(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            try {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (!e.isAltDown()) handler.getCurrentMap().modifyState(x, y, 1);
                    else handler.getCurrentMap().resetField(x, y);
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    handler.getCurrentMap().modifyState(x, y, -1);
                }
            } catch (TouchNotPossibleException e1) {
                log("You can't click here!");
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    /**
     * logs a message to the log field
     *
     * @param message to log
     */
    public void log(String message) {
        output.append("\n" + message);
    }

    /**
     * logs a message to the log field with a level (representing brute force recursion levels)
     *
     * @param level   the recursion level
     * @param message to log
     */
    public void logL(int level, String message) {
        output.append("\n");
        for (int i = 0; i < level; i++) {
            output.append(" +");
        }
        output.append(" " + message);
    }

    /**
     * logs a section end line
     */
    public void logSectionEnd() {
        output.append("\n--------=======------\n");
    }

    /**
     * logs to the console
     *
     * @param message to log
     */
    public void logBackground(String message) {
        System.out.println(message);
    }

    /**
     * clear the log field
     */
    public void cleanLog() {
        output.setText("");
    }
}
