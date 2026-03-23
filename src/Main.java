import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

    public class Main {
        JFrame displayZoneFrame;
        Image wastedImage = null;
        Image backgroundImage = null;
        RenderEngine renderEngine;
        DynamicSprite hero;
        PhysicEngine physicEngine;
        GameEngine gameEngine;
        AudioPlayground audioPlayground;
        private BufferedImage swordSheet;
        private BufferedImage swordNorth, swordSouth, swordEast, swordWest;

        public Main() throws Exception {
            displayZoneFrame = new JFrame("Java Labs");
            displayZoneFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            displayZoneFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);

            renderEngine = new RenderEngine();
            physicEngine = new PhysicEngine();

            //on charge l'image wasted
            wastedImage = ImageIO.read(new File("./img/wasted.png"));
            backgroundImage = ImageIO.read(new File("./img/zelda_fond_ecran.png"));
            // La donner au moteur de rendu
            renderEngine.setWastedImage(wastedImage);
            renderEngine.setBackgroundImage(backgroundImage);
            physicEngine.setRenderEngine(renderEngine);

            hero = new DynamicSprite(600, 250,
                    ImageIO.read(new File("./img/heroTileSheetLowRes.png")), 48, 50);
            hero.setHero(true);
            gameEngine = new GameEngine(hero);
            /*
            ---image mastersword : attaque ---
             */
            try {
                java.awt.image.BufferedImage swordSheet = javax.imageio.ImageIO.read(new java.io.File("./img/swordTileSheet.png"));
                // avec paint on regarde les dimensions et coordonnées de chaque épée
                int northX = 0;   // coordonnées coin supérieur gauche
                int northY = 197;   // coordonnées coin supérieur gauche
                int northWidth = 130;  // largeur
                int northHeight = 182; // hauteur
                java.awt.Image sNorth = swordSheet.getSubimage(northX, northY, northWidth, northHeight);

                int southX =0;
                int southY =0;
                int southWidth = 130;
                int southHeight = 188;
                java.awt.Image sSouth = swordSheet.getSubimage(southX, southY, southWidth, southHeight);


                int eastX = 160;
                int eastY = 0;
                int eastWidth = 233;
                int eastHeight = 150;
                java.awt.Image sEast  = swordSheet.getSubimage(eastX, eastY, eastWidth, eastHeight);


                int westX = 170;
                int westY = 230;
                int westWidth = 222;
                int westHeight = 110;
                java.awt.Image sWest  = swordSheet.getSubimage(westX, westY, westWidth, westHeight);



                gameEngine.setAttackTools(renderEngine, sNorth, sSouth, sEast, sWest);

            } catch (Exception e) {
                System.out.println("Erreur chargement épée : " + e.getMessage());
            }

            Playground playground = new Playground("./data/level2.txt", "level3.txt");
            for (Displayable d : playground.getSpriteList()) { // prend en arg la liste et renvoie toute la liste
                renderEngine.addToRenderList(d);
            }
            physicEngine.setHero(hero);
            renderEngine.addToRenderList(hero);
            physicEngine.setEnvironment(playground.getSolidSpriteList());
            physicEngine.addToMovingSpriteList(hero);
            physicEngine.setTrapList(playground.getTrapList());
            physicEngine.setCoinList(playground.getCoinList());
            for (Sprite s : playground.getStairList()) {
                physicEngine.addToStairList((Stair) s);
            }
            this.audioPlayground = new AudioPlayground();
            audioPlayground.playBackgroundMusic("./audio/menu.wav");
            try {
                BufferedImage tileSheet = ImageIO.read(new File("./img/tileSetCompleted.png"));
                int gridUnit = 16;
                int spacing = 1;

                // 3. Choisir l'ennemi (Le chevalier rouge en bas à droite)
                int column = 27;
                int row = 39;

                // 4. Calcul des coordonnées pixels
                int xgrid = column * (gridUnit + spacing);
                int ygrid = row * (gridUnit + spacing);

                // 5. Découpage
                BufferedImage enemySprite = tileSheet.getSubimage(xgrid, ygrid, 48, 48);

                // 6. Création de l'ennemi dans le jeu
                int numberOfEnemies = 5;

                for (int i = 0; i < numberOfEnemies; i++) {
                    // 1. Calcul d'une position aléatoire pour chaque ennemi
                    // (Math.random() * 400) donne un nombre entre 0 et 400
                    double x = Math.random() * 500;
                    double y = Math.random() * 200;

                    // 2. Création des ennemies
                    SimpleEnemy enemy = new SimpleEnemy(x, y, enemySprite, 48, 48);

                    LinkEnemy badLink = new LinkEnemy(300, 100, ImageIO.read(new File("./img/dark_link_lowres.png")), 48, 50);
                    renderEngine.addToRenderList(badLink);
                    physicEngine.addToEnemyList(badLink);


                    // 3. Configuration pour ne pas qu'il clignote (image fixe)
                    enemy.setSpriteSheetNumberOfColumn(1);
                    renderEngine.addToRenderList(enemy);
                    physicEngine.addToEnemyList(enemy);
                }
                Image booImage = ImageIO.read(new File("./img/boo.png"));
                for (int i = 0; i < 3; i++) {
                    BooEnemy monBoo = new BooEnemy(100 + 150 * i, 50 * i, booImage, 48, 48);
                    renderEngine.addToRenderList(monBoo);
                    physicEngine.addToEnemyList(monBoo);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            // 1. TIMER DE RENDU
            Timer renderTimer = new Timer(50, (time) -> {
                if (renderEngine.isGameOver()) {
                    ((Timer) time.getSource()).stop();
                } else {
                    renderEngine.update();
                }

                renderEngine.getRenderList().removeIf(d -> (d instanceof Coin) && ((Coin) d).isCollected());
                renderEngine.getRenderList().removeIf(d -> (d instanceof MasterSword) && ((MasterSword) d).isCollected());
                renderEngine.getRenderList().removeIf(d -> (d instanceof SwordAttack) && ((SwordAttack) d).isExpired());
                renderEngine.repaint();
            });

            // 2. TIMER DE JEU (Changement de niveau, Game Over)
            Timer gameTimer = new Timer(50, (time) -> {

                // Priorité 1 : Est-ce que c'est Game Over ?
                if (renderEngine.isGameOver()) {
                    audioPlayground.stopBackgroundMusic();
                    audioPlayground.playSound("./audio/death_sound.wav");
                    ((Timer) time.getSource()).stop();
                    return; // On arrête de lire ce bloc ici
                }

                // Priorité 2 : Est-ce qu'on doit changer de niveau ?
                else if (physicEngine.getNextLevelToLoad() != null) {
                    String levelName = physicEngine.getNextLevelToLoad();

                    // On coupe le timer temporairement
                    ((Timer) time.getSource()).stop();

                    switchLevel(levelName);

                    // On le relance
                    ((Timer) time.getSource()).start();

                    physicEngine.resetLevelLoad();
                    return; // Important : on ne fait rien d'autre ce tour-ci
                }

                // Priorité 3 : Si tout va bien, on joue !
                else {
                    gameEngine.update();
                }
            });

            // 3. TIMER PHYSIQUE
            Timer physicTimer = new Timer(50, (time) -> {
                if (renderEngine.isGameOver()) {
                    ((Timer) time.getSource()).stop();
                } else {
                    //sans la page d'accueil on écrit juste : physicEngine.update();
                    if (!renderEngine.isMenu) {
                        physicEngine.update(); // La physique ne tourne que si on joue
                    }
                    renderEngine.repaint(); // Le dessin tourne tout le temps
                }
            });

            renderTimer.start();
            gameTimer.start();
            physicTimer.start();

            displayZoneFrame.getContentPane().add(renderEngine);
            displayZoneFrame.addKeyListener(gameEngine);
            // dans le menu on veut que la souris soit détectée et clique bien dans les cases
            renderEngine.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mousePressed(java.awt.event.MouseEvent e) {
                    // On vérifie si on est bien sur l'écran du menu
                    if (renderEngine.isMenu) {
                        int mouseX = e.getX();
                        int mouseY = e.getY();

                        // case 1 : didacticiel
                        if (mouseX >= 300 && mouseX <= 500 && mouseY >= 200 && mouseY <= 250) {
                            renderEngine.isMenu = false;
                            switchLevel("level1.txt");
                        }

                        // case 2 : Campagne
                        else if (mouseX >= 300 && mouseX <= 500 && mouseY >= 300 && mouseY <= 350) {
                            renderEngine.isMenu = false;
                            switchLevel("level2.txt");
                        }
                    }
                }
            });
            displayZoneFrame.setVisible(true);
        }

        /*
        --- Mécanique de Jeu pour le MODE Campagne : placement aléatoires des monstres, usage des escaliers,
        On clear les monstres entre chaque niveau et on fait réapparaître les pièces ---
         */

        public void switchLevel(String levelName) {
            System.out.println("Chargement du niveau : " + levelName);
            //Son du jeu menu/tutoriel/campagne
            if (audioPlayground != null // On arrête la musique précédente pour éviter la superposition
                    // on veut que la musique continue meme en changeant de niveau
                    && !levelName.equals("level3.txt")
                    && !levelName.equals("level4.txt")) {
                audioPlayground.stopBackgroundMusic();
            }
            if (levelName.equals("MENU")) {
                audioPlayground.playBackgroundMusic("./audio/menu.wav");
                renderEngine.isMenu = true;
                hero.setX(600); hero.setY(250); //il faut replacer le héros sinon conflit avec l'escalier
                return;
            }
            else if (levelName.equals("level1.txt")) {
                audioPlayground.playBackgroundMusic("./audio/tutoriel.wav");
            }
            else if (!levelName.equals("level3.txt")
                    && !levelName.equals("level4.txt")) {
                audioPlayground.playBackgroundMusic("./audio/Kass_-Theme_wav.wav");
            }
            try {
                //On vide l'ancien niveau
                renderEngine.getRenderList().clear();
                physicEngine.getEnemyList().clear();
                physicEngine.getStairList().clear();
                physicEngine.getEnvironment().clear();
                physicEngine.getTrapList().clear();
                physicEngine.getCoinList().clear();
                if (physicEngine.getSwordList() != null) {
                    physicEngine.getSwordList().clear();
                }



                //destination du prochain niveau n+1
                String destination = "level2.txt";
                if (levelName.equals("level2.txt")) {
                    destination = "level3.txt";
                }
                else if (levelName.equals("level3.txt")) {
                    destination = "level4.txt";
                }
                //On charge le Playground avec le bon niveau et la bonne destination
                Playground newLevel = new Playground("./data/" + levelName, destination);

                // 3. On ajoute les décors
                for (Displayable d : newLevel.getSpriteList()) {
                    renderEngine.addToRenderList(d);
                }
                physicEngine.setEnvironment(newLevel.getSolidSpriteList());
                physicEngine.setTrapList(newLevel.getTrapList());
                physicEngine.setCoinList(newLevel.getCoinList());
                physicEngine.setSwordList(newLevel.getSwordList());

                for (Sprite s : newLevel.getStairList()) {
                    physicEngine.addToStairList((Stair) s);
                }

                // 4. On remet le héros par-dessus le décor
                renderEngine.addToRenderList(hero);
                if (!levelName.equals("level1.txt") && !levelName.equals("MENU")) {
                    //on fait apparaître les monstres
                    BufferedImage tileSheet = ImageIO.read(new File("./img/tileSetCompleted.png"));
                    int gridUnit = 16;
                    int spacing = 1;
                    BufferedImage enemySprite = tileSheet.getSubimage(27 * (gridUnit + spacing), 39 * (gridUnit + spacing), 48, 48);

                    // On remet 5 ennemis normaux
                    for (int i = 0; i < 5; i++) {
                        double x = Math.random() * 500;
                        double y = Math.random() * 200;
                        SimpleEnemy enemy = new SimpleEnemy(x, y, enemySprite, 48, 48);
                        enemy.setSpriteSheetNumberOfColumn(1);
                        renderEngine.addToRenderList(enemy);
                        physicEngine.addToEnemyList(enemy);
                    }

                    // On remet un Dark Link
                    LinkEnemy badLink = new LinkEnemy(300, 100, ImageIO.read(new File("./img/dark_link_lowres.png")), 48, 50);
                    renderEngine.addToRenderList(badLink);
                    physicEngine.addToEnemyList(badLink);
                    // On remet Boo
                    Image booImage = ImageIO.read(new File("./img/boo.png"));
                    for (int i = 0; i < 3; i++) {
                        BooEnemy monBoo = new BooEnemy(400 + 50 * i, 100 + 50 * i, booImage, 48, 48);
                        renderEngine.addToRenderList(monBoo);
                        physicEngine.addToEnemyList(monBoo);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public static void main(String[] args) throws Exception {
            Main main = new Main();
        }



    }




