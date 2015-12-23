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
    
    public PyramidTower (Vector3f location){
        this.setPrice(100);
        this.setLevel(1);
        this.setLocation(location);
        this.setLiving(true);
        this.setLocation(new Vector3f(location.x, 0, location.z));
        
        Dome b = new Dome(Vector3f.ZERO, 2, 4, 1f,false); 
        this.setSpatial(new Geometry("PyramidTower", b));
        this.getSpatial().setLocalScale(2f, 15f, 2f);
        Material mat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Magenta);
        this.getSpatial().setMaterial(mat);
        this.getSpatial().setLocalTranslation(this.getLocation());
        
        line = new Geometry("line");
        Material matL = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matL.setColor("Color", ColorRGBA.Red);
        line.setMaterial(matL);
    }
    
    @Override
    public void setCollidable(){
        CollisionShape towerShape = CollisionShapeFactory.createMeshShape(this.getSpatial());
        towerC = new RigidBodyControl(towerShape, 0);
        this.getSpatial().addControl(towerC);
        Main.getBulletAppState().getPhysicsSpace().add(towerC);
    }
    
    @Override
    public void action(float tpf) {
        for(int i = 0; i < Main.getWorld().getAllBombs().size(); i++){
            Bomb bomb = Main.getWorld().getAllBombs().get(i);
            if(bomb.getSpatial().getLocalTranslation().subtract(this.getSpatial().getLocalTranslation()).length() <= this.getRange() && isLiving() && canShoot() && bomb.getSpatial().getName().equals("shootingBomb")){
               ShootingBomb sBomb = (ShootingBomb) bomb;
                if(sBomb.isShooting()){
                    Line l = new Line(this.getSpatial().getLocalTranslation().setY(7), Main.getWorld().getAllBombs().get(i).getSpatial().getLocalTranslation());
                    line.setMesh(l);
                    Main.app.getRootNode().attachChild(line);
                    super.shot();
                    this.disableShooting((ShootingBomb) Main.getWorld().getAllBombs().get(i));
                }
            }
        }
        if(!this.isLiving()){
            Main.getBulletAppState().getPhysicsSpace().remove(towerC);
            line.removeFromParent();
        }
        if(System.currentTimeMillis()-getLastShot() >= 50){
            line.removeFromParent();
        }
    }
    
    private void disableShooting(ShootingBomb b){
        b.disableShooting();
    }
        
    @Override
    public void setLevel(int newLevel) {
        super.setLevel(newLevel);
        this.setRange(getNewRange(newLevel));
        this.setHealth(getNewHealth(newLevel));
        this.setMaxHealth(getNewHealth(newLevel));
        this.setShotsPerSecond(getNewSPS(newLevel));
    }
    
    @Override
    public int getNewRange(int newLevel) {
        return 10+newLevel*5;
    }
    
        
    @Override
    public int getNewHealth(int newLevel) {
        return 100+newLevel*25;
    }
    
    @Override
    public double getNewSPS(int newLevel) {
        return Math.round(newLevel/20.0*100)/100.0;
    }
    
    @Override
    public int getUpgradePrice() {
        return this.getMaxHealth();
    }
    
    /**
     * Wird aufgerufen wenn der Turm upgegradet werden soll. Ruft zuerst Dialog zum bestätigen auf.
     */
    @Override
    public void increaseLevel() {
        Main.app.getHudState().showUpgradeTower(this);
    }
    
    /**
     * Upgradet Turm.
     */
    public void upgrade() {
         if(Main.app.getWorld().getPlayer().getMoney() >= getUpgradePrice()) {
           Main.app.getWorld().getPlayer().increaseMoney(-getUpgradePrice());
           setLevel(this.getLevel()+1);
        }
    }
}
