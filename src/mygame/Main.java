package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.FlyByCamera;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import de.lessvoid.nifty.Nifty;

/**
 * test
 * @author normenhansen
 */

public class Main extends SimpleApplication {
    
    public static void main(String[] args) {
        System.out.println("test");
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        Spatial scene = assetManager.loadModel("Scenes/newScene.j3o");
        scene.setLocalTranslation(0, -5, 0);
        //scene.setLocalScale(2f);
        rootNode.attachChild(scene);
        flyCam.setMoveSpeed(50);
         
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay( assetManager, inputManager, audioRenderer, guiViewPort);
        //Create a new NiftyGui objects
        Nifty nifty = niftyDisplay.getNifty();
        //Read yout XML and initialize your custom Screen Controller
        nifty.fromXml("Interface/screen.xml", "start");
        //attach the Niftry display to the gui view port as a processor
        guiViewPort.addProcessor(niftyDisplay);
        //disable the fly cam
        flyCam.setDragToRotate(true);
        
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
