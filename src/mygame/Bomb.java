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
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;

/**
 *
 * @author florian.wenk
 */
public class Bomb extends Entity{
    
    Spatial bomb;
    RigidBodyControl bombC;
    
    public Bomb (int level){
        this.setLiving(true);
	this.setLevel(level);
        this.setSpeed(1);
        Sphere sphere = new Sphere(100, 100, 1);
        bomb = new Geometry("bomb", sphere);
        Material mat = new Material (Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        bomb.setMaterial(mat);
        Main.bombs.add(this);
        Main.bombNode.attachChildAt(bomb, Main.bombs.indexOf(this));
        Main.app.getStateManager().attach(this);
        
        CollisionShape bombShape = CollisionShapeFactory.createMeshShape(bomb);
        bombC = new RigidBodyControl(bombShape, 0);
        bombC.setKinematic(true);
        bomb.addControl(bombC);
        Main.bulletAppState.getPhysicsSpace().add(bombC);
    }
	
    public Bomb(int level, Vector3f location){
            this(level);
            super.setLocation(location);
            bomb.setLocalTranslation(location);
    }

    @Override
    public void increaseHealth(int health){
        super.increaseHealth(health);
        if(!this.isLiving())
            if(this.getLevel() > 1){
                this.setLevel(this.getLevel()-1);
                this.setLiving(true);
            }
    }
        
    public void makeDamage(Entity e){
        e.increaseHealth(-this.getDamage());
    }
    
    public void move(Vector3f offset){
        offset.setY(0);
        super.setLocation(offset.add(this.getLocation()));
    }
    
    @Override
    public void setLocation(Vector3f location){
        super.setLocation(location);
        bomb.setLocalTranslation(location);
    }
    
    @Override
    public void update(float tpf) {
        if(this.getLocation().subtract(bomb.getLocalTranslation()).length() > 1){
            Vector3f v = new Vector3f(this.getLocation().subtract(bomb.getLocalTranslation()));
            v.divideLocal(10*v.length());
            v.multLocal(5*tpf);
            bomb.move(v.mult(this.getSpeed()));
        }
        if(Main.app.getCamera().getLocation().subtract(this.bomb.getLocalTranslation()).length() <= 3 && this.isLiving() && Main.player.isLiving()){
                this.makeDamage(Main.player);
                this.setLiving(false);
        }
        if(Main.beacon.beacon.getLocalTranslation().subtract(this.bomb.getLocalTranslation()).length() <= 3 && isLiving()){
                this.makeDamage(Main.beacon);
                this.setLiving(false);
        }
        if(!this.isLiving()){
            this.bomb.removeFromParent();
            Main.bulletAppState.getPhysicsSpace().remove(bombC);
        }
    }
    
    
}
