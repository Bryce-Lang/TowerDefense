
import com.raylib.java.Raylib;
import com.raylib.java.core.Color;

import static com.raylib.java.core.input.Keyboard.KEY_SPACE;

public class Main {
	public static void main(String[] args) {
		
		// window size, independent of actual screen size
		final int screen_width = 960;
		final int screen_height = 540;
		Raylib rlj = new Raylib(screen_width, screen_height, "Tower Defense");
		
		map map = new map(screen_width, screen_height, screen_width / 5, 15);
		
		rlj.core.SetTargetFPS(60);
		
		// main game loop- runs once every frame until window is closed with x button or ESC key
		while (!rlj.core.WindowShouldClose()) {  // Detect window close button or ESC key
			
			// regenerate map on space press
			if (rlj.core.IsKeyReleased(KEY_SPACE)) {
				map = new map(screen_width, screen_height, screen_width / 5, 15);
			}
			
			// Draw to screen here ------------------------------------------------------
			rlj.core.BeginDrawing();
			
			rlj.core.ClearBackground(Color.RAYWHITE);
			
			// draw menu on left of screen
			rlj.shapes.DrawRectangle(0, 0, screen_width / 5, screen_height, Color.LIGHTGRAY);
			rlj.text.DrawText("reserved for menu", 5, 0, 15, Color.BLACK);
			rlj.text.DrawText("press [space] to \ngenerate new map", 5, 50, 15, Color.BLACK);
			
			map.draw(rlj);
			
			rlj.core.EndDrawing();
			//---------------------------------------------------------------------------
		}
	}
}
