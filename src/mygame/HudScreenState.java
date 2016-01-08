/*t
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
 *
 * @author samuel
 */
public class HudScreenState extends AbstractAppState implements ScreenController, Controller{
    private Nifty nifty;
    private Screen screen;
    private SimpleDateFormat df = new SimpleDateFormat("HH:mm");
    private int itemSelected = 1;
    private World world;
    //private long lastSelectionChanged; //Zeit, als das letzte Mal die Auswahl geändert wurde. Wird gebraucht um die Anzeige der Turmbeschreibung nach eine Zeitspannen verschwinden zu lassen

    private String[] descriptions = {"Zerstört Bomben: 20$", "Verlangsamt Bomben: 30$", "Macht schiessende Bomben schiessunfähig: 100$", "Upgraden", "Heilen: 1$ pro Lebenspunkt"};

    private Element towerPopup;
    private Tower tower;
    private Element endWavePopup;
    private boolean cameraDragToRotate = false;     //Ist nur dann true, wenn bei der aktuellen anzeige DragToRotate der FlyByCamera true ist (z.B: bei Popup)
    long startWaveTime =  0;
    private boolean buildPhase = false;
    private boolean debugMode = true;
       
    public void setWorld(World world) {
        this.world = world;
    }
    
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
        updateText("money", ("Geld: " + Main.getWorld().getPlayer().getMoney() + "$"));
        updateText("towerDescription", descriptions[getSelectedItemNum()-1]);
        updateText("beaconHealth", (world.getBeacon().getHealth()+"/"+world.getBeacon().getMaxHealth()));
        updateText("health", world.getPlayer().getHealth()+"/"+world.getPlayer().getMaxHealth());
        
