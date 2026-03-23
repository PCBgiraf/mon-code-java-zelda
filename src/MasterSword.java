import java.awt.Image;

public class MasterSword extends Sprite {
    private boolean collected = false;

    public MasterSword(double x, double y, Image image, double width, double height) {
        super(x, y, image, width, height);
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }
}