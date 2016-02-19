package mygame.Entitys;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;
import mygame.Main;

/**
 * Turm, der Schaden zufügt. Erstellt und kontrolliert einen Turm, der Schaden zufügt.
 * @author Florian Wenk
 */
public class SimpleTower extends Tower{
    
    private RigidBodyControl towerC;    //Wird benötigt um den Turm kollidierbar zu machen
    private Geometry line;              //Schusslinie
    
    /**
     * Initialisiert den Turm. Setzt Grundattribute, ladet das Modell und erstellt die Schusslinie.
     * @param location Ort
     */
    public SimpleTower (Vector3f location){
        super();
        this.setName("Zerstörender Turm");
        this.setPrice(100);
        this.increaseTotalPaidMoney(this.getPrice());
        this.setLevel(1);
        this.setLocation(location);
        this.setLiving(true);
        //Modell von: http://www.blendswap.com/blends/view/2611 (User: RH2) 
        //Bearbeitet von: Florian Wenk
        Spatial s = Main.app.getAssetManager().loadModel("Objects/SimpleTower.j3o").scale(2f, 2f, 2f);
        s.setName("Tower");
        this.getNode().attachChild(s);
        this.getSpatial().setName("SimpleTower");
        
        // Licht hinzufügen
        PointLight light1 = new PointLight();
        light1.setPosition(new Vector3f(location.x ,20, location.z));
        light1.setRadius(100f);
        s.addLight(light1);
        
        PointLight light2 = new PointLight();
        light2.setPosition(new Vector3f(location.x +1,2, location.z-1));
        light2.setRadius(100f);
        s.addLight(light2);
        
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
        towerC.setKinematic(true);
        Main.getBulletAppState().getPhysicsSpace().add(towerC);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void action(float tpf) {
        Bomb bomb = this.getBombToShootAt();
        if(bomb != null && bomb.getSpatial().getLocalTranslation().subtract(this.getSpatial().getLocalTranslation()).length() <= this.getRange() && isLiving() && canShoot()){
           this.getSpatial().lookAt(bomb.getSpatial().getLocalTranslation().add(Main.getWorld().getBombNode().getLocalTranslation()).setY(0), new Vector3f(0,1,0));
           Line l = new Line(this.getSpatial().getLocalTranslation().add(0, 3, 0), bomb.getSpatial().getLocalTranslation());
           line.setMesh(l);
           Main.app.getRootNode().attachChild(line);
           super.shot();
           this.makeDamage(bomb);
        }
        if(!this.isLiving() && !this.isDead()){
            died();
            Main.getBulletAppState().getPhysicsSpace().remove(towerC);
            line.removeFromParent();
        }
        if(System.currentTimeMillis()-getLastShot() >= 50){
            line.removeFromParent();
        }
        if(this.getHealthPercentage() <= 20 && !this.lowHealthSignIsVisble()){
            this.showLowHealthSign();
        } else if (this.getHealthPercentage() > 20 && this.lowHealthSignIsVisble()){
            this.hideLowHealthSign();
        }
        this.getNode().getChild("levelNumber").lookAt(Main.app.getWorld().getPlayer().getLocation().setY(this.getNode().getChild("levelNumber").getLocalTranslation().getY()), new Vector3f (0, 1, 0));
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
        this.setLevelNumberSpatial(newLevel, 6);
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
    public int getNewDamage(int newLevel) {
        return 25+newLevel*25;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public int getNewHealth(int newLevel) {
        return 50+newLevel*10;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public double getNewSPS(int newLevel) {
        return 0.5+newLevel*0.5;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public int getUpgradePrice() {
        return this.getMaxHealth()*(this.getLevel()/10+1);
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
           this.increaseTotalPaidMoney(getUpgradePrice());
           Main.app.getWorld().getPlayer().increaseMoney(-getUpgradePrice());
           setLevel(this.getLevel()+1);
           Main.app.getWorld().getPlayer().playAudioBought();
         } else {
           Main.app.getWorld().getPlayer().playAudioNotEnoughMoney();
         }
    }
}
