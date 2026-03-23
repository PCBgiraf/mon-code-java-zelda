import java.awt.Image;

public class SwordAttack extends Sprite {
    // Le chronomètre de vie (en millisecondes)
    private long creationTime;
    private final long lifetime = 700; // L'épée restera visible 0.7 seconde
    private boolean readyToBeCleaned = false;

    public SwordAttack(double x, double y, Image image, double width, double height) {
        super(x, y, image, width, height);
        // On note l'heure exacte de sa naissance
        this.creationTime = System.currentTimeMillis();
    }

    public void draw(java.awt.Graphics g) {  //uniquement pour le renderEngine pour bien afficher l'épée
        super.draw(g);
        this.readyToBeCleaned = true;
    }


    public boolean isExpired() {
        return (System.currentTimeMillis() - creationTime > lifetime) && readyToBeCleaned;
    }
}