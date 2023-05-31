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
	
	int point_num;
	
	// arraylist of coordinates starting with enemy entrance and ending with player base
	ArrayList<Vector2> points = new ArrayList<>();
	
	// Total length of the map
	float map_length = 0;
	
	// average distance between points in the map
	float average_dist = 0;
	
	// TODO: delete; only used for debugging
	ArrayList<Vector2> debug_points = new ArrayList<>();
	
	public map(int in_screen_width, int in_screen_height, int in_menu_margin, int in_point_num) {
		screen_width = in_screen_width;
		screen_height = in_screen_height;
		menu_margin = in_menu_margin;
		point_num = in_point_num;
		generate_map();
	}
	
	private void generate_map() {
		int map_gen_attempts = 0;
		// attempts to create a map without overlap 10000 times, ends when one is made
		do {
			
			debug_points = new ArrayList<>();
			
			// points array initialized with given point number + 2 for entrance and exit
			points = new ArrayList<>();
			for (int j = 0; j < (point_num + 2); ++j) {
				Vector2 p = new Vector2(0, 0);
				points.add(p);
			}
			
			Vector2 entrance = generate_edge_point();
			Vector2 exit = generate_edge_point();
			// add entrance to points array at index 0
			points.set(0, entrance);
			// add exit to points array at end
			points.set(points.size() - 1, exit);
			
			ArrayList<Vector2> tmp_points_arr = generate_points(screen_width / 9);
			
			connect_points(entrance, exit, tmp_points_arr);
			
			points = smooth(points);
			
			++map_gen_attempts;
		} while (!verify_points() && map_gen_attempts < 10000);
		
		points = normalize_points(points);
	}
	
	// checks for overlaps and out of bounds points
	private boolean verify_points() {
		for (int j = 0; j < points.size(); ++j) {
			for (int k = 0; k < points.size(); ++k) {
				// checks if points that are more than 10 indices away in the arraylist are closer than 10 units
				if (Raymath.Vector2Distance(points.get(j), points.get(k)) < 10.0 &&
						Math.abs(j - k) > 10) {
					
					return false;
				}
			}
			// checks that the current point is on screen
			if (points.get(j).x > screen_width ||
					points.get(j).x < menu_margin ||
					points.get(j).y > screen_height ||
					points.get(j).y < 0.0) {
				
				return false;
			}
		}
		return true;
	}
	
	// generates a random point around the perimeter of the map area
	private Vector2 generate_edge_point() {
		Random rand = new Random();
		
		Vector2 vec = new Vector2();
		
		int edge = rand.nextInt(4);
		
		switch (edge) {
			case 0:
				vec.y = 0;
				vec.x = (float) (menu_margin + (rand.nextFloat() * (screen_width - menu_margin + 1.0)));
				break;
			case 1:
				vec.y = screen_height;
				vec.x = (float) (menu_margin + (rand.nextFloat() * (screen_width - menu_margin + 1.0)));
				break;
			case 2:
				vec.x = menu_margin;
				vec.y = (float) (rand.nextFloat() * screen_height);
				break;
			case 3:
				vec.x = screen_width;
				vec.y = (float) (rand.nextFloat() * screen_height);
				break;
		}
		
		return vec;
	}
	
	// Generates points within the map area buffered by margin_size
	private ArrayList<Vector2> generate_points(int margin_size) {
		
		Random rand = new Random();

		ArrayList<Vector2> tmp = new ArrayList<>();
		
		// point_attempt_counter is used to break out of loop if parameters are set incorrectly; stops an infinite loop forming
		int point_attempt_counter = 0;
		
		for (int i = 0; i < point_num; ++i) {
			++point_attempt_counter;
			// declare and generate a new point in the map
			Vector2 p = new Vector2();
			p.x = (rand.nextFloat() * (screen_width - menu_margin - margin_size + 1)) + (menu_margin + (margin_size / 2));
			p.y = (rand.nextFloat() * (screen_height - margin_size + 1)) + (margin_size / 2);
			
			// first point needs to be added manually
			if (i == 0) {
				tmp.add(p);
				continue;
			}
			
			// loop through points already in map, verifying that they are reasonably far away
			// adds to tmp if they are, otherwise tries again
			for (int j = tmp.size() - 1; j >= 0; --j) {
				// reject point if too close to another
				if (Raymath.Vector2Distance(p, tmp.get(j)) < (screen_height * 3.0 / point_num)) {
					--i;
					break;
				} else if (j == 0) { // every point has been checked, add p to tmp
					tmp.add(p);
				}
			}
			
			if (point_attempt_counter > (1000 * point_num)) {
				System.out.println("points not fittable :(");
				break;
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
	
	// calculates smoothed points using a mishmash of (tweaked) Catmull-Rom velocity estimation with quadratic bezier curve calculation
	private ArrayList<Vector2> smooth(ArrayList<Vector2> in_points) {
		
		// first step: add points beyond the entrance and exit as splines don't go to end points -------------------------------------
		Vector2 entrance_ghost = reflect(in_points.get(1), in_points.get(0));
		// we want entrance_ghost to be a reflection of in_points[1] over the border that entrance is on, so we set it's x or y
		// based on whether entrance is on a vertical or horizontal border
		if (in_points.get(0).y == 0.0 || in_points.get(0).y == screen_height) {
			entrance_ghost.x = in_points.get(1).x;
		} else {
			entrance_ghost.y = in_points.get(1).y;
		}
		in_points.add(0, entrance_ghost);
		
		Vector2 exit_ghost = reflect(in_points.get(in_points.size() - 2), in_points.get(in_points.size() - 1));
		// we do the same for exit_ghost that we did for entrance_ghost
		if (in_points.get(in_points.size() - 1).y == 0.0 || in_points.get(in_points.size() - 1).y == screen_height) {
			exit_ghost.x = in_points.get(in_points.size() - 2).x;
		} else {
			exit_ghost.y = in_points.get(in_points.size() - 2).y;
		}
		in_points.add(exit_ghost);
		
		// ---------------------------------------------------------------------------------------------------------------------------
		
		// second step: loop through in_points ArrayList, adding appropriate number of points based on distance between points -------
		// a new arraylist is made to simplify indexing; we copy points in as we go. this will be returned when finished
		ArrayList<Vector2> tmp = new ArrayList<>();
		
		// loop over all points, generating smooth transitions between them
		for (int i = 1; i < (in_points.size() - 2); ++i) {
			
			tmp.add(in_points.get(i));
			
			// estimate control point curr_control------------------------------------------------------------------------------------
			Vector2 curr_control = Raymath.Vector2Subtract(in_points.get(i + 1), in_points.get(i - 1));
			
			// next, we shift the point to be perpendicular to the vector from the current point to the average of the two surrounding points
			Float prev_dist = Raymath.Vector2Distance(in_points.get(i), in_points.get(i - 1));
			Float next_dist = Raymath.Vector2Distance(in_points.get(i), in_points.get(i + 1));
			Float offset = prev_dist / (prev_dist + next_dist);
			Vector2 mid = Raymath.Vector2Lerp(in_points.get(i - 1), in_points.get(i + 1), offset);
			Vector2 norm = Raymath.Vector2Subtract(in_points.get(i), mid);
			float len = length(norm);
			// if this point lies on the line between it's neighbors, len = 0 and norm x = 0.0 and y = 0.0
			// we fix this by resetting norm to point to the next point
			if (len == 0.0) {
				norm = Raymath.Vector2Subtract(points.get(i), points.get(i + 1));
				len = length(norm);
			}
			// normalize the vector and rotate it 90 degrees
			norm = new Vector2(-norm.y / len, norm.x / len);
			float control_len = length(curr_control);
			
			// norm isn't necessarily on the same side of the point as curr_control
			// we want it to be on the same side, so we flip it if control_len > the length of curr_control + norm
			Vector2 diff = Raymath.Vector2Add(curr_control, norm);
			float diff_len = length(diff);
			if (control_len > diff_len) {
				norm = reflect(norm, new Vector2(0, 0));
			}
			
			// set curr_control to equal it's length times norm
			curr_control = new Vector2(norm.x * control_len, norm.y * control_len);
			
			curr_control = Raymath.Vector2Divide(curr_control, new Vector2(4, 4));
			
			// -----------------------------------------------------------------------------------------------------------------------
			
			// Estimate control point near the next point ----------------------------------------------------------------------------
			// this is done in similar fashion to curr_control
			Vector2 next_control = Raymath.Vector2Subtract(in_points.get(i + 2), in_points.get(i));
			
			// next, we shift the point to be perpendicular to the vector from the current point to the average of the two surrounding points
			prev_dist = Raymath.Vector2Distance(in_points.get(i + 1), in_points.get(i));
			next_dist = Raymath.Vector2Distance(in_points.get(i + 1), in_points.get(i + 2));
			offset = prev_dist / (prev_dist + next_dist);
			mid = Raymath.Vector2Lerp(in_points.get(i), in_points.get(i + 2), offset);
			norm = Raymath.Vector2Subtract(in_points.get(i + 1), mid);
			
			// normalize the vector and rotate it 90 degrees
			len = length(norm);
			// if the point lies on the line between it's neighboring points, len = 0 and norm x = 0.0 and y = 0.0
			// we can fix this by detecting it, and reseting norm to point toward the last point
			if (len == 0.0) {
				norm = Raymath.Vector2Subtract(points.get(i + 1), points.get(i));
				len = length(norm);
				System.out.println("mid x: " + mid.x + " y: " + mid.y);
				System.out.println("point x: " + in_points.get(i + 1).x + " y: " + in_points.get(i + 1).y);
				System.out.println("norm x: " + norm.x + " y: " + norm.y);
			}
			norm = new Vector2(-norm.y / len, norm.x / len);
			
			control_len = length(next_control);
			
			// norm isn't necessarily on the same side of the point as next_control
			// we want it to be on the opposite side, so we flip it if control_len < the length of next_control + norm
			diff = Raymath.Vector2Add(next_control, norm);
			diff_len = length(diff); 
			if (control_len < diff_len) {
				norm = reflect(norm, new Vector2(0, 0));
			}
			
			// set next_control to equal it's length times norm
			next_control = new Vector2(norm.x * control_len, norm.y * control_len);
			
			next_control = Raymath.Vector2Divide(next_control, new Vector2(4, 4));
			// -----------------------------------------------------------------------------------------------------------------------
			
			// sometimes control points overlap, resulting in pointy and loopy artifacts
			// here we reduce the distance of the control points until they no longer overlap
			boolean over;
			for (int j = 0; j < 6; ++j){
				over = Raymath.Vector2Distance(in_points.get(i), Raymath.Vector2Add(curr_control, in_points.get(i))) > Raymath.Vector2Distance(in_points.get(i), Raymath.Vector2Add(next_control, in_points.get(i + 1)));
				if (!over)
					break;
				curr_control = Raymath.Vector2Divide(curr_control, new Vector2((float) 1.5, (float) 1.5));
				next_control = Raymath.Vector2Divide(next_control, new Vector2((float) 1.5, (float) 1.5));
			}
			
			// other times control points are near 90 degrees to one another and too close, causing a "pinch"
			// here we detect and remove those by reducing control point distance
			boolean pinch;
			for (int j = 0; j < 6; ++j){
				pinch = Raymath.Vector2Distance(in_points.get(i + 1), Raymath.Vector2Add(curr_control, in_points.get(i))) < Raymath.Vector2Distance(in_points.get(i + 1), Raymath.Vector2Add(next_control, in_points.get(i + 1)));
				if (!pinch)
					break;
				curr_control = Raymath.Vector2Divide(curr_control, new Vector2((float) 1.5, (float) 1.5));
				next_control = Raymath.Vector2Divide(next_control, new Vector2((float) 1.5, (float) 1.5));
			}
			
			// then we put them in place by adding them to their respective points
			curr_control = Raymath.Vector2Add(curr_control, in_points.get(i));
			next_control = Raymath.Vector2Add(next_control, in_points.get(i + 1));
			
			debug_points.add(curr_control); // TODO: delete
			debug_points.add(next_control); // TODO: delete
			
			// distance from current point to next point
			int p_distance = (int) Raymath.Vector2Distance(in_points.get(i), in_points.get(i+1));
			
			// iterates between current point and the next, adding roughly evenly spread points between them
			int segs = (int) (p_distance / (Math.sqrt(point_num) + 2)) + 6;
			for (int j = 1; j < segs; ++j) {
				tmp.add(smooth_lerp(in_points, i, j * ((float) 1.0 / segs), curr_control, next_control));
			}
		}
		// --------------------------------------------------------------------------------------------------------------------------
		
		// exit point needs to be added manually
		tmp.add(in_points.get(in_points.size() - 2));
		
		return tmp;
	}
	
	// quadratic bezier lerping given current point index and estimated control points
	private Vector2 smooth_lerp(ArrayList<Vector2> unsmooth_points, int p_ind, float by, Vector2 curr_control, Vector2 next_control) {
		// Polynomial coefficients used in place of lerps to speed up computation
		// instead of using Raymath.etc for everything it's easier just to split them into x and y and do it normally
		float t_3_x = (by * by * by) * ( -(unsmooth_points.get(p_ind).x) + (3 * curr_control.x) - (3 * next_control.x) + (unsmooth_points.get(p_ind + 1).x));
		float t_2_x = (by * by) * ((3 * unsmooth_points.get(p_ind).x - (6 * curr_control.x) + (3 * next_control.x)));
		float t_x = by * (-(3 * unsmooth_points.get(p_ind).x) + (3 * curr_control.x));
		
		float t_3_y = (by * by * by) * ( -(unsmooth_points.get(p_ind).y) + (3 * curr_control.y) - (3 * next_control.y) + (unsmooth_points.get(p_ind + 1).y));
		float t_2_y = (by * by) * ((3 * unsmooth_points.get(p_ind).y - (6 * curr_control.y) + (3 * next_control.y)));
		float t_y = by * (-(3 * unsmooth_points.get(p_ind).y) + (3 * curr_control.y));
		
		return new Vector2((unsmooth_points.get(p_ind).x + t_x + t_2_x + t_3_x), (unsmooth_points.get(p_ind).y + t_y + t_2_y + t_3_y));
	}
	
	// "reflects" one point over another
	private Vector2 reflect(Vector2 point_to_reflect, Vector2 reflection_point) {
		Vector2 vec_diff = Raymath.Vector2Subtract(reflection_point, point_to_reflect);
		return Raymath.Vector2Add(reflection_point, vec_diff);
	}
	
	// I don't yet know why but Raymath.Vector2Length breaks things hence this
	private float length(Vector2 vec) {
		return (float) Math.sqrt(Math.abs(vec.x * vec.x + vec.y * vec.y));
	}
	
	// returns arraylist of distances between the points in the map, also sets map_length
	private ArrayList<Vector2> normalize_points(ArrayList<Vector2> in_points) {
		ArrayList<Vector2> norm_points = new ArrayList<>();
		// distance from each point to the next
		ArrayList<Float> point_dists = new ArrayList<>();
		for (int i = 0; i < (points.size() - 1); ++i) {
			float point_dist = Raymath.Vector2Distance(in_points.get(i), in_points.get(i + 1));
			point_dists.add(point_dist);
			map_length += point_dist;
		}
		average_dist = map_length / in_points.size();
		for (int i = 0; i < in_points.size(); ++i) {
			float point_dist = average_dist * i;
			int j = 0;
			while (point_dist > point_dists.get(j)) {
				point_dist -= point_dists.get(j);
				++j;
			}
			norm_points.add(Raymath.Vector2Lerp(in_points.get(j), in_points.get(j + 1), point_dist / point_dists.get(j)));
		}
		norm_points.add(in_points.get(in_points.size() - 1));
		
		return norm_points;
	}
	
	// returns Vector2 location on the map given a float percentage 0.0-1.0
	public Vector2 get_loc(float by) {
		if (by > 1.0 || by < 0.0) {
			return new Vector2(0, 0);
		}
		int index = (int) ((map_length / average_dist) * by);
		float point_dist = ((map_length * by) % average_dist) / average_dist;
		return Raymath.Vector2Lerp(points.get(index), points.get(index + 1), point_dist);
	}
	
	public void draw(Raylib rlj) {
		for (int i = 0; i < points.size(); ++i) {
			// draw points from points arraylist
			//rlj.shapes.DrawCircleV(points.get(i), (float) 2.0, Color.BLACK);
			// draw lines between points
			if (i > 0)
				rlj.shapes.DrawLineV(points.get(i-1) , points.get(i), Color.BLUE);
			// number points
			//rlj.text.DrawText(String.valueOf(i), (int) points.get(i).x - 3, (int) points.get(i).y - 4, 10, Color.WHITE);
		}
		
		/* TODO: delete
		for (int i = 0; i < debug_points.size(); ++i) {
			if (i % 3 == 0) {
				rlj.shapes.DrawCircleV(debug_points.get(i), (float) 2.0, Color.BLACK);
			} else {
				rlj.shapes.DrawCircleV(debug_points.get(i), (float) 3.0, Color.GREEN);
			}
			
			rlj.text.DrawText(String.valueOf(i), (int) debug_points.get(i).x - 3, (int) debug_points.get(i).y - 4, 10, Color.BLACK);
		}
		*/
	}
}
