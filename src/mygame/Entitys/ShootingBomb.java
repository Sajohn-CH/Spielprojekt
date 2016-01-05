/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.Entitys;

import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.PQTorus;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Torus;
import mygame.Main;

/**
 *
 * @author florianwenk
 */
public class ShootingBomb extends Bomb{
    
    private Geometry line;
    private Node n;
    private long shot;
    private int shotsPerSecond;
    private int range;
    private boolean shooting;
    
    public ShootingBomb(int level){
        super(level);
        line = new Geometry("line");
        Material matL = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matL.setColor("Color", ColorRGBA.Yellow);
        line.setMaterial(matL);
        
        shooting = true;
        shot = System.currentTimeMillis();
        
        n = new Node("shootingBomb");
                
        Sphere sphere = new Sphere(100, 100, 1);
        Cylinder cylinder = new Cylinder(50, 50, 1.01f, .25f);
        Spatial gun = Main.app.getAssetManager().loadModel("Objects/Gun.j3o").scale(.2f);
        gun.setName("gun");
        gun.setLocalTranslation(0, .5f, 0);
        n.attachChild(new Geometry("bomb",sphere));
        n.attachChild(new Geometry("color",cylinder));
        n.attachChild(gun);
        Quaternion q = new Quaternion();
        q.fromAngleAxis((float) Math.PI/2 , new Vector3f(1,0,0));
        n.getChild("color").rotate(q);
        n.getChild("bomb").setMaterial(Main.app.getAssetManager().loadMaterial("Materials/Black.j3m"));
        n.getChild("color").setMaterial(this.getMaterial());
        n.getChild("gun").setMaterial(this.getMaterial());
        this.setSpatial(n);
        this.getSpatial().setLocalTranslation(this.getLocation());
        
//        Torus t = new Torus(100, 100, 0.75f, 2f);
//        n.attachChild(new Geometry("shootingBomb", t));
//        n.getChild("shootingBomb").setMaterial(this.getMaterial());
//        n.getChild("shootingBomb").setLocalTranslation(this.getLocation());
//        
//        Quaternion q = new Quaternion();
//        q.fromAngleAxis((float) Math.PI/2 , new Vector3f(1,0,0));
//        n.getChild("shootingBomb").setLocalRotation(q);
//        
//        this.setSpatial(n);
        
//        Vector3f location = this.getLocation();
    }
    
    public boolean canShoot(){
        if(System.currentTimeMillis() - shot >= 1000/shotsPerSecond && shooting){
            return true;
        }
        return false;
    }
    
    public boolean isShooting(){
        return shooting;
    }
    
    public void shot(){
        this.shot = System.currentTimeMillis();
    }
        
    @Override
    public void action(float tpf) {
        super.action(tpf);
        if(canShoot() && this.isLiving() & Main.getWorld().getPlayer().isLiving()){
            if(Main.app.getCamera().getLocation().subtract(this.getSpatial().getLocalTranslation()).length() <= this.range){
                n.getChild("gun").lookAt(Main.app.getCamera().getLocation(), new Vector3f(0,1,0));
                Line l = new Line(this.getSpatial().getLocalTranslation(), Main.app.getCamera().getLocation().setY(2.5f));
                line.setMesh(l);
                Main.app.getRootNode().attachChild(line);
                shot();
                Main.getWorld().getPlayer().increaseHealth(-this.getDamage());
            }
        }
        if(canShoot() && this.isLiving() && !Main.getWorld().getAllTowers().isEmpty()){
            for(int i = 0; i < Main.getWorld().getAllTowers().size(); i++){
                if(Main.getWorld().getAllTowers().get(i).getSpatial().getLocalTranslation().subtract(this.getSpatial().getLocalTranslation()).length() <= this.range && canShoot()){
                   n.getChild("gun").lookAt(Main.getWorld().getAllTowers().get(i).getSpatial().getLocalTranslation(), new Vector3f(0,1,0));
                   Line l = new Line(this.getSpatial().getLocalTranslation(), Main.getWorld().getAllTowers().get(i).getSpatial().getLocalTranslation());
                   line.setMesh(l);
                   Main.app.getRootNode().attachChild(line);
                   shot();
                   this.makeDamage(Main.getWorld().getAllTowers().get(i));
                }
            }
        }
        if(canShoot()){
            n.getChild("gun").lookAt(this.getWay().getThisCorner().add(0, 2, 0), new Vector3f(0,1,0));
        }
        if(!this.isLiving()){
            line.removeFromParent();
        }
        if(System.currentTimeMillis()-shot>= 50){
            line.removeFromParent();
        }
    }
  
    /**
     * Setzt das Level der Bombe auf den übergebenen Wert. Dies erhört die Reichweite, den Schaden, die Lebenspunkte und die Schussrate der Bombe.
     * @param newLevel  neues Level 
     */
    @Override
    public void setLevel(int newLevel) {
        super.setLevel(newLevel);
        this.range = 10+newLevel*5;
        this.shotsPerSecond = (newLevel/2)+1;
    }
    
    public void disableShooting(){
        shooting = false;
        n.detachChildNamed("gun");
    }
}
