package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.math.Vector3f;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.CheckBox;
import de.lessvoid.nifty.controls.DropDown;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.Slider;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;
import java.io.File;
import java.text.SimpleDateFormat;
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
                screen.findNiftyControl("checkboxScroll", CheckBox.class).check();
            } else {
                screen.findNiftyControl("checkboxScroll", CheckBox.class).uncheck();
            }
            if(Main.app.getSettings().isFullscreen()){
                screen.findNiftyControl("checkboxFullscreen", CheckBox.class).check();
            } else {
                screen.findNiftyControl("checkboxFullscreen", CheckBox.class).uncheck();
            }
            if(Main.app.getSettings().isVsync()){
                screen.findNiftyControl("checkboxVsync", CheckBox.class).check();
            } else {
                screen.findNiftyControl("checkboxVsync", CheckBox.class).uncheck();
            }
            screen.findNiftyControl("dropdownResolution", DropDown.class).addAllItems(Main.app.getSettings().getPossibleResolutionsStrings());
            screen.findNiftyControl("dropdownResolution", DropDown.class).selectItem(Main.app.getSettings().getActiveResolution());
            screen.findNiftyControl("dropdownColorDepth", DropDown.class).addAllItems(Main.app.getSettings().getPossibleColorDepthsStrings());
            screen.findNiftyControl("dropdownColorDepth", DropDown.class).selectItem(Main.app.getSettings().getActiveColorDepth());
            screen.findNiftyControl("dropdownAntiAliasing", DropDown.class).addAllItems(Main.app.getSettings().getPossibleAntiAliasingStrings());
            screen.findNiftyControl("dropdownAntiAliasing", DropDown.class).selectItem(Main.app.getSettings().getActiveAntiAliasing());
            
            screen.findNiftyControl("masterVolumeSlider", Slider.class).setValue((float) Main.app.getSettings().getVolumeMaster());
            screen.findNiftyControl("effectsVolumeSlider", Slider.class).setValue((float) Main.app.getSettings().getVolumeEffects());
            screen.findNiftyControl("musicVolumeSlider", Slider.class).setValue((float) Main.app.getSettings().getVolumeMusic());
            if(Main.app.getSettings().isVolumeMasterMuted()){
                screen.findNiftyControl("checkboxMuteMasterVolume", CheckBox.class).check();
            } else {
                screen.findNiftyControl("checkboxMuteMasterVolume", CheckBox.class).uncheck();
            }
            if(Main.app.getSettings().isVolumeEffectsMuted()){
                screen.findNiftyControl("checkboxMuteEffectsVolume", CheckBox.class).check();
            } else {
                screen.findNiftyControl("checkboxMuteEffectsVolume", CheckBox.class).uncheck();
            }
            if(Main.app.getSettings().isVolumeMusicMuted()){
                screen.findNiftyControl("checkboxMuteMusicVolume", CheckBox.class).check();
            } else {
                screen.findNiftyControl("checkboxMuteMusicVolume", CheckBox.class).uncheck();
            }
        } else if (screen.getScreenId().equals("highscores")){
            reloadHighscores();
        } else if (screen.getScreenId().equals("credits")){
            loadCredits();
        } else if (screen.getScreenId().equals("keyBindings")){
            Settings settings = Main.app.getSettings();
            updateButtonText("forward", settings.getKeyString(settings.getKey("forward")));
            updateButtonText("backward", settings.getKeyString(settings.getKey("backward")));
            updateButtonText("goLeft", settings.getKeyString(settings.getKey("goLeft")));
            updateButtonText("goRight", settings.getKeyString(settings.getKey("goRight")));
            updateButtonText("jump", settings.getKeyString(settings.getKey("jump")));
            updateButtonText("item_1", settings.getKeyString(settings.getKey("item_1")));
            updateButtonText("item_2", settings.getKeyString(settings.getKey("item_2")));
            updateButtonText("item_3", settings.getKeyString(settings.getKey("item_3")));
            updateButtonText("item_4", settings.getKeyString(settings.getKey("item_4")));
            updateButtonText("item_5", settings.getKeyString(settings.getKey("item_5")));
            updateButtonText("help", settings.getKeyString(settings.getKey("help")));
        } else if (screen.getScreenId().equals("chooseScene")){
            screen.findNiftyControl("listBoxScene", ListBox.class).addAllItems(Main.app.getPossibleSceneNames());
            screen.findNiftyControl("listBoxScene", ListBox.class).selectItemByIndex(0);
            screen.findElementByName("#startGame").setFocus();
        } else if (screen.getScreenId().equals("chooseSave")){
            screen.findNiftyControl("listBoxSave", ListBox.class).addAllItems(Main.app.getPossibleSaveNames());
            screen.findNiftyControl("listBoxSave", ListBox.class).selectItemByIndex(0);
            screen.findElementByName("#loadGame").setFocus();
        }
    }

    /**
     * {@inheritDoc }
     */
    public void onStartScreen() {
        if(screen.getScreenId().equals("gameOver")) {
            screen.findElementByName("untilWave").getRenderer(TextRenderer.class).setText(Main.app.getSettings().getLanguageProperty("untilWave1") + " " + getCurrentWave()+ " " + Main.app.getSettings().getLanguageProperty("untilWave2"));    
        }
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
    }
    
    /**
     * Startet das Spiel.
     */
    public void startGame() {
        Main.getWorld().setScene((String) screen.findNiftyControl("listBoxScene", ListBox.class).getFocusItem());
        nifty.gotoScreen("hud");
        //Spiel fortsetzen
        continueGame();
        
        //Spielstand zurücksetzen.
        Main.app.getWorld().setPaused(false);
        Main.app.getFlyByCamera().setDragToRotate(false);
        Main.app.getWorld().getBeacon().setLevel(1);
        Main.app.getWorld().getBeacon().setLiving(true);
        Main.app.getWorld().getBeacon().setHealth(Main.app.getWorld().getBeacon().getMaxHealth());
        Main.app.getRootNode().attachChild(Main.app.getWorld().getBeacon().getSpatial());
        Main.app.getWorld().getBeacon().setCollidable();
        Main.app.getWorld().getPlayer().setLocation(new Vector3f(0,10,0));
        Main.app.getWorld().getPlayer().revive();
        Main.app.getWorld().getPlayer().turn();
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
         //InfoBildschirm anzeigen
        Main.app.getHudState().toggleHelpLayer();
    }
    
    public void chooseScene(){
        nifty.gotoScreen("chooseScene");
    }
    
    /**
     * Beendet das Spiel.
     */
    public void quitGame() {
       saveGame();
       Main.app.getHighscores().saveHighscores();
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
        File saveGame = new File("saves/" + (String) screen.findNiftyControl("listBoxSave", ListBox.class).getFocusItem() + ".save");
        String [] d = ((String) screen.findNiftyControl("listBoxSave", ListBox.class).getFocusItem()).split(";")[0].split("-");
        String [] t = ((String) screen.findNiftyControl("listBoxSave", ListBox.class).getFocusItem()).split(";")[1].split("-");
        Cryption crypt = new Cryption(d[2] + "S" + t[1] + "v" + d[0] + "S" + d[1] + "F" + t[0]);
        crypt.decrypt(saveGame, saveGame);
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
                Element upEle = (Element) towerElement.getElementsByTagName("Up").item(0);
                Vector3f up = new Vector3f();
                up.setX(Float.valueOf(upEle.getAttribute("x")));
                up.setY(Float.valueOf(upEle.getAttribute("y")));
                up.setZ(Float.valueOf(upEle.getAttribute("z")));
                switch (towerElement.getAttribute("Type")) {
                    case "mygame.Entitys.SimpleTower":
                        tower = new SimpleTower(position, up);
                        break;
                    case "mygame.Entitys.SloweringTower":
                        tower = new SloweringTower(position, up);
                        break;
                    case "mygame.Entitys.DeactivationTower":
                        tower = new DeactivationTower(position, up);
                        break;
                    default:
                        tower = new SimpleTower(position, up);
                        break;
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
            
            //Setzt Scene
            Main.getWorld().setScene(rootElement.getAttribute("SceneName"));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        crypt.encrypt(saveGame, saveGame);
        
        continueGame();
    }
    
    public void chooseSave(){
        nifty.gotoScreen("chooseSave");
    }
    
    public void deleteSave(){
        File file = new File("saves/" + (String) screen.findNiftyControl("listBoxSave", ListBox.class).getFocusItem() + ".save");
        file.delete();
        screen.findNiftyControl("listBoxSave", ListBox.class).removeItem(file.getName().substring(0, file.getName().length()-5));
        Main.app.updateSaveNames();
        screen.findNiftyControl("listBoxSave", ListBox.class).selectItemByIndex(0);
    }
    
    /**
     * Speichert den aktuellen Spielstand.
     */
    private void saveGame() {
         SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy;HH-mm");
         String fileName = Main.getWorld().getScene().getName() + ";" + df.format(System.currentTimeMillis());
         File saveGame = new File("saves/" + fileName + ".save");
         try{
            saveGame.createNewFile();
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            Element rootElement = doc.createElement("SaveGame");
            rootElement.setAttribute("SceneName", Main.getWorld().getScene().getName());
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
                position.setAttribute("x", String.valueOf(towers.get(i).getSpatial().getLocalTranslation().getX()));
                position.setAttribute("y", String.valueOf(towers.get(i).getSpatial().getLocalTranslation().getY()));
                position.setAttribute("z", String.valueOf(towers.get(i).getSpatial().getLocalTranslation().getZ()));
                tower.appendChild(position);
                Element up = doc.createElement("Up");
                up.setAttribute("x", String.valueOf(towers.get(i).getUp().getX()));
                up.setAttribute("y", String.valueOf(towers.get(i).getUp().getY()));
                up.setAttribute("z", String.valueOf(towers.get(i).getUp().getZ()));
                tower.appendChild(up);
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
         
        String [] d = fileName.split(";")[1].split("-");
        String [] t = fileName.split(";")[2].split("-");
        Cryption crypt = new Cryption(d[2] + "S" + t[1] + "v" + d[0] + "S" + d[1] + "F" + t[0]);
        crypt.encrypt(saveGame, saveGame);
    }
    
    /**
     * Gibt die aktuelle Welle zurück. Wird gebraucht, damit beim GameOver-Screen angezeigt werden kann, wie viele Wellen man überlebt hat.
     * @return Aktuelle Welle
     */
   public int getCurrentWave() {
       return Main.app.getGame().getWave();
   }
   
   public void saveSettings() {
        DropDown dropdownResolution = screen.findNiftyControl("dropdownResolution", DropDown.class);
        Main.app.getSettings().setResolution(Main.app.getSettings().getPossibleResolutions().get(dropdownResolution.getSelectedIndex()));
        Main.app.getSettings().setFullscreen(screen.findNiftyControl("checkboxFullscreen", CheckBox.class).isChecked());
        Main.app.getSettings().setVsync(screen.findNiftyControl("checkboxVsync", CheckBox.class).isChecked());
//            Main.app.getSettings().setColorDepth(Main.app.getSettings().getPossibleColorDepths().get(screen.findNiftyControl("dropdownColorDepth", DropDown.class).getSelectedIndex()));
        Main.app.getSettings().setAntiAliasing(Main.app.getSettings().getPossibleAntiAliasing().get(screen.findNiftyControl("dropdownAntiAliasing", DropDown.class).getSelectedIndex()));
        Main.app.restart();
        Main.app.getSettings().setVolumeMaster((double) screen.findNiftyControl("masterVolumeSlider", Slider.class).getValue());
        Main.app.getSettings().setVolumeEffects((double) screen.findNiftyControl("effectsVolumeSlider", Slider.class).getValue());
        Main.app.getSettings().setVolumeMusic((double) screen.findNiftyControl("musicVolumeSlider", Slider.class).getValue());
        Main.app.getSettings().setVolumeMasterMuted(screen.findNiftyControl("checkboxMuteMasterVolume", CheckBox.class).isChecked());
        Main.app.getSettings().setVolumeEffectsMuted(screen.findNiftyControl("checkboxMuteEffectsVolume", CheckBox.class).isChecked());
        Main.app.getSettings().setVolumeMusicMuted(screen.findNiftyControl("checkboxMuteMusicVolume", CheckBox.class).isChecked());
        Main.app.getSettings().saveSettings();
        nifty.gotoScreen("start");
   }
   
   public void saveKeyBindings(){
       if(!Main.app.getSettings().hasAnyKeyMultipleTimes()){
           gotoSettings();
       }
   }
   
   /**
    * Wechselt zwischen aktivierten und deaktivieren Mausrad. Das Mausrad kann gebraucht werden um zwischen den einzelnen Slots in der Leiste zu wechseln.
    */
   public void toggleScroll() {
       Settings settings = Main.app.getSettings();
       
       //Text ändern
       CheckBox cb = screen.findNiftyControl("checkboxScroll", CheckBox.class);
       if(cb.isChecked()) {
           cb.uncheck();
           settings.setUseScroll(false);
       } else {
           cb.check();
           settings.setUseScroll(true);
       }
       
       Main.app.reloadKeys();
   }
   
   /**
    * Wechselt zum Einstellungsbildschirm.
    */
   public void gotoSettings() {
       nifty.gotoScreen("settings");
   }
   
   public void gotoKeyBindings() {
       nifty.gotoScreen("keyBindings");
   }
   
   /**
    * Ändert die Tastaturbelegung.
    * @param keyBinding Welche Aktion neu belegt wird
    */
   //http://olavz.com/nifty-gui-create-key-bindings-for-your-game-in-jmonkey-engine-3/
   private SettingsInputHandler settingsInputHandler;
   public void setKeyBinding(String eventId) {
       Screen screen = nifty.getScreen("keyBindings");
       if(settingsInputHandler != null) {
           Button button = screen.findNiftyControl(settingsInputHandler.getEventId(), Button.class);
           button.setText("");
           Main.app.getInputManager().removeRawInputListener(settingsInputHandler);
       }
       Button button = screen.findNiftyControl(eventId, Button.class);
       button.setText(Main.app.getSettings().getLanguageProperty("pressAnyKey"));
       settingsInputHandler = new SettingsInputHandler(this, eventId);
       Main.app.getInputManager().addRawInputListener(settingsInputHandler);
       deactivateKeyBindingsButtonsExept(eventId);
   }
   
   /**
    * Wird aufgerufen sobald eine Taste gedrückt wurde, wenn der SettingsInputHandler aktiviert ist.
    * @param evt  Tastendruck
    * @param eventId  Welche Aktion neu belegt werden soll
    */
   //http://olavz.com/nifty-gui-create-key-bindings-for-your-game-in-jmonkey-engine-3/
   public void keyBindCallBack(KeyInputEvent evt, String eventId) {
      /* Callback, triggered from settingsKeyHandler */ 
      Screen screen = nifty.getScreen("keyBindings");
      Button button = screen.findNiftyControl(eventId, Button.class);
      String keyName = Main.app.getSettings().getKeyString(evt.getKeyCode());
      //Setzt nur dann die neue Taste, wenn es einen gültigen KeyName bekommt, wenn dies der Fall ist, ist es wohl eine Spezialtaste, die leicht 
//      zu komplikationen führen könnte.
      if(!keyName.equals("")) {
          button.setText(keyName);
          Main.app.getSettings().setKey(eventId, evt.getKeyCode());
          Main.app.reloadKeys();
      }
      if(Main.app.getSettings().hasAnyKeyMultipleTimes()){
           screen.findNiftyControl("saveKeyBindings", Button.class).setText(Main.app.getSettings().getLanguageProperty("sameKeyBindingsWarining"));
           screen.findNiftyControl("saveKeyBindings", Button.class).setTextColor(new Color(1, 0, 0, 1));
      } else {
           screen.findNiftyControl("saveKeyBindings", Button.class).setText(Main.app.getSettings().getLanguageProperty("saveKeyBindings"));
           screen.findNiftyControl("saveKeyBindings", Button.class).setTextColor(Color.WHITE);
      }
      activateKeyBindingsButtonsExept(eventId);
      setAllMultipleKeyBindingsButtonsRed();
//       System.out.print(evt.getKeyChar()+"; ");
//       System.out.print(String.valueOf(evt.getKeyChar()).toUpperCase()+"; ");
//       System.out.println(evt.getKeyCode());
      
      Main.app.getInputManager().removeRawInputListener(settingsInputHandler);
      settingsInputHandler = null;
   }
   
   private void deactivateKeyBindingsButtonsExept(String buttonId) {
        if(!buttonId.equals("forward"))
            screen.findElementByName("forward").setVisible(false);
        if(!buttonId.equals("backward"))
            screen.findElementByName("backward").setVisible(false);
        if(!buttonId.equals("goLeft"))
            screen.findElementByName("goLeft").setVisible(false);
        if(!buttonId.equals("goRight"))
            screen.findElementByName("goRight").setVisible(false);
        if(!buttonId.equals("jump"))
            screen.findElementByName("jump").setVisible(false);
        if(!buttonId.equals("item_1"))
            screen.findElementByName("item_1").setVisible(false);
        if(!buttonId.equals("item_2"))
            screen.findElementByName("item_2").setVisible(false);
        if(!buttonId.equals("item_3"))
            screen.findElementByName("item_3").setVisible(false);
        if(!buttonId.equals("item_4"))
            screen.findElementByName("item_4").setVisible(false);
        if(!buttonId.equals("item_5"))
            screen.findElementByName("item_5").setVisible(false);
        if(!buttonId.equals("help"))
            screen.findElementByName("help").setVisible(false);
   }
   
   private void activateKeyBindingsButtonsExept (String buttonId) {
        if(!buttonId.equals("forward"))
            screen.findElementByName("forward").setVisible(true);
        if(!buttonId.equals("backward"))
            screen.findElementByName("backward").setVisible(true);
        if(!buttonId.equals("goLeft"))
            screen.findElementByName("goLeft").setVisible(true);
        if(!buttonId.equals("goRight"))
            screen.findElementByName("goRight").setVisible(true);
        if(!buttonId.equals("jump"))
            screen.findElementByName("jump").setVisible(true);
        if(!buttonId.equals("item_1"))
            screen.findElementByName("item_1").setVisible(true);
        if(!buttonId.equals("item_2"))
            screen.findElementByName("item_2").setVisible(true);
        if(!buttonId.equals("item_3"))
            screen.findElementByName("item_3").setVisible(true);
        if(!buttonId.equals("item_4"))
            screen.findElementByName("item_4").setVisible(true);
        if(!buttonId.equals("item_5"))
            screen.findElementByName("item_5").setVisible(true);
        if(!buttonId.equals("help"))
            screen.findElementByName("help").setVisible(true);
   }
   
   private void setKeyBindingsButtonRedIfMultiple (String buttonId){
       if(Main.app.getSettings().hasKeyMultipleTimes(Main.app.getSettings().getKey(buttonId))){
           screen.findNiftyControl(buttonId, Button.class).setTextColor(new Color(1, 0, 0, 1));
       } else {
           screen.findNiftyControl(buttonId, Button.class).setTextColor(Color.WHITE);
       }
   }
   
   private void setAllMultipleKeyBindingsButtonsRed (){
       setKeyBindingsButtonRedIfMultiple("forward");
       setKeyBindingsButtonRedIfMultiple("backward");
       setKeyBindingsButtonRedIfMultiple("goLeft");
       setKeyBindingsButtonRedIfMultiple("goRight");
       setKeyBindingsButtonRedIfMultiple("jump");
       setKeyBindingsButtonRedIfMultiple("item_1");
       setKeyBindingsButtonRedIfMultiple("item_2");
       setKeyBindingsButtonRedIfMultiple("item_3");
       setKeyBindingsButtonRedIfMultiple("item_4");
       setKeyBindingsButtonRedIfMultiple("item_5");
       setKeyBindingsButtonRedIfMultiple("help");
   }
   
   private void updateButtonText(String id, String text) {
       screen.findNiftyControl(id, Button.class).setText(text);
       nifty.gotoScreen("settings"); 
   }
   
   public void restoreDefaultSettings(){
        Main.app.getSettings().loadDefaultSettings(false);
        Main.app.restart();

        screen.findNiftyControl("checkboxScroll", CheckBox.class).uncheck();
        screen.findNiftyControl("checkboxFullscreen", CheckBox.class).check();
        screen.findNiftyControl("dropdownResolution", DropDown.class).selectItem(Main.app.getAppSettings().getWidth() + " x " + Main.app.getAppSettings().getHeight());
        screen.findNiftyControl("checkboxVsync", CheckBox.class).uncheck();
        screen.findNiftyControl("dropdownColorDepth", DropDown.class).selectItem(Main.app.getAppSettings().getBitsPerPixel());
        screen.findNiftyControl("dropdownAntiAliasing", DropDown.class).selectItemByIndex(0);
        screen.findNiftyControl("masterVolumeSlider", Slider.class).setValue(1);
        screen.findNiftyControl("effectsVolumeSlider", Slider.class).setValue(1);
        screen.findNiftyControl("musicVolumeSlider", Slider.class).setValue(1);
        screen.findNiftyControl("checkboxMuteMasterVolume", CheckBox.class).uncheck();
        screen.findNiftyControl("checkboxMuteEffectsVolume", CheckBox.class).uncheck();
        screen.findNiftyControl("checkboxMuteMusicVolume", CheckBox.class).uncheck();
        
        nifty.gotoScreen("start");
   }
   
   public void resetKeyBindings(){
        Main.app.getSettings().setKeys(Main.app.getSettings().getDefaultKeys());
        Settings settings = Main.app.getSettings();
        updateButtonText("forward", settings.getKeyString(settings.getKey("forward")));
        updateButtonText("backward", settings.getKeyString(settings.getKey("backward")));
        updateButtonText("goLeft", settings.getKeyString(settings.getKey("goLeft")));
        updateButtonText("goRight", settings.getKeyString(settings.getKey("goRight")));
        updateButtonText("jump", settings.getKeyString(settings.getKey("jump")));
        updateButtonText("item_1", settings.getKeyString(settings.getKey("item_1")));
        updateButtonText("item_2", settings.getKeyString(settings.getKey("item_2")));
        updateButtonText("item_3", settings.getKeyString(settings.getKey("item_3")));
        updateButtonText("item_4", settings.getKeyString(settings.getKey("item_4")));
        updateButtonText("item_5", settings.getKeyString(settings.getKey("item_5")));
        updateButtonText("help", settings.getKeyString(settings.getKey("help")));
        setAllMultipleKeyBindingsButtonsRed();
        screen.findNiftyControl("saveKeyBindings", Button.class).setText("Zurück zu den Einstellungen");
        screen.findNiftyControl("saveKeyBindings", Button.class).setTextColor(Color.WHITE);
   }
   
   /**
    * Wechselt zum Highscoresbildschirm.
    */
   public void gotoHighscores(){
       nifty.gotoScreen("highscores");
   }
   
   /**
    * Löscht alle Highscores.
    */
   public void clearHighscores(){
       Main.app.getHighscores().deleteAllHighscores();
       reloadHighscores();
   }
   
   /**
    * Aktualisiert den Text des Highscoresbildschirms.
    */
   public void reloadHighscores(){
       Highscores highscores = Main.app.getHighscores();
       SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy '; ' HH:mm");
       int j = 25;
       if(highscores.getAllHighscores().size() < 25){
           j = highscores.getAllHighscores().size();
       }
       for(int i = 0; i < j; i ++){
           screen.findElementByName((i+1) + "place").getRenderer(TextRenderer.class).setText("  " + String.valueOf(i+1));
           screen.findElementByName("name" + (i+1)).getRenderer(TextRenderer.class).setText("   " + highscores.getHighscore(i+1).getName());
           screen.findElementByName("wave" + (i+1)).getRenderer(TextRenderer.class).setText("   " + String.valueOf(highscores.getHighscore(i+1).getWave()));
           screen.findElementByName("date" + (i+1)).getRenderer(TextRenderer.class).setText("   " + df.format(highscores.getHighscore(i+1).getDate()));
           screen.findElementByName("world" + (i+1)).getRenderer(TextRenderer.class).setText("  " + highscores.getHighscore(i+1).getWorld());
       }
       for(int i = j; i < 25; i ++){
           screen.findElementByName((i+1) + "place").getRenderer(TextRenderer.class).setText("");
           screen.findElementByName("name" + (i+1)).getRenderer(TextRenderer.class).setText("");
           screen.findElementByName("wave" + (i+1)).getRenderer(TextRenderer.class).setText("");
           screen.findElementByName("date" + (i+1)).getRenderer(TextRenderer.class).setText("");
           screen.findElementByName("world" + (i+1)).getRenderer(TextRenderer.class).setText("");
       }
   }
   
   public void gotoCredits(){
       nifty.gotoScreen("credits");
   }
   
   public void loadCredits(){
       ArrayList<String> text = (ArrayList<String>) Main.app.getAssetManager().loadAsset(Main.app.getSettings().getLanguageProperty("credits"));
       
       for(int i = 0; i < text.size(); i ++){
           screen.findElementByName((i+1) + "credits").getRenderer(TextRenderer.class).setText(text.get(i));
       }
   }
   
   public void toggleFullscreen(){
       screen.findNiftyControl("checkboxFullscreen", CheckBox.class).toggle();
   }
   
   public void toggleVsync(){
       screen.findNiftyControl("checkboxVsync", CheckBox.class).toggle();
   }
   
   public void toggleMasterVolumeMuted(){
       screen.findNiftyControl("checkboxMuteMasterVolume", CheckBox.class).toggle();
       if(screen.findNiftyControl("checkboxMuteMasterVolume", CheckBox.class).isChecked()){
           screen.findNiftyControl("masterVolumeSlider", Slider.class).disable();
           screen.findNiftyControl("effectsVolumeSlider", Slider.class).disable();
           screen.findNiftyControl("musicVolumeSlider", Slider.class).disable();
           screen.findNiftyControl("checkboxMuteEffectsVolume", CheckBox.class).disable();
           screen.findNiftyControl("checkboxMuteMusicVolume", CheckBox.class).disable();
       } else {
           screen.findNiftyControl("masterVolumeSlider", Slider.class).enable();
           screen.findNiftyControl("effectsVolumeSlider", Slider.class).enable();
           screen.findNiftyControl("musicVolumeSlider", Slider.class).enable();
           screen.findNiftyControl("checkboxMuteEffectsVolume", CheckBox.class).enable();
           screen.findNiftyControl("checkboxMuteMusicVolume", CheckBox.class).enable();
       }
   }
   
   public void toggleEffectsVolumeMuted(){
       screen.findNiftyControl("checkboxMuteEffectsVolume", CheckBox.class).toggle();
       if(screen.findNiftyControl("checkboxMuteEffectsVolume", CheckBox.class).isChecked()){
           screen.findNiftyControl("effectsVolumeSlider", Slider.class).disable();
       } else {
           screen.findNiftyControl("effectsVolumeSlider", Slider.class).enable();
       }
   }
   
   public void toggleMusicVolumeMuted(){
       screen.findNiftyControl("checkboxMuteMusicVolume", CheckBox.class).toggle();
       if(screen.findNiftyControl("checkboxMuteMusicVolume", CheckBox.class).isChecked()){
           screen.findNiftyControl("musicVolumeSlider", Slider.class).disable();
       } else {
           screen.findNiftyControl("musicVolumeSlider", Slider.class).enable();
       }
   }
}
