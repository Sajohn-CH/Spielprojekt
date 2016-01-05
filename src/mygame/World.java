/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import mygame.Entitys.Bomb;
import mygame.Entitys.Beacon;
import mygame.Entitys.Player;
import mygame.Entitys.Tower;
import com.jme3.app.state.AbstractAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author samuel
 */
public class World extends AbstractAppState{
    
    private Spatial scene;
    private HashMap<Spatial, Bomb> bombs;
    private HashMap<Spatial, Tower> towers;
    private Beacon beacon;
    private Player player;
    private Node bombNode;
    private Node towerNode;
    private Node wayNode;
    private File cornersFile = new File("assets/Scenes/scene_1.xml");
    
    public World(Beacon beacon, Player player, Spatial scene) {
        this.beacon = beacon;
        this.player = player;
        Main.getBulletAppState().getPhysicsSpace().add(player.getCharacterControl());
        this.scene = scene;
        this.bombs = new HashMap<Spatial, Bomb>();
        this.towers = new HashMap<Spatial, Tower>();
        this.bombNode = new Node();
        this.towerNode = new Node();
        this.wayNode = new Node();
        generateWayGeometries();
//        Main.app.getRootNode().attachChild(SkyFactory.createSky(
//            Main.app.getAssetManager(), "Textures/sky/BrightSky.dds", false));
//        Main.app.getRootNode().attachChild(SkyFactory.createSky(
//            Main.app.getAssetManager(), "Textures/sky/BrightSky","Textures/sky/BrightSky", "Textures/sky/BrightSky", "Textures/sky/BrightSky", "Textures/sky/BrightSky"
//                ,"Textures/sky/BrightSky", ));
        //TODO: weitermachen
        
        // Himmel laden
        Texture west = Main.app.getAssetManager().loadTexture("Textures/sky/BrightSky/west.jpg");
        Texture east = Main.app.getAssetManager().loadTexture("Textures/sky/BrightSky/east.jpg");
        Texture north = Main.app.getAssetManager().loadTexture("Textures/sky/BrightSky/north.jpg");
        Texture south = Main.app.getAssetManager().loadTexture("Textures/sky/BrightSky/south.jpg");
        Texture up = Main.app.getAssetManager().loadTexture("Textures/sky/BrightSky/up.jpg");
        Texture down = Main.app.getAssetManager().loadTexture("Textures/sky/BrightSky/down.jpg");
        Main.app.getRootNode().attachChild(SkyFactory.createSky(Main.app.getAssetManager(), west, east, north, south, up, down));
                 
        // Scene collidable machen
        Main.app.getRootNode().attachChild(scene);
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape((Node) scene);
        RigidBodyControl sceneC = new RigidBodyControl(sceneShape, 0);
        scene.addControl(sceneC);
        Main.getBulletAppState().getPhysicsSpace().add(sceneC);
    }
    
    public void addBomb(Bomb bomb) {
        bombNode.attachChild(bomb.getSpatial());
        this.bombs.put(bomb.getSpatial(), bomb);
    }
    
    public Bomb getBomb(Spatial spatial) {
        return bombs.get(spatial);
    }
    
    public ArrayList<Bomb> getAllBombs() {
        return new ArrayList<Bomb>(bombs.values());
    }
    
