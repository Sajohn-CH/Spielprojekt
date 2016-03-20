package mygame;

import java.lang.reflect.InvocationTargetException;
import mygame.Entitys.Bomb;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Das grundsätzliche Spielprinzip. Es werden die Wellen kontrolliert und die Bomben, die in einer solchen kommen generiert.
 * @author Florian Wenk
 */
public class Game {
    private int wave;                                       //Die wievielte Welle gerade läuft
    private HashMap<Class<? extends Bomb>, HashMap<String, ArrayList<Integer>>> bombsLeft;
    private ArrayList<Class<? extends Bomb>> classes;
    private ArrayList<Integer> fromWave;
    private HashMap<Class<? extends Bomb>, String> classToLanguageString;
    private HashMap<String, Class<? extends Bomb>> languageStringToClass;
    private long lastTime;                                  //Das Letze Mal, als eine Bombe erstellt wurde
    private long nextTime;                                  //Wieviel Zeit (in Milisekunden) vergeht bis die nächste Bombe erstellt wird.
   
    /**
     * Initialisiert das Spielprinzip. Sorgt dafür, dass Bomben erstellt werden.
     * @param wave Welche Welle es ist.
     */
    public Game(int wave, ArrayList<String> classNames){
        this.wave = wave;
        this.bombsLeft = new HashMap<>();
        this.classes = new ArrayList<>();
        this.classes = new ArrayList<>();
        this.fromWave = new ArrayList<>();
        this.classToLanguageString = new HashMap<>();
        this.languageStringToClass = new HashMap<>();
        for(int i = 0; i < classNames.size(); i ++){
            String[] className = classNames.get(i).split(";");
            try {
                classes.add(Class.forName("mygame.Entitys." + className[0]).asSubclass(Bomb.class));
                fromWave.add(Integer.valueOf(className[1]));
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        reloadLanguage();
        lastTime = System.currentTimeMillis();
        nextTime = 100;
    }
    
    /**
     * Setzt wieviele Bomben generiert werden sollen.
     */
    public void startWave(){
        HashMap<String, ArrayList<Integer>> map;
        for(int i = 0; i < classes.size(); i ++){
            map = generateBombsList(fromWave.get(i));
            if(map != null && !map.isEmpty()){
                bombsLeft.put(classes.get(i), map);
            }
        }
    }
    
    private HashMap<String, ArrayList<Integer>> generateBombsList(int fromWave){
        if(wave < fromWave){
            return null;
        }
        ArrayList<Integer> classBombsLeftPerLevel= new ArrayList<>();
        ArrayList<Integer> classBombsLeftLevels = new ArrayList<>();
        int bombLeft = Math.round((float) ((wave+1-fromWave)*Math.pow((wave+1-fromWave), 0.5)/2));
        while(bombLeft > 0){
            int i = (int) bombLeft/wave*2;
            if(i > bombLeft){
                i = bombLeft;
            } else if (i <= 0){
                i = 1;
            }
            bombLeft -= i;
            classBombsLeftPerLevel.add(i);
            classBombsLeftLevels.add(classBombsLeftPerLevel.size());
        }
        HashMap<String, ArrayList<Integer>> map = new HashMap();
        map.put("bombsLeft", classBombsLeftPerLevel);
        map.put("levels", classBombsLeftLevels);
        return map;
    }
    
    /**
     * {@link Game#startWave()}
     * Setzt die Welle auf den übergebenen Wert.
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
        Main.app.getWorld().getPlayer().increaseMoney((int) Math.sqrt(this.wave) * 75);
        Main.app.getWorld().getPlayer().playAudioEarnMoney();
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
        for(int i = 0; i < classes.size(); i++){
            if(bombsLeft.containsKey(classes.get(i)) && !bombsLeft.get(classes.get(i)).get("bombsLeft").isEmpty())
                return true;
        }
        return false;
    }
    
    /**
     * Wird bei jedem Update ausgeführt, erstellt die Bomben.
     * @param tpf Time per Frame
     */
    public void action(float tpf){
        if(System.currentTimeMillis()-lastTime >= nextTime){
            if(classes.isEmpty()){
                return;
            }
            Class bombClass = null;
            do{
                bombClass = classes.get(Math.round((float) (Math.random()*(classes.size()-1))));
            } while (!bombsLeft.containsKey(bombClass) || (bombsLeft.containsKey(bombClass) && bombsLeft.get(bombClass).get("bombsLeft").isEmpty()));
            int i = Math.round((float) (Math.random()*(bombsLeft.get(bombClass).get("bombsLeft").size()-1)));
            if(!bombsLeft.get(bombClass).get("bombsLeft").isEmpty() && bombsLeft.get(bombClass).get("bombsLeft").get(i) >= 0){
                try {
                    Main.app.getWorld().addBomb((Bomb) bombClass.getConstructor(Integer.class).newInstance(bombsLeft.get(bombClass).get("levels").get(i)));
                } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                }
                bombsLeft.get(bombClass).get("bombsLeft").set(i, bombsLeft.get(bombClass).get("bombsLeft").get(i) - 1);
                if(bombsLeft.get(bombClass).get("bombsLeft").get(i) == 0){
                    bombsLeft.get(bombClass).get("bombsLeft").remove(i);
                    bombsLeft.get(bombClass).get("levels").remove(i);
                }
            }
            lastTime =System.currentTimeMillis();
            nextTime = (long) ((Math.random()*10)*100);
        }
    }
    
    public ArrayList<String> getPossibleBombTypes(){
        ArrayList<String> bombTypes = new ArrayList<>();
        bombTypes.add(Main.app.getSettings().getLanguageProperty("allBombs"));
        for(int i = 0; i < classes.size(); i++){
            bombTypes.add(classToLanguageString.get(classes.get(i)));
        }
        return bombTypes;
    }
    
    public Class<? extends Bomb> getBombType(String type){
        return languageStringToClass.get(type);
    }
    
    public void reloadLanguage(){
        for(int i = 0; i < classes.size(); i++){
            classToLanguageString.put(classes.get(i), Main.app.getSettings().getLanguageProperty(classes.get(i).getSimpleName(), classes.get(i).getSimpleName()));
            languageStringToClass.put(Main.app.getSettings().getLanguageProperty(classes.get(i).getSimpleName(), classes.get(i).getSimpleName()), classes.get(i));
        }
    }
}
