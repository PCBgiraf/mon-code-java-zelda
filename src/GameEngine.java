import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

public class GameEngine implements Engine, KeyListener {
    // Référence vers le héros pour pouvoir le contrôler
    private DynamicSprite hero;
    private RenderEngine renderEngine;
    private Image swordNorth;
    private Image swordSouth;
    private Image swordEast;
    private Image swordWest;

    public GameEngine(DynamicSprite hero) {
        this.hero = hero;
    }

    public void update() {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        // on détecte l'appui sur une touche
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                hero.setDirection(Direction.NORTH);
                hero.setWalking(true);
                break;
            case KeyEvent.VK_DOWN:
                hero.setDirection(Direction.SOUTH);
                hero.setWalking(true);
                break;
            case KeyEvent.VK_LEFT:
                hero.setDirection(Direction.WEST);
                hero.setWalking(true);
                break;
            case KeyEvent.VK_RIGHT:
                hero.setDirection(Direction.EAST);
                hero.setWalking(true);
                break;
            case KeyEvent.VK_SPACE: // dégainer épée
                /*
                --- 1. Condition de sécurité : Link doit posséder l'épée ---
                 */
                if (hero.getSwordObtainedTime() == 0) {
                    System.out.println("Vous n'avez pas encore récupéré la Mastersword !");
                    return;
                }
                if (System.currentTimeMillis() - hero.getLastAttackTime() < 700) {
                    return; //on donne un cooldown à l'attaque
                }
                // On met immédiatement à jour l'heure de la nouvelle attaque :
                hero.setLastAttackTime(System.currentTimeMillis());
                new AudioPlayground().playSound("./audio/cri_link.wav");

                Direction linkDirection = hero.getDirection();
                Image swordImage = null;
                double attackX = hero.getX();
                double attackY = hero.getY();
                double gridSize = 48;

                switch (linkDirection) {
                    case NORTH: // Case AU-DESSUS de Link
                        swordImage = swordNorth;
                        attackY -= gridSize;
                        break;
                    case SOUTH:
                        swordImage = swordSouth;
                        attackY += gridSize;
                        break;
                    case EAST:
                        swordImage = swordEast;
                        attackX += gridSize;
                        break;
                    case WEST:
                        swordImage = swordWest;
                        attackX -= gridSize;
                        break;
                }

                /*
                --- ajout de l'attaque ---
                 */
                if (swordImage != null) {

                    SwordAttack attack = new SwordAttack(
                            attackX,
                            attackY,
                            swordImage,
                            gridSize,
                            gridSize);
                    renderEngine.getRenderList().add(attack);
                }
                break;
        }
    }
    public void setAttackTools(RenderEngine render, java.awt.Image n, java.awt.Image s, java.awt.Image e, java.awt.Image w) {
        this.renderEngine = render;
        this.swordNorth = n;
        this.swordSouth = s;
        this.swordEast = e;
        this.swordWest = w;
    }
    public void keyReleased(KeyEvent e) {
        //héros s'arrête quand on relâche la touche
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
                hero.setWalking(false);
                break;
        }
    }
}