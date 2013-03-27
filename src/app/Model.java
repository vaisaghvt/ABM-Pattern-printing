package app;


import agent.Agent;
import agent.Agent.Act;
import agent.Agent.SenseThink;

import datatypes.AgentGroup;


import datatypes.DestinationPattern;
import datatypes.ModelDetails;
import environment.Space;


import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.vecmath.Point2d;

import sim.engine.ParallelSequence;
import sim.engine.Schedule;
import sim.engine.Sequence;
import sim.engine.SimState;

/**
 * Model
 *
 * @author michaellees Created: Nov 24, 2010
 *         <p/>
 *         Copyright michaellees
 *         <p/>
 *         Description:
 *         <p/>
 *         The Model is the core of the program. It contains the agentList, the
 *         obstacleList and the space and also the different parameters of the MODEL
 *         itself. It also contains the main().
 */
public class Model extends SimState {


    //    private double gridSize = PropertySet.GRIDSIZE;
    private Space space;

    /**
     * Actually initialised to ArrayList as can be seen in second constructor,
     * But using List type here so that we can change the implementation later
     * if needed
     */
    private List<Agent> agentList;

    public static Model publicInstance = null;
    private ArrayList<Collection<Point2d>> goalsForPattern;
    private int worldYSize;
    private int worldXSize;

    //    //for different pbm scenarios to set initial preferredVelocity
//    private int pbmScenario = 0;
//    
//    public void setPbmSecenario(int pbmTestCase){
//        pbmScenario = pbmTestCase;
//    }
//    public int getPbmScenario(){
//        return pbmScenario;
//    }
//    //the list to keep record of every agent's status in each timestep
//    //each record contains a list of status for each agent
//    //for each agent, there is a list to record all the necessary status (e.g., velocity, position etc)
//    public ArrayList<ArrayList<ArrayList>> records;
    public Model() {
        this(PropertySet.SEED);

    }

    public Model(long seed) {
        super(seed);
        publicInstance = this;

        agentList = new ArrayList<Agent>();
        initializeScenarioFromFile();


    }

    private void initializeScenarioFromFile() {

//        SimulationScenario scenario = unmarshalFile(PropertySet.FILE_PATH);
//        this.name = scenario.getName();
        ModelDetails scenario = new ModelDetails();
        scenario.loadFromFile(new File(PropertySet.FILE_PATH));

        this.worldXSize = scenario.getxSize();
        this.worldYSize = scenario.getySize();
        Gui.scale = scenario.getScale();

        space = new Space(scenario.getxSize(), scenario.getySize(), 1, this);


        if (scenario.getAgentGroups().size() > 1) {
            System.out.println("ERROR!");
            System.exit(0);
        }
        AgentGroup tempAgentGroup = scenario.getAgentGroups().get(0);
        final double maxSpeed = tempAgentGroup.getMaxSpeed();
        final double minSpeed = tempAgentGroup.getMinSpeed();
        final double meanSpeed = tempAgentGroup.getMeanSpeed();

        final double sdevSpeed = tempAgentGroup.getSDevSpeed();
        int[][] spaces = initializeLattice(tempAgentGroup.getStartPoint().x, tempAgentGroup.getStartPoint().y,
                tempAgentGroup.getEndPoint().x, tempAgentGroup.getEndPoint().y);

//                Vector2d groupDirection = new Vector2d(tempAgentGroup.getGroupDirectionX(),tempAgentGroup.getGroupDirectionY());//normalized vector to specify the group direction
//                groupDirection.normalize();
//
        for (int i = 0; i <scenario.getRequiredNumberOfAgents(); i++) {
            Agent agent = new Agent(this.getSpace());
            Point2d position = this.getAgentPosition(tempAgentGroup.getStartPoint().x, tempAgentGroup.getStartPoint().y,
                    spaces);
            agent.setCurrentPosition(position.x, position.y);

            double initialSpeed = random.nextGaussian() * sdevSpeed + meanSpeed;
            if (initialSpeed < minSpeed) {
                initialSpeed = minSpeed;
            } else if (initialSpeed > maxSpeed) {
                initialSpeed = maxSpeed;
            }

            agent.setPreferredSpeed(initialSpeed);
            agent.setMaximumSpeed(maxSpeed);

//                    agent.set  //to modify to include a group of agents with preferred direction!@@@@@@@@@
            this.addNewAgent(agent);

            agent.setPrefVelocity(); //set prefVel according to prefDirection
//                    groupDirection.scale(initialSpeed);
//                    agent.setVelocity(groupDirection);
        }


        goalsForPattern = new ArrayList<Collection<Point2d>>(scenario.getPatterns().size());
        for(DestinationPattern pattern: scenario.getPatterns()){
            goalsForPattern.add(pattern.getGoalPoints());
        }

        List<Agent> unAllocatedAgents = allocateGoalsToAgents(agentList, goalsForPattern.get(0));
        if(!unAllocatedAgents.isEmpty()){
            setGoalsForUnallocated(unAllocatedAgents);
        }



        this.scheduleAgents();

    }

