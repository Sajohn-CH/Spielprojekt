package mygame.Entitys;


import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import mygame.Main;
import mygame.Way;

/**
 * Eine Bombe. Erstellt eine Bombe und kontrolliert diese. {@link Entity}
 * @author Florian Wenk
 */
public class Bomb extends Entity{
    
    private Material mat;
    private ColorRGBA[] colors = {ColorRGBA.Blue, ColorRGBA.Cyan, ColorRGBA.Green, ColorRGBA.Magenta, ColorRGBA.Red, ColorRGBA.Orange, ColorRGBA.Yellow, ColorRGBA.Green, ColorRGBA.Pink, ColorRGBA.Brown};
    private Way way;
    private int money;
    private int decreasedSpeed;
    private Node n;
    
    /**
     * Erstellt die Bombe. Erstellt den Spatial der Bombe und setzt den Weg.
     * @param level 
     */
    public Bomb (int level){
        this.setLiving(true);
        
        //Spatial erstellen
        n = new Node("bomb");
        Sphere sphere = new Sphere(100, 100, 1);
        Cylinder cylinder = new Cylinder(50, 50, 1.01f, .25f);
        n.attachChild(new Geometry("bomb",sphere));
        n.attachChild(new Geometry("color",cylinder));
        Quaternion q = new Quaternion();
        q.fromAngleAxis((float) Math.PI/2 , new Vector3f(1,0,0));
        n.getChild("color").rotate(q);
        
        mat = new Material (Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        // Setzt das Maximale Level auf Level 40
        if(level > 40){
            level = 40;
        }
        this.setLevel(level);
        
        n.getChild("bomb").setMaterial(Main.app.getAssetManager().loadMaterial("Materials/Black.j3m"));
        n.getChild("color").setMaterial(mat);
        this.setSpatial(n);
        
        way = new Way();
        super.setLocation(way.getStartPoint().setY(2));
        this.getSpatial().setLocalTranslation(way.getStartPoint());
        money = 5;
    }
    
    /**
     * {@inheritDoc }
     *
     */
    @Override
    public void increaseHealth(int health){
        super.increaseHealth(health);
        if(!this.isLiving())
            if(this.getLevel() > 1){
                this.setLevel(this.getLevel()-1);
                this.setLiving(true);
            }
    }
        
    /**
     * Fügt einem Gegenstand Schaden zu.
     * @param e Der Gegenstand dem Schadenzugefügt werden soll.
     */
    public void makeDamage(Entity e){
        e.increaseHealth(-this.getDamage());
    }
    
    /**
     * Bewegt die Bombe. Lässt die Bombe zu dem neuen Platz schweben.
     * @param offset Vektor wie weit und in welche Richtung.
     */
    public void move(Vector3f offset){
        offset.setY(0);
        super.setLocation(offset.add(this.getLocation()));
    }
    
    /**
     * Lässt die Bombe zu einem bestimmten Ort wandern. Sorgt dafür dass die Bombe sich langsam zum Zielort bewegt.
     * @param location Der Ort zu dem die Bombe gehen soll.
     */
    private void moveTo(Vector3f location){
        move(location.subtract(this.getLocation()));
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void setLocation(Vector3f location){
        super.setLocation(location);
        this.getSpatial().setLocalTranslation(location);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void action(float tpf) {
        if(this.getLocation().subtract(this.getSpatial().getLocalTranslation()).length() > 1){
            Vector3f v = new Vector3f(this.getLocation().subtract(this.getSpatial().getLocalTranslation()));
            v.divideLocal(10*v.length());
            v.multLocal(5*tpf);
            this.getSpatial().move(v.mult(this.getSpeed()));
        } else {
            moveTo(way.getNextCorner());
        }
        if(Main.app.getCamera().getLocation().subtract(this.getSpatial().getLocalTranslation()).length() <= 5 && this.isLiving() && Main.getWorld().getPlayer().isLiving()){
                this.makeDamage(Main.getWorld().getPlayer());
                this.setLiving(false);
                money = 0;
        }
        if(Main.getWorld().getBeacon().getSpatial().getLocalTranslation().subtract(this.getSpatial().getLocalTranslation()).length() <= 3 && isLiving()){
                this.makeDamage(Main.getWorld().getBeacon());
                this.setLiving(false);
                money = 0;
        }
        if(!this.isLiving()){
//            Main.getBulletAppState().getPhysicsSpace().remove(bombC);
            Main.app.getWorld().getPlayer().increaseMoney(money);
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    protected void die() {
        if(this.getLevel() == 1) {
            super.die();
        } else {
            setLevel(getLevel()-1);
        }
        Main.getWorld().getPlayer().increaseMoney(money);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void setLevel(int newLevel) {
        //setzt Leben als Funktion mit level
        this.setHealth(50+newLevel*50);
        this.setDamage(10+newLevel*10);
        this.setSpeed(50-newLevel*2+Main.getGame().getWave()*2-decreasedSpeed);
        
        mat.setColor("Color", colors[(newLevel-1)%colors.length]);
        super.setLevel(newLevel);
    }
     /**
      * Verlangsamt die Bombe. Verringert das Tempo der Bombe.
      * @param speed Die Anzahl der Tempopunkte, die abezogen werden sollen.
      */
    public void decreaseSpeed (int speed){
        this.decreasedSpeed += speed;
        if(decreasedSpeed >= (50-this.getLevel()*2+Main.getGame().getWave()*2)-10-getLevel()*2){
            decreasedSpeed = (50-this.getLevel()*2+Main.getGame().getWave()*2)-10-getLevel()*2;
        }
        this.setSpeed((50-this.getLevel()*2+Main.getGame().getWave()*2) - decreasedSpeed);
    }
    
    /**
     * 
     * @return Um wieviel die Bombe verlangsamt ist.
     */
    public int getDecreasedSpeed(){
        return decreasedSpeed;
    }
    
    /**
     * 
     * @return Das Material der Bombe
     */
    public Material getMaterial(){
        return mat;
    }
    
    /**
     * 
     * @return Der Weg der Bombe
     */
    public Way getWay(){
        return way;
    }
        
}
