import com.raylib.java.Raylib;
import com.raylib.java.core.Color;
import com.raylib.java.core.input.Mouse.MouseButton;
import static com.raylib.java.core.input.Keyboard.KEY_ENTER;
import static com.raylib.java.core.input.Keyboard.KEY_SPACE;

import java.util.ArrayList;

public class screenManager {

    // Types and Structures Definition
    enum GameScreen {
        LOGO,
        TITLE,
        GAMEPLAY,
        ENDING
    }
    static ArrayList<Enemy> enemies = new ArrayList<>();
    public static void main(String[] args) {
        // Initialization
        final int screen_width = 800;
        final int screen_height = 450;
        int level = 3;
        long offset = 0;
        float timer = 0;
        Raylib rlj = new Raylib(screen_width, screen_height, "raylib [core] example - basic screen manager");
        map map = new map(screen_width, screen_height, screen_width / 5, 32);
    	if(levelCleared()){
			level++;
			for(int i = 0; i < level; i++){
				Enemy enemy = new Enemy(100, map.points.get(0).x, map.points.get(0).y, offset, 500);
				enemies.add(enemy);
				offset += 10;
			}
		}
        GameScreen currentScreen = GameScreen.LOGO;

        // TODO: Initialize all required variables and load all required data here!
       // map map = new map(screenWidth, screenHeight, screenWidth / 5, 15);

        int framesCounter = 0;          // Useful to count frames

        final double targetFPS = 60.0;
        final double frameTime = 1.0 / targetFPS;

        // Main game loop
        while (!rlj.core.WindowShouldClose()) {  // Detect window close button or ESC key
            double startTime = System.currentTimeMillis() / 1000.0;

            // Update
            switch(currentScreen) {
                case LOGO:
                    // TODO: Update LOGO screen variables here!
                    framesCounter++;    // Count frames

                    // Wait for 2 seconds (120 frames) before jumping to TITLE screen
                    if (framesCounter > 120) {
                        currentScreen = GameScreen.TITLE;
                    }
                    break;
                case TITLE:
                    // TODO: Update TITLE screen variables here!

                    // Press enter to change to GAMEPLAY screen
                    if (isKeyPressed(rlj, KEY_ENTER) || isMouseButtonPressed(rlj,MouseButton.MOUSE_BUTTON_LEFT)) { // 13 is the key code for Enter key
                        currentScreen = GameScreen.GAMEPLAY;
                    }
                    break;
                case GAMEPLAY:
                    // TODO: Update GAMEPLAY screen variables here!
                	if (rlj.core.IsKeyReleased(KEY_SPACE)) {
        				map = new map(screen_width, screen_height, screen_width / 5, 32);
        				timer = 0;
        			}
        			timer += 0.2f;
                    // Press enter to change to ENDING screen
                    if (isKeyPressed(rlj, KEY_ENTER) || isMouseButtonPressed(rlj,MouseButton.MOUSE_BUTTON_LEFT)) { // 13 is the key code for Enter key
                        currentScreen = GameScreen.ENDING;
                    }
                    break;
                case ENDING:
                    // TODO: Update ENDING screen variables here!

                    // Press enter to return to TITLE screen
                    if (isKeyPressed(rlj, KEY_ENTER) || isMouseButtonPressed(rlj,MouseButton.MOUSE_BUTTON_LEFT)) { // 13 is the key code for Enter key
                        currentScreen = GameScreen.TITLE;
                    }
                    break;
                default:
                    break;
            }

            // Draw
            rlj.core.BeginDrawing();

            rlj.core.ClearBackground(Color.RAYWHITE);

            switch(currentScreen) {
                case LOGO:
                    // TODO: Draw LOGO screen here!
                    rlj.text.DrawText("LOGO SCREEN", 20, 20, 40, Color.GOLD);
                    rlj.text.DrawText("WAIT for 2 SECONDS...", 290, 220, 20, Color.GRAY);
                    break;
                case TITLE:
                    // TODO: Draw TITLE screen here!
                    rlj.shapes.DrawRectangle(0, 0, screen_width, screen_height, Color.GREEN);
                    rlj.text.DrawText("TITLE SCREEN", 20, 20, 40, Color.DARKGREEN);
                    rlj.text.DrawText("PRESS ENTER or TAP to JUMP to GAMEPLAY SCREEN", 120, 220, 20, Color.DARKGREEN);
                    break;
                    
                case GAMEPLAY:
                    // TODO: Draw GAMEPLAY screen here!
                    rlj.shapes.DrawRectangle(0, 0, screen_width, screen_height, Color.PURPLE);
                    //rlj.text.DrawText("GAMEPLAY SCREEN", 20, 20, 40, Color.MAROON);
                    rlj.shapes.DrawRectangle(0, 0, screen_width / 5, screen_height, Color.DARKGRAY);
        			rlj.text.DrawText("reserved for menu", 5, 0, 15, Color.RAYWHITE);
        			rlj.text.DrawText("press [space] to \ngenerate new map", 5, 50, 15, Color.RAYWHITE);
        			rlj.text.DrawText("Level: " + level, 200, 5, 15, Color.RAYWHITE);
        			map.draw(rlj);
        			//let enemies run along the path
        			for(int i = 0; i < enemies.size(); i++){
        				enemies.get(i).draw(rlj, map, timer);
        			}
                    rlj.text.DrawText("PRESS ENTER or TAP to JUMP to ENDING SCREEN", 130, 220, 20, Color.MAROON);
                    break;
                case ENDING:
                    // TODO: Draw ENDING screen here!
                    rlj.shapes.DrawRectangle(0, 0, screen_width, screen_height, Color.BLUE);
                    rlj.text.DrawText("ENDING SCREEN", 20, 20, 40, Color.DARKBLUE);
                    rlj.text.DrawText("PRESS ENTER or TAP to RETURN to TITLE SCREEN", 120, 220, 20, Color.DARKBLUE);
                    break;
                default:
                    break;
            }

            rlj.core.EndDrawing();

            // Delay to control frame rate
            double endTime = System.currentTimeMillis() / 1000.0;
            double deltaTime = endTime - startTime;

            if (deltaTime < frameTime) {
                double delayTime = frameTime - deltaTime;
                int delayMillis = (int)(delayTime * 1000);

                try {
                    Thread.sleep(delayMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // De-Initialization
        // TODO: Unload all loaded data (textures, fonts, audio) here!

        rlj.core.CloseWindow();        // Close window and OpenGL context
    }

    private static boolean isKeyPressed(Raylib rlj, int keyCode) {
        return rlj.core.IsKeyPressed(keyCode);
    }
    
    private static boolean isMouseButtonPressed(Raylib rlj, int mOUSE_BUTTON_LEFT) {
        return rlj.core.IsMouseButtonPressed(MouseButton.MOUSE_BUTTON_LEFT);
    }
    public static boolean levelCleared(){
		if(enemies.size() == 0)
			return true;
		return false;
	}
}
