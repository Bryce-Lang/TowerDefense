import com.raylib.java.core.Color;
import com.raylib.java.raymath.Vector2;
import com.raylib.java.Raylib;

public class FastEnemy extends Enemy{
	
	public FastEnemy() {
		super();
	}
	
	public FastEnemy(int set_speed, int set_health) {
		super(set_speed, set_health);
	}
	
	public FastEnemy(float set_prog, int set_speed, int set_health) {
		super(set_prog, set_speed, set_health);
	}
	
	public FastEnemy(Vector2 set_coord, int set_speed, int set_health) {
		super(set_coord, set_speed, set_health);
	}
	
	public void draw(Raylib rlj) {
		rlj.shapes.DrawEllipse((int) coord.x,(int) coord.y, 8, 5, Color.YELLOW);
		rlj.shapes.DrawEllipse((int) coord.x + 3,(int) coord.y, 1, 3, Color.BLACK);
		rlj.shapes.DrawEllipse((int) coord.x - 3,(int) coord.y, 1, 3, Color.BLACK);
		if (total_health != curr_health) {
			float health_bar_width = (float) Math.sqrt(total_health);
			float remaining_bar_width = (float) Math.sqrt(curr_health);
			
			rlj.shapes.DrawRectangle((int) (coord.x - (health_bar_width / 2f)),(int) coord.y - 14,(int) health_bar_width, 4, Color.RED);
			
			rlj.shapes.DrawRectangle((int) (coord.x - (health_bar_width / 2f)),(int) coord.y - 14,(int)  remaining_bar_width, 4, Color.GREEN);
		}
	}
}