    public void removeBomb(Bomb bomb){
        bombNode.detachChild(bomb.getSpatial());
        this.bombs.remove(bomb.getSpatial());
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public Beacon getBeacon() {
        return beacon;
    }
    
    public Spatial getScene(){
        return scene;
    }
    
    public void addTower(Tower tower) {
        towerNode.attachChild(tower.getSpatial());
        this.towers.put(tower.getSpatial(), tower);
        tower.setCollidable();
    }
    
    public Tower getTower (Spatial spatial) {
        return towers.get(spatial);
    }
    
    public ArrayList<Tower> getAllTowers() {
        return new ArrayList<Tower>(towers.values());
    }
    
    public void removeTower(Tower tower){
        if(tower.isDead()){
            tower.removeFireEffect();
        }
        towerNode.detachChild(tower.getSpatial());
        this.towers.remove(tower.getSpatial());
    }

    public Node getBombNode() {
        return bombNode;
    }

    public Node getTowerNode() {
        return towerNode;
    }
    
    public Tower getNearestTower(Vector3f location){
        ArrayList<Tower> allTowers = this.getAllTowers();
        if(!allTowers.isEmpty()){
            Tower nearest = allTowers.get(0);
            for(int i = 1; i < allTowers.size(); i ++){
                if(allTowers.get(i).getLocation().subtract(location).length() < nearest.getLocation().subtract(location).length()){
                    nearest = allTowers.get(i);
                }
            }
            return nearest;
        }
        return null;
    }
    
    @Override
    public void update (float tpf){
        ArrayList<Bomb> allBombs = this.getAllBombs();
        ArrayList<Tower> allTowers= this.getAllTowers();
        
        player.action(tpf);
        beacon.action(tpf);
        for(int i = 0; i < allBombs.size(); i++){
            allBombs.get(i).action(tpf);
            if(!allBombs.get(i).isLiving()){
                this.removeBomb(allBombs.get(i));
            }
            
        }
        for(int i = 0; i < allTowers.size(); i++){
            allTowers.get(i).action(tpf);
            if(!allTowers.get(i).isLiving() && allTowers.get(i).canRemove()){
                this.removeTower(allTowers.get(i));
            }
        }
    }
    
    public ArrayList<Vector3f> getAllCorners(){
        //Corners xml-file auslesen
        ArrayList<Vector3f> corners = new ArrayList<Vector3f>();
        try {
            DocumentBuilderFactory dbFacotry = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFacotry.newDocumentBuilder();
            Document doc = dBuilder.parse(cornersFile);
            dBuilder = dbFacotry.newDocumentBuilder();
            doc.getDocumentElement().normalize();
            
            NodeList list = doc.getElementsByTagName("corner");
            for(int i = 0; i < list.getLength(); i++) {
                Element element = getElement(i, list);
                
                Vector3f vector = new Vector3f();
                vector.setX(Float.valueOf(element.getAttribute("x")));
                vector.setY(Float.valueOf(element.getAttribute("y")));
                vector.setZ(Float.valueOf(element.getAttribute("z")));
                corners.add(vector);
            }
        } catch (Exception ex) {
            Logger.getLogger(World.class.getName()).log(Level.SEVERE, null, ex);
        }
        return corners;
    }
    
    private Element getElement(int id, NodeList list) {
        for(int i = 0; i < list.getLength(); i++) {
            Element element = (Element) list.item(i);
            if(element.getAttribute("id").equals(String.valueOf(id))) {
                return element;
            }
        }
        return null;
    }
    
    private void generateWayGeometries(){
        //Unsichtbare Geometries auf den Weg legen, damit dort nichts gebaut werden kann.
        ArrayList<Vector3f> corners = getAllCorners();
        Geometry geom1C = new Geometry("Corner0", new Box(corners.get(0).mult(2), 6, 0, 6));
        Material mat = new Material();
        geom1C.setMaterial(mat);
        wayNode.attachChild(geom1C);
        for(int i = 1; i < corners.size(); i++){
            Geometry geomC = new Geometry("Corner" + i, new Box(corners.get(i).mult(2), 6, 0, 6));
            geomC.setMaterial(mat);
            wayNode.attachChild(geomC);
            Geometry geom = new Geometry("WayPiece " + i, new Box(corners.get(i).add(corners.get(i-1)), corners.get(i).subtract(corners.get(i-1)).x+6, 0, corners.get(i).subtract(corners.get(i-1)).z+6));
            geom.setMaterial(mat);
            wayNode.attachChild(geom);
        }
    }
    
    public Node getWayNode(){
        return this.wayNode;
    }
    
    public void setPaused(boolean pause){
        this.setEnabled(!pause);
    }
    
    public boolean isPaused(){
        return !this.isEnabled();
    }
}
 