/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;

/**
 *
 * @author Florian Wenk
 */
public class Way {
    private ArrayList<Vector3f> corners;
    private int positionID;
    
    public Way(){
        corners = Main.getWorld().getAllCorners();
        positionID = 1;
    }
    
    public Vector3f getStartPoint(){
        return corners.get(0).mult(2);
    }
    
    public Vector3f getNextCorner(){
        positionID += 1;
        return corners.get(positionID-1).mult(2);
    }
    
}
