
import com.raylib.java.raymath.Vector2;
import com.raylib.java.Raylib;
import com.raylib.java.core.Color;

public class Enemy {
	public Vector2 coord;
	
	public int speed;
	
	public int total_health;
	
	public int curr_health;
	
	public float progress;
	
	public Enemy(int set_speed, int set_health) {
		coord = new Vector2(-10000, -10000);
		speed = set_speed;
		total_health = set_health;
		curr_health = set_health;
		progress = 0f;
	}
	
	public Enemy(float set_prog, int set_speed, int set_health) {
		progress = set_prog;
		coord = new Vector2(-10000, -10000);
		speed = set_speed;
		total_health = set_health;
		curr_health = set_health;
	}
	
	public Enemy(Vector2 set_coord, int set_speed, int set_health) {
		coord = new Vector2(-10000, -10000);
		speed = set_speed;
		total_health = set_health;
		curr_health = set_health;
		progress = 0f;
	}
	
	public void draw(Raylib rlj) {
		rlj.shapes.DrawEllipse((int) coord.x,(int) coord.y, 12, 8, Color.MAROON);
		rlj.shapes.DrawEllipse((int) coord.x + 5,(int) coord.y, 2, 4, Color.BLACK);
		rlj.shapes.DrawEllipse((int) coord.x - 5,(int) coord.y, 2, 4, Color.BLACK);
		if (total_health != curr_health) {
			float health_bar_width = (float) Math.sqrt(total_health);
			float remaining_bar_width = (float) Math.sqrt(curr_health);
			
			rlj.shapes.DrawRectangle((int) (coord.x - (health_bar_width / 2f)),(int) coord.y - 14,(int) health_bar_width, 4, Color.RED);
			
			rlj.shapes.DrawRectangle((int) (coord.x - (health_bar_width / 2f)),(int) coord.y - 14,(int)  remaining_bar_width, 4, Color.GREEN);
		}
	}
	
	public void step() {
		progress += (speed / 100000f);
	}
}


/* deprecated enemy class
import com.raylib.java.Raylib;
import com.raylib.java.core.Color;
import com.raylib.java.raymath.Vector2;
import java.util.ArrayList;

public class Enemy {
    int health = 0;
    int speed;
    long offset;
    float xPos;
    float yPos;
    Thread enemyThread;
    public Enemy(int health, float xPos, float yPos, long offset, int speed){
        this.health = health;
        this.xPos = xPos;
        this.yPos = yPos;
        this.offset = offset;
        this.speed = speed;
        enemyThread = new Thread();
        enemyThread.start();
    }
    int i = 0;
    public void draw(Raylib rlj, Map map, float timer){

        ArrayList<Vector2> points = map.points;
        //checks if stage has been reset
        if(timer == 0) {
            i = 0;
        }
        //if enemy reaches end of map
        if(i == points.size()){
            return;
        }
        //waits an iteration of the main while loop by the number determined from offset. ie: offset = 10, wait 10 iterations before drawing
        if(offset == 0){
            try {
                enemyThread.sleep(speed);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            rlj.shapes.DrawCircleV(points.get(i), (float) 10.0, Color.RED);
            i++;
        }
        else
            offset--;
    }

}
*/