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
    
    Node n;
    
    public Bomb (int level){
	this.setLevel(level);
        Sphere sphere = new Sphere(100, 100, 1);
        Spatial s = new Geometry("bomb", sphere);
        Material mat = new Material (Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        s.setMaterial(mat);
        CollisionShape bombShape = CollisionShapeFactory.createMeshShape(s);
        RigidBodyControl bombC = new RigidBodyControl(bombShape, 0);
        s.addControl(bombC);
        n = new Node();
        n.attachChild(s);
        n.addControl(bombC);
        Main.bulletAppState.getPhysicsSpace().add(bombC);
    }
	
    public Bomb(int level, Vector3f location){
            this(level);
            super.setLocation(location);
            n.setLocalTranslation(location);
    }

    @Override
    public void increaseHealth(int health){
        this.increaseHealth(health);
        if(!this.isLiving())
            if(this.getLevel() > 1){
                this.setLevel(this.getLevel()-1);
                this.setLiving(true);
            }
    }
    
    public boolean isAtBeacon(Vector3f beaconPlace){
        return (beaconPlace == this.getLocation());
    }
    
    public void makeDamage(Entity e){
        e.increaseHealth(-this.getDamage());
    }
    
    public void move(Vector3f offset){
        super.setLocation(offset.add(this.getLocation()));
    }
    
    @Override
    public void setLocation(Vector3f location){
        super.setLocation(location);
        n.setLocalTranslation(location);
    }
    
    @Override
    public void update(float tpf) {
        if(this.getLocation() != n.getLocalTranslation()){
            Vector3f v = new Vector3f(this.getLocation().subtract(n.getLocalTranslation()
                    ));
            v.normalizeLocal();
            v.mult(this.getSpeed() * tpf);
            n.move(v);
        }
    }
    
    
}
