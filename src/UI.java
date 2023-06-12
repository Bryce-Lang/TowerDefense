import com.raylib.java.Raylib;
import com.raylib.java.core.Color;
import com.raylib.java.core.input.Mouse.MouseButton;
import com.raylib.java.shapes.Rectangle;
import com.raylib.java.shapes.rShapes;


public class UI {
    private int screenWidth;
    private int screenHeight;
    private Raylib rlj;
    private Button playButton;
    private Button helpButton;

    public UI(int screenWidth, int screenHeight, Raylib rlj) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.rlj = rlj;
        
     // Initialize buttons
        playButton = new Button("PLAY", new Rectangle(100, 200, 200, 50), Color.RED);
        helpButton = new Button("HELP", new Rectangle(100, 300, 200, 50), Color.BLUE);
 
    }
    
    public void renderTitleScreen() {
        rlj.shapes.DrawRectangle(0, 0, screenWidth, screenHeight, Color.GREEN);
        rlj.text.DrawText("TOWERS", 20, 20, 40, Color.DARKGREEN);
       // rlj.text.DrawText("PRESS Play to GAMEPLAY SCREEN or Help for Controls", 120, 220, 20, Color.DARKGREEN);
        // Draw other UI elements specific to the title screen
     // Render the buttons
        playButton.render(rlj);
        helpButton.render(rlj);
        rlj.text.DrawText("PRESS Play to GAMEPLAY SCREEN or Help for Controls", 120, 120, 20, Color.DARKGREEN);
        
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
    public String handleInput() {
        if (rlj.core.IsMouseButtonPressed(MouseButton.MOUSE_BUTTON_LEFT)) {
            int mouseX = rlj.core.GetMouseX();
            int mouseY = rlj.core.GetMouseY();

            // Check if buttons are clicked
            if (playButton.isClicked(mouseX, mouseY)) {
                return "PLAY";
            } else if (helpButton.isClicked(mouseX, mouseY)) {
                return "HELP";
            }
        }

        return "";
    }

}

class Button {
    private String text;
    private Rectangle bounds;
    private Color color;
    private boolean isClicked;

    public Button(String text, Rectangle bounds, Color color) {
        this.text = text;
        this.bounds = bounds;
        this.color = color;
    }

    public void render(Raylib rl) {
        rShapes.DrawRectangleRec(bounds, color);
        rl.text.DrawText(text, (int)bounds.x + 10, (int)bounds.y + 10, 20, Color.WHITE);
    }

    public boolean isClicked(int mouseX, int mouseY) {
        isClicked = mouseX >= bounds.x && mouseX <= (bounds.x + bounds.width) &&
                mouseY >= bounds.y && mouseY <= (bounds.y + bounds.height);
        return isClicked;
    }
}