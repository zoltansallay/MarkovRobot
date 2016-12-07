package hu.meditations.markovrobot;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Scanner;

public class Path {
    private Point startPosition = new Point (100, 100);
    private double startPhi = 0.0;
    private ArrayList<Segment> segments = new ArrayList<Segment>();
    private double totalLength = 0.0;

    private BufferedImage image;
    public static final int CANVASWIDTH = 1000;
    public static final int CANVASHEIGTH = 500;

    public Path(String filename) {
        java.io.File file = new java.io.File(filename);
        java.io.FileInputStream in = null;
        try {
            in = new java.io.FileInputStream(file);
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(in);
        while(scanner.hasNext()) {
            String line = scanner.nextLine();
            String s[] = line.split("\\s+");
            if (s[0].contentEquals("Start")) {
                startPosition = new Point(Double.valueOf(s[1]), Double.valueOf(s[2]));
                startPhi = Double.valueOf(s[3]);
            } else {
                try {
                    Segment segment = SegmentFactory.createSegment(line.split("\\s+"));
                    segments.add(segment);
                    totalLength += segment.getLength(); 
                    //segment.dump();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        scanner.close();
        createCanvas();
    }

    private void createCanvas() {
        image = new java.awt.image.BufferedImage(CANVASWIDTH, CANVASHEIGTH, java.awt.image.BufferedImage. TYPE_INT_RGB);
        java.awt.Graphics2D im = (java.awt.Graphics2D)image.getGraphics();
        im.fillRect(0, 0, CANVASWIDTH, CANVASHEIGTH);
        im.setColor(java.awt.Color.black);
        for(Segment segment : segments) {
            segment.render(im);
        }
        try {
            javax.imageio.ImageIO.write(image,"jpg",new java.io.File("data/canvas.jpg"));
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public Point getStartPosition() {
        return startPosition;
    }

    public double getStartPhi() {
        return startPhi;
    }

    public BufferedImage getImage() {
        return image;
    }
    
    public double roadTaken(Point position) {
        Segment closestSegment = null;
        double taken = 0.0;
        double min = Double.MAX_VALUE; 

        for(Segment segment : segments) {
            double dist = segment.distanceFromPoint(position);
            if (dist < min) {
                closestSegment = segment;
                min = dist;
            }
        }
        for(Segment segment : segments) {
            if (segment == closestSegment) {
                taken += Math.max(0, segment.project(position));
                break;
            }
            taken += segment.getLength();
        }
        return (taken / totalLength);
    }

    public int scan(Point position, double r) {
        double x = position.getx();
        // !!! 500- in y
        double y = 500-position.gety();
        int all = 0;
        int sum = 0;

        for (int i = (int)(x - r - 1); i < (int)(x + r + 1); i++) {
            for (int j = (int)(y - r - 1); j < (int)(y + r + 1); j++) {
                if (((double)i - x) * ((double)i - x) + ((double)j - y) * ((double)j - y) < r * r) {
                    java.awt.Color c = new java.awt.Color(image.getRGB(i, j));
                    all++;
                    if (c.getRed() == 0) {
                        sum++;
                    }
                }
            }
        }
        return (int)((double)sum / (double)all * 100.0);
    }

    public void mutate() {

    }


}
