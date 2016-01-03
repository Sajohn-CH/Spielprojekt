/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.Entitys;

import mygame.Entitys.Tower;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.InputListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Line;
import mygame.Main;

/**
 *
 * @author florianwenk
 */
public class Player extends Entity{
    
    private CharacterControl player;
    private Vector3f walkDirection = new Vector3f();
    private boolean left = false, right = false, up = false, down = false, shoot = false;
    private Vector3f camDir = new Vector3f();
    private Vector3f camLeft = new Vector3f();
    private InputListener inputListener;
    private Geometry line;
    private long shot;
    private boolean isShooting = false;
    private int money;
    private boolean isHealing = false;
    
    private double shotsPerSecond = 50;
    private int range = 100;
    
    public Player(InputListener inputListener){
        money = 250;
        this.setLiving(true);
        this.inputListener = inputListener;
        setUpKeys();
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 6f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);
        player.setJumpSpeed(20);
        player.setFallSpeed(90);
        player.setGravity(30);
        player.setPhysicsLocation(new Vector3f(10, 10, 10));
        this.setLiving(true);
        this.setDamage(2);
        setHealth(this.maxHealth);
        this.setSpeed(100);
        Main.getBulletAppState().getPhysicsSpace().add(player);
        
        line = new Geometry("line", new Line());
        Material mat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        line.setMaterial(mat);
        
