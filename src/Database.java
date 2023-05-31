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
					else {
						System.out.println("Error");
					}
				}
				System.out.println("\n");
			}	
		}
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		Database data = new Database();
		data.selectAll("enemies");
	}
}

