package mygame;

import com.jme3.input.KeyInput;
import com.jme3.system.AppSettings;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Kontrolliert die Einstellungen des Spiels. Dies beinhaltet vorallem die Tastaturbelegung. Ist aktuell noch nicht zu 100% implementiert und funktionsfähig.
 * @author Samuel Martin
 */
public class Settings {
    
    private GraphicsDevice device;
    private Dimension resolution;
    private boolean fullscreen;
    private boolean vsync;
    private int colorDepth;
    private int antiAliasing;
    
    private double volumeMaster;
    private double volumeEffects;
    private double volumeMusic;
    private boolean volumeMasterIsMuted;
    private boolean volumeEffectsIsMuted;
    private boolean volumeMusicIsMuted;
//    private int frameRate;
    private String[] keysItems;        //Tasten für die Schnellzugrife auf der Leiste unten. Für jeden Slot einen. Es gibt 5.
    private String[] keysWalking;      //Tasten zum Laufen. Reihenfolge, left, right, up, down.
    private String keyJump;             //Taste zum Springen
    private String keyDebug;         //Taste um den Debugbildschirm aufzurufen
    private boolean useScroll;          //Gibt an ob das Mausrad zum Scrollen der Slots in der Leiste genutzt werden soll.
    private HashMap<String, Integer> keys;
    private Properties languageProperties;
    
    final private HashMap<String, Integer> KEYBOARDKEYS;
    
