import java.util.ArrayList;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class PhysicEngine implements Engine {
    // Liste des objets qui bougent (ex: le héros)
    private ArrayList<DynamicSprite> movingSpriteList;
    // Liste des obstacles (ex: murs, rochers)
    private ArrayList<Sprite> environment;
    private ArrayList<Sprite> trapList = new ArrayList<>();
    private ArrayList<Sprite> coinList = new ArrayList<>();
    private DynamicSprite hero; // On a besoin de savoir qui est le héros spécifiquement
    private ArrayList<Sprite> swordList = new ArrayList<>();
    // On a besoin d'une liste séparée pour les ennemis (pour attaquer le héros)
    private ArrayList<Sprite> enemyList = new ArrayList<>();
    // On a besoin de parler au RenderEngine pour stopper le jeu
    private RenderEngine renderEngine;
    private ArrayList<Stair> stairList = new ArrayList<>();
    private String nextLevelToLoad = null;
    public void setTrapList(ArrayList<Sprite> trapList) {
        this.trapList = trapList;
    }

    public void addToStairList(Stair stair) {
        stairList.add(stair);
    }

    //pour permettre au Main de savoir s'il faut changer de niveau
    public String getNextLevelToLoad() {
        return nextLevelToLoad;
    }

    //pour remettre à zéro après le changement
    public void resetLevelLoad() {
        this.nextLevelToLoad = null;
    }
    public PhysicEngine() {
        movingSpriteList = new ArrayList<>();
        environment = new ArrayList<>();
    }

    // Pour ajouter le héros au moteur
    public void addToMovingSpriteList(DynamicSprite sprite) {
        movingSpriteList.add(sprite);
    }

    // Pour définir les obstacles (murs, arbres...)
    public void setEnvironment(ArrayList<Sprite> environment) {
        this.environment = environment;
    }

    public void setHero(DynamicSprite hero) {
        this.hero = hero;
    }

    public void addToEnemyList(DynamicSprite enemy) {
        this.enemyList.add(enemy);
        this.movingSpriteList.add(enemy); // On l'ajoute aussi à la liste générale des mouvements
    }

    public void setRenderEngine(RenderEngine renderEngine) {
        this.renderEngine = renderEngine;
    }

    public void update() {
        for (DynamicSprite dynamicSprite : movingSpriteList) {
            dynamicSprite.moveIfPossible(environment);

            // 2. Gestion des pièges
            boolean currentlyOnTrap = false;

            // On calcule la hitbox du héros
            Rectangle2D.Double heroHitbox = new Rectangle2D.Double(dynamicSprite.getX(), dynamicSprite.getY(), dynamicSprite.getWidth(), dynamicSprite.getHeight());

            for (Sprite trap : trapList) {
                // On crée un rectangle mathématique TEMPORAIRE qui a la même taille et position que le piège
                Rectangle2D.Double trapHitbox = new Rectangle2D.Double(trap.getX(), trap.getY(), trap.getWidth(), trap.getHeight());

                // Si intersection avec un piège
                if (heroHitbox.intersects(trapHitbox)) {
                    dynamicSprite.setStunned(); // Tente d'étourdir le héros
                    currentlyOnTrap = true;
                }
            }

            // Si on n'est plus sur un piège, on réinitialise pour pouvoir être piégé à nouveau plus tard
            if (!currentlyOnTrap) {
                dynamicSprite.resetTrapState();
            }
        }
        if (hero != null && renderEngine != null) {
            // 1. Hitbox du héros
            Rectangle2D heroBox = new Rectangle2D.Double(hero.getX(), hero.getY(), hero.getWidth(), hero.getHeight());

            // 2. On vérifie contre tous les ennemis
            for (Sprite enemy : enemyList) {
                Rectangle2D enemyBox = new Rectangle2D.Double(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight());

                if (heroBox.intersects(enemyBox)) {
                    System.out.println("GAME OVER ! Collision avec un ennemi.");
                    // On dit au moteur de rendu d'afficher l'écran de fin
                    renderEngine.setGameOver(true);
                }
                if (enemy instanceof BooEnemy) {
                    BooEnemy boo = (BooEnemy) enemy;

                    // 1. On vérifie si le héros regarde dans la direction de Boo
                    boolean heroLooksAtBoo = false;
                    if (hero.getDirection() == Direction.EAST && boo.getX() >= hero.getX()) {
                        heroLooksAtBoo = true; // Héros regarde à droite, Boo est à droite
                    }
                    else if (hero.getDirection() == Direction.WEST && boo.getX() <= hero.getX()) {
                        heroLooksAtBoo = true; // Héros regarde à gauche, Boo est à gauche
                    }
                    else if (hero.getDirection() == Direction.SOUTH && boo.getY() >= hero.getY()) {
                        heroLooksAtBoo = true; // Héros regarde en bas, Boo est en bas
                    }
                    else if (hero.getDirection() == Direction.NORTH && boo.getY() <= hero.getY()) {
                        heroLooksAtBoo = true; // Héros regarde en haut, Boo est en haut
                    }

                    // 2. Si le héros NE regarde PAS Boo, Boo attaque (il avance en ignorant les murs)
                    if (!heroLooksAtBoo) {
                        double speed = 1.5; // Vitesse de Boo

                        // Rapprochement sur X
                        if (boo.getX() < hero.getX()) boo.setX(boo.getX() + speed);
                        else if (boo.getX() > hero.getX()) boo.setX(boo.getX() - speed);

                        // Rapprochement sur Y
                        if (boo.getY() < hero.getY()) boo.setY(boo.getY() + speed);
                        else if (boo.getY() > hero.getY()) boo.setY(boo.getY() - speed);
                    }
                    // S'il le regarde, on ne fait rien (Boo est pétrifié !)
                }
            }
        }
        if (hero != null) {
            Rectangle2D heroBox = new Rectangle2D.Double(hero.getX(), hero.getY(), hero.getWidth(), hero.getHeight());

            // On vérifie tous les escaliers
            for (Stair stair : stairList) {
                Rectangle2D stairBox = new Rectangle2D.Double(stair.getX(), stair.getY(), stair.getWidth(), stair.getHeight());

                if (heroBox.intersects(stairBox)) {
                    System.out.println("Escalier trouvé ! Direction : " + stair.getNextLevel());
                    // On enregistre le niveau à charger
                    this.nextLevelToLoad = stair.getNextLevel();
                }
            }
            // --- GESTION DES PIÈCES ---
            ArrayList<Sprite> coinsToRemove = new ArrayList<>();

            for (Sprite c : coinList) {
                Coin coin = (Coin) c;

                // 1. On vérifie si la Hitbox de Link croise celle de la pièce
                boolean isTouching = hero.getX() < coin.getX() + coin.getWidth() &&
                        hero.getX() + hero.getWidth() > coin.getX() &&
                        hero.getY() < coin.getY() + coin.getHeight() &&
                        hero.getY() + hero.getHeight() > coin.getY();

                // 2. On met à jour le chronomètre de la pièce
                coin.updatePickupLogic(isTouching);

                // 3. Si elle vient d'être validée (100ms écoulées)
                if (coin.isCollected()) {
                    coinsToRemove.add(coin);
                    playSound("coin_obtention.wav");
                    hero.setCoinScore(hero.getCoinScore() + 1);
                    hero.setLastCoinTime(System.currentTimeMillis());
                }
            }
// On retire les pièces ramassées pour qu'elles n'existent plus physiquement
            coinList.removeAll(coinsToRemove);
        }
        // --- GESTION DE LA MASTERSWORD ---
        ArrayList<Sprite> swordsToRemove = new ArrayList<>();
        for (Sprite s : swordList) {
            MasterSword sword= (MasterSword) s;
            boolean isHeroOnSword =
                    hero.getX() < s.getX() + s.getWidth() &&
                            hero.getX() + hero.getWidth() > s.getX() &&
                            hero.getY() < s.getY() + s.getHeight() &&
                            hero.getY() + hero.getHeight() > s.getY();

            if (isHeroOnSword) {
                // Si Link a moins de 7 pièces
                if (hero.getCoinScore() < 7) {
                    // On déclenche le chronomètre du message d'erreur
                    hero.setLastSwordMessageTime(System.currentTimeMillis());
                } else {
                    // si Link a 7 pièces ou plus
                    sword.setCollected(true); // On la marque comme ramassée
                    swordsToRemove.add(sword); // On prépare sa suppression

                    // On joue le son épique !
                    new AudioPlayground().playSound("./audio/sword_obtention.wav");
                    hero.setSwordObtainedTime(System.currentTimeMillis());
                }
            }
        }
        swordList.removeAll(swordsToRemove);
    }
    public ArrayList<Sprite> getEnemyList() {
        return this.enemyList;
    }
    public ArrayList<Stair> getStairList() {
        return this.stairList;
    }
    public ArrayList<Sprite> getEnvironment() {
        return this.environment;
    }
    public ArrayList<Sprite> getTrapList() {
        return this.trapList;
    }

    public void playSound(String fileName) {
        try {
            File soundFile = new File("./audio/" + fileName);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            System.out.println("Erreur son : " + e.getMessage());
        }
    }
    public ArrayList<Sprite> getCoinList() { return coinList; }
    public void setCoinList(ArrayList<Sprite> coinList) { this.coinList = coinList; }
    public ArrayList<Sprite> getSwordList() { return swordList; }
    public void setSwordList(ArrayList<Sprite> swordList) { this.swordList = swordList; }
}
