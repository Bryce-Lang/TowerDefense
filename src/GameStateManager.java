import java.util.ArrayList;
import java.util.Random;

import static com.raylib.java.core.input.Mouse.MouseButton.MOUSE_BUTTON_LEFT;
import static com.raylib.java.core.input.Mouse.MouseButton.MOUSE_BUTTON_RIGHT;
import static com.raylib.java.core.input.Keyboard.KEY_R;
import static com.raylib.java.core.input.Keyboard.KEY_D;
import com.raylib.java.Raylib;
import com.raylib.java.core.Color;
import com.raylib.java.core.rCore;
import com.raylib.java.raymath.Raymath;
import com.raylib.java.raymath.Vector2;

public class GameStateManager {
	
	// defaults; should be updated with the constructor
	private static int screen_width = 960;
	private static int screen_height = 540;
	
	private final static int tower_cost = 20;
	
	private Raylib rlj;
	
	private Database db;
	
	// stores remaining player health 
	public int player_health;
	
	// stores player money as an int
	public int player_money;
	
	public Map map;
	
	// Stores enemies
	public ArrayList<Enemy> enemies = new ArrayList<>();
	
	// Stores towers
	public ArrayList<Tower> towers = new ArrayList<>();
	
	// currently highlighted tower for upgrades
	private int h_tower_ind = -1;
	
	// current level; used to generate enemy types and number
	public int level = 0;
	
	// dummy enemy and tower used to hold defaults
	private Enemy def_enemy;
	private Tower def_tower;
	
	public GameStateManager(int width, int height, Raylib in_rlj, Database in_db) {
		screen_width = width;
		screen_height = height;
		map = new Map(screen_width, screen_height, (screen_width / 5), 32);
		rlj = in_rlj;
		db = in_db;
		player_health = 100;
		player_money = 100;
		
		load_defaults();
	}
	
	public GameStateManager(int width, int height, Raylib in_rlj, Database in_db, int level) {
		screen_width = width;
		screen_height = height;
		this.level = level;
		map = new Map(screen_width, screen_height, (screen_width / 5), 32);
		rlj = in_rlj;
		db = in_db;
		player_health = 100;
		player_money = 100;
		
		load_defaults();
	}
	
	public void draw() {
		map.draw(rlj);
		
		for (int i = 0; i < enemies.size(); ++i) {
			enemies.get(i).draw(rlj);
		}
		
		for (int i = 0; i < towers.size(); ++i) {
			towers.get(i).draw(rlj);
		}
		
		// draw highlight on selected tower
		if (h_tower_ind != -1) {
			rlj.shapes.DrawCircleLines((int) towers.get(h_tower_ind).coord.x,
									   (int) towers.get(h_tower_ind).coord.y,
									   towers.get(h_tower_ind).range,
									   Color.LIGHTGRAY);
			rlj.shapes.DrawCircleLines((int) towers.get(h_tower_ind).coord.x,
									   (int) towers.get(h_tower_ind).coord.y,
									   11,
									   Color.YELLOW);
			rlj.text.DrawText("Damage: " + towers.get(h_tower_ind).damage,
							  (int) towers.get(h_tower_ind).coord.x - 25,
							  (int) towers.get(h_tower_ind).coord.y - 24,
							  10,
							  Color.RAYWHITE);
			rlj.text.DrawText("Range: " + towers.get(h_tower_ind).range / 10,
							  (int) towers.get(h_tower_ind).coord.x - 25,
							  (int) towers.get(h_tower_ind).coord.y - 36,
							  10,
							  Color.RAYWHITE);
		}
	}
	
