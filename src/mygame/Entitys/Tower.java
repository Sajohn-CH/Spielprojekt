/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.Entitys;

import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import mygame.Main;


/**
 *
 * @author florianwenk
 */
public class Tower extends Entity{
    
    private int range;
    private int price;
    private double shotsPerSecond;
    private long shot;
    private long died = 0;
    private ParticleEmitter flame;
    private int totalPaidMoney = 0;
    
    public int getRange(){
        return range;
    }
    
    public void setRange(int range){
        this.range = range;
    }
    
    public void makeDamage(Entity e){
        e.increaseHealth(-this.getDamage());
    }
    
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
    
    public double getShotsPerSecond(){
        return shotsPerSecond;
    }
    
    public void setShotsPerSecond(double shotsPerSecond){
        this.shotsPerSecond = shotsPerSecond;
    }
    
    public boolean canShoot(){
        if(System.currentTimeMillis() - shot >= 1000/shotsPerSecond){
            return true;
        }
        return false;
    }
    
    public void shot(){
        this.shot = System.currentTimeMillis();
    }
    
    public long getLastShot(){
        return shot;
    }
    
    /**
     * Upgradet den Turm auf das nächste Level, dafür wird die Methode {@link SimpleTower#setLevel(int)} aufgerufen. Davor wird überpüft, ob der Spieler dafür genung Geld hat. 
     * Wenn dies der Fall ist wird ihm dies abgezogen.
     */
    public void increaseLevel() {
        
    }
    
     public int getNewRange(int newLevel) {
        return 10+newLevel*5;
    }
    
    public int getNewDamage(int newLevel) {
        return 25+newLevel*25;
    }
    
    public int getNewHealth(int newLevel) {
        return 50+newLevel*10;
    }
    
    public double getNewSPS(int newLevel) {
        return (newLevel/2)+1;
    }
    
    public int getUpgradePrice() {
        return this.getMaxHealth();
    }
    
    public void upgrade() {
        
    }
    
    public void setCollidable(){
        
    }
    
    public void died(){
        died = System.currentTimeMillis();
                
        flame = new ParticleEmitter("Flame", Type.Point, 32);
        flame.setSelectRandomImage(true);
        flame.setStartColor(new ColorRGBA(1f, 0.4f, 0.05f, (float) (1f)));
        flame.setEndColor(new ColorRGBA(.4f, .22f, .12f, 0f));
        flame.setStartSize(1.3f);
        flame.setEndSize(2f);
        flame.setShape(new EmitterSphereShape(Vector3f.ZERO, 1f));
        flame.setParticlesPerSec(0);
        flame.setGravity(0, -5, 0);
        flame.setLowLife(.4f);
        flame.setHighLife(.5f);
        flame.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 7, 0));
        flame.getParticleInfluencer().setVelocityVariation(1f);
        flame.setImagesX(2);
        flame.setImagesY(2);
        Material mat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture("Texture", Main.app.getAssetManager().loadTexture("Effects/Explosion/flame.png"));
        mat.setBoolean("PointSprite", true);
        flame.setMaterial(mat);
        flame.setLocalTranslation(this.getLocation());
        flame.emitAllParticles();
        Main.app.getRootNode().attachChild(flame);
        this.getSpatial().removeFromParent();
    }
    
    public boolean canRemove(){
        if (System.currentTimeMillis() - died >= 10000){
            return true;
        }
        return false;
    }
    
    public void removeFireEffect(){
        flame.killAllParticles();
        flame.removeFromParent();
    }
    
    public boolean isDead(){
        if(died != 0){
            return true;
        }
        return false;
    }
    
    public void remove(){
        Main.app.getWorld().getPlayer().increaseMoney((int) (totalPaidMoney * 0.75));
        Main.app.getWorld().removeTower(this);
    }
    
    public void increaseTotalPaidMoney(int money){
        this.totalPaidMoney += money;
    }
}
