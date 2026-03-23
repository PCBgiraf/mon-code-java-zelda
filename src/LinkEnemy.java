

import java.awt.Image;
import java.util.ArrayList;

public class LinkEnemy extends DynamicSprite {

    private int timeSinceLastChange = 0;
    private final int CHANGE_DIRECTION_TIME = 50; // Change de direction toutes les 2.5 sec environ

    public LinkEnemy(double x, double y, Image image, double width, double height) {
        super(x, y, image, width, height);
        // On définit par défaut que c'est une spritesheet de 10 colonnes (comme Link)
        this.setSpriteSheetNumberOfColumn(10);
    }

    public void moveIfPossible(ArrayList<Sprite> environment) {
        // 1. Intelligence Artificielle (Copiée de SimpleEnemy)
        boolean isBlocked = !isMovingPossible(environment);

        if (isBlocked || timeSinceLastChange > CHANGE_DIRECTION_TIME) {
            chooseRandomDirection();
            timeSinceLastChange = 0;
        } else {
            timeSinceLastChange++;
        }

        // 2. Mouvement réel (On appelle le parent qui gère aussi l'animation !)
        super.moveIfPossible(environment);
    }

    private void chooseRandomDirection() {
        int randomDir = (int) (Math.random() * 4);
        switch (randomDir) {
            case 0: setDirection(Direction.NORTH); break;
            case 1: setDirection(Direction.SOUTH); break;
            case 2: setDirection(Direction.EAST); break;
            case 3: setDirection(Direction.WEST); break;
        }
    }
}