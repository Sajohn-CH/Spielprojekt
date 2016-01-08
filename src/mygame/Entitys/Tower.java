/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.Entitys;

import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import mygame.Main;


/**
 * Der grundlegende Turm. Stellt die Grundprinzipien eines Turmes. Wird zum weitervererben gebraucht.
 * @author Florian Wenk
 */
public class Tower extends Entity{
    
    private int range;
    private int price;
    private double shotsPerSecond;
    private long shot;
    private long died = 0;
    private ParticleEmitter flame;
    private int totalPaidMoney = 0;
    
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
        return this.getMaxHealth();
    }
    
    /**
     * Der Turm wird upgegradet. Die Werte des neuen Levels werden gesetzt.
     */
    public void upgrade() {
        
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
        mat.setTexture("Texture", Main.app.getAssetManager().loadTexture("Effects/Explosion/flame.png"));
        mat.setBoolean("PointSprite", true);
        flame.setMaterial(mat);
        flame.setLocalTranslation(this.getLocation());
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
        Main.app.getWorld().getPlayer().increaseMoney((int) (totalPaidMoney * 0.75));
        Main.app.getWorld().removeTower(this);
    }
    
    /**
     * Erhöht das bisher für diesen Turm gezahlte Geld. Das Geld, das für die Errichtung und Upgrades dieses Turms gezahlt wurde.
     * @param money Wieviel Geld zusätzlich in diesen Turm gezahlt wurde.
     */
    public void increaseTotalPaidMoney(int money){
        this.totalPaidMoney += money;
    }
}
