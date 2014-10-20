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

    private ArrayList<Point[]> data;
    public static int RECT_SIZE = 4;
    public static Color[] colors = new Color[]{Color.MAGENTA, Color.GREEN, Color.ORANGE, Color.RED, Color.PINK, Color.CYAN, Color.BLUE};
    private Point draw_info;
    private String draw_name;
    private boolean display_legend;

    private int scroll_level;
    private ArrayList<String> data_names;
    private ArrayList<Boolean> display_labels;
    private ArrayList<Boolean> display_data;

    /**
     * Creates new form panel
     */
    public Panel() {
        initComponents();
        data = new ArrayList<>();
        data_names = new ArrayList<>();
        scroll_level = 0;
        display_labels = new ArrayList<>();
        display_data = new ArrayList<>();
        addMouseWheelListener(new MouseWheelListener() {

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                scroll_level += e.getWheelRotation();
                repaint();
            }

        });

        addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = new Point(e.getX(), e.getY());
                Point minDraw = null;
                Point minPoint = null;
                String minName = "";
                double minDis = 0;
                boolean okay = false;
                for (int i = 0; i < data.size(); i++) {
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
                if (okay) {
                    draw_info = minPoint;
                    draw_name = minName;
                } else {
                    draw_info = null;
                }
                repaint();
            }

        });
    }

    public void toggle_legend() {
        display_legend = !display_legend;
    }

    public void toggle_label(int a) {
        if (a < display_labels.size()) {
            display_labels.set(a, !display_labels.get(a));
        }
    }

    public void toggle_data(int a) {
        if (a < display_data.size()) {
            display_data.set(a, !display_data.get(a));
        }
    }

    private double euclid(Point p1, Point p2) {
        int xDif = (int) (p2.x - p1.x);
        int yDif = (int) (p2.y - p1.y);
        return Math.sqrt(xDif * xDif + yDif * yDif);
    }

    public String gibPoints(Point[] points, String set_name) {
        data.add(points);
        display_labels.add(false);
        data_names.add(set_name);
        display_data.add(true);
        return colors[data.size()].toString();
    }

    private double maxX() {
        double max = 0;
        ArrayList<Double> xs = new ArrayList<>();
        for (Point[] a : data) {
            for (Point p : a) {
                if (!xs.contains(p.x)) {
                    xs.add(p.x);
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

    private double maxY() {
        double max = 0;
        double maxX = maxX();
        for (Point[] a : data) {
            for (Point p : a) {
                if (p.x <= maxX) {
                    max = Math.max(p.y, max);
                }
            }
        }
        return max;
    }

    private int minX() {
        return (int) data.get(0)[0].x;
    }

    private double minY() {
        double min = Double.POSITIVE_INFINITY;
        for (Point[] a : data) {
            for (Point p : a) {
                min = Math.min(p.y, min);
            }
        }
        return min;
    }

    private double xMul() {
        return ((double) getWidth() / maxX());
    }

    private double yMul() {
        return ((double) maxY() - minY()) / (double) getHeight();
    }

    private int diff() {
        return 1;
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
