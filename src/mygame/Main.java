package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResults;
import com.jme3.collision.UnsupportedCollisionException;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
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
import de.lessvoid.nifty.Nifty;
import java.util.ArrayList;

/**
 * test
 * @author normenhansen
 */

public class Main extends SimpleApplication implements ActionListener{
    protected static Spatial scene;
    protected static Main app;
    protected static BulletAppState bulletAppState;
    private RigidBodyControl sceneC;
    private Node n;     //Aufhebare Objekte
    protected static Node bombNode;
    protected static ArrayList<Bomb> bombs;
    protected static Node towerNode;
    protected static ArrayList<Tower> towers;
    protected static Player player;
    protected static Beacon beacon;
    private Nifty nifty;
    private HudScreenState hudState;
    
    public static void main(String[] args) {
        app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        //Set this boolean true when the game loop should stop running when ever the window loses focus.
        app.setPauseOnLostFocus(true);
        
        scene = assetManager.loadModel("Scenes/newScene.j3o");
        scene.setLocalScale(2f);
        
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        
        bombs = new ArrayList<Bomb>();
        bombNode = new Node();
        rootNode.attachChild(bombNode);
        
        towers = new ArrayList<Tower>();
        towerNode = new Node();
        rootNode.attachChild(towerNode);
        
        player = new Player(this);
        player.setDamage(100);
        player.setHealth(100);
        
        beacon = new Beacon(new Vector3f(50, 0, 50), 100);
        
        setUpKeys();
        n = new Node();             // attach to n to let disappear when player is there
        Node n1 = new Node();       // attach to n1 to make collision resistant
                
        Bomb bomb = new Bomb(1, new Vector3f(0, 4, 0));
        bomb.setSpeed(10);
        bomb.setHealth(100);
        bomb.setDamage(100);
        bomb.move(beacon.getLocation());
        
        Bomb bomb1 = new Bomb(1, new Vector3f(0, 4, 0));
        bomb1.setSpeed(3);
        bomb1.setHealth(100);
        bomb1.setDamage(10);
        bomb1.move(new Vector3f(20, 0, 100));
        
        Bomb bomb2 = new Bomb(1, new Vector3f(0, 4, 0));
        bomb2.setSpeed(5);
        bomb2.setHealth(100);
        bomb2.setDamage(10);
        bomb2.move(new Vector3f(-20, 0, -100));
        
        
        Box b = new Box(1, 1, 1);
        Geometry geom = new Geometry("Box", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);
        geom.setMaterial(mat);
        geom.setLocalTranslation(5, 2, 5);
        n.attachChild(geom);
        
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape((Node) scene);
        sceneC = new RigidBodyControl(sceneShape, 0);
        scene.addControl(sceneC);
        
        Box c = new Box(1, 1, 1);
        Geometry geom1 = new Geometry("Box1", c);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Blue);
        geom1.setMaterial(mat1);
        geom1.setLocalTranslation(10, 2, 10);
        n1.attachChild(geom1);
        
        Box d = new Box (5,1,5);
        Geometry large = new Geometry("largeBox", d);
        large.setMaterial(mat1);
        large.setLocalTranslation(20, 2, 20);
        n1.attachChild(large);
        
        Sphere e = new Sphere(100, 100, 2);
        Geometry sphere = new Geometry("sphere", e);
        sphere.setMaterial(mat1);
        sphere.setLocalTranslation(-20, 2, -20);
        n1.attachChild(sphere);
        
        CollisionShape boxShape = CollisionShapeFactory.createMeshShape((Node) n1);
        RigidBodyControl boxC = new RigidBodyControl(boxShape, 0);
        n1.addControl(boxC);
              
        rootNode.attachChild(n);
        rootNode.attachChild(n1);
        rootNode.attachChild(scene);
        bulletAppState.getPhysicsSpace().add(sceneC);
        bulletAppState.getPhysicsSpace().add(boxC);
        
        //MyStartScreen myStartScreen = new MyStartScreen();
        //stateManager.attach(myStartScreen);
        //HudScreenState hudScreenState = new HudScreenState();
        //stateManager.attach(hudScreenState);
        
        
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
        hudState.setPlayer(player);
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
        player.onAction(binding, isPressed);
        if (binding.equals("Menu")) {
            nifty.gotoScreen("pause");
            flyCam.setDragToRotate(true);
            detachCrossHairs();
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
        for (int i = 0; i < n.getChildren().size(); i++)
            if(!n.getChildren().isEmpty())
                if((cam.getLocation().getX()-1 < n.getChild(i).getLocalTranslation().add(n.getLocalTranslation()).getX() && cam.getLocation().getX()+1 > n.getChild(i).getLocalTranslation().add(n.getLocalTranslation()).getX()) && (cam.getLocation().getZ()-1 < n.getChild(i).getLocalTranslation().add(n.getLocalTranslation()).getZ() && cam.getLocation().getZ()+1 > n.getChild(i).getLocalTranslation().add(n.getLocalTranslation()).getZ()))
                    n.detachChildAt(0); // If cam is in box -> detach box
        player.walk(tpf, cam);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    public void initCrossHairs(){
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+"); // crosshairs
        ch.setLocalTranslation( // center
          settings.getWidth() / 2 - ch.getLineWidth()/2, settings.getHeight() / 2 + ch.getLineHeight()/2, 0);
        guiNode.attachChildAt(ch, 0);
    }
    
    public void detachCrossHairs(){
        guiNode.detachChildAt(0);
    }
    
}