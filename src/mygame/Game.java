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
    private long nextTime;
   
    public Game(int wave){
        this.wave = wave;
        this.bombsLeftPerLevel = new ArrayList<Integer>();
        lastTime = System.currentTimeMillis();
        nextTime = 100;
    }
    
    public void startWave(){
        //TODO: Mehrere Levels Bomben generieren
        int bombsLeft = (int) (wave*Math.pow(wave,0.5));
        while(bombsLeft > 0){
            int i = (int) bombsLeft/wave*2;
            if(i > bombsLeft){
                i = bombsLeft;
            } else if (i <= 0){
                i = 1;
            }
            bombsLeft -= i;
            bombsLeftPerLevel.add(i);
        }
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
        if(System.currentTimeMillis()-lastTime >= nextTime){
            int i = Math.round((float) (Math.random()*(bombsLeftPerLevel.size()-1)));
            if(bombsLeftPerLevel.get(i) >= 0){
                Main.getWorld().addBomb(new Bomb(i+1));
                bombsLeftPerLevel.set(i, bombsLeftPerLevel.get(i) - 1);
                if(bombsLeftPerLevel.get(i) == 0){
                    bombsLeftPerLevel.remove(i);
                }
                lastTime =System.currentTimeMillis();
                nextTime = (long) ((Math.random()*10)*100);
            }
        }
    }
}
