package mygame.Entitys;

import com.jme3.audio.AudioNode;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.InputListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Line;
import mygame.HudScreenState;
import mygame.Main;

/**
 * Der Spieler. Erstellt und konntrolliert den Spieler und seine Aktionen.
 * @author Florian Wenk und Samuel Martin
 */
public class Player extends Entity{
    
    private CharacterControl player;        //Sorgt für Physik des Spielers
    private Vector3f walkDirection;         //Laufrichtung des Spielers
    private boolean left, right, up, down;  //Ob die jeweilige Taste fürs Laufen gedrückt ist
    private Vector3f camDir;                //Richtung der Kamera
    private Vector3f camLeft;               //"Links" in richtung der Kamera
    private InputListener inputListener;    //Wird für die Eingaben des Spielers benötigt
    private Geometry line;                  //Schusslinie
    private Geometry healingLine;           //Heilungslinie
    private long shot;                      //Wann zuletzt geschossen wurde
    private boolean isShooting;             //Ob der Spieler am schiessen ist
    private int money;                      //Das Geld des Spielers
    private boolean isHealing;              //Ob der Spieler am heilen ist
    private boolean hasHealed;              //Ob der Spieler geheilt hat
    
    private double shotsPerSecond;          //Wieoft der Spieler in der Sekunde schiesst
    private int range;                      //Reichweite des Spielers
    
    private AudioNode shootAudio;           //Ton, der beim Schiessen abgespielt wird
    private AudioNode walkAudio;            //Ton, der beim Laufen abgespielt wird
    private AudioNode buyAudio;             //Ton, der beim Kaufen abgespielt wird
    private AudioNode notEnoughMoneyAudio;  //Ton, der bei zu wenig Geld zum kaufen abgespielt wird
    private AudioNode earnMoneyAudio;       //Ton, der beim verdienen von Geld abgespielt wird
    
    private int key_left;                   //Keycode der Links-Taste
    private int key_right;                  //Keycode der Rechts-Taste
    private int key_up;                     //Keycode der Oben-Taste
    private int key_down;                   //Keycode der Unten-Taste
    private int key_jump;                   //Keycode der Springen-Taste
    
    /**
     * Initialisiert den Spieler. Setzt Grundattribute des Spielers, erstellt die Waffe und Schusslinie und lädt die Töne.
     * @param inputListener Für Tasteneingaben benötigt
     */
    public Player(InputListener inputListener){
        left = false;
        right = false;
        up = false;
        down = false;
        walkDirection = new Vector3f();
        camDir = new Vector3f();
        camLeft = new Vector3f();
        isShooting = false;
        isHealing = false;
        hasHealed = false;
        shotsPerSecond = 50;
        range = 100;
        key_left = KeyInput.KEY_A;
        key_right = KeyInput.KEY_D;
        key_up = KeyInput.KEY_W;
        key_down = KeyInput.KEY_S;
        key_jump = KeyInput.KEY_SPACE;
        
        money = 250;
        this.inputListener = inputListener;
        //Ob Tastaturbelgung gesetzt werden soll. Dies ist nur beim ersten Starten des Spiels notwendig, nicht bei Neustarten
        setUpKeys();
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 6f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);
        player.setJumpSpeed(20);
        player.setFallSpeed(90);
        player.setGravity(30);
        player.setPhysicsLocation(new Vector3f(0,10,0));
        this.setLiving(true);
        this.setDamage(2);
        setHealth(this.maxHealth);
        this.setSpeed(50);
        Main.app.getBulletAppState().getPhysicsSpace().add(player);
        
        line = new Geometry("line", new Line());
        Material mat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        line.setMaterial(mat);
        
