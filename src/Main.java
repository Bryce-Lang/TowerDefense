
import com.raylib.java.Raylib;
import com.raylib.java.core.Color;
import com.raylib.java.raymath.Vector2;
import com.raylib.java.shapes.Rectangle;
import com.raylib.java.textures.Texture2D;

import java.util.ArrayList;

import static com.raylib.java.core.input.Keyboard.KEY_SPACE;
import static com.raylib.java.core.input.Mouse.MouseButton.MOUSE_BUTTON_LEFT;
import static com.raylib.java.core.rCore.*;
import static com.raylib.java.textures.rTextures.LoadTexture;

public class Main {
	static ArrayList<Enemy> enemies = new ArrayList<>();
	static float timer = 0;
	static int level = 3;
	//window size, independent of actual screen size
	static final int screen_width = 960;
	static final int screen_height = 540;
	static long offset = 0;
	static map map;
	public static void main(String[] args) {
		Vector2 mouse_position = new Vector2(0.0f,0.0f);
		// window size, independent of actual screen sizeVector2 mouse_position = new Vector2(0.0f,0.0f);
		Raylib rlj = new Raylib(screen_width, screen_height, "Tower Defense");

		Texture2D new_map_button = LoadTexture("new_map.png");
		Rectangle map_rectangle = new Rectangle(0,0,new_map_button.width,new_map_button.height);
		Rectangle map_bound = new Rectangle(15,50,new_map_button.width,new_map_button.height);

		Texture2D exit_button = LoadTexture("exit.png");
		Rectangle exit_rectangle = new Rectangle(0,0,exit_button.width,exit_button.height);
		Rectangle exit_bound = new Rectangle(15,120,exit_button.width,exit_button.height);
		
		//Initial game setup
		if(levelCleared()){
			resetMap();
		}
		
		rlj.core.SetTargetFPS(60);
		
		// main game loop- runs once every frame until window is closed with x button or ESC key
		while (!rlj.core.WindowShouldClose()) {  // Detect window close button or ESC key

			mouse_position = GetMousePosition();

			// regenerate map on space press
			/*if (rlj.core.IsKeyReleased(KEY_SPACE)) {
				map = new map(screen_width, screen_height, screen_width / 5, 32);
				timer = 0;
			}*/
			if (rlj.shapes.CheckCollisionPointRec(mouse_position, map_bound)) {
				if (rlj.core.IsMouseButtonReleased(MOUSE_BUTTON_LEFT)) {
					resetMap();
				}
			}
			timer += 0.001f;
			
			// Draw to screen here ------------------------------------------------------
			rlj.core.BeginDrawing();
			
			rlj.core.ClearBackground(Color.BLACK);
			
			// draw menu on left of screen
			rlj.shapes.DrawRectangle(0, 0, screen_width / 5, screen_height, Color.DARKGRAY);
			rlj.text.DrawText("reserved for menu", 5, 0, 15, Color.RAYWHITE);
			rlj.text.DrawText("press [space] to \ngenerate new map", 5, 50, 15, Color.RAYWHITE);
			rlj.text.DrawText("Level: " + level, 200, 5, 15, Color.RAYWHITE);
			rlj.text.DrawText("Time: " + timer, 200, 55, 15, Color.RAYWHITE);

			rlj.textures.DrawTextureRec(new_map_button, map_rectangle, new Vector2(15,50), Color.RAYWHITE);
			rlj.textures.DrawTextureRec(exit_button, exit_rectangle, new Vector2(15,120), Color.RAYWHITE);



			if (rlj.shapes.CheckCollisionPointRec(mouse_position, exit_bound)) {
				if (IsMouseButtonDown(MOUSE_BUTTON_LEFT)) {
					rlj.core.WindowShouldClose();
					return;
				}
			}

			map.draw(rlj);
			//let enemies run along the path
			for(int i = 0; i < enemies.size(); i++){
				enemies.get(i).draw(rlj, map, timer);
			}
			//
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
	//function to reset all variables and generate a new map.
	public static void resetMap(){
		map = new map(screen_width, screen_height, screen_width / 5, 32);
		timer = 0;
		offset = 0;
		enemies.clear();
		for(int i = 0; i < level; i++){
			Enemy enemy = new Enemy(100, map.points.get(0).x, map.points.get(0).y, offset, 5);
			enemies.add(enemy);
			offset += 20;
		}
	}
}
