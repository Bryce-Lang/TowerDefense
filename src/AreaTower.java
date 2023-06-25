
import com.raylib.java.raymath.Vector2;
import com.raylib.java.Raylib;
import com.raylib.java.core.Color;

public class AreaTower extends Tower{
	
	public AreaTower() {
		super();
		id = "area";
	}
	
	public AreaTower(Vector2 set_coord) {
		super(set_coord);
		id = "area";
	}
	
	public AreaTower(Vector2 set_coord, int set_range, int set_damage) {
		super(set_coord, set_range, set_damage);
		id = "area";
	}
	
	public void draw(Raylib rlj) {
		rlj.shapes.DrawCircleV(coord, 10f, Color.DARKGREEN);
		rlj.shapes.DrawCircleV(coord, 3f, Color.ORANGE);
	}
}
