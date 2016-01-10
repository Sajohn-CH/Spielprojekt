package mygame;

import mygame.Entitys.Beacon;
import mygame.Entitys.Player;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import de.lessvoid.nifty.Nifty;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

/**
 * Die Hauptklasse des Spiels. Sie initialisert alle Komponenten.
 * @author Samuel Martin und Florian Wenk
 */

public class Main extends SimpleApplication implements ActionListener{
    public static Main app;             //Die Application selbst. 
    private static BulletAppState bulletAppState;   
    private Nifty nifty;                //Wird benötigt um auf die graphische Oberfläche Bildschirme etc. anzeigen zu lassen.
    private HudScreenState hudState;    //Der ScreenController, der kontrolliert, was der HUD macht (Der Bildschirm, der während des Spielens angezeigt wird.)
    private MyStartScreen startState;   //Der ScreenController, der kontrolliert, was die Start-, Pause- und Einstellungsbildschirme machen.  
    private static World world;         //Die Spielwelt
    private static Game game;           //Das "Spiel". Kontrolliert die Wellengenerierung
    private boolean debugMode = true;   //Gibt an ob der Debugmodus aktiviert ist.
    private static AppSettings appSettings;     //Die Einstellungen der Applications (kommt von der JMonkeyApplication). Sie ist für Auflösung etc. zuständig
    private Settings settings;          //Die selber erstellten Einstellungen. Sie ist für die Tastenbelegung etc. zuständig.
//    private boolean scrollToChangeSelection;    
    private Spatial scene;              //Die Spielszene
    
//    private int key_item_1 = KeyInput.KEY_1;
//    private int key_item_2 = KeyInput.KEY_2;
//    private int key_item_3 = KeyInput.KEY_3;
//    private int key_item_4 = KeyInput.KEY_4;
//    private int key_item_5 = KeyInput.KEY_5;
//    private int key_debug = KeyInput.KEY_F4;
    
    /**
     * Startet das Spiel bzw. die Simple-Application und legt gewisse Einstellungen fest.
     * @param args 
     */
    public static void main(String[] args) {
        app = new Main();
        
        //AppSettings initialisieren
        appSettings = new AppSettings(true);
        
        //Titel setzen
        appSettings.setTitle("First-Person-View TowerDefense Game");
        //Get the Resolution of the main/default display
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        //set the found resolution of the monitor as the resolution of the game.
        appSettings.setResolution(device.getDisplayMode().getWidth(), device.getDisplayMode().getHeight());
        appSettings.setFrequency(device.getDisplayMode().getRefreshRate());
        appSettings.setBitsPerPixel(device.getDisplayMode().getBitDepth());
        // Frame rate limitieren
        appSettings.setFrameRate(60);
        //AppSettings hinzufügen
        app.setSettings(appSettings);
//        app.setShowSettings(false);
        //Start into Fullscreen
        appSettings.setFullscreen(device.isFullScreenSupported());
        
        app.start();
    }

    /**
     * Initialisiert das Spiel. Es Werden alle nötigen Objekte initialisiert: Die Welt ({@link World}) mit dem Spieler und Beacon, das Spiel ({@link Game}) und die 
     * GUI mit den xml-Dateien und den zwei Controllers und Appstates ({@link HudScreenState}, {@link MyStartScreen}). Auch wird die Szenen gesetzt und die Kamera
     * initialisiert.
     */
    @Override
    public void simpleInitApp() {
        settings = new Settings();
        
        //Set this boolean true when the game loop should stop running when ever the window loses focus.
        app.setPauseOnLostFocus(true);
        
        scene = assetManager.loadModel("Scenes/scene_1.j3o");
        scene.setLocalScale(2f);
        
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        
        Player player = new Player(this);
        
        Beacon beacon = new Beacon(new Vector3f(0, 0, 0), 100);
        world = new World(beacon, player, scene);
        stateManager.attach(world);
        world.setPaused(true);
        
        beacon.turn();
        player.turn();
        
        rootNode.attachChild(world.getBombNode());
        rootNode.attachChild(world.getTowerNode());
        rootNode.attachChild(world.getBeacon().getSpatial());
        
        setUpKeys();
        
        game = new Game(1);
        game.startWave();
        
        //Test, ob es dann beim ersten Setzen eines Turms nicht lange braucht
        app.getAssetManager().loadModel("Objects/SimpleTower.j3o");
        app.getAssetManager().loadModel("Objects/MGTower.j3o");
        app.getAssetManager().loadModel("Objects/PyramidTower.j3o");
        
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay( assetManager, inputManager, audioRenderer, guiViewPort);
        //Create a new NiftyGui objects
        nifty = niftyDisplay.getNifty();
        //Lädt die benötigten XML-Dateien (diese werden dabei überprüft)
        addXmlFile("Interface/hud.xml");
        addXmlFile("Interface/screen.xml"); 
        startState = (MyStartScreen) nifty.getScreen("start").getScreenController();
        nifty.registerScreenController(startState);
        nifty.gotoScreen("hud");
        stateManager.attach(startState);
        
        hudState = (HudScreenState) nifty.getScreen("hud").getScreenController();
        nifty.registerScreenController(hudState);
        stateManager.attach(hudState);
        
        nifty.gotoScreen("start");
        //nifty.fromXml("Interface/screen.xml", "start", myStartScreen);
        //attach the Niftry display to the gui view port as a processor
        guiViewPort.addProcessor(niftyDisplay);
        //disable the fly cam
        flyCam.setDragToRotate(true);
        
        //Entfernt, dass escape das Spiel beendet
        inputManager.deleteMapping(SimpleApplication.INPUT_MAPPING_EXIT);
        //Setzt Scrollgeschwindigkeit auf null, so dass man mit dem Mausrad nicht scrollen kann.
        flyCam.setZoomSpeed(0);
        //Setzt standardbewegung der Kamera ausser Kraft, damit man im pausemenu nicht laufen kann.
        flyCam.setMoveSpeed(0);
        
        changeDebugMode();
    }
    
