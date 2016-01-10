/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import mygame.Entitys.SloweringTower;
import mygame.Entitys.Player;
import mygame.Entitys.DeactivationTower;
import mygame.Entitys.SimpleTower;
import mygame.Entitys.Tower;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Kontrolliert den Start und Pause-Bildschirm. Er ist der ScreenController vom Start- und dem Pausebildschirm. Er stellt die Methoden zur Verfügung, welche diese
 * Bildschirme aufrufen müssen (Spiel starten, beenden etc.).
 * @author Samuel Martin
 */
public class MyStartScreen extends AbstractAppState implements ScreenController{
    private Nifty nifty;        //Das Niftyobject das mit der Methode bind() übergeben wird. Wird benötigt um auf die graphische Oberfläche zuzugreifen
    private Screen screen;      //Das Screenobject das mit der Methode bind() übergeben wird. Wird benötigt um auf die graphische Oberfläche zuzugreifen. Es repräsentiert den aktuell angezeigten Bildschirm.
    
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        //TODO: initialize your AppState, e.g. attach spatials to rootNode
        //this is called on the OpenGL thread after the AppState has been attached
    }
    
    /**
     * {@inheritDoc } 
     */
    @Override
    public void update(float tpf) {
         //TODO: implement behavior during runtime
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void cleanup() {
        super.cleanup();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
    }

    /**
     * {@inheritDoc }
     */
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
        System.out.println("bind " + screen.getScreenId());
        if(screen.getScreenId().equals("settings")) {
            //Lädt die eingestellten Einstellungen, damit diese angezeigt werden, wenn der Einstellungsbildschirm geladen wird.
            if(Main.app.getSettings().isUseScroll()) {
                screen.findElementByName("enableScroll").disable();
            } else {
            screen.findElementByName("disableScroll").disable();
            }
        }
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * {@inheritDoc }
     */
    public void onStartScreen() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.      
    }

    /**
     * {@inheritDoc }
     */
    public void onEndScreen() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Führt das Spiel weiter aus.
     */
    public void continueGame() {
        //Zum richtigen Bildschirm wechseln
        nifty.gotoScreen("hud");
        //Kamera ändern
        Main.app.getFlyByCamera().setDragToRotate(Main.app.getHudState().isCameraDragToRotate());
         if(!Main.app.getFlyByCamera().isDragToRotate()){
            Main.app.getFlyByCamera().setRotationSpeed(1);
        }
        Main.getWorld().setPaused(false);
        //InfoBildschirm anzeigen
        Main.app.getHudState().toggleHelpLayer();
    }
    
    /**
     * Startet das Spiel.
     */
    public void startGame() {
        //Spiel fortsetzen
        continueGame();
        
        //Spielstand zurücksetzen.
        Main.app.getWorld().getPlayer().setLocation(new Vector3f(0,10,0));
        Main.app.getWorld().getPlayer().revive();
        Main.app.getWorld().getPlayer().setMoney(250);
        //Alle Türme zurücksetzen
        for(int i = Main.app.getWorld().getAllTowers().size()-1; i >= 0; i--) {
            Main.app.getWorld().removeTower(Main.app.getWorld().getAllTowers().get(i));
        }
        //Alle Bomben zurücksetzen
        for(int i = Main.app.getWorld().getAllBombs().size()-1; i >= 0; i--) {
            Main.app.getWorld().removeBomb(Main.app.getWorld().getAllBombs().get(i));
        }
        //Welle 1 starten
        Main.app.getGame().startWave(1);
        //Falls Bauzeit/phase war diese Beenden (einfach auf false setzen)
        Main.app.getHudState().setBuildPhase(false);
        
    }
    
    /**
     * Beendet das Spiel.
     */
    public void quitGame() {
       saveGame();
       Main.app.stop();
    }
    
    /**
     * Wechsel zum Startbildschirm.
     */
    public void toStart() {
        nifty.gotoScreen("start");
    }
    
    /**
     * Lädt einen Spielstand von einer XML-Datei. Diese Datei ist aktuell immer "saveGame.xml". 
     */
    public void loadGame() {
        File saveGame = new File("saveGame.xml");
        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(saveGame);
            dBuilder = dbFactory.newDocumentBuilder();
            doc.getDocumentElement().normalize();
            
            //Lädt alle Türme
            NodeList nList = doc.getElementsByTagName("Tower");
            for(int i = 0; i < nList.getLength(); i++) {
                Tower tower;
                Element towerElement = (Element) nList.item(i);
                Element posEle = (Element) towerElement.getElementsByTagName("Position").item(0);
                Vector3f position = new Vector3f();
                position.setX(Float.valueOf(posEle.getAttribute("x")));
                position.setY(Float.valueOf(posEle.getAttribute("y")));
                position.setZ(Float.valueOf(posEle.getAttribute("z")));
                if(towerElement.getAttribute("Type").equals("mygame.Entitys.SimpleTower")) {
                    tower = new SimpleTower(position);
                } else if(towerElement.getAttribute("Type").equals("mygame.Entitys.MGTower")) {
                    tower = new SloweringTower(position);
                } else {
                    tower = new DeactivationTower(position);
                }
                Main.app.getWorld().addTower(tower);
                tower.setLevel(Integer.valueOf(towerElement.getAttribute("Level")));
                tower.setHealth(Integer.valueOf(towerElement.getAttribute("Health")));
            }
            
            //Setzt Player
            Element playerEle = (Element) doc.getElementsByTagName("Player").item(0);
            Player player = Main.app.getWorld().getPlayer();
            player.setHealth(Integer.valueOf(playerEle.getAttribute("Health")));
            player.setMaxHealth(Integer.valueOf(playerEle.getAttribute("MaxHealth")));
            player.setRange(Integer.valueOf(playerEle.getAttribute("Range")));
            player.setShotsPerSecond(Double.valueOf(playerEle.getAttribute("SPS")));
            player.setDamage(Integer.valueOf(playerEle.getAttribute("Damage")));
            player.setSpeed(Integer.valueOf(playerEle.getAttribute("Speed")));
            player.setMoney(Integer.valueOf(playerEle.getAttribute("Money")));
            //Setzt Position des Players
            Vector3f pos = new Vector3f();
            Element posEle = (Element) playerEle.getElementsByTagName("Position").item(0);
            pos.setX(Float.valueOf(posEle.getAttribute("x")));
            pos.setY(Float.valueOf(posEle.getAttribute("y")));
            pos.setZ(Float.valueOf(posEle.getAttribute("z")));
            System.out.println("Set pos of Player at: "+pos);
            player.setLocation(pos);
            //Schaut ob Player tot ist
            if(player.getHealth() <= 0) {
                player.setLiving(false);
            } 
            
            //Setzt Beacon
            Element beaconEle = (Element) doc.getElementsByTagName("Beacon").item(0);
            Main.app.getWorld().getBeacon().setHealth(Integer.valueOf(beaconEle.getAttribute("Health")));
            Main.app.getWorld().getBeacon().setLevel(Integer.valueOf(beaconEle.getAttribute("Level")));
            
            //Setzt Welle
            Element rootElement = (Element) doc.getElementsByTagName("SaveGame").item(0);
            Main.app.getGame().startWave(Integer.valueOf(rootElement.getAttribute("Wave")));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        continueGame();
    }
    
    /**
     * Speichert den aktuellen Spielstand. Der wird immer in die Datei "saveGame.xml".
     */
    private void saveGame() {
         File saveGame = new File("saveGame.xml");
         try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            Element rootElement = doc.createElement("SaveGame");
            rootElement.setAttribute("Wave", String.valueOf(Main.app.getGame().getWave()));
            rootElement.setAttribute("Date", String.valueOf(System.currentTimeMillis()));
            doc.appendChild(rootElement);
            
            //Speichere alle Türme
            Element world = doc.createElement("World");
            ArrayList<Tower> towers = Main.app.getWorld().getAllTowers();
            for(int i = 0; i < towers.size(); i++) {
                Element tower = doc.createElement("Tower");
                tower.setAttribute("Type", towers.get(i).getClass().getName());
                tower.setAttribute("Level", String.valueOf(towers.get(i).getLevel()));
                tower.setAttribute("Health", String.valueOf(towers.get(i).getHealth()));
                //Speichert Position des Turms
                Element position = doc.createElement("Position");
                position.setAttribute("x", String.valueOf(towers.get(i).getLocation().getX()));
                position.setAttribute("y", String.valueOf(towers.get(i).getLocation().getY()));
                position.setAttribute("z", String.valueOf(towers.get(i).getLocation().getZ()));
                tower.appendChild(position);
                world.appendChild(tower);
            }
            rootElement.appendChild(world);
            
            //Speichert Beacon
           Element beacon = doc.createElement("Beacon");
           beacon.setAttribute("Health", String.valueOf(Main.app.getWorld().getBeacon().getHealth()));
           beacon.setAttribute("Level", String.valueOf(Main.app.getWorld().getBeacon().getLevel()));
           rootElement.appendChild(beacon);
           
           //Speichert Spieler
           Element playerElement = doc.createElement("Player");
           Player player = Main.app.getWorld().getPlayer();
           playerElement.setAttribute("Health", String.valueOf(player.getHealth()));
           playerElement.setAttribute("MaxHealth", String.valueOf(player.getMaxHealth()));
           playerElement.setAttribute("Range", String.valueOf(player.getRange()));
           playerElement.setAttribute("SPS", String.valueOf(player.getSPS()));
           playerElement.setAttribute("Damage", String.valueOf(player.getDamage()));
           playerElement.setAttribute("Speed", String.valueOf(player.getSpeed()));
           playerElement.setAttribute("Money", String.valueOf(player.getMoney()));
           //Speichert Position des Spielers
           Element posPlayer = doc.createElement("Position");
           posPlayer.setAttribute("x", String.valueOf(player.getLocation().getX()));
           posPlayer.setAttribute("y", String.valueOf(player.getLocation().getY()));
           posPlayer.setAttribute("z", String.valueOf(player.getLocation().getZ()));
           playerElement.appendChild(posPlayer);
           
           rootElement.appendChild(playerElement);       
           
           TransformerFactory transformerFactory = TransformerFactory.newInstance();
           Transformer transformer = transformerFactory.newTransformer();
           DOMSource source = new DOMSource(doc);
           StreamResult result = new StreamResult(saveGame);
           transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Gibt die aktuelle Welle zurück. Wird gebraucht, damit beim GameOver-Screen angezeigt werden kann, wie viele Wellen man überlebt hat.
     * @return Aktuelle Welle
     */
   public int getCurrentWave() {
       return Main.app.getGame().getWave();
   }
   
   public void saveSettings() {
       nifty.gotoScreen("start");
   }
   
   public void toggleScroll() {
       Settings settings = Main.app.getSettings();
       
       //Text ändern
       de.lessvoid.nifty.elements.Element buttonOn = screen.findElementByName("enableScroll");
       de.lessvoid.nifty.elements.Element buttonOff = screen.findElementByName("disableScroll");
       if(buttonOn.isEnabled()) {
           buttonOn.disable();
           buttonOff.enable();
           settings.setUseScroll(true);
       } else {
           buttonOn.enable();
           buttonOff.disable();
           settings.setUseScroll(false);
       }  
       
       Main.app.deleteKeys();
       Main.app.setUpKeys();
   }
   
   public void gotoSettings() {
       nifty.gotoScreen("settings"); 
   }   
}
