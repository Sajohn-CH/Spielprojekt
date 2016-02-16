package mygame;

import com.jme3.math.Vector3f;
import java.util.ArrayList;

/**
 * Der Weg. Diese Klasse hat die Eckpunkte und damit den Weg, den die Bomben gehen. Die Eckpunkte werden weitergegeben.
 * @author Florian Wenk
 */
public class Way {
    private ArrayList<Vector3f> corners;    //Alle Ecken des Weges.
    private int positionID;                 //Gibt die aktuelle Ecke an.
    
    /**
     * Initialisiert den Weg. Lädt die Eckpunkte und initialisiert die positionID.
     */
    public Way(){
        corners = Main.getWorld().getAllCorners();
        positionID = 1;
    }
    
    /**
     * Gibt den StartPunkt zurück. Der Ort, an dem die Bomben generiert werden sollen.
     * @return StartPunkt
     */
    public Vector3f getStartPoint(){
        return corners.get(0).mult(2);
    }
    
    /**
     * Gibt das nächste Ziel der Bombe. Gibt den nächsten Eckpunkt der Bombe zurück und die PositionID wird erhöht.
     * @return Nächste Ecke
     */
    public Vector3f getNextCorner(){
        positionID += 1;
        return corners.get(positionID-1).mult(2);
    }
    
    /**
     * Gibt die Ecke zurück, zu welcher die Bombe unterwegs ist.
     * @return Diese Ecke
     */
    public Vector3f getThisCorner(){
        return corners.get(positionID-1).mult(2);
    }
    
    /**
     * Gibt zurück zur wievielten Ecke sie unterwegs ist.
     * @return Wievielte Ecke
     */
    public int getPositionID(){
        return positionID;
    }
    
    /**
     * Setzt, dass die Bombe zu der Ecke, die übergeben wird, geht.
     * @param cornerID Wievielte Ecke
     */
    public void setNextCorner(int cornerID){
        positionID = cornerID;
    }
}
