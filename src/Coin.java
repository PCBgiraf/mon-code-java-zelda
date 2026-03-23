import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Coin extends Sprite {
    private long firstTouchTime = 0;
    private boolean isBeingTouched = false;
    private boolean collected = false;

    // Le "Getter" pour savoir si elle est ramassée
    public boolean isCollected() {
        return collected;
    }
    public void updatePickupLogic(boolean currentlyTouching) {
        if (currentlyTouching && !collected) {
            // Si Link vient juste de poser le pied dessus
            if (!isBeingTouched) {
                isBeingTouched = true;
                firstTouchTime = System.currentTimeMillis(); // On lance le chrono
            } else {
                // S'il est toujours dessus, on vérifie si 100ms se sont écoulées
                if (System.currentTimeMillis() - firstTouchTime > 100) {
                    this.collected = true; // La pièce est officiellement ramassée
                }
            }
        } else {
            // Si Link s'en va avant les 100ms, on remet à zéro !
            isBeingTouched = false;
        }
    }


    private BufferedImage fullSpriteSheet;
    private int currentFrame = 0;
    private int maxFrames = 7;
    // Les points de départ (X1,X2,...)
    private int[] frameX = {30, 110, 175, 238, 266, 325, 392};
    // Les largeurs
    private int[] frameWidths = {70, 55, 50, 12, 44, 55, 68};

    private int frameY = 50;
    private int frameHeight = 70;

    // Le chronomètre pour la vitesse
    private int animationTimer = 0;
    private int animationSpeed = 6;

    public Coin(double x, double y, BufferedImage spriteSheet, double renderWidth, double renderHeight) {
        super(x, y, null, renderWidth, renderHeight);
        this.fullSpriteSheet = spriteSheet;
    }

    public void draw(Graphics g) {
        int currentX = frameX[currentFrame];
        int currentW = frameWidths[currentFrame];

        BufferedImage currentImage = fullSpriteSheet.getSubimage(currentX, frameY, currentW, frameHeight);

        // On calcule le centrage pour ne pas que la pièce tremble
        // On garde les proportions de la pièce par rapport à la taille de la case (48x48)
        int destHeight = (int) getHeight();
        int destWidth = (currentW * destHeight) / frameHeight;

        // On calcule le décalage pour la centrer au milieu de la case
        int offsetX = ((int) getWidth() - destWidth) / 2;

        g.drawImage(currentImage, (int) getX() + offsetX, (int) getY(), destWidth, destHeight, null);

        animationTimer++;
        if (animationTimer >= animationSpeed) {
            currentFrame++;
            if (currentFrame >= maxFrames) {
                currentFrame = 0;
            }
            animationTimer = 0;
        }
    }
}