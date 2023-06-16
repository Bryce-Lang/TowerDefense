
import com.raylib.java.raymath.Vector2;
import com.raylib.java.Raylib;
import com.raylib.java.core.Color;

public class Tower {
	public Vector2 coord;
	
	public int range;
	
	public int damage;
	
	public Tower() {
		coord = new Vector2(0, 0);
		range = 100;
		damage = 4;
	}
	
	public Tower(Vector2 set_coord) {
		coord = set_coord;
		range = 100;
		damage = 4;
	}
	
	public Tower(Vector2 set_coord, int set_range, int set_damage) {
		coord = set_coord;
		range = set_range;
		damage = set_damage;
	}
	
	public void draw(Raylib rlj) {
		rlj.shapes.DrawCircleV(coord, 10f, Color.DARKPURPLE);
		rlj.shapes.DrawCircleV(coord, 3f, Color.SKYBLUE);
	}
}