	// takes in frames counter to determine whether towers should shoot
	public void run_frame(int tick) {
		
		// place towers where player clicks
		if (player_money >= tower_cost && rlj.core.IsMouseButtonPressed(MOUSE_BUTTON_LEFT)) {
			boolean valid_pos = true;
			Vector2 mouse_pos = rCore.GetMousePosition();
			for (int i = 0; i < towers.size(); ++i) {
				if (15 > Raymath.Vector2Distance(mouse_pos, towers.get(i).coord)) {
					valid_pos = false;
					break;
				}
			}
			if (valid_pos) {
				player_money -= tower_cost;
				towers.add(new Tower(rCore.GetMousePosition(), def_tower.range, def_tower.damage));
				h_tower_ind = towers.size() - 1;
			}
		}
		
		// select highlighted tower for upgrades
		if (rlj.core.IsMouseButtonPressed(MOUSE_BUTTON_RIGHT)) {
			if (h_tower_ind == -1) {
				h_tower_ind = 0;
			}
			for (int i = 0; i < towers.size(); ++i) {
				if (Raymath.Vector2Distance(rCore.GetMousePosition(), towers.get(h_tower_ind).coord) > 
					Raymath.Vector2Distance(rCore.GetMousePosition(), towers.get(i).coord)) {
					
					h_tower_ind = i;
				}
			}
		}
		
		if (rlj.core.IsKeyPressed(KEY_R)) {
			upgrade_h_range();
		}
		
		if (rlj.core.IsKeyPressed(KEY_D)) {
			upgrade_h_damage();
		}
		
		// start next level when no enemies remain
		if (enemies.size() == 0) {
			level_up();
		}
		
		// draw map, enemies, and towers
		draw();
		
		// fire towers
		fire_towers();
		
		// move enemies along path
		update_enemies();
	}
	
	private void fire_towers() {
		
		if (enemies.size() > 0) {
			// for each tower, find the closest enemy. if within range and 
			for (int i = 0; i < towers.size(); ++i) {
				int farthest_enemy_ind = -1;
				// for each enemy, check if within range
				for (int j = 0; j < enemies.size(); ++j) {
					if (towers.get(i).range > Raymath.Vector2Distance(towers.get(i).coord, enemies.get(j).coord)) {
						if (farthest_enemy_ind == -1)
							farthest_enemy_ind = j;
						if (enemies.get(j).progress > enemies.get(farthest_enemy_ind).progress) {
							farthest_enemy_ind = j;
						}
					}
				}
				if (farthest_enemy_ind != -1) {
					// subtract damage from enemy health
					enemies.get(farthest_enemy_ind).curr_health -= towers.get(i).damage;
					// draw laser shot
					rlj.shapes.DrawLineV(towers.get(i).coord, enemies.get(farthest_enemy_ind).coord, Color.RED);
				}
			}
			
			//check if any enemies died; if they did, delete them
			for (int i = 0; i < enemies.size(); ++i) {
				if (enemies.get(i).curr_health <= 0) {
					player_money += (Math.sqrt(enemies.get(i).total_health) / 8) + 1;
					enemies.remove(i);
					--i;
				}
			}
		}
	}
	
	private void update_enemies() {
		
		for (int i = 0; i < enemies.size(); ++i) {
			// tells enemies to update their progress based on their speed
			enemies.get(i).step();
			// updates enemy coords based on their new progress
			enemies.get(i).coord = map.get_loc(enemies.get(i).progress);
			// checks if enemies have reached the exit; if so, reduce player health and remove enemy
			if (enemies.get(i).progress >= 1.0) {
				player_health -= 3;
				enemies.remove(i);
				--i;
			}
		}
	}
	
	private void level_up() {
		
		++level;
		Random rand = new Random();
		for (int i = 0; i < level * 2; ++i) {
			int speed = rand.nextInt((level + 1) * 5) + 30;
			Enemy n = new Enemy((float) -((Math.sqrt((float) i + rand.nextFloat())) / 80f),
						speed,
						(((level + 1) * 100) + 300) - (speed * 10));
			n.speed = Math.min((int) (n.speed * 0.8f), 200);
			
			enemies.add(n);
			
		}
	}
	
	private void load_defaults() {
		if (db.getEnemyHealth("1") != -1) {
			def_enemy = new Enemy(new Vector2(-10, -10), db.getEnemySpeed("1"), db.getEnemyHealth("1"));
			def_tower = new Tower(new Vector2(-10, -10), db.getTowerRange("1"), db.getTowerDamage("1"));
		} else {
			def_enemy = new Enemy();
			def_tower = new Tower();
		}
	}
	
	public void upgrade_h_range() {
		if (player_money >=  (towers.get(h_tower_ind).range - (def_tower.range - 10))) {
			player_money -= (towers.get(h_tower_ind).range - (def_tower.range - 10));
			towers.get(h_tower_ind).upgrade_range();
		}
	}
	
	public void upgrade_h_damage() {
		if (player_money >= (towers.get(h_tower_ind).damage * 2)) {
			player_money -= (towers.get(h_tower_ind).damage * 2);
			towers.get(h_tower_ind).upgrade_damage();
		}
	}
	
	public void regen_map() {
		map = new Map(screen_width, screen_height, (screen_width / 5), 32);
	}
	
	public void update_screen_size(int width, int height) {
		screen_width = width;
		screen_height = height;
	}
}
