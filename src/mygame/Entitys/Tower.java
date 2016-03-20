package mygame.Entitys;

import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import mygame.Main;


/**
 * Der grundlegende Turm. Stellt die Grundprinzipien eines Turmes. Wird zum weitervererben gebraucht.
 * @author Florian Wenk
 */
public class Tower extends Entity{
    
    private int range;                  //Reichweite
    private int price;                  //Preis zum bauen
    private double shotsPerSecond;      //Schüsse pro Sekunde
    private long shot;                  //Wann zuletzt geschossen
    private long died;              //Wann gestorben(Wird für Effekt benötigt, damit dieser noch genügend lange angezeigt wird)
    private ParticleEmitter flame;      //Effekt bei Zerstörung
    private int totalPaidMoney;     //Das gesamte dafür gezahlte Geld
    private String name;       //Der Name des Turmes
    private boolean lowHealthSignIsVisible; //Zeichen, dass der Turm wenig Leben hat ist aktiv.
    private Node n;
    private Material numberMat;
    private Vector3f up;
    protected Float multiplier;
    
    private boolean shootAtNearestBomb;
    private boolean shootAtStrongestBomb;
    private boolean shootAtWeakestBomb;
    private boolean shootAtFurthestBomb;
    
    private boolean shootAtAllBombs;
    private Class<? extends Bomb> bombsClass;
    
    /**
     * Konstruktor. Initialisiert alle Werte.
     */
    public Tower() {  
        died = 0;
        totalPaidMoney = 0;
        name = "Turm";
        lowHealthSignIsVisible = false;
        n = new Node(name);
        this.setSpatial(n);
        numberMat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        numberMat.setColor("Color", ColorRGBA.Gray);
        up = new Vector3f(0, 1, 0);
        multiplier = Main.getWorld().getTowerMultiplier();
        
        shootAtNearestBomb = true;
        shootAtStrongestBomb = false;
        shootAtWeakestBomb = false;
        shootAtFurthestBomb = false;
        
        shootAtAllBombs = true;
        bombsClass = Bomb.class;
    }
      
    /**
     * Gibt die Reichweite des Turmes zurück.
     * @return Reichweite
     */
    public int getRange(){
        return range;
    }
    
    /**
     * Setzt die Reichweite des Turmes.
     * @param range Neue Reichweite
     */
    public void setRange(int range){
        this.range = range;
    }
    
    /**
     * Fügt einem Objekt Schaden zu.
     * @param e Objekt dem Schaden hinzugefügt wird.
     */
    public void makeDamage(Entity e){
        e.increaseHealth(-this.getDamage());
    }
    
    /**
     * Gibt Preis des Turmes zurück.
     * @return Preis
     */
    public int getPrice() {
        return price;
    }

    /**
     * Setzt Preis des Turmes.
     * @param price Neuer Preis
     */
    public void setPrice(int price) {
        this.price = price;
    }
    
    /**
     * Gibt Anzahl Schüsse pro Sekunde zurück.
     * @return Schüsse pro Sekunde.
     */
    public double getShotsPerSecond(){
        return shotsPerSecond;
    }
    
    /**
     * Setzt Schüsse pro Sekunde.
     * @param shotsPerSecond Schüsse pro Sekunde
     */
    public void setShotsPerSecond(double shotsPerSecond){
        this.shotsPerSecond = shotsPerSecond;
    }
    
    /**
     * Gibt zurück, ob der Turm Schiessen kann. Ob die Zeit zwischen zwei Schüssen verstrichen ist.
     * @return Ob der Turm bereit zum Schiessen ist.
     */
    public boolean canShoot(){
        if(System.currentTimeMillis() - shot >= 1000/shotsPerSecond){
            return true;
        }
        return false;
    }
    
    /**
     * Setzt die Zeit des letzten Schusses auf jetzt.
     */
    public void shot(){
        this.shot = System.currentTimeMillis();
    }
    
    /**
     * Gibt die Zeit des letzten Schusses zurück.
     * @return Zeit des letzten Schusses.
     */
    public long getLastShot(){
        return shot;
    }
    
