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
import com.jme3.scene.shape.Line;
import mygame.Main;

/**
 * Turm, der Schiessende Bomben in normale umwandelt. Erstellt einen Turm der Schiessenden Bomben die Fähigkeit zu schiessen entzieht und kontrolliert diesen.
 * @author Florian Wenk
 */
public class DeactivationTower extends Tower{
      
    RigidBodyControl towerC;
    private Geometry line;
    private long shot;
    
    /**
     * Erstellt den Tower. Setzt wichtige Attribute des Turmes, ladet das Modell und erstellt die Schusslinie.
     * @param location 
     */
    public DeactivationTower (Vector3f location){
        this.setPrice(100);
        this.increaseTotalPaidMoney(this.getPrice());
        this.setLevel(1);
        this.setLocation(location);
        this.setLiving(true);
        this.setLocation(new Vector3f(location.x, 0, location.z));
        
        //Modell von: http://www.blendswap.com/blends/view/77840 (User: thanhkhmt1)
        //Bearbeitet von: Florian Wenk
        this.setSpatial(Main.app.getAssetManager().loadModel("Objects/PyramidTower.j3o").scale(0.5f));
        
        PointLight light1 = new PointLight();
        light1.setPosition(new Vector3f(location.x ,100, location.z));
        light1.setRadius(1000f);
        this.getSpatial().addLight(light1);
        
        PointLight light2 = new PointLight();
        light2.setPosition(new Vector3f(location.x ,1, location.z +100));
        light2.setRadius(1000f);
        this.getSpatial().addLight(light2);
        
        PointLight light3 = new PointLight();
        light3.setPosition(new Vector3f(location.x,1, location.z-100));
        light3.setRadius(1000f);
        this.getSpatial().addLight(light3);
        
        PointLight light4 = new PointLight();
        light4.setPosition(new Vector3f(location.x +100, 1, location.z));
        light4.setRadius(1000f);
        this.getSpatial().addLight(light4);
        
        PointLight light5 = new PointLight();
        light5.setPosition(new Vector3f(location.x -100, 1, location.z));
        light5.setRadius(1000f);
        this.getSpatial().addLight(light5);
        
        this.getSpatial().setLocalTranslation(this.getLocation());
        
        line = new Geometry("line");
        Material matL = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matL.setColor("Color", ColorRGBA.Red);
        line.setMaterial(matL);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void setCollidable(){
        CollisionShape towerShape = CollisionShapeFactory.createMeshShape(this.getSpatial());
        towerC = new RigidBodyControl(towerShape, 0);
        this.getSpatial().addControl(towerC);
        Main.getBulletAppState().getPhysicsSpace().add(towerC);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void action(float tpf) {
        for(int i = 0; i < Main.getWorld().getAllBombs().size(); i++){
            Bomb bomb = Main.getWorld().getAllBombs().get(i);
            if(bomb.getSpatial().getLocalTranslation().subtract(this.getSpatial().getLocalTranslation()).length() <= this.getRange() && bomb.getSpatial().getName().equals("shootingBomb") && this.canShoot()){
                this.getSpatial().lookAt(bomb.getSpatial().getLocalTranslation().add(Main.getWorld().getBombNode().getLocalTranslation()), new Vector3f(0,1,0));
            }
            if(bomb.getSpatial().getLocalTranslation().subtract(this.getSpatial().getLocalTranslation()).length() <= this.getRange() && isLiving() && canShoot() && bomb.getSpatial().getName().equals("shootingBomb")){
               ShootingBomb sBomb = (ShootingBomb) bomb;
                if(sBomb.isShooting()){
                    Line l = new Line(this.getSpatial().getLocalTranslation().add(0,4,0), bomb.getSpatial().getLocalTranslation());
                    line.setMesh(l);
                    Main.app.getRootNode().attachChild(line);
                    super.shot();
                    this.disableShooting((ShootingBomb) Main.getWorld().getAllBombs().get(i));
                }
            }
        }
        if(!this.isLiving() && !this.isDead()){
            died();
            Main.getBulletAppState().getPhysicsSpace().remove(towerC);
            line.removeFromParent();
        }
        if(System.currentTimeMillis()-getLastShot() >= 50){
            line.removeFromParent();
        }
    }
    
    /**
     * Entzieht der Bombe die Fähigkeit zu schiessen, indem die Methode {@link ShootingBomb#disableShooting() } aufgerufen wird.
     * @param b ShootingBomb der die Fähigkeit entzogen werden soll.
     */
    private void disableShooting(ShootingBomb b){
        b.disableShooting();
    }
        
    /**
     * {@inheritDoc }
     */
    @Override
    public void setLevel(int newLevel) {
        super.setLevel(newLevel);
        this.setRange(getNewRange(newLevel));
        this.setHealth(getNewHealth(newLevel));
        this.setMaxHealth(getNewHealth(newLevel));
        this.setShotsPerSecond(getNewSPS(newLevel));
    }
        
    /**
     * {@inheritDoc }
     */
    @Override
    public int getNewRange(int newLevel) {
        return 10+newLevel*5;
    }
        
    /**
     * {@inheritDoc }
     */
    @Override
    public int getNewHealth(int newLevel) {
        return 100+newLevel*25;
    }
        
    /**
     * {@inheritDoc }
     */
    @Override
    public double getNewSPS(int newLevel) {
        return Math.round(newLevel/20.0*100)/100.0;
    }
        
    /**
     * {@inheritDoc }
     */
    @Override
    public int getUpgradePrice() {
        return this.getMaxHealth();
    }
        
    /**
     * {@inheritDoc }
     */
    @Override
    public void increaseLevel() {
        Main.app.getHudState().showUpgradeTower(this);
    }
    
    /**
     * Upgradet Turm. Überprüft, ob der Spieler genügend Geld hat, und falls das der Fall ist, wird das Geld abgezogen und derTurm upgegraded.
     */
    public void upgrade() {
         if(Main.app.getWorld().getPlayer().getMoney() >= getUpgradePrice()) {
           this.increaseTotalPaidMoney(getUpgradePrice());
           Main.app.getWorld().getPlayer().increaseMoney(-getUpgradePrice());
           setLevel(this.getLevel()+1);
           Main.app.getWorld().getPlayer().playAudioBought();
         } else {
           Main.app.getWorld().getPlayer().playAudioNotEnoughMoney();
         }
    }
}
