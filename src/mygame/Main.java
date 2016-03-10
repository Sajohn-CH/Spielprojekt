package mygame;

import mygame.Entitys.Beacon;
import mygame.Entitys.Player;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import de.lessvoid.nifty.Nifty;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Die Hauptklasse des Spiels. Sie initialisert alle Komponenten.
 * @author Samuel Martin und Florian Wenk
 */

public class Main extends SimpleApplication implements ActionListener{
    public static Main app;             //Die Application selbst. 
    private static BulletAppState bulletAppState;   //Wird benötigt, um die Objekte kollidierbar zu machen.
    private Nifty nifty;                //Wird benötigt um auf die graphische Oberfläche Bildschirme etc. anzeigen zu lassen.
    private HudScreenState hudState;    //Der ScreenController, der kontrolliert, was der HUD macht (Der Bildschirm, der während des Spielens angezeigt wird.)
    private MyStartScreen startState;   //Der ScreenController, der kontrolliert, was die Start-, Pause- und Einstellungsbildschirme machen.  
    private static World world;         //Die Spielwelt
    private static Game game;           //Das "Spiel". Kontrolliert die Wellengenerierung
    private boolean debugMode;          //Gibt an ob der Debugmodus aktiviert ist.
    private static AppSettings appSettings;     //Die Einstellungen der Applications (kommt von der JMonkeyApplication). Sie ist für Auflösung etc. zuständig
    private Settings settings;          //Die selber erstellten Einstellungen. Sie ist für die Tastenbelegung etc. zuständig.   
    private Highscores highscores;      //Die Highscores, die Angezeigt werden
    private ArrayList<String> sceneNames;
    private ArrayList<String> saveNames;
    
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
        app.setDisplayStatView(false);
        app.setDisplayFps(false);
        app.setPauseOnLostFocus(true);
        app.setShowSettings(false);
        //Start into Fullscreen
        appSettings.setFullscreen(device.isFullScreenSupported());
                
