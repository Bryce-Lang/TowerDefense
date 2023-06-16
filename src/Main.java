
import java.util.ArrayList;

import com.raylib.java.Raylib;
import com.raylib.java.core.Color;
import com.raylib.java.raymath.Vector2;
import com.raylib.java.shapes.Rectangle;
import com.raylib.java.textures.Texture2D;

import static com.raylib.java.core.input.Keyboard.KEY_F;
import static com.raylib.java.core.input.Keyboard.KEY_SPACE;
import static com.raylib.java.core.input.Mouse.MouseButton.MOUSE_BUTTON_LEFT;
import static com.raylib.java.core.rCore.*;
import static com.raylib.java.textures.rTextures.LoadTexture;

public class Main {
	private static ArrayList<Enemy> enemies = new ArrayList<>();
	
	public static void main(String[] args) {
		
		Vector2 mouse_position = new Vector2(0.0f,0.0f);
		
		// window size, independent of actual screen size
		final int screen_width = 960;
		final int screen_height = 540;
		float timer = 0;
		int level = 3;
		long offset = 0;
		
		Raylib rlj = new Raylib(screen_width, screen_height, "Tower Defense");

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
		//if(levelCleared()){
		//	resetMap();
		//}
		
		Database db = new Database();
		
		GameStateManager game_state = new GameStateManager(screen_width, screen_height, rlj, db);
		
		//checks if the previous level is cleared. Increments level counter, and adds in a new set of enemies.
		//if(levelCleared()){
		//	level++;
		//	for(int i = 0; i < level; i++){
		//		Enemy enemy = new Enemy(100, game_state.map.points.get(0).x, game_state.map.points.get(0).y, offset);
		//		enemies.add(enemy);
		//		offset += 10;
		//	}
		//}
		
		int game_tick = 0;
		
		rlj.core.SetTargetFPS(60);
		
		// main game loop- runs once every frame until window is closed with x button or ESC key
		while (!rlj.core.WindowShouldClose()) {  // Detect window close button or ESC key
			
			++game_tick;
			
			if (rlj.core.IsKeyPressed(KEY_F)) rlj.core.ToggleFullscreen();  // TODO delete
			
			mouse_position = GetMousePosition();

			if (rlj.shapes.CheckCollisionPointRec(mouse_position, map1_bound)) {
				
			}
			if (rlj.shapes.CheckCollisionPointRec(mouse_position, map0_bound)) {
				
			}

			if (rlj.core.IsKeyReleased(KEY_SPACE)) {
				game_state.regen_map();
			}
			
			timer += 0.001f;
			
			// Draw to screen here ------------------------------------------------------
			rlj.core.BeginDrawing();
			
			rlj.core.ClearBackground(Color.BLACK);
			
			game_state.run_frame(game_tick);
			
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

			//map.draw(rlj);
			//let enemies run along the path
			//for(int i = 0; i < enemies.size(); i++){
			//	enemies.get(i).draw(rlj, map, timer);
			//}
			
			rlj.core.EndDrawing();
			//---------------------------------------------------------------------------
		}
	}
	// method to check if enemies array list is empty.
	public static boolean levelCleared(){
		if(enemies.size() == 0)
			return true;
		return false;
	}
	
	/*function to reset all variables and generate a new map.
	public static void resetMap(){
		map = new Map(screen_width, screen_height, screen_width / 5, 32);
		timer = 0;
		offset = 0;
		enemies.clear();
		for(int i = 0; i < level; i++){
			Enemy enemy = new Enemy(100, map.points.get(0).x, map.points.get(0).y, offset, 5);
			enemies.add(enemy);
			offset += 20;
		}
	}
	*/
}
