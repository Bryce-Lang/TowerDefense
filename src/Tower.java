
import com.raylib.java.raymath.Vector2;
import com.raylib.java.Raylib;
import com.raylib.java.core.Color;

public class Tower {
	
	private static final int DEF_RANGE = 80;
	private static final int DEF_DAMAGE = 4;
	
	public String id;
	
	public Vector2 coord;
	
	public int range;
	
	public int damage;
	
	public Tower() {
		coord = new Vector2(0, 0);
		range = DEF_RANGE;
		damage = DEF_DAMAGE;
		id = "single";
	}
	
	public Tower(Vector2 set_coord) {
		coord = set_coord;
		range = DEF_RANGE;
		damage = DEF_DAMAGE;
		id = "single";
	}
	
	public Tower(Vector2 set_coord, int set_range, int set_damage) {
		coord = set_coord;
		range = set_range;
		damage = set_damage;
		id = "single";
	}
	
	public int get_range_upgrade_cost() {
		return range - (DEF_RANGE - 10);
	}
	
	public int get_damage_upgrade_cost() {
		return damage * 2;
	}
	
	public void upgrade_range() {
		range += 7 - Math.min(range / 150, 4);
	}
	
	public void upgrade_damage() {
		damage += Math.sqrt(damage);
	}
	
	public void draw(Raylib rlj) {
		rlj.shapes.DrawCircleV(coord, 10f, Color.DARKPURPLE);
		rlj.shapes.DrawCircleV(coord, 3f, Color.SKYBLUE);
	}
}
