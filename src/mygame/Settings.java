package mygame;

import com.jme3.input.KeyInput;
import com.jme3.system.AppSettings;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

/**
 * Kontrolliert die Einstellungen des Spiels. Dies beinhaltet vorallem die Tastaturbelegung. Ist aktuell noch nicht zu 100% implementiert und funktionsfähig.
 * @author Samuel Martin
 */
public class Settings {
    
    private GraphicsDevice device;
//    private Dimension resolution;
//    private int frameRate;
    private boolean fullscreen;
    private String[] keysItems;        //Tasten für die Schnellzugrife auf der Leiste unten. Für jeden Slot einen. Es gibt 5.
    private String[] keysWalking;      //Tasten zum Laufen. Reihenfolge, left, right, up, down.
    private String keyJump;             //Taste zum Springen
    private String keyDebug;         //Taste um den Debugbildschirm aufzurufen
    private boolean useScroll;          //Gibt an ob das Mausrad zum Scrollen der Slots in der Leiste genutzt werden soll.
    
    /**
     * Konstruktor. Belegt die Einstellungen mit Standarwerten.
     */
    public Settings(){
        device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        fullscreen = Main.app.getAppSettings().isFullscreen();
        
        keysItems = new String[5];
        keysItems[0] ="1";
        keysItems[1] ="2";
        keysItems[2] ="3";
        keysItems[3] ="4";
        keysItems[4] ="5";
        
        keysWalking = new String[4];
        keysWalking[0] = "A";
        keysWalking[1] = "D";
        keysWalking[2] = "W";
        keysWalking[3] = "S";
        keyJump = " ";

        keyDebug = "F4";
    }
    
    /**
     * Gibt den KeyCode der Taste die das übergebene Zeichen erzeugt zurück. Keine Pfeiltasten, da diese Standardmässig für die Kamera verwendet werden.
     * @param key Das Zeichen der gesuchten Taste
     * @return KeyCode der Taste
     */
    public int getKeyCode(String key){
        int code = 0;
        if(key == null)
            return 0;
        if(key.equals(" "))
            code = KeyInput.KEY_SPACE;
        else if(key.equals( "A"))
            code = KeyInput.KEY_A;

        else if(key.equals( "B"))
            code = KeyInput.KEY_B;

        else if(key.equals( "C"))
            code = KeyInput.KEY_C;

        else if(key.equals( "D"))
            code = KeyInput.KEY_D;

        else if(key.equals( "E"))
            code = KeyInput.KEY_E;

        else if(key.equals( "F"))
            code = KeyInput.KEY_F;

        else if(key.equals( "G"))
            code = KeyInput.KEY_G;

        else if(key.equals( "H"))
            code = KeyInput.KEY_H;

        else if(key.equals( "I"))
            code = KeyInput.KEY_I;

        else if(key.equals( "J"))
            code = KeyInput.KEY_J;

        else if(key.equals( "K"))
            code = KeyInput.KEY_K;

        else if(key.equals( "L"))
            code = KeyInput.KEY_L;

        else if(key.equals( "M"))
            code = KeyInput.KEY_M;

        else if(key.equals( "N"))
            code = KeyInput.KEY_N;

        else if(key.equals( "O"))
            code = KeyInput.KEY_O;

        else if(key.equals( "P"))
            code = KeyInput.KEY_P;

        else if(key.equals( "Q"))
            code = KeyInput.KEY_Q;

        else if(key.equals( "R"))
            code = KeyInput.KEY_R;

        else if(key.equals( "S"))
            code = KeyInput.KEY_S;

        else if(key.equals( "T"))
            code = KeyInput.KEY_T;

        else if(key.equals( "U"))
            code = KeyInput.KEY_U;

        else if(key.equals( "V"))
            code = KeyInput.KEY_V;

        else if(key.equals( "W"))
            code = KeyInput.KEY_W;

        else if(key.equals( "X"))
            code = KeyInput.KEY_X;

        else if(key.equals( "Y"))
            code = KeyInput.KEY_Y;

        else if(key.equals( "Z"))
            code = KeyInput.KEY_Z;

        else if(key.equals( "0"))
            code = KeyInput.KEY_0;

        else if(key.equals( "1"))
            code = KeyInput.KEY_1;

        else if(key.equals( "2"))
            code = KeyInput.KEY_2;

        else if(key.equals( "3"))
            code = KeyInput.KEY_3;

        else if(key.equals( "4"))
            code = KeyInput.KEY_4;

        else if(key.equals( "5"))
            code = KeyInput.KEY_5;

        else if(key.equals( "6"))
            code = KeyInput.KEY_6;

        else if(key.equals( "7"))
            code = KeyInput.KEY_7;

        else if(key.equals( "8"))
            code = KeyInput.KEY_8;

        else if(key.equals( "9"))
            code = KeyInput.KEY_9;

        else if(key.equals( "F1"))
            code = KeyInput.KEY_F1;

        else if(key.equals( "F2"))
            code = KeyInput.KEY_F2;

        else if(key.equals( "F3"))
            code = KeyInput.KEY_F3;

        else if(key.equals( "F4"))
            code = KeyInput.KEY_F4;

        else if(key.equals( "F5"))
            code = KeyInput.KEY_F5;

        else if(key.equals( "F6"))
            code = KeyInput.KEY_F6;

        else if(key.equals( "F7"))
            code = KeyInput.KEY_F7;

        else if(key.equals( "F8"))
            code = KeyInput.KEY_F8;

        else if(key.equals( "F9"))
            code = KeyInput.KEY_F9;

        else if(key.equals( "F10"))
            code = KeyInput.KEY_F10;

        else if(key.equals( "F11"))
            code = KeyInput.KEY_F11;

        else if(key.equals( "F12"))
            code = KeyInput.KEY_F12;

        else if(key.equals( "F13"))
            code = KeyInput.KEY_F13;

        else if(key.equals( "F14"))
            code = KeyInput.KEY_F14;

        else if(key.equals( "F15"))
            code = KeyInput.KEY_F15;

        else if(key.equals( "RSHIFT"))
            code = KeyInput.KEY_RSHIFT;

        else if(key.equals( "LSHIFT"))
            code = KeyInput.KEY_LSHIFT;

        else if(key.equals( "BACK"))
            code = KeyInput.KEY_BACK;

        else if(key.equals( "PGUP"))
            code = KeyInput.KEY_PGUP;

        else if(key.equals( "PGDOWN"))
            code = KeyInput.KEY_PGDN;
        return code;
    }

