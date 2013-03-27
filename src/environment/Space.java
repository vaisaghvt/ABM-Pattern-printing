package environment;

import agent.Agent;

import javax.vecmath.Point2d;

import app.Model;
import sim.field.continuous.Continuous2D;
import sim.util.Bag;
import sim.util.Double2D;

/**
 * Space
 *
 * @author michaellees Created: Nov 24, 2010
 *
 * Copyright michaellees
 *
 * Description:
 *
 * This class defines the environment. It has two layers : an agent layer with
 * all the agents on it and an obstacleSpace layer with all the obstacles on it.
 */
public class Space {

    protected double gridDimension;
    public static double xRealSize;
    public static double yRealSize;
    /**
     * This is the space were all the agents are stored
     */
    protected Continuous2D agentSpace;


    protected Model model;

    public Space(int xSize, int ySize, double gridSize, Model rm) {

        gridDimension = gridSize;

        xRealSize = xSize * gridDimension;
        yRealSize = ySize * gridDimension;

        agentSpace = new Continuous2D(gridDimension, xRealSize, yRealSize);

        model = rm;
    }

    public Continuous2D getCurrentAgentSpace() {
        return agentSpace;
    }

    public Model getModel() {
        return model;
    }



    public void updatePositionOnMap(Agent agent, double x, double y) {
        //TODO: vvt: check whether the agent was created on an existing obstacle
        agent.setCurrentPosition(x, y);
        agentSpace.setObjectLocation(agent, new Double2D(x, y));
    }

    public Bag senseNeighbours(Agent me) {
        double sensorRange = Agent.SENSOR_RANGE;
        Bag neighbours = findNeighbours(me.getCurrentPosition(), sensorRange * me.getRadius());

//        do {
//////            Vector2d unitAgentDirection = new Vector2d(me.getPrefVelocity());
//////            unitAgentDirection.normalize();
//////            for (int i = 0; i < neighbours.size(); i++) {
//////                Agent agentNeighbour = (Agent) neighbours.get(i);
//////                Vector2d neighbourDirection = new Vector2d(agentNeighbour.getCurrentPosition());
//////                neighbourDirection.sub(me.getCurrentPosition());
//////                neighbourDirection.normalize();
//////                double angleRadians = neighbourDirection.angle(unitAgentDirection);
//////                if ((Double.compare(angleRadians, (Math.PI / 2.0)) > 0
//////                        && Double.compare(angleRadians, (3.0 * Math.PI / 2.0)) < 0)) {
//////                    neighbours.remove(i);
//////                }
//////            }
//            if (neighbours.size() > 10) {
//                sensorRange *= 0.8;
//               neighbours = findNeighbours(me.getCurrentPosition(), sensorRange * me.getRadius());
//            }
//        } while (neighbours.size() > 10);
        return neighbours;

    }


    public Bag findNeighbours(Point2d currentPosition, double radius) {
        Bag neighbours = agentSpace.getObjectsExactlyWithinDistance(new Double2D(currentPosition.x, currentPosition.y), radius);
        return neighbours;
    }


}
