package mygame.Entitys;

import com.jme3.audio.AudioNode;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.InputListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Sphere;
import mygame.Main;
import mygame.Settings;

/**
 * Der Spieler. Erstellt und konntrolliert den Spieler und seine Aktionen.
 * @author Florian Wenk und Samuel Martin
 */
public class Player extends Entity{
    
    private String name;                    //Name des Spielers
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
    private int healPoints;                 //Punkte die beim Heilen pro Updateloop geheilt werden.
    private boolean isHealing;              //Ob der Spieler am heilen ist
    private boolean hasHealed;              //Ob der Spieler geheilt hat
    private boolean towerInfoVisible;       //Ob die Turm info sichtbar ist
    private Tower towerWithTowerInfoVisible;    // Wenn die turminfo sichtbar ist, auf welchen Turm sie zeigt
    
    private double shotsPerSecond;          //Wieoft der Spieler in der Sekunde schiesst
    private int range;                      //Reichweite des Spielers
    
    private AudioNode shootAudio;           //Ton, der beim Schiessen abgespielt wird
    private AudioNode walkAudio;            //Ton, der beim Laufen abgespielt wird
    private AudioNode buyAudio;             //Ton, der beim Kaufen abgespielt wird
    private AudioNode notEnoughMoneyAudio;  //Ton, der bei zu wenig Geld zum kaufen abgespielt wird
    private AudioNode earnMoneyAudio;       //Ton, der beim verdienen von Geld abgespielt wird
    
    private int keyLeft;                   //Keycode der Links-Taste
    private int keyRight;                  //Keycode der Rechts-Taste
    private int keyUp;                     //Keycode der Oben-Taste
    private int keyDown;                   //Keycode der Unten-Taste
    private int keyJump;                   //Keycode der Springen-Taste
    
    private Geometry placeOnScene;         //Ist immer am ort auf der Scene wo der Spieler hinblickt
    private boolean towerPreviewVisible;
    private Tower towerPreview;
    private Geometry towerPreviewRange;
    
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
        towerInfoVisible = false;
        towerWithTowerInfoVisible = null;
        healPoints = 1;
        shotsPerSecond = 50;
        range = 100; 
        money = 250;
        this.inputListener = inputListener;
        towerPreviewVisible = false;
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
        
        placeOnScene = new Geometry("sphere", new Sphere(32, 32, 1f));
        placeOnScene.setMaterial(mat);
//        Main.app.getRootNode().attachChild(placeOnScene);

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
        shootAudio = new AudioNode(Main.app.getAssetManager(), "Audio/Effects/shootAudio.wav", false);
        shootAudio.setPositional(false);
        shootAudio.setLooping(true);
        Main.app.getRootNode().attachChild(shootAudio);
        
        //Sound von: https://freesound.org/people/Shadowedhunter/sounds/155920/ (User: Shadowedhunter)
        //Bearbeitet von: Florian Wenk
        walkAudio = new AudioNode(Main.app.getAssetManager(), "Audio/Effects/walkAudio.wav", false);
        walkAudio.setPositional(false);
        walkAudio.setLooping(true);
        Main.app.getRootNode().attachChild(walkAudio);
        
        //Sound von: https://freesound.org/people/jact878787/sounds/323809/ (User: jact878787)
        //Bearbeitet von: Florian Wenk
        buyAudio = new AudioNode(Main.app.getAssetManager(), "Audio/Effects/buyAudio.wav", false);
        buyAudio.setPositional(false);
        buyAudio.setLooping(false);
        Main.app.getRootNode().attachChild(buyAudio);
        
        //Sound von: https://freesound.org/people/clairinski/sounds/184372/ (User: clairinski)
        //Bearbeitet von: Florian Wenk
        notEnoughMoneyAudio = new AudioNode(Main.app.getAssetManager(), "Audio/Effects/notEnoughMoneyAudio.wav", false);
        notEnoughMoneyAudio.setPositional(false);
        notEnoughMoneyAudio.setLooping(false);
        Main.app.getRootNode().attachChild(notEnoughMoneyAudio);
        
