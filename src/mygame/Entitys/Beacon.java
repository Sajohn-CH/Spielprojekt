/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.Entitys;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import mygame.Main;

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
        
        this.setSpatial(Main.app.getAssetManager().loadModel("Objects/Beacon.j3o").scale(2f));
        this.getSpatial().setLocalTranslation(this.getLocation());
        
        PointLight light1 = new PointLight();
        light1.setPosition(new Vector3f(location.x ,1000, location.z));
        light1.setRadius(10000f);
        this.getSpatial().addLight(light1);
        
        PointLight light2 = new PointLight();
        light2.setPosition(new Vector3f(location.x ,1, location.z +1000));
        light2.setRadius(10000f);
        this.getSpatial().addLight(light2);
        
        PointLight light3 = new PointLight();
        light3.setPosition(new Vector3f(location.x,1, location.z-1000));
        light3.setRadius(10000f);
        this.getSpatial().addLight(light3);
        
        PointLight light4 = new PointLight();
        light4.setPosition(new Vector3f(location.x +1000, 1, location.z));
        light4.setRadius(10000f);
        this.getSpatial().addLight(light4);
        
        PointLight light5 = new PointLight();
        light5.setPosition(new Vector3f(location.x -1000, 1, location.z));
        light5.setRadius(10000f);
        this.getSpatial().addLight(light5);
        
//        Box b = new Box(8, 8, 8);
//        this.setSpatial( new Geometry("Box", b));
//        Material mat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
//        mat.setColor("Color", ColorRGBA.Orange);
//        this.getSpatial().setMaterial(mat);
//        this.getSpatial().setLocalTranslation(this.getLocation());
        //Main.app.getRootNode().attachChild(beacon);
        //Main.app.getStateManager().attach(this);
        
        CollisionShape beaconShape = CollisionShapeFactory.createMeshShape(this.getSpatial());
        beaconC = new RigidBodyControl(beaconShape, 0);
        this.getSpatial().addControl(beaconC);
        Main.getBulletAppState().getPhysicsSpace().add(beaconC);
    }
    
    public void turn(){
        this.getSpatial().lookAt(Main.getWorld().getAllCorners().get(Main.getWorld().getAllCorners().size()-1), new Vector3f(0,1,0));
    }
    
    @Override
    public void action(float tpf) {
        if(!this.isLiving()){
            Main.getWorld().setPaused(true);
            Main.getWorld().getPlayer().setLiving(false);
            this.getSpatial().removeFromParent();
            Main.getBulletAppState().getPhysicsSpace().remove(beaconC);
            Main.app.gameOver();
        }
    }
    
    public void increaseLevel() {
        int upgradePrice = getNewHealthPrice();
        if(Main.app.getWorld().getPlayer().getMoney() >= upgradePrice) {
            int increaseHealth;
            increaseHealth = getNewMaxHealth()-getMaxHealth();
            this.setMaxHealth(this.getMaxHealth()+increaseHealth);
            this.increaseHealth(increaseHealth);
            this.setLevel(this.getLevel()+1);
            Main.app.getWorld().getPlayer().increaseMoney(-upgradePrice);
        }
    }
    
    public int getNewHealthPrice() {
        return (int)(this.getMaxHealth());
    }
    
    public int getNewMaxHealth() {
        return (int)(this.getMaxHealth()*1.1);
    }
}
