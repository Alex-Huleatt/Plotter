/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Display2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import operating.systems.Point;

/**
 *
 * @author alexhuleatt
 */
public class Displayer {

    public static void main(String[] args) {
        String path = Displayer.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String decodedPath = ".";
//        try {
//            decodedPath = URLDecoder.decode(path, "UTF-8");
//            decodedPath = decodedPath.substring(0, decodedPath.lastIndexOf("/") + 1);
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(Displayer.class.getName()).log(Level.SEVERE, null, ex);
//        }
        File f = new File(decodedPath);
        File[] files = f.listFiles();
        Frame fr = new Frame();
        for (File file : files) {
            String fname = file.getName();
            System.out.println(fname + " " + fname.matches(".*\\.csv"));
            if (fname.matches(".*\\.csv")) {
                System.out.println("Scanning: " + fname);
                String name = fname.substring(0, fname.indexOf("."));
                try {
                    fr.gibPoints(readFile(file), name);
                } catch (FileNotFoundException e) {
                    System.out.println(e);
                    System.out.println("Bad Data in " + fname);
                }
            }
        }
        fr.setVisible(true);
    }

    private static Point[] readFile(File f) throws FileNotFoundException {
        Scanner sc = new Scanner(f);
        ArrayList<Point> arr = new ArrayList<>();
        while (sc.hasNextLine()) {
            String[] s = sc.nextLine().split(",");
            double x = Double.parseDouble(s[0]);
            double y = Double.parseDouble(s[1]);
            arr.add(new Point(x,y));
        }
        return arr.toArray(new Point[arr.size()]);
    }

}
