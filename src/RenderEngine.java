import javax.swing.JPanel;
import java.awt.*;
import java.util.ArrayList;

public class RenderEngine extends JPanel implements Engine {
    // Liste des éléments à afficher sur l'écran
    private ArrayList<Displayable> renderList;
    public boolean isMenu = true; // Par défaut, quand on lance le jeu, on est dans le menu !
    private boolean isGameOver = false; // Par défaut, le jeu n'est pas fini
    private Image wastedImage = null;   // L'image à afficher
    private Image backgroundImage = null;
    public void setGameOver(boolean gameOver) {
        this.isGameOver = gameOver;
    }
    // Permet au Main de savoir si le jeu est fini
    public boolean isGameOver() {
        return this.isGameOver;
    }
    public void setWastedImage(Image wastedImage) {
        this.wastedImage = wastedImage;
    }
    public void setBackgroundImage(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
    }
    public RenderEngine() {
        // Initialisation de la liste dans le constructeur
        this.renderList = new ArrayList<>();
    }

    public void update() {
        // La méthode update demande à Swing de redessiner le composant
        this.repaint();
    }

    public void paint(Graphics g) {
        // 1. On nettoie l'écran
        super.paint(g);
        //affichage game over
        if (isGameOver) {
            if (wastedImage != null) {
                g.drawImage(wastedImage, 0, 0, getWidth(), getHeight(), null);
            }
            return;
        }
        else if (isMenu) {
            // --- DESSIN DU MENU ---
            g.drawImage(backgroundImage,0,0,getWidth(),getHeight(),null);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Welcome to THE LEGEND OF ZELDA", 300, 100); // Titre
            g.setFont(new Font("Arial", Font.BOLD, 25));
            g.drawString("Créé par Loysel Antoine 1G1TD1TP2", 300, 140);
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 25));
            g.drawString("N'hésitez pas à appuyer sur SHIFT pour courir", 400, 700);
            g.drawString("La mastersword ne fonctionne pas encore contre les ennemies", 380, 730);
            // BOUTON 1 : DIDACTICIEL (Rectangle de X=300, Y=200, Largeur=200, Hauteur=50)
            g.setColor(Color.DARK_GRAY);
            g.fillRect(300, 200, 200, 50);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Didacticiel", 345, 233);

            // BOUTON 2 : CAMPAGNE (Rectangle de X=300, Y=300, Largeur=200, Hauteur=50)
            g.setColor(Color.DARK_GRAY);
            g.fillRect(300, 300, 200, 50);
            g.setColor(Color.WHITE);
            g.drawString("Campagne", 345, 333);
        }
        else {
            //si ni game over ni menu, alors le jeu dessine les objets (herbe,rocher,héros,...)
            for (Displayable renderable : renderList) {
                renderable.draw(g);
            }
            for (Displayable s : renderList) {
                if (s instanceof DynamicSprite) {
                    DynamicSprite dSprite = (DynamicSprite) s;

                    long currentTime = System.currentTimeMillis();
                    long timeSinceObtention = currentTime - dSprite.getSwordObtainedTime();
                    /*
                    ---Obtention Épée---
                     */
                    if (dSprite.isHero() && timeSinceObtention > 500 && timeSinceObtention < 2500) {

                        g.setColor(Color.YELLOW);
                        g.setFont(new Font("Arial", Font.BOLD, 16));
                        String text = "Cliquer sur espace pour attaquer";
                        g.drawString(text, (int) dSprite.getX() - 50, (int) dSprite.getY() - 20);
                    }
                    /*
                    ---Obtention Pièce---
                     */
                    if (dSprite.isHero() && (System.currentTimeMillis() - dSprite.getLastCoinTime() < 700)) {
                        g.setColor(Color.YELLOW);
                        g.setFont(new Font("Arial", Font.BOLD, 16));
                        String text = "Coin : " + dSprite.getCoinScore() + "/7";
                        g.drawString(text, (int) dSprite.getX() - 10, (int) dSprite.getY() - 10);
                    }
                    /*
                     --- TEXTE DE LA MASTERSWORD ---
                     */
                    if (dSprite.isHero() && (System.currentTimeMillis() - dSprite.getLastSwordMessageTime() < 700)) {
                        g.setColor(Color.RED);
                        g.setFont(new Font("Arial", Font.BOLD, 16));
                        String text = "Insuffisant : " + dSprite.getCoinScore() + "/7 pièces";
                        g.drawString(text, (int) dSprite.getX() - 10, (int) dSprite.getY() - 10);
                    }
                    if (dSprite.isHero() && dSprite.isStunned()) { //si Piège alors héros clignote
                        if (System.currentTimeMillis() % 200 < 100) {
                            continue;
                        }
                    }
                }
                s.draw(g);
            }

        }
    }
    public void addToRenderList(Displayable displayable) {
        this.renderList.add(displayable);
    }
    public ArrayList<Displayable> getRenderList() {
        return this.renderList;
    }
}
