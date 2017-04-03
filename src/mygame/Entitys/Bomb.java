package mygame.Entitys;

import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
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
    
    private Material mat;       //Das Material der Bombe
    private ColorRGBA[] colors; //Die Farben, die die Bombe annehmen kann(um das Level anzuzeigen)
    private Way way;            //Der Weg der Bombe
    private int money;          //Wieviel Geld der Spieler beim zerstören einer Schicht bekommt
    private int decreasedSpeed; //Um wieviel die Geschwindigkeit verringert wurde
    private Node n;             //Enthält alle Graphischen Elemente der Bombe
    protected Float multiplier;
    private boolean isNormal;
    
    /**
     * Erstellt die Bombe. Erstellt den Spatial der Bombe und setzt den Weg.
     * @param level 
     */
    public Bomb (Integer level){
        colors  = new ColorRGBA[]{ColorRGBA.Blue, ColorRGBA.Cyan, ColorRGBA.Green, ColorRGBA.Magenta, ColorRGBA.Red, ColorRGBA.Orange, ColorRGBA.Yellow, ColorRGBA.Green, ColorRGBA.Pink, ColorRGBA.Brown};
        this.setLiving(true);
        this.setNormal(true);
        multiplier = Main.getWorld().getBombMultiplier();
        
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
        this.getSpatial().setLocalTranslation(way.getStartPoint().setY(2));
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
        if(this.getLocation().add(0, -this.getLocation().getY(), 0).distance(this.getSpatial().getLocalTranslation().add(0, -this.getSpatial().getLocalTranslation().getY(), 0)) > 1){
            Vector3f v = new Vector3f(this.getLocation().subtract(this.getSpatial().getLocalTranslation().add(0, -this.getSpatial().getLocalTranslation().getY(), 0)));
            v.divideLocal(10*v.length());
            v.multLocal(5*tpf);
            v.multLocal(this.getSpeed());
            Vector3f w = new Vector3f(0, 0, 0);
            Ray rayDown = new Ray(this.getSpatial().getLocalTranslation(), new Vector3f(0, -1, 0));
            CollisionResults results = new CollisionResults();
            Main.app.getWorld().getScene().collideWith(rayDown, results);
            if(results.size() > 0 && results.getClosestCollision().getContactPoint().distance(this.getSpatial().getLocalTranslation()) != 2){
                w = new Vector3f(0, 2 - results.getClosestCollision().getContactPoint().distance(this.getSpatial().getLocalTranslation()), 0);
                this.getSpatial().move(w);
                if(w.y > 0){
                    if(w.length() > v.length()*.9){
                        v.multLocal(.1f);
                    } else {
                        v = v.normalize().mult(v.length()-w.length());
                    }
                } else {
                    v = v.normalize().mult(v.length()+.25f*w.length());
                }
            } else if (results.size() == 0){
                Ray rayUp = new Ray(this.getSpatial().getLocalTranslation(), new Vector3f(0, 1, 0));
                Main.app.getWorld().getScene().collideWith(rayUp, results);
                if(results.size() > 0){
                    w = new Vector3f(0, results.getClosestCollision().getContactPoint().distance(this.getSpatial().getLocalTranslation()) + 2, 0);
                    this.getSpatial().move(w);
                }
            }
            this.getSpatial().move(v);
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
            Main.getWorld().getPlayer().increaseMoney(money*2);
        } else {
            setLevel(getLevel()-1);
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void setLevel(int newLevel) {
        //setzt Leben als Funktion mit level
        this.setHealth(50+(int)(newLevel*50*multiplier));
        this.setDamage(10+(int)(newLevel*10*multiplier));
        this.setSpeed(50-(int)(newLevel*2*multiplier)+Main.app.getGame().getWave()*2-decreasedSpeed);
        
        mat.setColor("Color", colors[(newLevel-1)%colors.length]);
        super.setLevel(newLevel);
    }
     /**
      * Verlangsamt die Bombe. Verringert das Tempo der Bombe.
      * @param speed Die Anzahl der Tempopunkte, die abezogen werden sollen.
      */
    public void decreaseSpeed (int speed){
        this.decreasedSpeed += speed;
        if(decreasedSpeed >= (50-this.getLevel()*2+Main.app.getGame().getWave()*2)-10-getLevel()*2){
            decreasedSpeed = (50-this.getLevel()*2+Main.app.getGame().getWave()*2)-10-getLevel()*2;
        }
        this.setSpeed((50-this.getLevel()*2+Main.app.getGame().getWave()*2) - decreasedSpeed);
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
    
    /**
     * Gibt zurück zur wievielten Ecke sie unterwegs ist.
     * @return Wievielte Ecke
     */
    public int getCornerIndex(){
        return way.getPositionID();
    }
    
    /**
     * Setzt zu welcher Ecke die Bombe geht.
     * @param cornerIndex Wievielte Ecke
     */
    public void gotoCorner(int cornerIndex){
        way.setNextCorner(cornerIndex);
    }
    
    /**
     * Gibt die Distanz zur nächsten Ecke zurück
     * @return Distanz
     */
    public double getDistanceToNextCorner(){
        return this.getSpatial().getLocalTranslation().subtract(way.getThisCorner()).length();
    }
    
    /**
     * Gibt zurück, ob die Bombe nun "normal" ist.
     * @return 
     */
    public boolean isNormal(){
        return isNormal;
    }
    
    public void setNormal(boolean isNormal){
        this.isNormal = isNormal;
        setMoney(5);
    }
    
    protected int getMoney(){
        return money;
    }
    
    protected void setMoney(int money){
        this.money = money;
    }
}
