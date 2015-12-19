/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import java.util.ArrayList;

/**
 *
 * @author florianwenk
 */
public class Game {
    private int wave;
    private ArrayList<Integer> bombsLeftPerLevel;
    private long lastTime;
   
    public Game(int wave){
        this.wave = wave;
        this.bombsLeftPerLevel = new ArrayList<Integer>();
    }
    
    public void startWave(){
        bombsLeftPerLevel.add(0, wave*20);
    }
    
    public void nextWave(){
        this.wave += 1;
        startWave();
    }
    
    public int getWave(){
        return this.wave;
    }
    
    public boolean bombLeft(){
        return !bombsLeftPerLevel.isEmpty();
    }
    
    public void action(float tpf){
        if(System.currentTimeMillis()-lastTime >= ((Math.random()*5)*200)/wave){
            int i = (int) Math.random()*bombsLeftPerLevel.size();
            Main.getWorld().addBomb(new Bomb(i+1));
            bombsLeftPerLevel.set(i, bombsLeftPerLevel.get(i) - 1);
            if(bombsLeftPerLevel.get(i) == 0){
                bombsLeftPerLevel.remove(i);
            }
            lastTime =System.currentTimeMillis();
        }
    }
}
