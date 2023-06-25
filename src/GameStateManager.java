import java.util.ArrayList;
import java.util.Random;

import java.sql.SQLException;

import static com.raylib.java.core.input.Mouse.MouseButton.MOUSE_BUTTON_LEFT;
import static com.raylib.java.core.input.Mouse.MouseButton.MOUSE_BUTTON_RIGHT;
import com.raylib.java.shapes.Rectangle;
import com.raylib.java.Raylib;
import com.raylib.java.core.Color;
import com.raylib.java.core.rCore;
import com.raylib.java.raymath.Raymath;
import com.raylib.java.raymath.Vector2;

public class GameStateManager {
	
	// defaults; should be updated with the constructor
	private static int screen_width = 960;
	private static int screen_height = 540;
	
	private static final Color BUTTON_0 = new Color(0, 40, 40, 255);
	private static final Color BUTTON_1 = new Color(120, 120, 120, 255);
	private static final Color BUTTON_2 = new Color(0, 60, 0, 255);
	
	private static int menu_margin;
	
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
	private AreaTower def_tower_area;
	
	private Button buy_tower_single;
	private Button buy_tower_area;
	private Button upgrade_range;
	private Button upgrade_damage;
	private Button play_next_round;
	private Button save_game;
	private Button regen_map;
	
	private boolean bought_tower_single = false;
	private boolean bought_tower_area = false;
	
	private boolean play = false;
	
	public GameStateManager(int set_width, int set_height, Raylib set_rlj, Database set_db) {
		screen_width = set_width;
		screen_height = set_height;
		
		rlj = set_rlj;
		db = set_db;
		
		player_health = 100;
		player_money = 1000;
		
		menu_margin = screen_width / 5;
		
		map = new Map(screen_width, screen_height, menu_margin);
		
		init_buttons();
		
		load_defaults();
	}
	
	public GameStateManager(int set_width, int set_height, Raylib set_rlj, Database set_db, int set_level) {
		screen_width = set_width;
		screen_height = set_height;
		level = set_level;
		
		rlj = set_rlj;
		db = set_db;
		
		player_health = 100;
		player_money = 1000;
		
		menu_margin = screen_width / 5;
		
		map = new Map(screen_width, screen_height, menu_margin);
		
		init_buttons();
		
		load_defaults();
	}
	
	private void init_buttons() {
		upgrade_range = new Button("", new Rectangle(10, (screen_height - 300), 170, 40), BUTTON_0);
		upgrade_damage = new Button("", new Rectangle(10, (screen_height - 250), 170, 40), BUTTON_0);
		
		buy_tower_single = new Button("buy    single", new Rectangle(10, (screen_height - 170), 145, 40), BUTTON_0);
		buy_tower_area = new Button("buy    area", new Rectangle(10, (screen_height - 120), 145, 40), BUTTON_0);
		
		play_next_round = new Button("", new Rectangle(10, (screen_height - 50), 40, 40), BUTTON_1);
		save_game = new Button("save", new Rectangle(90, (screen_height - 50), 65, 40), BUTTON_1);
		
		regen_map = new Button("new map", new Rectangle(10, (screen_height - 360), 100, 40), BUTTON_2);
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
									   new Color(255, 255, 255, 170));
			rlj.shapes.DrawCircleLines((int) towers.get(h_tower_ind).coord.x,
									   (int) towers.get(h_tower_ind).coord.y,
									   11,
									   new Color(255, 255, 0, 200));
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
	
