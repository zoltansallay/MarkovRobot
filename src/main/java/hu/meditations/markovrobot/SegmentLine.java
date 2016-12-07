package hu.meditations.markovrobot;

import java.awt.Graphics2D;

public class SegmentLine implements Segment { 
	
    private Point start;
    private Point end;
    private double width = 5.0;

    public SegmentLine(String[] data) {
        start = new Point(Double.valueOf(data[1]), Double.valueOf(data[2])); 
        end = new Point(Double.valueOf(data[3]), Double.valueOf(data[4]));
        width = Double.valueOf(data[5]);
    }

    public SegmentLine(Point start, Point end) {
        this.start = start;
        this.end = end;
    }
	
    @Override
    public void dump() {
        System.out.printf("SegmentLine ");
        start.dump();
        System.out.printf(" ");
        end.dump();
        System.out.printf("\n");
    }

    @Override
    public void render(Graphics2D graphics) {
        Point v = end.add(start.minus()).scalar(width / 2 / getLength()).rotate(Math.PI / 2);
        Point s1 = start.add(v);
        Point s2 = start.add(v.minus());
        Point e1 = end.add(v);
        Point e2 = end.add(v.minus());
        int xPoints[] = { (int)s1.getx(), (int)s2.getx(), (int)e2.getx(), (int)e1.getx() };
        // !!! 500- in y
        int yPoints[] = { 500-(int)s1.gety(), 500-(int)s2.gety(), 500-(int)e2.gety(), 500-(int)e1.gety() };
        graphics.fillPolygon(xPoints, yPoints, 4);
        // !!! 500- and (-1)*width in y
        graphics.fillOval((int)(end.getx() - width / 2), 500-(int)(end.gety() + width / 2), (int)width, (int)width);
    }

    @Override
    public double getLength() {
        return start.distance(end); 
    }

    @Override
    public double project(Point pos) {
        return ( (end.getx() - start.getx()) * (pos.getx() - start.getx()) + (end.gety() - start.gety()) * (pos.gety() - start.gety()) ) / getLength(); 
    }

    @Override
    public double distanceFromPoint(Point pos) {
        double sd = start.distance(pos);
        double ed = end.distance(pos);
        double pr = project(pos);
        if (pr < 0) return sd;
        else if (pr > getLength()) return ed;
        else return Math.sqrt(sd * sd - pr * pr);
    }

}
