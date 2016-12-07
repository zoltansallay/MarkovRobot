package hu.meditations.markovrobot;

public class Point {

    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getx() {
        return x;
    }

    public double gety() {
        return y;
    }

    public Point rotate(double phi) {
        return new Point(x * Math.cos(phi) - y * Math.sin(phi), x * Math.sin(phi) + y * Math.cos(phi));
    }

    public Point add(Point delta) {
        return new Point(x + delta.getx(), y + delta.gety());
    }

    public Point minus() {
        return new Point(-x , -y);
    }

    public Point scalar(double alpha) {
        return new Point(x * alpha, y * alpha);
    }

    public double distance(Point p) {
        return Math.sqrt( (p.getx() - x) * (p.getx() - x) + (p.gety() - y) * (p.gety() - y) ); 
    }

    public double distance2(Point p) {
        return (p.getx() - x) * (p.getx() - x) + (p.gety() - y) * (p.gety() - y); 
    }

    public void dump() {
        System.out.printf("%1$.2f %2$.2f", x, y);
    }

}
