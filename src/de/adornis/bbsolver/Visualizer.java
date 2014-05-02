package de.adornis.bbsolver;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Visualizer extends JFrame {

    public static boolean gui = true;
    public static boolean disabled = false;
    private final BMapHandler handler;

    private JPanel visualizerRootPanel;
    private JLabel[][] bubbleFields;
    private JLabel aa;
    private JLabel ba;
    private JLabel ca;
    private JLabel da;
    private JLabel ea;
    private JLabel ab;
    private JLabel bb;
    private JLabel cb;
    private JLabel db;
    private JLabel eb;
    private JLabel ac;
    private JLabel bc;
    private JLabel cc;
    private JLabel dc;
    private JLabel ec;
    private JLabel ad;
    private JLabel bd;
    private JLabel cd;
    private JLabel dd;
    private JLabel ed;
    private JLabel ae;
    private JLabel be;
    private JLabel ce;
    private JLabel de;
    private JLabel ee;
    private JLabel af;
    private JLabel bf;
    private JLabel cf;
    private JLabel df;
    private JLabel ef;
    private JButton restart;
    private JButton exit;
    private JButton cont;
    private JButton run;
    private JTextArea output;
    private JButton brute;
    private JTextField touches;
    private JComboBox modus;
    private JTextField delay;
    private JButton save;

    public Visualizer() {
        super("hello world");
        bubbleFields = new JLabel[5][6];
        if(gui) {
            setContentPane(visualizerRootPanel);
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setVisible(true);

            // auto scroll down
            ((DefaultCaret)output.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

            bubbleFields[0][0] = aa;
            bubbleFields[1][0] = ba;
            bubbleFields[2][0] = ca;
            bubbleFields[3][0] = da;
            bubbleFields[4][0] = ea;
            bubbleFields[0][1] = ab;
            bubbleFields[1][1] = bb;
            bubbleFields[2][1] = cb;
            bubbleFields[3][1] = db;
            bubbleFields[4][1] = eb;
            bubbleFields[0][2] = ac;
            bubbleFields[1][2] = bc;
            bubbleFields[2][2] = cc;
            bubbleFields[3][2] = dc;
            bubbleFields[4][2] = ec;
            bubbleFields[0][3] = ad;
            bubbleFields[1][3] = bd;
            bubbleFields[2][3] = cd;
            bubbleFields[3][3] = dd;
            bubbleFields[4][3] = ed;
            bubbleFields[0][4] = ae;
            bubbleFields[1][4] = be;
            bubbleFields[2][4] = ce;
            bubbleFields[3][4] = de;
            bubbleFields[4][4] = ee;
            bubbleFields[0][5] = af;
            bubbleFields[1][5] = bf;
            bubbleFields[2][5] = cf;
            bubbleFields[3][5] = df;
            bubbleFields[4][5] = ef;

            save.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handler.overwrite();
                }
            });
            restart.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    output.setText("");
                    handler.reset();
                    touches.setText(handler.getTouches() + "");
                }
            });
            exit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    Visualizer.this.dispatchEvent(new WindowEvent(Visualizer.this, WindowEvent.WINDOW_CLOSING));
                }
            });
            cont.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    handler.getMap().nextCycle();
                }
            });
            run.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handler.getMap().completeCycle(Long.parseLong(delay.getText()));
                }
            });
            brute.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ArrayList<int[]> results = handler.bruteForceThisShit(Integer.parseInt(touches.getText()));
                    cleanLog();
                    log("Results: ");
                    for (int[] result : results) {
                        log(" --> " + result[0] + " - " + result[1]);
                    }
                    logSectionEnd();
                }
            });
            modus.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    switch(e.getItem().toString().toLowerCase().charAt(0)) {
                        case 'm':
                            cont.setEnabled(true);
                            brute.setEnabled(false);
                            run.setEnabled(false);
                            save.setEnabled(false);
                            setFieldHandlers(false);
                            break;
                        case 'a':
                            run.setEnabled(true);
                            brute.setEnabled(false);
                            cont.setEnabled(false);
                            save.setEnabled(false);
                            setFieldHandlers(false);
                            break;
                        case 'b':
                            brute.setEnabled(true);
                            cont.setEnabled(false);
                            run.setEnabled(false);
                            save.setEnabled(false);
                            setFieldHandlers(false);
                            break;
                        case 'c':
                            brute.setEnabled(false);
                            cont.setEnabled(false);
                            run.setEnabled(false);
                            save.setEnabled(true);
                            setFieldHandlers(true);
                            break;
                        case 'n':
                            brute.setEnabled(false);
                            cont.setEnabled(false);
                            run.setEnabled(false);
                            setFieldHandlers(false);

                            for(int x = 0; x < BMapHandler.getSizeX(); x++) {
                                for(int y = 0; y < BMapHandler.getSizeY(); y++) {
                                    bubbleFields[x][y].setText("");
                                }
                            }

                            break;
                    }
                    handler.reset();
                }
            });
        }

        this.handler = new BMapHandler(this);

        touches.setText(handler.getTouches() + "");

        // TODO improve dirty workaround to get the event to fire
        modus.setSelectedIndex(0);
    }

    public void setFieldHandlers(final boolean editMode) {

        for(int x = 0; x < BMapHandler.getSizeX(); x++) {
            for(int y = 0; y < BMapHandler.getSizeY(); y++) {
                final int currentX = x;
                final int currentY = y;

                for( MouseListener l : bubbleFields[x][y].getMouseListeners()) {
                    bubbleFields[x][y].removeMouseListener(l);
                }
                if(editMode) {
                    bubbleFields[x][y].addMouseListener(new EditModeMouseListener(currentX, currentY));
                } else {
                    bubbleFields[x][y].addMouseListener(new RunModeMouseListener(currentX, currentY));
                }

                // in order to see background color
                bubbleFields[x][y].setOpaque(true);
            }
        }
    }

    public void visualize(Entity[][][] fields) {

        if(!disabled) {
            if (!gui) {
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

                for (int i = 0; i < BMapHandler.getSizeY(); i++) {
                    for (int j = 0; j < BMapHandler.getSizeX(); j++) {
                        if (fields[j][i][0] != null) {
                            int state = ((BField) fields[j][i][0]).getState();
                            bubbleFields[j][i].setText("=" + state + "= ");

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
                            }
                            bubbleFields[j][i].setBackground(c);
                        } else {
                            bubbleFields[j][i].setText("=-= ");
                            bubbleFields[j][i].setBackground(Color.WHITE);
                        }
                        for (int z = 1; z <= 4; z++) {
                            if (fields[j][i][z] != null) {
                                // bubbleFields[j][i].setText(bubbleFields[j][i].getText() + ((Bubble) fields[j][i][z]).getDirection());
                                bubbleFields[j][i].setText(bubbleFields[j][i].getText() + "(O)");
                            }
                        }
                    }
                }
            }
        }
    }

    public void log(String message) {
        output.append("\n" + message);
    }

    public void logSectionEnd() {
        output.append("\n--------=======------\n");
    }

    public void logBackground(String message) {
        System.out.println(message);
    }

    public void cleanLog() {
        output.setText("");
    }


    private class RunModeMouseListener implements MouseListener {

        private final int currentX;
        private final int currentY;

        public RunModeMouseListener(int currentX, int currentY) {
            this.currentX = currentX;
            this.currentY = currentY;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if(!touches.getText().equals("0")) {
                try {
                    handler.getMap().touch(currentX, currentY);
                } catch (TouchNotPossibleException e1) {
                    log("Touch not possible here!");
                }

                SwingWorker<Void, Void> cycleWorker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        handler.getMap().completeCycle(Long.parseLong(delay.getText()));
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
                if(e.getButton() == MouseEvent.BUTTON1) {
                    if(!e.isAltDown())
                        handler.getMap().modifyState(x, y, 1);
                    else
                        handler.getMap().resetField(x, y);
                } else if(e.getButton() == MouseEvent.BUTTON3) {
                    handler.getMap().modifyState(x, y, -1);
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
}
