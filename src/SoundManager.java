import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundManager {
    public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        // Load the audio file
        File file = new File("sounds/Space Rap.wav");
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);

        // Set the track to loop continuously
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        clip.start();

        // Keep the program running while your game is active
        while (true) {
            // Game logic and other code here
        }
    }
    
    static void playBackgroundMusic(float volume) {
        try {
            File file = new File("sounds/Space Rap.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            // Check if the clip supports volume control
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                // Set the volume (gain) in decibels
                gainControl.setValue(volume);
            }

            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }


}
