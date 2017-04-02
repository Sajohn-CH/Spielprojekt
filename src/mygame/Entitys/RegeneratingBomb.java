/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.Entitys;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import mygame.Main;

/**
 *
 * @author florianwenk
 */
public class RegeneratingBomb extends Bomb{
    
    private boolean regenerating;
    private int regenerationSpeed;
    private int maxLvl;
    private long lastRegenerated;
    private Node n;
    
    public RegeneratingBomb(Integer level){
        super(level);
        super.setNormal(false);
        super.setMoney(15);
        maxLvl = level;
        regenerating = true;
        regenerationSpeed = 10;
        lastRegenerated = Main.app.getClock().getTime();
        n = new Node("regeneratingBomb");
                
        Sphere sphere = new Sphere(100, 100, 1);
        Cylinder cylinder = new Cylinder(50, 50, 1.01f, .25f);
        n.attachChild(new Geometry("bomb",sphere));
        n.attachChild(new Geometry("color",cylinder));
        Quaternion q = new Quaternion();
        q.fromAngleAxis((float) Math.PI/2 , new Vector3f(1,0,0));
        n.getChild("color").rotate(q);
        Material mat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        ColorRGBA color = new ColorRGBA(.4f, 0, 0, 1);
        mat.setColor("Color", color);
        n.getChild("bomb").setMaterial(mat);
        n.getChild("color").setMaterial(this.getMaterial());
        this.setSpatial(n);
        this.getSpatial().setLocalTranslation(this.getLocation());
    }
    
    @Override
    public void action(float tpf){
        super.action(tpf);
        if(canRegenerate()){
            regenerate();
        }
    }
    
    private boolean canRegenerate(){
        if(regenerating && Main.app.getClock().getTime()-lastRegenerated >= 10000/regenerationSpeed && this.getLevel() < maxLvl){
            return true;
        }
        return false;
    }
    
    private void regenerate(){
        if(this.getLevel() < maxLvl){
            super.setLevel(this.getLevel()+1);
            lastRegenerated = Main.app.getClock().getTime();
        }
    }
    
    @Override
    public void setLevel(int newLevel){
        super.setLevel(newLevel);
        if(newLevel > maxLvl){
            regenerationSpeed = (int)(newLevel/5*multiplier)+1;
            if(regenerationSpeed > 10){
                regenerationSpeed=10;
            }
            maxLvl = newLevel;
        }
        lastRegenerated = Main.app.getClock().getTime();
    }
    
    @Override
    public void setNormal(boolean normal){
        regenerating = false;
        super.setNormal(true);
        if(n != null){
            n.getChild("bomb").setMaterial(Main.app.getAssetManager().loadMaterial("Materials/Black.j3m"));
        }
    }
}
