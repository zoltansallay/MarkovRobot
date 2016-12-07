package hu.meditations.markovrobot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Voronoi {
    private static final long serialVersionUID = 1L;

    public BufferedImage image;
    private final Color colBack = Color.black;
    private final Color colPoints = Color.orange;
    private final Color colLines = Color.orange;
    private int canvasX = 100;
    private int canvasY = 100;
    private int zoom = 1;
//    private int N;
    private int i, j, k, l;
    private double di, ys, t;
    private double x0, y0, xx, yy, xa1 = 0, ya1 = 0, yy2;
    private double di3, ys3, t2, ds, us;
    private double y20, y21, sa0, sa1, sa2, sa3, sa4, sa5;
    private int sa6, br, br2, u, k2;
    private double kx[] = new double[100];
    private double ky[] = new double[100];
    private double kz[] = new double[100];
//    private double x1[] = new double[100];
//    private double y1[] = new double[100];
    private int x[] = new int[100];
    private int y[] = new int[100];
    private double s[] = new double[100];

    public Voronoi(int canvasX, int canvasY, int zoom) {
        this.canvasX = canvasX;
        this.canvasY = canvasY;
        this.zoom = zoom;
//        this.setPreferredSize(new java.awt.Dimension(canvasX * zoom, canvasY * zoom));
        image = new BufferedImage(canvasX * zoom, canvasY * zoom, BufferedImage. TYPE_INT_RGB);
    }
  
  // order by such that te1[0] < te1[1] < te1[2]...
    private void heapv(double te1[], double te2[], double te3[], int NN) {
        int kk, kks, ii, jj, mm;
        double b1, b2, b3, c1, c2, c3;

        kks = (int)(NN/2);
        for (kk = kks; kk >= 1; kk--) {
            ii = kk;
            b1 = te1[ii-1]; b2=te2[ii-1]; b3=te3[ii-1];
            while (2*ii <= NN) {
                jj = 2*ii;
                if (jj+1 <= NN) {
                    if (te1[jj-1]<te1[jj]) {
                        jj++;
                    }
                }
                if (te1[jj-1]<=b1) {
                    break;
                }
                te1[ii-1] = te1[jj-1]; te2[ii-1] = te2[jj-1]; te3[ii-1] = te3[jj-1];
                ii = jj;
            }
            te1[ii-1] = b1; te2[ii-1] = b2; te3[ii-1] = b3;
        }
        for (mm = NN-1; mm >= 1; mm--) {
            c1 = te1[mm]; c2 = te2[mm]; c3 = te3[mm];
            te1[mm] = te1[0]; te2[mm] = te2[0]; te3[mm] = te3[0];
            ii = 1;
            while(2*ii <= mm) {
                kk = 2*ii;
                if (kk+1 <= mm) {
                    if (te1[kk-1] <= te1[kk]) {
                        kk++;
                    }
                }
                if (te1[kk-1] <= c1) {
                    break;
                }
                te1[ii-1] = te1[kk-1]; te2[ii-1] = te2[kk-1]; te3[ii-1] = te3[kk-1];
                ii = kk;
            }
            te1[ii-1] = c1; te2[ii-1] = c2; te3[ii-1] = c3;
        }
    }

    public void calcVoronoi(int N, double[] x1, double[] y1) {
        Graphics2D g = (Graphics2D)image.getGraphics();
        g.setColor(colBack);
        g.fillRect(0 * zoom, 0 * zoom, canvasX * zoom, canvasY * zoom);
        g.setColor(colPoints);
 
        for (k = 0; k < N; k++) {
            //x1[k] = Math.random() * (canvasX - 30) + 15;
            //y1[k] = Math.random() * (canvasY - 30) + 15;
            x[k] = (int)(x1[k] + 0.5);
            y[k] = (int)(y1[k] + 0.5);
            s[k] = Math.sqrt(x1[k] * x1[k] + y1[k] * y1[k]);
            g.fillOval((x[k] - 1) * zoom, (y[k] - 1) * zoom, 2 * zoom, 2 * zoom);
        }
        g.setColor(colLines);
        for (i = 0; i <= N - 2; i++) {
            for (j = i + 1; j <= N - 1; j++) {
                br = 0;
                t = Math.pow(x1[i] - x1[j], 2) + Math.pow(y1[i] - y1[j], 2); // the distance between i and j
                di = -1 / ( (y1[i] - y1[j]) / (x1[i] - x1[j]) ); // the slope of bisector(perpendicular at midpoint) between i and j
                ys = (y1[i] + y1[j]) / 2 - (x1[i] + x1[j]) / 2 * di; // intercept of the bisector(perpendicular at midpoint) between i and j
                // Now, y = di * x + ys is the bisector(perpendicular at midpoint) between i and j.
                // So we can obtain region V(a_i) = {x|d(x,a_i)<=d(x,a_j) for j not i}, in this case, j is fixed.
                // Voronoi equation is V(a_i) = {x|d(x,a_i)<=d(x,a_j) for all j not i}, in this case j is not fixed, for all j not i.
                // So, we must check line segments(group of points) on this bisector is nearer than any other j(not fixed, this j becomes k and u at later for-loop) 
                // we do this check loop- for (u=1;u<=N;u++)  u<>i, u<>j
                // Now, what are line segments(group of points)?
                // We want compare with any other j, So line segments are obtained from intersection between this bisector and the bisector of i and k(k is not i,j).

                // if intercept>0 and <(the height of the screen) then the start point of the bisector is (0,ys)
                if (ys > 0 && ys < canvasY) {
                    x0 = 0; y0 = ys;
                }
                else { // ys <0 or ys > the height of the screen
                    if (di > 0) { // if slope > 0 then the start point is (-ys/di, 0)
                        x0 = -ys / di; y0 = 0;
                    }
                    else{ // if slope < 0 then the start point is ((canvasY-ys)/di, canvasY)
                        x0 = (canvasY - ys) / di;
                        y0 = canvasY;
                    }
                }
                yy = di * canvasX + ys; // yy is the y-coordinate at x= the width of the screen
                if (yy > 0 && yy < canvasY) { // if yy>0 and yy<the hight of the screen then the end point of the bisector(perpendicular at midpoint) between i and j is (width of the screen,yy)
                    xa1 = canvasX; ya1 = yy;
                }
                else{ // yy<0 or yy> the hight of the screen
                    if (di > 0) { // slope>0
                        xa1 = (canvasY - ys) / di; ya1 = canvasY;
                    }
                    else { //slope<0
                        xa1 = -ys / di;
                        ya1 = 0;
                    }
                }

                // calculate the intersection of the two bisectors (i,j) and (i,k)
                // first intersection is the start point of bis(i,j)
                l = 0;
                kx[l] = x0; ky[l] = y0; // start point of bis(i,j)
                sa2 = x1[j] - x1[i]; // difference of x between i and j
                sa4 = y1[j] - y1[i]; // difference of y between i and j

                // calculate the intersection of the two bisectors (i,j) and (i,k), k is not i,j
                for (k = 0; k <= N - 1; k++) {
                    if (k != i && k != j) {
                        t2 = Math.pow(x1[i] - x1[k], 2) + Math.pow(y1[i] - y1[k], 2); // the distance between i and k

                        di3 = -1 / ( (y1[i] - y1[k]) / (x1[i] - x1[k]) ); // slope of bis(i,k)
                        ys3 = (y1[i] + y1[k]) / 2 - (x1[i] + x1[k]) / 2 * di3; // intercept of bis(i,k)
                        // Now, y = di3 * x +ys3 is bis(i,k)
                        y20 = di3 * x0 + ys3; // y-coordinate of the start(this start point is the start point(x-coordinate) of bis(i,j)) point of bis(i,k)
                        y21 = di3 * xa1 + ys3; // x-coordinate of the start(this start point is the start point(x-coordinate) of bis(i,j)) point of bis(i,k)
                        sa0 = y0 - y20; // the difference of the two y-coordinates at the start point
                        sa1 = ya1 - y21; // the difference of the two y-coordinates at the end point
                        sa3 = x1[k] - x1[i]; // the difference of x-coordinates between i and k
                        sa5 = y1[k] - y1[i]; // the difference of y-coordinates between i and k
                        if (sa2 * sa3 > 0 && sa4 * sa5 > 0) { // From the view point of the i(set the point of i to the point of origin, that is, x_i=0, y_i=0), x_j*y_j>0 and x_k*y_k>0 then 
                            sa6 = 1; //sa6 is a flag
                        } else {
                            sa6 = 0;
                        }
                        if (sa0 * sa1 > 0 && t > t2 && sa6 == 1) { // From the view of i, if j and k is same direction(the signs of x and y are same), bis(i,j) and bis(i,k) do not cross inside the screen, and k is closer to i than j then 
                            br = 1; // then the bisector of (i,j) doesn't appear inside the screen  -> break
                            break;
                        }
                        if (sa0 * sa1 < 0 || t < t2 || sa6 != 0 ) { // case else
                            if (sa0 * sa1 < 0 || t > t2) { // there is the intersection inside the screen and j is closer to i than k
                                l++; // calculate the intersection
                                kx[l] = (ys3 - ys) / (di - di3);
                                ky[l] = di * kx[l] + ys;
                                //Now (kx,ky) is intersection between bis(i,j) and bis(i,k)
                            }	
                        }	
                    } //if (k!=i && k!=j)
                } //next k
                if (br == 0) {
                    l++; // set the end point as the intersection
                    kx[l] = xa1;
                    ky[l] = ya1;
                    for (u = 0; u <= l; u++) {// kz are dummy variables they have no mean
                        kz[u] = 0;
                    }
                    // Now, on bis(i,j), y=di*x+ys, there are l intersections.
                    // Sort these (kx,ky)
                    heapv(kx, ky, kz, l); // order (kx,ky) such that kx[0]<kx[1]<....<kx[l-1]
                    for (k = 0; k <= l - 1; k++) {//  consider the intervals between two intersections
                        k2 = k + 1; // k is the start point of the interval, k2 is the end point of the interval
                        xx = (kx[k] + kx[k2]) / 2; // x-coordinate of the midpoint of the two intersections
                        yy2 = di * xx + ys; // y-coordinate of the midpoint of the two intersections
                        ds = Math.pow(xx - x1[i], 2) + Math.pow(yy2 - y1[i], 2); // distance between the midpoint and i,  it is the same of the distance between the midpoint and j
                        // if this distance, ds is shorter than any other distance to u(u is not i,j), this line segment OKs
                        // Voronoi equation: V(a_i) = {x|d(x,a_i)<=d(x,a_j) for all j not i}, in this case j is not fixed, for all j not i.
                        br2 = 0; //if this distance, ds is shorter than any other distance to u, br2 keeps 0.
                        for (u = 0; u <= N - 1; u++) { //u<>i, u<>j
                            if (u != i && u != j) {
                                us = Math.pow( xx - x1[u], 2) + Math.pow(yy2 - y1[u], 2); // the distance between the midpoint and u
                                if (us < ds) { //if the distance to u is smaller than the distance to j(i) then the bisector should not be drawn -> break
                                    br2 = 1;
                                    break;
                                }
                            }
                        }
                        if (br2 == 0) { // if flag=0 then draw the interval
                            g.drawLine( (int)(kx[k ] + 0.5) * zoom, (int)(ky[k ] + 0.5) * zoom, 
                                        (int)(kx[k2] + 0.5) * zoom, (int)(ky[k2] + 0.5) * zoom);
                            break; // the bisector should be draw just once -> break
                        }//if br2==0
                    } //next k
                } //if br==0
            } //next j
        } //next i
    }

}