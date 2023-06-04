import java.util.ArrayList;
import com.raylib.java.Raylib;
import com.raylib.java.core.Color;

public class GameStateManager {
	
	// defaults; should be updated with the constructor
	private static int screen_width = 960;
	private static int screen_height = 540;
	
	private Raylib rlj;
	
	public Map map;
	
	// Stores enemies
	public ArrayList<Enemy> enemies = new ArrayList<>();
	
	// TODO: implement towers
	// public ArrayList<Tower> towers = new ArrayList<>();
	
	// current level; used to generate enemy types and number
	private int level = 0;
	
	public GameStateManager(int width, int height, Raylib in_rlj) {
		screen_width = width;
		screen_height = height;
		map = new Map(screen_width, screen_height, (screen_width / 5), 32);
		rlj = in_rlj;
	}
	
	public GameStateManager(int width, int height, Raylib in_rlj, int level) {
		screen_width = width;
		screen_height = height;
		this.level = level;
		map = new Map(screen_width, screen_height, (screen_width / 5), 32);
		rlj = in_rlj;
	}
	
	// TODO: update enemy draw as Enemy.java is updated
	float timer = 0;
	public void draw() {
		timer += 0.2;
		
		map.draw(rlj);
		
		for (int i = 0; i < enemies.size(); ++i) {
			//enemies.get(i).draw(rlj, map, timer);
		}
		
		/*for (int i = 0; i < towers.size(); ++i) {
			towers.get(i).draw();
		*/
	}
	
	public void regen_map() {
		map = new Map(screen_width, screen_height, (screen_width / 5), 32);
	}
	
	public void update_screen_size(int width, int height) {
		screen_width = width;
		screen_height = height;
	}
}
