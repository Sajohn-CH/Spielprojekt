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
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 *
 * @author florianwenk
 */
public class SimpleTower extends Tower{
    
    Spatial tower;
    RigidBodyControl towerC;
    
    public SimpleTower (Vector3f location, int damage, int range){
        this.setDamage(damage);
        this.setRange(range);
        this.setLocation(location);
        this.setLiving(true);
        this.setLocation(new Vector3f(location.x, 4, location.z));
        Box b = new Box(2, 8, 2);
        tower = new Geometry("Box", b);
        Material mat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Yellow);
        tower.setMaterial(mat);
        tower.setLocalTranslation(this.getLocation());
        Main.towers.add(this);
        Main.towerNode.attachChild(tower);
        Main.app.getStateManager().attach(this);
        
        CollisionShape towerShape = CollisionShapeFactory.createMeshShape(tower);
        towerC = new RigidBodyControl(towerShape, 0);
        tower.addControl(towerC);
        Main.bulletAppState.getPhysicsSpace().add(towerC);
    }
    
    @Override
      public void update(float tpf) {
        for(int i = 0; i < Main.bombs.size(); i++){
            if(Main.bombs.get(i).bomb.getLocalTranslation().subtract(this.tower.getLocalTranslation()).length() <= this.getRange() && isLiving()){
                    this.makeDamage(Main.bombs.get(i));
            }
        }
        if(!this.isLiving()){
            this.tower.removeFromParent();
            Main.bulletAppState.getPhysicsSpace().remove(towerC);
        }
    }
    
}
