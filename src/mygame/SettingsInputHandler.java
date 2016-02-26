package mygame;

import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;

/**
 * Abgeleitet von http://olavz.com/nifty-gui-create-key-bindings-for-your-game-in-jmonkey-engine-3/.
 * @author samuel
 */
public class SettingsInputHandler implements RawInputListener{
   MyStartScreen appState;
   String eventId;
    
    public SettingsInputHandler(MyStartScreen appState, String eventId) {
        this.appState = appState;
        this.eventId = eventId;
        
    }
    
    public String getEventId() {
        return eventId;
    }

    public void beginInput() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void endInput() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void onJoyAxisEvent(JoyAxisEvent evt) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void onJoyButtonEvent(JoyButtonEvent evt) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void onMouseMotionEvent(MouseMotionEvent evt) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void onMouseButtonEvent(MouseButtonEvent evt) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void onKeyEvent(KeyInputEvent evt) {
        try{
            appState.keyBindCallBack(evt, eventId);
        } catch (Exception ex) {
            
        }
    }

    public void onTouchEvent(TouchEvent evt) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
