/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package app;


import java.awt.Color;
import javax.swing.JFrame;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.continuous.ContinuousPortrayal2D;
import sim.portrayal.grid.ObjectGridPortrayal2D;

/**
 * Gui
 *
 * @author michaellees
 * Created: Nov 24, 2010
 *
 * Copyright michaellees  
 *
 * Description:
 *
 * Gui window for running the testbed in MASON. This file that contains the main
 * method which should be executed when a visual display and control is required.
 */
public class Gui extends GUIState {

    /**
     * This is a singleton class that holds all the display information
     */
    public Display2D display;
    public JFrame displayFrame;
    private Model model;
    /**
     * Number of pixels that each cell (or unit space) should be represented by (for display).
     */
    public static int scale;
    ContinuousPortrayal2D agentPortrayal;
    ObjectGridPortrayal2D checkBoardPortrayal;
   

    public Gui() {
        this(new Model(PropertySet.SEED));
    }

    public Gui(SimState state) {
        super(state);

        model = (Model) state;
        checkBoardPortrayal = new ObjectGridPortrayal2D();
        agentPortrayal = new ContinuousPortrayal2D();

    }

    /**
     *  tell the portrayals what to portray and how to portray them
     */
    public void setupPortrayals() {


        agentPortrayal.setField(model.getSpace().getCurrentAgentSpace());


    }

    @Override
    public void start() {
        super.start();
        setupPortrayals();
        

        display.reset();
        display.repaint();
    }

    @Override
    public void load(SimState state) {
        super.load(state);
        setupPortrayals();  // set up our portrayals for the new SimState model
        display.reset();    // reschedule the displayer
        display.repaint();  // redraw the display
    }

    /**
     * This function controls the controller on the side of the display with all 
     * the play and stuff
     * @param c Controller that is responsible for running the simulation
     */
    @Override
    public void init(Controller c) {
        super.init(c);

        // Make the Display2D.  We'll have it display stuff later.
        model = (Model) state;

        display = new Display2D(model.getWorldXSize() * scale, model.getWorldYSize() * scale, this, 1);

        //create and display frame
        displayFrame = display.createFrame();
        c.registerFrame(displayFrame);   // register the frame so it appears in the "Display" list
        displayFrame.setVisible(true);
        displayFrame.setResizable(false);

        display.attach(agentPortrayal, "Agent portrayal");  // attach the portrayals


        

        // specify the backdrop color  -- what gets painted behind the displays
        display.setBackdrop(new Color(220, 220, 220));
    }

    @Override
    public void quit() {
        super.quit();
        if (displayFrame != null) {
            displayFrame.dispose();
        }
        displayFrame = null;  // let gc
        display = null;       // let gc
    }

    /**
     * These two methods return the name of the applet window and it's description 
     * wherever used. It actually practically overrides Mason's in-built methods
     * of the same name. For how this is actually done, check documentation.
     * @return 
     */
    public static String getName() {
        return "Crowd Simulation";
    }

    public static Object getInfo() {
        return "<H2>Motion planning testbed</H2><p>A testbed for various different"
                + " motion planning and collision avoidance systems.</p>";
    }

    public static void main(String[] args) {

        new Gui().createController();
    }
}
