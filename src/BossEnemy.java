import com.raylib.java.Raylib;
import com.raylib.java.core.Color;
import com.raylib.java.raymath.Vector2;

public class BossEnemy extends Enemy{
	
	public BossEnemy() {
		super();
	}
	
	public BossEnemy(int set_speed, int set_health) {
		super(set_speed, set_health);
	}
	
	public BossEnemy(float set_prog, int set_speed, int set_health) {
		super(set_prog, set_speed, set_health);
	}
	
	public BossEnemy(Vector2 set_coord, int set_speed, int set_health) {
		super(set_coord, set_speed, set_health);
	}
	
	public void draw(Raylib rlj) {
		rlj.shapes.DrawEllipse((int) coord.x,(int) coord.y, 24, 16, Color.ORANGE);
		rlj.shapes.DrawEllipse((int) coord.x + 8,(int) coord.y, 3, 6, Color.BLACK);
		rlj.shapes.DrawEllipse((int) coord.x - 8,(int) coord.y, 3, 6, Color.BLACK);
		if (total_health != curr_health) {
			float health_bar_width = (float) Math.sqrt(total_health);
			float remaining_bar_width = (float) Math.sqrt(curr_health);
			
			rlj.shapes.DrawRectangle((int) (coord.x - (health_bar_width / 2f)),(int) coord.y - 22,(int) health_bar_width, 4, Color.RED);
			
			rlj.shapes.DrawRectangle((int) (coord.x - (health_bar_width / 2f)),(int) coord.y - 22,(int)  remaining_bar_width, 4, Color.GREEN);
		}
	}
}
