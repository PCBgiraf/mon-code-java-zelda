import java.awt.Image;
import java.util.ArrayList;

public class BooEnemy extends DynamicSprite {

    public BooEnemy(double x, double y, Image image, double width, double height) {
        super(x, y, image, width, height);
        this.setSpriteSheetNumberOfColumn(1); //car image fixe
    }


    public void moveIfPossible(ArrayList<Sprite> environment) {
        //les mouvements sont donnés dans le physicEngine
    }
}