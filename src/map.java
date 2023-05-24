
import java.util.ArrayList;
import java.util.Random;
import com.raylib.java.raymath.Vector2;
import com.raylib.java.raymath.Raymath;

// stores coordinate data for each point in the map
public class map {
	
	final int max_segments = 50;
	
	int segment_count = 1; // total segments in map
	ArrayList<Vector2> points = new ArrayList<>(); // arraylist of coordinates starting with enemy entrance and ending with player base
	
	public map(int screen_width, int screen_height) {
		generate_map(screen_width, screen_height);
	}
	
	private void generate_map(int screen_width, int screen_height) {
		int segment_length = screen_height / 15; // length of each segment, defined relative to window size
		int menu_margin = screen_width / 5; // 20% of the window is reserved for a menu on the left side of the screen
		
		Random rand = new Random();
		
		Vector2 entrance = new Vector2();
		Vector2 exit = new Vector2();
		
		// generates random vertical position for map entrance with a margin on the top and bottom
		entrance.y = rand.nextInt(screen_height - segment_length + 1 ) + (segment_length / 2);
		// x coordinate is determined by the right edge of the menu
		entrance.x = menu_margin;
		
		// generates random y coordinate for map exit with margin on the top and bottom
		exit.y = rand.nextInt(screen_height - segment_length + 1) + (segment_length / 2);
		exit.x = screen_width;
		
		// regenerates points until successful or 1000 failed iterations 
		int iterations = 0;
		while(generate_points(entrance, exit, segment_length, menu_margin, screen_width, screen_height) != 0 && iterations < 10) {
			System.out.println("regenerating map...");
			points = new ArrayList<>(); // points array is reset after every failed iteration
			segment_count = 1; // segment count reset after every failed iteration
			++iterations;
		}
		if (iterations == 10) {
			System.out.println("map generation failed.");
		}
	}
	
	private int generate_points(Vector2 entrance, Vector2 exit, int segment_length, int menu_margin, int screen_width, int screen_height) {
		
		/*
		 * curr represents the currently generating point in the map; it "walks" in segment_length steps from the entrance 
		 * in a random direction continuously until it's close to the exit, at which point it jumps there.
		 */
		Vector2 curr = new Vector2();
		Double dir; // direction to walk in (radians), randomized before every step
		curr.x = entrance.x;
		curr.y = entrance.y;
		
		Random rand = new Random();
		
		points.add(entrance);
		
		do {
			System.out.println("generating point...");
			dir = (rand.nextDouble() * 2.0 * 3.14159265358979); // get random direction in radians
			
			curr.x += segment_length * Math.cos(dir);
			curr.y += segment_length * Math.sin(dir);
			System.out.println(String.format("x:%f\ny:%f", curr.x, curr.y));
			System.out.println(Raymath.Vector2Distance(curr, exit));
			// verifies x coordinate is on screen and within margin; if not, resets curr and skips this iteration
			if (curr.x < (menu_margin + (segment_length / 2)) || curr.x > (screen_width - (segment_length / 2))) {
				System.out.println("out of bounds x");
				curr.x -= segment_length * Math.cos(dir);
				curr.y -= segment_length * Math.sin(dir);
				continue;
			}
			
			// verifies y coordinate is on screen and within margin; if not, resets curr and skips this iteration
			if (curr.y < (segment_length / 2) || curr.y > (screen_height - (segment_length / 2))) {
				System.out.println("out of bounds y");
				curr.x -= segment_length * Math.cos(dir);
				curr.y -= segment_length * Math.sin(dir);
				continue;
			}
			
			// checks if more than max_segments have been generated, if so, resets the generation algorithm to try again
			if (segment_count >= max_segments) {
				return 1;
			}
			
			points.add(curr);
			segment_count++;
			
			// checks if curr is close to exit, if it is, loop is exited and generation is complete
			if (Raymath.Vector2Distance(curr, exit) < (segment_length * 2.0)) {
				break;
			}
			
		} while (true);
		
		points.add(exit);
		segment_count++;
		
		return 0;
	}
}
