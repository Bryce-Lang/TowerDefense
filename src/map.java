import java.util.ArrayList;
import java.util.Random;
import com.raylib.java.Raylib;
import com.raylib.java.core.Color;
import com.raylib.java.raymath.Vector2;
import com.raylib.java.raymath.Raymath;

// stores coordinate data for each point in the map
public class Map {
	private int screen_width;
	private int screen_height;
	private int menu_margin;
	
	private int point_num;
	
	// arraylist of coordinates starting with enemy entrance and ending with player base
	public ArrayList<Vector2> points = new ArrayList<>();
	
	// Total length of the map
	public float map_length = 0;
	
	// average distance between points in the map
	private float average_dist = 0;
	
	public Map(int in_screen_width, int in_screen_height, int in_menu_margin) {
		screen_width = in_screen_width;
		screen_height = in_screen_height;
		menu_margin = in_menu_margin;
		point_num = 32;
		generate_map();
	}
	
	public Map(int in_screen_width, int in_screen_height, int in_menu_margin, int in_point_num) {
		screen_width = in_screen_width;
		screen_height = in_screen_height;
		menu_margin = in_menu_margin;
		point_num = in_point_num;
		if (point_num > 32)
			point_num = 32;
		generate_map();
	}
	
	// returns Vector2 location on the map given a float percentage 0.0-1.0
	public Vector2 get_loc(float by) {
		if (by > 1.0f || by < 0.0f) {
			return new Vector2(0, 0);
		}
		int index = (int) ((map_length / average_dist) * by);
		float point_dist = ((map_length * by) % average_dist) / average_dist;
		return Raymath.Vector2Lerp(points.get(index), points.get(index + 1), point_dist);
	}
	
	public void draw(Raylib rlj) {
		for (int i = 0; i < points.size(); ++i) {
			// draw lines between points
			if (i > 0)
				rlj.shapes.DrawLineV(points.get(i-1) , points.get(i), Color.BLUE);
		}
	}
	
	// designed to be called only at instantiation
	private void generate_map() {
		int map_gen_attempts = 0;
		// attempts to create a valid map (no overlap or off screen points) 10000 times, ends when one is made
		do {
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
			
			ArrayList<Vector2> gen_points = generate_points(screen_width / 8);
			
			points = connect_points(entrance, exit, gen_points);
			
			points = smooth(points);
			
		} while (!verify_points() && ++map_gen_attempts < 10000);
		
		points = normalize_points(points);
		
		// TODO: add bridges that slow and speed enemies, add portals that teleport enemies
	}
	
	// checks for overlaps and out of bounds points
	private boolean verify_points() {
		// only every 3 points is checked against every 4 other points; this speeds the algorithm up considerably at risk of occasional overlap
		for (int j = 0; j < points.size() - 4; j += 3) {
			for (int k = 0; k < points.size() - 4; k += 4) {
				// checks if points that are more than 10 indices away in the arraylist are closer than 10 units
				if (Raymath.Vector2Distance(points.get(j), points.get(k)) < 20.0f &&
						Math.abs(j - k) > 10) {
					
					return false;
				}
			}
			// checks that the current point is on screen
			if (points.get(j).x > screen_width ||
					points.get(j).x < menu_margin ||
					points.get(j).y > screen_height ||
					points.get(j).y < 0.0f) {
				
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
				vec.x = (menu_margin + (rand.nextFloat() * (screen_width - menu_margin + 1.0f)));
				break;
			case 1:
				vec.y = screen_height;
				vec.x = (menu_margin + (rand.nextFloat() * (screen_width - menu_margin + 1.0f)));
				break;
			case 2:
				vec.x = menu_margin;
				vec.y = (rand.nextFloat() * screen_height);
				break;
			case 3:
				vec.x = screen_width;
				vec.y = (rand.nextFloat() * screen_height);
				break;
		}
		
		return vec;
	}
	
	// Generates points within the map area buffered by margin_size
	private ArrayList<Vector2> generate_points(int margin_size) {
		
		Random rand = new Random();

		ArrayList<Vector2> new_points = new ArrayList<>();
		
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
				new_points.add(p);
				continue;
			}
			
			// loop through points already in map, verifying that they are reasonably far away
			// adds to new_points if they are, otherwise tries again
			for (int j = new_points.size() - 1; j >= 0; --j) {
				// reject point if too close to another
				if (Raymath.Vector2Distance(p, new_points.get(j)) < (screen_height * 2.0 / ((point_num / 2.0) + 2))) {
					--i;
					break;
				} else if (j == 0) { // every point has been checked, add p to new_points
					new_points.add(p);
				}
			}
			
			// breaks out of loop if average point attempts exceeds 100; prevents infinite loop if parameters set improperly
			if (point_attempt_counter > (100 * point_num)) {
				break;
			}
		}
		
