import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Playground {
    // Liste pour les collisions (Murs, Arbres, Rochers)
    private ArrayList<Sprite> environment = new ArrayList<>();
    // Liste pour tout ce qui est visible (Tout le monde)
    private ArrayList<Sprite> spriteList = new ArrayList<>();
    private ArrayList<Sprite> coinList = new ArrayList<>();
    private ArrayList<Sprite> trapList = new ArrayList<>();
    private ArrayList<Sprite> stairList = new ArrayList<>();
    private ArrayList<Sprite> swordList = new ArrayList<>();
    private String destinationLevel;
    public Playground(String pathName, String nextLevelDest) {
        this.destinationLevel = nextLevelDest;
        try {
            final Image imageTree = ImageIO.read(new File("./img/tree.png"));
            final Image imageGrass = ImageIO.read(new File("./img/grass.png"));
            final Image imageRock = ImageIO.read(new File("./img/rock.png"));
            final Image imageTrap = ImageIO.read(new File("./img/trap.png"));
            final Image imageStair = ImageIO.read(new File("./img/stair.png"));
            final Image imageLacV = ImageIO.read(new File("./img/lac.png"));
            final Image imagePontH = ImageIO.read(new File("./img/pont.png"));
            final Image imageSol = ImageIO.read(new File("./img/sol.png"));
            final Image imageSword = ImageIO.read(new File("./img/mastersword.png"));

            final int imageTreeWidth = imageTree.getWidth(null);
            final int imageTreeHeight = imageTree.getHeight(null);
            final int imageGrassWidth = imageGrass.getWidth(null);
            final int imageGrassHeight = imageGrass.getHeight(null);
            final int imageRockWidth = imageRock.getWidth(null);
            final int imageRockHeight = imageRock.getHeight(null);
            final int imageTrapWidth = imageTrap.getWidth(null);
            final int imageTrapHeight = imageTrap.getHeight(null);
            final int imageStairWidth = imageStair.getWidth(null);
            final int imageStairHeight = imageStair.getHeight(null);


            BufferedReader bufferedReader = new BufferedReader(new FileReader(pathName));
            String line = bufferedReader.readLine();
            int lineNumber = 0;
            int columnNumber = 0;

            BufferedImage originalGanon = ImageIO.read(new File("./img/ganon.png"));
            int targetWidth = 128;
            int targetHeight = 128;

            // On crée une image vide à la bonne taille
            BufferedImage resizedGanon = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = resizedGanon.createGraphics();
            g2d.drawImage(originalGanon.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH), 0, 0, null);
            g2d.dispose();
            final Image imageGanon = resizedGanon;

            while (line != null) {
                for (byte element : line.getBytes(StandardCharsets.UTF_8)) {
                    switch (element) {
                        case 'T': // ARBRE (Solide)
                            SolidSprite tree = new SolidSprite(columnNumber * imageTreeWidth,
                                    lineNumber * imageTreeHeight,
                                    imageTree, imageTreeWidth, imageTreeHeight);
                            spriteList.add(tree);    // On le voit
                            environment.add(tree);   // On le touche
                            break;

                        case '_': // HERBE (Pas solide)
                            Sprite grass = new Sprite(columnNumber * imageGrassWidth,
                                    lineNumber * imageGrassHeight,
                                    imageGrass, imageGrassWidth, imageGrassHeight);
                            spriteList.add(grass);   // On le voit
                            // PAS d'ajout dans environment (on marche dessus)
                            break;

                        case 'R': // ROCHER (Solide)
                            SolidSprite rock = new SolidSprite(columnNumber * imageRockWidth,
                                    lineNumber * imageRockHeight, imageRock, imageRockWidth, imageRockHeight);
                            spriteList.add(rock);    // On le voit
                            environment.add(rock);   // On le touche
                            break;
                        case '.': // SOL (Pas solide)
                            Sprite newSol = new Sprite(
                                    columnNumber * imageGrassWidth,
                                    lineNumber * imageGrassHeight,
                                    imageSol, //
                                    imageGrassWidth,
                                    imageGrassHeight);

                            spriteList.add(newSol);
                            break;
                        case 'L': // LAC V (solide)
                            SolidSprite newLac = new SolidSprite(
                                    columnNumber * imageGrassWidth,
                                    lineNumber * imageGrassHeight,
                                    imageLacV,
                                    imageGrassWidth,                 // Largeur (48)
                                    imageGrassHeight);               // Hauteur (48)

                            spriteList.add(newLac);
                            environment.add(newLac);
                            break;

                        case 'B': // PONT H (Pas solide)
                            Sprite newPont = new Sprite(
                                    columnNumber * imageGrassWidth,
                                    lineNumber * imageGrassHeight,
                                    imagePontH, //
                                    imageGrassWidth,
                                    imageGrassHeight);

                            spriteList.add(newPont);
                            break;
                        case 'P': // PIÈGE
                            Trap trap = new Trap(columnNumber * imageTrapWidth,
                                    lineNumber * imageTrapHeight, imageTrap, imageTrapWidth, imageTrapHeight);

                            spriteList.add(trap); // Pour qu'il soit dessiné
                            trapList.add(trap);   // Pour la logique
                            break;

                        case 'S': // ESCALIER
                            spriteList.add(new Sprite(columnNumber * imageGrassWidth, lineNumber * imageGrassHeight, imageGrass, imageGrassWidth, imageGrassHeight));
                            Stair newStair = new Stair(columnNumber * imageStairWidth,
                                    lineNumber * imageStairHeight,
                                    imageStair,
                                    imageStairWidth,
                                    imageStairHeight,
                                    this.destinationLevel); //Destination

                            stairList.add(newStair);  // Pour la logique
                            spriteList.add(newStair); // Pour le dessin
                            break;
                        case 'M': // RETOUR AU MENU
                            spriteList.add(new Sprite(columnNumber * imageGrassWidth, lineNumber * imageGrassHeight, imageGrass, imageGrassWidth, imageGrassHeight));
                            Stair menuStair = new Stair(
                                    columnNumber * imageGrassWidth,
                                    lineNumber * imageGrassHeight,
                                    imageStair,
                                    imageGrassWidth,
                                    imageGrassHeight,
                                    "MENU");

                            stairList.add(menuStair);
                            spriteList.add(menuStair);
                            break;
                        case 'C': // PIECE
                            spriteList.add(new Sprite(columnNumber * imageGrassWidth,
                                    lineNumber * imageGrassHeight,
                                    imageGrass, imageGrassWidth, imageGrassHeight));
                            try {
                                BufferedImage coinSheet = ImageIO.read(new File("./img/coin.png"));

                                // On crée la pièce aux coordonnées actuelles de la boucle (sur une case de 48x48)
                                Coin newCoin = new Coin(
                                        columnNumber * imageGrassWidth,
                                        lineNumber * imageGrassHeight,coinSheet,imageGrassWidth,imageGrassHeight);
                                spriteList.add(newCoin);coinList.add(newCoin);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case 'G': // Ganon (Ancrage en haut à gauche)
                            spriteList.add(new Sprite(columnNumber * imageGrassWidth, lineNumber * imageGrassHeight,imageSol, 128, 128));
                            SolidSprite solidGanon = new SolidSprite(columnNumber * imageGrassWidth, lineNumber * imageGrassHeight, imageGanon, 128, 128);
                            spriteList.add(solidGanon);
                            environment.add(solidGanon);
                            break;
                        case 'E': // Mastersword
                            spriteList.add(new Sprite(columnNumber * imageGrassWidth, lineNumber * imageGrassHeight, imageGrass, imageGrassWidth, imageGrassHeight));
                            MasterSword sword = new MasterSword(columnNumber * imageGrassWidth, lineNumber * imageGrassHeight, imageSword, imageGrassWidth, imageGrassHeight);

                            spriteList.add(sword); // Pour qu'elle soit dessinée
                            swordList.add(sword);  // Pour que le moteur physique sache où elle est
                            break;
                    }
                    columnNumber++;
                }
                columnNumber = 0;
                lineNumber++;
                line = bufferedReader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Sprite> getSolidSpriteList() {
        ArrayList<Sprite> solidSpriteArrayList = new ArrayList<>();
        for (Sprite sprite : environment) {
            if (sprite instanceof SolidSprite) solidSpriteArrayList.add(sprite);
        }
        return solidSpriteArrayList;
    }

    public ArrayList<Displayable> getSpriteList() {
        ArrayList<Displayable> displayableArrayList = new ArrayList<>();
        // On parcourt spriteList (tout) et non environment (juste les murs)
        for (Sprite sprite : spriteList) {
            displayableArrayList.add((Displayable) sprite);
        }
        return displayableArrayList;
    }

    public ArrayList<Sprite> getTrapList() {
        return trapList;
    }
    public ArrayList<Sprite> getStairList() {
        return stairList;
    }
    public ArrayList<Sprite> getCoinList() { return coinList; }
    public ArrayList<Sprite> getSwordList() { return swordList; }
}