package hu.meditations.markovrobot;

import java.awt.Graphics2D;

public interface Segment {

    public double getLength();
    public double project(Point position);
    public double distanceFromPoint(Point position);
    public void render(Graphics2D g);
    public void dump();
	
}
