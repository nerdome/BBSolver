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
    private JLabel[][] out;
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

    public Visualizer() {
        super("hello world");
        this.handler = new BMapHandler();
        out = new JLabel[5][6];
        if(gui) {
            setContentPane(visualizerRootPanel);
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setVisible(true);

            // auto scroll down
            ((DefaultCaret)output.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

            out[0][0] = aa;
            out[1][0] = ba;
            out[2][0] = ca;
            out[3][0] = da;
            out[4][0] = ea;
            out[0][1] = ab;
            out[1][1] = bb;
            out[2][1] = cb;
            out[3][1] = db;
            out[4][1] = eb;
            out[0][2] = ac;
            out[1][2] = bc;
            out[2][2] = cc;
            out[3][2] = dc;
            out[4][2] = ec;
            out[0][3] = ad;
            out[1][3] = bd;
            out[2][3] = cd;
            out[3][3] = dd;
            out[4][3] = ed;
            out[0][4] = ae;
            out[1][4] = be;
            out[2][4] = ce;
            out[3][4] = de;
            out[4][4] = ee;
            out[0][5] = af;
            out[1][5] = bf;
            out[2][5] = cf;
            out[3][5] = df;
            out[4][5] = ef;

            restart.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    output.setText("");
                    handler.reset();
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
                            break;
                        case 'a':
                            run.setEnabled(true);
                            brute.setEnabled(false);
                            cont.setEnabled(false);
                            break;
                        case 'b':
                            brute.setEnabled(true);
                            cont.setEnabled(false);
                            run.setEnabled(false);
                            break;
                        case 'n':
                            brute.setEnabled(false);
                            cont.setEnabled(false);
                            run.setEnabled(false);

                            for(int x = 0; x < BMapHandler.getSizeX(); x++) {
                                for(int y = 0; y < BMapHandler.getSizeY(); y++) {
                                    out[x][y].setText("");
                                }
                            }

                            break;
                    }
                    handler.reset();
                }
            });

            for(int x = 0; x < BMapHandler.getSizeX(); x++) {
                for(int y = 0; y < BMapHandler.getSizeY(); y++) {
                    final int currentX = x;
                    final int currentY = y;
                    out[x][y].addMouseListener(new MouseListener() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            handler.getMap().touch(currentX,currentY);

                            SwingWorker<Void, Void> cycleWorker = new SwingWorker<Void, Void>() {
                                @Override
                                protected Void doInBackground() throws Exception {
                                    handler.getMap().completeCycle(Long.parseLong(delay.getText()));
                                    return null;
                                }
                            };
                            cycleWorker.execute();
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
                    });
                    // in order to see background color
                    out[x][y].setOpaque(true);
                }
            }
        }

        // TODO improve dirty workaround to get the event to fire
        modus.setSelectedIndex(1);
        modus.setSelectedIndex(0);
    }

    public void visualize(Entity[][][] fields) {
        logBackground("Visualizing...");

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
                            out[j][i].setText("=" + state + "= ");

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
                            out[j][i].setBackground(c);
                        } else {
                            out[j][i].setText("=-= ");
                            out[j][i].setBackground(Color.WHITE);
                        }
                        for (int z = 1; z <= 4; z++) {
                            if (fields[j][i][z] != null) {
                                out[j][i].setText(out[j][i].getText() + ((Bubble) fields[j][i][z]).getDirection());
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

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
