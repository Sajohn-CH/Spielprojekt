/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.InputListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Line;

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
    private int money;
    
    public Player(InputListener inputListener){
        money = 100;
        this.setLiving(true);
        this.inputListener = inputListener;
        setUpKeys();
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 6f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);
        player.setJumpSpeed(20);
        player.setFallSpeed(90);
        player.setGravity(30);
        player.setPhysicsLocation(new Vector3f(0, 10, 0));
        this.setLiving(true);
        this.setDamage(0);
        setHealth(this.maxHealth);
        this.setSpeed(50);
        Main.getBulletAppState().getPhysicsSpace().add(player);
        
        line = new Geometry("line");
        Material mat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        line.setMaterial(mat);
        
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
            this.shoot();
        } else if (binding.equals("placeTower") && this.isLiving()) {
            this.placeTower();
        } else if (binding.equals("Jump")) {
          if (isPressed) {player.jump(); }
        }
    }
    
    @Override
    public void action(float tpf){
        camDir.set(Main.app.getCamera().getDirection()).multLocal(0.6f);
        camLeft.set(Main.app.getCamera().getLeft()).multLocal(0.4f);
        //Versucht y-Komponente zu entferen, damit man nicht nach oben/unten gehen kann
        //camDir.subtractLocal(0, camDir.y, 0);
        //camLeft.subtractLocal(0, camLeft.y, 0);
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
//        walkDirection.normalizeLocal();
//        player.walkDirection.multLocal(player.getSpeed() * tpf);
        player.setWalkDirection(walkDirection);
        Main.app.getCamera().setLocation(player.getPhysicsLocation());
        if(System.currentTimeMillis()-shot >= 50){
            line.removeFromParent();
            shot = 0;
        }
    }
    
    private void makeDamage(Entity e){
        e.increaseHealth(-this.getDamage());
    }
    
    private void shoot(){
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(Main.app.getCamera().getLocation(), Main.app.getCamera().getDirection());
        
        Line l = new Line(Main.app.getCamera().getLocation().subtract(0, 1, 0), Main.app.getCamera().getLocation().add(Main.app.getCamera().getDirection().mult(100)));
        line.setMesh(l);
        Main.app.getRootNode().attachChild(line);
        
        Main.getWorld().getBombNode().collideWith(ray, results);
        if (results.size() > 0) {
          CollisionResult closest = results.getClosestCollision();
            l = new Line(Main.app.getCamera().getLocation().subtract(0, 1, 0), closest.getContactPoint());
            line.setMesh(l);
          if(closest.getGeometry().getName().equals("bomb")){
              makeDamage(Main.getWorld().getBomb(closest.getGeometry()));
        }
      }
        shot = System.currentTimeMillis();
    }
    
    private void placeTower(){
        //ERROR: Being called twice
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
            if(v.subtract(Main.app.getCamera().getLocation()).length() > 4 && v.subtract(Main.app.getCamera().getLocation()).length() > 4){
                //Zieht Geld 
                increaseMoney(-tower.getPrice());
                // plaziert Turm
                Main.getWorld().addTower(tower);
            }
        }
    }
    
    public CharacterControl getCharacterControl(){
        return player;
    }
    
    public int getMoney(){
        return money;
    }
    
    public void increaseMoney(int money){
        this.money += money;
    }
    
}
