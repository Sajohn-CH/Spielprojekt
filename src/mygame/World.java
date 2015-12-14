/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.state.AbstractAppState;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author samuel
 */
public class World extends AbstractAppState{
    
    private HashMap<Spatial, Bomb> bombs;
    private HashMap<Spatial, Tower> towers;
    private Beacon beacon;
    private Player player;
    private Node bombNode;
    private Node towerNode;
    
    public World(Beacon beacon, Player player) {
        this.beacon = beacon;
        this.player = player;
        this.bombs = new HashMap<Spatial, Bomb>();
        this.towers = new HashMap<Spatial, Tower>();
        this.bombNode = new Node();
        this.towerNode = new Node();
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
    
    public Player getPlayer() {
        return player;
    }
    
    public Beacon getBeacon() {
        return beacon;
    }
    
    public void addTower(Tower tower) {
        towerNode.attachChild(tower.getSpatial());
        this.towers.put(tower.getSpatial(), tower);
    }
    
    public Tower getTower (Spatial spatial) {
        return towers.get(spatial);
    }
    
    public ArrayList<Tower> getAllTowers() {
        return new ArrayList<Tower>(towers.values());
    }

    public Node getBombNode() {
        return bombNode;
    }

    public Node getTowerNode() {
        return towerNode;
    }
    
    @Override
    public void update (float tpf){
        ArrayList<Tower> allTowers= this.getAllTowers();
        ArrayList<Bomb> allBombs = this.getAllBombs();
        
        player.action(tpf);
        beacon.action(tpf);
        for(int i = 0; i < allBombs.size(); i++){
            allBombs.get(i).action(tpf);
        }
        for(int i = 0; i < allTowers.size(); i++){
            allTowers.get(i).action(tpf);
        }
    }
    
}
