/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Display2;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import javax.imageio.ImageIO;
import operating.systems.Point;

/**
 *
 * @author alexhuleatt
 */
public class Panel extends javax.swing.JPanel {

    private ArrayList<Point[]> data; //data files
    public static int RECT_SIZE = 4; //size of point
    public static Color[] colors = new Color[]{Color.MAGENTA, Color.GREEN, Color.ORANGE, Color.RED, Color.PINK, Color.CYAN, Color.BLUE}; //color constants
    private Point draw_info; //Point the mouse is hovering over
    private boolean display_legend; //boolean to determine if legend should be displayed

    private int scroll_level; //integer representing the zoom level
    private ArrayList<String> data_names; //Names of all data files
    private ArrayList<Boolean> display_labels; //Boolean representing if the point info for this data should be displayed
    private ArrayList<Boolean> display_data; //Boolean determining if this data file should be displayed

    /**
     * Creates new form panel
     */
    public Panel() {
        initComponents(); //auto-generated, initialize stuff
        data = new ArrayList<>();
        data_names = new ArrayList<>();
        scroll_level = 0;
        display_labels = new ArrayList<>();
        display_data = new ArrayList<>();
        addMouseWheelListener(new MouseWheelListener() {

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                scroll_level += e.getWheelRotation(); //modify zoom level on scroll
                repaint();
            }

        });

