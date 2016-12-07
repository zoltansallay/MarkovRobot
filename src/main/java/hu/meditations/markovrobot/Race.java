/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.meditations.markovrobot;

import java.awt.Color;
import javax.swing.JPanel;

/**
 *
 * @author zoz
 */
public class Race extends JPanel {
    
    private final Car car = new Car();
    private Path path;
    private boolean onPath;
    private double success;
    
    public Race(Controls controls, Path path) {
        this.path = path;
        this.setPreferredSize(new java.awt.Dimension(Path.CANVASWIDTH, Path.CANVASHEIGTH));
        car.setControls(controls);
        reset();
    }
    
    public final void reset() {
        car.setPositionAndPhi(path.getStartPosition(), path.getStartPhi());
        onPath = false;
        success = 0.0;
    }

    public void setControls(Controls controls) {
        car.setControls(controls);
        reset();
    }
    
    public void setPath(Path path) {
        this.path = path;
        reset();
    }

    public double getSuccess() {
        return success;
    }
    
    public void step(double deltaT) {
        int sensorValue1 = path.scan(car.getAbsolutePositionOfRelative(Car.REL_SENSOR_POSITION_1), Car.SENSOR_R);
        int sensorValue2 = path.scan(car.getAbsolutePositionOfRelative(Car.REL_SENSOR_POSITION_2), Car.SENSOR_R);
        onPath = (sensorValue1 + sensorValue2 > 0);
        adjustSuccess();
        car.setVelocityBasedOnSensorValues(sensorValue1, sensorValue2);
        car.step(deltaT);
    }
    
    public void run(double deltaT, double maxTime) {
        double time = 0.0;
        while (time < maxTime) {
            step(deltaT);
            time = time + deltaT;
        }
    }

    private void adjustSuccess() {
        if (onPath) {
            double suc = path.roadTaken(car.getPosition());
            success = Math.max(suc, success);
        }
    }
/*      if (path.outOfBox(car.getPosition())) {
            isRunning = true;
       }
*/
    
    @Override
    protected void paintComponent(java.awt.Graphics g) {
        // paint background, border
        g.drawImage(path.getImage(), 0, 0, this);
        // paint car
        Point br1 = car.getAbsolutePositionOfRelative(Car.B1);
        Point br2 = car.getAbsolutePositionOfRelative(Car.B2);
        Point br3 = car.getAbsolutePositionOfRelative(Car.B3);
        Point br4 = car.getAbsolutePositionOfRelative(Car.B4);
        int xPoints[] = { (int)br1.getx(), (int)br2.getx(), (int)br3.getx(), (int)br4.getx() };
        int yPoints[] = { 500-(int)br1.gety(), 500-(int)br2.gety(), 500-(int)br3.gety(), 500-(int)br4.gety() }; // !!! 500- in y
        g.setColor(Color.blue);
        g.fillPolygon(xPoints, yPoints, 4);
        // paint yellow arrowhead
        Point br0 = car.getAbsolutePositionOfRelative(Car.B0);
        Point s1 = car.getAbsolutePositionOfRelative(Car.REL_SENSOR_POSITION_1);
        Point s2 = car.getAbsolutePositionOfRelative(Car.REL_SENSOR_POSITION_2);
        xPoints = new int[] { (int)br0.getx(), (int)s1.getx(), (int)s2.getx() };
        yPoints = new int[] { 500-(int)br0.gety(), 500-(int)s1.gety(), 500-(int)s2.gety() }; // !!! 500- in y
        g.setColor(Color.orange);
        g.fillPolygon(xPoints, yPoints, 3);
    }

}