    /**
     * Gibt die Tasten für die einzelnen Slots der Leiste zurück.
     * @return Tasten 
     */
    public String[] getKeysItems() {
        return keysItems;
    }

    /**
     * Setzt die Tasten für die einzelnen Slots der Leiste.
     * @param keys_items Tasten für die Slots.
     */
    public void setKeysItems(String[] keys_items) {
        this.keysItems = keys_items;
    }

    /**
     * Gibt die Tasten, die zum Laufen benötigt werden zurück.
     * @return Tasten
     */
    public String[] getKeysWalking() {
        return keysWalking;
    }

    /**
     * Setzt die Tasten, die zum Laufen benötigt werden.
     * @param keys_walking Tasten zum Laufen
     */
    public void setKeysWalking(String[] keys_walking) {
        this.keysWalking = keys_walking;
    }

    /**
     * Gibt die Taste für um den Debugmodus zu aktivieren zurück.
     * @return Taste
     */
    public String getKeyDebug() {
        return keyDebug;
    }

    /**
     * Setzt die Taste um den Debugmodus zu aktivieren.
     * @param key_debug Taste für Debugmodus
     */
    public void setKeyDebug(String key_debug) {
        this.keyDebug = key_debug;
    }

    /**
     * Gibt an ob das Mausrad gebraucht wird um zwischen den Slots der Leiste zu wechseln.
     * @return Mausrad aktiv
     */
    public boolean isUseScroll() {
        return useScroll;
    }

    /**
     * Setzt ob das Mausrad gebraucht wird um zwischen den Slots der Leiste zu wechseln.
     * @param useScroll Ist Mausrad aktiviert?
     */
    public void setUseScroll(boolean useScroll) {
        this.useScroll = useScroll;
    }

    /**
     * Gibt die Taste zurück, die zum Springen gedrückt werden muss.
     * @return Taste zum Springen
     */
    public String getKeyJump() {
        return keyJump;
    }

    /**
     * Setzt die Taste, die zum Springen gedrückt werden muss.
     * @param keyJump Taste zum Springen
     */
    public void setKeyJump(String keyJump) {
        this.keyJump = keyJump;
    }
    
    public boolean isFullscreen(){
        return fullscreen;
    }
    
    public void setFullscreen(boolean enable){
        AppSettings appSettings = Main.app.getAppSettings();
        this.fullscreen = enable;
        if(device.isFullScreenSupported()){
            appSettings.setFullscreen(enable);
        }
        Main.app.setSettings(appSettings);
    }
}
