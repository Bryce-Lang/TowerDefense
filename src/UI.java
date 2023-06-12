import com.raylib.java.Raylib;
import com.raylib.java.core.Color;

public class UI {
    private int screenWidth;
    private int screenHeight;
    private Raylib rlj;
    
    public UI(int screenWidth, int screenHeight, Raylib rlj) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.rlj = rlj;
    }
    
    public void renderTitleScreen() {
        rlj.shapes.DrawRectangle(0, 0, screenWidth, screenHeight, Color.GREEN);
        rlj.text.DrawText("TOWERS", 20, 20, 40, Color.DARKGREEN);
        rlj.text.DrawText("PRESS ENTER or TAP to JUMP to GAMEPLAY SCREEN", 120, 220, 20, Color.DARKGREEN);
        // Draw other UI elements specific to the title screen
    }
    
    public void renderHelpScreen() {
        // Draw UI elements specific to the help screen
    }
    
    public void renderGameplayScreen() {
        rlj.shapes.DrawRectangle(0, 0, screenWidth, screenHeight, Color.PURPLE);
        // Draw UI elements specific to the gameplay screen
        // Draw the map and other gameplay elements
    }
    
    // Other methods for handling button clicks or interactions
}
