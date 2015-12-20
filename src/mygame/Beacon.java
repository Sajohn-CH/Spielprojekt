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
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 *
 * @author florianwenk
 */
public class Beacon extends Entity {
    
    private RigidBodyControl beaconC;
    
    public Beacon(Vector3f location, int health){
        this.setLiving(true);
        this.setLocation(new Vector3f(location.x, 4, location.z));
        this.setHealth(health);
        Box b = new Box(8, 8, 8);
        this.setSpatial( new Geometry("Box", b));
        Material mat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Orange);
        this.getSpatial().setMaterial(mat);
        this.getSpatial().setLocalTranslation(this.getLocation());
        //Main.app.getRootNode().attachChild(beacon);
        //Main.app.getStateManager().attach(this);
        
        CollisionShape beaconShape = CollisionShapeFactory.createMeshShape(this.getSpatial());
        beaconC = new RigidBodyControl(beaconShape, 0);
        this.getSpatial().addControl(beaconC);
        Main.getBulletAppState().getPhysicsSpace().add(beaconC);
    }
    
    @Override
    public void action(float tpf) {
        if(!this.isLiving()){
            Main.getWorld().setPaused(true);
            Main.getWorld().getPlayer().setLiving(false);
            this.getSpatial().removeFromParent();
            Main.getBulletAppState().getPhysicsSpace().remove(beaconC);
        }
    }
    
    public void increaseLevel() {
        int upgradePrice = (int)(this.getMaxHealth());
        if(Main.app.getWorld().getPlayer().getMoney() >= upgradePrice) {
            int increaseHealth;
            increaseHealth = (int)(this.getMaxHealth()*0.1);
            this.setMaxHealth(this.getMaxHealth()+increaseHealth);
            this.increaseHealth(increaseHealth);
            this.setLevel(this.getLevel()+1);
            Main.app.getWorld().getPlayer().increaseMoney(-upgradePrice);
        }
    }
}
