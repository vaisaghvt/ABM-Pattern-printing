package agent;

/**
 *
 *
 *
 *
 *
 * Description:This class describes the agents themselves, except for the
 * portrayal components all the internal characteristics of the agents are
 * stored here.
 *
 */

import app.PropertySet;
import environment.Space;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import motionPlanners.VelocityCalculator;
import motionPlanners.rvo2.RVO_2_1;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.portrayal.LocationWrapper;
import sim.util.Bag;
import utility.PrecisePoint;

public class Agent extends AgentPortrayal {


    public final static double RADIUS = 0.15;
    public final static double DEFAULT_PREFERRED_SPEED = 1.2;
    public final static int SENSOR_RANGE = 30; //sensor range in proportion to agent radius
    public static int agentCount = 0; // number of agents
    protected final int id;



    /**
     * Current position of the agent from javax.vecmath
     */
    protected PrecisePoint currentPosition;

    /**
     * Current velocity of the agent
     */
    protected PrecisePoint velocity;
    private PrecisePoint chosenVelocity;
    /**
     * Agents preferred velocity, calculated each timestep according to goal and
     * current location
     */
    protected Vector2d prefVelocity;
    /**
     * Sets whether to display the agent's velocity on the map or not
     */
    protected double preferredSpeed;
    protected double maxSpeed;


    /**
     * Intermediate goal destination of agent
     */
    protected Point2d goal;
    /**
     * Environmental space of the agents, contains multiple MASON fields
     */
    protected Space mySpace;
    //@hunan: added for PBM only
//    public boolean violateExpectancy;
    /**
     * The motion planning system used by the agent, this can used any method
     * for motion planning that implements the VelocityCalculator interface
     */
    protected VelocityCalculator velocityCalc;
    private SenseThink senseThinkAgent;
    private Act actAgent;
    private boolean goalReached;


    public Agent(Space mySpace) {
        super(); //for portraying the trails on the agentportrayal layer
        this.mySpace = mySpace;
        currentPosition = new PrecisePoint();
//        goal = new Point2d();
        id = agentCount++;
        //DEFAULT_PREFERRED_SPEED is the default value specified in 1.xml
        //the parameter value of preferredSpeed should be only set to this default value when it is not set with the value from the xml initialization file
        if (preferredSpeed == 0) {
            preferredSpeed = Agent.DEFAULT_PREFERRED_SPEED;
        }

        velocityCalc = new RVO_2_1();
    }


    public boolean reachedGoal() {

        if (goalReached) {
            return true;
        } else {


            goalReached = (goal != null) && (currentPosition.toPoint().distance(goal) < RADIUS * 3);


            return goalReached;
        }
    }

    public Point2d getCurrentPosition() {
        return currentPosition.toPoint();
    }

    /*
     * returns the position at the face (edge of the circle) along the velocity direction
     * redundant with getMyPositionAtEye
     */
//    public Point2d getCurrentEyePosition(){
//        PrecisePoint predictPos = new PrecisePoint(this.getVelocity().getX(), this.getVelocity().getY());
//        predictPos.scale(RADIUS);
//        predictPos.add(this.getCurrentPosition());
//        return predictPos.toPoint();
//    }
    public double getX() {
        return currentPosition.getX();
    }

    public double getY() {
        return currentPosition.getY();
    }

    final public void setCurrentPosition(double x, double y) {
        currentPosition = new PrecisePoint(x, y);
    }

    public Vector2d getVelocity() {
        if (velocity == null) {
            velocity = new PrecisePoint();
        }
//        return velocity.toVector(); //the error comes from here, 1.3,0 gives vector value of 1,0
        return new Vector2d(velocity.getX(), velocity.getY());
    }


    /**
     * Sets and returns the prefered velocity. Generally this is just the
     * velocity towards goal. But in the evacTest scenario, this is set to the
     * checkpoint nearby
     */
    public final void setPrefVelocity() {
        if (this.goal != null) {
            //no preferredDirection            
            prefVelocity = new Vector2d(goal);
            prefVelocity.sub(currentPosition.toPoint());
            prefVelocity.normalize();
            prefVelocity.scale(preferredSpeed); //@hunan:added the scale for perferredSpeed
        } else {
//            System.out.println("goal is null");
            prefVelocity = new Vector2d();
        }

    }


    public double getMaxSpeed() {
        return maxSpeed;
    }


    public Space getMySpace() {
        return mySpace;
    }


    public void setPreferredSpeed(double preferredSpeed) {
        this.preferredSpeed = preferredSpeed;
    }

    @Override
    public String getName(LocationWrapper wrapper) {
        return "Agent " + id;
    }

    @Override
    public String toString() {
        return "Agent" + id;
    }


    public SenseThink getSenseThink() {
        return this.senseThinkAgent;
    }

    public Act getAct() {
        return this.actAgent;
    }

    public void createSteppables() {
        this.senseThinkAgent = new SenseThink();
        this.actAgent = new Act();

    }


    public void setMaximumSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public double getRadius() {
        return radius;
    }

    public void setGoal(Point2d goal) {
//        System.out.println("Agent " + id + " goal set to " + goal);
        goalReached = false;
        this.goal = goal;
    }



    public class SenseThink implements Steppable {

        @Override
        public void step(SimState ss) {



            Bag sensedNeighbours = mySpace.senseNeighbours(Agent.this);


            //default as towards the goal
            setPrefVelocity(); //update the preferredVelocity according to the current position and the goal


            if (prefVelocity.x != 0 || prefVelocity.y != 0) {
                assert !Double.isNaN(prefVelocity.x);
                Vector2d tempVelocity = velocityCalc.calculateVelocity(Agent.this, sensedNeighbours,
                        prefVelocity, PropertySet.TIME_STEP);
                if (Double.isNaN(
                        tempVelocity.x)) {
                    assert false;
                }
                chosenVelocity = new PrecisePoint(tempVelocity.x, tempVelocity.y);
            } else {
                chosenVelocity = new PrecisePoint(0, 0);
            }
//            }//end of if(!dead)
        }//end of step(ss)


    }

    /**
     * updates the actual position after calculation. The division of steps is
     * to ensure that all agents update their positions and move simultaneously.
     * Implementation of Removable step is to make sure agents die after exiting
     * the simulation area
     */
    public class Act implements Steppable {

        @Override
        public void step(SimState ss) {



                velocity = new PrecisePoint(chosenVelocity.getX() + mySpace.getModel().random.nextFloat() * utility.Geometry.EPSILON, chosenVelocity.getY() + mySpace.getModel().random.nextFloat() * utility.Geometry.EPSILON);

                double currentPosition_x = (currentPosition.getX()
                        + velocity.getX() * PropertySet.TIME_STEP);
                double currentPosition_y = (currentPosition.getY()
                        + velocity.getY() * PropertySet.TIME_STEP);
                setCurrentPosition(currentPosition_x, currentPosition_y);
                getMySpace().updatePositionOnMap(Agent.this, currentPosition_x,
                        currentPosition_y);

        }
    }
    /*
     * for Display in the Property Window 
     */


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Agent other = (Agent) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return this.id;
    }


}
