package mygame;

import mygame.Entitys.Bomb;
import mygame.Entitys.Beacon;
import mygame.Entitys.Player;
import mygame.Entitys.Tower;
import com.jme3.app.state.AbstractAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Line;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import java.util.ArrayList;
import java.util.HashMap;
import mygame.Entitys.ShootingBomb;

/**
 * Die Spielwelt. Sie enthält alle nötigen Element der Welt: Die Umgebung (scene), alle Bomben und Türme, den Beacon (den es zu verteidigen gilt), den Spieler, sowie den Weg den die Bomben zurücklegen müssen. 
 * Auch entählt es die Nodes (Gruppe von graphischen Objekten in der JMonkeyEngine), an den die Türme und Bomben "befestigt" sind und damit angezeigt werden.
 * @author Samuel Martin und Florian Wenk
 */
public class World extends AbstractAppState{
    
    private Spatial scene;                  //Die Spielszene
    private HashMap<Spatial, Bomb> bombs;   //Alle Bomben. Jeweils mit ihrem Spatial (Objekt in der 3D-Welt)
    private HashMap<Spatial, Tower> towers;     //Alle Türme. Jeweils mit ihrem Spatial (Objekt in der 3D-Welt)
    private Beacon beacon;                  //Der Beacon
    private Player player;                  //Der Spieler
    private Node bombNode;                  //Node mit allen Bomben
    private Node towerNode;                 //Node mit allen Türmen
    private Node wayNode;                   //Node mit dem Weg der Bomben. Blockiert diesen, damit nichts drauf gebaut werden kann
    private ArrayList<Vector3f> corners;               //ArrayList, in der alle Eckpunkte des Weges gespeichert sind.
    private Float bombMultiplier;
    private Float towerMultiplier;
    
    /**
     * Konstruktor. Lädt den Himmel, fügt die Szene hinzu und initialisiert alles.
     * @param beacon Den Beacon in der Spielwelt   
     * @param player Der Spieler in der Spielwelt  
     * @param sceneName Der Name der "Szene", die Umgebung der Spielwelt bzw. die Spielwelt selbst
     */
    public World(Beacon beacon, Player player, String sceneName) {
        this.beacon = beacon;
        this.player = player;
        Main.getBulletAppState().getPhysicsSpace().add(player.getCharacterControl());
        this.bombs = new HashMap<Spatial, Bomb>();
        this.towers = new HashMap<Spatial, Tower>();
        this.bombNode = new Node();
        this.towerNode = new Node();
        this.wayNode = new Node();
        this.setScene(sceneName);
        
        // Himmel laden
        Texture west = Main.app.getAssetManager().loadTexture("Textures/sky/BrightSky/west.jpg");
        Texture east = Main.app.getAssetManager().loadTexture("Textures/sky/BrightSky/east.jpg");
        Texture north = Main.app.getAssetManager().loadTexture("Textures/sky/BrightSky/north.jpg");
        Texture south = Main.app.getAssetManager().loadTexture("Textures/sky/BrightSky/south.jpg");
        Texture up = Main.app.getAssetManager().loadTexture("Textures/sky/BrightSky/up.jpg");
        Texture down = Main.app.getAssetManager().loadTexture("Textures/sky/BrightSky/down.jpg");
        Main.app.getRootNode().attachChild(SkyFactory.createSky(Main.app.getAssetManager(), west, east, north, south, up, down));
    }
    
    /**
     * Fügt der Welt eine Bombe hinzu. Diese wird auch grad dem entsprechenden Node hinzugefügt, sodass die Bombe angezeigt wird.
     * @param bomb Hinzuzufügenden Bombe
     */
    public void addBomb(Bomb bomb) {
        bombNode.attachChild(bomb.getSpatial());
        this.bombs.put(bomb.getSpatial(), bomb);
    }
    
    /**
     * Gibt die Bombe zurück, die dem übergeben Spatial (graphisches Objekt der JMonkeyEngine) entspricht. 
     * @param spatial Spatial der gewünschen Bombe
     * @return Bombe
     */
    public Bomb getBomb(Spatial spatial) {
        return bombs.get(spatial);
    }
    
