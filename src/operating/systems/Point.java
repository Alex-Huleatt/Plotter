/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package operating.systems;

import java.text.DecimalFormat;

/**
 *
 * @author alexhuleatt
 */
public class Point {
    public double x; 
    public double y;
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    @Override
    public String toString() {
        DecimalFormat f = new DecimalFormat("0.##E0");
        String xs = f.format(x);
        String ys = f.format(y);
        return "(" + xs + "," + ys + ")";
        
    }
    
}
