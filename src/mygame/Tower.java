/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;


/**
 *
 * @author florianwenk
 */
public class Tower extends Entity{
    
    private int range;
    private int price;
    private double shotsPerSecond;
    private long shot;
    
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
    
    public int getNewSPS(int newLevel) {
        return (newLevel/2)+1;
    }
    
    public int getUpgradePrice() {
        return this.getMaxHealth();
    }
    
    public void upgrade() {
        
    }
    
    public void setCollidable(){
        
    }
    
}
