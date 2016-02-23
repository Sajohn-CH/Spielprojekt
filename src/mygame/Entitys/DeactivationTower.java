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
 * Turm, der Schiessende Bomben in normale umwandelt. Erstellt einen Turm der Schiessenden Bomben die Fähigkeit zu schiessen entzieht und kontrolliert diesen.
 * @author Florian Wenk
 */
public class DeactivationTower extends Tower{
      
    private RigidBodyControl towerC;    //Wird benötigt um den Turm kollidierbar zu machen
    private Geometry line;              //Die Schusslinie des Turmes
    
    /**
     * Erstellt den Tower. Setzt wichtige Attribute des Turmes, ladet das Modell und erstellt die Schusslinie.
     * @param location 
     */
    public DeactivationTower (Vector3f location, Vector3f up){
        super();
        this.setName("Deaktivierender Turm");
        this.setPrice(200);
        this.increaseTotalPaidMoney(this.getPrice());
        this.setUp(up);
        this.setLevel(1);
        this.setLocation(location);
        this.setLiving(true);
        super.setShootAt(true, false, false, false);
        super.setShootOnlyAtShootingBombs();
        
        //Modell von: http://www.blendswap.com/blends/view/77840 (User: thanhkhmt1)
        //Bearbeitet von: Florian Wenk
        Spatial s = Main.app.getAssetManager().loadModel("Objects/PyramidTower.j3o").scale(0.5f).rotate(0, (float) Math.toRadians(90), 0);
        s.setName("Tower");
        this.getNode().attachChild(s);
        this.getSpatial().setName("DeactivationTower");
        
        PointLight light1 = new PointLight();
        light1.setPosition(new Vector3f(location.x ,location.y + 100, location.z));
        light1.setRadius(1000f);
        this.getSpatial().addLight(light1);
        
        PointLight light2 = new PointLight();
        light2.setPosition(new Vector3f(location.x ,location.y + 1, location.z +100));
        light2.setRadius(1000f);
        this.getSpatial().addLight(light2);
        
        PointLight light3 = new PointLight();
        light3.setPosition(new Vector3f(location.x,location.y + 1, location.z-100));
        light3.setRadius(1000f);
        this.getSpatial().addLight(light3);
        
        PointLight light4 = new PointLight();
        light4.setPosition(new Vector3f(location.x +100, location.y + 1, location.z));
        light4.setRadius(1000f);
        this.getSpatial().addLight(light4);
        
        PointLight light5 = new PointLight();
        light5.setPosition(new Vector3f(location.x -100, location.y + 1, location.z));
        light5.setRadius(1000f);
        this.getSpatial().addLight(light5);
        
        this.getSpatial().setLocalTranslation(this.getLocation());
        
        line = new Geometry("line");
        Material matL = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matL.setColor("Color", ColorRGBA.Magenta);
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
        ShootingBomb bomb = (ShootingBomb) super.getBombToShootAt();
        if(bomb != null && bomb.getSpatial().getLocalTranslation().subtract(this.getSpatial().getLocalTranslation()).length() <= this.getRange() && this.canShoot()){
            if(bomb.isShooting()){
                this.getSpatial().lookAt(bomb.getSpatial().getLocalTranslation().add(Main.getWorld().getBombNode().getLocalTranslation()).setY(this.getSpatial().getLocalTranslation().getY()), this.getUp());
                this.getSpatial().rotateUpTo(this.getUp());
                this.disableShooting(bomb);
                Line l = new Line(this.getSpatial().getLocalTranslation().add(this.getUp().normalize().mult(4)), bomb.getSpatial().getLocalTranslation());
                line.setMesh(l);
                Main.app.getRootNode().attachChild(line);
                super.shot();
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
        if(this.getHealthPercentage() <= 20 && !this.lowHealthSignIsVisble()){
            this.showLowHealthSign();
        } else if (this.getHealthPercentage() > 20 && this.lowHealthSignIsVisble()){
            this.hideLowHealthSign();
        }
        this.getNode().getChild("levelNumber").lookAt(Main.app.getWorld().getPlayer().getLocation().setY(this.getNode().getChild("levelNumber").getLocalTranslation().add(this.getSpatial().getLocalTranslation()).getY()), new Vector3f (0, 1, 0));
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
        return this.getMaxHealth()*(this.getLevel()/10+1);
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
    @Override
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
