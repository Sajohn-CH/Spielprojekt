package mygame;

import mygame.Entitys.Bomb;
import mygame.Entitys.ShootingBomb;
import java.util.ArrayList;

/**
 * Das grundsätzliche Spielprinzip. Es werden die Wellen kontrolliert und die Bomben, die in einer solchen kommen generiert.
 * @author Florian Wenk
 */
public class Game {
    private int wave;
    private ArrayList<Integer> bombsLeftPerLevel;
    private ArrayList<Integer> shootingBombsLeftPerLevel;
    private long lastTime;
    private long nextTime;
   
    /**
     * Initialisiert das Spielprinzip. Sorgt dafür, dass Bomben erstellt werden.
     * @param wave Welche Welle es ist.
     */
    public Game(int wave){
        this.wave = wave;
        this.bombsLeftPerLevel = new ArrayList<Integer>();
        this.shootingBombsLeftPerLevel = new ArrayList<Integer>();
        lastTime = System.currentTimeMillis();
        nextTime = 100;
    }
    
    /**
     * Setzt wieviele Bomben generiert werden sollen.
     */
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
    
    /**
     * {@link startWave()}
     * @param wave Setzt die Welle.
     */
    public void startWave(int wave) {
        this.wave = wave;
        startWave();
    }
    
    /**
     * Startet die nächste Welle. Öffnet das EndWavePopup und startet die neue Welle.
     */
    public void nextWave(){
        // Bonus für welle überstanden
        Main.getWorld().getPlayer().increaseMoney(this.wave * 50);
        this.wave += 1;
        //Wenn Player tot, wiederbeleben.
        if(!Main.app.getWorld().getPlayer().isLiving()){
            Main.app.getWorld().getPlayer().revive();
        }
        Main.app.getHudState().showEndWavePopup();
    }
    
    /**
     * Gibt zurück, welche Welle zurzeit ist.
     * @return Die wievielte Welle
     */
    public int getWave(){
        return this.wave;
    }
    
    /**
     * Gibt zurück ob noch Bomben zu generieren sind.
     * @return Ob schon alle Bomben generiert sind.
     */
    public boolean bombLeft(){
        return !bombsLeftPerLevel.isEmpty() || !shootingBombsLeftPerLevel.isEmpty();
    }
    
    /**
     * Wird bei jedem Update ausgeführt, erstellt die Bomben.
     * @param tpf Time per Frame
     */
    public void action(float tpf){
        if(System.currentTimeMillis()-lastTime >= nextTime){
            if(Math.random() < 0.5 || shootingBombsLeftPerLevel.isEmpty()){
                int i = Math.round((float) (Math.random()*(bombsLeftPerLevel.size()-1)));
                if(bombsLeftPerLevel.get(i) >= 0){
                    Main.getWorld().addBomb(new Bomb(i+1));
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