        //Setzt HealthBar des Players
        Element healthPnl = screen.findElementByName("healthPnl");
        Element healthElement = screen.findElementByName("healthBar");
        healthElement.setWidth((int)(world.getPlayer().getHealthPercentage()/100f*healthPnl.getWidth()));
        //Setzt HealthBar des Beacon
        Element beaconHealthBar = screen.findElementByName("beaconHealthBar");
        Element topBar = screen.findElementByName("top_bar");
        beaconHealthBar.setWidth((int)(world.getBeacon().getHealthPercentage()/100f*topBar.getWidth()));
                
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
    }
    
    private void updateText(String element, String text) {
        screen.findElementByName(element).getRenderer(TextRenderer.class).setText(text);
    }
    
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
        System.out.println("bind("+screen.getScreenId()+")");        
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void onStartScreen() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void onEndScreen() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public String getScore() {
        return "0";
    }
    /**
     * Setzt Auswahl/Slot des Items in der Leiste auf die gegeben Zahl
     * @param num  Auszuwählender Slot
     */
    public void selectItem(int num) {
        //Merke Zeitpunkt
//        lastSelectionChanged = System.currentTimeMillis();
        NiftyImage img = nifty.getRenderEngine().createImage(screen, "Interface/item-frame.png", false);
        Element item = screen.findElementByName("item-"+itemSelected);
        item.getRenderer(ImageRenderer.class).setImage(img);
        
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
       
       Main.app.getFlyByCamera().setDragToRotate(true);
       cameraDragToRotate = true;
       towerPopup = nifty.createPopup("niftyPopupTower");
       if(tower.getLevel() >= 30){
           towerPopup.findElementByName("upgrade").setVisible(false);
       }
       towerPopup.findElementByName("#title").getRenderer(TextRenderer.class).setText(tower.getName() + " Stufe " + tower.getLevel());
       towerPopup.findElementByName("price").getRenderer(TextRenderer.class).setText(price);
       if(!tower.getSpatial().getName().equals("DeactivationTower")){
           towerPopup.findElementByName("damage").getRenderer(TextRenderer.class).setText(damage);
       } else {
           towerPopup.findElementByName("textDamage").getRenderer(TextRenderer.class).setText(damage);
           towerPopup.findElementByName("damage").getRenderer(TextRenderer.class).setText("");
       }
       towerPopup.findElementByName("health").getRenderer(TextRenderer.class).setText(health);
       towerPopup.findElementByName("sps").getRenderer(TextRenderer.class).setText(sps);
       towerPopup.findElementByName("range").getRenderer(TextRenderer.class).setText(range);
       nifty.showPopup(screen, towerPopup.getId(), null);  
       Main.app.getWorld().setPaused(true);
   }
   
   /**
    * Schliesst das Popup, welches mit {@link HudScreenState#showUpgradeTower(mygame.Tower, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)} geöffnet, wurde wieder.
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
     
    public void removeTower(){
        if(tower == null) {
            return;
        }
        closeTowerPopup("false");
        tower.remove();
    }
   
   public boolean isCameraDragToRotate() {
       return cameraDragToRotate;
   }
   
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
       String beaconHealth = beacon.getMaxHealth()+"+"+(beacon.getNewMaxHealth()-beacon.getMaxHealth());
       
       endWavePopup.findElementByName("#waveEnd").getRenderer(TextRenderer.class).setText("Ende der Welle "+(Main.app.getGame().getWave()-1));
       endWavePopup.findElementByName("#PLHealth").getRenderer(TextRenderer.class).setText(health);
       endWavePopup.findElementByName("#PLDamage").getRenderer(TextRenderer.class).setText(damage);
       endWavePopup.findElementByName("#PLSPS").getRenderer(TextRenderer.class).setText(sps);
       endWavePopup.findElementByName("#PLRange").getRenderer(TextRenderer.class).setText(range);
       endWavePopup.findElementByName("#PLSpeed").getRenderer(TextRenderer.class).setText(speed);
       endWavePopup.findElementByName("#BeaconUpgrade").getRenderer(TextRenderer.class).setText(beaconHealth);
       
   }
   
   public void nextWave() {
       //Erster Aufruf hat nifty == null. Herkunft unbekannt
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
   
   private void startNextWave() {
       Main.app.getGame().startWave();
       startWaveTime = 0;
       buildPhase = false;  
   }

    public boolean isBuildPhase() {
        return buildPhase;
    }
   
    public String getHealthPrice() {
        return String.valueOf(Main.app.getWorld().getPlayer().getNewMaxHealthPrice());
    }
    
    public String getDamagePrice() {
        return String.valueOf(Main.app.getWorld().getPlayer().getNewDamagePrice());
    }
    
    public String getSPSPrice() {
        return String.valueOf(Main.app.getWorld().getPlayer().getNewSPSPrice());
    }
    
    public String getRangePrice() {
        return String.valueOf(Main.app.getWorld().getPlayer().getNewRangePrice());
    }
    
    public String getSpeedPrice() {
        return String.valueOf(Main.app.getWorld().getPlayer().getNewSpeedPrice());
    }
    
    public void upgradePlayerHealth() {
        Main.app.getWorld().getPlayer().increaseMaxHealth();
        reloadEndWavePopup();
    }
    
    public void upgradePlayerDamage() {
        Main.app.getWorld().getPlayer().increaseDamage();
        reloadEndWavePopup();
    }
    
    public void upgradePlayerSPS() {
        Main.app.getWorld().getPlayer().increaseSPS();
        reloadEndWavePopup();
    }
    
    public void upgradePlayerRange() {
        Main.app.getWorld().getPlayer().increaseRange();
        reloadEndWavePopup();
    }
    
    public void upgradePlayerSpeed() {
        Main.app.getWorld().getPlayer().increaseSpeed();
        reloadEndWavePopup();
    }
    
    public void upgradeBeacon() {
        Main.app.getWorld().getBeacon().increaseLevel();
        reloadEndWavePopup();
    }
    
    public String getBeaconUpgradePrice() {
        return String.valueOf(Main.app.getWorld().getBeacon().getNewHealthPrice());
    }
    
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
   
    public void setDebugModeEnabled(boolean flag) {
        Element debugLayer =  screen.findElementByName("debug");
        debugLayer.setVisible(flag);
        debugMode = flag;
    }
    
    public void setBuildPhase(boolean buildPhase) {
        this.buildPhase = buildPhase;
    }
    
    public void setStartWaveTime(long startWaveTime) {
        this.startWaveTime = startWaveTime;
    }

    public void bind(Nifty nifty, Screen screen, Element element, Properties parameter, Attributes controlDefinitionAttributes) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void init(Properties parameter, Attributes controlDefinitionAttributes) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void onFocus(boolean getFocus) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean inputEvent(NiftyInputEvent inputEvent) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return true;
    }
}
