
import java.util.ArrayList;
import java.util.Random;
import com.raylib.java.Raylib;
import com.raylib.java.raymath.Vector2;
import com.raylib.java.core.Color;
import com.raylib.java.raymath.Raymath;

// stores coordinate data for each point in the map
public class map {
	int screen_width;
	int screen_height;
	int menu_margin;
	
	int point_num = 15;
	
	// arraylist of coordinates starting with enemy entrance and ending with player base
	ArrayList<Vector2> points = new ArrayList<>();
	
	// TODO: delete; only used for debugging
	ArrayList<Vector2> debug_points = new ArrayList<>();
	
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
		
		Random rand = new Random();
		
		Vector2 entrance = new Vector2();
		Vector2 exit = new Vector2();
		
		// TODO: allow entrance and exit to be generated anywhere along the border rather than just stuck to left and right sides
		
		// generates random vertical position for map entrance with a margin on the top and bottom
		entrance.y = rand.nextInt(screen_height);
		// x coordinate is determined by the right edge of the menu
		entrance.x = menu_margin;
		// add entrance to points array at index 0
		points.set(0, entrance);
		
		// generates random y coordinate for map exit with margin on the top and bottom
		exit.y = rand.nextInt(screen_height);
		exit.x = screen_width;
		// add exit to points array at appropriate index
		points.set(points.size() - 1, exit);
		
		// TODO: reset margin
		ArrayList<Vector2> tmp_points_arr = generate_points(screen_width / 16);
		
		connect_points(entrance, exit, tmp_points_arr);
		
