/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package motionPlanners;

import agent.Agent;
import javax.vecmath.Vector2d;
import sim.util.Bag;

/**
 *
 * @author michaellees
 *
 * The interface which needs to implemented by all classes that implement 
 * collision detection algorithms
 *
 */
public interface VelocityCalculator {

       public Vector2d calculateVelocity(Agent me,
            Bag neighbors, Vector2d preferredVelocity, double timeStep);
}