    /**
     * Fügt Tastenbelegungen hinzu.
     */
    //Change in diagramm private -> public
    public void setUpKeys() {
        String[] key_items = settings.getKeys_items();
        //Allgemeine Tasten
        inputManager.addMapping("Menu", new KeyTrigger(KeyInput.KEY_ESCAPE), new KeyTrigger(KeyInput.KEY_PAUSE));
        inputManager.addListener(this, "Menu");
        //Tasten für SchnelleisteSlots
        inputManager.deleteMapping("item_1");
        inputManager.addMapping("item_1", new KeyTrigger(settings.getKeyCode(key_items[0])));
        inputManager.addListener(this, "item_1");
        inputManager.deleteMapping("item_2");
        inputManager.addMapping("item_2", new KeyTrigger(settings.getKeyCode(key_items[1])));
        inputManager.addListener(this, "item_2");
        inputManager.deleteMapping("item_3");
        inputManager.addMapping("item_3", new KeyTrigger(settings.getKeyCode(key_items[2])));
        inputManager.addListener(this, "item_3");
        inputManager.deleteMapping("item_4");
        inputManager.addMapping("item_4", new KeyTrigger(settings.getKeyCode(key_items[3])));
        inputManager.addListener(this, "item_4");
        inputManager.deleteMapping("item_5");
        inputManager.addMapping("item_5", new KeyTrigger(settings.getKeyCode(key_items[4])));
        inputManager.addListener(this, "item_5");
        inputManager.deleteMapping("debug");
        inputManager.addMapping("debug", new KeyTrigger(settings.getKeyCode(settings.getKey_debug())));
        inputManager.addListener(this, "debug");
        //Mausrad
          if(settings.isUseScroll()) {
            inputManager.addMapping("item_scroll_up", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
            inputManager.addListener(this, "item_scroll_up");
            inputManager.addMapping("item_scroll_down", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
            inputManager.addListener(this, "item_scroll_down");
        }
    }
    
    /**
     * {@inheritDoc}
     * Definiert was bei welcher Tastenbelegung gemacht werden soll.
     */
    @Override
    public void onAction(String binding, boolean isPressed, float tpf) {
        if(!getWorld().isPaused()){
            world.getPlayer().onAction(binding, isPressed);
            if (binding.equals("Menu")) {
                world.getPlayer().stopAudio();
                world.getPlayer().setNotWalking();
                nifty.gotoScreen("pause");
                flyCam.setDragToRotate(true);
                flyCam.setRotationSpeed(0);
                world.setPaused(true);
            } else if(binding.equals("item_1")) {
                hudState.selectItem(1);
            } else if(binding.equals("item_2")) {
                hudState.selectItem(2);
            } else if(binding.equals("item_3")) {
                hudState.selectItem(3);
            } else if(binding.equals("item_4")) {
                hudState.selectItem(4);
            } else if(binding.equals("item_5")) {
                hudState.selectItem(5);
            } else if(binding.equals("item_scroll_up")) {
                hudState.nextSelectedItem();
            } else if(binding.equals("item_scroll_down")) {
                hudState.lastSelectedItem();
            } else if(binding.equals("debug") && isPressed) {
                changeDebugMode();
            }
        }    
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void simpleUpdate(float tpf) {
        //Wenn Kamera DragToRotate ist, dann wird ein Menu angezeigt (Menu für Wellenende muss nicht angezeigt werden)
        if(!game.bombLeft() && world.getAllBombs().isEmpty() && !hudState.isCameraDragToRotate() && !hudState.isBuildPhase()){
            game.nextWave();
            world.getPlayer().stopAudio();
            world.getPlayer().setNotWalking();
        } else if (game.bombLeft()){
            game.action(tpf);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    /**
     * Gibt die Welt ({@link World}) zurück.
     * @return  Welt
     */
    public static World getWorld() {
        return world;
    }
    
    /**
     * Gibt den HudScreenState zurück, der den HUD kontrolliert.
     * @return HudScreenState
     */
    public HudScreenState getHudState() {
        return hudState;
    }
    
    /**
     * Gibt BulletAppState zurück, der für Kollision zuständig ist.
     * @return BulletAppState
     */
    public static BulletAppState getBulletAppState(){
        return bulletAppState;
    }
    
    /**
     * Gibt das Game ({@link Game}) zurück. Dies steuert die Spielmechanik
     * @return Game
     */
    public static Game getGame(){
        return game;
    }
    
    /**
     * Ändert die Debugmodus. Schaltet ihn aus, wenn er an war und umgekehrt.
     */
    private void changeDebugMode() {
        debugMode = !debugMode;
        hudState.setDebugModeEnabled(debugMode);
    }
    
    /**
     * Wird aufgerufen, wenn das Spiel vorbei ist. Ruft den entsprechenden Bildschirm auf.
     */
    public void gameOver() {
        getWorld().setPaused(true);
        getFlyByCamera().setDragToRotate(true);
        nifty.gotoScreen("gameOver");
    }
   
    /**
     * Fügt eine XML-Datei zu nifty hinzu und überprüft die übergebene xml-Datei auf Fehler. Dabei werden die Methoden nifty.validateXml() 
     * und nifty.addXml() gebraucht. Es wird der Pfad zur Datei übergeben. So wie er auch bei den Methoden nifty.validateXml(), 
     * nifty.addXml() etc. gebraucht wird.
     * @param file Pfad zur Datei.
     */
    private void addXmlFile(String file) {
         try {
            nifty.validateXml(file);
        } catch (Exception ex) {
            System.out.println("[FEHLER] Datei "+file+" enthält Fehler");
            ex.printStackTrace();
        }
         nifty.addXml(file);
    }
    
    /**
     * Ändert die Einstellungen zu den übergebenen Werten.
     * @param scrollToChangeSelection Ob man durch die Item-Leiste scrollen kann
     * @param resolutionWidth Auflösungsbreite
     * @param resolutionHeight Auflösungshöhe
     * @param frameRate Framerate
     * @param fullscreen Ob fullscreen oder nicht
     * @param key_item_1 Taste um 1. Element der Leiste auszuwählen
     * @param key_item_2 Taste um 2. Element der Leiste auszuwählen
     * @param key_item_3 Taste um 3. Element der Leiste auszuwählen
     * @param key_item_4 Taste um 4. Element der Leiste auszuwählen
     * @param key_item_5 Taste um 5. Element der Leiste auszuwählen
     * @param key_debug Taste um  debug-Modus zu aktivieren
     * @param key_left Taste um nach Links zu gehen
     * @param key_right Taste um nach rechts zu gehen
     * @param key_up Taste um geradeaus zu gehen
     * @param key_down Taste um zurück zu gehen
     * @param key_jump  Taste um zu springen
     */
//    public void changeSettings(boolean scrollToChangeSelection, int resolutionWidth, int resolutionHeight, int frameRate, boolean fullscreen, 
//            String key_item_1, 
//            String key_item_2, 
//            String key_item_3, 
//            String key_item_4, 
//            String key_item_5, 
//            String key_debug, 
//            String key_left, 
//            String key_right, 
//            String key_up, 
//            String key_down, 
//            String key_jump){
//        this.scrollToChangeSelection = scrollToChangeSelection;
//        
//        appSettings.setResolution(resolutionWidth, resolutionHeight);
//        appSettings.setFrameRate(frameRate);
//        appSettings.setFullscreen(fullscreen);
//        app.setSettings(appSettings);
//        app.restart();
//        
//        if(key_item_1 != null)
//            this.key_item_1 = getKeyCode(key_item_1);
//        if(key_item_2 != null)
//            this.key_item_2 = getKeyCode(key_item_2);
//        if(key_item_3 != null)
//            this.key_item_3 = getKeyCode(key_item_3);
//        if(key_item_4 != null)
//            this.key_item_4 = getKeyCode(key_item_4);
//        if(key_item_5 != null)
//            this.key_item_5 = getKeyCode(key_item_5);
//        if(key_debug != null)
//            this.key_debug = getKeyCode(key_debug);
//                
//        setUpKeys();
//        
//        world.getPlayer().replaceKeys(getKeyCode(key_left), getKeyCode(key_right), getKeyCode(key_up), getKeyCode(key_down), getKeyCode(key_jump));
//    }
    
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
    
    public Settings getSettings() {
        return settings;
    }
}