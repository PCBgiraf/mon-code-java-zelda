import java.awt.Image;

public class Stair extends Sprite {
    // Variable pour savoir vers quel niveau cet escalier nous emmène
    private String nextLevel;

    public Stair(double x, double y, Image image, double width, double height, String nextLevel) {
        super(x, y, image, width, height);
        this.nextLevel = nextLevel;
    }

    public String getNextLevel() {
        return nextLevel;
    }
}