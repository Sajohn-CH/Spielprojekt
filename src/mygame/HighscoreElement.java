package mygame;

/**
 * Das Grundlegende Element eines Highscores. Enthält die Daten eines Highscores, der Name, die erreichte Welle und das Datum.
 * @author Florian Wenk
 */
public class HighscoreElement {
    private String name;
    private int wave;
    private long date;
    private String world;
    
    /**
     * Initialisiert den Highscore. Setzt die Werte des Highscores.
     * @param name Name
     * @param wave Erreichte Welle
     * @param date Datum
     */
    public HighscoreElement(String name, int wave, long date, String world){
        this.name = name;
        this.wave = wave;
        this.date = date;
        this.world = world;
    }
    
    /**
     * Gibt den Namen zurück.
     * @return Name
     */
    public String getName(){
        return name;
    }
    
    /**
     * Gibt die erreichte Welle zurück.
     * @return Welle
     */
    public int getWave(){
        return wave;
    }
    
    /**
     * Gibt das Datum zurück.
     * @return Datum
     */
    public long getDate(){
        return date;
    }
    
    /**
     * Gibt den Namen der Welt zurück.
     * @return Name der Welt
     */
    public String getWorld(){
        return world;
    }
}
