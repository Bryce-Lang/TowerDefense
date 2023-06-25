import static com.raylib.java.textures.rTextures.LoadTexture;

import com.raylib.java.Raylib;
import com.raylib.java.core.Color;
import com.raylib.java.core.input.Mouse.MouseButton;
import com.raylib.java.shapes.Rectangle;
import com.raylib.java.shapes.rShapes;
import com.raylib.java.textures.Texture2D;


public class UI {
    private int screenWidth;
    private int screenHeight;
    private Raylib rlj;
    private Button playButton;
    private Button helpButton;
    private Button nextButton;
    private Button exitButton;
    private Button backButton;
    
    private Texture2D logoImage;
    
    private int helpPageCounter;
    
    public UI(int screenWidth, int screenHeight, Raylib rlj) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.rlj = rlj;
        helpPageCounter = 0;
     // Initialize buttons
        playButton = new Button("PLAY", new Rectangle(100, 200, 200, 50), Color.RED);
        helpButton = new Button("HELP", new Rectangle(100, 300, 200, 50), Color.BLUE);
        nextButton = new Button("NEXT", new Rectangle(100, 470, 200, 50), Color.GREEN);
        exitButton = new Button("EXIT", new Rectangle(700, 470, 200, 50), Color.RED);
        backButton = new Button("BACK", new Rectangle(300, 470, 200, 50), Color.BLUE);
        

 
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
    
    public void loadAssets() {
    	//Load logo
    	logoImage = LoadTexture("image/logo.png");
    	
    }
    
    public void renderHelpScreen() {
        // Draw UI elements specific to the help screen
    	rlj.shapes.DrawRectangle(0, 0, screenWidth, screenHeight, Color.WHITE);
        if (helpPageCounter == 0) {  
    	rlj.text.DrawText("OBJECTIVE: Stop the enemies "
    			+ "\n from reaching the base!", 20, 20, 40, Color.SKYBLUE);
        }else if (helpPageCounter == 1) {
            // Display second page of instructions
            rlj.text.DrawText("HOW TO PLAY: use Wealth to buy towers"
            		+ "\n after selecting a tower"
            		+ "\n click on the map to place them."
            		+ "\n"
            		+ "-Towers can be upgraded for a fee", 20, 20, 40, Color.SKYBLUE);
        } else if (helpPageCounter == 2) {
            // Display third page of instructions
            rlj.text.DrawText("MECHANICS: Base on the color of the road"
            		+ "\n it will effect the enimes movement."
            		+ "\n", 20, 20, 40, Color.SKYBLUE);
            rlj.text.DrawText("RED will slow them down.", 80, 150, 40, Color.RED);
            rlj.text.DrawText("GREEN will speed them up.", 80, 200, 40, Color.GREEN);
            rlj.text.DrawText("BLUE will leave them as is.", 80, 250, 40, Color.BLUE);
            rlj.text.DrawText("BE CAREFUL! Enemies grow STRONGER and "
            		+ "\n swarm FASTER as you progres! ", 20, 350, 40, Color.ORANGE);
        }
        
    	nextButton.render(rlj);
        backButton.render(rlj);
        exitButton.render(rlj);
        //rlj.text.DrawText("PRESS Play to GAMEPLAY SCREEN or Help for Controls", 120, 120, 20, Color.DARKGREEN);
         
    }
    public void renderLogoScreen() {
        // Draw UI elements specific to the Logo screen
    	rlj.textures.DrawTexture(logoImage, 100, 0, Color.WHITE);
        
         
    }
    public void renderGameplayScreen() {
        rlj.shapes.DrawRectangle(0, 0, screenWidth, screenHeight, Color.PURPLE);
        // Draw UI elements specific to the gameplay screen
        // Draw the map and other gameplay elements
    }
    
    // Other methods for handling button clicks or interactions
    public String handleInput(screenManager.GameScreen currentScreen) {
        if (currentScreen == screenManager.GameScreen.TITLE) {
            // Handle input for the TITLE screen
            if (rlj.core.IsMouseButtonPressed(MouseButton.MOUSE_BUTTON_LEFT)) {
                int mouseX = rlj.core.GetMouseX();
                int mouseY = rlj.core.GetMouseY();
                
                if (playButton.isClicked(mouseX, mouseY)) {
                    return "PLAY";
                } else if (helpButton.isClicked(mouseX, mouseY)) {
                    return "HELP";
                }
            }
        } else if (currentScreen == screenManager.GameScreen.HELP) {
            // Handle input for the HELP screen
            if (rlj.core.IsMouseButtonPressed(MouseButton.MOUSE_BUTTON_LEFT)) {
                int mouseX = rlj.core.GetMouseX();
                int mouseY = rlj.core.GetMouseY();
                
                if (nextButton.isClicked(mouseX, mouseY)) {
                    // Increment the help page counter
                    helpPageCounter++;
                    if (helpPageCounter > 2) {
                        helpPageCounter = 0; // Reset the counter if it exceeds the maximum page value
                    }
                } else if (backButton.isClicked(mouseX, mouseY)) {
                    // Decrement the help page counter
                    helpPageCounter--;
                    if (helpPageCounter < 0) {
                        helpPageCounter = 2; // Wrap around to the maximum page value if the counter goes below 0
                    }
                } else if (exitButton.isClicked(mouseX, mouseY)) {
                    return "EXIT";
                }
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