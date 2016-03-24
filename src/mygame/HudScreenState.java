
package mygame;

import mygame.Entitys.Beacon;
import mygame.Entitys.SimpleTower;
import mygame.Entitys.Player;
import mygame.Entitys.SloweringTower;
import mygame.Entitys.Tower;
import com.jme3.app.state.AbstractAppState;
import com.jme3.math.Vector3f;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.CheckBox;
import de.lessvoid.nifty.controls.Controller;
import de.lessvoid.nifty.controls.DropDown;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.xml.xpp3.Attributes;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Kontrolliert das HUD während das Spiel läuft. Es ist der ScreenController von allen Screens und Popups im hud.xml. Es öffnet und schliesst die Popups zum Turmupgraden
 * und am Ende jeder Welle. Auch ändert es Anzeigen im HUD und den Popups damit diese aktuelle Werte anzeigen. 
 * @author Samuel Martin 
 */
public class HudScreenState extends AbstractAppState implements ScreenController, Controller{
    private Nifty nifty;            //Das Niftyobject das mit der Methode bind() übergeben wird. Wird benötigt um auf die graphische Oberfläche zuzugreifen
    private Screen screen;          //Das Screenobject das mit der Methode bind() übergeben wird. Wird benötigt um auf die graphische Oberfläche zuzugreifen. Es repräsentiert den aktuell angezeigten Bildschirm.
    private SimpleDateFormat df;    //Das Simpledate welches benötigt wird um die aktuelle Uhrzeit im Format Stunden:Minuten anzuzeigen.
    private int itemSelected;       //Welcher Slot in der Leiste gerade ausgewählt ist.
    private String[] descriptions;   //Alle Beschreibungen der Items, die in der Leiste ausgewählt werden können.
    private Element towerPopup;     //Das Popup, welches erscheint, wenn man einen Turm upgraden will.
    private Element chooseTowerPopup;
    private ArrayList<Class<? extends Tower>> towerClasses;
    private Class<? extends Tower> towerClass;
    private HashMap<Class<? extends Tower>, String> classToLanguageString;
    private HashMap<String, Class<? extends Tower>> languageStringToClass;
    private Tower tower;            //Der Turm der geupgradet werden soll. Die Variable wird jedes Mal neu initialisiert.
    private Element endWavePopup;   //Das Popup das am Ende jeder Welle erscheint.
    private boolean cameraDragToRotate;     //Ist nur dann true, wenn bei der aktuellen anzeige DragToRotate der FlyByCamera true ist (z.B: bei Popup)
    private long startWaveTime;        //Die Startzeit der nächsten Welle. Diese wird immer gesetzt, wenn eine Welle vorbei ist. Sie wird benötigt, da nach dem Ende erste eine Bauzeit/phase kommt, bevor die nächste startet.
    private boolean buildPhase;     //Zeigt an ob gerade Bauzeit/phase ist.
    private boolean debugMode;       //Zeigt an ob gerade der Debugmodus aktiviert ist.
    