	public void draw_gui() {
		buy_tower_single.render(rlj);
		buy_tower_area.render(rlj);
		
		if (level == 0) {
			regen_map.render(rlj);
		}
		
		if (h_tower_ind != -1) {
			upgrade_range.render(rlj);
			rlj.text.DrawText("+range: $" + towers.get(h_tower_ind).get_range_upgrade_cost(),
							  20,
							  (screen_height - 290),
							  20,
							  Color.WHITE);
			upgrade_damage.render(rlj);
			rlj.text.DrawText("+damage: $" + towers.get(h_tower_ind).get_damage_upgrade_cost(),
							  20,
							  (screen_height - 240),
							  20,
							  Color.WHITE);
		}
		
		play_next_round.render(rlj);
		
		if (play) {
			rlj.shapes.DrawRectangle(20,screen_height - 40, 6, 20, Color.DARKGRAY);
			rlj.shapes.DrawRectangle(34, screen_height - 40, 6, 20, Color.DARKGRAY);
		} else {
			rlj.shapes.DrawTriangle(new Vector2(22, (screen_height - 40)),
									new Vector2(22, (screen_height - 20)),
									new Vector2(42, (screen_height - 30)),
									Color.DARKGREEN);
		}
		
		save_game.render(rlj);
		
		if (bought_tower_single || bought_tower_area) {
			towers.get(towers.size() - 1).coord = rCore.GetMousePosition();
			towers.get(towers.size() - 1).draw(rlj);
		}
		
		def_tower.draw(rlj);
		def_tower_area.draw(rlj);
	}
	
	// takes in frames counter to determine whether towers should shoot
	public void run_frame(int tick) {
		
		// check button clicks
		if (rlj.core.IsMouseButtonPressed(MOUSE_BUTTON_LEFT)) {
			
			Vector2 mouse_pos = rCore.GetMousePosition();
			
			if (!bought_tower_single && !bought_tower_area && buy_tower_single.isClicked((int) mouse_pos.x, (int) mouse_pos.y)) {
				towers.add(new Tower());
				bought_tower_single = true;
				
			} else if (!bought_tower_single && !bought_tower_area && buy_tower_area.isClicked((int) mouse_pos.x, (int) mouse_pos.y)) {
				towers.add(new AreaTower());
				bought_tower_area = true;
				
			} else if (h_tower_ind != -1 && upgrade_range.isClicked((int) mouse_pos.x, (int) mouse_pos.y)) {
				upgrade_h_range();
				
			} else if (h_tower_ind != -1 && upgrade_damage.isClicked((int) mouse_pos.x, (int) mouse_pos.y)) {
				upgrade_h_damage();
				
			} else if (play_next_round.isClicked((int) mouse_pos.x, (int) mouse_pos.y)) {
				play = !play;
				
			} else if (save_game.isClicked((int) mouse_pos.x, (int) mouse_pos.y)) {
				
			} else if (bought_tower_single || bought_tower_area) {
				if (rCore.GetMousePosition().x > menu_margin) {
					boolean valid_pos = true;
					for (int i = 0; i < (towers.size() - 1); ++i) {
						if (15 > Raymath.Vector2Distance(towers.get(i).coord, rCore.GetMousePosition()))
							valid_pos = false;
					}
					if (valid_pos) {
						bought_tower_single = false;
						bought_tower_area = false;
						h_tower_ind = towers.size() - 1;
					}
				}
				
			} else if (level == 0 && regen_map.isClicked((int) mouse_pos.x, (int) mouse_pos.y)) {
				map = new Map(screen_width, screen_height, menu_margin);
				
			} else {
				if (towers.size() > 0) {
					// select closest tower if no button is pressed
					int closest_tower_ind = 0;
					for (int i = 0; i < towers.size(); ++i) {
						if (Raymath.Vector2Distance(rCore.GetMousePosition(), towers.get(closest_tower_ind).coord) > 
							Raymath.Vector2Distance(rCore.GetMousePosition(), towers.get(i).coord)) {
							
							closest_tower_ind = i;
						}
					}
					if (30 > Raymath.Vector2Distance(rCore.GetMousePosition(), towers.get(closest_tower_ind).coord)) {
						if (h_tower_ind == closest_tower_ind) {
							h_tower_ind = -1;
						} else {
							h_tower_ind = closest_tower_ind;
						}
					}
				}
			}
		}
		
		// if autoplay is on and no enemies remain, play next level
		if (play && enemies.size() == 0)
			level_up();
		
		// draw map, enemies, and towers
		draw();
		
		// fire towers
		fire_towers();
		
		// move enemies / move them along path
		update_enemies();
	}
	