    /**
     * Upgradet den Turm auf das nächste Level, dafür wird die Methode {@link SimpleTower#setLevel(int)} aufgerufen. Es wird ein Popup zum Bestätigen aufgerufen. Vor dem Upgraden wird überpüft, ob der Spieler dafür genug Geld hat. 
     * Wenn dies der Fall ist wird ihm dies abgezogen, sonst wird kein Update ausgeführt.
     */
    public void increaseLevel() {
        
    }
    
    /**
     * Berechnet die Reichweite, die der Turm bei einem bestimmten Level hätte.
     * @param newLevel Level
     * @return Reichweite bei diesem Level.
     */
    public int getNewRange(int newLevel) {
        return 10+newLevel*5;
    }
    
    /**
     * Berechnet den Schaden, den der Turm bei einem bestimmten Level zufügen würde.
     * @param newLevel Level
     * @return Schaden bei diesem Level.
     */
    public int getNewDamage(int newLevel) {
        return 25+newLevel*25;
    }
    
    /**
     * Berechnet das Leben, das der Turm bei einem bestimmten Level hätte.
     * @param newLevel Level
     * @return Leben bei diesem Level.
     */
    public int getNewHealth(int newLevel) {
        return 50+newLevel*10;
    }
    
    /**
     * Berechnet die Schüsse pro Sekunde, die der Turm bei einem bestimmten Level hätte.
     * @param newLevel Level
     * @return Schüsse pro Sekunde bei diesem Level.
     */
    public double getNewSPS(int newLevel) {
        return (newLevel/2)+1;
    }
    
    /**
     * Berechnet den Upradepreis auf das nächste Level.
     * @return Upgradepreis.
     */
    public int getUpgradePrice() {
        return this.getNewHealth(this.getLevel());
    }
    
    /**
     * Der Turm wird upgegradet. Die Werte des neuen Levels werden gesetzt.
     */
    public void upgrade() {
        
    }
    
    /**
     * Der Turm wird automatisch soweit upgegradet, bis er das maximale Level erreicht hat oder der Spieler nicht mehr genügend Geld hat.
     */
    public void upgradeToMax(){
        int lvl = this.getLevel();
        while(Main.app.getWorld().getPlayer().getMoney() >= this.getUpgradePrice() && this.getLevel() < 30){
            this.upgrade();
        }
        if(this.getLevel() == lvl){
            Main.app.getWorld().getPlayer().playAudioNotEnoughMoney();
        }
    }
    
    /**
     * Der Turm wird kollidierbar gemacht, damit man nicht hindurchlaufen kann.
     */
    public void setCollidable(){
        
    }
    
