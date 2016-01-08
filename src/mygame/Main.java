package mygame;

import mygame.Entitys.Beacon;
import mygame.Entitys.Player;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
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
    public static Main app;
    private String str;
    private static BulletAppState bulletAppState;
    private RigidBodyControl sceneC;
    private Node n;     //Aufhebare Objekte;
    private Nifty nifty;
    private HudScreenState hudState;
    private static World world;
    private static Game game;
    private long waveEnded = 0;
    private boolean debugMode = true;
    
    /**
     * Startet das Spiel bzw. die Simple-Application und legt gewisse Einstellungen fest.
     * @param args 
     */
    public static void main(String[] args) {
        app = new Main();
        
        //AppSettings initialisieren
        AppSettings appSettings = new AppSettings(true);
        
        //Titel setzen
        appSettings.setTitle("First-Person-View TowerDefense Game");
        //Start into Fullscreen
        //Get the Resolution of the main/defautl display
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        appSettings.setResolution(device.getDisplayMode().getWidth(), device.getDisplayMode().getHeight());
        appSettings.setResolution(2560, 1440);
        appSettings.setFrequency(device.getDisplayMode().getRefreshRate());
        appSettings.setBitsPerPixel(device.getDisplayMode().getBitDepth());
        //set the found resolution of the monitor as the resolution of the game.
        appSettings.setFullscreen(device.isFullScreenSupported());
        // Frame rate limitieren
        appSettings.setFrameRate(60);
        //AppSettings hinzufügen
        app.setSettings(appSettings);

        app.start();
    }

    /**
     * Initialisiert das Spiel. Es Werden alle nötigen Objekte initialisiert: Die Welt ({@link World}) mit dem Spieler und Beacon, das Spiel ({@link Game}) und die 
     * GUI mit den xml-Dateien und den zwei Controllers und Appstates ({@link HudScreenState}, {@link MyStartScreen}). Auch wird die Szenen gesetzt und die Kamera
     * initialisiert.
     */
    @Override
    public void simpleInitApp() {
        //Set this boolean true when the game loop should stop running when ever the window loses focus.
        app.setPauseOnLostFocus(true);
        
        Spatial scene = assetManager.loadModel("Scenes/scene_1.j3o");
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
        MyStartScreen startState = (MyStartScreen) nifty.getScreen("start").getScreenController();
        nifty.registerScreenController(startState);
        nifty.gotoScreen("hud");
        stateManager.attach(startState);
        
        hudState = (HudScreenState) nifty.getScreen("hud").getScreenController();
        //hudState.setPlayer(player);
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
    private void setUpKeys() {
        //Allgemeine Tasten
        inputManager.addMapping("Menu", new KeyTrigger(KeyInput.KEY_ESCAPE), new KeyTrigger(KeyInput.KEY_PAUSE));
        inputManager.addListener(this, "Menu");
        //Tasten für SchnelleisteSlots
        inputManager.addMapping("item_1", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addListener(this, "item_1");
        inputManager.addMapping("item_2", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addListener(this, "item_2");
        inputManager.addMapping("item_3", new KeyTrigger(KeyInput.KEY_3));
        inputManager.addListener(this, "item_3");
        inputManager.addMapping("item_4", new KeyTrigger(KeyInput.KEY_4));
        inputManager.addListener(this, "item_4");
        inputManager.addMapping("item_5", new KeyTrigger(KeyInput.KEY_5));
        inputManager.addListener(this, "item_5");
        inputManager.addMapping("debug", new KeyTrigger(KeyInput.KEY_F4));
        inputManager.addListener(this, "debug");
        //Mausrad
//        inputManager.addMapping("item_scroll_up", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
//        inputManager.addListener(this, "item_scroll_up");
//        inputManager.addMapping("item_scroll_down", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
//        inputManager.addListener(this, "item_scroll_down");
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
     * @return 
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
}