		points = smooth();
	}
	
	// Generates points within the map area buffered by margin_size
	private ArrayList<Vector2> generate_points(int margin_size) {
		
		Random rand = new Random();

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
			
			// loop through points already in map, verifying that they are reasonably far away, adding to points if they are
			for (int j = tmp.size() - 1; j >= 0; --j) {
				// reject point if too close to another
				if (Raymath.Vector2Distance(p, tmp.get(j)) < (screen_height / point_num)) {
					--i;
					break;
				} else if (j == 0) { // every point has been checked, add p to tmp
					tmp.add(p);
				}
			}
		}
		
		return tmp;
	}
	
	// connects previously generated points into a path
	private void connect_points(Vector2 entrance, Vector2 exit, ArrayList<Vector2> tmp) {
		/*
		 * starting from the entrance and exit, add closest points until no more points remain
		 * then connect last two points
		 * points are "connected" implicitly; in practice the are just next to each other
		 * in the points arraylist
		 */
		Vector2 entrance_lead = new Vector2(entrance.x, entrance.y);
		Vector2 exit_lead = new Vector2(exit.x, exit.y);
		
		// point index used to add new points at correct index in points arraylist; starts at 1 because index 0 is for entrance
		int point_index = 1;
		while(tmp.size() > 0) {
			// index of the current closest point in tmp
			int cp_index = 0;
			
			// entrance_lead starts at entrance and "walks" to closest point until no more points are left ---------------------------
			
			// find closest point in tmp to entrance lead
			for (int i = 0; i < tmp.size(); ++i) {
				if (Raymath.Vector2Distance(entrance_lead, tmp.get(i)) < Raymath.Vector2Distance(entrance_lead, tmp.get(cp_index))) {
					cp_index = i;
				}
			}
			// closest point to entrance_lead added to points arraylist
			points.set(point_index, tmp.get(cp_index));
			
			// entrance lead moved to closest point remaining in tmp
			entrance_lead = tmp.get(cp_index);
			
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
			
			// repeat above for exit lead --------------------------------------------------------------------------------------------
			for (int i = 0; i < tmp.size(); ++i) {
				if (Raymath.Vector2Distance(exit_lead, tmp.get(i)) < Raymath.Vector2Distance(exit_lead, tmp.get(cp_index))) {
					cp_index = i;
				}
			}
			
			points.set(points.size() - point_index, tmp.get(cp_index));
			
			exit_lead = tmp.get(cp_index);
			
			tmp.remove(cp_index);
			
			// -----------------------------------------------------------------------------------------------------------------------
		}
	}
	
	// calculates smoothed points using a mishmash of Catmull-Rom velocity estimation with cubic bezier curve calculation
	private ArrayList<Vector2> smooth() {
		
		// first step: add points beyond the entrance and exit as splines don't go to end points -------------------------------------
		Vector2 entrance_ghost = reflect(points.get(1), points.get(0));
		entrance_ghost.y = points.get(1).y;
		points.add(0, entrance_ghost);
		
		Vector2 exit_ghost = reflect(points.get(points.size() - 2), points.get(points.size() - 1));
		exit_ghost.y = points.get(points.size() - 2).y;
		points.add(exit_ghost);
		points.add(exit_ghost);
		
		// ---------------------------------------------------------------------------------------------------------------------------
		
		// second step: loop through points ArrayList, adding appropriate number of points based on distance between points ----------
		// a new arraylist is made to simplify indexing; we copy points in as we go. this will be returned when finished
		ArrayList<Vector2> tmp = new ArrayList<>();
		
		tmp.add(entrance_ghost);
		tmp.add(exit_ghost);
		tmp.add(exit_ghost);
		// loop over all points
		for (int i = 1; i < (points.size() - 2); ++i) {
			// add 
			tmp.add(tmp.size() - 2, points.get(i));
			debug_points.add(points.get(i));  // TODO: delete
			
			// distance from current point to next point
			int p_distance = (int) Raymath.Vector2Distance(points.get(i), points.get(i+1));
			
			// calculate vector between neighboring points and divide by 2
			Vector2 curr_control = Raymath.Vector2Subtract(points.get(i + 1), points.get(i - 1));
			curr_control = Raymath.Vector2Divide(curr_control, new Vector2(6, 6));
			// then add to our current point to estimate a control point
			curr_control = Raymath.Vector2Add(curr_control, points.get(i));
			debug_points.add(curr_control); // TODO: delete
			
			// we now need to estimate the control point close to the next point
			// this is done in similar fashion
			Vector2 next_control = Raymath.Vector2Subtract(points.get(i + 2), points.get(i));
			next_control = Raymath.Vector2Divide(next_control, new Vector2(6, 6));
			next_control = Raymath.Vector2Add(next_control, points.get(i + 1));
			// except we reflect it over it's point
			next_control = reflect(next_control, points.get(i + 1));
			debug_points.add(next_control); // TODO: delete
			
			// iterates between current point and the next, adding p_distance/10 number of points evenly spread
			for (int j = 0; j < (p_distance / 5); ++j) {
				tmp.add(tmp.size() - 2, smooth_lerp(i, j * (1.0 / (p_distance / 5.0)), curr_control, next_control));
			}
		}
		// --------------------------------------------------------------------------------------------------------------------------
		
		// remove end points used for interpolation
		tmp.remove(0);
		tmp.remove(tmp.size() - 1);
		tmp.remove(tmp.size() - 1);
		
		return tmp;
	}
	
	// quadratic bezier lerping given current point index and estimated control points
	private Vector2 smooth_lerp(int p_ind, double by, Vector2 curr_control, Vector2 next_control) {
		// linearly interpolate between current point and control point
		Vector2 to_curr_control = Raymath.Vector2Lerp(points.get(p_ind), curr_control, (float) by);
		
		// linearly interpolate between curr control point and next control point
		Vector2 to_next_control = Raymath.Vector2Lerp(curr_control, next_control, (float) by);
		
		// linearly interpolate between next control point and next point
		Vector2 to_next = Raymath.Vector2Lerp(next_control, points.get(p_ind + 1), (float) by);
		
		// linearly interpolate between to_curr_control and to_next_control
		Vector2 lerp_1 = Raymath.Vector2Lerp(to_curr_control, to_next_control, (float) by);
		
		// linearly interpolate between to_next_control and to_next
		Vector2 lerp_2 = Raymath.Vector2Lerp(to_next_control, to_next, (float) by);
		
		// linearly interpolate between the previously calculated points
		return Raymath.Vector2Lerp(lerp_1, lerp_2, (float) by);
	}
	
	// "reflects" one point over another
	private Vector2 reflect(Vector2 point_to_reflect, Vector2 reflection_point) {
		Vector2 vec_diff = Raymath.Vector2Subtract(reflection_point, point_to_reflect);
		return Raymath.Vector2Add(reflection_point, vec_diff);
	}
	
	public void draw(Raylib rlj) {
		for (int i = 0; i < points.size(); ++i) {
			// draw points from points arraylist
			//rlj.shapes.DrawCircleV(points.get(i), (float) 10.0, Color.BLACK);
			// draw lines between points
			if (i > 0)
				rlj.shapes.DrawLineV(points.get(i-1) , points.get(i), Color.DARKBLUE);
			// number points
			//rlj.text.DrawText(String.valueOf(i), (int) points.get(i).x - 3, (int) points.get(i).y - 4, 10, Color.WHITE);
		}
		
		/* TODO: delete
		for (int i = 0; i < debug_points.size(); ++i) {
			if (i % 3 == 0) {
				rlj.shapes.DrawCircleV(debug_points.get(i), (float) 10.0, Color.BLACK);
			} else {
				rlj.shapes.DrawCircleV(debug_points.get(i), (float) 15.0, Color.GREEN);
			}
			rlj.text.DrawText(String.valueOf(i), (int) debug_points.get(i).x - 3, (int) debug_points.get(i).y - 4, 10, Color.WHITE);
		}
		*/
	}
}
