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
import com.jme3.scene.shape.Sphere;

/**
 *
 * @author florian.wenk
 */
public class Bomb extends Entity{
    
    private RigidBodyControl bombC;
    private Material mat;
    private ColorRGBA[] colors = {ColorRGBA.Blue, ColorRGBA.Cyan, ColorRGBA.Green, ColorRGBA.Magenta, ColorRGBA.Red, ColorRGBA.Pink};
    private Way way;
    
    public Bomb (int level){
        this.setLiving(true);
        this.setSpeed(1);
        Sphere sphere = new Sphere(100, 100, 1);
        this.setSpatial(new Geometry("bomb", sphere));
        mat = new Material (Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        this.getSpatial().setMaterial(mat);
        this.setLevel(level);
        way = new Way();
        super.setLocation(way.getStartPoint().setY(2));
        this.getSpatial().setLocalTranslation(way.getStartPoint());
        
//        CollisionShape bombShape = CollisionShapeFactory.createMeshShape(this.getSpatial());
//        bombC = new RigidBodyControl(bombShape, 0);
//        bombC.setKinematic(true);
//        this.getSpatial().addControl(bombC);
//        Main.getBulletAppState().getPhysicsSpace().add(bombC);
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
        System.out.println("damage:" + this.getDamage());
    }
    
    public void move(Vector3f offset){
        offset.setY(0);
        super.setLocation(offset.add(this.getLocation()));
    }
    
    private void moveTo(Vector3f location){
        move(location.subtract(this.getLocation()));
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
        } else {
            moveTo(way.getNextCorner());
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
//            Main.getBulletAppState().getPhysicsSpace().remove(bombC);
            Main.app.getWorld().getPlayer().increaseMoney(10);
        }
    }
    
    @Override
    protected void die() {
        if(this.getLevel() == 1) {
            super.die();
        } else {
            setLevel(getLevel()-1);
        }
    }
    
    @Override
    public void setLevel(int newLevel) {
        //setzt Leben als Funktion mit level
        this.setHealth(50+newLevel*50);
        this.setDamage(10+newLevel*10);
        this.setSpeed(50-newLevel*2+Main.getGame().getWave()*2);
        
        mat.setColor("Color", colors[(newLevel-1)%colors.length]);
        super.setLevel(newLevel);
    }
    
}
