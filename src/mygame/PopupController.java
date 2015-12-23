/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Controller;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.xml.xpp3.Attributes;
import java.util.Properties;

/**
 *
 * @author samuel
 */
public class PopupController implements Controller{

    private Nifty nifty;
    private Screen screen;
    private String price;
    private String health;
    private String damage;
    private String shotsPerSecond;
    
    public void bind(Nifty nifty, Screen screen, Element element, Properties parameter, Attributes controlDefinitionAttributes) {
        this.nifty = nifty;
        this.screen = screen;
    }

    public void init(Properties parameter, Attributes controlDefinitionAttributes) {

    }

    public void onStartScreen() {
        
    }

    public void onFocus(boolean getFocus) {

    }

    public boolean inputEvent(NiftyInputEvent inputEvent) {
        return false;
    }
    
    public void setTowerValues(String price, String health, String damage, String shotsPerSecond) {
        this.price = price;
        this.health = health;
        this.damage = damage;
        this.shotsPerSecond = shotsPerSecond;
    }

    public String getPrice() {
        return price;
    }

    public String getHealth() {
        return health;
    }

    public String getDamage() {
        return damage;
    }

    public String getShotsPerSecond() {
        return shotsPerSecond;
    }
    
    
    
}