		return new_points;
	}
	
	// connects previously generated points into a path
	private ArrayList<Vector2> connect_points(Vector2 entrance_lead, Vector2 exit_lead, ArrayList<Vector2> generated_points) {
		// starting from the entrance and exit, add closest points until no more points remain then connect last two points
		// points are "connected" implicitly; in practice the are just next to each other in the points arraylist
		
		// connected_points of same size as generated_points, initialized to 0, 0
		ArrayList<Vector2> connected_points = new ArrayList<>();
		for (int i = 0; i < (generated_points.size() + 2); ++i) {
			connected_points.add(new Vector2(0, 0));
		}
		
		connected_points.set(0, entrance_lead);
		connected_points.set(connected_points.size() - 1, exit_lead);
		
		// point index used to add new points at correct index in points arraylist; starts at 1 because index 0 is for entrance
		int point_index = 1;
		while(generated_points.size() > 0) {
			// index of the current closest point in generated_points
			int cp_index = 0;
			
			// entrance_lead starts at entrance and "walks" to closest point until no more points are left ---------------------------
			
			// find closest point in generated_points to entrance lead
			for (int i = 0; i < generated_points.size(); ++i) {
				if (Raymath.Vector2Distance(entrance_lead, generated_points.get(i)) < Raymath.Vector2Distance(entrance_lead, generated_points.get(cp_index))) {
					cp_index = i;
				}
			}
			// closest point to entrance_lead added to points arraylist
			connected_points.set(point_index, generated_points.get(cp_index));
			
			// entrance lead moved to closest point remaining in generated_points
			entrance_lead = generated_points.get(cp_index);
			
			// closest point removed from generated_points array so it won't be selected again
			generated_points.remove(cp_index);
			
			// -----------------------------------------------------------------------------------------------------------------------
			
			// verify that there are more points in generated_points to be added, if not break out of loop
			if (generated_points.size() == 0)
				break;
			
			// point index is incremented here so that it can be subtracted from points.size() to get correct index
			++point_index;
			
			// reset cp_index location for exit lead; only important because cp_index could now be out of bounds
			cp_index = 0;
			
			// repeat above for exit lead --------------------------------------------------------------------------------------------
			for (int i = 0; i < generated_points.size(); ++i) {
				if (Raymath.Vector2Distance(exit_lead, generated_points.get(i)) < Raymath.Vector2Distance(exit_lead, generated_points.get(cp_index))) {
					cp_index = i;
				}
			}
			
			Vector2 vec = generated_points.get(cp_index);
			
			connected_points.set(connected_points.size() - point_index, vec);
			
			exit_lead = generated_points.get(cp_index);
			
			generated_points.remove(cp_index);
			
			// -----------------------------------------------------------------------------------------------------------------------
		}
		
		return connected_points;
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
		ArrayList<Vector2> smoothed_points = new ArrayList<>();
		
		// loop over all points, generating smooth transitions between them
		for (int i = 1; i < (in_points.size() - 2); ++i) {
			
			smoothed_points.add(in_points.get(i));
			
			Vector2 close_control = est_close_control(in_points, i);
			
			Vector2 far_control = est_far_control(in_points, i);
			
			// sometimes control points overlap, resulting in pointy and loopy artifacts
			// here we reduce the distance of the control points until they no longer overlap
			float cc_lap_fac; // distance from current point to close_control
			float fc_lap_fac; // distance from current point to far_control
			for (int j = 0; j < 5; ++j){
				cc_lap_fac = Raymath.Vector2Distance(in_points.get(i), Raymath.Vector2Add(close_control, in_points.get(i)));
				fc_lap_fac = Raymath.Vector2Distance(in_points.get(i), Raymath.Vector2Add(far_control, in_points.get(i + 1)));
				if (!(cc_lap_fac > fc_lap_fac))
					break;
				close_control = Raymath.Vector2Divide(close_control, new Vector2(1.25f, 1.25f));
				far_control = Raymath.Vector2Divide(far_control, new Vector2(1.25f, 1.25f));
			}
			
			// other times control points are near 90 degrees to one another and too close, causing a "pinch"
			// here we detect and remove those by reducing control point distance
			float cc_pinch_fac; // distance from close_control to next point
			float fc_pinch_fac; // distance from far_control to current point
			for (int j = 0; j < 5; ++j){
				cc_pinch_fac = Raymath.Vector2Distance(in_points.get(i + 1), Raymath.Vector2Add(close_control, in_points.get(i)));
				fc_pinch_fac = Raymath.Vector2Distance(in_points.get(i + 1), Raymath.Vector2Add(far_control, in_points.get(i + 1)));
				if (!(cc_pinch_fac < fc_pinch_fac))
					break;
				close_control = Raymath.Vector2Divide(close_control, new Vector2(1.25f, 1.25f));
				far_control = Raymath.Vector2Divide(far_control, new Vector2(1.25f, 1.25f));
			}
			
			// then we put them in place by adding them to their respective points
			close_control = Raymath.Vector2Add(close_control, in_points.get(i));
			far_control = Raymath.Vector2Add(far_control, in_points.get(i + 1));
			
			// distance from current point to next point
			int p_distance = (int) Raymath.Vector2Distance(in_points.get(i), in_points.get(i + 1));
			
			// iterates between current point and the next, adding roughly evenly spread points between them
			// completely arbitrary calculation, generates points pretty well though
			int segs = (int) ((p_distance + point_num) / (Math.sqrt(point_num + 32))) + 6;
			for (int j = 1; j < segs; ++j) {
				smoothed_points.add(smooth_lerp(in_points, i, j * (1.0f / segs), close_control, far_control));
			}
		}
		// --------------------------------------------------------------------------------------------------------------------------
		
		// exit point needs to be added manually
		smoothed_points.add(in_points.get(in_points.size() - 2));
		
		return smoothed_points;
	}
	
	// estimates control points using a custom algorithm based on the Catmull-Rom velocity estimation
	private Vector2 est_close_control(ArrayList<Vector2> in_points, int ind) {
		// use Catmull-Rom velocity estimation too estimate control point vector from current point
		Vector2 close_control = Raymath.Vector2Subtract(in_points.get(ind + 1), in_points.get(ind - 1));
		
		// if the angle between the neighboring points is too narrow, "kinks" often form
		// we fix this by setting our control point vector to be perpendicular to a vector between mid and current point
		
		// offset is the percentage of current point between it's neighbors based on its distance to each
		float prev_dist = Raymath.Vector2Distance(in_points.get(ind), in_points.get(ind - 1));
		float next_dist = Raymath.Vector2Distance(in_points.get(ind), in_points.get(ind + 1));
		float offset = prev_dist / (prev_dist + next_dist);
		
		// mid is a point on the line between the previous point and next point roughly close to our current point
		Vector2 mid = Raymath.Vector2Lerp(in_points.get(ind - 1), in_points.get(ind + 1), offset);
		
		// norm starts as the vector between current point and mid
		Vector2 norm = Raymath.Vector2Subtract(in_points.get(ind), mid);
		float len = length(norm);
		
		// if current point lies on the line between it's neighbors, len = 0 and norm x = 0.0 and y = 0.0
		// we fix this by resetting norm to point toward the next point
		if (len == 0.0f) {
			norm = Raymath.Vector2Subtract(points.get(ind), points.get(ind + 1));
			len = length(norm);
		}
		
		// normalize norm and rotate it 90 degrees; it can now be used to rotate control
		norm = new Vector2(-norm.y / len, norm.x / len);
		
		// norm isn't necessarily on the same side of the point as curr_control
		// we want it to be on the same side, so we flip it if control_len > the length of curr_control + norm
		float control_len = length(close_control);
		Vector2 diff = Raymath.Vector2Add(close_control, norm);
		float diff_len = length(diff);
		if (control_len > diff_len) {
			norm = reflect(norm, new Vector2(0, 0));
		}
		
		close_control = new Vector2(norm.x * control_len, norm.y * control_len);
		
		// the full length of curr_control is too long, so we shorten it
		close_control = Raymath.Vector2Divide(close_control, new Vector2(4, 4));
		
		return close_control;
	}
	
	// estimates the control point near the next point in points array; works similarly to est_close_control, reference for detailed info
	private Vector2 est_far_control(ArrayList<Vector2> in_points, int ind) {
		Vector2 far_control = Raymath.Vector2Subtract(in_points.get(ind + 2), in_points.get(ind));
		
		// next, we shift the point to be perpendicular to the vector from the current point to the average of the two surrounding points
		float prev_dist = Raymath.Vector2Distance(in_points.get(ind + 1), in_points.get(ind));
		float next_dist = Raymath.Vector2Distance(in_points.get(ind + 1), in_points.get(ind + 2));
		float offset = prev_dist / (prev_dist + next_dist);
		Vector2 mid = Raymath.Vector2Lerp(in_points.get(ind), in_points.get(ind + 2), offset);
		Vector2 norm = Raymath.Vector2Subtract(in_points.get(ind + 1), mid);
		
		float len = length(norm);
		
		// if the point lies on the line between it's neighboring points, len = 0 and norm x = 0.0 and y = 0.0
		// we can fix this by detecting it, and reseting norm to point toward the last point
		if (len == 0.0f) {
			norm = Raymath.Vector2Subtract(points.get(ind + 1), points.get(ind + 2));
			len = length(norm);
		}
		norm = new Vector2(-norm.y / len, norm.x / len);
		
		float control_len = length(far_control);
		
		// norm isn't necessarily on the same side of the point as far_control
		// we want it to be on the opposite side, so we flip it if control_len < the length of far_control + norm
		Vector2 diff = Raymath.Vector2Add(far_control, norm);
		float diff_len = length(diff); 
		if (control_len < diff_len) {
			norm = reflect(norm, new Vector2(0, 0));
		}
		
		// set far_control to equal it's length times norm
		far_control = new Vector2(norm.x * control_len, norm.y * control_len);
		
		// full length of far_control is too long, so we shorten it
		far_control = Raymath.Vector2Divide(far_control, new Vector2(4, 4));
		
		return far_control;
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
	
	// returns new map ArrayList with points roughly equidistant to each other
	private ArrayList<Vector2> normalize_points(ArrayList<Vector2> in_points) {
		ArrayList<Vector2> norm_points = new ArrayList<>();
		// stores distance from each point to the next
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
	
	// "reflects" one point over another
	private Vector2 reflect(Vector2 point_to_reflect, Vector2 reflection_point) {
		Vector2 vec_diff = Raymath.Vector2Subtract(reflection_point, point_to_reflect);
		return Raymath.Vector2Add(reflection_point, vec_diff);
	}
	
	// Raymath.Vector2Length assumes a positive vector, resulting in NaN values if negative. this length function is better.
	private float length(Vector2 vec) {
		return (float) Math.sqrt(Math.abs((vec.x * vec.x) + (vec.y * vec.y)));
	}
}
