/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.Entitys;

import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import mygame.World;

/**
 * Das grundlegende Objekt. Setzt die Grundattribute(Leben, Schaden, Level, Geschwindigkeit, Ort, Ob es lebt, Maximales Leben, Spatial) eines jedes Objektes. Diese Klasse wird zum weitervererben benötigt.
 * @author Florian Wenk
 */
public class Entity {
    private int health;
    private int damage;
    private int level; //level 0 == tot
    private int speed;
    private Vector3f location;
    private boolean living;
    protected int maxHealth = 100;
    private Spatial spatial;

    
    /**
     * Die Aktionen des Objektes. Diese Methode wird in der Methode {@link World#update(float) } aufgerufen. Sie steuert alle Aktionen des Objektes und wird in jedem Updateloop aufgerufen.
     * @param tpf Time per Frame
     */
    public void action(float tpf) {
        
    }
    
    /**
     * 
     * @return Lebenspunkte des Objektes
     */
    public int getHealth(){
        return this.health;
    }
    
    /**
     * 
     * @param maxHealth Maximale Lebenspunkte
     */
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    /**
     * 
     * @return  Maximale Lebenspunkte
     */
    public int getMaxHealth() {
        return maxHealth;
    }
    
    /**
     * 
     * @return Leben in Prozent des Maximalen Lebens.
     */
    public int getHealthPercentage() {
        return (int)(100f*health/maxHealth);
    }
    
    /**
     * 
     * @param health Lebenspunkte
     */
    public void setHealth(int health){
        this.health = health;
    }

    /**
     * Lebenspunkte erhöhen/verringern(-). Falls das Leben unter 0 sinkt stirbt das Objekt
     * @param health Anzahl zu erhöhendes Leben
     */
    public void increaseHealth(int health){
        if(this.health+health > this.getMaxHealth()) {
            this.health = this.maxHealth;
            return;
        }
        this.health = this.health + health;
        if(this.health <= 0) {
             die();
        }     
    }

    /**
     * 
     * @return Schaden der hinzugefügt wird
     */
    public int getDamage(){
        return this.damage;
    }

    /**
     * Setzt den Schaden der das Objekt macht.
     * @param damage Schaden
     */
    public void setDamage(int damage){
        this.damage = damage;
    }

    /**
     * 
     * @return Level
     */
    public int getLevel(){
        return this.level;
    }

    /**
     * Setzt das Level des Objektes.
     * @param level Level
     */
    public void setLevel (int level){
        this.level = level;
    }

    /**
     * 
     * @return Geschwindigkeit
     */
    public int getSpeed (){
        return this.speed;
    }

    /**
     * Setzt die Geschwindigkeit des Objektes.
     * @param speed Geschwindigkeit
     */
    public void setSpeed(int speed){
        this.speed = speed;
    }

    /**
     * 
     * @return Ort des Objektes
     */
    public Vector3f getLocation(){
        return this.location;
    }
    
    /**
     * Setzt den Ort des Objektes.
     * @param location Ort an dem das Objekt gesetzt wird.
     */
    public void setLocation(Vector3f location){
        this.location = location;
    }

    /**
     * 
     * @return Ob das Objekt lebt.
     */
    public boolean isLiving(){
        return this.living;
    }
    
    /**
     * Setzt den Zustand (Lebend/Tot) des Objektes
     * @param living Lebt?
     */
    public void setLiving(boolean living){
        this.living = living;
    }

    /**
     * 
     * @return Spatial des Objektes
     */
    public Spatial getSpatial() {
        return spatial;
    }

    /**
     * Setzt den Spatial.
     * @param spatial Spatial des Objektes
     */
    public void setSpatial(Spatial spatial) {
        this.spatial = spatial;
    }
    
    /**
     * Lässt das Objekt sterben.
     */
    protected void die() {
        living = false;
    }    
}
