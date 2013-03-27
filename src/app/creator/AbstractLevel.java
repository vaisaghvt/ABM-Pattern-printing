/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package app.creator;

import datatypes.AgentGroup;
import datatypes.DestinationPattern;
import datatypes.ModelDetails;
import datatypes.PatternLine;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;

import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.vecmath.Point2d;

/**
 *The app creation is done level by level. Just extend this class and Add to 
 * CreatorMain() in case you want to add a new Level.
 * 
 * 
 * @author vaisagh
 */
public abstract class AbstractLevel {

    protected ModelDetails model;
    protected JFrame frame;
    protected JLabel statusBar;
    protected JButton previousButton;
    protected JButton clearButton;
    protected JButton nextButton;

    public AbstractLevel(ModelDetails model, JFrame frame, JLabel statusBar, JPanel buttonArea) {
        this.model = model;
        this.frame = frame;
        this.statusBar = statusBar;
        this.previousButton = (JButton) buttonArea.getComponent(0);
        this.clearButton = (JButton) buttonArea.getComponent(1);
        this.nextButton = (JButton) buttonArea.getComponent(2);

    }

    public abstract void setUpLevel();

    public abstract void clearUp();

    public abstract void draw(Graphics g);

    public abstract void clearAllPoints();

    public abstract String getName();

    void drawCurrentPoint(Graphics g, Point currentPoint) {
        int xSize = model.getxSize();
        int ySize = model.getySize();
        int scale = model.getScale();
        g.setColor(Color.BLUE);
        g.drawLine(0, (int) (currentPoint.getY()),  (xSize * scale), (int) (currentPoint.getY()));
        g.drawLine((int) (currentPoint.getX()), 0, (int) (currentPoint.getX()), (ySize * scale));

    }

    void drawPoints(Graphics g, List<Point2d> points) {
        g.setColor(Color.blue);
        int scale = model.getScale();
        for (int i = 0; i < points.size(); i++) {

            double x = points.get(i).x;
            double y = points.get(i).y;
            g.fillOval((int) (x * scale - 3), (int) (y * scale - 3), 6, 6);
        }
    }

    void calculateCurrentPoint(MouseEvent e, Point currentPoint, boolean halfway) {

        int xSize = model.getxSize();
        int ySize = model.getySize();
        int scale = model.getScale();
        if (e.getX() > xSize * scale || e.getY() > ySize * scale
                || e.getX() < 0 || e.getY() < 0) {
            return;
        }

        currentPoint.setLocation(e.getPoint().x, e.getPoint().y);


    }

    public boolean mouseReleaseDefaultActions(MouseEvent e, Point currentPoint, Point2d point, Point2d prevPoint) {
        int xSize = model.getxSize();
        int ySize = model.getySize();
        int scale = model.getScale();
        if (currentPoint.getX() > xSize * scale || currentPoint.getY() > ySize * scale
                || currentPoint.getX() < 0 || currentPoint.getY() < 0) {
            return false;
        }
        if (point.x >= 0) {
            prevPoint.x = point.x;
            prevPoint.y = point.y;
        }

        point.x = currentPoint.getX() / scale;
        point.y = currentPoint.getY() / scale;


        if (prevPoint.x >= 0) {
            if (e.isControlDown()) {

                if (Math.abs(point.x - prevPoint.x) < 0.3) {

                    point.x= prevPoint.x;


                } else if (Math.abs(point.y - prevPoint.y) < 0.3) {

                    point.y = prevPoint.y;


                } else {
                    point.x = prevPoint.x;
                    point.y = prevPoint.y;


                    return false;


                }
            }
        }

//        if (prevPoint == null) {
//            prevPoint = new Position();
//        }
        return true;
    }

    public void drawGridLines(Graphics g) {
        int xSize = model.getxSize();
        int ySize = model.getySize();
        int scale = model.getScale();

            for (int i = 0; i <= xSize; i++) {
                g.setColor(Color.black);
                g.drawLine( (i * scale), 0, (i * scale), (ySize * scale));
            }

            for (int i = 0; i <= ySize; i++) {
                g.setColor(Color.black);
                g.drawLine(0, (i * scale),  (xSize * scale),  (i * scale));
            }

    }




    void drawAgentGroups(Graphics g, List<AgentGroup> agentGroups) {
        int scale = model.getScale();
        for (AgentGroup tempGroup : agentGroups) {


            double startX = tempGroup.getStartPoint().x * scale;
            double startY = tempGroup.getStartPoint().y * scale;
            double goalX = tempGroup.getEndPoint().x * scale;
            double goalY = tempGroup.getEndPoint().y * scale;



            g.setColor(Color.RED);

            g.drawRect((int) startX, (int) startY, (int) (goalX - startX), (int) (goalY - startY));

        }
    }


    public void drawPattern(Graphics g, DestinationPattern currentPattern) {
        int scale = model.getScale();
        for(PatternLine line: currentPattern.getPatternLines()){

            double startX = line.getStartPoint().x * scale;
            double startY = line.getStartPoint().y * scale;
            double goalX = line.getEndPoint().x * scale;
            double goalY = line.getEndPoint().y * scale;



            g.setColor(Color.RED);

            g.drawLine((int) startX, (int) startY, (int) goalX, (int) goalY);
        }
    }
}
