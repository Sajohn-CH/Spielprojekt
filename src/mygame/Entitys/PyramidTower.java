/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.Entitys;

import mygame.Entitys.Tower;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.light.AmbientLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Dome;
import com.jme3.scene.shape.Line;
import mygame.Main;

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
        
//        Dome b = new Dome(Vector3f.ZERO, 2, 4, 1f,false); 
//        this.setSpatial(new Geometry("PyramidTower", b));
//        this.getSpatial().setLocalScale(2f, 15f, 2f);
//        Material mat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
//        mat.setBoolean("UseMaterialColors",true);
//        mat.setColor("Ambient",ColorRGBA.White);
//        mat.setColor("Diffuse",ColorRGBA.White);
//        mat.setColor("Specular",ColorRGBA.White);
//        this.getSpatial().setMaterial(mat);
        
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
