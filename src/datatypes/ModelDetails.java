/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datatypes;


import javax.swing.*;
import javax.vecmath.Point2d;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;


/**
 * Stores the model details. This file needs to be updated if the data being created
 * has either been changed or a new level has been added.
 *
 * @author vaisagh
 */
public class ModelDetails {


    private String title;
    private int scale;
    private int xSize;
    private int ySize;

    private List<DestinationPattern> patterns;
    private double minSpeed;
    private double maxSpeed;
    private double meanSpeed;
    private double sdevSpeed;
    private double SDevSpeed;


    public void loadFromFile(File file) {
        try {
            Scanner sc = new Scanner(file);
            title = file.getName();
            scale = readIntegerValue(sc);
            xSize = readIntegerValue(sc);
            ySize = readIntegerValue(sc);


            minSpeed = readDouble(sc);
            maxSpeed = readDouble(sc);
            meanSpeed = readDouble(sc);
            sdevSpeed = readDouble(sc);


            int numberOfPatterns = readIntegerValue(sc);
            this.patterns = new ArrayList<DestinationPattern>(numberOfPatterns);
            for (int i = 0; i < numberOfPatterns; i++) {

                String line = sc.nextLine().trim();
                String name = line.split(" ")[1];
                DestinationPattern destinationPattern = new DestinationPattern(name);
                int numberOfLines = Integer.parseInt(sc.nextLine().trim());
                ;
                for (int j = 0; j < numberOfLines; j++) {


                    line = sc.nextLine().trim();
                    String[] points = line.split(";");
                    String[] parts = points[0].trim().split("=");
                    String pointString = parts[1].trim();
                    String[] coordinates = pointString.split(",");
                    double x = Double.parseDouble(coordinates[0].trim());
                    double y = Double.parseDouble(coordinates[1].trim());
                    Point2d startPoint = new Point2d(x, y);

                    parts = points[1].trim().split("=");
                    pointString = parts[1].trim();
                    coordinates = pointString.split(",");
                    x = Double.parseDouble(coordinates[0].trim());
                    y = Double.parseDouble(coordinates[1].trim());
                    Point2d endPoint = new Point2d(x, y);

                    destinationPattern.addPatternLine(new PatternLine(
                            startPoint, endPoint
                    ));

                }
                this.patterns.add(destinationPattern);
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    private double readDouble(Scanner sc) {

        String line = sc.nextLine().trim();
        String[] parts = line.split("=");
        return Double.parseDouble(parts[1].trim());
    }

    private Point2d readPoint2d(Scanner sc) {
        String[] parts = sc.nextLine().trim().split("=");
        String pointString = parts[1].trim();
        String[] coordinates = pointString.split(",");
        double x = Double.parseDouble(coordinates[0].trim());
        double y = Double.parseDouble(coordinates[1].trim());
        return new Point2d(x, y);

    }

    private int readIntegerValue(Scanner sc) {
        String line = sc.nextLine().trim();
        String[] parts = line.split("=");
        return Integer.parseInt(parts[1].trim());
    }


    public void saveToFile() {

        File scenarioFile = new File(title);
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(scenarioFile)));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        writer.println("scale = " + scale);
        writer.println("xSize = " + xSize);
        writer.println("ySize = " + ySize);





            writer.println("MinSpeed = " + minSpeed);
            writer.println("MaxSpeed = " + maxSpeed);
            writer.println("MeanSpeed = " + meanSpeed);
            writer.println("SDevSpeed = " + sdevSpeed);


        writer.println("NumberOfPatterns = " + patterns.size());
        for (DestinationPattern pattern : patterns) {
            writer.println("Pattern " + pattern.getName() + " : ");
            writer.println(pattern.getPatternLines().size());
            for (PatternLine line : pattern.getPatternLines()) {
                writer.print("StartPoint = "
                        + line.getStartPoint().x + "," + line.getStartPoint().y + " ; ");
                writer.println("EndPoint = "
                        + line.getEndPoint().x + "," + line.getEndPoint().y);
            }
        }


        writer.close();
        JOptionPane.showMessageDialog(new JFrame(), "Document Created successfully!", "success", JOptionPane.PLAIN_MESSAGE);
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public void setScale(String scale) {
        this.scale = Integer.parseInt(scale);
    }

    public void setXSize(String xSize) {
        this.xSize = Integer.parseInt(xSize);
    }

    public void setYSize(String ySize) {
        this.ySize = Integer.parseInt(ySize);
    }


    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public void setXSize(Integer xsize) {
        this.xSize = xsize;
    }

    public void setYSize(Integer ysize) {
        this.ySize = ysize;
    }

    public int getScale() {
        return scale;
    }

    public String getTitle() {
        return title;
    }

    public int getxSize() {
        return xSize;
    }

    public int getySize() {
        return ySize;
    }





    public List<DestinationPattern> getPatterns() {
        return patterns;
    }


    public void setPatterns(Enumeration<DestinationPattern> patterns) {
        this.patterns = new ArrayList<DestinationPattern>();
        while (patterns.hasMoreElements()) {
            this.patterns.add(patterns.nextElement());
        }
    }

    public int getRequiredNumberOfAgents() {
        int numberOfAgentsNeeded = 0;
        for (DestinationPattern pattern : patterns) {
            numberOfAgentsNeeded = Math.max(numberOfAgentsNeeded, pattern.getNumberOfAgentsNeeded());
        }
        return numberOfAgentsNeeded;
    }

    public void setSDevSpeed(double SDevSpeed) {
        this.sdevSpeed = SDevSpeed;
    }

    public void setMeanSpeed(double meanSpeed) {
        this.meanSpeed = meanSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public void setMinSpeed(double minSpeed) {
        this.minSpeed = minSpeed;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getMinSpeed() {
        return minSpeed;
    }

    public double getMeanSpeed() {
        return meanSpeed;
    }

    public double getSDevSpeed() {
        return SDevSpeed;
    }
}