    /**
     * Gibt alle Bomben als ArrayList zurück.
     * @return Alle Bomben der Welt
     */
    public ArrayList<Bomb> getAllBombs() {
        return new ArrayList<Bomb>(bombs.values());
    }
    
    /**
     * Entfernt eine Bombe.
     * @param bomb  Zu entfernende Bombe
     */
    public void removeBomb(Bomb bomb){
        bombNode.detachChild(bomb.getSpatial());
        this.bombs.remove(bomb.getSpatial());
    }
    
    /**
     * Gibt den Spieler der Welt zurück.
     * @return  Spieler
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * Gibt den Beacon der Welt zurück.
     * @return  Beacon
     */
    public Beacon getBeacon() {
        return beacon;
    }
    
    /**
     * Gibt die Szene der Welt zurück.
     * @return  Szene
     */
    public Spatial getScene(){
        return scene;
    }
    
    /**
     * Gibt die Szene der Welt zurück.
     * @return  Szene
     */
    public void setScene(String name){
        if(Main.app.getRootNode().hasChild(scene)){
            Main.app.getRootNode().detachChild(scene);
            Main.getBulletAppState().getPhysicsSpace().remove(scene.getControl(RigidBodyControl.class));
        }
        scene = Main.app.getAssetManager().loadModel("Scenes/" + name + ".j3o");
        scene.setLocalScale(2f);
        scene.setName(name);         
        // Scene collidable machen
        Main.app.getRootNode().attachChild(scene);
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape((Node) scene);
        RigidBodyControl sceneC = new RigidBodyControl(sceneShape, 0);
        scene.addControl(sceneC);
        Main.getBulletAppState().getPhysicsSpace().add(sceneC);
        
        HashMap<String, Object> data = (HashMap<String, Object>) Main.app.getAssetManager().loadAsset("Scenes/" + scene.getName() + ".sceneData");
        bombMultiplier = (Float) data.get("bombMultiplier");
        towerMultiplier = (Float) data.get("towerMultiplier");
        
        this.corners = (ArrayList<Vector3f>) Main.app.getAssetManager().loadAsset("Scenes/" + scene.getName() + ".corners");
        wayNode.detachAllChildren();
        generateWayGeometries();
    }
    
    /**
     * Fügt einen Turm der Welt hinzu. Dieser wird auch den entpsrehcenden Node hinzugefügt, so dass der Turm auch angezeigt wird.
     * @param tower 
     */
    public void addTower(Tower tower) {
        towerNode.attachChild(tower.getSpatial());
        this.towers.put(tower.getSpatial(), tower);
        tower.setCollidable();
    }
    
    /**
     * Gibt den Turm zurück, welcher dem übergebenen Spatial (graphisches Objekt der JMonkeyEngine) entspricht.
     * @param spatial  Spatial des Turms
     * @return Turm
     */
    public Tower getTower (Spatial spatial) {
        return towers.get(spatial);
    }
    
    /**
     * Gibt alle Türme der Spielwelt als ArrayList zurück.
     * @return Alle Türme
     */
    public ArrayList<Tower> getAllTowers() {
        return new ArrayList<Tower>(towers.values());
    }
    
    /**
     * Entfernt einen Turm.
     * @param tower Zu entfernender Turm.
     */
    public void removeTower(Tower tower){
        if(tower.isDead()){
            tower.removeFireEffect();
        }
        towerNode.detachChild(tower.getSpatial());
        this.towers.remove(tower.getSpatial());
        Main.getBulletAppState().getPhysicsSpace().remove(tower.getSpatial());
    }

    /**
     * Gibt den Node mit allen Bomben zurück.
     * @return Bombennode
     */
    public Node getBombNode() {
        return bombNode;
    }

    /**
     * Gibt den Node mit allen Türmen zurück
     * @return Turmnode
     */
    public Node getTowerNode() {
        return towerNode;
    }
    
    /**
     * Gibt den nächsten Turm bei einem bestimmten Ort zurück.
     * @param location Ort
     * @return nächsten Turm
     */
    public Tower getNearestTower(Vector3f location){
        ArrayList<Tower> allTowers = this.getAllTowers();
        if(!allTowers.isEmpty()){
            Tower nearest = allTowers.get(0);
            for(int i = 1; i < allTowers.size(); i ++){
                if(allTowers.get(i).getSpatial().getLocalTranslation().subtract(location).length() < nearest.getSpatial().getLocalTranslation().subtract(location).length()){
                    nearest = allTowers.get(i);
                }
            }
            return nearest;
        }
        return null;
    }
    