    /**
     * Konstruktor. Belegt die Einstellungen mit Standarwerten.
     */
    public Settings(){
        languageProperties = new Properties();
        device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                
        keys = new HashMap();
//        keys = getDefaultKeys();
        loadSettings();
        
        KEYBOARDKEYS = new HashMap();
        loadKEYBOARDKEYS();
        
        keysItems = new String[5];
        keysItems[0] = getKeyString(getKey("item_1"));
        keysItems[1] = getKeyString(getKey("item_2"));
        keysItems[2] = getKeyString(getKey("item_3"));
        keysItems[3] = getKeyString(getKey("item_4"));
        keysItems[4] = getKeyString(getKey("item_5"));
        
        keysWalking = new String[4];
        keysWalking[0] = getKeyString(getKey("goLeft"));
        keysWalking[1] = getKeyString(getKey("goRight"));
        keysWalking[2] = getKeyString(getKey("forward"));
        keysWalking[3] = getKeyString(getKey("backward"));
        keyJump = getKeyString(getKey("jump"));

        keyDebug = getKeyString(getKey("debug"));
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
    
    /**
     * Setzt eine neue Taste für eine bestimmte Aktion
     * @param eventId Aktion
     * @param keyCode neue Taste
     */
    public void setKey(String eventId, int keyCode) {
        keys.put(eventId, keyCode);
        return;
    }
    
    public void setKeys(HashMap<String, Integer> keys){
        this.keys = keys;
    }
    
    /**
     * Gibt den KeyCode für eine bestimmte Aktion zurück
     * @param eventId Aktion    
     * @return KeyCode
     */
    public int getKey(String eventId) {
        return keys.get(eventId);
        
    }
    
    public boolean hasKey(int keyCode){
        return keys.containsValue(keyCode);
    }
    
    public boolean hasKeyMultipleTimes(int keyCode){
        int multiple = 0;
        for (Map.Entry<String, Integer> entry: keys.entrySet()) {
            if(entry.getValue() == keyCode){
                multiple++;
            }
            if(multiple >= 2){
                return true;
            }
        }
        return false;
    }
    
    public boolean hasAnyKeyMultipleTimes(){
        for (Map.Entry<String, Integer> entry: keys.entrySet()) {
            if(hasKeyMultipleTimes(entry.getValue())){
                return true;
            }
        }
        return false;
    }
    
    /**
     * Gitb den String, der den gegebenen KeyCode beschreibt, zurück
     * @param keyCode KeyCode der Taste
     * @return String, der Taste bzw. KeyCode beschreibt 
     */
    public String getKeyString(int keyCode) {
        for (Map.Entry<String, Integer> entry: KEYBOARDKEYS.entrySet()) {
            if(entry.getValue() == keyCode) {
                return entry.getKey();
            }
        }
        return null;
    } 
    
//    public int getKeyCode(String string) {
//        return KEYBOARDKEYS.get(string);
//    }
    
    private void loadKEYBOARDKEYS() {
        KEYBOARDKEYS.put("A", KeyInput.KEY_A);
        KEYBOARDKEYS.put("B", KeyInput.KEY_B);
        KEYBOARDKEYS.put("C", KeyInput.KEY_C);
        KEYBOARDKEYS.put("D", KeyInput.KEY_D);
        KEYBOARDKEYS.put("E", KeyInput.KEY_E);
        KEYBOARDKEYS.put("F", KeyInput.KEY_F);
        KEYBOARDKEYS.put("G", KeyInput.KEY_G);
        KEYBOARDKEYS.put("H", KeyInput.KEY_H);
        KEYBOARDKEYS.put("I", KeyInput.KEY_I);
        KEYBOARDKEYS.put("J", KeyInput.KEY_J);
        KEYBOARDKEYS.put("K", KeyInput.KEY_K);
        KEYBOARDKEYS.put("L", KeyInput.KEY_L);
        KEYBOARDKEYS.put("M", KeyInput.KEY_M);
        KEYBOARDKEYS.put("N", KeyInput.KEY_N);
        KEYBOARDKEYS.put("O", KeyInput.KEY_O);
        KEYBOARDKEYS.put("P", KeyInput.KEY_P);
        KEYBOARDKEYS.put("Q", KeyInput.KEY_Q);
        KEYBOARDKEYS.put("R", KeyInput.KEY_R);
        KEYBOARDKEYS.put("S", KeyInput.KEY_S);
        KEYBOARDKEYS.put("T", KeyInput.KEY_T);
        KEYBOARDKEYS.put("U", KeyInput.KEY_U);
        KEYBOARDKEYS.put("V", KeyInput.KEY_V);
        KEYBOARDKEYS.put("W", KeyInput.KEY_W);
        KEYBOARDKEYS.put("X", KeyInput.KEY_X);
        KEYBOARDKEYS.put("Y", KeyInput.KEY_Y);
        KEYBOARDKEYS.put("Z", KeyInput.KEY_Z);
        KEYBOARDKEYS.put("0", KeyInput.KEY_0);
        KEYBOARDKEYS.put("1", KeyInput.KEY_1);
        KEYBOARDKEYS.put("2", KeyInput.KEY_2);
        KEYBOARDKEYS.put("3", KeyInput.KEY_3);
        KEYBOARDKEYS.put("4", KeyInput.KEY_4);
        KEYBOARDKEYS.put("5", KeyInput.KEY_5);
        KEYBOARDKEYS.put("6", KeyInput.KEY_6);
        KEYBOARDKEYS.put("7", KeyInput.KEY_7);
        KEYBOARDKEYS.put("8", KeyInput.KEY_8);
        KEYBOARDKEYS.put("9", KeyInput.KEY_9);
        KEYBOARDKEYS.put("NUMPAD0", KeyInput.KEY_NUMPAD0);
        KEYBOARDKEYS.put("NUMPAD1", KeyInput.KEY_NUMPAD1);
        KEYBOARDKEYS.put("NUMPAD2", KeyInput.KEY_NUMPAD2);
        KEYBOARDKEYS.put("NUMPAD3", KeyInput.KEY_NUMPAD3);
        KEYBOARDKEYS.put("NUMPAD4", KeyInput.KEY_NUMPAD4);
        KEYBOARDKEYS.put("NUMPAD5", KeyInput.KEY_NUMPAD5);
        KEYBOARDKEYS.put("NUMPAD6", KeyInput.KEY_NUMPAD6);
        KEYBOARDKEYS.put("NUMPAD7", KeyInput.KEY_NUMPAD7);
        KEYBOARDKEYS.put("NUMPAD8", KeyInput.KEY_NUMPAD8);
        KEYBOARDKEYS.put("NUMPAD9", KeyInput.KEY_NUMPAD9);
        KEYBOARDKEYS.put("NUMPADCOMMA", KeyInput.KEY_NUMPADCOMMA);
        KEYBOARDKEYS.put("NUMPADENTER", KeyInput.KEY_NUMPADENTER);
        KEYBOARDKEYS.put("NUMPADEQUALS", KeyInput.KEY_NUMPADEQUALS);
        KEYBOARDKEYS.put("F1", KeyInput.KEY_F1);
        KEYBOARDKEYS.put("F2", KeyInput.KEY_F2);
        KEYBOARDKEYS.put("F3", KeyInput.KEY_F3);
        KEYBOARDKEYS.put("F4", KeyInput.KEY_F4);
        KEYBOARDKEYS.put("F5", KeyInput.KEY_F5);
        KEYBOARDKEYS.put("F6", KeyInput.KEY_F6);
        KEYBOARDKEYS.put("F7", KeyInput.KEY_F7);
        KEYBOARDKEYS.put("F8", KeyInput.KEY_F8);
        KEYBOARDKEYS.put("F9", KeyInput.KEY_F9);
        KEYBOARDKEYS.put("F10", KeyInput.KEY_F10);
        KEYBOARDKEYS.put("F11", KeyInput.KEY_F11);
        KEYBOARDKEYS.put("F12", KeyInput.KEY_F12);
        KEYBOARDKEYS.put("F13", KeyInput.KEY_F13);
        KEYBOARDKEYS.put("F14", KeyInput.KEY_F14);
        KEYBOARDKEYS.put("F15", KeyInput.KEY_F15);
        KEYBOARDKEYS.put("Space", KeyInput.KEY_SPACE);
        KEYBOARDKEYS.put("Back", KeyInput.KEY_BACK);
        KEYBOARDKEYS.put("L_Control", KeyInput.KEY_LCONTROL);
        KEYBOARDKEYS.put("R_Control", KeyInput.KEY_RCONTROL);
        KEYBOARDKEYS.put("R_Shift", KeyInput.KEY_RSHIFT);
        KEYBOARDKEYS.put("L_Shift", KeyInput.KEY_LSHIFT);
        KEYBOARDKEYS.put("Tab", KeyInput.KEY_TAB);
        KEYBOARDKEYS.put("PGUP", KeyInput.KEY_PGUP);
        KEYBOARDKEYS.put("PGDN", KeyInput.KEY_PGDN);
        KEYBOARDKEYS.put("PAUSE", KeyInput.KEY_PAUSE);
        KEYBOARDKEYS.put("COMMA", KeyInput.KEY_COMMA);
        KEYBOARDKEYS.put("DOT", KeyInput.KEY_PERIOD);
        KEYBOARDKEYS.put("MINUS", KeyInput.KEY_MINUS);
        KEYBOARDKEYS.put("ENTER", KeyInput.KEY_RETURN);
        KEYBOARDKEYS.put("L_ALT", KeyInput.KEY_LMENU);
        KEYBOARDKEYS.put("R_ALT", KeyInput.KEY_RMENU);
        KEYBOARDKEYS.put("L_META", KeyInput.KEY_LMETA);
        KEYBOARDKEYS.put("R_META", KeyInput.KEY_RMETA);
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
    
    public boolean isVsync(){
        return vsync;
    }
    
    public void setVsync(boolean enable){
        AppSettings appSettings = Main.app.getAppSettings();
        this.vsync = enable;
        appSettings.setVSync(enable);
        Main.app.setSettings(appSettings);
    }
    
    public ArrayList<Dimension> getPossibleResolutions(){
        ArrayList<Dimension> l = new ArrayList<Dimension>();
        DisplayMode [] mode = device.getDisplayModes();
        for(int i = 0; i < mode.length; i++){
            l.add(new Dimension(mode[i].getWidth(), mode[i].getHeight()));
        }
        return l;
    }
    
    public ArrayList getPossibleResolutionsStrings(){
        ArrayList<String> l = new ArrayList<>();
        DisplayMode [] mode = device.getDisplayModes();
        for(int i = 0; i < mode.length; i++){
            if(!l.contains(mode[i].getWidth()+ " x " + mode[i].getHeight())){
                l.add(mode[i].getWidth()+ " x " + mode[i].getHeight());
            }
        }
        return l;
    }
    
    public void setResolution(Dimension d){
        AppSettings appSettings = Main.app.getAppSettings();
        this.resolution = d;
        appSettings.setResolution((int) d.getWidth(), (int) d.getHeight());
        Main.app.setSettings(appSettings);
    }
    
    public void setResolution(int width, int height){
        AppSettings appSettings = Main.app.getAppSettings();
        this.resolution = new Dimension(width, height);
        appSettings.setResolution(width, height);
        Main.app.setSettings(appSettings);
    }
    
    public String getActiveResolution(){
        return this.resolution.width + " x " + this.resolution.height;
    }
    
    public ArrayList<Integer> getPossibleColorDepths(){
        ArrayList<Integer> l = new ArrayList<>();
        DisplayMode [] mode = device.getDisplayModes();
        for(int i = 0; i < mode.length; i++){
            if(!l.contains(mode[i].getBitDepth())){
                l.add(mode[i].getBitDepth());
            }
        }
        return l;
    }
    
    public ArrayList getPossibleColorDepthsStrings(){
        ArrayList<String> l = new ArrayList<>();
        DisplayMode [] mode = device.getDisplayModes();
        for(int i = 0; i < mode.length; i++){
            if(!l.contains(String.valueOf(mode[i].getBitDepth() + " bpp"))){
                l.add(mode[i].getBitDepth()+ " bpp");
            }
        }
        return l;
    }
    
    public void setColorDepth(int colorDepth){
        AppSettings appSettings = Main.app.getAppSettings();
        this.colorDepth = colorDepth;
        if(device.getDisplayMode().getBitDepth() == colorDepth){
            appSettings.setDepthBits(colorDepth);
        }
        Main.app.setSettings(appSettings);
    }
    
    public String getActiveColorDepth(){
        return Main.app.getAppSettings().getDepthBits() + " bpp";
    }
    
    public ArrayList<Integer> getPossibleAntiAliasing(){
        ArrayList<Integer> l = new ArrayList<>();
        l.add(0);
        l.add(2);
        l.add(4);
        l.add(6);
        l.add(8);
        l.add(16);
        return l;
    }
    
    public ArrayList getPossibleAntiAliasingStrings(){
        ArrayList<String> l = new ArrayList<>();
        l.add("Aus");
        l.add(2 + "x");
        l.add(4 + "x");
        l.add(6 + "x");
        l.add(8 + "x");
        l.add(16 + "x");
        return l;
    }
    
    public void setAntiAliasing(int antiAliasing){
        AppSettings appSettings = Main.app.getAppSettings();
        this.antiAliasing = antiAliasing;
        appSettings.setSamples(antiAliasing);
        Main.app.setSettings(appSettings);
    }
    
    public String getActiveAntiAliasing(){
        return Main.app.getAppSettings().getSamples() + "x";
    }
    
    public double getVolumeEffectsEffective(){
        if(volumeMasterIsMuted || volumeEffectsIsMuted){
            return 0;
        }
        return volumeMaster*volumeEffects;
    }
    
    public double getVolumeMusicEffective(){
        if(volumeMasterIsMuted || volumeMusicIsMuted){
            return 0;
        }
        return volumeMaster*volumeMusic;
    }
    
    public double getVolumeMaster(){
        return volumeMaster;
    }
    
    public double getVolumeEffects(){
        return volumeEffects;
    }
    
    public double getVolumeMusic(){
        return volumeMusic;
    }
    
    public void setVolumeMaster(double volumeMaster){
        this.volumeMaster = volumeMaster;
        Main.app.getWorld().getPlayer().reloadVolumes();
    }
    
    public void setVolumeEffects(double volumeEffects){
        this.volumeEffects = volumeEffects;
        Main.app.getWorld().getPlayer().reloadVolumes();
    }
    
    public void setVolumeMusic(double volumeMusic){
        this.volumeMusic = volumeMusic;
    }

    public void setVolumeMasterMuted(boolean muted){
        this.volumeMasterIsMuted = muted;
        Main.app.getWorld().getPlayer().reloadVolumes();
    }
    
    public void setVolumeEffectsMuted(boolean muted){
        this.volumeEffectsIsMuted = muted;
        Main.app.getWorld().getPlayer().reloadVolumes();
    }
    
    public void setVolumeMusicMuted(boolean muted){
        this.volumeMusicIsMuted = muted;
    }
    
    public boolean isVolumeMasterMuted(){
        return this.volumeMasterIsMuted;
    }
    
    public boolean isVolumeEffectsMuted(){
        return this.volumeEffectsIsMuted;
    }
    
    public boolean isVolumeMusicMuted(){
        return this.volumeMusicIsMuted;
    }
    
    public void loadSettings(){
        File settings = new File("settings.save");
        if(!settings.exists()){
            loadDefaultSettings(true);
            return;
        }
        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(settings);
            doc.getDocumentElement().normalize();
            
            Element keys = (Element) doc.getElementsByTagName("Keys").item(0);
            this.keys.put("forward", Integer.valueOf(keys.getAttribute("forward")));
            this.keys.put("backward", Integer.valueOf(keys.getAttribute("backward")));
            this.keys.put("goRight", Integer.valueOf(keys.getAttribute("goRight")));
            this.keys.put("goLeft", Integer.valueOf(keys.getAttribute("goLeft")));
            this.keys.put("jump", Integer.valueOf(keys.getAttribute("jump")));
            this.keys.put("menu_1", Integer.valueOf(keys.getAttribute("menu_1")));
            this.keys.put("menu_2", Integer.valueOf(keys.getAttribute("menu_2")));
            this.keys.put("item_1", Integer.valueOf(keys.getAttribute("item_1")));
            this.keys.put("item_2", Integer.valueOf(keys.getAttribute("item_2")));
            this.keys.put("item_3", Integer.valueOf(keys.getAttribute("item_3")));
            this.keys.put("item_4", Integer.valueOf(keys.getAttribute("item_4")));
            this.keys.put("item_5", Integer.valueOf(keys.getAttribute("item_5")));
            this.keys.put("debug", Integer.valueOf(keys.getAttribute("debug")));
            this.keys.put("help", Integer.valueOf(keys.getAttribute("help")));
            
            Element useScroll = (Element) doc.getElementsByTagName("useScroll").item(0);
            this.useScroll = Boolean.valueOf(useScroll.getAttribute("useScroll"));
            
            Element graphicSettings = (Element) doc.getElementsByTagName("GraphicSettings").item(0);
            setFullscreen(Boolean.valueOf(graphicSettings.getAttribute("Fullscreen")));
            setVsync(Boolean.valueOf(graphicSettings.getAttribute("Vsync")));
//            setColorDepth(Integer.valueOf(graphicSettings.getAttribute("ColorDepth")));
            setAntiAliasing(Integer.valueOf(graphicSettings.getAttribute("AntiAliasing")));
            
            Element resolution = (Element) doc.getElementsByTagName("Resolution").item(0);
            this.setResolution(Integer.valueOf(resolution.getAttribute("Width")), Integer.valueOf(resolution.getAttribute("Height")));
            
            Element volumeMaster = (Element) doc.getElementsByTagName("VolumeMaster").item(0);
            this.volumeMaster = Double.valueOf(volumeMaster.getAttribute("Volume"));
            this.volumeMasterIsMuted = Boolean.valueOf(volumeMaster.getAttribute("isMuted"));
            
            Element volumeEffects = (Element) doc.getElementsByTagName("VolumeEffects").item(0);
            this.volumeEffects = Double.valueOf(volumeEffects.getAttribute("Volume"));
            this.volumeEffectsIsMuted = Boolean.valueOf(volumeEffects.getAttribute("isMuted"));
            
            Element volumeMusic = (Element) doc.getElementsByTagName("VolumeMusic").item(0);
            this.volumeMusic = Double.valueOf(volumeMusic.getAttribute("Volume"));
            this.volumeMusicIsMuted = Boolean.valueOf(volumeMusic.getAttribute("isMuted"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void saveSettings(){
        File saveSettings = new File("settings.save");
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder;
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            Element rootElement = doc.createElement("Settings");
            rootElement.setAttribute("Date", String.valueOf(System.currentTimeMillis()));
            doc.appendChild(rootElement);
            
            Element controlSettings = doc.createElement("ControlSettings");
            Element keys = doc.createElement("Keys");
            keys.setAttribute("forward", String.valueOf(getKey("forward")));
            keys.setAttribute("backward", String.valueOf(getKey("backward")));
            keys.setAttribute("goLeft", String.valueOf(getKey("goLeft")));
            keys.setAttribute("goRight", String.valueOf(getKey("goRight")));
            keys.setAttribute("jump", String.valueOf(getKey("jump")));
            keys.setAttribute("menu_1", String.valueOf(getKey("menu_1")));
            keys.setAttribute("menu_2", String.valueOf(getKey("menu_2")));
            keys.setAttribute("item_1", String.valueOf(getKey("item_1")));
            keys.setAttribute("item_2", String.valueOf(getKey("item_2")));
            keys.setAttribute("item_3", String.valueOf(getKey("item_3")));
            keys.setAttribute("item_4", String.valueOf(getKey("item_4")));
            keys.setAttribute("item_5", String.valueOf(getKey("item_5")));
            keys.setAttribute("debug", String.valueOf(getKey("debug")));
            keys.setAttribute("help", String.valueOf(getKey("help")));
            controlSettings.appendChild(keys);
            
            Element useScroll = doc.createElement("useScroll");
            useScroll.setAttribute("useScroll", String.valueOf(this.useScroll));
            controlSettings.appendChild(useScroll);
            rootElement.appendChild(controlSettings);
            
            Element graphicSettings = doc.createElement("GraphicSettings");
            graphicSettings.setAttribute("Fullscreen", String.valueOf(this.fullscreen));
            graphicSettings.setAttribute("Vsync", String.valueOf(this.vsync));
            graphicSettings.setAttribute("ColorDepth", String.valueOf(this.colorDepth));
            graphicSettings.setAttribute("AntiAliasing", String.valueOf(this.antiAliasing));
            
            Element resolution = doc.createElement("Resolution");
            resolution.setAttribute("Width", String.valueOf(this.resolution.width));
            resolution.setAttribute("Height", String.valueOf(this.resolution.height));
            graphicSettings.appendChild(resolution);
            rootElement.appendChild(graphicSettings);
            
            Element volumeSettings = doc.createElement("VolumeSettings");
            Element volumeMaster = doc.createElement("VolumeMaster");
            volumeMaster.setAttribute("Volume", String.valueOf(this.volumeMaster));
            volumeMaster.setAttribute("isMuted", String.valueOf(this.volumeMasterIsMuted));
            volumeSettings.appendChild(volumeMaster);
            
            Element volumeEffects = doc.createElement("VolumeEffects");
            volumeEffects.setAttribute("Volume", String.valueOf(this.volumeEffects));
            volumeEffects.setAttribute("isMuted", String.valueOf(this.volumeEffectsIsMuted));
            volumeSettings.appendChild(volumeEffects);
            
            Element volumeMusic = doc.createElement("VolumeMusic");
            volumeMusic.setAttribute("Volume", String.valueOf(this.volumeMusic));
            volumeMusic.setAttribute("isMuted", String.valueOf(this.volumeMusicIsMuted));
            volumeSettings.appendChild(volumeMusic);
            rootElement.appendChild(volumeSettings);
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(saveSettings);
            transformer.transform(source, result);
        } catch (Exception ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public HashMap getDefaultKeys(){
        HashMap<String, Integer> keys = new HashMap<>();
        keys.put("forward", KeyInput.KEY_W);
        keys.put("backward", KeyInput.KEY_S);
        keys.put("goRight", KeyInput.KEY_D);
        keys.put("goLeft", KeyInput.KEY_A);
        keys.put("jump", KeyInput.KEY_SPACE);
        keys.put("menu_1", KeyInput.KEY_ESCAPE);
        keys.put("menu_2", KeyInput.KEY_PAUSE);
        keys.put("item_1", KeyInput.KEY_1);
        keys.put("item_2", KeyInput.KEY_2);
        keys.put("item_3", KeyInput.KEY_3);
        keys.put("item_4", KeyInput.KEY_4);
        keys.put("item_5", KeyInput.KEY_5);
        keys.put("debug", KeyInput.KEY_F4);
        keys.put("help", KeyInput.KEY_F1);
        return keys;
    }
    
    public void loadDefaultSettings(boolean resetKeys){
        setResolution(new Dimension(device.getDisplayMode().getWidth(), device.getDisplayMode().getHeight()));
        setFullscreen(device.isFullScreenSupported());
        setVsync(false);
//        setColorDepth(device.getDisplayMode().getBitDepth());
        setAntiAliasing(0);
        
        volumeMaster = 1;
        volumeMusic = 1;
        volumeEffects = 1;
        volumeMasterIsMuted = false;
        volumeEffectsIsMuted = false;
        volumeMusicIsMuted = false;
        
        if(resetKeys){
            keys = getDefaultKeys();
        }
    }
    
    public Properties getLanguageProperties(){
        return this.languageProperties;
    }
    
    public void setLanguage(String languagesShort){
        this.languageProperties = (Properties) Main.app.getAssetManager().loadAsset("Languages/" + languagesShort + ".properties");
    }
    
    public String getLanguageProperty(String key, String defaultValue){
        return this.languageProperties.getProperty(key, defaultValue);
    }
    
    public String getLanguageProperty(String key){
        return this.languageProperties.getProperty(key);
    }
    
    public void reloadLanguage(){
        Nifty nifty = Main.app.getNifty();
        Screen screen = nifty.getScreen("hud");
        setNiftyText(screen, "tutorial", getLanguageProperty("tutorial"));
        setNiftyText(screen, "aim1", getLanguageProperty("aim1"));
        setNiftyText(screen, "aim2", getLanguageProperty("aim2"));
        setNiftyText(screen, "bombs", getLanguageProperty("bombs"));
        setNiftyText(screen, "textShootingBombs", getLanguageProperty("textShootingBombs"));
        setNiftyText(screen, "textNormalBombs", getLanguageProperty("textNormalBombs"));
        setNiftyText(screen, "towers", getLanguageProperty("towers"));
        setNiftyText(screen, "simpleTower", getLanguageProperty("simpleTower"));
        setNiftyText(screen, "sloweringTower", getLanguageProperty("sloweringTower"));
        setNiftyText(screen, "deactivationTower", getLanguageProperty("deactivationTower"));
        setNiftyText(screen, "upgradeAndHeal", getLanguageProperty("upgradeAndHeal"));
        setNiftyText(screen, "placeTower", getLanguageProperty("placeTower"));
        setNiftyText(screen, "changeSelection1", getLanguageProperty("changeSelection1"));
        setNiftyText(screen, "changeSelection2", getLanguageProperty("changeSelection2"));
        setNiftyText(screen, "shoot", getLanguageProperty("shoot"));
        setNiftyText(screen, "pause", getLanguageProperty("pause"));
        setNiftyText(screen, "textHelp", getLanguageProperty("textHelp"));
        
        screen = nifty.getScreen("start");
        setNiftyText(screen, "title", getLanguageProperty("title"));
        setNiftyButtonText(screen, "StartButton", getLanguageProperty("StartButton"));
        setNiftyButtonText(screen, "loadgame", getLanguageProperty("loadgame"));
        setNiftyButtonText(screen, "SettingsButton", getLanguageProperty("SettingsButton"));
        setNiftyButtonText(screen, "HighscoresButton", getLanguageProperty("HighscoresButton"));
        setNiftyButtonText(screen, "CreditsButton", getLanguageProperty("CreditsButton"));
        setNiftyButtonText(screen, "QuitButton", getLanguageProperty("QuitButton"));
        
        screen = nifty.getScreen("pause");
        setNiftyButtonText(screen, "StartButtonPause", getLanguageProperty("StartButtonPause"));
        setNiftyButtonText(screen, "MenuButton", getLanguageProperty("MenuButton"));
        setNiftyButtonText(screen, "QuitButtonPause", getLanguageProperty("QuitButtonPause"));
        
        screen = nifty.getScreen("gameOver");
        setNiftyText(screen, "textGameOver", getLanguageProperty("textGameOver"));
        setNiftyText(screen, "beaconDestroyed", getLanguageProperty("beaconDestroyed"));
        setNiftyButtonText(screen, "buttonGameOver", getLanguageProperty("buttonGameOver"));
        setNiftyButtonText(screen, "#buttonGameOver", getLanguageProperty("buttonGameOverQuit"));
        
        screen = nifty.getScreen("settings");
        setNiftyText(screen, "textScrollMouse", "   " + getLanguageProperty("textScrollMouse"));
        setNiftyText(screen, "textVolumes", getLanguageProperty("textVolumes"));
        setNiftyText(screen, "textMasterVolume", getLanguageProperty("textMasterVolume") + "   ");
        setNiftyText(screen, "textMuteMasterVolume", "   " + getLanguageProperty("textMuteMasterVolume"));
        setNiftyText(screen, "textEffectsVolume", getLanguageProperty("textEffectsVolume") + "   ");
        setNiftyText(screen, "textMuteEffectsVolume", "   " + getLanguageProperty("textMuteEffectsVolume"));
        setNiftyText(screen, "textMusicVolume", getLanguageProperty("textMusicVolume") + "   ");
        setNiftyText(screen, "textMuteMusicVolume", "   " + getLanguageProperty("textMuteMusicVolume"));
        setNiftyText(screen, "textFullscreen", "   " + getLanguageProperty("textFullscreen"));
        setNiftyText(screen, "textResolution", getLanguageProperty("textResolution") + "   ");
        setNiftyText(screen, "textVSync", "   " + getLanguageProperty("textVSync"));
        setNiftyText(screen, "textColorDepth", getLanguageProperty("textColorDepth") + "   ");
        setNiftyText(screen, "textAntiAliasing", getLanguageProperty("textAntiAliasing") + "   ");
        setNiftyButtonText(screen, "buttonToKeys", getLanguageProperty("buttonToKeys"));
        setNiftyButtonText(screen, "saveSettings", getLanguageProperty("saveSettings"));
        setNiftyButtonText(screen, "restoreSettings", getLanguageProperty("restoreSettings"));
        
        screen = nifty.getScreen("keyBindings");
        setNiftyText(screen, "textForward", getLanguageProperty("textForward"));
        setNiftyText(screen, "textBackward", getLanguageProperty("textBackward"));
        setNiftyText(screen, "textLeft", getLanguageProperty("textLeft"));
        setNiftyText(screen, "textRight", getLanguageProperty("textRight"));
        setNiftyText(screen, "textJump", getLanguageProperty("textJump"));
        setNiftyText(screen, "textItem_1", getLanguageProperty("textItem_1"));
        setNiftyText(screen, "textItem_2", getLanguageProperty("textItem_2"));
        setNiftyText(screen, "textItem_3", getLanguageProperty("textItem_3"));
        setNiftyText(screen, "textItem_4", getLanguageProperty("textItem_4"));
        setNiftyText(screen, "textItem_5", getLanguageProperty("textItem_5"));
        setNiftyText(screen, "textHelp", getLanguageProperty("textHelp"));
        setNiftyButtonText(screen, "saveKeyBindings", getLanguageProperty("saveKeyBindings"));
        setNiftyButtonText(screen, "resetKeyBindings", getLanguageProperty("resetKeyBindings"));
        
        screen = nifty.getScreen("highscores");
        setNiftyText(screen, "textPlace", getLanguageProperty("textPlace"));
        setNiftyText(screen, "textName", getLanguageProperty("textName"));
        setNiftyText(screen, "textWave", getLanguageProperty("textWave"));
        setNiftyText(screen, "textWorld", getLanguageProperty("textWorld"));
        setNiftyText(screen, "textDate", getLanguageProperty("textDate"));
        setNiftyButtonText(screen, "backToStartHighscores", getLanguageProperty("backToStartHighscores"));
        setNiftyButtonText(screen, "clearHighscores", getLanguageProperty("clearHighscores"));
        
        screen = nifty.getScreen("credits");
        Main.app.getStateManager().getState(MyStartScreen.class).loadCredits();
        setNiftyButtonText(screen, "backToStartCredits", getLanguageProperty("backToStartCredits"));
        
        screen = nifty.getScreen("chooseScene");
        setNiftyButtonText(screen, "#startGame", getLanguageProperty("startGame"));
        setNiftyButtonText(screen, "backToStartChooseScene", getLanguageProperty("backToStartChooseScene"));
        
        screen = nifty.getScreen("chooseSave");
        setNiftyButtonText(screen, "#loadGame", getLanguageProperty("loadGame"));
        setNiftyButtonText(screen, "backToStartChooseSave", getLanguageProperty("backToStartChooseSave"));
        setNiftyButtonText(screen, "#deleteSave", getLanguageProperty("deleteSave"));
    }
    
    public void reloadTowerPopupLanguage(de.lessvoid.nifty.elements.Element towerPopup){
        setNiftyText(towerPopup, "textPrice", getLanguageProperty("textPrice"));
        setNiftyText(towerPopup, "textDamage", getLanguageProperty("textDamage"));
        setNiftyText(towerPopup, "textHealth", getLanguageProperty("textHealth"));
        setNiftyText(towerPopup, "textSPS", getLanguageProperty("textSPS"));
        setNiftyText(towerPopup, "textRange", getLanguageProperty("textRange"));
        setNiftyText(towerPopup, "textShootAt", getLanguageProperty("textShootAt"));
        setNiftyText(towerPopup, "textNearest", getLanguageProperty("textNearest") + "   ");
        setNiftyText(towerPopup, "textFurthest", getLanguageProperty("textFurthest") + "   ");
        setNiftyText(towerPopup, "textStrongest", getLanguageProperty("textStrongest") + "   ");
        setNiftyText(towerPopup, "textWeakest", getLanguageProperty("textWeakest") + "   ");
        setNiftyText(towerPopup, "textAllBombs", getLanguageProperty("textAllBombs") + "   ");
        setNiftyText(towerPopup, "textNormalBombs", getLanguageProperty("textNormalBombs") + "   ");
        setNiftyText(towerPopup, "textShootingBombs", getLanguageProperty("textShootingBombs") + "   ");
        setNiftyButtonText(towerPopup, "upgrade", getLanguageProperty("upgrade"));
        setNiftyButtonText(towerPopup, "upgradeToMax", getLanguageProperty("upgradeToMax"));
        setNiftyButtonText(towerPopup, "removeTower", getLanguageProperty("removeTower"));
        setNiftyButtonText(towerPopup, "cancel", getLanguageProperty("cancel"));
    }
    
    public void reloadRemoveTowerPopupLanguage(de.lessvoid.nifty.elements.Element removePopup){
        setNiftyText(removePopup, "#title", getLanguageProperty("title"));
        setNiftyButtonText(removePopup, "yes", getLanguageProperty("yes"));
        setNiftyButtonText(removePopup, "no", getLanguageProperty("no"));
    }
    
    public void reloadEndWavePopupLanguage(de.lessvoid.nifty.elements.Element endWavePopup){
        setNiftyText(endWavePopup, "textEndWaveHealth", getLanguageProperty("textEndWaveHealth"));
        setNiftyText(endWavePopup, "textEndWaveDamage", getLanguageProperty("textEndWaveDamage"));
        setNiftyText(endWavePopup, "textEndWaveSPS", getLanguageProperty("textEndWaveSPS"));
        setNiftyText(endWavePopup, "textEndWaveRange", getLanguageProperty("textEndWaveRange"));
        setNiftyText(endWavePopup, "textEndWaveSpeed", getLanguageProperty("textEndWaveSpeed"));
        setNiftyText(endWavePopup, "textEndWaveHealingSpeed", getLanguageProperty("textEndWaveHealingSpeed"));
        setNiftyText(endWavePopup, "textBeaconUpgrade", getLanguageProperty("textBeaconUpgrade"));
        setNiftyText(endWavePopup, "textBeaconHealth", getLanguageProperty("textBeaconHealth"));
        setNiftyButtonText(endWavePopup, "#nextWave", getLanguageProperty("nextWave"));
    }
    
    private void setNiftyButtonText(de.lessvoid.nifty.elements.Element popup, String elementID, String text){
        popup.findNiftyControl(elementID, Button.class).setText(text);
    }
    
    private void setNiftyButtonText(Screen screen, String elementID, String text){
        screen.findNiftyControl(elementID, Button.class).setText(text);
    }
    
    private void setNiftyText(de.lessvoid.nifty.elements.Element popup, String elementID, String text){
        popup.findElementByName(elementID).getRenderer(TextRenderer.class).setText(text);
    }
    
    private void setNiftyText(Screen screen, String elementID, String text){
        screen.findElementByName(elementID).getRenderer(TextRenderer.class).setText(text);
    }
}
