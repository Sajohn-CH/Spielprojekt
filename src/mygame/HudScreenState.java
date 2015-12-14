/*t
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.state.AbstractAppState;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.text.SimpleDateFormat;


/**
 *
 * @author samuel
 */
public class HudScreenState extends AbstractAppState implements ScreenController{
    private Nifty nifty;
    private Screen screen;
    private SimpleDateFormat df = new SimpleDateFormat("HH:mm");
    private int itemSelected = 1;
//    private Player player;
    private World world;
    
//    public void setPlayer(Player player){
//        this.player = player;
//    }
    
    public void setWorld(World world) {
        this.world = world;
    }
    
    @Override
    public void update(float tpf) {
        //Setze Uhrzeit
        Element timeElement = screen.findElementByName("time");
        timeElement.getRenderer(TextRenderer.class).setText("Uhrzeit: "+df.format(System.currentTimeMillis()));
        //Setze Geldmenge
        Element moneyElement = screen.findElementByName("money");
        moneyElement.getRenderer(TextRenderer.class).setText("Geld: 100$");
        //Setzt HealthBar des Players
        Element healthPnl = screen.findElementByName("healthPnl");
        Element healthElement = screen.findElementByName("healthBar");
        healthElement.setWidth((int)(world.getPlayer().getHealthPercentage()/100f*healthPnl.getWidth()));
        Element healthText = screen.findElementByName("health");
        healthText.getRenderer(TextRenderer.class).setText(world.getPlayer().getHealthPercentage()+"%");
        //Setzt HealthBar des Beacon
        Element beaconHealthBar = screen.findElementByName("beaconHealthBar");
        Element topBar = screen.findElementByName("top_bar");
        beaconHealthBar.setWidth((int)(world.getBeacon().getHealthPercentage()/100f*topBar.getWidth()));
        Element beaconHealth = screen.findElementByName("beaconHealth");
        beaconHealth.getRenderer(TextRenderer.class).setText(world.getBeacon().getHealthPercentage()+"%");
        
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
        //
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
     
}