    public List<Agent> allocateGoalsToAgents(List<Agent> agentList, Collection<Point2d> goals) {
        int i=0;
        List<Agent> unallocated = new ArrayList<Agent>();
        unallocated.addAll(agentList);
        for(Point2d goal:goals){
            agentList.get(i).setGoal(goal);
            unallocated.remove(agentList.get(i));
            i++;
        }
        return unallocated;



    }

    public void setGoalsForUnallocated(List<Agent> unAllocatedAgents) {
        WrapUp.resetDefaultAgentPosition();
        for(Agent agent:unAllocatedAgents){
            agent.setGoal(WrapUp.nextDefaultLocation());
        }
    }


    @Override
    public void start() {

        super.start();
        // This function is equivalent to a reset. 
        //Need to readup a bit more to see if it is even necessary...
        setup();
        initializeScenarioFromFile();
        schedule.scheduleRepeating(new WrapUp(this, agentList, goalsForPattern), 5, 50.0);

    }


    /**
     * resets all the values to the initial values. this is just to be safe.
     */
    public void setup() {

        space = null;
        agentList = new ArrayList<Agent>();

        Agent.agentCount = 0;

    }

    public void scheduleAgents() {
        List<SenseThink> senseThinkAgents = new ArrayList<SenseThink>();

        List<Act> actAgents = new ArrayList<Act>();
        for (Agent agent : agentList) {
            senseThinkAgents.add(agent.getSenseThink());
            actAgents.add(agent.getAct());
        }

//        senseThinkStoppable = mySpace.getModel().schedule.scheduleRepeating(senseThinkAgent, 2, 1.0);
//        actStoppable = mySpace.getModel().schedule.scheduleRepeating(actAgent, 3, 1.0);
//        (new Agent(this.space)).scheduleAgent();
        schedule.scheduleRepeating(Schedule.EPOCH, 1, new ParallelSequence(senseThinkAgents.toArray(new SenseThink[]{})), 1.0);
        schedule.scheduleRepeating(Schedule.EPOCH, 2, new Sequence(actAgents.toArray(new Act[]{})), 1.0);
    }





    public Space getSpace() {
        return space;
    }


    public void addNewAgent(Agent a) {
        a.createSteppables();
        agentList.add(a);
        space.updatePositionOnMap(a, a.getX(), a.getY());

    }


    @Override
    public void finish() {
        System.out.println("wrapping up");

    }

    private Point2d getAgentPosition(double mnx, double mny, int[][] spaces) {
        // determine the mass of the agent;
        int x = 0, y = 0;

        while (true) {
            x = this.random.nextInt(spaces.length);
            y = this.random.nextInt(spaces[0].length);
//            pos = new Point2d(x, y);
            if (spaces[x][y] == 0) {
                spaces[x][y] = 1;
                break;
            }
            // make sure agents are not created on top of each other
//            for (Agent agent : this.getAgentList()) {
//                double dx = x - agent.getCurrentPosition().getX();
//                double dy = y - agent.getCurrentPosition().getY();
//                double d = Math.hypot(dx, dy);
//
//                double minDist = (agent.getRadius() * 2 + Agent.RADIUS) / 2.0;
//
//                // if d<=minDist then the agents are 'overlapping'...
//                if (d <= minDist) {
//                    pos = null;
//                    break;
//                }
//            }
        }


        return new Point2d(mnx + (x * Agent.RADIUS * 2), mny + (y * Agent.RADIUS * 2));

    }

    public static void main(String[] args) {

        doLoop(Model.class, args);

        System.exit(0);
    }

    private int[][] initializeLattice(Double startx, Double starty, Double endx, Double endy) {
        int sizeX = (int) Math.floor((endx - startx) / (Agent.RADIUS * 2));
        int sizeY = (int) Math.floor((endy - starty) / (Agent.RADIUS * 2));
        return new int[sizeX][sizeY];

    }

    public int getWorldYSize() {
        return worldYSize;
    }

    public int getWorldXSize() {
        return worldXSize;
    }
}
