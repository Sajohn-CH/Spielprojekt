/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.Entitys;

import mygame.Entitys.Tower;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Line;
import mygame.Main;

/**
 *
 * @author florianwenk
 */
public class SimpleTower extends Tower{
    
    RigidBodyControl towerC;
    private Geometry line;
    
    public SimpleTower (Vector3f location){
        this.setPrice(20);
        this.setLevel(1);
        this.setLocation(location);
        this.setLiving(true);
        this.setLocation(new Vector3f(location.x, 0, location.z));
        // Modell laden
        this.setSpatial(Main.app.getAssetManager().loadModel("Objects/SimpleTower.j3o").scale(2f, 2f, 2f));
        
        // Licht hinzufügen
        PointLight light1 = new PointLight();
        light1.setPosition(new Vector3f(location.x ,20, location.z));
        light1.setRadius(100f);
        this.getSpatial().addLight(light1);
        
        PointLight light2 = new PointLight();
        light2.setPosition(new Vector3f(location.x +1,2, location.z-1));
        light2.setRadius(100f);
        this.getSpatial().addLight(light2);
        
//        Box b = new Box(2, 8, 2);
//        this.setSpatial(new Geometry("SimpleTower", b));
//        
//        Material mat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
//        mat.setColor("Color", ColorRGBA.Yellow);
//        
//        this.getSpatial().setMaterial(mat);
////        this.setSpatial(Main.app.getAssetManager().loadModel("Objects/SimpleTower/SimpleTower.j3o").scale(3f));
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
            if(bomb.getSpatial().getLocalTranslation().subtract(this.getSpatial().getLocalTranslation()).length() <= this.getRange() && this.canShoot()){
                this.getSpatial().lookAt(bomb.getSpatial().getLocalTranslation().add(Main.getWorld().getBombNode().getLocalTranslation()).setY(0), new Vector3f(0,1,0));
            }
            if(bomb.getSpatial().getLocalTranslation().subtract(this.getSpatial().getLocalTranslation()).length() <= this.getRange() && isLiving() && canShoot()){
               this.getSpatial().lookAt(bomb.getSpatial().getLocalTranslation().add(Main.getWorld().getBombNode().getLocalTranslation()).setY(0), new Vector3f(0,1,0));
               Line l = new Line(this.getSpatial().getLocalTranslation().add(0, 3, 0), bomb.getSpatial().getLocalTranslation());
               line.setMesh(l);
               Main.app.getRootNode().attachChild(line);
               super.shot();
               this.makeDamage(Main.getWorld().getAllBombs().get(i));
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
  
    /**
     * Setzt das Level des Turms auf den übergebenen Wert. Dies erhört die Reichweite, den Schaden, die Lebenspunkte und die Schussrate des Turmes.
     * @param newLevel  neues Level 
     */
    @Override
    public void setLevel(int newLevel) {
        super.setLevel(newLevel);
        this.setRange(getNewRange(newLevel));
        this.setDamage(getNewDamage(newLevel));
        this.setHealth(getNewHealth(newLevel));
        this.setMaxHealth(getNewHealth(newLevel));
        this.setShotsPerSecond(getNewSPS(newLevel));
    }
    
    @Override
    public int getNewRange(int newLevel) {
        return 10+newLevel*5;
    }
    
    @Override
    public int getNewDamage(int newLevel) {
        return 25+newLevel*25;
    }
    
    @Override
    public int getNewHealth(int newLevel) {
        return 50+newLevel*10;
    }
    
    @Override
    public double getNewSPS(int newLevel) {
        return 0.5+newLevel*0.5;
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
           Main.app.getWorld().getPlayer().playAudioBought();
         } else {
           Main.app.getWorld().getPlayer().playAudioNotEnoughMoney();
         }
    }
}
