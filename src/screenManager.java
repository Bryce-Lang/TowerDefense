import com.raylib.java.Raylib;
import com.raylib.java.core.Color;
import com.raylib.java.core.input.Mouse.MouseButton;
import static com.raylib.java.core.input.Keyboard.KEY_ENTER;
import static com.raylib.java.core.input.Keyboard.KEY_SPACE;
import static com.raylib.java.core.input.Mouse.MouseButton.MOUSE_BUTTON_LEFT;

import com.raylib.java.raymath.Vector2;
import com.raylib.java.textures.Texture2D;
import static com.raylib.java.textures.rTextures.LoadTexture;
import com.raylib.java.shapes.Rectangle;
import static com.raylib.java.core.rCore.*;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

import java.util.ArrayList;

public class screenManager {
    private static UI ui;
    // Types and Structures Definition
    enum GameScreen {
        LOGO,
        TITLE,
        GAMEPLAY,
        ENDING,
        HELP
    }
    private static ArrayList<Enemy> enemies = new ArrayList<>();
    public static void main(String[] args) {
        // Initialization
    	Vector2 mouse_position = new Vector2(0.0f,0.0f);
        final int screen_width = 960;
        final int screen_height = 540;
        float timer = 0;
		int level = 3;
		long offset = 0;
        Raylib rlj = new Raylib(screen_width, screen_height, "Tower Defense");
        ui = new UI(screen_width, screen_height, rlj);
        Texture2D map0 = LoadTexture("map0.png");
		Rectangle map0_rectangle = new Rectangle(0,0,map0.width,map0.height);
		Rectangle map0_bound = new Rectangle(15,50,map0.width,map0.height);
		Texture2D map1 = LoadTexture("map1.png");
		Rectangle map1_rectangle = new Rectangle(0,0,map1.width,map1.height);
		Rectangle map1_bound = new Rectangle(15,50,map1.width,map1.height);


		Texture2D exit0 = LoadTexture("exit0.png");
		Rectangle exit0_rectangle = new Rectangle(0,0,exit0.width,exit0.height);
		Rectangle exit0_bound = new Rectangle(15,120,exit0.width,exit0.height);
		Texture2D exit1 = LoadTexture("exit1.png");
		Rectangle exit1_rectangle = new Rectangle(0,0,exit1.width,exit1.height);
		Rectangle exit1_bound = new Rectangle(15,50,exit1.width,exit1.height);
        
		//Initial game setup
		Database db = new Database();
		GameStateManager game_state = new GameStateManager(screen_width, screen_height, rlj, db);
		//checks if the previous level is cleared. Increments level counter, and adds in a new set of enemies.
		/*if(levelCleared()){
			level++;
			for(int i = 0; i < level; i++){
				Enemy enemy = new Enemy(100, game_state.map.points.get(0).x, game_state.map.points.get(0).y, offset);
				enemies.add(enemy);
				offset += 10;
			}
		}*/
		int game_tick = 0;
		rlj.core.SetTargetFPS(60);
		//if(levelCleared()){
		//		resetMap();
		//	}
			
        /*
        Map map = new Map(screen_width, screen_height, screen_width / 5, 32);
    	
        
        if(levelCleared()){
			level++;
			for(int i = 0; i < level; i++){
				//Enemy enemy = new Enemy(100, map.points.get(0).x, map.points.get(0).y, offset, 500);
				//enemies.add(enemy);
				offset += 10;
			}
		}
		*/
        GameScreen currentScreen = GameScreen.LOGO;
        
        
        // TODO: Initialize all required variables and load all required data here!
       // map map = new map(screenWidth, screenHeight, screenWidth / 5, 15);

        int framesCounter = 0;          // Useful to count frames

        final double targetFPS = 60.0;
        final double frameTime = 1.0 / targetFPS;
        SoundManager.playBackgroundMusic(-25.0f);
        
        while (!rlj.core.WindowShouldClose()) {  // Detect window close button or ESC key
            double startTime = System.currentTimeMillis() / 1000.0;
            ++game_tick;
            mouse_position = GetMousePosition();
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
                	
                    
                    String buttonClicked = ui.handleInput();
                    if (!buttonClicked.isEmpty()) {
                        if (buttonClicked.equals("PLAY")) {
                            currentScreen = GameScreen.GAMEPLAY;
                        } else if (buttonClicked.equals("HELP")) {
                            currentScreen = GameScreen.HELP;
                        }
                    }
                    
                    break;

                case GAMEPLAY:
                    // TODO: Update GAMEPLAY screen variables here!
                	
                	if (rlj.shapes.CheckCollisionPointRec(mouse_position, map1_bound)) {
        				
        			}
        			if (rlj.shapes.CheckCollisionPointRec(mouse_position, map0_bound)) {
        				
        			}
                	
                	if (rlj.core.IsKeyReleased(KEY_SPACE)) {
        				game_state.regen_map();
        			}
                	timer += 0.001f;
                	/*
                	if (rlj.core.IsKeyReleased(KEY_SPACE)) {
        				map = new Map(screen_width, screen_height, screen_width / 5, 32);
        				timer = 0;
        			}
        			timer += 0.2f;
                    // Press enter to change to ENDING screen
                    if (isKeyPressed(rlj, KEY_ENTER) || isMouseButtonPressed(rlj,MouseButton.MOUSE_BUTTON_LEFT)) { // 13 is the key code for Enter key
                        currentScreen = GameScreen.ENDING;
                    }*/
                    break;
                case ENDING:
                    // TODO: Update ENDING screen variables here!

                    // Press enter to return to TITLE screen
                    if (isKeyPressed(rlj, KEY_ENTER) || isMouseButtonPressed(rlj,MouseButton.MOUSE_BUTTON_LEFT)) { // 13 is the key code for Enter key
                        currentScreen = GameScreen.TITLE;
                    }
                    break;
                case HELP:
                	// TODO: Update LOGO screen variables here!
                	if(framesCounter > 120) {
                		framesCounter = 0; 
                	}
                	framesCounter++;    // Count frames

                    // Wait for 2 seconds (120 frames) before jumping to TITLE screen
                    if (framesCounter > 120) {
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
                	ui.renderTitleScreen();
                	
                	break;
                case GAMEPLAY:
                	game_state.run_frame(game_tick);
                	rlj.core.ClearBackground(Color.BLACK);
                	// draw menu on left of screen
        			rlj.shapes.DrawRectangle(0, 0, screen_width / 5, screen_height, Color.DARKGRAY);
        			
        			//rlj.text.DrawText("Level: " + level, 200, 5, 15, Color.RAYWHITE);
        			
        			//rlj.text.DrawText("Time: " + timer, 200, 55, 15, Color.RAYWHITE);
        			
        			rlj.textures.DrawTextureRec(map0, map0_rectangle, new Vector2(15,50), Color.RAYWHITE);
        			rlj.textures.DrawTextureRec(exit0, exit0_rectangle, new Vector2(15,120), Color.RAYWHITE);

        			
        			if (rlj.shapes.CheckCollisionPointRec(mouse_position, map0_bound)) {
        				rlj.textures.DrawTextureRec(map1, map1_rectangle, new Vector2(15,50), Color.RAYWHITE);
        				if (rlj.core.IsMouseButtonReleased(MOUSE_BUTTON_LEFT)) {
        					//resetMap();
        				}
        			}
        			
        			
        			if (rlj.shapes.CheckCollisionPointRec(mouse_position, exit0_bound)) {
        				rlj.textures.DrawTextureRec(exit1, exit1_rectangle, new Vector2(15,120), Color.RAYWHITE);
        				if (IsMouseButtonDown(MOUSE_BUTTON_LEFT)) {
        					rlj.core.WindowShouldClose();
        					return;
        				}
        			}
        			
        			rlj.text.DrawText("Health: " + Math.max(game_state.player_health, 0), 20, 30, 20, Color.RAYWHITE);
        			rlj.text.DrawText("Wealth: " + game_state.player_money, 20, 70, 20, Color.RAYWHITE);
        			rlj.text.DrawText("Level: " + game_state.level, 20, 110, 20, Color.RAYWHITE);
        			rlj.text.DrawFPS(20, 180);
        			game_state.draw_gui();
        			/*
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
        				//enemies.get(i).draw(rlj, map, timer);
        			}
                    rlj.text.DrawText("PRESS ENTER or TAP to JUMP to ENDING SCREEN", 130, 220, 20, Color.MAROON);
                    */
                    break;
                case ENDING:
                    // TODO: Draw ENDING screen here!
                    rlj.shapes.DrawRectangle(0, 0, screen_width, screen_height, Color.BLUE);
                    rlj.text.DrawText("ENDING SCREEN", 20, 20, 40, Color.DARKBLUE);
                    rlj.text.DrawText("PRESS ENTER or TAP to RETURN to TITLE SCREEN", 120, 220, 20, Color.DARKBLUE);
                    break;
                case HELP:
                    // TODO: Draw LOGO screen here!
                    rlj.text.DrawText("pressed help", 20, 20, 40, Color.GOLD);
                    rlj.text.DrawText("WAIT for 2 SECONDS...", 290, 220, 20, Color.GRAY);
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