    /**
     * Gibt die nächste Bombe bei einem bestimmten Ort zurück.
     * @param location Ort
     * @return nächste Bombe
     */
    public Bomb getNearestBomb(Vector3f location){
        ArrayList<Bomb> allBombs = this.getAllBombs();
        if(!allBombs.isEmpty()){
            Bomb nearest = allBombs.get(0);
            for(int i = 1; i < allBombs.size(); i ++){
                if(allBombs.get(i).getSpatial().getLocalTranslation().subtract(location).length() < nearest.getSpatial().getLocalTranslation().subtract(location).length()){
                    nearest = allBombs.get(i);
                }
            }
            return nearest;
        }
        return null;
    }
    
    /**
     * Gibt die nächste ShootingBomb bei einem bestimmten Ort zurück.
     * @param location Ort
     * @return nächste ShootingBomb
     */
    public ShootingBomb getNearestShootingBomb(Vector3f location){
        ArrayList<Bomb> allBombs = this.getAllBombs();
        if(!allBombs.isEmpty()){
            ShootingBomb nearest = null;
            for(int i = 0; i < allBombs.size(); i ++){
                if(nearest == null && allBombs.get(i).getSpatial().getName().equals("shootingBomb")){
                    nearest = (ShootingBomb) allBombs.get(i);
                }else if(nearest != null && allBombs.get(i).getSpatial().getLocalTranslation().subtract(location).length() < nearest.getSpatial().getLocalTranslation().subtract(location).length() && allBombs.get(i).getSpatial().getName().equals("shootingBomb")){
                    nearest = (ShootingBomb) allBombs.get(i);
                }
            }
            return nearest;
        }
        return null;
    }
    
    /**
     * Gibt die nächste Normale Bombe bei einem bestimmten Ort zurück.
     * @param location Ort
     * @return nächste normale Bombe
     */
    public Bomb getNearestNormalBomb(Vector3f location){
        ArrayList<Bomb> allBombs = this.getAllBombs();
        if(!allBombs.isEmpty()){
            Bomb nearest = null;
            for(int i = 0; i < allBombs.size(); i ++){
                if(nearest == null && allBombs.get(i).getSpatial().getName().equals("bomb")){
                    nearest = allBombs.get(i);
                }else if(nearest != null && allBombs.get(i).getSpatial().getLocalTranslation().subtract(location).length() < nearest.getSpatial().getLocalTranslation().subtract(location).length()){
                    if(allBombs.get(i).getSpatial().getName().equals("bomb")){
                        nearest = allBombs.get(i);
                    } else {
                        ShootingBomb sBomb = (ShootingBomb) allBombs.get(i);
                        if(!sBomb.isShooting()){
                            nearest = (Bomb) sBomb;
                        }
                    }
                }
            }
            return nearest;
        }
        return null;
    }
    
    /**
     * Gibt alle Bomben zurück die einen Maximalabstand zu einem Ort haben zurück.
     * @param location Ort
     * @param range Maximalabstand
     * @return alle Bomben im Umkreis
     */
     public ArrayList<Bomb> getAllBombsInRange(Vector3f location, int range){
        ArrayList<Bomb> allBombs = this.getAllBombs();
        ArrayList<Bomb> bombsInRange = new ArrayList<Bomb>();
        if(!allBombs.isEmpty()){
            for(int i = 0; i < allBombs.size(); i ++){
                if(allBombs.get(i).getSpatial().getLocalTranslation().subtract(location).length() <= range){
                    bombsInRange.add(allBombs.get(i));
                }
            }
            return bombsInRange;
        }
        return null;
    }
     