        //Mouse listener allows the user to get information on a point by hovering over it
        addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = new Point(e.getX(), e.getY()); //Point from the mouse
                Point minDraw = null; //normalize point closest to normalize mouse point
                Point minPoint = null; //Point closest to mouse
                String minName = "";
                double minDis = 0; //minimum distance found between point and mouse
                boolean okay = false;
                for (int i = 0; i < data.size(); i++) {
                    if (display_data.get(i)) {
                        Point[] arr = data.get(i);
                        for (int j = 0; j < arr.length; j++) {
                            Point p2 = arr[j];
                            Point temp_draw = drawPoint(p2);
                            double dis = euclid(p, temp_draw);
                            if (dis < 10 && (minDraw == null || dis < minDis)) {
                                minDraw = temp_draw;
                                minPoint = p2;
                                minDis = dis;
                                minName = data_names.get(i);
                                okay = true;
                            }
                        }
                    }
                }
                if (okay) { //only display if the closest point is under a threshold
                    draw_info = minPoint;
                } else {
                    draw_info = null;
                }
                repaint();
            }

        });
    }

    /**
     * Toggle whether the legend should be displayed or not.
     */
    public void toggle_legend() {
        display_legend = !display_legend;
    }

    /**
     * Toggle whether point information for the inputted data number should be
     * displayed
     *
     * @param a
     */
    public void toggle_label(int a) {
        if (a < display_labels.size()) {
            display_labels.set(a, !display_labels.get(a));
        }
    }

    /**
     * Toggle whether the inputted data should be displayed or not
     *
     * @param a
     */
    public void toggle_data(int a) {
        if (a < display_data.size()) {
            display_data.set(a, !display_data.get(a));
        }
    }

    /**
     *
     * @param p1
     * @param p2
     * @return Euclidean distance between p1 and p2
     */
    private double euclid(Point p1, Point p2) {
        int xDif = (int) (p2.x - p1.x);
        int yDif = (int) (p2.y - p1.y);
        return Math.sqrt(xDif * xDif + yDif * yDif);
    }

    /**
     * Gives a data file to the panel.
     *
     * @param points
     * @param set_name
     * @return
     */
    public String gibPoints(Point[] points, String set_name) {
        data.add(points);
        display_labels.add(false);
        data_names.add(set_name);
        display_data.add(true);
        return colors[data.size()].toString();
    }

    /**
     * Returns the maximum x value that this panel will display
     *
     * @return
     */
    private double maxX() {
        double max = 0;
        ArrayList<Double> xs = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            if (display_data.get(i)) {
                Point[] a = data.get(i);
                for (Point p : a) {
                    if (!xs.contains(p.x)) {
                        xs.add(p.x);
                    }
                }
            }
        }
        Collections.sort(xs);
        int level = 1 + scroll_level;
        if (level >= xs.size()) {
            level = xs.size() - 1;
            scroll_level = xs.size() - 1;
        }
        if (level <= 0) {
            scroll_level = 0;
            level = 1;
        }
        return xs.get(xs.size() - (level));
    }

    /**
     * Returns the maximum y-value that this panel will display
     *
     * @return
     */
    private double maxY() {
        double max = 0;
        double maxX = maxX();
        for (int i = 0; i < data.size(); i++) {
            if (display_data.get(i)) {
                Point[] a = data.get(i);
                for (Point p : a) {
                    if (p.x <= maxX) {
                        max = Math.max(p.y, max);
                    }
                }
            }
        }
        return max;
    }

    /**
     * Returns the minimum x value that this will display
     *
     */
    private int minX() {
        return (int) data.get(0)[0].x;
    }

    /**
     * 
     * @return minimum y-value this panel will display
     */
    private double minY() {
        double min = Double.POSITIVE_INFINITY;
        for (Point[] a : data) {
            for (Point p : a) {
                min = Math.min(p.y, min);
            }
        }
        return min;
    }

    /**
     * 
     * @return normalization value for x
     */
    private double xMul() {
        return ((double) getWidth() / maxX());
    }

    /**
     * 
     * @return normalization factor for y
     */
    private double yMul() {
        return ((double) maxY() - minY()) / (double) getHeight();
    } 

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());

        if (data == null || data.isEmpty()) {
            return;
        }

        for (int i = 0; i < data.size(); i++) {
            if (display_data.get(i)) {
                Point[] c_data = data.get(i);
                g.setColor(colors[i]);
                for (int j = 0; j < c_data.length; j++) {
                    Point p = drawPoint(c_data[j]);
                    g.fillRect((int) p.x, (int) p.y, RECT_SIZE, RECT_SIZE);
                    if (j > 0) {
                        Point p2 = drawPoint(c_data[j - 1]);
                        g.drawLine((int) p.x + RECT_SIZE / 2, (int) p.y + RECT_SIZE / 2, (int) p2.x + RECT_SIZE / 2, (int) p2.y + RECT_SIZE / 2);
                    }
                    if (display_labels.get(i)) {
                        display_label(c_data[j], g, colors[i]);
                        g.setColor(colors[i]);
                    }
                }
            }
        }
        display_label(draw_info, g, new Color(0, 0, 0));

        if (display_legend) {
            FontMetrics fontMetrics = g.getFontMetrics();
            int height = fontMetrics.getHeight();
            int i = 0;
            for (String s : data_names) {
                if (display_data.get(i)) {
                    g.setColor(Color.WHITE);
                    int y = height * (i + 1);
                    g.drawString(s, 10, y);
                    int width = fontMetrics.stringWidth(s);
                    width += 20;
                    g.setColor(colors[i]);
                    g.fillRect(width, y - 10, 10, 10);
                }
                i++;
            }
        }
    }

    private Color avg_black(Color c) {
        return new Color((255 + c.getRed()) / 2, (255 + c.getGreen()) / 2, (255 + c.getBlue()) / 2);
    }

    private void display_label(Point p, Graphics g, Color c) {
        g.setColor(avg_black(c));
        if (p != null) {
            Point d = drawPoint(p);
            int dx, dy;
            if (d.x > getWidth() / 2) {
                dx = -1;
            } else {
                dx = 1;
            }
            if (d.y < getHeight() / 2) {
                dy = -1;
            } else {
                dy = 1;
            }
            String str = p.toString();
            FontMetrics fontMetrics = g.getFontMetrics();
            int width = fontMetrics.stringWidth(str);
            int height = fontMetrics.getHeight();

            if (dx == -1) {
                d.x -= width + 10;
            } else {
                d.x += 10;
            }
            if (d.y < 0) {
                d.y = height;
            }
            if (d.y > getHeight()) {
                d.y = getHeight() - height;
            }
            g.drawString(str, (int) d.x, (int) d.y);
        }
    }

    public Point drawPoint(Point p) {
        int x = (int) (p.x * xMul()) - RECT_SIZE / 2;
        int y = (int) ((getHeight() + minY() / yMul()) - (p.y / yMul())) - RECT_SIZE / 2;
        return new Point(x, y);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 800, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
