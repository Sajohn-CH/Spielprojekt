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
    
    RigidBodyControl bombC;
    
    public Bomb (int level){
        this.setLiving(true);
	this.setLevel(level);
        this.setSpeed(1);
        Sphere sphere = new Sphere(100, 100, 1);
        this.setSpatial(new Geometry("bomb", sphere));
        Material mat = new Material (Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        this.getSpatial().setMaterial(mat);
        
        CollisionShape bombShape = CollisionShapeFactory.createMeshShape(this.getSpatial());
        bombC = new RigidBodyControl(bombShape, 0);
        bombC.setKinematic(true);
        this.getSpatial().addControl(bombC);
        Main.getBulletAppState().getPhysicsSpace().add(bombC);
        
        init();
    }
	
    public Bomb(int level, Vector3f location){
            this(level);
            super.setLocation(location);
            this.getSpatial().setLocalTranslation(location);
    }

    private void init(){
        //Main.bombs.add(this);
        //Main.bombNode.attachChildAt(bomb, Main.bombs.indexOf(this));
        //Main.app.getStateManager().attach(this);
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
        this.getSpatial().setLocalTranslation(location);
    }
    
    @Override
    public void action(float tpf) {
        if(this.getLocation().subtract(this.getSpatial().getLocalTranslation()).length() > 1){
            Vector3f v = new Vector3f(this.getLocation().subtract(this.getSpatial().getLocalTranslation()));
            v.divideLocal(10*v.length());
            v.multLocal(5*tpf);
            this.getSpatial().move(v.mult(this.getSpeed()));
        }
        if(Main.app.getCamera().getLocation().subtract(this.getSpatial().getLocalTranslation()).length() <= 3 && this.isLiving() && Main.getWorld().getPlayer().isLiving()){
                this.makeDamage(Main.getWorld().getPlayer());
                this.setLiving(false);
        }
        if(Main.getWorld().getBeacon().getSpatial().getLocalTranslation().subtract(this.getSpatial().getLocalTranslation()).length() <= 3 && isLiving()){
                this.makeDamage(Main.getWorld().getBeacon());
                this.setLiving(false);
        }
        if(!this.isLiving()){
            Main.getBulletAppState().getPhysicsSpace().remove(bombC);
            Main.app.getWorld().getPlayer().increaseMoney(10);
        }
    }
    
    
}
