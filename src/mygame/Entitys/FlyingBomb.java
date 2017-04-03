package mygame.Entitys;

import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import mygame.Main;

/**
 * Eine Bombe. Erstellt eine Bombe und kontrolliert diese. {@link Entity}
 * @author Florian Wenk
 */
public class FlyingBomb extends Bomb{
    
    private double height;
    /**
     * Erstellt die Bombe. Erstellt den Spatial der Bombe und setzt den Weg.
     * @param level 
     */
    public FlyingBomb (Integer level){
        super(level);
        super.setLocation(getWay().getStartPoint().setY(10));
        this.getSpatial().setLocalTranslation(getWay().getStartPoint().setY(10));
        setMoney(10);
        setHeight(20);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void action(float tpf) {
        if(this.getLocation().add(0, -this.getLocation().getY(), 0).distance(this.getSpatial().getLocalTranslation().add(0, -this.getSpatial().getLocalTranslation().getY(), 0)) > 3){
            Vector3f v = new Vector3f(this.getLocation().subtract(this.getSpatial().getLocalTranslation().add(0, -this.getSpatial().getLocalTranslation().getY(), 0)));
            v.divideLocal(10*v.length());
            v.multLocal(5*tpf);
            v.multLocal(this.getSpeed());
            Vector3f w = new Vector3f(0, 0, 0);
            Ray rayDown = new Ray(this.getSpatial().getLocalTranslation(), new Vector3f(0, -1, 0));
            CollisionResults results = new CollisionResults();
            Main.app.getWorld().getScene().collideWith(rayDown, results);
            if(results.size() > 0 && results.getClosestCollision().getContactPoint().distance(this.getSpatial().getLocalTranslation()) != getHeight()+1){
                w = new Vector3f(0, (int) getHeight() - results.getClosestCollision().getContactPoint().distance(this.getSpatial().getLocalTranslation()), 0);
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
                    w = new Vector3f(0, results.getClosestCollision().getContactPoint().distance(this.getSpatial().getLocalTranslation()) + (int) getHeight(), 0);
                    this.getSpatial().move(w);
                }
            }
            this.getSpatial().move(v);
        } else {
            try{
                moveTo(getWay().getNextCorner());
            }catch (Exception e) {}
        }
        if(Main.app.getCamera().getLocation().subtract(this.getSpatial().getLocalTranslation()).length() <= 5 && this.isLiving() && Main.getWorld().getPlayer().isLiving()){
                this.makeDamage(Main.getWorld().getPlayer());
                this.setLiving(false);
                setMoney(0);
        }
        if(Main.getWorld().getBeacon().getSpatial().getLocalTranslation().subtract(this.getSpatial().getLocalTranslation()).length() <= getHeight()*2 && isLiving()){
            this.setHeight(this.getHeight()-0.5);
        }
        if(Main.getWorld().getBeacon().getSpatial().getLocalTranslation().subtract(this.getSpatial().getLocalTranslation()).length() <= 5 && isLiving()){
                this.makeDamage(Main.getWorld().getBeacon());
                this.setLiving(false);
                setMoney(0);
        }
        if(!this.isLiving()){
//            Main.getBulletAppState().getPhysicsSpace().remove(bombC);
            Main.app.getWorld().getPlayer().increaseMoney(super.getMoney());
        }
    }
    
    /**
     * Lässt die Bombe zu einem bestimmten Ort wandern. Sorgt dafür dass die Bombe sich langsam zum Zielort bewegt.
     * @param location Der Ort zu dem die Bombe gehen soll.
     */
    private void moveTo(Vector3f location){
        move(location.subtract(this.getLocation()));
    }
    
    protected void setHeight(double height){
        this.height = height;
    }
    
    protected double getHeight() {
        return height;
    }
}