        this.setSpatial(Main.app.getAssetManager().loadModel("Objects/Gun.j3o").scale(.2f));
        this.getSpatial().setLocalTranslation(Main.app.getCamera().getLocation().add(0, -1.75f, 0).add(Main.app.getCamera().getDirection().normalize().mult(1.5f)));
        PointLight light = new PointLight();
        light.setPosition(new Vector3f(0, 1000, 0));
        light.setRadius(100000);
        this.getSpatial().addLight(light);
        Main.app.getRootNode().attachChild(this.getSpatial());
        
    }
    
    private void setUpKeys() {
        //Tasten um Spieler zu Steuern
        Main.app.getInputManager().addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        Main.app.getInputManager().addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        Main.app.getInputManager().addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        Main.app.getInputManager().addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        Main.app.getInputManager().addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        Main.app.getInputManager().addMapping("Shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        Main.app.getInputManager().addMapping("placeTower", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        Main.app.getInputManager().addListener(this.inputListener, "Left");
        Main.app.getInputManager().addListener(this.inputListener, "Right");
        Main.app.getInputManager().addListener(this.inputListener, "Up");
        Main.app.getInputManager().addListener(this.inputListener, "Down");
        Main.app.getInputManager().addListener(this.inputListener, "Jump");
        Main.app.getInputManager().addListener(this.inputListener, "Shoot");
        Main.app.getInputManager().addListener(this.inputListener, "placeTower");
    }
    
   public void onAction(String binding, boolean isPressed){
        if (binding.equals("Left")) {
            left = isPressed;
        } else if (binding.equals("Right")) {
            right= isPressed;
        } else if (binding.equals("Up")) {
            up = isPressed;
        } else if (binding.equals("Down")) {
            down = isPressed;
        } else if (binding.equals("Shoot") && this.isLiving()) {
            if(isPressed) {
                isShooting = true;
                Main.app.getRootNode().attachChild(line);
            }
            if(!isPressed){
                isShooting = false;
                line.removeFromParent();
            }
        } else if (binding.equals("placeTower") && this.isLiving()) {
            //if(!isPressed) verhindert, dass die Methode zweimal ausgeführt wird
            if(isPressed && Main.app.getHudState().getSelectedItemNum() == 5) {
                isHealing = true;
            }
            if(!isPressed) {
                  if(Main.app.getHudState().getSelectedItemNum()==4) {
                      this.upgradeObject();
                  } else if(Main.app.getHudState().getSelectedItemNum() == 5) {
                    isHealing = false;
                  } else {
                    this.placeTower();  
                  }
            }
        } else if (binding.equals("Jump")) {
          if (isPressed) {
              player.jump();
          }
        }
    }
   
   private boolean canShoot(){
       if(System.currentTimeMillis()-shot >= 1000/shotsPerSecond){
           shot = 0;
           return true;
       }
       return false;
   }
    
    @Override
    public void action(float tpf){
        camDir.set(Main.app.getCamera().getDirection()).multLocal(0.6f);
        camLeft.set(Main.app.getCamera().getLeft()).multLocal(0.4f);
        //Versucht y-Komponente zu entferen, damit man nicht nach oben/unten gehen kann
        camDir.setY(0);
        camLeft.setY(0);
        walkDirection.set(0, 0, 0);
        if (left) {
            walkDirection.addLocal(camLeft);
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (up) {
            walkDirection.addLocal(camDir);
        }
        if (down) {
            walkDirection.addLocal(camDir.negate());
        }
        walkDirection.normalizeLocal();
        walkDirection.multLocal(this.getSpeed() * tpf);
        player.setWalkDirection(walkDirection);
        Main.app.getCamera().setLocation(player.getPhysicsLocation());
        
        this.getSpatial().setLocalTranslation(Main.app.getCamera().getLocation().add(Main.app.getCamera().getUp().normalize().mult(-1.75f)).add(Main.app.getCamera().getLeft().normalize().mult(-.75f)).add(Main.app.getCamera().getDirection().normalize().mult(1.9f)));
        this.getSpatial().lookAt(Main.app.getCamera().getDirection().mult(range).add(Main.app.getCamera().getLocation()), new Vector3f(0,1,0));
        
        if(isHealing) {
            heal();
        }
        if(isShooting){
            shoot();
        }
        if(!isLiving() && (isHealing || isShooting)){
            isHealing = false;
            isShooting = false;
            line.removeFromParent();
        }
        if(!isLiving() && this.getSpatial().hasAncestor(Main.app.getRootNode())){
            this.getSpatial().removeFromParent();
        }
    }
    
    private void makeDamage(Entity e){
        e.increaseHealth(-this.getDamage());
    }
    
    private void shoot(){
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(Main.app.getCamera().getLocation(), Main.app.getCamera().getDirection());
        
        //Schusslinie generieren
        Line l = new Line(this.getSpatial().getLocalTranslation().add(Main.app.getCamera().getUp().normalize().mult(1.3f)), Main.app.getCamera().getLocation().add(Main.app.getCamera().getDirection().normalize().mult(range)));
        line.setMesh(l);
        
        //Prüft, ob eine Bombe getroffen wurde
        Main.getWorld().getBombNode().collideWith(ray, results);
        if (canShoot() && results.size() > 0) {
            CollisionResult closest = results.getClosestCollision();
            l = new Line(this.getSpatial().getLocalTranslation().add(Main.app.getCamera().getUp().normalize().mult(1.3f)), closest.getContactPoint());
            line.setMesh(l);
            if(closest.getContactPoint().add(Main.getWorld().getBombNode().getLocalTranslation()).subtract(Main.app.getCamera().getLocation()).length() <= range){
                makeDamage(Main.getWorld().getBomb(closest.getGeometry().getParent()));
            }
            shot = System.currentTimeMillis();
        }
    }
    
    private void placeTower(){
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(Main.app.getCamera().getLocation(), Main.app.getCamera().getDirection());
        // trifft auf scene
        Main.getWorld().getScene().collideWith(ray, results);
        if (results.size() > 0) {
            // kontrolliert ob kollision mit Beacon
            CollisionResults resultsBeacon = new CollisionResults();
            Main.getWorld().getBeacon().getSpatial().collideWith(ray, resultsBeacon);
            if(resultsBeacon.size() > 0){
                // Zu nahe an beacon -> nicht setzen
                return;
            }
            CollisionResults resultsWay = new CollisionResults();
            Main.getWorld().getWayNode().collideWith(ray, resultsWay);
            if(resultsWay.size() != 0){
                return;
            }
            CollisionResult closest = results.getClosestCollision();
            Vector3f v = closest.getContactPoint();
            v = v.setY(v.getY()+4);
            Tower tower = Main.app.getHudState().getSelectedTower(v);
            //kontrolliert ob Spieler genug Geld hat
            if(getMoney()-tower.getPrice() < 0) {
                //Nicht genug Geld -> Turm wird nicht gesetzt.
                return;
            }
            v = v.setY(v.getY()-4);
            Tower nearest = Main.getWorld().getNearestTower(v);
            // konntrolliert ob Distanz zum nächsten Turm genügend gross ist
            if(nearest != null && nearest.getLocation().subtract(v).length() < 10){
                // Zu nahe an einem anderen Turm -> Turm wird nicht gesetzt
                return;
            }
            if(v.subtract(Main.app.getCamera().getLocation()).length() < 15){
                return;
            }
            //Zieht Geld 
            increaseMoney(-tower.getPrice());
            // plaziert Turm
            Main.getWorld().addTower(tower);
        }
    }
    
    private void heal() {
        CollisionResults resultsTower = new CollisionResults();
        CollisionResults resultsBeacon = new CollisionResults();
        Ray ray = new Ray(Main.app.getCamera().getLocation(), Main.app.getCamera().getDirection());
        Main.getWorld().getTowerNode().collideWith(ray, resultsTower);
        Main.getWorld().getBeacon().getSpatial().collideWith(ray, resultsBeacon);
        if(resultsTower.size() == 0 && resultsBeacon.size() == 0) {
            //Es wird auf nichts gezeigt -> Spieler heilen
            if(this.getHealth() < this.getMaxHealth() && this.getMoney() > 0) {
                this.increaseMoney(-1);
                this.increaseHealth(1);
            } else {
                isHealing = false;
            }                
        } else if(resultsTower.size() == 0) {
            if(resultsBeacon.size() != 0){
                Beacon beacon = Main.getWorld().getBeacon();
                    if(beacon.getHealth() < beacon.getMaxHealth() && this.getMoney() > 0) {
                        beacon.setHealth(beacon.getHealth()+1);
                        this.increaseMoney(-1);
                    } else {
                        isHealing = false;
                    }
            }
        } else {
            //Es gibt min. einen Turm. Es wird der nächste geholt
            Vector3f pointTower = resultsTower.getClosestCollision().getContactPoint();
            Tower nearestTower = Main.app.getWorld().getNearestTower(pointTower);
            if(resultsBeacon.size() > 0) {
                //Es gibt Türme und Beacon mit Kollision -> Hearusfinden welcher näher ist
                Vector3f pointBeacon = resultsBeacon.getClosestCollision().getContactPoint();
                if(nearestTower.getLocation().subtract(pointTower).length() > Main.app.getWorld().getBeacon().getLocation().subtract(pointBeacon).length()) {
                    //Beacon ist näher
                    Beacon beacon = Main.getWorld().getBeacon();
                    if(beacon.getHealth() < beacon.getMaxHealth() && this.getMoney() > 0) {
                        beacon.setHealth(beacon.getHealth()+1);
                        this.increaseMoney(-1);
                    } else {
                        isHealing = false;
                    }
                } else {
                    //ein Turm heilen
                    if(nearestTower.getHealth() < nearestTower.getMaxHealth() && this.getMoney() > 0) {
                        nearestTower.setHealth(nearestTower.getHealth()+1);
                        this.increaseMoney(-1);
                    } else {
                        isHealing = false;
                    }
                }
            } else {
                //ein Turm heilen
                if(nearestTower.getHealth() < nearestTower.getMaxHealth() && this.getMoney() > 0) {
                    nearestTower.setHealth(nearestTower.getHealth()+1);
                    this.increaseMoney(-1);
                } else {
                    isHealing = false;
                }
            }
        }
    }
    
    public void upgradeObject() {
        CollisionResults resultsTower = new CollisionResults();
        CollisionResults resultsBeacon = new CollisionResults();
        Ray ray = new Ray(Main.app.getCamera().getLocation(), Main.app.getCamera().getDirection());
        Main.getWorld().getTowerNode().collideWith(ray, resultsTower);
        Main.getWorld().getBeacon().getSpatial().collideWith(ray, resultsBeacon);
        if(resultsTower.size() == 0) {
            if(resultsBeacon.size() != 0){
                Main.getWorld().getBeacon().increaseLevel();
            }
        } else {
            //Es gibt min. einen Turm. Es wird der nächste geholt
            Vector3f pointTower = resultsTower.getClosestCollision().getContactPoint();
            Tower nearestTower = Main.app.getWorld().getNearestTower(pointTower);
            if(resultsBeacon.size() > 0) {
                //Es gibt Türme und Beacon mit Kollision -> Hearusfinden welcher näher ist
                Vector3f pointBeacon = resultsBeacon.getClosestCollision().getContactPoint();
                if(nearestTower.getLocation().subtract(pointTower).length() > Main.app.getWorld().getBeacon().getLocation().subtract(pointBeacon).length()) {
                    //Beacon ist näher
                    Main.getWorld().getBeacon().increaseLevel();
                } else {
                    //Ein Turm upgraden
                   nearestTower.increaseLevel();
                   
                }
            } else {
               //Ein Turm upgraden
               nearestTower.increaseLevel();
            }
        }
    }
    
    public CharacterControl getCharacterControl(){
        return player;
    }
    
    public int getMoney(){
        return money;
    }

    public double getSPS() {
        return shotsPerSecond;
    }

    public int getRange() {
        return range;
    }

    public void setShotsPerSecond(double shotsPerSecond) {
        this.shotsPerSecond = shotsPerSecond;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public void setMoney(int money) {
        this.money = money;
    }
    
    public void increaseMoney(int money){
        this.money += money;
    }
    
    public int getNewRange(){
        return (int)(range * 1.1);
    }
    
    public void increaseRange(){
        if(this.getMoney() >= this.getNewRangePrice()) {
           this.increaseMoney(-this.getNewRangePrice());
           range = getNewRange();
        }
    }
    
    public int getNewRangePrice(){
        return (int) (getNewRange()*0.75);
    }
    
    public int getNewDamage(){
        return (int)(this.getDamage() + Math.sqrt(this.getDamage()));
    }
    
    public void increaseDamage(){
        if(this.getMoney() >= this.getNewDamagePrice()) {
           this.increaseMoney(-this.getNewDamagePrice());
           this.setDamage(getNewDamage()); 
        }
    }
    
    public int getNewDamagePrice(){
        return getNewDamage()*50;
    }
    
    public double getNewSPS(){   
        return (int) shotsPerSecond + Math.sqrt(shotsPerSecond);
    }
    
    public void increaseSPS(){
        if(this.getMoney() >= this.getNewSPSPrice()) {
            this.increaseMoney(-this.getNewSPSPrice());
            shotsPerSecond = getNewSPS();
        }
        
    }
    
    public int getNewSPSPrice(){
        return (int) (getNewSPS()*10);
    }
    
    public int getNewSpeed(){
        return (int) (this.getSpeed()*1.05);
    }
    
    public int getNewSpeedPrice() {
        return (int) (getNewSpeed()*1.5);
    }
    
    public void increaseSpeed(){
        if(this.getMoney() >= this.getNewSpeedPrice()) {
         this.increaseMoney(-this.getNewSpeedPrice());
         this.setSpeed(getNewSpeed());   
        }
    }
    
    public int getNewMaxHealth() {
        return (int)(this.maxHealth*1.1);
    }
    
    public int getNewMaxHealthPrice() {
        return (int)(getNewMaxHealth()*1.5);
    }
    
    public void increaseMaxHealth() {
        if(this.getMoney() >= this.getNewMaxHealthPrice()) {
            this.increaseMoney(-this.getNewMaxHealthPrice());
            this.setHealth(this.getNewMaxHealth()-this.getHealth());
            this.setMaxHealth(this.getNewMaxHealth());
        }
    }
    
    @Override
    public Vector3f getLocation() {
        return Main.app.getCamera().getLocation();
    }
    
    @Override 
    public void setLocation(Vector3f location) {
        player.setPhysicsLocation(location);
        Main.app.getCamera().setLocation(location);
    }
}