        //Sound von: https://freesound.org/people/severaltimes/sounds/173989/ (User: severaltimes)
        //Bearbeitet von: Florian Wenk
        earnMoneyAudio = new AudioNode(Main.app.getAssetManager(), "Audio/Effects/earnMoneyAudio.wav", false);
        earnMoneyAudio.setPositional(false);
        earnMoneyAudio.setLooping(false);
        Main.app.getRootNode().attachChild(earnMoneyAudio);
        
        reloadVolumes();
    }
    
    /**
     * Konstruktor, der neben dem was {@link Player#Player(com.jme3.input.controls.InputListener)} macht, noch den Namen setzt.
     * @param inputListener Für Tasteneingaben benötigt
     * @param name Name des Spielers
     */
    public Player(InputListener inputListener, String name){
        this(inputListener);
        this.name = name;
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
        keyLeft = Main.app.getSettings().getKey("goLeft");
        keyRight = Main.app.getSettings().getKey("goRight");
        keyUp = Main.app.getSettings().getKey("forward");
        keyDown = Main.app.getSettings().getKey("backward");
        keyJump = Main.app.getSettings().getKey("jump");
        
        Main.app.getInputManager().addMapping("Left", new KeyTrigger(keyLeft));
        Main.app.getInputManager().addMapping("Right", new KeyTrigger(keyRight));
        Main.app.getInputManager().addMapping("Up", new KeyTrigger(keyUp));
        Main.app.getInputManager().addMapping("Down", new KeyTrigger(keyDown));
        Main.app.getInputManager().addMapping("Jump", new KeyTrigger(keyJump));
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
     * Löscht alle Tastaturbelegungen, damit sie wieder neu belegt werden können.
     */
    private void deleteKeys() {
         Main.app.getInputManager().deleteMapping("Left");
         Main.app.getInputManager().deleteMapping("Right");
         Main.app.getInputManager().deleteMapping("Up");
         Main.app.getInputManager().deleteMapping("Down");
         Main.app.getInputManager().deleteMapping("Jump");
         Main.app.getInputManager().deleteMapping("Shoot");
         Main.app.getInputManager().deleteMapping("placeTower");
    }
    
    /**
     * Methode, die aufgerufen wird, damit die Tastaturbelegung neu von der {@link Settings}-Klasse geladen wird.
     */
    public void reloadKeys() {
        deleteKeys();
        setUpKeys();
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
            if(isPressed) {
                switch(Main.app.getHudState().getSelectedItemNum()){
                    case 1:
                    case 2:
                    case 4:
                        //Turmvorschau
                        towerPreviewVisible = true;
                        break;
                    case 5:
                        isHealing = true;
                        break;
                        
                }
            }
            if(!isPressed) {
                switch(Main.app.getHudState().getSelectedItemNum()){
                    case 1:
                    case 2:
                        if(towerPreviewVisible){
                            this.placeTower();
                        }
                        break;
                    case 3:
                        Main.app.getHudState().showChooseTowerPopup();
                        break;
                    case 4:
                        this.upgradeObject();
                        break;
                    case 5:
                        isHealing = false;
                        if(hasHealed){
                          playAudioBought();
                          hasHealed = false;
                        }
                        break;
                }
                towerPreviewVisible = false;
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
        if(walkDirection.length() != 0 && player.onGround()){
            walkAudio.play();
        } else {
            walkAudio.stop();
        }
        player.setWalkDirection(walkDirection);
        Main.app.getCamera().setLocation(player.getPhysicsLocation());
        this.getSpatial().setLocalTranslation(Main.app.getCamera().getLocation().add(Main.app.getCamera().getUp().normalize().mult(-1.75f)).add(Main.app.getCamera().getLeft().normalize().mult(-.75f)).add(Main.app.getCamera().getDirection().normalize().mult(1.9f)));
        this.getSpatial().lookAt(Main.app.getCamera().getDirection().mult(range).add(Main.app.getCamera().getLocation()), new Vector3f(0,1,0));
        Main.app.getListener().setLocation(Main.app.getCamera().getLocation());
        
        if(isHealing) {
            heal();
        } else {
            healingLine.removeFromParent();
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
            healingLine.removeFromParent();
        }
        if(!isLiving() && this.getSpatial().hasAncestor(Main.app.getRootNode())){
            this.getSpatial().removeFromParent();
        }

        //Kontrolle ob Turm im Sichtfeld
        CollisionResults results = new CollisionResults();
        CollisionResults resultsBeacon = new CollisionResults();
        CollisionResults resultsScene = new CollisionResults();
        Ray ray = new Ray(Main.app.getCamera().getLocation(), Main.app.getCamera().getDirection());
        Main.getWorld().getTowerNode().collideWith(ray, results);
        Main.getWorld().getBeacon().getSpatial().collideWith(ray, resultsBeacon);
        Main.getWorld().getScene().collideWith(ray, resultsScene);
        if(results.size() != 0 && (resultsBeacon.size() == 0 || results.getClosestCollision().getContactPoint().subtract(this.getLocation()).length() < resultsBeacon.getClosestCollision().getContactPoint().subtract(this.getLocation()).length()) && results.getClosestCollision().getContactPoint().subtract(this.getLocation()).length() <= 150 && (resultsScene.size() == 0 || results.getClosestCollision().getContactPoint().subtract(this.getLocation()).length() < resultsScene.getClosestCollision().getContactPoint().subtract(this.getLocation()).length())) {
            Tower tower = Main.getWorld().getNearestTower(results.getClosestCollision().getContactPoint());
            Main.app.getHudState().showTowerInfo(tower);
            towerInfoVisible = true;
            towerWithTowerInfoVisible = tower;
        } else {
            Main.app.getHudState().hideTowerInfo();
            towerInfoVisible = false;
            towerWithTowerInfoVisible = null;
        }
        if(resultsScene.size() != 0){
           if(Main.app.getWorld().getSceneDecoration() != null){
               CollisionResults resultsDecoration = new CollisionResults();
               Main.app.getWorld().getSceneDecoration().collideWith(ray, resultsDecoration);
               if(resultsDecoration.size() == 0){
                   placeOnScene.setLocalTranslation(resultsScene.getClosestCollision().getContactPoint());
               } else {
                   placeOnScene.setLocalTranslation(new Vector3f(0, 0, 0));
               }
           } else {
               placeOnScene.setLocalTranslation(resultsScene.getClosestCollision().getContactPoint());
           }
     //            if(!placeOnScene.hasAncestor(Main.app.getRootNode())){
     //                Main.app.getRootNode().attachChild(placeOnScene);
     //            }
        } else {
     //             placeOnScene.removeFromParent();
            placeOnScene.setLocalTranslation(new Vector3f(0, 0, 0));
        }
        if(player.getPhysicsLocation().getY() <= -150 && !player.onGround()){
            revive();
        }
        if(towerPreviewVisible && Main.app.getHudState().getSelectedItemNum() != 4 || towerPreview == null){
            if(towerPreview == null){
                towerPreview = getPreviewTower();
                if(towerPreview != null){
                    towerPreviewRange = new Geometry("Range", new Sphere(50, 50, (float) towerPreview.getRange()));
                    Material mat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
                    mat.setColor("Color", new ColorRGBA(0, 0, 0, 0.25f));
                    mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
                    mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
                    towerPreviewRange.setMaterial(mat);
                    towerPreviewRange.setQueueBucket(RenderQueue.Bucket.Translucent);
                    towerPreviewRange.setLocalTranslation(towerPreview.getNode().getLocalTranslation());
                    Main.app.getRootNode().attachChild(towerPreviewRange);
                    Main.app.getRootNode().attachChild(towerPreview.getNode());
                }
             } else {
                 replacePreviewTower();
             }
        } else if (towerPreviewVisible && Main.app.getHudState().getSelectedItemNum() == 4){
            if(towerPreview.getNode().hasAncestor(Main.app.getRootNode())){
                towerPreview.getNode().removeFromParent();
            }
            if(!towerPreviewRange.hasAncestor(Main.app.getRootNode()) && towerInfoVisible){
                towerPreviewRange = new Geometry("Range", new Sphere(50, 50, (float) towerWithTowerInfoVisible.getNewRange(towerWithTowerInfoVisible.getLevel()+1)));
                Material mat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
                mat.setColor("Color", new ColorRGBA(0, 0, 0, 0.25f));
                mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
                mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
                towerPreviewRange.setMaterial(mat);
                towerPreviewRange.setQueueBucket(RenderQueue.Bucket.Translucent);
                towerPreviewRange.setLocalTranslation(towerWithTowerInfoVisible.getNode().getLocalTranslation());
                Main.app.getRootNode().attachChild(towerPreviewRange);
            } else if (towerPreviewRange.hasAncestor(Main.app.getRootNode()) && towerWithTowerInfoVisible == null) {
                towerPreviewRange.removeFromParent();
            }
        } else if (towerPreview != null && towerPreview.getNode().hasAncestor(Main.app.getRootNode())){
            towerPreview.getNode().removeFromParent();
        } else if (towerPreviewRange != null && towerPreviewRange.hasAncestor(Main.app.getRootNode())){
            towerPreviewRange.removeFromParent();
        }
        if (Main.app.getHudState().getSelectedItemNum() == 3 || Main.app.getHudState().getSelectedItemNum() == 5){
            towerPreviewVisible = false;
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
        CollisionResults resultsScene = new CollisionResults();
        Main.getWorld().getScene().collideWith(ray, resultsScene);
        Vector3f v = null;
        if(resultsScene.size() != 0){
            v = resultsScene.getClosestCollision().getContactPoint();
            if (v != null && Main.getWorld().distanceIsLonger(l.getStart(), l.getEnd(), v)){
                l = new Line(this.getSpatial().getLocalTranslation().add(Main.app.getCamera().getUp().normalize().mult(1.3f)), v);
                line.setMesh(l);
            }
        }
        //Prüft, ob eine Bombe getroffen wurde
        Main.getWorld().getBombNode().collideWith(ray, results);
        if (canShoot() && results.size() > 0) {
            CollisionResult closest = results.getClosestCollision();
            l = new Line(this.getSpatial().getLocalTranslation().add(Main.app.getCamera().getUp().normalize().mult(1.3f)), closest.getContactPoint());
            line.setMesh(l);
            if(Main.getWorld().distanceIsLonger(l.getStart(), v, l.getEnd()) && closest.getContactPoint().add(Main.getWorld().getBombNode().getLocalTranslation()).subtract(Main.app.getCamera().getLocation()).length() <= range){
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
        Ray ray = new Ray(placeOnScene.getLocalTranslation(), Main.app.getCamera().getDirection());
        // trifft auf scene
        Main.app.getWorld().getScene().collideWith(ray, results);
        if (results.size() > 0) {
            //Punkt auf der scene wo der Spieler hinblickt, da sphere immer dorthingesetzt wird. results.getClosestCollision().getContactPoint() ergab falsche resultate
            Vector3f location = placeOnScene.getLocalTranslation();
            Vector3f up = results.getClosestCollision().getContactNormal();
            //Es wird nicht auf Scene geschaut -> abbrechen
            if(location.equals(new Vector3f(0, 0, 0))){
                return;
            }
            // senkrechte Wand -> Spatial auf der Seite
            if(up.getY() == 0){
                return;
            }
            // kontrolliert ob kollision mit Beacon
            CollisionResults resultsBeacon = new CollisionResults();
            Main.app.getWorld().getBeacon().getSpatial().collideWith(ray, resultsBeacon);
            if(resultsBeacon.size() > 0){
                // Zu nahe an beacon -> nicht setzen
                return;
            }
            Tower tower = Main.app.getHudState().getSelectedTower(location, up);
            CollisionResults resultsWay = new CollisionResults();
            Ray rayWay = new Ray(location, new Vector3f(0, 0-location.getY(), 0));
            Main.getWorld().getWayNode().collideWith(rayWay, resultsWay);
            if(resultsWay.size() != 0){
                return;
            }
            Main.getWorld().getWayNode().collideWith(ray, resultsWay);
            if(resultsWay.size() != 0){
                return;
            }
            //kontrolliert ob Spieler genug Geld hat
            if(getMoney()-tower.getPrice() < 0) {
                //Nicht genug Geld -> Turm wird nicht gesetzt.
                playAudioNotEnoughMoney();
                return;
            }
            Tower nearest = Main.app.getWorld().getNearestTower(location);
            // konntrolliert ob Distanz zum nächsten Turm genügend gross ist
            if(nearest != null && nearest.getSpatial().getLocalTranslation().distance(tower.getSpatial().getLocalTranslation()) < 10){
                // Zu nahe an einem anderen Turm -> Turm wird nicht gesetzt
                return;
            }
            if(location.subtract(Main.app.getCamera().getLocation()).length() < 5){
                return;
            }
            //Zieht Geld 
            increaseMoney(-tower.getPrice());
            playAudioBought();
            // plaziert Turm
            Main.app.getWorld().addTower(tower);
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
                int beforeHealed = this.getHealth();
                this.increaseHealth(healPoints);
                this.increaseMoney(beforeHealed-this.getHealth());
                hasHealed = true;
                healingLine.removeFromParent();
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
                        int beforeHealed = beacon.getHealth();
                        beacon.increaseHealth(healPoints);
                        this.increaseMoney(beforeHealed-beacon.getHealth());
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
                        int beforeHealed = beacon.getHealth();
                        beacon.increaseHealth(healPoints);
                        this.increaseMoney(beforeHealed-beacon.getHealth());
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
                        int beforeHealed = nearestTower.getHealth();
                        nearestTower.increaseHealth(healPoints);
                        this.increaseMoney(beforeHealed-nearestTower.getHealth());
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
                    int beforeHealed = nearestTower.getHealth();
                    nearestTower.increaseHealth(healPoints);
                    this.increaseMoney(beforeHealed-nearestTower.getHealth());
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
        if(towerInfoVisible){
            towerWithTowerInfoVisible.increaseLevel();
            return;
        }
        CollisionResults resultsTower = new CollisionResults();
        CollisionResults resultsBeacon = new CollisionResults();
        CollisionResults resultsScene = new CollisionResults();
        Ray ray = new Ray(Main.app.getCamera().getLocation(), Main.app.getCamera().getDirection());
        Main.app.getWorld().getTowerNode().collideWith(ray, resultsTower);
        Main.app.getWorld().getBeacon().getSpatial().collideWith(ray, resultsBeacon);
        Main.app.getWorld().getScene().collideWith(ray, resultsScene);
        if(resultsTower.size() == 0) {
            if(resultsBeacon.size() != 0 && resultsBeacon.getClosestCollision().getContactPoint().subtract(this.getLocation()).length() < resultsScene.getClosestCollision().getContactPoint().subtract(this.getLocation()).length()){
                Main.getWorld().getBeacon().increaseLevel();
            }
        } else {
            if(resultsScene.size() > 0 && resultsTower.getClosestCollision().getContactPoint().subtract(this.getLocation()).length() > resultsScene.getClosestCollision().getContactPoint().subtract(this.getLocation()).length()){
                return;
            }
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
     * Gibt die Anzahl Lebenspunkte die pro Updateloop geheilt werden zurück.
     * @return Lebenspunkte pro Updateloop
     */
    public int getHealPoints(){
        return healPoints;
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
     * Setzt die anzahl Lebenspunkte pro Updateloop.
     * @param healPoints Lebenspunkte pro Updateloop
     */
    public void setHealPoints(int healPoints){
        this.healPoints = healPoints;
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
     * Berechnet Lebenspunkte, die pro Sekunde geheilt werden. Berechnet die neuen Lebenspunkte pro Sekunde, die nach einem Upgrade geheilt würden.
     * @return Neue Lebenspunkte pro Updateloop
     */
    public int getNewHealPoints(){
        return this.healPoints+1;
    }
    
    /**
     * Berechnet den Preis eines Upgrades der Lebenspunkte pro Updateloop beim Heilen.
     * @return 
     */
    public int getNewHealPointsPrice(){
        return (int) (getNewHealPoints() * 100);
    }
    
    /**
     * Erhöht die Lebenspunkte pro Updateloop, die der Spieler heilt. Erhöht die Lebenspunkte pro Sekunde und zieht diesem die Kosten ab, sofern er genügend Geld hat.
     */
    public void increaseHealPoints(){
        if(this.getMoney() >= this.getNewHealPointsPrice()){
            this.increaseMoney(-this.getNewHealPointsPrice());
            this.setHealPoints(this.getNewHealPoints());
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
            this.setLocation(new Vector3f(0, 10, 0));
            this.setLiving(true);
            this.setMaxHealth(100);
            setHealth(this.maxHealth);
            this.setHealPoints(1);
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
     * Stoppt das Schiessen und Heilen des Spielers.
     */
    public void setNotShootingAndHealing(){
        isShooting = false;
        isHealing = false;
        line.removeFromParent();
        healingLine.removeFromParent();
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
     * @param keysWalking Tasten zum Laufen. Reihenfolge, left, right, up, down.
     * @param keyJump Taste um zu springen
     */
    public void replaceKeys(String[] keysWalking, String keyJump){
        keyLeft = Main.app.getSettings().getKeyCode(keysWalking[0]);
        keyRight = Main.app.getSettings().getKeyCode(keysWalking[1]);
        keyUp = Main.app.getSettings().getKeyCode(keysWalking[2]);
        keyDown = Main.app.getSettings().getKeyCode(keysWalking[3]);
        this.keyJump = Main.app.getSettings().getKeyCode(keyJump);
        
        setUpKeys();
    }
    
    /**
     * Gibt den Namen des Spielers zurück.
     * @return Name des Spielers
     */
    public String getName(){
        return name;
    }
    
    /**
     * Setzt den Namen des Spielers.
     * @param name Neuer Name
     */
    public void setName(String name){
        this.name = name;
    }
    
    public void reloadVolumes(){
        shootAudio.setVolume((float) Main.app.getSettings().getVolumeEffectsEffective());
        walkAudio.setVolume(.5f * (float) Main.app.getSettings().getVolumeEffectsEffective());
        buyAudio.setVolume((float) Main.app.getSettings().getVolumeEffectsEffective());
        notEnoughMoneyAudio.setVolume((float) Main.app.getSettings().getVolumeEffectsEffective());
        earnMoneyAudio.setVolume((float) Main.app.getSettings().getVolumeEffectsEffective());
    }
    
    private Tower getPreviewTower(){
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(placeOnScene.getLocalTranslation(), Main.app.getCamera().getDirection());
        // trifft auf scene
        Main.app.getWorld().getScene().collideWith(ray, results);
        if (results.size() > 0) {
            //Punkt auf der scene wo der Spieler hinblickt, da sphere immer dorthingesetzt wird. results.getClosestCollision().getContactPoint() ergab falsche resultate
            Vector3f location = placeOnScene.getLocalTranslation();
            Vector3f up = results.getClosestCollision().getContactNormal();
            //Es wird nicht auf Scene geschaut -> abbrechen
            if(location.equals(new Vector3f(0, 0, 0))){
                return null;
            }
            // senkrechte Wand -> Spatial auf der Seite
            if(up.getY() == 0){
                return null;
            }
            // kontrolliert ob kollision mit Beacon
            CollisionResults resultsBeacon = new CollisionResults();
            Main.app.getWorld().getBeacon().getSpatial().collideWith(ray, resultsBeacon);
            if(resultsBeacon.size() > 0){
                // Zu nahe an beacon -> nicht setzen
                return null;
            }
            Tower tower = Main.app.getHudState().getSelectedTower(location, up);
            CollisionResults resultsWay = new CollisionResults();
            Ray rayWay = new Ray(location, new Vector3f(0, 0-location.getY(), 0));
            Main.getWorld().getWayNode().collideWith(rayWay, resultsWay);
            if(resultsWay.size() != 0){
                return null;
            }
            Main.getWorld().getWayNode().collideWith(ray, resultsWay);
            if(resultsWay.size() != 0){
                return null;
            }
            
            Tower nearest = Main.app.getWorld().getNearestTower(location);
            // konntrolliert ob Distanz zum nächsten Turm genügend gross ist
            if(nearest != null && nearest.getSpatial().getLocalTranslation().distance(tower.getSpatial().getLocalTranslation()) < 10){
                // Zu nahe an einem anderen Turm -> Turm wird nicht gesetzt
                return null;
            }
            if(location.subtract(Main.app.getCamera().getLocation()).length() < 5){
                return null;
            }
            return tower;
        }
        return null;
    }
    
    private void replacePreviewTower(){
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(placeOnScene.getLocalTranslation(), Main.app.getCamera().getDirection());
        // trifft auf scene
        Main.app.getWorld().getScene().collideWith(ray, results);
        if (results.size() > 0) {
            //Punkt auf der scene wo der Spieler hinblickt, da sphere immer dorthingesetzt wird. results.getClosestCollision().getContactPoint() ergab falsche resultate
            Vector3f location = placeOnScene.getLocalTranslation();
            Vector3f up = results.getClosestCollision().getContactNormal();
            //Es wird nicht auf Scene geschaut -> abbrechen
            if(location.equals(new Vector3f(0, 0, 0))){
                towerPreview.getNode().removeFromParent();
                towerPreviewRange.removeFromParent();
                return;
            }
            // senkrechte Wand -> Spatial auf der Seite
            if(up.getY() == 0){
                towerPreview.getNode().removeFromParent();
                towerPreviewRange.removeFromParent();
                return;
            }
            // kontrolliert ob kollision mit Beacon
            CollisionResults resultsBeacon = new CollisionResults();
            Main.app.getWorld().getBeacon().getSpatial().collideWith(ray, resultsBeacon);
            if(resultsBeacon.size() > 0){
                // Zu nahe an beacon -> nicht setzen
                towerPreview.getNode().removeFromParent();
                towerPreviewRange.removeFromParent();
                return;
            }
            Tower tower = Main.app.getHudState().getSelectedTower(location, up);
            if(tower == null){
                return;
            }
            CollisionResults resultsWay = new CollisionResults();
            Ray rayWay = new Ray(location, new Vector3f(0, 0-location.getY(), 0));
            Main.getWorld().getWayNode().collideWith(rayWay, resultsWay);
            if(resultsWay.size() != 0){
                towerPreview.getNode().removeFromParent();
                towerPreviewRange.removeFromParent();
                return;
            }
            Main.getWorld().getWayNode().collideWith(ray, resultsWay);
            if(resultsWay.size() != 0){
                towerPreview.getNode().removeFromParent();
                towerPreviewRange.removeFromParent();
                return;
            }
            
            Tower nearest = Main.app.getWorld().getNearestTower(location);
            // konntrolliert ob Distanz zum nächsten Turm genügend gross ist
            if(nearest != null && nearest.getSpatial().getLocalTranslation().distance(tower.getSpatial().getLocalTranslation()) < 10){
                // Zu nahe an einem anderen Turm -> Turm wird nicht gesetzt
                towerPreview.getNode().removeFromParent();
                towerPreviewRange.removeFromParent();
                return;
            }
            if(location.subtract(Main.app.getCamera().getLocation()).length() < 5){
                towerPreview.getNode().removeFromParent();
                towerPreviewRange.removeFromParent();
                return;
            }
            towerPreview.getNode().removeFromParent();
            towerPreview = tower;
            Main.app.getRootNode().attachChild(towerPreview.getNode());
            towerPreviewRange.removeFromParent();
            towerPreviewRange = new Geometry("Range", new Sphere(50, 50, (float) towerPreview.getRange()));
            Material mat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", new ColorRGBA(0, 0, 0, 0.25f));
            mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
            mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
            towerPreviewRange.setMaterial(mat);
            towerPreviewRange.setQueueBucket(RenderQueue.Bucket.Translucent);
            towerPreviewRange.setLocalTranslation(towerPreview.getNode().getLocalTranslation());
            Main.app.getRootNode().attachChild(towerPreviewRange);
            return;
        }
        towerPreview.getNode().removeFromParent();
        towerPreviewRange.removeFromParent();
    }
}