        healingLine = new Geometry("line", new Line());
        Material mat1 = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Cyan);
        healingLine.setMaterial(mat1);
        
        //Modell von: http://www.blendswap.com/blends/view/67733 (User: genx473)
        //Beatrbeitet von: Florian Wenk
        this.setSpatial(Main.app.getAssetManager().loadModel("Objects/Gun.j3o").scale(.2f));
        this.getSpatial().setLocalTranslation(Main.app.getCamera().getLocation().add(0, -1.75f, 0).add(Main.app.getCamera().getDirection().normalize().mult(1.5f)));
        PointLight light = new PointLight();
        light.setPosition(new Vector3f(0, 1000, 0));
        light.setRadius(100000);
        this.getSpatial().addLight(light);
        Main.app.getRootNode().attachChild(this.getSpatial());
        
        //Sound von: https://freesound.org/people/cormi/sounds/93979/ (User: cormi)
        //Bearbeitet von: Florian Wenk
        shootAudio = new AudioNode(Main.app.getAssetManager(), "Audio/shootAudio.wav", false);
        shootAudio.setPositional(false);
        shootAudio.setLooping(true);
        shootAudio.setVolume(2);
        Main.app.getRootNode().attachChild(shootAudio);
        
        //Sound von: https://freesound.org/people/Shadowedhunter/sounds/155920/ (User: Shadowedhunter)
        //Bearbeitet von: Florian Wenk
        walkAudio = new AudioNode(Main.app.getAssetManager(), "Audio/walkAudio.wav", false);
        walkAudio.setPositional(false);
        walkAudio.setLooping(true);
        walkAudio.setVolume(.5f);
        Main.app.getRootNode().attachChild(walkAudio);
        
        //Sound von: https://freesound.org/people/jact878787/sounds/323809/ (User: jact878787)
        //Bearbeitet von: Florian Wenk
        buyAudio = new AudioNode(Main.app.getAssetManager(), "Audio/buyAudio.wav", false);
        buyAudio.setPositional(false);
        buyAudio.setLooping(false);
        buyAudio.setVolume(2f);
        Main.app.getRootNode().attachChild(buyAudio);
        
        //Sound von: https://freesound.org/people/clairinski/sounds/184372/ (User: clairinski)
        //Bearbeitet von: Florian Wenk
        notEnoughMoneyAudio = new AudioNode(Main.app.getAssetManager(), "Audio/notEnoughMoneyAudio.wav", false);
        notEnoughMoneyAudio.setPositional(false);
        notEnoughMoneyAudio.setLooping(false);
        notEnoughMoneyAudio.setVolume(2f);
        Main.app.getRootNode().attachChild(notEnoughMoneyAudio);
        
        //Sound von: https://freesound.org/people/severaltimes/sounds/173989/ (User: severaltimes)
        //Bearbeitet von: Florian Wenk
        earnMoneyAudio = new AudioNode(Main.app.getAssetManager(), "Audio/earnMoneyAudio.wav", false);
        earnMoneyAudio.setPositional(false);
        earnMoneyAudio.setLooping(false);
        earnMoneyAudio.setVolume(2f);
        Main.app.getRootNode().attachChild(earnMoneyAudio);
    }
    
    /**
     * Lässt den Spieler zum Anfangsort des Weges schauen(Von wo die Bomben erscheinen).
     */
    public void turn(){
        Main.app.getCamera().lookAt(Main.getWorld().getAllCorners().get(0), new Vector3f(0, 1, 0));
    }
    
    /**
     * Initialisiert die Tasten.
     */
    private void setUpKeys() {
        //Tasten um Spieler zu Steuern
        Main.app.getInputManager().deleteMapping("Left");
        Main.app.getInputManager().addMapping("Left", new KeyTrigger(key_left));
        Main.app.getInputManager().deleteMapping("Right");
        Main.app.getInputManager().addMapping("Right", new KeyTrigger(key_right));
        Main.app.getInputManager().deleteMapping("Up");
        Main.app.getInputManager().addMapping("Up", new KeyTrigger(key_up));
        Main.app.getInputManager().deleteMapping("Down");
        Main.app.getInputManager().addMapping("Down", new KeyTrigger(key_down));
        Main.app.getInputManager().deleteMapping("Jump");
        Main.app.getInputManager().addMapping("Jump", new KeyTrigger(key_jump));
        Main.app.getInputManager().addMapping("Shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        Main.app.getInputManager().addMapping("placeTower", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        Main.app.getInputManager().addListener(this.inputListener, "Left");
        Main.app.getInputManager().addListener(this.inputListener, "Right");
        Main.app.getInputManager().addListener(this.inputListener, "Up");
        Main.app.getInputManager().addListener(this.inputListener, "Down");
        Main.app.getInputManager().addListener(this.inputListener, "Jump");
        Main.app.getInputManager().addListener(this.inputListener, "Shoot");
        Main.app.getInputManager().addListener(this.inputListener, "placeTower");
    }
    
    /**
     * Reagiert auf die Tasteneingaben. {@link Main#onAction(java.lang.String, boolean, float) }
     * @param binding Welche Taste gedrückt wurde.
     * @param isPressed Ob die Taste gedrückt oder losgelassen wurde.
     */
    public void onAction(String binding, boolean isPressed){
         if (binding.equals("Left")) {
             left = isPressed;
         } else if (binding.equals("Right")) {
             right= isPressed;
         } else if (binding.equals("Up")) {
             up = isPressed;
         } else if (binding.equals("Down")) {
             down = isPressed;
         } else if (binding.equals("Shoot") && this.isLiving()) {
             if(isPressed) {
                 isShooting = true;
                 Main.app.getRootNode().attachChild(line);
             }
             if(!isPressed){
                 isShooting = false;
                 line.removeFromParent();
             }
         } else if (binding.equals("placeTower") && this.isLiving()) {
             //if(!isPressed) verhindert, dass die Methode zweimal ausgeführt wird
             if(isPressed && Main.app.getHudState().getSelectedItemNum() == 5) {
                 isHealing = true;
             }
             if(!isPressed) {
                   if(Main.app.getHudState().getSelectedItemNum()==4) {
                       this.upgradeObject();
                   } else if(Main.app.getHudState().getSelectedItemNum() == 5) {
                       isHealing = false;
                       if(hasHealed){
                         playAudioBought();
                         hasHealed = false;
                       }
                   } else {
                       this.placeTower();  
                   }
             }
         } else if (binding.equals("Jump")) {
           if (isPressed) {
               player.jump();
           }
         }
     }

    /**
     * Gibt zurück, ob genügend Zeit vergangen ist, um wieder zu schiessen.
     * @return Ob wieder geschossen werden kann.
     */
    private boolean canShoot(){
        if(System.currentTimeMillis()-shot >= 1000/shotsPerSecond){
            shot = 0;
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void action(float tpf){
          if (!Main.app.getFlyByCamera().isDragToRotate() && !Main.app.getWorld().isPaused() && !player.isEnabled()) {
              player.setEnabled(true);
          }
          camDir.set(Main.app.getCamera().getDirection()).multLocal(0.6f);
          camLeft.set(Main.app.getCamera().getLeft()).multLocal(0.4f);
          //Versucht y-Komponente zu entferen, damit man nicht nach oben/unten gehen kann
          camDir.setY(0);
          camLeft.setY(0);
          walkDirection.set(0, 0, 0);
          if (left) {
              walkDirection.addLocal(camLeft);
          }
          if (right) {
              walkDirection.addLocal(camLeft.negate());
          } 
          if (up) {
              walkDirection.addLocal(camDir);
          }
          if (down) {
              walkDirection.addLocal(camDir.negate());
          }
         walkDirection.normalizeLocal();
         walkDirection.multLocal(this.getSpeed() * tpf);
         if(walkDirection.length() != 0){
             walkAudio.play();
         } else {
             walkAudio.stop();
         }
         player.setWalkDirection(walkDirection);
         Main.app.getCamera().setLocation(player.getPhysicsLocation());
         this.getSpatial().setLocalTranslation(Main.app.getCamera().getLocation().add(Main.app.getCamera().getUp().normalize().mult(-1.75f)).add(Main.app.getCamera().getLeft().normalize().mult(-.75f)).add(Main.app.getCamera().getDirection().normalize().mult(1.9f)));
         this.getSpatial().lookAt(Main.app.getCamera().getDirection().mult(range).add(Main.app.getCamera().getLocation()), new Vector3f(0,1,0));

         if(isHealing) {
             heal();
         } 
         if(isShooting){
             shootAudio.play();
             shoot();
         } else {
             shootAudio.stop();
         }
         if(!isLiving() && (isHealing || isShooting)){
             isHealing = false;
             isShooting = false;
             line.removeFromParent();
         }
         if(!isLiving() && this.getSpatial().hasAncestor(Main.app.getRootNode())){
             this.getSpatial().removeFromParent();
         }
     }

   /**
    * Fügt einem Objekt Schaden zu.
    * @param e Objekt, welchem Schadenzugefügt werden soll.
    */
    private void makeDamage(Entity e){
        if(e.isLiving()){
            e.increaseHealth(-this.getDamage());
        }
    }
    
    /**
     * Schiessen. Generiert die Schusslinie; Schaut wer getroffen wurde und fügt allenfals Schaden zu.
     */
    private void shoot(){
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(this.getSpatial().getLocalTranslation().add(Main.app.getCamera().getUp().normalize().mult(1.3f)), Main.app.getCamera().getLocation().add(Main.app.getCamera().getDirection().normalize().mult(range)).subtract(this.getSpatial().getLocalTranslation().add(Main.app.getCamera().getUp().normalize().mult(1.3f))));
        
        //Schusslinie generieren
        Line l = new Line(this.getSpatial().getLocalTranslation().add(Main.app.getCamera().getUp().normalize().mult(1.3f)), Main.app.getCamera().getLocation().add(Main.app.getCamera().getDirection().normalize().mult(range)));
        line.setMesh(l);
        
        //Prüft, ob eine Bombe getroffen wurde
        Main.getWorld().getBombNode().collideWith(ray, results);
        if (canShoot() && results.size() > 0) {
            CollisionResult closest = results.getClosestCollision();
            l = new Line(this.getSpatial().getLocalTranslation().add(Main.app.getCamera().getUp().normalize().mult(1.3f)), closest.getContactPoint());
            line.setMesh(l);
            if(closest.getContactPoint().add(Main.getWorld().getBombNode().getLocalTranslation()).subtract(Main.app.getCamera().getLocation()).length() <= range){
                makeDamage(Main.getWorld().getBomb(closest.getGeometry().getParent()));
            }
            shot = System.currentTimeMillis();
        }
    }
    
    /**
     * Plaziert einen Turm. Überprüft, wo der Turm gesetzt werden soll, ob dies dort möglich ist (noch kein anderer Turm, nicht beim Beacon, nicht auf dem Weg, nicht zunahe beim Spieler) 
     * und Plaziert den Turm, der unten ausgewählt wurde{@link HudScreenState#getSelectedTower(com.jme3.math.Vector3f) }.
     */
    private void placeTower(){
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(Main.app.getCamera().getLocation(), Main.app.getCamera().getDirection());
        // trifft auf scene
        Main.getWorld().getScene().collideWith(ray, results);
        if (results.size() > 0) {
            // kontrolliert ob kollision mit Beacon
            CollisionResults resultsBeacon = new CollisionResults();
            Main.getWorld().getBeacon().getSpatial().collideWith(ray, resultsBeacon);
            if(resultsBeacon.size() > 0){
                // Zu nahe an beacon -> nicht setzen
                return;
            }
            CollisionResults resultsWay = new CollisionResults();
            Main.getWorld().getWayNode().collideWith(ray, resultsWay);
            if(resultsWay.size() != 0){
                return;
            }
            CollisionResult closest = results.getClosestCollision();
            Vector3f v = closest.getContactPoint();
            v = v.setY(v.getY()+4);
            Tower tower = Main.app.getHudState().getSelectedTower(v);
            //kontrolliert ob Spieler genug Geld hat
            if(getMoney()-tower.getPrice() < 0) {
                //Nicht genug Geld -> Turm wird nicht gesetzt.
                playAudioNotEnoughMoney();
                return;
            }
            v = v.setY(v.getY()-4);
            Tower nearest = Main.getWorld().getNearestTower(v);
            // konntrolliert ob Distanz zum nächsten Turm genügend gross ist
            if(nearest != null && nearest.getLocation().subtract(v).length() < 10){
                // Zu nahe an einem anderen Turm -> Turm wird nicht gesetzt
                return;
            }
            if(v.subtract(Main.app.getCamera().getLocation()).length() < 15){
                return;
            }
            //Zieht Geld 
            increaseMoney(-tower.getPrice());
            playAudioBought();
            // plaziert Turm
            Main.getWorld().addTower(tower);
        }
    }
    
    /**
     * Heilt ein Objekt. Überprüft welches Objekt(Turm, Beacon oder Player), Heilt dieses und zieht Geld ab.
     */
    private void heal() {
        CollisionResults resultsTower = new CollisionResults();
        CollisionResults resultsBeacon = new CollisionResults();
        Ray ray = new Ray(Main.app.getCamera().getLocation(), Main.app.getCamera().getDirection());
        Main.getWorld().getTowerNode().collideWith(ray, resultsTower);
        Main.getWorld().getBeacon().getSpatial().collideWith(ray, resultsBeacon);
        if(resultsTower.size() == 0 && resultsBeacon.size() == 0) {
            //Es wird auf nichts gezeigt -> Spieler heilen
            if(this.getHealth() < this.getMaxHealth() && this.getMoney() > 0) {
                this.increaseMoney(-1);
                this.increaseHealth(1);
                hasHealed = true;
            } else {
                isHealing = false;
            }                
        } else if(resultsTower.size() == 0) {
            if(resultsBeacon.size() != 0){
                Beacon beacon = Main.getWorld().getBeacon();
                    if(beacon.getHealth() < beacon.getMaxHealth() && this.getMoney() > 0) {
                        Line l = new Line(this.getSpatial().getLocalTranslation().add(Main.app.getCamera().getUp().normalize().mult(1.3f)), resultsBeacon.getClosestCollision().getContactPoint());
                        healingLine.setMesh(l);
                        Main.app.getRootNode().attachChild(healingLine);
                        beacon.setHealth(beacon.getHealth()+1);
                        this.increaseMoney(-1);
                        hasHealed = true;
                    } else {
                        isHealing = false;
                        healingLine.removeFromParent();
                    }
            }
        } else {
            //Es gibt min. einen Turm. Es wird der nächste geholt
            Vector3f pointTower = resultsTower.getClosestCollision().getContactPoint();
            Tower nearestTower = Main.app.getWorld().getNearestTower(pointTower);
            if(resultsBeacon.size() > 0) {
                //Es gibt Türme und Beacon mit Kollision -> Hearusfinden welcher näher ist
                Vector3f pointBeacon = resultsBeacon.getClosestCollision().getContactPoint();
                if(nearestTower.getLocation().subtract(pointTower).length() > Main.app.getWorld().getBeacon().getLocation().subtract(pointBeacon).length()) {
                    //Beacon ist näher
                    Beacon beacon = Main.getWorld().getBeacon();
                    if(beacon.getHealth() < beacon.getMaxHealth() && this.getMoney() > 0) {
                        Line l = new Line(this.getSpatial().getLocalTranslation().add(Main.app.getCamera().getUp().normalize().mult(1.3f)), pointBeacon);
                        healingLine.setMesh(l);
                        Main.app.getRootNode().attachChild(healingLine);
                        beacon.setHealth(beacon.getHealth()+1);
                        this.increaseMoney(-1);
                        hasHealed = true;
                    } else {
                        isHealing = false;
                        healingLine.removeFromParent();
                    }
                } else {
                    //ein Turm heilen
                    if(nearestTower.getHealth() < nearestTower.getMaxHealth() && this.getMoney() > 0) {
                        Line l = new Line(this.getSpatial().getLocalTranslation().add(Main.app.getCamera().getUp().normalize().mult(1.3f)), resultsTower.getClosestCollision().getContactPoint());
                        healingLine.setMesh(l);
                        Main.app.getRootNode().attachChild(healingLine);
                        nearestTower.setHealth(nearestTower.getHealth()+1);
                        this.increaseMoney(-1);
                        hasHealed = true;
                    } else {
                        isHealing = false;
                        healingLine.removeFromParent();
                    }
                }
            } else {
                //ein Turm heilen
                if(nearestTower.getHealth() < nearestTower.getMaxHealth() && this.getMoney() > 0) {
                    Line l = new Line(this.getSpatial().getLocalTranslation().add(Main.app.getCamera().getUp().normalize().mult(1.3f)), resultsTower.getClosestCollision().getContactPoint());
                    healingLine.setMesh(l);
                    Main.app.getRootNode().attachChild(healingLine);
                    nearestTower.setHealth(nearestTower.getHealth()+1);
                    this.increaseMoney(-1);
                    hasHealed = true;
                } else {
                    isHealing = false;
                    healingLine.removeFromParent();
                }
            }
        }
    }
    
    /**
     * Objekt wird upgegraded. Überprüft welches Objekt, falls es Ein Turm ist, wird ein Popup aufgerufen, erhöht das Level des Objektes ({@link Tower#increaseLevel() }/{@link Beacon#increaseLevel() }.
     */
    public void upgradeObject() {
        setNotWalking();
        stopAudio();
        CollisionResults resultsTower = new CollisionResults();
        CollisionResults resultsBeacon = new CollisionResults();
        Ray ray = new Ray(Main.app.getCamera().getLocation(), Main.app.getCamera().getDirection());
        Main.getWorld().getTowerNode().collideWith(ray, resultsTower);
        Main.getWorld().getBeacon().getSpatial().collideWith(ray, resultsBeacon);
        if(resultsTower.size() == 0) {
            if(resultsBeacon.size() != 0){
                Main.getWorld().getBeacon().increaseLevel();
            }
        } else {
            //Es gibt min. einen Turm. Es wird der nächste geholt
            Vector3f pointTower = resultsTower.getClosestCollision().getContactPoint();
            Tower nearestTower = Main.app.getWorld().getNearestTower(pointTower);
            if(resultsBeacon.size() > 0) {
                //Es gibt Türme und Beacon mit Kollision -> Hearusfinden welcher näher ist
                Vector3f pointBeacon = resultsBeacon.getClosestCollision().getContactPoint();
                if(nearestTower.getLocation().subtract(pointTower).length() > Main.app.getWorld().getBeacon().getLocation().subtract(pointBeacon).length()) {
                    //Beacon ist näher
                    Main.getWorld().getBeacon().increaseLevel();
                } else {
                    //Ein Turm upgraden
                   nearestTower.increaseLevel();
                   
                }
            } else {
               //Ein Turm upgraden
               nearestTower.increaseLevel();
            }
        }
    }
    
    /**
     * 
     * @return CharacterControl des Players
     */
    public CharacterControl getCharacterControl(){
        return player;
    }
    
    /**
     * 
     * @return Wieviel Geld der Spieler hat
     */
    public int getMoney(){
        return money;
    }

    /**
     * 
     * @return Schüsse pro Sekunde, mit denen der Player schiesst
     */
    public double getSPS() {
        return shotsPerSecond;
    }

    /**
     * 
     * @return Reichweite des Players
     */
    public int getRange() {
        return range;
    }

    /**
     * Setzt Schüsse pro Sekunde.
     * @param shotsPerSecond Neue Schüsse pro Sekunde
     */
    public void setShotsPerSecond(double shotsPerSecond) {
        this.shotsPerSecond = shotsPerSecond;
    }

    /**
     * Setzt die Reichweite
     * @param range Neue Reichweite
     */
    public void setRange(int range) {
        this.range = range;
    }

    /**
     * Setzt das Geld des Spielers.
     * @param money Neues Geld
     */
    public void setMoney(int money) {
        this.money = money;
    }
    
    /**
     * Erhöht das Geld des Spielers
     * @param money Hinzuzufügendes Geld
     */
    public void increaseMoney(int money){
        this.money += money;
    }
    
    /**
     * Berechnet Reichweite. Berechnet die Reichweite, die der Spieler nach einem Upgrade der Reichweite hätte.
     * @return Neue Reichweite
     */
    public int getNewRange(){
        return (int)(range * 1.1);
    }
    
    /**
     * Erhöht die Reichweite. Erhöht die Reichweite des Spielers, sofern sich dieser das Leisten kann, und zieht das Geld ab.
     */
    public void increaseRange(){
        if(this.getMoney() >= this.getNewRangePrice()) {
           this.increaseMoney(-this.getNewRangePrice());
           range = getNewRange();
           playAudioBought();
        } else {
            playAudioNotEnoughMoney();
        }
    }
    
    /**
     * Berechenet upgradekosten eines Upgrades der Reichweite
     * @return Upgradekosten
     */
    public int getNewRangePrice(){
        return (int) (getNewRange()*0.75);
    }
    
    /**
     * Berechnet Schaden. Berechnet den neuen Schaden, den der Player nach einem upgrade des Schadens machen würde.
     * @return Neuer Schaden
     */
    public int getNewDamage(){
        return (int)(this.getDamage() + Math.sqrt(this.getDamage()));
    }
    
    /**
     * Erhöht den Schaden. Erhöht den Schaden des Spielers, wenn dieser genügend Geld hat, und zieht das Geld ab.
     */
    public void increaseDamage(){
        if(this.getMoney() >= this.getNewDamagePrice()) {
           this.increaseMoney(-this.getNewDamagePrice());
           this.setDamage(getNewDamage()); 
            playAudioBought();
        } else {
            playAudioNotEnoughMoney();
        }
    }
    
    /**
     * Berechnet den Preis eines Upgrades des Schadens.
     * @return Preis eines Schadensupgrades
     */
    public int getNewDamagePrice(){
        return getNewDamage()*50;
    }
    
    /**
     * Berechnet Schüsse pro Sekunde. Berechnet wieviele Schüsse por Sekunde der Spieler nach einem Upgrade abgeben könnte.
     * @return Neue Schüsse pro Sekunde
     */
    public double getNewSPS(){   
        return (int) shotsPerSecond + Math.sqrt(shotsPerSecond);
    }
    
    /**
     * Erhöht die Anzahl Schüsse pro Sekunde. Falls der Spieler genügend Geld hat werden die Schüsse Pro Sekunde upgegraded und das Geld abgezogen.
     */
    public void increaseSPS(){
        if(this.getMoney() >= this.getNewSPSPrice()) {
            this.increaseMoney(-this.getNewSPSPrice());
            shotsPerSecond = getNewSPS();
            playAudioBought();
        } else {
            playAudioNotEnoughMoney();
        }
        
    }
    
    /**
     * Berechnet den Preis eines Upgrades der Schüsse pro Sekunde
     * @return Preis eines Upgrades
     */
    public int getNewSPSPrice(){
        return (int) (getNewSPS()*5);
    }
    
    /**
     * Berechnet Geschwindigkeit. Berechnet die Geschwindigkeit, die ein Spieler nach einem Upgrade hätte.
     * @return Neue Geschwindigkeit
     */
    public int getNewSpeed(){
        return (int) (this.getSpeed()*1.05);
    }
    
    /**
     * Berechnet den Preis eines Geschwindigkeitsupgrades.
     * @return Preis des Upgrades
     */
    public int getNewSpeedPrice() {
        return (int) (getNewSpeed()*1.5);
    }
    
    /**
     * Erhöht die Geschwindigkeit des Spielers. Erhöht die Geschwindigkeit des Spielers und zieht diesem die Kosten ab, sofern er genügend Geld hat.
     */
    public void increaseSpeed(){
        if(this.getMoney() >= this.getNewSpeedPrice()) {
         this.increaseMoney(-this.getNewSpeedPrice());
         this.setSpeed(getNewSpeed());   
            playAudioBought();
        } else {
            playAudioNotEnoughMoney();
        }
    }
    
    /**
     * Berechnet Maximales Leben. Berechnet das Maximale Leben, das der Spieler nach einem Upgrade des Lebens hätte.
     * @return Neues Maximales Leben
     */
    public int getNewMaxHealth() {
        return (int)(this.maxHealth*1.1);
    }
    
    /**
     * Berechnet Preis eines Upgrades des Maximalen Lebens.
     * @return Preis des Upgrades
     */
    public int getNewMaxHealthPrice() {
        return (int)(getNewMaxHealth()*1.5);
    }
    
    /**
     * Maximales Leben wird Upgegraded. Das Maximale Leben wird erhöht und das hinzukommende auch dem effektiven Leben hinzugefügt. Die Kosten für das Upgrade werden abgezogen.
     */
    public void increaseMaxHealth() {
        if(this.getMoney() >= this.getNewMaxHealthPrice()) {
            this.increaseMoney(-this.getNewMaxHealthPrice());
            int diff = this.getNewMaxHealth()-this.getMaxHealth();
            this.setMaxHealth(this.getNewMaxHealth());
            this.increaseHealth(diff);
            playAudioBought();
        } else {
            playAudioNotEnoughMoney();
        }
    }
    
    /**
     * Wiederbelebt den Spieler. Der Spieler wird wiederbelebt, falls er genügend Geld hat. Das Geld wird abgezogen und alle Upgrades werden zurückgesetzt. 
     */
    public void revive(){
        if(this.getRevivePrice() <= this.getMoney()){
            this.increaseMoney(-getRevivePrice());
            this.setLiving(true);
            this.setMaxHealth(100);
            setHealth(this.maxHealth);
            this.setDamage(2);
            this.setSpeed(50);
            this.setShotsPerSecond(50);
            this.setRange(100);
            Main.app.getRootNode().attachChild(this.getSpatial());
            playAudioBought();
        } else {
            playAudioNotEnoughMoney();
        }
    }
    
    /**
     * Berechnet den Preis zum Wiederbeleben.
     * @return Preis zum Wiederbeleben
     */
    public int getRevivePrice(){
        return (int) (Math.sqrt(Main.app.getGame().getWave()) * 50);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Vector3f getLocation() {
        return Main.app.getCamera().getLocation();
    }
    
    /**
     * {@inheritDoc }
     */
    @Override 
    public void setLocation(Vector3f location) {
        player.setPhysicsLocation(location);
        Main.app.getCamera().setLocation(location);
    }
    
    /**
     * Stoppt das gehen des Spielers.
     */
    public void setNotWalking(){
        left = false;
        right = false;
        up = false;
        down = false;
        walkDirection = new Vector3f(0, 0, 0);
        player.setEnabled(false);
    }
    
    /**
     * Stoppt die Wiedergabe der langanhaltenden Soundeffekte.
     */
    public void stopAudio(){
        shootAudio.stop();
        walkAudio.stop();
    }
    
    /**
     * Spielt den Effekt ab, der beim kaufen abgespielt werden soll, ab.
     */
    public void playAudioBought(){
        buyAudio.playInstance();
    }
    
    /**
     * Spielt den Effekt ab, der bei zu wenig Geld zum kaufen abgespielt werden soll, ab.
     */
    public void playAudioNotEnoughMoney(){
        notEnoughMoneyAudio.playInstance();
    }

    /**
     * Spielt den Effekt ab, der beim verdienen von Geld abgespielt werden soll, ab.
     */
    public void playAudioEarnMoney(){
        earnMoneyAudio.playInstance();
    }
    
    /**
     * Ändert die Tastaturbelegungen.
     * @param key_left Taste um nach Links zu laufen
     * @param key_right Taste um nach rechts zu laufen
     * @param key_up Taste um geradeaus zu laufen
     * @param key_down Taste um zurück zu laufen
     * @param key_jump Taste um zu springen
     */
    public void replaceKeys(int key_left,
            int key_right,
            int key_up,
            int key_down,
            int key_jump){
        if(key_left != 0)
            this.key_left = key_left;
        if(key_right != 0)
            this.key_right = key_right;
        if(key_up != 0)
            this.key_up = key_up;
        if(key_down != 0)
            this.key_down = key_down;
        if(key_jump != 0)
            this.key_jump = key_jump;
        
        setUpKeys();
    }
}
