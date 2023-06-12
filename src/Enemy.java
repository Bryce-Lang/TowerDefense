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