     /**
     * Gibt alle ShootingBombs zurück die einen Maximalabstand zu einem Ort haben zurück.
     * @param location Ort
     * @param range Maximalabstand
     * @return alle ShootingBombs im Umkreis
     */
     public ArrayList<ShootingBomb> getAllShootingBombsInRange(Vector3f location, int range){
        ArrayList<Bomb> allBombs = this.getAllBombs();
        ArrayList<ShootingBomb> shootingBombsInRange = new ArrayList<ShootingBomb>();
        if(!allBombs.isEmpty()){
            for(int i = 0; i < allBombs.size(); i ++){
                if(allBombs.get(i).getSpatial().getLocalTranslation().subtract(location).length() <= range && allBombs.get(i).getSpatial().getName().equals("shootingBomb")){
                    ShootingBomb sBomb = (ShootingBomb) allBombs.get(i);
                    if(sBomb.isShooting()){
                        shootingBombsInRange.add(sBomb);
                    }
                }
            }
            return shootingBombsInRange;
        }
        return null;
    }
     
     /**
     * Gibt alle Normalen Bomben zurück die einen Maximalabstand zu einem Ort haben zurück.
     * @param location Ort
     * @param range Maximalabstand
     * @return alle normalen Bomben im Umkreis
     */
     public ArrayList<Bomb> getAllNormalBombsInRange(Vector3f location, int range){
        ArrayList<Bomb> allBombs = this.getAllBombs();
        ArrayList<Bomb> normalBombsInRange = new ArrayList<Bomb>();
        if(!allBombs.isEmpty()){
            for(int i = 0; i < allBombs.size(); i ++){
                if(allBombs.get(i).getSpatial().getLocalTranslation().subtract(location).length() <= range){
                    if(allBombs.get(i).getSpatial().getName().equals("bomb")){
                        normalBombsInRange.add(allBombs.get(i));
                    } else {
                        ShootingBomb sBomb = (ShootingBomb) allBombs.get(i);
                        if(!sBomb.isShooting()){
                            normalBombsInRange.add((Bomb) sBomb);
                        }
                    }
                }
            }
            return normalBombsInRange;
        }
        return null;
    }
    
    /**
     * {@inheritDoc }
     *
     */
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
    
    /**
     * Gibt alle Ecken des Weges zurück, den die Bomben zurücklegen um zum Beacon zu gelangen. Die Ecken werden aus einer XML-Datei ausgelesen. 
     * @return Alle Ecken des Weges
     */
    public ArrayList<Vector3f> getAllCorners(){
        return corners;
    }
    
    /**
     * Legt unsichtbare Elemente auf den Weg, damit darauf nichts gebaut werden kann.
     */
    private void generateWayGeometries(){
        //Unsichtbare Geometries auf den Weg legen, damit dort nichts gebaut werden kann.
        ArrayList<Vector3f> corners = getAllCorners();
        Geometry geom1C = new Geometry("Corner0", new Box(corners.get(0).mult(2), 6, 0, 6));
        Material mat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Yellow);
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
//        Zeigt zur Hilfe die Geometries auf dem Weg an.
//        Main.app.getRootNode().attachChild(wayNode);
    }
    
    /**
     * Gitb den Node mit dem Weg zurück.
     * @return Wegnode
     */
    public Node getWayNode(){
        return this.wayNode;
    }
    
    /**
     * Pausiert oder entpausiert das Spiel
     * @param pause true = pausieren, false = entpausieren (weiterfahren)
     */
    public void setPaused(boolean pause){
        this.setEnabled(!pause);
        player.setNotShootingAndHealing();
        player.setNotWalking();
    }
    
    /**
     * Gibt zurück ob das Spiel pausiert.
     * @return Ist Spiel pausiert? 
     */
    public boolean isPaused(){
        return !this.isEnabled();
    }
    
    /**
     * Gibt zurück ob der Punkt further weiter entfernt ist als der Punkt nearer
     * @param origin Von wo
     * @param further weiter Entfernt?
     * @param nearer Näher?
     * @return Ob further weiter entfernt als nearer -- wenn einer null dann true
     */
    public boolean distanceIsLonger(Vector3f origin, Vector3f further, Vector3f nearer){
        if(origin == null || further == null || nearer == null){
            return true;
        }
        return origin.distance(further) > origin.distance(nearer);
    }
    
    public Float getBombMultiplier(){
        return this.bombMultiplier;
    }
    
    public Float getTowerMultiplier(){
        return this.towerMultiplier;
    }
}