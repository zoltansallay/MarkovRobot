package hu.meditations.markovrobot;

public class Car {
    /* public constants and dimensions */
    public static final double AXIS = 10.0;
    public static final double SENSOR_R = 5.0;
    public static final Point REL_SENSOR_POSITION_1 = new Point(0, 5);	 // right
    public static final Point REL_SENSOR_POSITION_2 = new Point(0, -5); // left
    public static final Point B0 = new Point(5, 0);	// nose
    public static final Point B1 = new Point(-15, 5);	// right back
    public static final Point B2 = new Point(-15, -5);	// left back
    public static final Point B3 = new Point(5, -5);	// left front
    public static final Point B4 = new Point(5, 5);	// right front

    private static final long serialVersionUID = 1L;
    
    private Controls controls;
    private Point position;
    private Point velocity;
    private double phi;

    public Car() {
    }

    public void setControls(Controls controls) {
        this.controls = controls;
    }

    public Point getPosition() {
        return position;
    }

    public void setPositionAndPhi(Point position, double phi) {
        this.position = position;
        this.phi = phi;
    }
    
    public Point getAbsolutePositionOfRelative(Point point) {
        return position.add(point.rotate(phi));
    }

    public void step(double deltaT) {
        double v1 = velocity.getx();
        double v2 = velocity.gety();
        Point delta = new Point((v1 + v2) / 2 * Math.cos(phi) * deltaT, (v1 + v2) / 2 * Math.sin(phi) * deltaT);
        setPositionAndPhi(position.add(delta), phi + (v2 - v1) / AXIS * deltaT);
        //dump();
    }

    public void setVelocityBasedOnSensorValues(int sensorValue1, int sensorValue2) {
        controls.setSensorValues(sensorValue1, sensorValue2);
        velocity = controls.getVelocityFromClosestControlPoint();
    }
 
    public void dump() {
        System.out.printf("car ");
        position.dump();
        controls.dump();
        System.out.printf("   v1: %1$f v2: %2$f", velocity.getx(), velocity.gety());
        System.out.printf("\n");
    }

}
