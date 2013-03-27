package datatypes;

import sun.font.GlyphList;

import javax.vecmath.Point2d;

/**
 * Created with IntelliJ IDEA.
 * User: vaisagh
 * Date: 24/3/13
 * Time: 7:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class AgentGroup {
    private Point2d startPoint;
    private Point2d endPoint;
    private int size;
    private double minSpeed;
    private double maxSpeed;
    private double meanSpeed;
    private double SDevSpeed;

    public AgentGroup(){
    }
    public AgentGroup(Point2d startPoint, Point2d endPoint, double minSpeed,
                      double maxSpeed, double meanSpeed, double sdevSpeed, int size) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.size = size;
        this.minSpeed = minSpeed;
        this.meanSpeed = meanSpeed;
        this.maxSpeed = maxSpeed;
        this.SDevSpeed = sdevSpeed;
    }

    public Point2d getStartPoint() {
        return startPoint;
    }

    public Point2d getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Point2d endPoint) {
        this.endPoint = endPoint;
    }

    public void setStartPoint(Point2d startPoint) {
        this.startPoint = startPoint;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setMinSpeed(double minSpeed) {
        this.minSpeed = minSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public void setMeanSpeed(double meanSpeed) {
        this.meanSpeed = meanSpeed;
    }

    public void setSDevSpeed(double SDevSpeed) {
        this.SDevSpeed = SDevSpeed;
    }

    public int getSize() {
        return size;
    }

    public double getMinSpeed() {
        return minSpeed;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getMeanSpeed() {
        return meanSpeed;
    }

    public double getSDevSpeed() {
        return SDevSpeed;
    }
}