	private void fire_towers() {
		
		if (enemies.size() > 0) {
			// for each tower, find the farthest enemy along path
			for (int i = 0; i < towers.size(); ++i) {
				switch (towers.get(i).id) {
					// single fire towers do all their damage to the enemy farthest along the path within their range
					case "single":
						int farthest_enemy_ind = -1;
						// for each enemy, check if within range and find farthest enemy along path
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
						break;
						
					// area attack towers do damage to all towers within range; their attack power is divided by number of enemies attacked
					case "area":
						ArrayList<Integer> enemies_within_range = new ArrayList<>();
						for (int j = 0; j < enemies.size(); ++j) {
							if (towers.get(i).range > Raymath.Vector2Distance(towers.get(i).coord, enemies.get(j).coord)) {
								enemies_within_range.add(j);
							}
						}
						for (int j = 0; j < enemies_within_range.size(); ++j) {
							enemies.get(enemies_within_range.get(j)).curr_health -= Math.max(1, (towers.get(i).damage / enemies_within_range.size()));
							rlj.shapes.DrawLineV(towers.get(i).coord, enemies.get(enemies_within_range.get(j)).coord, Color.RED);
						}
						break;
						
					default:
						break;
				}
			}
			
			//check if any enemies died; if they did, delete them and pay their worth
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
		
		// add normal enemies
		for (int i = 0; i < level * 2; ++i) {
			int speed = rand.nextInt((level + 1) * 5) + 30;
			Enemy n = new Enemy((float) -((Math.sqrt((float) i + rand.nextFloat())) / 80f),
						speed,
						((level * 100) + 300) - (speed * 10));
			n.speed = Math.min((int) (n.speed * 0.8f), 200);
			
			enemies.add(n);
		}
		
		// add fast enemies
		for (int i = 0; i < level - 5; ++i) {
			int speed = rand.nextInt((level + 1) * 10) + 200;
			Enemy n = new FastEnemy((float) -(((float) i + rand.nextFloat()) / (30f + ((float) level / 2f))),
						speed,
						((level * 60) + 300) - (speed));
			n.speed = Math.min((int) (n.speed * 0.8f), 400);
			
			enemies.add(n);
		}
		
		// add boss enemies
		for (int i = 0; i < Math.log(level - 20); ++i) {
			int speed = rand.nextInt(level * 2) + 30;
			Enemy n = new BossEnemy((float) -((Math.sqrt((float) i + rand.nextFloat())) / 40f),
						speed,
						((level * 1000) + 300) - (speed * 10));
			n.speed = Math.min((int) (n.speed * 0.8f), 200);
			
			enemies.add(n);
		}
	}
	
	private void load_defaults() {
		//if() {
		//	def_enemy = new Enemy(new Vector2(-10, -10), db.getEnemySpeed("1"), db.getEnemyHealth("1"));
		//	def_tower = new Tower(new Vector2(-10, -10), db.getTowerRange("1"), db.getTowerDamage("1"));
		//} else {
			def_enemy = new Enemy();
			def_tower = new Tower(new Vector2(72, screen_height - 150));
			def_tower_area = new AreaTower(new Vector2(72, screen_height - 100));
		//}
	}
	
	public void upgrade_h_range() {
		if (player_money >=  (towers.get(h_tower_ind).get_range_upgrade_cost())) {
			player_money -= (towers.get(h_tower_ind).get_range_upgrade_cost());
			towers.get(h_tower_ind).upgrade_range();
		}
		
	}
	
	public void upgrade_h_damage() {
		if (player_money >= (towers.get(h_tower_ind).get_damage_upgrade_cost())) {
			player_money -= (towers.get(h_tower_ind).get_damage_upgrade_cost());
			towers.get(h_tower_ind).upgrade_damage();
		}
	}
	
	public void regen_map() {
		map = new Map(screen_width, screen_height, (screen_width / 5));
	}
	/*
	public void update_screen_size(int width, int height) {
		screen_width = width;
		screen_height = height;
	}
	*/
}
