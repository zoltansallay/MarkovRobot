package hu.meditations.markovrobot;

import javax.swing.JApplet; 
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.Timer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MarkovRobot extends JApplet {
    private static final long serialVersionUID = 1L;

    public static void main(final String[] args) {
        JFrame frame = new JFrame("MarkovRobot szimuláció");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(50,50);
        frame.setSize(1000,600);
        MarkovRobotSimulator sim = new MarkovRobotSimulator(frame);
        frame.setContentPane(sim);
        frame.pack();
        frame.setVisible(true);
    }

    private static class MarkovRobotSimulator extends JPanel {
        private final long serialVersionUID = 1L;

        private JPanel mainPanel;
        private Timer screenTimer;                  // Timer that fires to animation step
        private final int screenInterval = 35;      // Milliseconds between screen updates

        private Race race;
        
        private double time = 0.0;                  // Simulation time
        private final double deltaT = 0.05;         // Simulation time step
        private final double maxTime = 25.0;        // Simulation time end
        private int screenOneStep = 16;             // Number of deltaT steps in one animation step
        
        private final int MAX_NUM_OF_PATHS = 10;
        private final int numOfPaths = 1;
        private int actualPathIndex = 0;
        private Path[] pathStack = new Path[MAX_NUM_OF_PATHS];

        private final int MAX_NUM_OF_CONTROLS = 100;
        private final int numOfControls = 50;
        private int actualControlsIndex = 0;
        private Controls[] controlsStack = new Controls[MAX_NUM_OF_CONTROLS];

        private MarkovRobotSimulator(final JFrame frame) {
            setLayout(new BorderLayout());
            JPanel buttonPanel = new JPanel(new FlowLayout());
            add(buttonPanel, BorderLayout.NORTH);
            JButton bStart = new JButton("Start");
            buttonPanel.add(bStart);
            JButton bStop = new JButton("Stop");
            buttonPanel.add(bStop);
            JButton bSpeedUp = new JButton("Speed up");
            buttonPanel.add(bSpeedUp);
            JButton bSpeedDown = new JButton("Speed down");
            buttonPanel.add(bSpeedDown);
            JButton bReset = new JButton("Reset");
            buttonPanel.add(bReset);
            JButton bMutateControls = new JButton("Mutate Controls");
            buttonPanel.add(bMutateControls);
            JButton bMutatePath = new JButton("Mutate Path");
            buttonPanel.add(bMutatePath);
            JButton bEvolve = new JButton("Evolve");
            buttonPanel.add(bEvolve);

            pathStack[actualPathIndex] = new Path("data/path00.dat");
            controlsStack[actualControlsIndex] = new Controls("data/control00.dat");
            race = new Race(controlsStack[actualControlsIndex], pathStack[actualPathIndex]);

            mainPanel = new JPanel(new BorderLayout());
            add(mainPanel, BorderLayout.CENTER);
            mainPanel.add(race, BorderLayout.CENTER);
            mainPanel.add(controlsStack[actualControlsIndex], BorderLayout.EAST);
            
            screenTimer = new Timer(screenInterval, new ScreenTimerAction());
            reset();

            bStart.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    screenTimer.start();
                }
            });

            bStop.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    screenTimer.stop();
                }
            });

            bSpeedUp.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (screenOneStep < 1024)
                        screenOneStep *= 2;
                }
            });

            bSpeedDown.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (screenOneStep > 1)
                        screenOneStep /= 2;
                }
            });

            bReset.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    screenTimer.stop();
                    reset();
                }
            });

            bMutateControls.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    screenTimer.stop();
                    controlsStack[actualControlsIndex].mutate();
                    repaint();
                    //reset();
                }
            });

            bMutatePath.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    pathStack[actualPathIndex].mutate();
                    screenTimer.stop();
                    reset();
                }
            });

            bEvolve.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    screenTimer.stop();
                    mainPanel.remove(controlsStack[actualControlsIndex]);
                    actualControlsIndex = startEvolution();
                    race.setControls(controlsStack[actualControlsIndex]);
                    mainPanel.add(controlsStack[actualControlsIndex], BorderLayout.EAST);
                    mainPanel.revalidate();
                    mainPanel.repaint();
                    reset();
                    //g.drawString("N="+N, 15 * zoom + shift, 15 * zoom + shift);
                }
            });
        }

        private void reset() {
            time = 0.0;
            race.reset();
            repaint();
        }

        class ScreenTimerAction implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < screenOneStep; i++) {
                    race.step(deltaT);
                    time = time + deltaT;
                }
                repaint();
            }
        }
        
        private void createPool(int n, int source) {
            for(int i = 0; i < n; i++) {
                if(i != source && i < MAX_NUM_OF_CONTROLS) {
                    controlsStack[i] = new Controls(controlsStack[source]);
                    controlsStack[i].mutate();
                }
            }
        }

        private int startEvolution() {
            double bestSuccess = 0.0;
            int bestControlsIndex;
            createPool(numOfControls, actualControlsIndex);
            bestControlsIndex = actualControlsIndex;
            for(int i = 0; i < numOfControls; i++) {
                race.setControls(controlsStack[i]);
                race.run(deltaT, maxTime);
                if(race.getSuccess() > bestSuccess) {
                    bestSuccess = race.getSuccess();
                    bestControlsIndex = i;
                }
            }
            return bestControlsIndex;
        }

    }
    
    private static class panel2 extends JPanel {
        private javax.swing.JButton jButton1;
        private javax.swing.JButton jButton2;
        private javax.swing.JButton jButton3;
            
        private panel2() {
            jButton1 = new javax.swing.JButton();
            jButton2 = new javax.swing.JButton();
            jButton3 = new javax.swing.JButton();
            jButton1.setText("gomb");
            jButton2.setText("jButton2");
            jButton3.setText("jButton3");

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
            setLayout(layout);
            layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(50, 50, 50)
                    .addComponent(jButton1)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton2)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton3)
                    .addContainerGap(165, Short.MAX_VALUE))
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(51, 51, 51)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton2)
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3))
                    .addContainerGap(199, Short.MAX_VALUE))
            );
        }
    }
    
}
