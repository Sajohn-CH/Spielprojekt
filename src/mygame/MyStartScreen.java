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
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.KeyInputHandler;
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
import mygame.Entitys.MGTower;
import mygame.Entitys.Player;
import mygame.Entitys.PyramidTower;
import mygame.Entitys.SimpleTower;
import mygame.Entitys.Tower;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author samuel
 */
public class MyStartScreen extends AbstractAppState implements ScreenController, KeyInputHandler{
    private Nifty nifty;
    
    
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        //TODO: initialize your AppState, e.g. attach spatials to rootNode
        //this is called on the OpenGL thread after the AppState has been attached
    }
    
    @Override
    public void update(float tpf) {
         //TODO: implement behavior during runtime
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
    }

    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        System.out.println("bind " + screen.getScreenId());
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void onStartScreen() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void onEndScreen() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    /** custom methods */
    public void startGame(String nextScreen) {
        nifty.gotoScreen(nextScreen);
        Main.app.getFlyByCamera().setDragToRotate(Main.app.getHudState().isCameraDragToRotate());
        Main.getWorld().setPaused(false);
    }
    
    public void quitGame() {
       saveGame();
       Main.app.stop();
    }
    
    public String getPlayerName() {
        return String.valueOf(System.currentTimeMillis());
    }
    
    public void toStart() {
        nifty.gotoScreen("start");
    }

    //From KeyInputHandler
    public boolean keyEvent(NiftyInputEvent inputEvent) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public String getScore() {
        return "0";
    }
    
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
                    tower = new MGTower(position);
                } else {
                    tower = new PyramidTower(position);
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
        
        startGame("hud");
    }
    
    /**
     * Speichert den aktuellen Spielstand
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
}
