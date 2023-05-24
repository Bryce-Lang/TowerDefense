
import java.util.ArrayList;
import java.util.Random;
import com.raylib.java.raymath.Vector2;
import com.raylib.java.raymath.Raymath;

// stores coordinate data for each point in the map
public class map {
	int screen_width;
	int screen_height;
	int menu_margin;
	
	int point_num = 15;
	
	ArrayList<Vector2> points = new ArrayList<>(); // arraylist of coordinates starting with enemy entrance and ending with player base
	
	
	public map(int in_screen_width, int in_screen_height, int in_menu_margin, int in_point_num) {
		screen_width = in_screen_width;
		screen_height = in_screen_height;
		menu_margin = in_menu_margin;
		point_num = in_point_num;
		// points array initialized with given point number + 2 for entrance and exit
		for (int i = 0; i < (point_num + 2); ++i) {
			Vector2 p = new Vector2(0, 0);
			points.add(p);
		}
		generate_map();
	}
	
	private void generate_map() {
		
		// path points aren't allowed too close to the edge, margin used in generation to verify
		int margin_size = screen_width / 16;
		
		Random rand = new Random();
		
		Vector2 entrance = new Vector2();
		Vector2 exit = new Vector2();
		
		// generates random vertical position for map entrance with a margin on the top and bottom
		entrance.y = rand.nextInt(screen_height - (margin_size * 2) + 1 ) + margin_size;
		// x coordinate is determined by the right edge of the menu
		entrance.x = menu_margin;
		// add entrance to points array at index 0
		points.get(0).x = entrance.x;
		points.get(0).y = entrance.y;
		
		// generates random y coordinate for map exit with margin on the top and bottom
		exit.y = rand.nextInt(screen_height - (margin_size * 2) + 1)  + margin_size;
		exit.x = screen_width;
		// add exit to points array at appropriate index
		points.get(point_num + 1).x = exit.x;
		points.get(point_num + 1).y = exit.y;
		
		ArrayList<Vector2> tmp = new ArrayList<>();
		for (int i = 0; i < point_num; ++i) {
			
			// declare and generate a new point in the map
			Vector2 p = new Vector2();
			p.x = rand.nextInt(screen_width - menu_margin - margin_size + 1) + (menu_margin + (margin_size / 2));
			p.y = rand.nextInt(screen_height - margin_size + 1) + (margin_size / 2);
			
			// first point needs to be added manually
			if (i == 0) {
				tmp.add(p);
				continue;
			}
			
			// loop through points already in map, verifying that they are reasonably far away, adding if they are
			for (int j = tmp.size() - 1; j >= 0; --j) {
				if (Raymath.Vector2Distance(p, tmp.get(j)) < screen_height / point_num) {
					--i;
					break;
				} else if (j == 0) { // every point has been checked, add p to tmp
					tmp.add(p);
				}
			}
		}
		//System.out.println("point generation complete");
		
		/*
		 * starting from the entrance and exit, add closest points until no more points remain
		 * then connect last two points
		 * points are "connected" implicitly; in practice the are just next to each other
		 * in the points arraylist
		 */
		Vector2 entrance_lead = new Vector2(entrance.x, entrance.y);
		Vector2 exit_lead = new Vector2(exit.x, exit.y);
		// point index used to add new points at correct index in points arraylist
		int point_index = 1;
		while(tmp.size() > 0) {
			// index of the current closest point in tmp
			int cp_index = 0;
			
			// find closest point in tmp to entrance lead ---------------------------------------------------------------------------
			for (int i = 0; i < tmp.size(); ++i) {
				if (Raymath.Vector2Distance(entrance_lead, tmp.get(i)) < Raymath.Vector2Distance(entrance_lead, tmp.get(cp_index))) {
					cp_index = i;
				}
			}
			// closest point to entrance_lead added to points arraylist
			points.get(point_index).x = tmp.get(cp_index).x;
			points.get(point_index).y = tmp.get(cp_index).y;
			
			// entrance lead moved to closest point remaining in tmp
			entrance_lead.x = tmp.get(cp_index).x;
			entrance_lead.y = tmp.get(cp_index).y;
			
			// closest point removed from tmp array so it won't be selected again
			tmp.remove(cp_index);
			
			// -----------------------------------------------------------------------------------------------------------------------
			
			// verify that there are more points in tmp to be added, if not break out of loop
			if (tmp.size() == 0)
				break;
			
			// point index is incremented here so that it can be subtracted from points.size() to get correct index
			++point_index;
			
			// reset cp_index location for exit lead; only important because cp_index could now be out of bounds
			cp_index = 0;
			
			// find closest point in tmp to exit lead -------------------------------------------------------------------------------
			for (int i = 0; i < tmp.size(); ++i) {
				if (Raymath.Vector2Distance(exit_lead, tmp.get(i)) < Raymath.Vector2Distance(exit_lead, tmp.get(cp_index))) {
					cp_index = i;
				}
			}
			
			points.get(points.size() - point_index).x = tmp.get(cp_index).x;
			points.get(points.size() - point_index).y = tmp.get(cp_index).y;
			
			exit_lead.x = tmp.get(cp_index).x;
			exit_lead.y = tmp.get(cp_index).y;
			
			tmp.remove(cp_index);
			
			// -----------------------------------------------------------------------------------------------------------------------
		}
	}
}
