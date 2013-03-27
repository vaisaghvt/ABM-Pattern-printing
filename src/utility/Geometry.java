/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import javax.vecmath.Vector2d;

/**
 * Geometry
 *
 * @author michaellees
 * Created: Dec 20, 2010
 *
 * Copyright michaellees 
 *
 * Description:
 *
 */
public class Geometry {

    public static float EPSILON;



    public static double det(Vector2d a, Vector2d b) {
        return a.x * b.y - a.y * b.x;
    }




    

    

}
