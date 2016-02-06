package mygame;

import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Highscores. Das Objekt, das alle Highscores enthält, ihnen den richtigen Rang zuweist, sie speichert und lädt.
 * @author Florian Wenk
 */
public class Highscores {
    private ArrayList<HighscoreElement> highscores;
    
    /**
     * Initialisiert die Highscores. Lädt die Highscores, falls diese Existieren.
     */
    public Highscores(){
        this.highscores = loadHighscores();
    }
    
    /**
     * Gibt einen bestimmten Platz der Highscores zurück.
     * @param place Platz
     * @return HighscoreElement auf dem entsprechenden Platz
     */
    public HighscoreElement getHighscore(int place){
        return highscores.get(place-1);
    }
    
    /**
     * Gibt alle Highscores zurück.
     * @return 
     */
    public ArrayList<HighscoreElement> getAllHighscores(){
        return highscores;
    }
    
    /**
     * Fügt ein Spielstand zu den Highscores hinzu.
     * @param name Name des Spielers
     * @param wave Erreichte Welle
     * @param world Name der Welt
     */
    public void addHighscore(String name, int wave, String world){
        if(highscores.isEmpty()){
            highscores.add(new HighscoreElement(name, wave, System.currentTimeMillis(), world));
            return;
        }
        int i = 0;
        while(highscores.get(i).getWave() > wave){
            i++;
        }
        highscores.add(i, new HighscoreElement(name, wave, System.currentTimeMillis(), world));
    }
    
    /**
     * Fügt ein Spielstand zu den Highscores hinzu. Setzt im gegensatz zu {@link Highscores#addHighscore(java.lang.String, int)} keinen Namen (den Namen auf "Unbekannt")
     * @param wave Erreichte Welle
     * @param world Name der Welt
     */
    public void addHighscore(int wave, String world){
        addHighscore("Unbekannt", wave, world);
    }
    
    /**
     * Löscht alle Highscores. Sowohl intern, als auch das Dokument indem sie gespeichert sind.
     */
    public void deleteAllHighscores(){
        this.highscores = new ArrayList<>();
        File highscores = new File("highscores.xml");
        highscores.delete();
    }
    
    /**
     * Lädt die Highscores. Lädt die Highscores aus dem Dokument.
     * @return Die ArrayList mit den Highscores
     */
    private ArrayList<HighscoreElement> loadHighscores (){
        ArrayList<HighscoreElement> highscores = new ArrayList<>();
        File highscoresFile = new File("highscores.high");
        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(highscoresFile);
            dBuilder = dbFactory.newDocumentBuilder();
            doc.getDocumentElement().normalize();

            //Lädt alle Highscores
            NodeList nList = doc.getElementsByTagName("Highscore");
            for(int i = 0; i < nList.getLength(); i++) {
                Element highscoreElement = (Element) nList.item(i);
                highscores.add(Integer.valueOf(highscoreElement.getAttribute("Place"))-1, new HighscoreElement(highscoreElement.getAttribute("Name"), Integer.valueOf(highscoreElement.getAttribute("Wave")), Long.valueOf(highscoreElement.getAttribute("Date")), highscoreElement.getAttribute("World")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        return highscores;
    }
    
    /**
     * Speichert die Highscores, falls sie nicht leer sind.
     */
    public void saveHighscores(){
         File highscores = new File("highscores.high");
         if(this.highscores.isEmpty()){
             highscores.delete();
             return;
         }
         try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            Element rootElement = doc.createElement("Highscores");
            rootElement.setAttribute("Date", String.valueOf(System.currentTimeMillis()));
            doc.appendChild(rootElement);
            
            //Speichere alle Highscores
            for(int i = 0; i < this.highscores.size(); i++) {
                Element highscore = doc.createElement("Highscore");
                highscore.setAttribute("Place", String.valueOf(i+1));
                highscore.setAttribute("Name", this.highscores.get(i).getName());
                highscore.setAttribute("Wave", String.valueOf(this.highscores.get(i).getWave()));
                highscore.setAttribute("Date", String.valueOf(this.highscores.get(i).getDate()));
                highscore.setAttribute("World", this.highscores.get(i).getWorld());
                rootElement.appendChild(highscore);
            }
                               
           TransformerFactory transformerFactory = TransformerFactory.newInstance();
           Transformer transformer = transformerFactory.newTransformer();
           DOMSource source = new DOMSource(doc);
           StreamResult result = new StreamResult(highscores);
           transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
