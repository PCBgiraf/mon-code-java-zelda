import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javax.swing.Timer;

public class DynamicSprite extends SolidSprite {
    // Attributs spécifiques au mouvement et à l'animation
    private boolean isWalking = false;
    private double speed = 5;
    private int spriteSheetNumberOfColumn = 10;
    private int timeBetweenFrame = 200;
    private Direction direction = Direction.SOUTH; //direction par défaut
    private boolean isStunned = false;
    private boolean isOnTrap = false;
    private boolean isHero = false;
    private int coinScore = 0;
    private long lastCoinTime = 0;
    private long lastSwordMessageTime = 0;
    private long swordObtainedTime = 0;
    private long lastAttackTime = 0;


    public DynamicSprite(double x, double y, Image image, double width, double height) {
        super(x, y, image, width, height);
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }


    public void setWalking(boolean walking) {
        this.isWalking = walking;
    }


    public void moveIfPossible(ArrayList<Sprite> environment) {
        if (isStunned) {
            return; // SI ÉTOURDI, ON S'ARRÊTE LÀ. On ne lit pas la suite.
        }

        if (isMovingPossible(environment)) {
            move();
        }

    }

    AudioPlayground audioPlayground = new AudioPlayground();
    // Méthode pour activer le piège
    public void setStunned() {
        if (!isOnTrap && this.isHero) { // On active seulement si on vient d'entrer sur le piège
            isStunned = true;
            isOnTrap = true;
            System.out.println("PIÈGE ACTIVÉ ! Bloqué pour 3 secondes.");
            audioPlayground.playSound("./audio/Oracle_Boss_Hit.wav");
            // Timer pour débloquer après 3 secondes (3000 ms)
            Timer stunTimer = new Timer(3000, (e) -> {
                isStunned = false;
                System.out.println("LIBÉRÉ !");
                ((Timer)e.getSource()).stop(); // Arrête le timer une fois fini
            });
            stunTimer.setRepeats(false); // S'assure qu'il ne s'exécute qu'une fois
            stunTimer.start();
        }
    }
    public boolean isStunned() {
        return this.isStunned;
    }

    // Méthode appelée quand on sort du piège
    public void resetTrapState() {
        isOnTrap = false;
    }

    public void draw(Graphics g) {

        int index = (int) (System.currentTimeMillis() / timeBetweenFrame % spriteSheetNumberOfColumn);


        int attitude = direction.getFrameLineNumber();


        int dx1 = (int) x;
        int dy1 = (int) y;
        int dx2 = (int) (x + width);
        int dy2 = (int) (y + height);

        int sx1 = (int) (index * width);
        int sy1 = (int) (attitude * height);
        int sx2 = (int) ((index + 1) * width);
        int sy2 = (int) ((attitude + 1) * height);

        g.drawImage(image, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
    }
    private void move() {
        if (this.isHero && !this.isWalking) { return;}

        switch (direction) {
            case NORTH: this.y -= speed; break;
            case SOUTH: this.y += speed; break;
            case EAST:  this.x += speed; break;
            case WEST:  this.x -= speed; break;
        }
    }

    protected boolean isMovingPossible(ArrayList<Sprite> environment) {
        // Création de la hitbox future
        Rectangle2D.Double hitBox = new Rectangle2D.Double();

        switch (direction) {
            case NORTH: hitBox.setRect(super.x, super.y - speed, super.width, super.height); break;
            case SOUTH: hitBox.setRect(super.x, super.y + speed, super.width, super.height); break;
            case EAST:  hitBox.setRect(super.x + speed, super.y, super.width, super.height); break;
            case WEST:  hitBox.setRect(super.x - speed, super.y, super.width, super.height); break;
        }


        for (Sprite s : environment) {

            if ((s instanceof SolidSprite) && (s != this)) {

                Rectangle2D.Double solidHitbox = new Rectangle2D.Double(s.x, s.y, s.width, s.height);


                if (hitBox.intersects(solidHitbox)) {
                    return false;
                }
            }
        }
        return true;
    }
    public Direction getDirection() {
        return this.direction; // Renvoie la variable qui stocke la direction actuelle
    }
    public void setSpriteSheetNumberOfColumn(int numberOfColumn) {
        this.spriteSheetNumberOfColumn = numberOfColumn;
    }
    public void setHero(boolean isHero) {
        this.isHero = isHero;
    }
    public boolean isHero() { return isHero; }
    public int getCoinScore() { return coinScore; }
    public void setCoinScore(int coinScore) { this.coinScore = coinScore; }

    public long getLastCoinTime() { return lastCoinTime; }
    public void setLastCoinTime(long lastCoinTime) { this.lastCoinTime = lastCoinTime; }

    public long getLastSwordMessageTime() { return lastSwordMessageTime; }
    public void setLastSwordMessageTime(long time) { this.lastSwordMessageTime = time; }

    public long getSwordObtainedTime() { return swordObtainedTime; }
    public void setSwordObtainedTime(long time) { this.swordObtainedTime = time; }

    public long getLastAttackTime() { return lastAttackTime; }
    public void setLastAttackTime(long lastAttackTime) { this.lastAttackTime = lastAttackTime; }
}