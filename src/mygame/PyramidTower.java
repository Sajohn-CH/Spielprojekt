/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Dome;
import com.jme3.scene.shape.Line;

/**
 *
 * @author florianwenk
 */
public class PyramidTower extends Tower{
      
    RigidBodyControl towerC;
    private Geometry line;
    private long shot;
    
    public PyramidTower (Vector3f location, int damage, int range){
        this.setDamage(damage);
        this.setRange(range);
        this.setLocation(location);
        this.setLiving(true);
        this.setLocation(new Vector3f(location.x, 0, location.z));
        Dome b = new Dome(Vector3f.ZERO, 2, 4, 1f,false); 
        this.setSpatial(new Geometry("Pyramid", b));
        this.getSpatial().setLocalScale(2f, 15f, 2f);
        Material mat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Magenta);
        this.getSpatial().setMaterial(mat);
        this.getSpatial().setLocalTranslation(this.getLocation());
        
        line = new Geometry("line");
        Material matL = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matL.setColor("Color", ColorRGBA.Red);
        line.setMaterial(matL);
        
        CollisionShape towerShape = CollisionShapeFactory.createMeshShape(this.getSpatial());
        towerC = new RigidBodyControl(towerShape, 0);
        this.getSpatial().addControl(towerC);
        Main.getBulletAppState().getPhysicsSpace().add(towerC);
    }
    
    @Override
    public void action(float tpf) {
        for(int i = 0; i < Main.getWorld().getAllBombs().size(); i++){
            if(Main.getWorld().getAllBombs().get(i).getSpatial().getLocalTranslation().subtract(this.getSpatial().getLocalTranslation()).length() <= this.getRange() && isLiving()){
               Line l = new Line(this.getSpatial().getLocalTranslation().setY(7), Main.getWorld().getAllBombs().get(i).getSpatial().getLocalTranslation());
               line.setMesh(l);
               Main.app.getRootNode().attachChild(line);
               shot = System.currentTimeMillis();
                    this.makeDamage(Main.getWorld().getAllBombs().get(i));
            }
        }
        if(!this.isLiving()){
            Main.getBulletAppState().getPhysicsSpace().remove(towerC);
        }
        if(System.currentTimeMillis()-shot >= 50){
            line.removeFromParent();
        }
    }
    
}
