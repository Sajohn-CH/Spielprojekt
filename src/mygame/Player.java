/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.InputListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;

/**
 *
 * @author florianwenk
 */
public class Player extends Entity{
    
    protected CharacterControl player;
    private Vector3f walkDirection = new Vector3f();
    private boolean left = false, right = false, up = false, down = false, shoot = false;
    private Vector3f camDir = new Vector3f();
    private Vector3f camLeft = new Vector3f();
    private InputListener inputListener;
    private ParticleEmitter fire;
    private static final int COUNT_FACTOR = 1;
    private static final float COUNT_FACTOR_F = 1f;
    private static final boolean POINT_SPRITE = true;
    private static final Type EMITTER_TYPE = POINT_SPRITE ? Type.Point : Type.Triangle;
    
    public Player(InputListener inputListener){
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
        Main.bulletAppState.getPhysicsSpace().add(player);
        
        fire = new ParticleEmitter("Flash", EMITTER_TYPE, 24 * COUNT_FACTOR);
        fire.setSelectRandomImage(true);
        fire.setStartColor(new ColorRGBA(1f, 0.8f, 0.36f, (float) (1f / COUNT_FACTOR_F)));
        fire.setEndColor(new ColorRGBA(1f, 0.8f, 0.36f, 0f));
        fire.setStartSize(.1f);
        fire.setEndSize(1.0f);
        fire.setShape(new EmitterSphereShape(Vector3f.ZERO, .05f));
        fire.setParticlesPerSec(0);
        fire.setGravity(0, 0, 0);
        fire.setLowLife(.2f);
        fire.setHighLife(.2f);
        fire.setInitialVelocity(new Vector3f(0, 1f, 0));
        fire.setVelocityVariation(10);
        fire.setImagesX(2);
        fire.setImagesY(2);
        Material mat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture("Texture", Main.app.getAssetManager().loadTexture("Effects/Explosion/flash.png"));
        mat.setBoolean("PointSprite", POINT_SPRITE);
        fire.setMaterial(mat);
        Main.app.getRootNode().attachChild(fire);
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
    
    public void walk(float tpf, Camera cam){
        camDir.set(cam.getDirection()).multLocal(0.6f);
        camLeft.set(cam.getLeft()).multLocal(0.4f);
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
        cam.setLocation(player.getPhysicsLocation());
    }
    
    private void makeDamage(Entity e){
        e.increaseHealth(-this.getDamage());
    }
    
    private void shoot(){
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(Main.app.getCamera().getLocation(), Main.app.getCamera().getDirection());
        fire.setLocalTranslation(Main.app.getCamera().getLocation().add(ray.direction.normalize().mult(5)));
        fire.getParticleInfluencer().setInitialVelocity(ray.direction.negate());
        fire.emitAllParticles();
        Main.bombNode.collideWith(ray, results);
        if (results.size() > 0) {
          CollisionResult closest = results.getClosestCollision();
          if(closest.getGeometry().getName().equals("bomb")){
              makeDamage(Main.bombs.get(closest.getGeometry().getParent().getChildIndex(closest.getGeometry())));
        }
      }
    }
    
    private void placeTower(){
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(Main.app.getCamera().getLocation(), Main.app.getCamera().getDirection());
        Main.scene.collideWith(ray, results);
        if (results.size() > 0) {
            CollisionResult closest = results.getClosestCollision();
            Vector3f v = closest.getContactPoint();
            v = v.setY(4);
            if(v.subtract(Main.app.getCamera().getLocation()).length() > 4)
            new SimpleTower(v, 50, 10);
        }
    }
    
}
