package datatypes;



import app.creator.CreatorMain;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import java.awt.*;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created with IntelliJ IDEA.
 * User: vaisagh
 * Date: 24/3/13
 * Time: 10:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class PatternLine {
    private final Point2d startPoint;
    private final Point2d endPoint;
    private final int numberOfAgentsNeeded;
    private static final double PERSONAL_SPACE = 0.1;

    public PatternLine(Point2d startPoint, Point2d endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        numberOfAgentsNeeded = calculateNumberOfAgentsNeeded();
    }

    private int calculateNumberOfAgentsNeeded() {
        double agentDiameter = (CreatorMain.AGENT_RADIUS+PERSONAL_SPACE) * 2;
        
        double lengthOfLine = startPoint.distance(endPoint);
        int n = (int) Math.floor(lengthOfLine/agentDiameter);


        
        return n;
    }

    public Point2d getStartPoint() {
        return startPoint;
    }

    public Point2d getEndPoint() {
        return endPoint;
    }

    public int getNumberOfAgentsNeeded() {
        return numberOfAgentsNeeded;
    }

    public Collection<? extends Point2d> getGoalPoints() {
        double firstAgentDistance = CreatorMain.AGENT_RADIUS+PERSONAL_SPACE;
        double subsequentDistance = (CreatorMain.AGENT_RADIUS+PERSONAL_SPACE) * 2;
        Vector2d unitDirection = new Vector2d(endPoint);
        unitDirection.sub(startPoint);
        unitDirection.normalize();
        int numberOfAgents = calculateNumberOfAgentsNeeded();

        Collection<Point2d> results = new HashSet<Point2d>();
        Point2d firstAgentLocation = new Point2d(unitDirection);
        firstAgentLocation.scale(firstAgentDistance);
        firstAgentLocation.add(startPoint);

        results.add(firstAgentLocation);
        for(int i=1;i<numberOfAgents;i++){
            Point2d newGoal = new Point2d(unitDirection);
            newGoal.scale(firstAgentDistance+subsequentDistance*i);
            newGoal.add(startPoint);

            results.add(newGoal);
        }




        return results;
    }
}
