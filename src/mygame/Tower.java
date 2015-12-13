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
    
    public int getRange(){
        return range;
    }
    
    public void setRange(int range){
        this.range = range;
    }
    
    public void makeDamage(Entity e){
        e.increaseHealth(-this.getDamage());
    }
    
    
}