        app.destroyInput();
        Settings settings = new Settings();
        app.start();
    }

    /**
     * Initialisiert das Spiel. Es Werden alle nötigen Objekte initialisiert: Die Welt ({@link World}) mit dem Spieler und Beacon, das Spiel ({@link Game}) und die 
     * GUI mit den xml-Dateien und den zwei Controllers und Appstates ({@link HudScreenState}, {@link MyStartScreen}). Auch wird die Szenen gesetzt und die Kamera
     * initialisiert.
     */
    @Override
    public void simpleInitApp() {
        assetManager.registerLoader(CornersLoader.class, "corners");
        assetManager.registerLoader(SceneDataLoader.class, "sceneData");
        assetManager.registerLoader(TextLoader.class, "credits", "txt");
        assetManager.registerLoader(PropertiesLoader.class, "properties");
        //Debugmode aktivieren, da das entsprechende Layer anfangs sichtbar ist. Wird später noch deaktiviert.
        debugMode = true;
        settings = new Settings();
        settings.setLanguage("de");
        highscores = new Highscores();
                
        //Set this boolean true when the game loop should stop running when ever the window loses focus.
        app.setPauseOnLostFocus(true);
        
        //Lädt die Namen der möglichen scenes
        sceneNames = (ArrayList<String>) assetManager.loadAsset("Scenes/sceneNames.txt");
                
        updateSaveNames();
        
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        
        Player player = new Player(this);
                
        Beacon beacon = new Beacon(new Vector3f(0, 0, 0), 100);
        world = new World(beacon, player, sceneNames.get(0));
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
        
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
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
        //attach the Nifty display to the gui view port as a processor
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
        inputManager.addMapping("Menu", new KeyTrigger(settings.getKey("menu_1")), new KeyTrigger(settings.getKey("menu_2")));
        inputManager.addListener(this, "Menu");
        //Tasten für SchnelleisteSlots
        inputManager.addMapping("item_1", new KeyTrigger(settings.getKey("item_1")));
        inputManager.addListener(this, "item_1");
        inputManager.addMapping("item_2", new KeyTrigger(settings.getKey("item_2")));
        inputManager.addListener(this, "item_2");
        inputManager.addMapping("item_3", new KeyTrigger(settings.getKey("item_3")));
        inputManager.addListener(this, "item_3");
        inputManager.addMapping("item_4", new KeyTrigger(settings.getKey("item_4")));
        inputManager.addListener(this, "item_4");
        inputManager.addMapping("item_5", new KeyTrigger(settings.getKey("item_5")));
        inputManager.addListener(this, "item_5");
        
        inputManager.addMapping("debug", new KeyTrigger(settings.getKey("debug")));
        inputManager.addListener(this, "debug");
        inputManager.addMapping("help", new KeyTrigger(settings.getKey("help")));
        inputManager.addListener(this, "help");
        //Mausrad
        if(settings.isUseScroll()) {
            inputManager.addMapping("item_scroll_up", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
            inputManager.addListener(this, "item_scroll_up");
            inputManager.addMapping("item_scroll_down", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
            inputManager.addListener(this, "item_scroll_down");
        }
    }
    
    /**
     * Löscht alle Taste der Tastaturbelegung. Wird gebraucht, damit man mit {@link Main#setUpKeys()} wieder neue hinzufügen kann.
     */
    private void deleteKeys() {
        
        //Allgemeine Tasten
        inputManager.deleteMapping("Menu");
        //Tasten für SchnelleisteSlots
        inputManager.deleteMapping("item_1");
        inputManager.deleteMapping("item_2");
        inputManager.deleteMapping("item_3");
        inputManager.deleteMapping("item_4");
        inputManager.deleteMapping("item_5");
        
        inputManager.deleteMapping("debug");
        inputManager.deleteMapping("help");
        //Mausrad (wird nur entfernt, falls es vorhanden ist.
        if(inputManager.hasMapping("item_scroll_up")) {
          inputManager.deleteMapping("item_scroll_up");
          inputManager.deleteMapping("item_scroll_down");  
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
            } else if(binding.equals("help") && isPressed) {
                hudState.toggleHelpLayer();
            }
        }    
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void simpleUpdate(float tpf) {
        if(!nifty.getCurrentScreen().getScreenId().equals("hud") && !world.isPaused()){
            world.setPaused(true);
        }
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
        if(Main.app.getWorld().getPlayer().getName() != null){
            highscores.addHighscore(getWorld().getPlayer().getName(), game.getWave(), getWorld().getScene().getName());
        } else {
            highscores.addHighscore(game.getWave(), getWorld().getScene().getName());
        }
        nifty.gotoScreen("gameOver");
        world.getPlayer().stopAudio();
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
     * Gibt das Einstellungsobjekt, in dem alle Tastaturbelegungen gepseichert sind zurück.
     * @return Einstellungen
     */
    public Settings getSettings() {
        return settings;
    }
    
    /**
     * Methode, die aufgerufen wird, damit die Tastaturbelegung neu von der {@link Settings}-Klasse geladen wird.
     */
    public void reloadKeys() {
        world.getPlayer().reloadKeys();
        deleteKeys();
        setUpKeys();
    }
    
    public Highscores getHighscores(){
        return highscores;
    }
    
    public AppSettings getAppSettings(){
        return appSettings;
    }
    
    public ArrayList<String> getPossibleSceneNames(){
        return sceneNames;
    }
    
    public ArrayList<String> getPossibleSaveNames(){
        return saveNames;
    }
    
    public void updateSaveNames(){
        File saves = new File("saves");
        if(!saves.exists()){
            saves.mkdir();
        }
        File[] saveGames = saves.listFiles();
        saveNames = new ArrayList<>();
        for(int i = 0; i < saveGames.length; i ++){
            if(!saveGames[i].isHidden()){
                saveNames.add(0, saveGames[i].getName().substring(0, saveGames[i].getName().length()-5));
            }
        }
    }
    
    public Nifty getNifty(){
        return nifty;
    }
}