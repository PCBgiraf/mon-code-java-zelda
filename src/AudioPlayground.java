import javax.sound.sampled.*;
import java.io.File;

public class AudioPlayground {
    private Clip clip;

    // Jouer un son une seule fois (ex: bruitage, explosion)
    public void playSound(String fileName) {
        try {
            File soundFile = new File(fileName);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip tempClip = AudioSystem.getClip();
            tempClip.open(audioIn);
            tempClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Jouer une musique en boucle (ex: musique de fond)
    public void playBackgroundMusic(String fileName) {
        try {
            File soundFile = new File(fileName);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);

            // 2. On utilise l'attribut de la classe 'this.clip'
            this.clip = AudioSystem.getClip();
            this.clip.open(audioIn);
            this.clip.loop(Clip.LOOP_CONTINUOUSLY);
            this.clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void stopBackgroundMusic() {
        if (this.clip != null && this.clip.isRunning()) {
            this.clip.stop();
        }
    }
}
