package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
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

/**
 * test
 * @author normenhansen
 */

public class Main extends SimpleApplication implements ActionListener{
    private Spatial scene;
    protected static Main app;
    protected static BulletAppState bulletAppState;
    private RigidBodyControl sceneC;
    private CharacterControl player;
    private Vector3f walkDirection = new Vector3f();
    private boolean left = false, right = false, up = false, down = false;
    private Vector3f camDir = new Vector3f();
    private Vector3f camLeft = new Vector3f();
    private Node n;     //Aufhebare Objekte
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
        
        setUpKeys();
        n = new Node();             // attach to n to let disappear when player is there
        Node n1 = new Node();       // attach to n1 to make collision resistant
                
        Bomb bomb = new Bomb(1, new Vector3f(0, 4, 0));
        stateManager.attach(bomb);
        rootNode.attachChild(bomb.n);
        bomb.move(new Vector3f(100, 0, 50));
        
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
              
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 6f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);
        player.setJumpSpeed(20);
        player.setFallSpeed(90);
        player.setGravity(30);
        player.setPhysicsLocation(new Vector3f(0, 10, 0));
    
        rootNode.attachChild(n);
        rootNode.attachChild(n1);
        rootNode.attachChild(scene);
        bulletAppState.getPhysicsSpace().add(sceneC);
        bulletAppState.getPhysicsSpace().add(boxC);
        bulletAppState.getPhysicsSpace().add(player);
        
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
    //Tasten um Spieler zu Steuern
    inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
    inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
    inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
    inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
    inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
    inputManager.addListener(this, "Left");
    inputManager.addListener(this, "Right");
    inputManager.addListener(this, "Up");
    inputManager.addListener(this, "Down");
    inputManager.addListener(this, "Jump");
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
    
    public void onAction(String binding, boolean isPressed, float tpf) {
    if (binding.equals("Left")) {
        left = isPressed;
    } else if (binding.equals("Right")) {
        right= isPressed;
    } else if (binding.equals("Up")) {
         up = isPressed;
    } else if (binding.equals("Down")) {
         down = isPressed;
    } else if (binding.equals("Jump")) {
         if (isPressed) { player.jump(); }
    } else if (binding.equals("Menu")) {
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
        for (int i = 0; i < n.getChildren().size(); i++)
            if(!n.getChildren().isEmpty())
                if((cam.getLocation().getX()-1 < n.getChild(i).getLocalTranslation().add(n.getLocalTranslation()).getX() && cam.getLocation().getX()+1 > n.getChild(i).getLocalTranslation().add(n.getLocalTranslation()).getX()) && (cam.getLocation().getZ()-1 < n.getChild(i).getLocalTranslation().add(n.getLocalTranslation()).getZ() && cam.getLocation().getZ()+1 > n.getChild(i).getLocalTranslation().add(n.getLocalTranslation()).getZ()))
                    n.detachChildAt(0); // If cam is in box -> detach box
        camDir.set(cam.getDirection()).multLocal(0.6f);
        camLeft.set(cam.getLeft()).multLocal(0.4f);
        walkDirection.set(0, 0, 0);
        if (left) {
            walkDirection.addLocal(camLeft);
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (up) {
            walkDirection.addLocal(camDir);
        }
        if (down) {
            walkDirection.addLocal(camDir.negate());
        }
        //walkDirection.normalizeLocal();
        //walkDirection.multLocal(50 * tpf);
        player.setWalkDirection(walkDirection);
        cam.setLocation(player.getPhysicsLocation());
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}