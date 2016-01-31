package mygame;

/**
 * Das Grundlegende Element eines Highscores. Enthält die Daten eines Highscores, der Name, die erreichte Welle und das Datum.
 * @author Florian Wenk
 */
public class HighscoreElement {
    private String name;
    private int wave;
    private long date;
    
    /**
     * Initialisiert den Highscore. Setzt die Werte des Highscores.
     * @param name Name
     * @param wave Erreichte Welle
     * @param date Datum
     */
    public HighscoreElement(String name, int wave, long date){
        this.name = name;
        this.wave = wave;
        this.date = date;
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
}
