/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import agent.Agent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import sim.engine.SimState;
import sim.engine.Steppable;

import javax.vecmath.Point2d;

/**
 *
 * Checks at the end of each time step if the simulation has ended. And kills the
 * threads if it's done.
 * Change the checks here if you want to change when the simulation ends.
 * @author vaisagh
 */
class WrapUp implements Steppable {

    private final List<Agent> agents;
    private final Model state;
    private final ArrayList<Collection<Point2d>> goalsForPattern;
    private int currentPatternNumber;
    private final static Point2d startingPoint = new Point2d(1,25);
    private static Point2d currentDefaultLocation;
    private static double yDisplacement =0.9;
    private static double xDisplacement =0.5;

    public WrapUp(Model state, List<Agent> agentList, ArrayList<Collection<Point2d>> goalsForPattern) {
        this.agents = agentList;
        this.state = state;
        this.goalsForPattern = goalsForPattern;
        currentPatternNumber =0;
    }

    @Override
    public void step(SimState arg0) {

        for (Agent agent : agents) {
            if(!agent.reachedGoal()){
//                System.out.println(agent.toString()+" not reached");
                return;
            }
        }
        currentPatternNumber=(currentPatternNumber+1)%goalsForPattern.size();
//        if(currentPatternNumber<goalsForPattern.size()){

            List<Agent> unallocated =state.allocateGoalsToAgents(agents,goalsForPattern.get(currentPatternNumber));
            state.setGoalsForUnallocated(unallocated);
            return;
//        }
//        System.out.println("here");
//        state.kill();
    }


    public static void resetDefaultAgentPosition(){
        currentDefaultLocation = new Point2d(startingPoint);

    }


    public static Point2d nextDefaultLocation(){
        currentDefaultLocation = new Point2d(currentDefaultLocation.x+xDisplacement, currentDefaultLocation.y);
        if(currentDefaultLocation.x> Model.publicInstance.getWorldXSize()){
            currentDefaultLocation = new Point2d(startingPoint.x, currentDefaultLocation.y+ yDisplacement);
        }
        return currentDefaultLocation;
    }
}
