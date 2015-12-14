/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResults;
import com.jme3.collision.UnsupportedCollisionException;
import com.jme3.math.Vector3f;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import java.util.Queue;

/**
 *
 * @author florian.wenk
 */
public class Entity {
    private int health;
    private int damage;
    private int level;
    private int speed;
    private Vector3f location;
    private boolean living;
    protected int maxHealth = 100;
    private Spatial spatial;

    public void action(float tpf) {
        
    }
    
    public int getHealth(){
        return this.health;
    }
    
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }
    
    public int getHealthPercentage() {
        //System.out.println("healt:"+health);
        //System.out.println("maxHealth"+maxHealth);
        return (int)(100f*health/maxHealth);
    }
    
    public void setHealth(int health){
        this.health = health;
    }

    public void increaseHealth(int health){
        this.health = this.health + health;
        if(this.health <= 0)
                living = false;
    }

    public int getDamage(){
        return this.damage;
    }

    public void setDamage(int damage){
        this.damage = damage;
    }

    public int getLevel(){
        return this.level;
    }

    public void setLevel (int level){
        this.level = level;
    }

    public int getSpeed (){
        return this.speed;
    }

    public void setSpeed(int speed){
        this.speed = speed;
    }

    public Vector3f getLocation(){
        return this.location;
    }
    
    public void setLocation(Vector3f location){
        this.location = location;
    }

    public boolean isLiving(){
        return this.living;
    }
    
    public void setLiving(boolean living){
        this.living = living;
    }

    public Spatial getSpatial() {
        return spatial;
    }

    public void setSpatial(Spatial spatial) {
        this.spatial = spatial;
    }
    
    
}