    /**
     * Der Turm wurde zerstört. Den Turm entfernen und einen Feuereffekt für einen Moment anzeigen.
     */
    public void died(){
        died = System.currentTimeMillis();
                
        flame = new ParticleEmitter("Flame", Type.Point, 32);
        flame.setSelectRandomImage(true);
        flame.setStartColor(new ColorRGBA(1f, 0.4f, 0.05f, (float) (1f)));
        flame.setEndColor(new ColorRGBA(.4f, .22f, .12f, 0f));
        flame.setStartSize(1.3f);
        flame.setEndSize(2f);
        flame.setShape(new EmitterSphereShape(Vector3f.ZERO, 1f));
        flame.setParticlesPerSec(25);
        flame.setGravity(0, -5, 0);
        flame.setLowLife(.4f);
        flame.setHighLife(.5f);
        flame.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 7, 0));
        flame.getParticleInfluencer().setVelocityVariation(1f);
        flame.setImagesX(2);
        flame.setImagesY(2);
        Material mat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture("Texture", Main.app.getAssetManager().loadTexture("Effects/flame.png"));
        mat.setBoolean("PointSprite", true);
        flame.setMaterial(mat);
        flame.setLocalTranslation(this.getSpatial().getLocalTranslation());
        flame.emitAllParticles();
        Main.app.getRootNode().attachChild(flame);
        this.getSpatial().removeFromParent();
    }
    
    /**
     * Gibt zurück, ob seit ddr Zerstörung des Turmes genügend Zeit vegangen ist, damit der Effekt entfernt werden kann. Damit wird der ganze Turm entfernt.
     * @return Ob Ob der Turm entfernt werden kann.
     */
    public boolean canRemove(){
        if (System.currentTimeMillis() - died >= 10000){
            return true;
        }
        return false;
    }
    
    /**
     * Den Feuereffekt entfernen.
     */
    public void removeFireEffect(){
        flame.setParticlesPerSec(0);
    }
    
    /**
     * Gibt zurück, ob der Turm zerstört wurde.
     * @return Ob der Turm zerstört wurde.
     */
    public boolean isDead(){
        if(died != 0){
            return true;
        }
        return false;
    }
    
    /**
     * Den Turm entfernen. Die Methode wird aufgerufen, wenn der Spieler den Turm entfernt. Sie entfernt den Turm und zahlt einen Teil des bezahlten Geldes zurück.
     */
    public void remove(){
        Main.getWorld().getPlayer().increaseMoney((int) (totalPaidMoney * 0.75));
        Main.getWorld().getPlayer().playAudioEarnMoney();
        Main.getWorld().removeTower(this);
    }
    
    /**
     * Erhöht das bisher für diesen Turm gezahlte Geld. Das Geld, das für die Errichtung und Upgrades dieses Turms gezahlt wurde.
     * @param money Wieviel Geld zusätzlich in diesen Turm gezahlt wurde.
     */
    public void increaseTotalPaidMoney(int money){
        this.totalPaidMoney += money;
    }
    
    /**
     * Gibt den Turmnamen zurück.
     * @return Turmname
     */
    public String getName(){
        return this.name;
    }
    
    /**
     * Setzt den Turmnamen.
     * @param name Turmname
     */
    public void setName(String name){
        this.name = name;
    }    
    
    /**
     * Gibt die Schwächste Bombe der Klasse in Reichweite zurück.
     * @param bombsClass Klasse der Bomben
     * @param allBombs Ob alle Bomben
     * @return schwächste Bombe
     */
    public Bomb getWeakestBombInRange(Class<? extends Bomb> bombsClass, boolean shootAtAllBombs){
        if(shootAtAllBombs){
            ArrayList<Bomb> allBombs = Main.app.getWorld().getAllBombsInRange(this.getSpatial().getLocalTranslation(), range);
            if(allBombs == null){
                return null;
            }
            if(!allBombs.isEmpty()){
                Bomb weakest = allBombs.get(0);
                for(int i = 0; i < allBombs.size(); i ++){
                    if(allBombs.get(i).getLevel() < weakest.getLevel() || (allBombs.get(i).getLevel() == weakest.getLevel() && allBombs.get(i).getSpatial().getLocalTranslation().subtract(this.getSpatial().getLocalTranslation()).length() < weakest.getSpatial().getLocalTranslation().subtract(this.getSpatial().getLocalTranslation()).length())){
                        weakest = allBombs.get(i);
                    }
                }
                return weakest;
            }
        } else {
            ArrayList<Bomb> allBombs = Main.app.getWorld().getAllClassBombsInRange(this.getSpatial().getLocalTranslation(), range, bombsClass);
            if(allBombs == null){
                return null;
            }
            if(!allBombs.isEmpty()){
                Bomb weakest = bombsClass.cast(allBombs.get(0));
                for(int i = 0; i < allBombs.size(); i ++){
                    if(allBombs.get(i).getLevel() < weakest.getLevel() || (allBombs.get(i).getLevel() == weakest.getLevel() && allBombs.get(i).getSpatial().getLocalTranslation().subtract(this.getSpatial().getLocalTranslation()).length() < weakest.getSpatial().getLocalTranslation().subtract(this.getSpatial().getLocalTranslation()).length())){
                        weakest = allBombs.get(i);
                    }
                }
                return weakest;
            }
        }
        return null;
    }
    
    /**
     * Gibt die stärkste Bombe in Reichweite zurück.
     * @param shootingBombs Ob nur shootingBombs
     * @param normalBomb Ob nur normale Bomben
     * @return stärkste Bombe
     */
    public Bomb getStrongestBombInRange(Class <? extends Bomb> bombsClass, boolean shootAtAllBombs){
        if(shootAtAllBombs){
            ArrayList<Bomb> allBombs = Main.app.getWorld().getAllBombsInRange(this.getSpatial().getLocalTranslation(), range);
            if(allBombs == null){
                return null;
            }
            if(!allBombs.isEmpty()){
                Bomb strongest = allBombs.get(0);
                for(int i = 0; i < allBombs.size(); i ++){
                    if(allBombs.get(i).getLevel() > strongest.getLevel() || (allBombs.get(i).getLevel() == strongest.getLevel() && allBombs.get(i).getSpatial().getLocalTranslation().subtract(this.getSpatial().getLocalTranslation()).length() < strongest.getSpatial().getLocalTranslation().subtract(this.getSpatial().getLocalTranslation()).length())){
                        strongest = allBombs.get(i);
                    }
                }
                return strongest;
            }
        } else {
            ArrayList<Bomb> allBombs = Main.app.getWorld().getAllClassBombsInRange(this.getSpatial().getLocalTranslation(), range, bombsClass);
            if(allBombs == null){
                return null;
            }
            if(!allBombs.isEmpty()){
                Bomb strongest = bombsClass.cast(allBombs.get(0));
                for(int i = 0; i < allBombs.size(); i ++){
                    if(allBombs.get(i).getLevel() > strongest.getLevel() || (allBombs.get(i).getLevel() == strongest.getLevel() && allBombs.get(i).getSpatial().getLocalTranslation().subtract(this.getSpatial().getLocalTranslation()).length() < strongest.getSpatial().getLocalTranslation().subtract(this.getSpatial().getLocalTranslation()).length())){
                        strongest = allBombs.get(i);
                    }
                }
                return strongest;
            }
        }
        return null;
    }
    
    /**
     * Gibt die weiteste Bombe in Reichweite zurück.
     * @param shootingBomb Ob nur ShootingBomb
     * @param normalBomb Ob nur normale Bomben
     * @return Weiteste Bombe
     */
    public Bomb getFurthestBombInRange(Class<? extends Bomb> bombsClass, boolean shootAtAllBombs){
        if(shootAtAllBombs){
            ArrayList<Bomb> allBombs = Main.app.getWorld().getAllBombsInRange(this.getSpatial().getLocalTranslation(), range);
            if(allBombs == null){
                return null;
            }
            if(!allBombs.isEmpty()){
                Bomb furthest = allBombs.get(0);
                for(int i = 0; i < allBombs.size(); i++){
                    if(allBombs.get(i).getCornerIndex() > furthest.getCornerIndex() || (allBombs.get(i).getCornerIndex() == furthest.getCornerIndex() && allBombs.get(i).getDistanceToNextCorner() < furthest.getDistanceToNextCorner())){
                        furthest = allBombs.get(i);
                    }
                }
                return furthest;
            }
        } else {
            ArrayList<Bomb> allBombs = Main.app.getWorld().getAllClassBombsInRange(this.getSpatial().getLocalTranslation(), range, bombsClass);
            if(allBombs == null){
                return null;
            }
            if(!allBombs.isEmpty()){
                Bomb furthest = bombsClass.cast(allBombs.get(0));
                for(int i = 0; i < allBombs.size(); i++){
                    if(allBombs.get(i).getCornerIndex() > furthest.getCornerIndex() || (allBombs.get(i).getCornerIndex() == furthest.getCornerIndex() && allBombs.get(i).getDistanceToNextCorner() < furthest.getDistanceToNextCorner())){
                        furthest = allBombs.get(i);
                    }
                }
                return furthest;
            }
        }
        return null;
    }
    
    public Bomb getNearestBombInRange(Class<? extends Bomb> bombsClass, boolean shootAtAllBombs){
        if(shootAtAllBombs){
            return Main.app.getWorld().getNearestBombInRange(this.getSpatial().getLocalTranslation(), range);
        } else {
            return Main.app.getWorld().getNearestClassBombInRange(this.getSpatial().getLocalTranslation(), range, bombsClass);
        }
    }
    
    /**
     * Gibt die Bombe zurück, auf die geschossen werden soll.
     * @return Bombe, die Ziel ist
     */
    public Bomb getBombToShootAt(){
        if(shootAtNearestBomb)
            return getNearestBombInRange(bombsClass, shootAtAllBombs);
        else if (shootAtFurthestBomb)
            return getFurthestBombInRange(bombsClass, shootAtAllBombs);
        else if (shootAtStrongestBomb)
            return getStrongestBombInRange(bombsClass, shootAtAllBombs);
        else if (shootAtWeakestBomb)
            return getWeakestBombInRange(bombsClass, shootAtAllBombs);
        return null;
    }
    
    /**
     * Setzt auf welche Bombe geschossen werden soll. Eine Variable ausser shootingBombs oder normalBomb (höchstens eine von beiden true) muss true sein und sonst keine.
     * @param bombsClass  Auf Welche klasse bombe
     * @param shootAtAllBombs  Ob auf alle Bomben
     * @param nearest Auf die nächste
     * @param furthest Auf die weiteste
     * @param strongest Auf die stärkste
     * @param weakest Auf die schwächste
     */
    public void setShootAt(Class<? extends Bomb> bombsClass, boolean shootAtAllBombs, boolean nearest, boolean furthest, boolean strongest, boolean weakest){
        this.shootAtAllBombs = shootAtAllBombs;
        this.bombsClass = bombsClass;
        if(shootAtAllBombs){
            setShootAtAllBombs();
        }
        this.setShootAt(nearest, furthest, strongest, weakest);
    }
    
    /**
     * Setzt dass auf alle Bomben geschossen werden soll.
     */
    public void setShootAtAllBombs(){
        this.shootAtAllBombs = true;
    }
    
    /**
     * Setzt auf welche Bombe geschossen werden soll. Es muss nur eine Variable true sein.
     * @param nearest Auf die nächste
     * @param furthest Auf die weiteste
     * @param strongest Auf die stärkste
     * @param weakest Auf die schwächste
     */
    public void setShootAt(boolean nearest, boolean furthest, boolean strongest, boolean weakest){
        this.shootAtNearestBomb = nearest;
        this.shootAtFurthestBomb = furthest;
        this.shootAtStrongestBomb = strongest;
        this.shootAtWeakestBomb = weakest;
    }
    
    public void setShootAt(Class<? extends Bomb> bombsClass, boolean shootAtAllBombs){
        this.bombsClass = bombsClass;
        this.shootAtAllBombs = shootAtAllBombs;
    }
    
    public Class<? extends Bomb> getShootAtBombsClass(){
        return this.bombsClass;
    }
    
    public String getShootAtBombsString(){
        return Main.app.getSettings().getLanguageProperty(this.bombsClass.getSimpleName(), this.bombsClass.getSimpleName());
    }
    
    /**
     * Gibt zurück ob der Turm nur auf alle Bomben schiesst.
     * @return Ob auf alle Bomben
     */
    public boolean getShootAtAllBombs(){
        return shootAtAllBombs;
    }
    
    /**
     * Gibt zurück auf was der Turm schiesst. Ob auf nächste, weiteste, stärkste oder schwächste.
     * @return nearest, furthest, strongest oder weakest je nach dem auf was geschossen wird.
     */
    public String getShootAt(){
        if(shootAtNearestBomb){
            return "nearest";
        } else if (shootAtFurthestBomb){
            return "furthest";
        } else if (shootAtStrongestBomb){
            return "strongest";
        } else if (shootAtWeakestBomb){
            return "weakest";
        }
        return null;
    }
    
    public void showLowHealthSign(){
        lowHealthSignIsVisible = true;
        Material mat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        mat.setTransparent(true);
        n.getChild("levelNumber").setMaterial(mat);
    }
    
    public void hideLowHealthSign(){
        lowHealthSignIsVisible = false;
        n.getChild("levelNumber").setMaterial(numberMat);
    }
    
    public boolean lowHealthSignIsVisble(){
        return this.lowHealthSignIsVisible;
    }
    
    public void setLevelNumberSpatial(int level, float height){
        n.detachChildNamed("levelNumber");
        // Selbsterstellte Modelle von Florian Wenk
        Spatial s = Main.app.getAssetManager().loadModel("Objects/RomanNumbers/" + level + ".j3o");
        s.setName("levelNumber");
        s.setMaterial(numberMat);
        s.setLocalTranslation(0, height, 0);
        n.attachChild(s);
    }
    
    public Node getNode(){
        return this.n;
    }
    
    public void setNode(Node n){
        this.n = n;
    }
    
    /**
     * Setzt den Vektor der nach oben zeigt.
     * @param up  Vektor in die die "Spitze" des Turms zeigen soll
     */
    public void setUp(Vector3f up){
        this.up = up;
        this.getSpatial().rotateUpTo(up);
    }
    
    /**
     * Gibt den Vektor zurück der nach oben zeigt.
     * @return Vektor in die die "Spitze" des Turms zeigen soll
     */
    public Vector3f getUp(){
        return this.up;
    }
}
