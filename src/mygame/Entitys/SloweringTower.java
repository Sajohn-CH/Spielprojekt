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
 * Turm, der Bomben verlangsamt. Erstellt und kontrolliert einen Turm, der Bomben verlangsamt.
 * @author Florian Wenk
 */
public class SloweringTower extends Tower{
    
    RigidBodyControl towerC;
    private Geometry line;
    
    /**
     * Initialisierung des Turmes. Setzt Grundattribute des Turmes, ladet das Modell und erstellt die Schusslinie.
     * @param location Ort
     */
    public SloweringTower(Vector3f location) {
        this.setName("Verlangsamender Turm");
        this.setPrice(30);
        this.increaseTotalPaidMoney(this.getPrice());
        this.setLevel(1);
        this.setLocation(location);
        this.setLiving(true);
        this.setLocation(new Vector3f(location.x, 0, location.z));
        
        //Modell von: http://www.blendswap.com/blends/view/71401 (User: zagony)
        //Bearbeitet von: Florian Wenk
        this.setSpatial(Main.app.getAssetManager().loadModel("Objects/MGTower.j3o"));
        
        PointLight light1 = new PointLight();
        light1.setPosition(new Vector3f(location.x +10 ,20, location.z));
        light1.setRadius(10000);
        PointLight light2 = new PointLight();
        light2.setPosition(new Vector3f(location.x -10 ,5, location.z));
        light2.setRadius(10000);
        PointLight light3 = new PointLight();
        light3.setPosition(new Vector3f(location.x ,20, location.z +10));
        light3.setRadius(10000);
        PointLight light4 = new PointLight();
        light4.setPosition(new Vector3f(location.x ,5, location.z -10));
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
        for(int i = 0; i < Main.getWorld().getAllBombs().size(); i++){
            Bomb bomb = Main.getWorld().getAllBombs().get(i);
            if(bomb.getSpatial().getLocalTranslation().subtract(this.getSpatial().getLocalTranslation()).length() <= this.getRange() && this.canShoot()){
                this.getSpatial().lookAt(bomb.getSpatial().getLocalTranslation().add(Main.getWorld().getBombNode().getLocalTranslation()).setY(0), new Vector3f(0,1,0));
            }
            if(bomb.getSpatial().getLocalTranslation().subtract(this.getSpatial().getLocalTranslation()).length() <= this.getRange() && isLiving() && canShoot() && bomb.getDecreasedSpeed() < bomb.getSpeed()-1){
               Vector3f vec = bomb.getSpatial().getLocalTranslation().subtract(this.getSpatial().getLocalTranslation()).normalize().mult(3.15f).setY(5.925f);
               Line l = new Line(this.getSpatial().getLocalTranslation().add(vec), bomb.getSpatial().getLocalTranslation());
               line.setMesh(l);
               Main.app.getRootNode().attachChild(line);
               super.shot();
               this.decreaseSpeed(Main.getWorld().getAllBombs().get(i));
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
           this.increaseTotalPaidMoney(getUpgradePrice());
           Main.app.getWorld().getPlayer().increaseMoney(-getUpgradePrice());
           setLevel(this.getLevel()+1);
           Main.app.getWorld().getPlayer().playAudioBought();
         } else {
           Main.app.getWorld().getPlayer().playAudioNotEnoughMoney();
         }
    }
}