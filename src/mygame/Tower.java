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
    private int shotsPerSecond;
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
    
    public int getShotsPerSecond(){
        return shotsPerSecond;
    }
    
    public void setShotsPerSecond(int shotsPerSecond){
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
    
}
