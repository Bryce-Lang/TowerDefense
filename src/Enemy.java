import com.raylib.java.Raylib;
import com.raylib.java.core.Color;
import com.raylib.java.raymath.Vector2;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Enemy {
    int health = 0;
    float xPos;
    float yPos;
    public Enemy(int health, float xPos, float yPos){
        this.health = health;
        this.xPos = xPos;
        this.yPos = yPos;
    }
    int i = 0;
    public void draw(Raylib rlj, map map, float timer){
        ArrayList<Vector2> points = map.points;
        if(timer == 0) {
            i = 0;
        }

        if(i == points.size())
            return;
        rlj.shapes.DrawCircleV(points.get(i), (float) 10.0, Color.RED);
        i++;
    }

}
