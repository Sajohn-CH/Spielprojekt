/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.Entitys;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.light.PointLight;
import com.jme3.math.Vector3f;
import mygame.Main;

/**
 * Der Beacon. Setzt den Beacon und kontrolliert diesen.
 * @author Florian Wenk
 */
public class Beacon extends Entity {
    
    private RigidBodyControl beaconC;
    
    /**
     * Erstellt den Beacon. Ladet das Modell und setzt dieses kollidierbar.
     * @param location Ort
     * @param health Lebenspunkte
     */
    public Beacon(Vector3f location, int health){
        this.setLiving(true);
        this.setLocation(new Vector3f(location.x, 4, location.z));
        this.setHealth(health);
        
        //Modell von: http://www.blendswap.com/blends/view/49878 (User: ultrasuperbox)
        //Bearbeitet von: Florian Wenk
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
        
        CollisionShape beaconShape = CollisionShapeFactory.createMeshShape(this.getSpatial());
        beaconC = new RigidBodyControl(beaconShape, 0);
        this.getSpatial().addControl(beaconC);
        Main.getBulletAppState().getPhysicsSpace().add(beaconC);
    }
    
    /**
     * Dreht den Beacon in die Richtung. Wird bisher nicht benötigt
     */
    public void turn(){
        this.getSpatial().lookAt(Main.getWorld().getAllCorners().get(Main.getWorld().getAllCorners().size()-1), new Vector3f(0,1,0));
    }
    
    /**
     * 
     * {@inheritDoc }
     */
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
    
    /**
     * Erhöht das Level des Beacons.
     */
    public void increaseLevel() {
        int upgradePrice = getNewHealthPrice();
        if(Main.app.getWorld().getPlayer().getMoney() >= upgradePrice) {
            int increaseHealth;
            increaseHealth = getNewMaxHealth()-getMaxHealth();
            this.setMaxHealth(this.getMaxHealth()+increaseHealth);
            this.increaseHealth(increaseHealth);
            this.setLevel(this.getLevel()+1);
            Main.app.getWorld().getPlayer().increaseMoney(-upgradePrice);
            Main.app.getWorld().getPlayer().playAudioBought();
        } else {
           Main.app.getWorld().getPlayer().playAudioNotEnoughMoney();
        }
    }
    
    /**
     * 
     * @return Kosten für das Erhöhen des Lebens
     */
    public int getNewHealthPrice() {
        return (int)(this.getMaxHealth());
    }
    
    /**
     * 
     * @return Die Lebenspunkte des Beacons wenn das Level un eins erhöht wird.
     */
    public int getNewMaxHealth() {
        return (int)(this.getMaxHealth()*1.1);
    }
}
