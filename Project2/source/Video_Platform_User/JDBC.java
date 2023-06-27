import java.sql.*;

public class JDBC {
	Statement stmt;
	Connection con;
	
	public Statement getStatement() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/VIDEO_PLATFORM", "root", "Justin0409");
			stmt = con.createStatement();
			
			return stmt;
		}
		catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void closeConnection() {
		try {
			stmt.close();
			con.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
