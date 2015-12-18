package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import de.lessvoid.nifty.Nifty;
import java.util.ArrayList;

/**
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
    
    public static void main(String[] args) {
        app = new Main();
        
        //AppSettings initialisieren
        AppSettings appSettings = new AppSettings(true);
        //Titel setzen
        appSettings.put("Title", "First-Person-View TowerDefense Game");
        //AppSettings hinzufügen
        app.setSettings(appSettings);
        
        app.start();
    }

    @Override
    public void simpleInitApp() {
        //Set this boolean true when the game loop should stop running when ever the window loses focus.
        app.setPauseOnLostFocus(true);
        
        Spatial scene = assetManager.loadModel("Scenes/scene_1.j3o");
        scene.setLocalScale(2f);
        
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        
        Player player = new Player(this);
        player.setDamage(100);
        player.setHealth(100);
        
        Beacon beacon = new Beacon(new Vector3f(0, 0, 0), 100);
        world = new World(beacon, player, scene);
        stateManager.attach(world);
        
        rootNode.attachChild(world.getBombNode());
        rootNode.attachChild(world.getTowerNode());
        rootNode.attachChild(world.getBeacon().getSpatial());
        
        setUpKeys();
//        n = new Node();             // attach to n to let disappear when player is there
//        Node n1 = new Node();       // attach to n1 to make collision resistant
                
        Bomb bomb = new Bomb(1);
        bomb.setSpeed(50);
        world.addBomb(bomb);
        
        Bomb bomb1 = new Bomb(1);
        bomb1.setSpeed(50);
        world.addBomb(bomb1);
        
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay( assetManager, inputManager, audioRenderer, guiViewPort);
        //Create a new NiftyGui objects
        nifty = niftyDisplay.getNifty();
        //Read yout XML and initialize your custom Screen Controller
        nifty.addXml("Interface/hud.xml");
        nifty.addXml("Interface/screen.xml"); 
        MyStartScreen startState = (MyStartScreen) nifty.getScreen("start").getScreenController();
        nifty.registerScreenController(startState);
        nifty.gotoScreen("hud");
        stateManager.attach(startState);
        
        hudState = (HudScreenState) nifty.getScreen("hud").getScreenController();
        //hudState.setPlayer(player);
        hudState.setWorld(world);
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
        
    }
    
    private void setUpKeys() {
        //Allgemeine Tasten
        inputManager.addMapping("Menu", new KeyTrigger(KeyInput.KEY_ESCAPE));
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
        //Mausrad
        inputManager.addMapping("item_scroll_up", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addListener(this, "item_scroll_up");
        inputManager.addMapping("item_scroll_down", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        inputManager.addListener(this, "item_scroll_down");
    }
    
    @Override
    public void onAction(String binding, boolean isPressed, float tpf) {
        world.getPlayer().onAction(binding, isPressed);
        if (binding.equals("Menu")) {
            nifty.gotoScreen("pause");
            flyCam.setDragToRotate(true);
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
        }
    
    }

    @Override
    public void simpleUpdate(float tpf) {
//        for (int i = 0; i < n.getChildren().size(); i++)
//            if(!n.getChildren().isEmpty())
//                if((cam.getLocation().getX()-1 < n.getChild(i).getLocalTranslation().add(n.getLocalTranslation()).getX() && cam.getLocation().getX()+1 > n.getChild(i).getLocalTranslation().add(n.getLocalTranslation()).getX()) && (cam.getLocation().getZ()-1 < n.getChild(i).getLocalTranslation().add(n.getLocalTranslation()).getZ() && cam.getLocation().getZ()+1 > n.getChild(i).getLocalTranslation().add(n.getLocalTranslation()).getZ()))
//                    n.detachChildAt(0); // If cam is in box -> detach box
       // world.getPlayer().action(tpf);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    public static World getWorld() {
        return world;
    }
    
    public HudScreenState getHudState() {
        return hudState;
    }
    
    public static BulletAppState getBulletAppState(){
        return bulletAppState;
    }
}