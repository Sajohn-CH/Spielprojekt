package mygame;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Line;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author samuel
 */
public class MGTower extends Tower{
    
    RigidBodyControl towerC;
    private Geometry line;
    private long shot;
    
    public MGTower(Vector3f location, int damage, int range, int shotsPerSecond) {
        this.setPrice(30);
        this.setDamage(damage);
        this.setRange(range);
        this.setShotsPerSecond(shotsPerSecond);
        this.setLocation(location);
        this.setLiving(true);
        this.setLocation(new Vector3f(location.x, 4, location.z));
        Cylinder b = new Cylinder(32, 32, 2, 8, true);
        this.setSpatial(new Geometry("Cylinder", b));
        Quaternion q = new Quaternion();
        q.fromAngleAxis((float) Math.PI/2 , new Vector3f(1,0,0));
        this.getSpatial().setLocalRotation(q);
        Material mat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Cyan);
        this.getSpatial().setMaterial(mat);
        this.getSpatial().setLocalTranslation(this.getLocation());
       
        line = new Geometry("line");
        Material matL = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matL.setColor("Color", ColorRGBA.Red);
        line.setMaterial(matL);
    }
    
    @Override
    public void setCollidable(){
        CollisionShape towerShape = CollisionShapeFactory.createMeshShape(this.getSpatial());
        towerC = new RigidBodyControl(towerShape, 0);
        this.getSpatial().addControl(towerC);
        Main.getBulletAppState().getPhysicsSpace().add(towerC);
    }
    
    @Override
    public void action(float tpf) {
        for(int i = 0; i < Main.getWorld().getAllBombs().size(); i++){
            if(Main.getWorld().getAllBombs().get(i).getSpatial().getLocalTranslation().subtract(this.getSpatial().getLocalTranslation()).length() <= this.getRange() && isLiving() && canShoot()){
               Line l = new Line(this.getSpatial().getLocalTranslation().setY(7), Main.getWorld().getAllBombs().get(i).getSpatial().getLocalTranslation());
               line.setMesh(l);
               Main.app.getRootNode().attachChild(line);
               super.shot();
               this.makeDamage(Main.getWorld().getAllBombs().get(i));
            }
        }
        if(!this.isLiving()){
            Main.getBulletAppState().getPhysicsSpace().remove(towerC);
        }
        if(System.currentTimeMillis()-getLastShot() >= 50){
            line.removeFromParent();
        }
    }
}
