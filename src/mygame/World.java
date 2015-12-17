/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.state.AbstractAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.util.SkyFactory;
import java.util.ArrayList;
import java.util.HashMap;

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
    
    public World(Beacon beacon, Player player, Spatial scene) {
        this.beacon = beacon;
        this.player = player;
        Main.getBulletAppState().getPhysicsSpace().add(player.getCharacterControl());
        this.scene = scene;
        this.bombs = new HashMap<Spatial, Bomb>();
        this.towers = new HashMap<Spatial, Tower>();
        this.bombNode = new Node();
        this.towerNode = new Node();
        Main.app.getRootNode().attachChild(SkyFactory.createSky(
            Main.app.getAssetManager(), "Textures/sky/BrightSky.dds", false));
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
            if(!allTowers.get(i).isLiving()){
                this.removeTower(allTowers.get(i));
            }
        }
    }
    
}
