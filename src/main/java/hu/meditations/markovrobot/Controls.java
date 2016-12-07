package hu.meditations.markovrobot;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JPanel;

public class Controls extends JPanel {

    private ArrayList<ControlPoint> controlPoints = new ArrayList<>();
    private int sensorValue1 = 0;
    private int sensorValue2 = 0;
    
    private final Color colActual = Color.red;
    private int voroX = 20;
    private int voroY = 0;
    private int canvasX = 100;
    private int canvasY = 100;
    private int zoom = 5;
    private Voronoi voronoi = new Voronoi(canvasX, canvasY, zoom);

    public Controls(Controls controlsToClone) {
        for (ControlPoint cp : controlsToClone.controlPoints) {
            try {
                this.controlPoints.add(new ControlPoint(cp.sensorValue1, cp.sensorValue2, cp.v1, cp.v2));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        initControls();
    }
    
    public Controls(String filename) {
        File file = new File(filename);
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(in);
        while(scanner.hasNext()) {
            String line = scanner.nextLine();
            try {
                controlPoints.add(new ControlPoint(line.split("\\s+")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        scanner.close();
        try {
            check();
        } catch (Exception e) {
            e.printStackTrace();
        }
        initControls();
    }

    private void initControls() {
        this.setPreferredSize(new Dimension(canvasX * zoom + voroX, canvasY * zoom + voroY));
        voronoi.calcVoronoi(getNumberOfControlPoints(), getControlPointsS1(), getControlPointsS2());
    }
    
    public void setSensorValues(int s1, int s2) {
        this.sensorValue1 = s1;
        this.sensorValue2 = s2;
    }
    
    private void check() throws Exception {
        if (controlPoints.isEmpty()) {
            throw new Exception("At least one controlpoint is needed!");
        }
    }

    public Point getVelocityFromClosestControlPoint() {
        double dmin = Double.MAX_VALUE;
        ControlPoint cpClosest = null;

        for (ControlPoint cp : controlPoints) {
            double d = (cp.sensorValue1 - sensorValue1) * (cp.sensorValue1 - sensorValue1) + (cp.sensorValue2 - sensorValue2) * (cp.sensorValue2 - sensorValue2);
            if (d < dmin) {
                dmin = d;
                cpClosest = cp;
            }
        }
        return new Point(cpClosest.v1, cpClosest.v2);
    }

    public int getNumberOfControlPoints() {
        return controlPoints.size();
    }

    public double[] getControlPointsS1() {
        double[] x = new double[controlPoints.size()];
        int i = 0;
        for (ControlPoint cp : controlPoints) {
            x[i] = cp.sensorValue1;
            i++;
        }
        return x;
    }

    public double[] getControlPointsS2() {
        double[] x = new double[controlPoints.size()];
        int i = 0;
        for (ControlPoint cp : controlPoints) {
            x[i] = cp.sensorValue2;
            i++;
        }
        return x;
    }

    public void mutate() {
        int s;
        double v;
        for (ControlPoint cp : controlPoints) {
            s = Math.max(0, cp.sensorValue1 + (int)(Math.round(Math.random() * 4 - 2)));
            cp.sensorValue1 = Math.min(s, 100);
            s = Math.max(0, cp.sensorValue2 + (int)(Math.round(Math.random() * 4 - 2)));
            cp.sensorValue2 = Math.min(s, 100);
            v = Math.max(-100.0, cp.v1 + (Math.random() * 6.0 - 3.0));
            cp.v1 = Math.min(v, 100.0);
            v = Math.max(-100.0, cp.v2 + (Math.random() * 6.0 - 3.0));
            cp.v2 = Math.min(v, 100.0);
        }
        voronoi.calcVoronoi(getNumberOfControlPoints(), getControlPointsS1(), getControlPointsS2());
    }

    public void dump() {
        System.out.printf("   s1: %1$d s2: %2$d", sensorValue1, sensorValue2);
    }

    @Override
    protected void paintComponent(java.awt.Graphics g) {
        //voronoi.paintComponent(g, voroX, voroY);
        g.drawImage(voronoi.image, voroX, voroY, this);
        g.setColor(colActual);
        g.fillOval((sensorValue1 - 1) * zoom + voroX, (sensorValue2 - 1) * zoom + voroY, 2 * zoom, 2 * zoom);
    }

    private class ControlPoint {

        public int sensorValue1;
        public int sensorValue2;
        public double v1;       // must be between -100 and 100
        public double v2;       // must be between -100 and 100

        public ControlPoint(int sensorValue1, int sensorValue2, double v1, double v2) throws Exception {
            this.sensorValue1 = sensorValue1;
            this.sensorValue2 = sensorValue2;
            this.v1 = v1;
            this.v2 = v2;
            check();
        }

        public ControlPoint(String[] data) throws Exception {
            this.sensorValue1 = Integer.valueOf(data[0]);
            this.sensorValue2 = Integer.valueOf(data[1]);
            this.v1 = Double.valueOf(data[2]);
            this.v2 = Double.valueOf(data[3]);
            check();
        }

        private void check() throws Exception {
            if ((Math.abs(v1) > 100.0) || (Math.abs(v2) > 100.0))
                throw(new Exception("Velocity must be between -100 and 100"));
        }

    }
}
