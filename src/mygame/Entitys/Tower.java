package mygame.Entitys;

import com.jme3.audio.AudioNode;
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
    private AudioNode shootAudio;
    
    private boolean shootAtNearestBomb;
    private boolean shootAtStrongestBomb;
    private boolean shootAtWeakestBomb;
    private boolean shootAtFurthestBomb;
    
    private ArrayList<Class<? extends Bomb>> bombsClass;
    
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
        
        bombsClass = new ArrayList<>();
        bombsClass.add(Bomb.class);
        for(int i = 0; i < Main.app.getGame().getPossibleBombTypes().size(); i++){
            bombsClass.add(Main.app.getGame().getBombType(Main.app.getGame().getPossibleBombTypes().get(i))); 
        }
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
        if(Main.app.getClock().getTime() - shot >= 1000/shotsPerSecond){
            return true;
        }
        return false;
    }
    
    /**
     * Setzt die Zeit des letzten Schusses auf jetzt und spielt den Schusston ab(je nach dem wie angegeben).
     * @param playAudio Ob Schusston abgespielt werden soll.
     */
    public void shot(boolean playAudio){
        this.shot = Main.app.getClock().getTime();
        if(shootAudio != null && playAudio){
            shootAudio.playInstance();
        }
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
        died = Main.app.getClock().getTime();
                
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
        if (Main.app.getClock().getTime() - died >= 10000){
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
    public Bomb getWeakestBombInRange(ArrayList<Class<? extends Bomb>> bombsClass){
        ArrayList<Bomb> allBombs = Main.app.getWorld().getAllClassListBombsInRange(this.getSpatial().getLocalTranslation(), range, bombsClass);
        if(allBombs == null){
            return null;
        }
        if(!allBombs.isEmpty()){
            Bomb weakest = null;
            for(int i = 0; weakest == null; i++){
                try{
                weakest = bombsClass.get(i).cast(allBombs.get(0));
                } catch (Exception e){}
            }
            for(int i = 0; i < allBombs.size(); i ++){
                if(allBombs.get(i).getLevel() < weakest.getLevel() || (allBombs.get(i).getLevel() == weakest.getLevel() && allBombs.get(i).getSpatial().getLocalTranslation().subtract(this.getSpatial().getLocalTranslation()).length() < weakest.getSpatial().getLocalTranslation().subtract(this.getSpatial().getLocalTranslation()).length())){
                    weakest = allBombs.get(i);
                }
            }
            return weakest;
        }
        return null;
    }
    
    /**
     * Gibt die stärkste Bombe in Reichweite zurück.
     * @param shootingBombs Ob nur shootingBombs
     * @param normalBomb Ob nur normale Bomben
     * @return stärkste Bombe
     */
    public Bomb getStrongestBombInRange(ArrayList<Class <? extends Bomb>> bombsClass){
        ArrayList<Bomb> allBombs = Main.app.getWorld().getAllClassListBombsInRange(this.getSpatial().getLocalTranslation(), range, bombsClass);
        if(allBombs == null){
            return null;
        }
        if(!allBombs.isEmpty()){
            Bomb strongest = null;
            for(int i = 0; strongest == null; i++){
                try{
                strongest = bombsClass.get(i).cast(allBombs.get(0));
                } catch (Exception e){}
            }
            for(int i = 0; i < allBombs.size(); i ++){
                if(allBombs.get(i).getLevel() > strongest.getLevel() || (allBombs.get(i).getLevel() == strongest.getLevel() && allBombs.get(i).getSpatial().getLocalTranslation().subtract(this.getSpatial().getLocalTranslation()).length() < strongest.getSpatial().getLocalTranslation().subtract(this.getSpatial().getLocalTranslation()).length())){
                    strongest = allBombs.get(i);
                }
            }
            return strongest;
        }
        return null;
    }
    
    /**
     * Gibt die weiteste Bombe in Reichweite zurück.
     * @param shootingBomb Ob nur ShootingBomb
     * @param normalBomb Ob nur normale Bomben
     * @return Weiteste Bombe
     */
    public Bomb getFurthestBombInRange(ArrayList<Class<? extends Bomb>> bombsClass){
        ArrayList<Bomb> allBombs = Main.app.getWorld().getAllClassListBombsInRange(this.getSpatial().getLocalTranslation(), range, bombsClass);
        if(allBombs == null){
            return null;
        }
        if(!allBombs.isEmpty()){
            Bomb furthest = null;
            for(int i = 0; furthest == null; i++){
                try{
                furthest = bombsClass.get(i).cast(allBombs.get(0));
                } catch (Exception e){}
            }
            for(int i = 0; i < allBombs.size(); i++){
                if(allBombs.get(i).getCornerIndex() > furthest.getCornerIndex() || (allBombs.get(i).getCornerIndex() == furthest.getCornerIndex() && allBombs.get(i).getDistanceToNextCorner() < furthest.getDistanceToNextCorner())){
                    furthest = allBombs.get(i);
                }
            }
            return furthest;
        }
        return null;
    }
    
    public Bomb getNearestBombInRange(ArrayList<Class<? extends Bomb>> bombsClass){
        return Main.app.getWorld().getNearestClassBombInRange(this.getSpatial().getLocalTranslation(), range, bombsClass);
    }
    
    /**
     * Gibt die Bombe zurück, auf die geschossen werden soll.
     * @return Bombe, die Ziel ist
     */
    public Bomb getBombToShootAt(){
        if(shootAtNearestBomb)
            return getNearestBombInRange(bombsClass);
        else if (shootAtFurthestBomb)
            return getFurthestBombInRange(bombsClass);
        else if (shootAtStrongestBomb)
            return getStrongestBombInRange(bombsClass);
        else if (shootAtWeakestBomb)
            return getWeakestBombInRange(bombsClass);
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
    public void setShootAt(ArrayList<Class<? extends Bomb>> bombsClass, boolean nearest, boolean furthest, boolean strongest, boolean weakest){
        this.bombsClass = bombsClass;
        this.setShootAt(nearest, furthest, strongest, weakest);
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
    
    public void setShootAt(ArrayList<Class<? extends Bomb>> bombsClass){
        this.bombsClass = bombsClass;
    }
    
    public ArrayList<Class<? extends Bomb>> getShootAtBombsClass(){
        return this.bombsClass;
    }
    
    public ArrayList<String> getShootAtBombsString(){
        ArrayList<String> bombStrings = new ArrayList<String>();
        for(int i = 0; i < bombsClass.size(); i++){
            bombStrings.add(Main.app.getSettings().getLanguageProperty(this.bombsClass.get(i).getSimpleName(), this.bombsClass.get(i).getSimpleName()));
        }
        return bombStrings;
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
    
    /**
     * Gibt zurück ob der Turm auf eine bestimmte Bombenart schiessen kann.
     * @param bombsClass Bombenart
     * @return Ob der Turm auf diese Bombenart schiessen kann
     */
    public boolean canShootAtBombsClass(Class <? extends Bomb> bombsClass){
        int money = Main.app.getWorld().getPlayer().getMoney();
        try{
            makeDamage(bombsClass.getConstructor(Integer.class).newInstance(1));
        } catch (Exception e) {
            Main.app.getWorld().getPlayer().setMoney(money);
            return false;
        }
        Main.app.getWorld().getPlayer().setMoney(money);
        return true;
    }
    
    /**
     * Gibt zurück ob der Turm auf alle Bombenarten schiessen kann
     * @return Ob der Turm auf alle Bombenarten schiessen kann.
     */
    public boolean canShootAtAllBombs(){
        ArrayList<String> bombTypes = Main.app.getGame().getPossibleBombTypes();
        for(int i = 1; i < bombTypes.size(); i++){
            if(!canShootAtBombsClass(Main.app.getGame().getBombType(bombTypes.get(i)))){
                return false;
            }
        }
        return true;
    }
    
    public void setShootAudio(String path){
        shootAudio = new AudioNode(Main.app.getAssetManager(), path, false);
        shootAudio.setPositional(true);
        shootAudio.setLooping(false);
        shootAudio.setRefDistance(2f);
        shootAudio.setLocalTranslation(this.getSpatial().getLocalTranslation());
        shootAudio.setVolume((float) Main.app.getSettings().getVolumeEffectsEffective());
        Main.app.getRootNode().attachChild(shootAudio);
    }
    
    public AudioNode getShootAudio(){
        return shootAudio;
    }
    
    public void setShootAudioVolume(float volume){
        shootAudio.setVolume(volume);
    }
}
