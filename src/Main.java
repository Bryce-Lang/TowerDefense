
import com.raylib.java.Raylib;
import com.raylib.java.core.Color;

public class Main {
	public static void main(String[] args) {
		
		final int screen_width = 1280;
		final int screen_height = 720;
		Raylib rlj = new Raylib(screen_width, screen_height, "Tower Defense");
		
		
		// main game loop- runs once every frame until window is closed with x button or ESC key
		while (!rlj.core.WindowShouldClose()) {  // Detect window close button or ESC key
			
			
			// Draw to screen here ------------------------------------------------------
			rlj.core.BeginDrawing();
			
			rlj.core.ClearBackground(Color.RAYWHITE);
			
			rlj.text.DrawText("raylib", 100, 0, 50, Color.BLACK);
			
			rlj.core.EndDrawing();
			//---------------------------------------------------------------------------
		}
	}
}
