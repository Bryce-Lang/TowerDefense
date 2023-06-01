import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
public class Database {
	/**
	 * Connect to Database
	 */
	private Connection connect() {
		Connection con = null;
		String url="jdbc:mysql://localhost:3306/towerdefensedb";
		String username = "root";
		String password = "";

		try {
			
			Class.forName("com.mysql.cj.jdbc.Driver");
			
			//creates connection to the database
			con = DriverManager.getConnection(url, username, password);
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} catch (ClassNotFoundException ex) {
			System.out.println(ex.getMessage());
		}
		return con;
	}
	
	/**
	 * Prints the given table in the database
	 * @param tableName the name of the table in the database
	 */
	public void selectAll(String tableName){ 
		try {
			Connection con = connect();
			Statement stmt = con.createStatement();
			ResultSet resultSet = stmt.executeQuery("SELECT * FROM " + tableName);
			ResultSetMetaData rsmd = resultSet.getMetaData();
			
			//loops through the result set
			while (resultSet.next()) {
				//loops through each column 
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String type = rsmd.getColumnTypeName(i);
					if (type == "INT") {
						System.out.println(rsmd.getColumnName(i) + ": " + resultSet.getInt(i));
					}
					else if (type == "VARCHAR") {
						System.out.println(rsmd.getColumnName(i) + ": " + resultSet.getString(i));
					}
					else if (type == "FLOAT") {
						System.out.println(rsmd.getColumnName(i) + ": " + resultSet.getFloat(i));
					}
					else {
						System.out.println("Error");
					}
				}
				System.out.println();
			}	
		}
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public String getEnemyName(String id) {
		try {
			Connection con = connect();
			Statement stmt = con.createStatement();
			ResultSet resultSet = stmt.executeQuery("SELECT * FROM enemies WHERE id = " + id);
			resultSet.next();
			return resultSet.getString(2);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return "Error";
	}
	
	public int getEnemySpeed(String id) {
		try {
			Connection con = connect();
			Statement stmt = con.createStatement();
			ResultSet resultSet = stmt.executeQuery("SELECT * FROM enemies WHERE id = " + id);
			resultSet.next();
			return resultSet.getInt(3);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return -1;
	}
	public int getEnemyHealth(String id) {
		try {
			Connection con = connect();
			Statement stmt = con.createStatement();
			ResultSet resultSet = stmt.executeQuery("SELECT * FROM enemies WHERE id = " + id);
			resultSet.next();
			return resultSet.getInt(4);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return -1;
	}
	
	public String getTowerName(String id) {
		try {
			Connection con = connect();
			Statement stmt = con.createStatement();
			ResultSet resultSet = stmt.executeQuery("SELECT * FROM towers WHERE id = " + id);
			resultSet.next();
			return resultSet.getString(2);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return "Error";
	}
	
	public int getTowerRange(String id) {
		try {
			Connection con = connect();
			Statement stmt = con.createStatement();
			ResultSet resultSet = stmt.executeQuery("SELECT * FROM towers WHERE id = " + id);
			resultSet.next();
			return resultSet.getInt(3);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return -1;
	}
	
	public float getTowerPosX(String id) {
		try {
			Connection con = connect();
			Statement stmt = con.createStatement();
			ResultSet resultSet = stmt.executeQuery("SELECT * FROM towers WHERE id = " + id);
			resultSet.next();
			return resultSet.getFloat(4);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return -1;
	}
	
	public float getTowerPosY(String id) {
		try {
			Connection con = connect();
			Statement stmt = con.createStatement();
			ResultSet resultSet = stmt.executeQuery("SELECT * FROM towers WHERE id = " + id);
			resultSet.next();
			return resultSet.getFloat(5);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return -1;
	}
	
	public int getTowerDamage(String id) {
		try {
			Connection con = connect();
			Statement stmt = con.createStatement();
			ResultSet resultSet = stmt.executeQuery("SELECT * FROM towers WHERE id = " + id);
			resultSet.next();
			return resultSet.getInt(6);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return -1;
	}
	
	public static void main(String[] args) {
		Database data = new Database();
		//enemy stats
		data.selectAll("enemies");
		System.out.println(data.getEnemyName("1"));
		System.out.println(data.getEnemySpeed("1"));
		System.out.println(data.getEnemyHealth("1") + "\n");
		//tower stats
		data.selectAll("towers");
		System.out.println(data.getTowerName("1"));
		System.out.println(data.getTowerRange("1"));
		System.out.println(data.getTowerPosX("1"));
		System.out.println(data.getTowerPosY("1"));
		System.out.println(data.getTowerDamage("1"));
	}
}
