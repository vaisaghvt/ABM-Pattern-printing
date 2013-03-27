package datatypes;

import javax.vecmath.Point2d;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created with IntelliJ IDEA.
 * User: vaisagh
 * Date: 24/3/13
 * Time: 10:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class DestinationPattern {

    private ArrayList<PatternLine> lines = new ArrayList<PatternLine>();
    private final String name;

    public DestinationPattern(String newPattern) {
        this.name = newPattern;
    }

    public void removeAllLines() {
        lines.clear();
    }

    public ArrayList<PatternLine>  getPatternLines() {
        return lines;
    }

    public String toString(){
        return name;
    }

    public void addPatternLine(PatternLine line) {
        lines.add(line);

    }



    public String getName() {
        return name;
    }

    public int getNumberOfAgentsNeeded(){
        int n=0;
        for(PatternLine line: lines){
            n+= line.getNumberOfAgentsNeeded();
        }
        return n;
    }

    public Collection<Point2d> getGoalPoints() {
        Collection<Point2d> goalPoints = new HashSet<Point2d>();
        for(PatternLine line: lines){
            goalPoints.addAll(line.getGoalPoints());
        }
        return goalPoints;
    }


}
