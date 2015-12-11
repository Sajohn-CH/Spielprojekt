/*t
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.state.AbstractAppState;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author samuel
 */
public class HudScreenState extends AbstractAppState implements ScreenController{
    private Nifty nifty;
    private Screen screen;
    
    @Override
    public void update(float tpf) {
        //System.out.println("loop");
        Element niftyElement = screen.findElementByName("time");
        niftyElement.getRenderer(TextRenderer.class).setText("time: "+String.valueOf(System.currentTimeMillis()));
        //TODO: implement behavior during runtime
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
    
}
