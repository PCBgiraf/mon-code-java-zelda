import java.awt.Graphics;
import java.awt.Image;

public class Sprite implements Displayable {
    // Attributs protégés (pour être accessibles par les classes filles comme SolidSprite)
    protected Image image;      // L'image visuelle [cite: 487]
    protected double x;         // Coordonnée horizontale [cite: 488]
    protected double y;         // Coordonnée verticale [cite: 488]
    protected double width;     // Largeur [cite: 489]
    protected double height;    // Hauteur [cite: 489]

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
    // Constructeur : initialise tous les attributs
    public Sprite(double x, double y, Image image, double width, double height) {
        this.x = x;
        this.y = y;
        this.image = image;
        this.width = width;
        this.height = height;
    }


    public void draw(Graphics g) {
        // Dessine l'image aux coordonnées (x, y) avec la taille (width, height)
        // Le dernier paramètre 'null' est l'ImageObserver (inutile ici)
        g.drawImage(image, (int)x, (int)y, (int)width, (int)height, null);
    }
    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}