    /**
     * Konstruktor. Initialisiert alle Werte.
     */
    public HudScreenState() {
        itemSelected = 1;
        startWaveTime = 0;
        cameraDragToRotate = false;
        buildPhase = false;
        debugMode = true;
        df = new SimpleDateFormat("HH:mm");
        descriptions = Main.app.getSettings().getLanguageProperties().getProperty("itemDescriptions").split(",");
        classToLanguageString = new HashMap<>();
        languageStringToClass = new HashMap<>();
        
        towerClasses = new ArrayList<>();
        ArrayList<String> classNames = (ArrayList <String>) Main.app.getAssetManager().loadAsset("towerClasses.txt");
        for(int i = 0; i < classNames.size(); i ++){
            try {
                towerClasses.add(Class.forName("mygame.Entitys." + classNames.get(i)).asSubclass(Tower.class));
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        towerClass = SloweringTower.class;
        
        reloadLanguage();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void update(float tpf) {
        //Setzt Text im HUD
        if(buildPhase) {
            long time = startWaveTime-System.currentTimeMillis();
            updateText("wave", Main.app.getSettings().getLanguageProperty("reloadWave", "Nächste Welle in") + " " + (time/1000));
        } else {
            updateText("wave", (Main.app.getSettings().getLanguageProperty("wave", "Welle:") + " " + Main.app.getGame().getWave()));
        }
        updateText("time", (Main.app.getSettings().getLanguageProperty("time", "Uhrzeit:") + " " + df.format(System.currentTimeMillis())));
        updateText("money", (Main.app.getSettings().getLanguageProperty("money", "Geld:") + " " + Main.app.getWorld().getPlayer().getMoney() + "$"));
        if(getSelectedItemNum() <= 2){
            if(getSelectedItemNum() == 1)
                updateText("towerDescription", Main.app.getSettings().getLanguageProperty("SimpleTower"));
            else
                updateText("towerDescription", Main.app.getSettings().getLanguageProperty(towerClass.getSimpleName()));
        } else {
            updateText("towerDescription", descriptions[getSelectedItemNum()-3]);
        }
        updateText("beaconHealth", (Main.app.getWorld().getBeacon().getHealth()+"/"+Main.app.getWorld().getBeacon().getMaxHealth()));
        updateText("health", Main.app.getWorld().getPlayer().getHealth()+"/"+Main.app.getWorld().getPlayer().getMaxHealth());
        
        //Setzt HealthBar des Players
        Element healthPnl = screen.findElementByName("healthPnl");
        Element healthElement = screen.findElementByName("healthBar");
        healthElement.setWidth((int)(Main.app.getWorld().getPlayer().getHealthPercentage()/100f*healthPnl.getWidth()));
        //Setzt HealthBar des Beacon
        Element beaconHealthBar = screen.findElementByName("beaconHealthBar");
        Element topBar = screen.findElementByName("top_bar");
        beaconHealthBar.setWidth((int)(Main.app.getWorld().getBeacon().getHealthPercentage()/100f*topBar.getWidth()));
                
        if(debugMode) {
            Player player = Main.app.getWorld().getPlayer();
            double x = Math.round(player.getLocation().x*10.0)/10.0;
            double y = Math.round(player.getLocation().y*10.0)/10.0;
            double z = Math.round(player.getLocation().z*10.0)/10.0;
            updateText("playerPosition", Main.app.getSettings().getLanguageProperty("playerPosition", "Spielerposition: "));
            updateText("PlayerPos", "x: "+x+" y: "+y+" z: "+z);
        }
        
        if(startWaveTime != 0 && startWaveTime <= System.currentTimeMillis()) {
            startNextWave();
        }
        
        if(towerPopup != null && towerPopup.isEnabled()) {
           Main.app.getFlyByCamera().setEnabled(false);
        } else if(endWavePopup != null && endWavePopup.isEnabled()) {
           Main.app.getFlyByCamera().setEnabled(false);
        } else if (!Main.app.getFlyByCamera().isEnabled()){
            Main.app.getFlyByCamera().setEnabled(true);
        }
        if(chooseTowerPopup != null){
            Class c = languageStringToClass.get((String) chooseTowerPopup.findNiftyControl("#listBoxChooseTower", ListBox.class).getSelection().get(0));
            chooseTowerPopup.findElementByName("#chooseTowerDescriptionText").getRenderer(TextRenderer.class).setText(Main.app.getSettings().getLanguageProperty(c.getSimpleName() + "Description"));
        }
    }
    
    /**
     * Ändert den Text eins bestimment Elements in der GUI.
     * @param element String der Elementid
     * @param text Text, welche das Element anzeigen soll.
     */
    private void updateText(String element, String text) {
        screen.findElementByName(element).getRenderer(TextRenderer.class).setText(text);
    }
    
    /**
     * {@inheritDoc}
     */
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
        System.out.println("bind("+screen.getScreenId()+")");
        if(screen.getScreenId().equals("hud")){
            Main.app.getSettings().reloadLanguageHud(screen);
            reloadDescriptionsLanguage();
        }
        //Deaktiviert den Hilfebildschirm
        toggleHelpLayer();
        hideTowerInfo();
    }

    /**
     * {@inheritDoc}
     */
    public void onStartScreen() {
        if(screen != null && screen.getScreenId().equals("hud")){
            Main.app.getSettings().reloadLanguageHud(screen);
            reloadDescriptionsLanguage();
        }
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * {@inheritDoc}
     */
    public void onEndScreen() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Setzt Auswahl/Slot des Items in der Leiste auf die gegeben Zahl. Dies geschieht dadurch dass der Rahmen (ein Bild) des Itemslots geändert wird.
     * @param num  Auszuwählender Slot
     */
    public void selectItem(int num) {
        //Ersetzt zuletzt gewähltes Item mit einem normalen Rahmen
        NiftyImage img = nifty.getRenderEngine().createImage(screen, "Interface/item-frame.png", false);
        Element item = screen.findElementByName("item-"+itemSelected);
        item.getRenderer(ImageRenderer.class).setImage(img);

        //Ersetzt den Rahmen des neu gewählten Items mit dem markierten Rahmen.
        NiftyImage imgSel = nifty.getRenderEngine().createImage(screen, "Interface/item-frame-selected.png", false);
        Element itemSel = screen.findElementByName("item-"+num);
        itemSel.getRenderer(ImageRenderer.class).setImage(imgSel);

        itemSelected = num;
    }
    /**
     * Wählt die nächste Auswahl/Slot der Leiste
     */
    public void nextSelectedItem() {
        if(itemSelected == 5) {
            selectItem(1);
        } else {
            int nextItem = itemSelected+1;
            selectItem(nextItem);
        }
    }
    
     /**
     * Wählt die letzte (nicht chronologisch) Auswahl/Slot der Leiste
     */
    public void lastSelectedItem() {
        if(itemSelected == 1) {
            selectItem(5);
        } else {
            int lastItem =  itemSelected-1;
            selectItem(lastItem);
        }
    }
    
    /**
     * Gibt den geraden gewählten Turm zurück. Dieser Turm wird dann später gebaut. Falls etwas anderes als ein Turm im Itemslot ausgewählt wird sollte dies vorher 
     * herausgefiltert werden.
     * @param location Ort, an dem der Turm gebaut werden soll. 
     * @return gewählter Turm, der gebaut werden soll
     */
    public Tower getSelectedTower(Vector3f location, Vector3f up) {
        switch(itemSelected) {
            case(1):
                return new SimpleTower(location, up);
                
            case(2):
                try {
                    return towerClass.getConstructor(Vector3f.class, Vector3f.class).newInstance(location, up);
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
                    Logger.getLogger(HudScreenState.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            default:
                return new SimpleTower(location, up);
        }
    }
    
    /**
     * Gibt den gerade gewählten Itemslot zurück. Die Nummerierung fängt bei 1 an.
     * @return gewählter Itemslot.
     */
   public int getSelectedItemNum() {
       return itemSelected;
   }
   /**
    * Zeigt das Popup für ein Turmupgrade an. 
    * @param tower  Referenz auf den zu upgradenden Turm
    */
   public void showUpgradeTower(Tower tower) {
       this.tower = tower;
       int newLevel = tower.getLevel()+1;
       String price = tower.getUpgradePrice()+"$";
       String damage = tower.getDamage()+"+"+(tower.getNewDamage(newLevel)-tower.getDamage());
       if(tower.getSpatial().getName().equals("DeactivationTower")){
            damage = Main.app.getSettings().getLanguageProperty("textDamageDeactivationTower");
       }
       String health = tower.getHealth()+"/"+tower.getMaxHealth()+"+"+(tower.getNewHealth(newLevel)-tower.getHealth());
       String sps = tower.getShotsPerSecond()+"+"+Math.round((tower.getNewSPS(newLevel)-tower.getShotsPerSecond())*100)/100.0;
       String range = tower.getRange()+"+"+(tower.getNewRange(newLevel)-tower.getRange());
       
       
       if(tower.getLevel() >= 30){
            price = tower.getUpgradePrice()+"$";
            damage = Integer.toString(tower.getDamage());
            if(tower.getSpatial().getName().equals("DeactivationTower")){
                 damage = "Macht schiessunfähig";
            }
            health = tower.getHealth()+"/"+tower.getMaxHealth();
            sps = Double.toString(tower.getShotsPerSecond());
            range = Integer.toString(tower.getRange());
       }
       
       //Setzt Texte im Popup entsprechend den Werten des Turms
       Main.app.getFlyByCamera().setDragToRotate(true);
       cameraDragToRotate = true;
       towerPopup = nifty.createPopup("niftyPopupTower");
       Main.app.getSettings().reloadTowerPopupLanguage(towerPopup);
       if(tower.getLevel() >= 30){
           towerPopup.findElementByName("upgrade").setVisible(false);
           towerPopup.findElementByName("upgradeToMax").setVisible(false);
           towerPopup.findElementByName("textPrice").setVisible(false);
           towerPopup.findElementByName("price").setVisible(false);
       }
       towerPopup.findElementByName("#title").getRenderer(TextRenderer.class).setText(tower.getName() + " " + Main.app.getSettings().getLanguageProperty("levelOfTower") + " " + tower.getLevel());
       towerPopup.findElementByName("price").getRenderer(TextRenderer.class).setText(price);
       if(!tower.getSpatial().getName().equals("DeactivationTower")){
           towerPopup.findElementByName("damage").getRenderer(TextRenderer.class).setText(damage);
       } else {
           towerPopup.findElementByName("textDamage").getRenderer(TextRenderer.class).setText(damage);
           towerPopup.findElementByName("damage").getRenderer(TextRenderer.class).setText("");
           towerPopup.findElementByName("panelAllBombs").setVisible(false);
           towerPopup.findElementByName("panelNormalBombs").setVisible(false);
       }
       towerPopup.findElementByName("health").getRenderer(TextRenderer.class).setText(health);
       towerPopup.findElementByName("sps").getRenderer(TextRenderer.class).setText(sps);
       towerPopup.findElementByName("range").getRenderer(TextRenderer.class).setText(range);
       
       if(tower.getShootAt().equals("nearest")){
            towerPopup.findNiftyControl("nearest", CheckBox.class).check();
            towerPopup.findNiftyControl("furthest", CheckBox.class).uncheck();
            towerPopup.findNiftyControl("strongest", CheckBox.class).uncheck();
            towerPopup.findNiftyControl("weakest", CheckBox.class).uncheck();
       } else if (tower.getShootAt().equals("furthest")){
            towerPopup.findNiftyControl("nearest", CheckBox.class).uncheck();
            towerPopup.findNiftyControl("furthest", CheckBox.class).check();
            towerPopup.findNiftyControl("strongest", CheckBox.class).uncheck();
            towerPopup.findNiftyControl("weakest", CheckBox.class).uncheck();
       } else if (tower.getShootAt().equals("strongest")){
            towerPopup.findNiftyControl("nearest", CheckBox.class).uncheck();
            towerPopup.findNiftyControl("furthest", CheckBox.class).uncheck();
            towerPopup.findNiftyControl("strongest", CheckBox.class).check();
            towerPopup.findNiftyControl("weakest", CheckBox.class).uncheck();
       } else if (tower.getShootAt().equals("weakest")){
            towerPopup.findNiftyControl("nearest", CheckBox.class).uncheck();
            towerPopup.findNiftyControl("furthest", CheckBox.class).uncheck();
            towerPopup.findNiftyControl("strongest", CheckBox.class).uncheck();
            towerPopup.findNiftyControl("weakest", CheckBox.class).check();
       }
       DropDown shootAtBomb = towerPopup.findNiftyControl("dropdownShootAtBomb", DropDown.class);
       shootAtBomb.addAllItems(Main.app.getGame().getPossibleBombTypes());
       if (tower.getShootAtAllBombs()){
           shootAtBomb.selectItemByIndex(0);
       } else {
           shootAtBomb.selectItem(tower.getShootAtBombsString());
       }
       
       nifty.showPopup(screen, towerPopup.getId(), null);  
       Main.app.getWorld().setPaused(true);
       towerPopup.findElementByName("cancel").setFocus();
   }
   
   /**
    * Schliesst das Popup, welches mit {@link HudScreenState#showUpgradeTower(mygame.Entitys.Tower)} geöffnet, wurde wieder.
    * @param upgrade Gibt an ob der Turm upgegradet werden soll. Wenn der String "true" entspricht wird upgegradet.
    */
   public void closeTowerPopup(String upgrade) {
       //Abfrage ob tower == null ist, da es einen Aufruf gibt, bei dem tower und nifty == null sind. Das führt zu einer entsprechenden Fehlermeldung. Es ist unbekannt woher dieser Aufruf kommt, da darauf ein zweiten Aufruf
       //folgt bei dem tower != null ist. tower sollte auch != null sein, da er zuvor bei showUpgradeTower gesetzt wurde und dies dazwischen nicht mehr aufgerufen wurde.
       if(tower == null) {
           return;
       }
       setShootAtBombsClass();
       if(upgrade.equals("true")) {
           this.tower.upgrade();
       }
       nifty.closePopup(towerPopup.getId());
       towerPopup.disable();
       Main.app.getFlyByCamera().setDragToRotate(false);
       Main.app.getWorld().setPaused(false);
       cameraDragToRotate = false;
   }
    
   public void upgradeToMax(){
       if(tower == null){
           return;
       }
       this.closeTowerPopup("false");
       tower.upgradeToMax();
   }
   
    /**
     * Setzt auf welche Bombe geschossen werden soll. Es muss nur eine Variable true sein.
     * @param nearest Auf die nächste
     * @param furthest Auf die weiteste
     * @param strongest Auf die stärkste
     * @param weakest Auf die schwächste
     */
    public void setShootAt(String nearest, String furthest, String strongest, String weakest){
        if(tower == null){
            return;
        }
        CheckBox checkboxNearest = screen.findNiftyControl("nearest", CheckBox.class);
        CheckBox checkboxFurthest = screen.findNiftyControl("furthest", CheckBox.class);
        CheckBox checkboxStrongest = screen.findNiftyControl("strongest", CheckBox.class);
        CheckBox checkboxWeakest = screen.findNiftyControl("weakest", CheckBox.class);
        if(nearest.equals("true")){
            tower.setShootAt(true, false, false, false);
            checkboxNearest.check();
            checkboxFurthest.uncheck();
            checkboxStrongest.uncheck();
            checkboxWeakest.uncheck();
        } else if(furthest.equals("true")){
            tower.setShootAt(false, true, false, false);
            checkboxNearest.uncheck();
            checkboxFurthest.check();
            checkboxStrongest.uncheck();
            checkboxWeakest.uncheck();
        } else if(strongest.equals("true")){
            tower.setShootAt(false, false, true, false);
            checkboxNearest.uncheck();
            checkboxFurthest.uncheck();
            checkboxStrongest.check();
            checkboxWeakest.uncheck();
        } else if(weakest.equals("true")){
            tower.setShootAt(false, false, false, true);
            checkboxNearest.uncheck();
            checkboxFurthest.uncheck();
            checkboxStrongest.uncheck();
            checkboxWeakest.check();
        }
    }
    
    /**
     * Setzt auf welche Bomben geschossen werden soll.
     */
    public void setShootAtBombsClass(){
        if(tower == null){
            return;
        }
        DropDown shootAtBomb = screen.findNiftyControl("dropdownShootAtBomb", DropDown.class);
        if(shootAtBomb.getSelectedIndex() == 0){
            tower.setShootAtAllBombs();
            return;
        }
        tower.setShootAt(Main.app.getGame().getBombType((String) shootAtBomb.getSelection()), false);
    }
    
    public void openRemoveTowerPopup(){
        if(tower == null){
            return;
        }
        closeTowerPopup("false");
        Main.app.getFlyByCamera().setDragToRotate(true);
        cameraDragToRotate = true;
        towerPopup = nifty.createPopup("niftyPopupRemoveTower");
        Main.app.getSettings().reloadRemoveTowerPopupLanguage(towerPopup);
        towerPopup.findElementByName("no").setFocus();
        Main.app.getWorld().setPaused(true);
        nifty.showPopup(screen, towerPopup.getId(), null);
        towerPopup.findElementByName("no").setFocus();
    }
    
   /**
    * Entfernt den Turm. Dies wird vom Towerpopup aufgerufen, so dass der Turm, dessen Upgrade-Popup offen ist entfernt wird.
    */
    public void removeTower(){
        if(tower == null) {
            return;
        }
        closeTowerPopup("false");
        tower.remove();
    }
   
   /**
    * Gibt zurück ob der Mauszeiger direkt die Kamera dreht (false) , also gefangen ist oder nicht (true).
    * @return Gefangen = false; Maustaste muss gedrückt werden um Kamera zu drehen = true
    */
   public boolean isCameraDragToRotate() {
       return cameraDragToRotate;
   }
   
   /**
    * Zeigt Popup für das Ende der Welle an.
    */
   public void showEndWavePopup() {
       //EndWavePopup wird nicht ausgeführt, wenn der Beacon tot ist -> gameOver. Der Beacon ruft eine entsprechende Methode auf, wenn er stirbt.
       if(!Main.app.getWorld().getBeacon().isLiving()) {
           return;
       }
       //Schliesst ein evtl. geöffnetes TowerUpgradePopup
       if(towerPopup != null && towerPopup.isEnabled()) {
           closeTowerPopup("false");
       }
       Main.app.getWorld().setPaused(true);
       Main.app.getFlyByCamera().setDragToRotate(true);
       cameraDragToRotate = true;
       endWavePopup = nifty.createPopup("waveEndPopup");
       Main.app.getSettings().reloadEndWavePopupLanguage(endWavePopup);
       nifty.showPopup(screen, endWavePopup.getId(), null);
       Player player = Main.app.getWorld().getPlayer();
       Beacon beacon = Main.app.getWorld().getBeacon();
       String health = player.getMaxHealth()+"+"+(player.getNewMaxHealth()-player.getMaxHealth());
       String damage = player.getDamage() + "+" + (player.getNewDamage() - player.getDamage());
       String sps = Math.round(player.getSPS()*1000.0)/1000.0 + "+" + Math.round((player.getNewSPS()-player.getSPS())*1000.0)/1000.0;
       String range = player.getRange()+"+"+(player.getNewRange()-player.getRange());
       String speed = player.getSpeed()+"+"+(player.getNewSpeed()-player.getSpeed());
       String healPoints = player.getHealPoints()+"+"+(player.getNewHealPoints()-player.getHealPoints());
       String beaconHealth = beacon.getMaxHealth()+"+"+(beacon.getNewMaxHealth()-beacon.getMaxHealth());
       
       //Setzt richtige Werte, für die Upgrademöglichkeiten
       endWavePopup.findElementByName("#waveEnd").getRenderer(TextRenderer.class).setText(Main.app.getSettings().getLanguageProperty("endWave") + " "+(Main.app.getGame().getWave()-1));
       endWavePopup.findElementByName("#PLHealth").getRenderer(TextRenderer.class).setText(health);
       endWavePopup.findElementByName("#PLDamage").getRenderer(TextRenderer.class).setText(damage);
       endWavePopup.findElementByName("#PLSPS").getRenderer(TextRenderer.class).setText(sps);
       endWavePopup.findElementByName("#PLRange").getRenderer(TextRenderer.class).setText(range);
       endWavePopup.findElementByName("#PLSpeed").getRenderer(TextRenderer.class).setText(speed);
       endWavePopup.findElementByName("#PLHealPoints").getRenderer(TextRenderer.class).setText(healPoints);
       endWavePopup.findElementByName("#BeaconUpgrade").getRenderer(TextRenderer.class).setText(beaconHealth);
       
       endWavePopup.findElementByName("#nextWave").setFocus();
   }
   
   /**
    * Schliest das EndWavePopup und startet die Bauphase am Anfang der nächsten Welle.
    */
   public void nextWave() {
       //Erster Aufruf hat nifty == null. Herkunft des Aufrufs unbekannt
       if(nifty == null) {
           return;
       }
       Main.app.getWorld().setPaused(false);
       nifty.closePopup(endWavePopup.getId());
       endWavePopup.disable();
       Main.app.getFlyByCamera().setDragToRotate(false);
       cameraDragToRotate = false;
       startWaveTime = System.currentTimeMillis()+10000;
       buildPhase = true;
   }
   
   /**
    * Startet die nächste Welle, wenn die Bauzeit vorbei ist.
    */
   private void startNextWave() {
       Main.app.getGame().startWave();
       startWaveTime = 0;
       buildPhase = false;  
   }

   /**
    * Gibt zurück ob gerade Bauzeit/phase ist. Dies ist immer für eine gewisse Zeit, nach dem Ende jeder Welle der Fall.
    * @return ist Bauphase
    */
    public boolean isBuildPhase() {
        return buildPhase;
    }
   
    /**
     * Gibt den Preis zum Upgraden der Lebenspunkte des Spielers zurück.
     * @return Ugradepreis
     */
    public String getHealthPrice() {
        return String.valueOf(Main.app.getWorld().getPlayer().getNewMaxHealthPrice());
    }
    
    /**
     * Gibt den Preis zum Upgraden des Schadens, der der Spieler verursacht, zurück.
     * @return Ugradepreis
     */
    public String getDamagePrice() {
        return String.valueOf(Main.app.getWorld().getPlayer().getNewDamagePrice());
    }
    
    /**
     * Gibt den Preis zum Upgraden der Schiessgeschwindigkeit des Spielers zurück.
     * @return Ugradepreis
     */
    public String getSPSPrice() {
        return String.valueOf(Main.app.getWorld().getPlayer().getNewSPSPrice());
    }
    
    /**
     * Gibt den Preis zum Upgraden der Reichweite des Spielers zurück.
     * @return Ugradepreis
     */
    public String getRangePrice() {
        return String.valueOf(Main.app.getWorld().getPlayer().getNewRangePrice());
    }
    
    /**
     * Gibt den Preis zum Upgraden der Geschwindigkeit des Spielers zurück.
     * @return Ugradepreis
     */
    public String getSpeedPrice() {
        return String.valueOf(Main.app.getWorld().getPlayer().getNewSpeedPrice());
    }
    
    /**
     * Gibt den Preis zum Wiederbeleben zurück
     * @return Wiederbelebungspreis.
     */
    public String getRevivePrice() {
        return String.valueOf(Main.app.getWorld().getPlayer().getRevivePrice());
    }
    
    /**
     * Wiederbelebt den Spieler.
     */
    public void revivePlayer() {
        if(!Main.app.getWorld().getPlayer().isLiving()){
            Main.app.getWorld().getPlayer().revive();
        }
        reloadEndWavePopup();
    }
    
    /**
     * Upgradet die Lebenspunkte des Spielers.
     */
    public void upgradePlayerHealth() {
        Main.app.getWorld().getPlayer().increaseMaxHealth();
        reloadEndWavePopup();
    }
    
    /**
     * Upgradet den Schaden, den der Spieler verursacht.
     */
    public void upgradePlayerDamage() {
        Main.app.getWorld().getPlayer().increaseDamage();
        reloadEndWavePopup();
    }
    
    /**
     * Upgradet die Schussgeschwindigkeit des Spielers.
     */
    public void upgradePlayerSPS() {
        Main.app.getWorld().getPlayer().increaseSPS();
        reloadEndWavePopup();
    }
    
    /**
     * Upgradet die Reichweite des Spielers.
     */
    public void upgradePlayerRange() {
        Main.app.getWorld().getPlayer().increaseRange();
        reloadEndWavePopup();
    }
    
    /**
     * Upgradet die Geschwindigkeit des Spielers.
     */
    public void upgradePlayerSpeed() {
        Main.app.getWorld().getPlayer().increaseSpeed();
        reloadEndWavePopup();
    }
        
    /**
     * Upgradet die Lebenspunkte, die pro Updateloop geheilt werden.
     */
    public void upgradePlayerHealPoints() {
        Main.app.getWorld().getPlayer().increaseHealPoints();
        reloadEndWavePopup();
    }
    
    /**
     * Gibt den Preis zum Upgraden der Lebenspunkte, die pro Updateloop geheilt werden zurück
     * @return Upgradepreis
     */
    public String getHealPointsUpgradePrice() {
        return String.valueOf(Main.app.getWorld().getPlayer().getNewHealPointsPrice());
    }
    
    /**
     * Upgradet den Beacon.
     */
    public void upgradeBeacon() {
        Main.app.getWorld().getBeacon().increaseLevel();
        reloadEndWavePopup();
    }
    
    /**
     * Gibt den Preis zum Upgraden des Beacons zurück
     * @return Upgradepreis
     */
    public String getBeaconUpgradePrice() {
        return String.valueOf(Main.app.getWorld().getBeacon().getNewHealthPrice());
    }
    
    /**
     * Lädt das EndWavePopup neu, damit alle Wert darin neu geladen werden. Dies geschieht nach dem Upgraden eines Wertes im EndWavePopup
     */
    private void reloadEndWavePopup() {
        //Popup erneut laden, damit neue Werte gesetzt werden. Dafür muss es geschlossen werden, damit die Beschriftungen
        //der Knöpfe im xml neu geladen wird, da mir keine Methode bekannt ist die Beschriftung er Knöpfe im Java-Code
        //zu ändern
        if(nifty == null) {
            return;
        }
        nifty.closePopup(endWavePopup.getId());
        endWavePopup.disable();
        showEndWavePopup();
    }
   
    /**
     * Aktiviert oder Deaktiviert den Debugmodus. Dieser zeigt im Spiel die Speilerposition an. (Im mom. noch eher nutzlos)
     * @param flag true = aktivieren, false = deaktivieren
     */
    public void setDebugModeEnabled(boolean flag) {
        Element debugLayer =  screen.findElementByName("debug");
        debugLayer.setVisible(flag);
        debugMode = flag;
    }
    
    /**
     * Aktiviert oder Deaktiviert die Bauzeit/phase. Die gibt es nach dem Ende jeder Welle
     * @param buildPhase  true = aktivieren, false = deaktivieren
     */
    public void setBuildPhase(boolean buildPhase) {
        this.buildPhase = buildPhase;
    }

    /**
     * {@inheritDoc}
     */
    public void bind(Nifty nifty, Screen screen, Element element, Properties parameter, Attributes controlDefinitionAttributes) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

     /**
     * {@inheritDoc}
     */
    public void init(Properties parameter, Attributes controlDefinitionAttributes) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

     /**
     * {@inheritDoc}
     */
    public void onFocus(boolean getFocus) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

     /**
     * {@inheritDoc}
     */
    public boolean inputEvent(NiftyInputEvent inputEvent) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return true;
    }
    
    /**
     * Aktiviert oder Deaktiviert den Hilfebildschirm, je nach dem ob er zuvor aktiviert oder deaktiviert war.
     */
    public void toggleHelpLayer() {
        Main.app.getWorld().getPlayer().stopAudio();
        Element helpLayer = screen.findElementByName("help");
        if(helpLayer.isVisible()) {
            helpLayer.setVisible(false);
            Main.app.getFlyByCamera().setDragToRotate(false);
            cameraDragToRotate = false;
            Main.app.getWorld().setPaused(false);
        } else {
            helpLayer.setVisible(true);
            Main.app.getFlyByCamera().setDragToRotate(true);
            cameraDragToRotate = true;
            Main.app.getWorld().setPaused(true);
        }
    }
    
    /**
     * Zeigt die Information zum übergebenen Turm in der Bildschirmmitte an. Dabei wird das Fadenkreuz deaktiviert, damit es nicht im Weg ist.
     * @param tower  Turm, dessen Informationen angezeigt werden
     */
    public void showTowerInfo(Tower tower) {
        screen.findElementByName("towerInfoLayer").setVisible(true);
        updateText("#towerHealth", tower.getHealth()+"/"+tower.getMaxHealth());
        updateText("#towerDescription", tower.getName() + " " + Main.app.getSettings().getLanguageProperty("levelOfTower") + " " + tower.getLevel());
        
        Element towerInfoPanel = screen.findElementByName("#towerInfoPanel");
        Element towerHealthBar = screen.findElementByName("#towerHealthBar");
        Element towerLostHealth = screen.findElementByName("#towerLostHealth");
        
        towerHealthBar.setWidth((int)(tower.getHealthPercentage()/100f*towerInfoPanel.getWidth()));  
        screen.findElementByName("crosshair").setVisible(false);
   }
    
    /**
     * Macht die Turminformation in der Mitte des Bildschirms wieder unsichtbar und aktiviert wieder das Fadenkreuz.
     */
    public void hideTowerInfo() {
        screen.findElementByName("towerInfoLayer").setVisible(false);
        screen.findElementByName("crosshair").setVisible(true);
    }
    
    public void reloadDescriptionsLanguage(){
        descriptions = Main.app.getSettings().getLanguageProperty("itemDescriptions").split(",");
    }
    
    public void showChooseTowerPopup(){
        //Setzt Texte im Popup entsprechend den Werten des Turms
        Main.app.getFlyByCamera().setDragToRotate(true);
        cameraDragToRotate = true;
        chooseTowerPopup = nifty.createPopup("#niftyPopupChooseTower");
        Main.app.getSettings().reloadChooseTowerPopupLanguage(chooseTowerPopup);
        nifty.showPopup(screen, chooseTowerPopup.getId(), null);  
        chooseTowerPopup.findElementByName("#chooseTower").setFocus();
        ListBox listBoxChooseTower = chooseTowerPopup.findNiftyControl("#listBoxChooseTower", ListBox.class);
        for(int i = 0; i < towerClasses.size(); i++){
            listBoxChooseTower.addItem(classToLanguageString.get(towerClasses.get(i)));
        }
        Main.app.getWorld().setPaused(true);
    }
    
    public void chooseTower(){
        if(nifty == null){
            return;
        }
        towerClass = languageStringToClass.get((String) chooseTowerPopup.findNiftyControl("#listBoxChooseTower", ListBox.class).getSelection().get(0));
        selectItem(2);
        NiftyImage img = nifty.getRenderEngine().createImage(screen, "Interface/" + towerClass.getSimpleName() + ".png", false);
        Element towerImage = screen.findElementByName("towerImage");
        towerImage.getRenderer(ImageRenderer.class).setImage(img);
        nifty.closePopup(chooseTowerPopup.getId());
        chooseTowerPopup.disable();
        Main.app.getFlyByCamera().setDragToRotate(false);
        Main.app.getWorld().setPaused(false);
        cameraDragToRotate = false;
        chooseTowerPopup = null;
    }
    
    
    public void reloadLanguage(){
        for(int i = 0; i < towerClasses.size(); i++){
            classToLanguageString.put(towerClasses.get(i), Main.app.getSettings().getLanguageProperty(towerClasses.get(i).getSimpleName() + "Name", towerClasses.get(i).getSimpleName()));
            languageStringToClass.put(Main.app.getSettings().getLanguageProperty(towerClasses.get(i).getSimpleName() + "Name", towerClasses.get(i).getSimpleName()), towerClasses.get(i));
        }
    }
}
