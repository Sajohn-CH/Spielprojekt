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
import com.jme3.scene.shape.Line;
import mygame.Main;

/**
 * Turm, der Bomben verlangsamt. Erstellt und kontrolliert einen Turm, der Bomben verlangsamt.
 * @author Florian Wenk
 */
public class SloweringTower extends Tower{
    
    private RigidBodyControl towerC;    //Wird benötigt um den Turm kollidierbar zu machen
    private Geometry line;              //Schusslinie
    
    /**
     * Initialisierung des Turmes. Setzt Grundattribute des Turmes, ladet das Modell und erstellt die Schusslinie.
     * @param location Ort
     */
    public SloweringTower(Vector3f location, Vector3f up) {
        super();
        this.setName("Verlangsamender Turm");
        this.setPrice(150);
        this.increaseTotalPaidMoney(this.getPrice());
        this.setUp(up);
        this.setLevel(1);
        this.setLocation(location);
        this.setLiving(true);
        
        //Modell von: http://www.blendswap.com/blends/view/71401 (User: zagony)
        //Bearbeitet von: Florian Wenk
        Spatial s = Main.app.getAssetManager().loadModel("Objects/MGTower.j3o");
        s.setName("Tower");
        this.getNode().attachChild(s);
        this.getSpatial().setName("SloweringTower");
        
        PointLight light1 = new PointLight();
        light1.setPosition(new Vector3f(location.x +10 ,location.y + 20, location.z));
        light1.setRadius(10000);
        PointLight light2 = new PointLight();
        light2.setPosition(new Vector3f(location.x -10 ,location.y + 5, location.z));
        light2.setRadius(10000);
        PointLight light3 = new PointLight();
        light3.setPosition(new Vector3f(location.x ,location.y + 20, location.z +10));
        light3.setRadius(10000);
        PointLight light4 = new PointLight();
        light4.setPosition(new Vector3f(location.x ,location.y + 5, location.z -10));
        light4.setRadius(10000);
        
        this.getSpatial().addLight(light1);
        this.getSpatial().addLight(light2);
        this.getSpatial().addLight(light3);
        this.getSpatial().addLight(light4);
                
        this.getSpatial().setLocalTranslation(this.getLocation());
       
        line = new Geometry("line");
        Material matL = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matL.setColor("Color", ColorRGBA.Blue);
        line.setMaterial(matL);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void setCollidable(){
        CollisionShape towerShape = CollisionShapeFactory.createBoxShape(this.getSpatial());
        towerC = new RigidBodyControl(towerShape, 0);
        this.getSpatial().addControl(towerC);
        towerC.setKinematic(true);
        Main.getBulletAppState().getPhysicsSpace().add(towerC);
    }
    
    /**
     * Verlangsamt eine Bombe.
     * @param b Bombe, die verlangsamt werden soll.
     */
    private void decreaseSpeed(Bomb b){
        b.decreaseSpeed(this.getDamage());
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void action(float tpf) {
        Bomb bomb = super.getBombToShootAt();
        if(bomb != null && bomb.getSpatial().getLocalTranslation().subtract(this.getSpatial().getLocalTranslation()).length() <= this.getRange() && isLiving() && canShoot() && bomb.getDecreasedSpeed() < bomb.getSpeed()-10-getLevel()*2){
           this.getSpatial().lookAt(bomb.getSpatial().getLocalTranslation().add(Main.getWorld().getBombNode().getLocalTranslation()).setY(this.getSpatial().getLocalTranslation().getY()), this.getUp());
           this.getSpatial().rotateUpTo(this.getUp());
           Vector3f vec = bomb.getSpatial().getLocalTranslation().subtract(this.getSpatial().getLocalTranslation()).normalize().mult(3.15f).add(this.getUp().normalize().mult(5.925f));
           Line l = new Line(this.getSpatial().getLocalTranslation().add(vec), bomb.getSpatial().getLocalTranslation());
           line.setMesh(l);
           Main.app.getRootNode().attachChild(line);
           super.shot();
           this.decreaseSpeed(bomb);
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
        this.getNode().getChild("levelNumber").lookAt(Main.app.getWorld().getPlayer().getLocation().setY(this.getNode().getChild("levelNumber").getLocalTranslation().add(this.getSpatial().getLocalTranslation()).getY()), new Vector3f (0, 1, 0));
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void setLevel(int newLevel) {
        super.setLevel(newLevel);
        this.setRange(getNewRange(newLevel));
        this.setDamage(getNewDamage(newLevel));
        this.setHealth(getNewHealth(newLevel));
        this.setMaxHealth(getNewHealth(newLevel));
        this.setShotsPerSecond(getNewSPS(newLevel));
        this.setLevelNumberSpatial(newLevel, 8);
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
        return 5+newLevel*5;
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
        return this.getMaxHealth()*(int)(this.getLevel()/10+1);
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