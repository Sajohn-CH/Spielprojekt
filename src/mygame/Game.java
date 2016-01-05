/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import mygame.Entitys.Bomb;
import mygame.Entitys.ShootingBomb;
import java.util.ArrayList;

/**
 *
 * @author florianwenk
 */
public class Game {
    private int wave;
    private ArrayList<Integer> bombsLeftPerLevel;
    private ArrayList<Integer> shootingBombsLeftPerLevel;
    private long lastTime;
    private long nextTime;
   
    public Game(int wave){
        this.wave = wave;
        this.bombsLeftPerLevel = new ArrayList<Integer>();
        this.shootingBombsLeftPerLevel = new ArrayList<Integer>();
        lastTime = System.currentTimeMillis();
        nextTime = 100;
    }
    
    public void startWave(){
        // Mehrere Levels Bomben generieren
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
        
        if(wave > 10){
            int shootingBombsLeft = (int) ((wave-10)*Math.pow((wave-10), 0.5))/2;
            while(shootingBombsLeft > 0){
                int i = (int) shootingBombsLeft/wave*2;
                if(i > shootingBombsLeft){
                    i = shootingBombsLeft;
                } else if (i <= 0){
                    i = 1;
                }
                shootingBombsLeft -= i;
                shootingBombsLeftPerLevel.add(i);
            }
        }
    }
    
    public void startWave(int wave) {
        this.wave = wave;
        startWave();
    }
    
    public void nextWave(){
        // Bonus für welle überstanden
        Main.getWorld().getPlayer().increaseMoney(this.wave * 50);
        this.wave += 1;
        Main.app.getHudState().showEndWavePopup();
        //startWave();
    }
    
    public int getWave(){
        return this.wave;
    }
    
    public boolean bombLeft(){
        return !bombsLeftPerLevel.isEmpty();
    }
    
    public void action(float tpf){
        if(System.currentTimeMillis()-lastTime >= nextTime){
            if(Math.random() < 0.5 || shootingBombsLeftPerLevel.isEmpty()){
                int i = Math.round((float) (Math.random()*(bombsLeftPerLevel.size()-1)));
                if(bombsLeftPerLevel.get(i) >= 0){
                    Main.getWorld().addBomb(new ShootingBomb(i+1));
                    bombsLeftPerLevel.set(i, bombsLeftPerLevel.get(i) - 1);
                    if(bombsLeftPerLevel.get(i) == 0){
                        bombsLeftPerLevel.remove(i);
                    }
                }
            } else {
                int i = Math.round((float) (Math.random()*(shootingBombsLeftPerLevel.size()-1)));
                if(shootingBombsLeftPerLevel.get(i) >= 0){
                    Main.getWorld().addBomb(new ShootingBomb(i+1));
                    shootingBombsLeftPerLevel.set(i, shootingBombsLeftPerLevel.get(i) - 1);
                    if(shootingBombsLeftPerLevel.get(i) == 0){
                        shootingBombsLeftPerLevel.remove(i);
                    }
                }
            }
            lastTime =System.currentTimeMillis();
            nextTime = (long) ((Math.random()*10)*100);
        }
    }
}
