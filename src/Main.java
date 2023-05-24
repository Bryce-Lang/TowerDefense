
import com.raylib.java.Raylib;
import com.raylib.java.core.Color;

import static com.raylib.java.core.input.Keyboard.KEY_SPACE;

public class Main {
	public static void main(String[] args) {
		
		// window size, independent of actual screen size
		final int screen_width = 960;
		final int screen_height = 540;
		Raylib rlj = new Raylib(screen_width, screen_height, "Tower Defense");
		
		map map = new map(screen_width, screen_height);
		
		// main game loop- runs once every frame until window is closed with x button or ESC key
		while (!rlj.core.WindowShouldClose()) {  // Detect window close button or ESC key
			
			
			// Draw to screen here ------------------------------------------------------
			rlj.core.BeginDrawing();
			
			rlj.core.ClearBackground(Color.RAYWHITE);
			
			rlj.shapes.DrawRectangle(0, 0, screen_width / 5, screen_height, Color.LIGHTGRAY);
			rlj.text.DrawText("reserved for menu", 5, 0, 15, Color.BLACK);
			rlj.text.DrawText("press [space] to \ngenerate new map", 5, 50, 15, Color.BLACK);
			
			for (int i = 0; i < map.segment_count-1; ++i) {
				rlj.shapes.DrawCircle((int) map.points.get(i).x, (int) map.points.get(i).y, (float) 10.0, Color.BLACK);
				rlj.text.DrawText(String.valueOf(i), (int) map.points.get(i).x - 5, (int) map.points.get(i).y - 5, 10, Color.WHITE);
			}
			
			if (rlj.core.IsKeyReleased(KEY_SPACE)) {
				map = new map(screen_width, screen_height);
			}
			
			rlj.core.EndDrawing();
			//---------------------------------------------------------------------------
		}
	}
}
