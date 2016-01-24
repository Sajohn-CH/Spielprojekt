
package mygame;

import mygame.Entitys.Beacon;
import mygame.Entitys.SimpleTower;
import mygame.Entitys.Player;
import mygame.Entitys.SloweringTower;
import mygame.Entitys.DeactivationTower;
import mygame.Entitys.Tower;
import com.jme3.app.state.AbstractAppState;
import com.jme3.math.Vector3f;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Controller;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.xml.xpp3.Attributes;
import java.text.SimpleDateFormat;
import java.util.Properties;


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
        descriptions = new String[5];
        descriptions[0] = "Zerstört Bomben: 100$";
        descriptions[1] = "Verlangsamt Bomben: 150$";
        descriptions[2] = "Macht schiessende Bomben schiessunfähig: 200$";
        descriptions[3] = "Upgraden";
        descriptions[4] = "Heilen: 1$ pro Lebenspunkt";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void update(float tpf) {
        //Setzt Text im HUD
        if(buildPhase) {
            long time = startWaveTime-System.currentTimeMillis();
            updateText("wave", "Nächste Welle in "+(time/1000));
        } else {
            updateText("wave", ("Welle: " + Main.getGame().getWave()));
        }
        updateText("time", ("Uhrzeit: "+df.format(System.currentTimeMillis())));
        updateText("money", ("Geld: " + Main.app.getWorld().getPlayer().getMoney() + "$"));
        updateText("towerDescription", descriptions[getSelectedItemNum()-1]);
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
        //Deaktiviert den Hilfebildschirm
        toggleHelpLayer();
    }

    /**
     * {@inheritDoc}
     */
    public void onStartScreen() {
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
     * Gibt dem geraden gewählten Turm zurück. Dieser Turm wird dann später gebaut. Falls etwas anderes als ein Turm im Itemslot ausgewählt wird sollte dies vorher 
     * herausgefiltert werden.
     * @param location Ort, an dem der Turm gebaut werden soll. 
     * @return gewählter Turm, der gebaut werden soll
     */
    public Tower getSelectedTower(Vector3f location) {
        switch(itemSelected) {
            case(1):
                return new SimpleTower(location);
                
            case(2):
                return new SloweringTower(location);
                
            case(3):
                return new DeactivationTower(location);
                
            default:
                return new SimpleTower(location);
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
            damage = "Macht schiessunfähig";
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
       if(tower.getLevel() >= 30){
           towerPopup.findElementByName("upgrade").setVisible(false);
           towerPopup.findElementByName("upgradeToMax").setVisible(false);
           towerPopup.findElementByName("textPrice").setVisible(false);
           towerPopup.findElementByName("price").setVisible(false);
       }
       towerPopup.findElementByName("#title").getRenderer(TextRenderer.class).setText(tower.getName() + " Stufe " + tower.getLevel());
       towerPopup.findElementByName("price").getRenderer(TextRenderer.class).setText(price);
       if(!tower.getSpatial().getName().equals("DeactivationTower")){
           towerPopup.findElementByName("damage").getRenderer(TextRenderer.class).setText(damage);
       } else {
           towerPopup.findElementByName("textDamage").getRenderer(TextRenderer.class).setText(damage);
           towerPopup.findElementByName("damage").getRenderer(TextRenderer.class).setText("");
           towerPopup.findElementByName("#buttonsShootingBomb").setVisible(false);
       }
       towerPopup.findElementByName("health").getRenderer(TextRenderer.class).setText(health);
       towerPopup.findElementByName("sps").getRenderer(TextRenderer.class).setText(sps);
       towerPopup.findElementByName("range").getRenderer(TextRenderer.class).setText(range);
       
       if(tower.getShootAt().equals("nearest")){
            towerPopup.findElementByName("nearest").disable();
            towerPopup.findElementByName("furthest").enable();
            towerPopup.findElementByName("strongest").enable();
            towerPopup.findElementByName("weakest").enable();
       } else if (tower.getShootAt().equals("furthest")){
            towerPopup.findElementByName("nearest").enable();
            towerPopup.findElementByName("furthest").disable();
            towerPopup.findElementByName("strongest").enable();
            towerPopup.findElementByName("weakest").enable();
       } else if (tower.getShootAt().equals("strongest")){
            towerPopup.findElementByName("nearest").enable();
            towerPopup.findElementByName("furthest").enable();
            towerPopup.findElementByName("strongest").disable();
            towerPopup.findElementByName("weakest").enable();
       } else if (tower.getShootAt().equals("weakest")){
            towerPopup.findElementByName("nearest").enable();
            towerPopup.findElementByName("furthest").enable();
            towerPopup.findElementByName("strongest").enable();
            towerPopup.findElementByName("weakest").disable();
       }
       if(tower.getShootAtShootingBombs()){
           towerPopup.findElementByName("shootingBombs").disable();
       } else if (tower.getShootAtAllBombs()){
           towerPopup.findElementByName("allBombs").disable();
       } else if (tower.getShootAtNormalBombs()){
           towerPopup.findElementByName("normalBombs").disable();
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
        de.lessvoid.nifty.elements.Element buttonNearest = screen.findElementByName("nearest");
        de.lessvoid.nifty.elements.Element buttonFurthest = screen.findElementByName("furthest");
        de.lessvoid.nifty.elements.Element buttonStrongest = screen.findElementByName("strongest");
        de.lessvoid.nifty.elements.Element buttonWeakest = screen.findElementByName("weakest");
        if(nearest.equals("true")){
            tower.setShootAt(true, false, false, false);
            buttonNearest.disable();
            buttonFurthest.enable();
            buttonStrongest.enable();
            buttonWeakest.enable();
        } else if(furthest.equals("true")){
            tower.setShootAt(false, true, false, false);
            buttonNearest.enable();
            buttonFurthest.disable();
            buttonStrongest.enable();
            buttonWeakest.enable();
        } else if(strongest.equals("true")){
            tower.setShootAt(false, false, true, false);
            buttonNearest.enable();
            buttonFurthest.enable();
            buttonStrongest.disable();
            buttonWeakest.enable();
        } else if(weakest.equals("true")){
            tower.setShootAt(false, false, false, true);
            buttonNearest.enable();
            buttonFurthest.enable();
            buttonStrongest.enable();
            buttonWeakest.disable();
        }
    }
    
    /**
     * Setzt, dass nur auf ShootingBombs geschossen werden soll.
     */
    public void setShootAtShootingBombs(){
        if(tower == null){
            return;
        }
        de.lessvoid.nifty.elements.Element buttonAll = screen.findElementByName("allBombs");
        de.lessvoid.nifty.elements.Element buttonShootingBombs = screen.findElementByName("shootingBombs");
        de.lessvoid.nifty.elements.Element buttonNormalBombs = screen.findElementByName("normalBombs");
        buttonAll.enable();
        buttonShootingBombs.disable();
        buttonNormalBombs.enable();
        tower.setShootOnlyAtShootingBombs();
    }
    
    /**
     * Setzt, dass nur auf normale Bomben geschossen werden soll.
     */
    public void setShootAtNormalBombs(){
        if(tower == null){
            return;
        }
        de.lessvoid.nifty.elements.Element buttonAll = screen.findElementByName("allBombs");
        de.lessvoid.nifty.elements.Element buttonShootingBombs = screen.findElementByName("shootingBombs");
        de.lessvoid.nifty.elements.Element buttonNormalBombs = screen.findElementByName("normalBombs");
        buttonAll.enable();
        buttonShootingBombs.enable();
        buttonNormalBombs.disable();
        tower.setShootOnlyAtNormalBombs();
    }
    
    /**
     * Setzt, dass nur auf ShootingBombs geschossen werden soll.
     */
    public void setShootAtAllBombs(){
        if(tower == null){
            return;
        }
        de.lessvoid.nifty.elements.Element buttonAll = screen.findElementByName("allBombs");
        de.lessvoid.nifty.elements.Element buttonShootingBombs = screen.findElementByName("shootingBombs");
        de.lessvoid.nifty.elements.Element buttonNormalBombs = screen.findElementByName("normalBombs");
        buttonAll.disable();
        buttonShootingBombs.enable();
        buttonNormalBombs.enable();
        tower.setShootAtAllBombs();
    }
    
    
    public void openRemoveTowerPopup(){
        if(tower == null){
            return;
        }
        closeTowerPopup("false");
        Main.app.getFlyByCamera().setDragToRotate(true);
        cameraDragToRotate = true;
        towerPopup = nifty.createPopup("niftyPopupRemoveTower");
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
       endWavePopup.findElementByName("#waveEnd").getRenderer(TextRenderer.class).setText("Ende der Welle "+(Main.app.getGame().getWave()-1));
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
}
