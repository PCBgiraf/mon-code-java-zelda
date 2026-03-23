import java.awt.Image;
import java.util.ArrayList;
import java.awt.Graphics;

public class SimpleEnemy extends DynamicSprite {

    // Compteur pour ne pas changer de direction trop souvent
    private int timeSinceLastChange = 0;
    private final int CHANGE_DIRECTION_TIME = 50; // Changer toutes les 50 frames (env. 2.5 secondes)

    public SimpleEnemy(double x, double y, Image image, double width, double height) {
        super(x, y, image, width, height);
    }

    public void draw(Graphics g) {
        // On dessine l'image entière aux coordonnées x, y
        // Sans essayer de calculer une ligne ou une colonne
        g.drawImage(image, (int) x, (int) y, (int) width, (int) height, null);
    }

    //mouvement de l'ennemi
    public void moveIfPossible(ArrayList<Sprite> environment) {

        boolean isBlocked = !isMovingPossible(environment);

        // Si on est bloqué par un mur OU si ça fait longtemps qu'on marche tout droit
        if (isBlocked || timeSinceLastChange > CHANGE_DIRECTION_TIME) {
            chooseRandomDirection();
            timeSinceLastChange = 0; // On remet le compteur à zéro
        } else {
            timeSinceLastChange++; // On incrémente le compteur
        }

        // 2. On effectue le mouvement réel (avec la méthode du parent qui gère les collisions)
        super.moveIfPossible(environment);
    }

    private void chooseRandomDirection() {
        // Math.random() renvoie un chiffre entre 0.0 et 1.0
        // On multiplie par 4 pour avoir une valeur entre 0 et 3.99
        int randomDir = (int) (Math.random() * 4);

        switch (randomDir) {
            case 0: setDirection(Direction.NORTH); break;
            case 1: setDirection(Direction.SOUTH); break;
            case 2: setDirection(Direction.EAST); break;
            case 3: setDirection(Direction.WEST); break;
        }
    }
}