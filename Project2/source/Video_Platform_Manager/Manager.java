import java.util.*;
import java.sql.*;

public class Manager {
	JDBC jdbc = new JDBC();
	Statement stmt;
	Scanner scanner = new Scanner(System.in);
	
	String managerID, managerPW;
	
	public static void main(String[] args) {
		Manager manager = new Manager();
		manager.startManager();
	}
	
	public void startManager() {
		System.out.println("----- Manager Program -----");
		System.out.println("1. Login");
		System.out.println("2. Create new manager");
		System.out.println("3. Delete manager");
		System.out.println("4. Quit program");
		System.out.print("Please enter a number to select a mode : ");
		
		String mode = scanner.nextLine();
			
		if (mode.equals("1")) {
			loginManager();
		}
		else if (mode.equals("2")) {
			createManager();
		}
		else if (mode.equals("3")) {
			deleteManager();
		}
		else if (mode.equals("4")) {
			System.out.println("----- Quit Program -----");
			System.exit(0);
		}
		else {
			System.out.println("----- Wrong command. Try again -----");
			startManager();
		}
	}
	
	public void loginManager() {
		boolean isLogin = false;
		jdbc = new JDBC();
		stmt = jdbc.getStatement();
		
		System.out.print("Please enter your manager ID : ");
		managerID = scanner.nextLine();
		
		System.out.print("Please enter your manager Password : ");
		managerPW = scanner.nextLine();
		
		try {
			ResultSet rs = stmt.executeQuery("SELECT managerID, managerPW FROM MANAGER");
			
			while(rs.next()) {
				String ID = rs.getString("managerID");
				String PW = rs.getString("managerPW");
				
				if (managerID.equals(ID) && managerPW.equals(PW)) {
					isLogin = true;
					break;
				}
			}
			
			rs.close();
			jdbc.closeConnection();
			
			if (isLogin) {
				while(true) {
					afterLogin();
				}
			}
			else if (!isLogin) {
				System.out.println();
				System.out.println("----- Login Failed -----");
				System.out.println("1. Try again");
				System.out.println("2. Return to Main");
				System.out.println("All numbers except 1, 2. Quit Program");
				System.out.print("Please enter a number : ");
				
				String mode = scanner.nextLine();
				
				if (mode.equals("1")) {
					loginManager();
				}
				else if (mode.equals("2")) {
					startManager();
				}
				else { 
					System.out.println("----- Quit Program -----");
					System.exit(0);
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void afterLogin() {
		System.out.println("\n----- Manager Menu - Manager : " + managerID + " -----");
		System.out.println("1. Show & Manage Users | Videos | Playlists");
		System.out.println("2. Delete Users | Videos | Playlists");
		System.out.println("3. Update manager information");
		System.out.println("4. Quit program");
		System.out.print("Please enter a number to select a mode : ");
		
		String mode = scanner.nextLine();
		
		if (mode.equals("1")) {
			show();
		}
		else if (mode.equals("2")) {
			delete();
		}
		else if (mode.equals("3")) {
			updateManager();
		}
		else if (mode.equals("4")){
			System.out.println("----- Quit Program -----");
			System.exit(0);
		}
		else {
			System.out.println("----- Wrong command. Try again -----");
		}
	}
	
	public void show() {
		while(true) {
			System.out.println("\n----- Show & Manage Users | Videos | Playlists -----");
			System.out.println("1. Show & Manage Users");
			System.out.println("2. Show & Manage Videos");
			System.out.println("3. Show Playlists");
			System.out.println("4. Return to previous menu");
			System.out.print("Please enter a number to select a mode : ");
		
			String mode = scanner.nextLine();
		
			if (mode.equals("1")) {
				showUserList();
			}
			else if (mode.equals("2")) {
				showVideo();
			}
			else if (mode.equals("3")) {
				showPlaylist();
			}
			else if (mode.equals("4")) {
				return;
			}
			else {
				System.out.println("----- Wrong command. Try again -----");
			}
		}
	}
	
	public void showUserList() {
		stmt = jdbc.getStatement();
		
		System.out.println("\n----- Show & Manage User List -----");
		
		try {
			String query = "SELECT userID, age, subscribedNum, userReportNum FROM MANAGER, USER WHERE MANAGER.managerID = USER.managerID AND MANAGER.managerID = '" + managerID + "'";
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				String userID = rs.getString("userID");
				int age = rs.getInt("age");
				int subsrcibedNum = rs.getInt("subscribedNum");
				int userReportNum = rs.getInt("userReportNum");
				
				System.out.println("User ID : " + userID + ", Age : " + age + ", subNum : " + subsrcibedNum + ", Reports : " + userReportNum);
			}
			rs.close();
			jdbc.closeConnection();
			
			manageUser();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void manageUser() {
		while(true) {
			System.out.println("\n## User Management ##");
			System.out.println("1. Change a user's manager");
			System.out.println("2. Show user's upload list");
			System.out.println("3. Show user's view history");
			System.out.println("4. Do nothing");
			System.out.print("Enter a number for the following task : ");
		
			String command = scanner.nextLine();			
			if (command.equals("1")) {
				transferUserManagement();
			}
			else if (command.equals("2")) {
				showUploadList();
			}
			else if (command.equals("3")) {
				showViewHistory();
			}
			else if (command.equals("4")) {
				return;
			}
			else {
				System.out.println("----- Wrong command. Try again -----");
			}
		}
	}
	
	public void transferUserManagement() {
		try {
			stmt = jdbc.getStatement();
		
			System.out.println("----- Transfer User Management -----");
			
			System.out.print("Please enter user's ID : ");
			String userID = scanner.nextLine();
			
			System.out.print("Please enter the ID of the user's new manager : ");
			String newManagerID = scanner.nextLine();
			
			String query = "UPDATE USER SET managerID = '" + newManagerID + "' WHERE userID = '" + userID + "' AND managerID = '" + managerID + "'";
			stmt.executeUpdate(query);
			
			jdbc.closeConnection();
		}
		catch(SQLException e) {
			System.out.println("*** Cannot change a manager ***");
			jdbc.closeConnection();
		}
	}
	
	public void showUploadList() {
		stmt = jdbc.getStatement();
		
		System.out.println("\n----- Show User's Upload List -----");
		System.out.print("Please enter user's ID : ");
		String userID = scanner.nextLine();
		
		try {
			String query = "SELECT videoTitle, videoID, uploadDate, videoLength, views, likes, vReportNum, ageLimit FROM USER, VIDEO "
					+ "WHERE USER.managerID = '" + managerID + "' AND userID = '" + userID + "' AND userID = uploaderID ORDER BY uploadDate DESC";
			ResultSet rs = stmt.executeQuery(query);
			
			System.out.println("## " + userID + "'s Upload List ##");
			while(rs.next()) {
				String videoTitle = rs.getString("videoTitle");
				String videoID = rs.getString("videoID");
				String uploadDate = rs.getString("uploadDate");
				String videoLength = rs.getString("videoLength");
				int views = rs.getInt("views");
				int likes = rs.getInt("likes");
				int vReportNum = rs.getInt("vReportNum");
				int ageLimit = rs.getInt("ageLimit");
				
				System.out.println("Video Title : " + videoTitle + ", Video ID : " + videoID + ", Upload Date : " + uploadDate
						+ ", Video Length : " + videoLength + ", Views : " + views + ", Likes : " + likes + ", Reports : " + vReportNum + ", Age Limit : " + ageLimit);
			}
			System.out.println("----- You cannot delete a video at this screen. If you want to delete the video, go to 'delete' page -----");
			
			rs.close();
			jdbc.closeConnection();
			
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void showViewHistory() {
		stmt = jdbc.getStatement();
		
		System.out.println("\n----- Show & Manage User's View History -----");
		System.out.print("Please enter user's ID : ");
		String userID = scanner.nextLine();
		
		try {
			String query = "SELECT videoTitle, videoID, uploadDate, videoLength, watchStartTime FROM USER, WATCH, VIDEO "
					+ "WHERE USER.managerID = '" + managerID + "' AND USER.userID = '" + userID + "' AND USER.userID = WATCH.userID AND watchVideoID = videoID ORDER BY watchStartTime DESC";
			ResultSet rs = stmt.executeQuery(query);
			
			System.out.println("## " + userID + "'s View History ##");
			while(rs.next()) {
				String videoTitle = rs.getString("videoTitle");
				String videoID = rs.getString("videoID");
				String uploadDate = rs.getString("uploadDate");
				String videoLength = rs.getString("videoLength");
				String watchStartTime = rs.getString("watchStartTime");
				
				System.out.println("Video Title : " + videoTitle + ", Video ID : " + videoID + ", Upload Date : " + uploadDate
						+ ", Video Length : " + videoLength + ", Watch at " + watchStartTime);
			}
			
			System.out.println("----- Done -----");
			
			System.out.println("\n----- Manage View History -----");
			System.out.println("1. Delete user's view history");
			System.out.println("Any keys except 1. Do nothing");
			System.out.print("Enter a number for the following task : ");
			
			String command = scanner.nextLine();
			if (command.equals("1")) {
				deleteViewHistory(userID);
			}
			
			rs.close();
			jdbc.closeConnection();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteViewHistory(String userID) throws SQLException {
		stmt = jdbc.getStatement();
		
		int num = stmt.executeUpdate("DELETE FROM WATCH WHERE userID = '" + userID + "'");
		if (num == 0) {
			System.out.println("## Cannot delete a view history ##");
		}
		else {
			System.out.println("----- Deletion Complete -----");
		}
		
		jdbc.closeConnection();
	}
	
	public void showVideo() {
		stmt = jdbc.getStatement();
		
		System.out.println("\n----- Show Video's Information List -----");
		
		try {
			String query = "SELECT videoTitle, videoID, uploaderID, uploadDate, videoLength, views, likes, vReportNum, ageLimit FROM VIDEO, USER "
					+ "WHERE managerID = '" + managerID + "' AND userID = uploaderID";
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				String videoTitle = rs.getString("videoTitle");
				int videoID = rs.getInt("videoID");
				String uploaderID = rs.getString("uploaderID");
				String uploadDate = rs.getString("uploadDate");
				String videoLength = rs.getString("videoLength");
				int views = rs.getInt("views"); 
				int likes = rs.getInt("likes");
				int vReportNum = rs.getInt("vReportNum");
				int ageLimit = rs.getInt("ageLimit");
				
				System.out.println("Video Title : " + videoTitle + ", Video ID : " + videoID + ", Uploader ID : " + uploaderID + ", Upload Date : " + uploadDate
						+ ", Video Length : " + videoLength + ", Views : " + views + ", Likes : " + likes + ", Reports : " + vReportNum + ", Age Limit : " + ageLimit);
			}
			
			rs.close();
			jdbc.closeConnection();
			
			System.out.println("\n----- Manage Videos -----");
			System.out.println("1. Set age limit");
			System.out.println("Any keys except 1. Do nothing");
			System.out.print("Enter a number for the following task : ");
			
			String command = scanner.nextLine();
			if (command.equals("1")) {
				setAgeLimit();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setAgeLimit() throws SQLException {
		int videoID = 0, limit = 0;
		
		try {
			System.out.print("Please enter video's ID you want to manage : ");
			videoID = scanner.nextInt();
			scanner.nextLine();
		
			System.out.print("Please enter an age limit of this video : ");
			limit = scanner.nextInt();
			scanner.nextLine();
		}
		catch (Exception e) {
			System.out.println("*** Please enter an integer ***");
			scanner.nextLine();
		}
		
		stmt = jdbc.getStatement();
		String query = "UPDATE VIDEO SET ageLimit = " + limit + " WHERE videoID = " + videoID + " AND uploaderID IN (SELECT userID FROM USER WHERE managerID = '" + managerID + "')";
		int num = stmt.executeUpdate(query);
		
		if (num == 0) {
			System.out.println("## No video to change an age limit ##");
		}
		else {
			System.out.println("----- Change " + videoID + "'s age limit to " + limit + " -----");
		}
	}
	
	public void showPlaylist() {
		stmt = jdbc.getStatement();
		
		System.out.println("\n----- Show Playlist's Information List -----");
		
		try {
			String query ="SELECT listTitle, listID, makerID, listReportNum FROM PLAYLIST, USER WHERE managerID = '" + managerID + "' AND makerID = userID";
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				String listTitle = rs.getString("listTitle");
				int listID = rs.getInt("listID");
				String makerID = rs.getString("makerID");
				int listReportNum = rs.getInt("listReportNum");
				
				System.out.println("List Title : " + listTitle + ", List ID : " + listID + ", Maker ID : " + makerID + ", Report : " + listReportNum);
				
				showListVideos(listID);
			}
			
			rs.close();
			jdbc.closeConnection();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void showListVideos(int listID) {
		stmt = jdbc.getStatement();
		
		System.out.println("-----------------------------------------------------");
		
		try {
			String query = "SELECT videoTitle, videoID, uploaderID, uploadDate, videoLength, views, likes, ageLimit FROM VIDEO, CONSIST_OF WHERE cListID = " + listID + " AND cVideoID = videoID";
			ResultSet rs = stmt.executeQuery(query);
		
			while(rs.next()) {
				String videoTitle = rs.getString("videoTitle");
				int videoID = rs.getInt("videoID");
				String uploaderID = rs.getString("uploaderID");
				String uploadDate = rs.getString("uploadDate");
				String videoLength = rs.getString("videoLength");
				int views = rs.getInt("views");
				int likes = rs.getInt("likes");
				int ageLimit = rs.getInt("ageLimit");
				
				System.out.println("Video Title : " + videoTitle + ", Video ID : " + videoID + ", Uploader : " + uploaderID + ", Upload Date : " 
				+ uploadDate + ", Video Length : " + videoLength + ", Views : " + views + ", Likes : " + likes + ", Age Limit : " + ageLimit);
			
			}
			
			System.out.println("-----------------------------------------------------");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void delete() {
		while(true) {
			System.out.println("\n----- Delete Users | Videos | Playlists -----");
			System.out.println("1. Delete Users");
			System.out.println("2. Delete Videos");
			System.out.println("3. Delete Playlists");
			System.out.println("4. Return to previous menu");
			System.out.print("Please enter a number to select a mode : ");
		
			String mode = scanner.nextLine();
		
			if (mode.equals("1")) {
				deleteUser();
			}
			else if (mode.equals("2")) {
				deleteVideo();
			}
			else if (mode.equals("3")) {
				deletePlaylist();
			}
			else if (mode.equals("4")) {
				return;
			}
			else {
				System.out.println("----- Wrong command. Try again -----");
			}
		}
	}	
	
	public void deleteUser() {
		System.out.println("\n----- Type the ID of User you want to delete -----");
		stmt = jdbc.getStatement();
		
		int userCnt = 0;
		
		try {
			String query = "SELECT userID, userReportNum FROM MANAGER, USER WHERE MANAGER.managerID = '" + managerID + "' AND MANAGER.managerID = USER.managerID AND userReportNum >= 100";
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				String userID = rs.getString("userID");
				int userReportNum = rs.getInt("userReportNum");
				
				System.out.println("User ID : " + userID + ", Report : " + userReportNum);
				++userCnt;
			}
			rs.close();
			
			if (userCnt == 0) {
				System.out.println("## No user to delete ##");
			}
			else {
				System.out.print("Enter the ID of the User you want to delete : ");
				String deleteUserID = scanner.nextLine();
				
				String deleteQuery = "DELETE FROM USER WHERE managerID = '" + managerID + "' AND userID = '" + deleteUserID + "'";
				int deleteNum = stmt.executeUpdate(deleteQuery);
				
				if (deleteNum == 0) {
					System.out.println("## Error : Cannot Delete a User ##");
				}
				else {
					System.out.println("## " + deleteUserID + " Deleted ##");
				}
			}
			rs.close();
			jdbc.closeConnection();
		}
		catch (SQLException e) {
			
		}
	}
	
	public void deleteVideo() {
		System.out.println("\n----- Delete Videos -----");
		
		stmt = jdbc.getStatement();
		
		int videoCnt = 0;
		
		try {
			String query = "SELECT videoTitle, videoID, uploaderID, vReportNum FROM VIDEO, USER WHERE managerID = '" + managerID
					+ "' AND userID = uploaderID AND vReportNum >= 100";
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				String videoTitle = rs.getString("videoTitle");
				int videoID = rs.getInt("videoID");
				String uploaderID = rs.getString("uploaderID");
				int vReportNum = rs.getInt("vReportNum");
				
				System.out.println("Video Title : " + videoTitle + ", Video ID : " + videoID + ", Uploader ID : " + uploaderID + ", Report : " + vReportNum);
				++videoCnt;
			}
			
			if (videoCnt == 0) {
				System.out.println("## No video to delete ##");
			}
			else {
				System.out.print("Enter the ID of the Video you want to delete : ");
				int deleteVideoID = 0;
				
				try {
					deleteVideoID = scanner.nextInt();
					scanner.nextLine();
				}
				catch (Exception e) {
					System.out.println("*** Please enter an integer ***");
					scanner.nextLine();
				}
				String deleteQuery = "DELETE FROM VIDEO WHERE videoID = " + deleteVideoID + " AND uploaderID IN (SELECT userID FROM USER WHERE managerID = '" + managerID + "') AND vReportNum >= 100";
				int deleteNum = stmt.executeUpdate(deleteQuery);
				
				if (deleteNum == 0) {
					System.out.println("## Error : Cannot Delete a Video ##");
				}
				else {
					System.out.println("## " + deleteVideoID + " Deleted ##");
				}
			}
			
			rs.close();
			jdbc.closeConnection();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deletePlaylist() {
		System.out.println("\n----- Delete Playlists -----");
		
		stmt = jdbc.getStatement();
		
		int playlistCnt = 0;
		
		try {
			String query = "SELECT listTitle, listID, makerID, listReportNum FROM MANAGER, USER, PLAYLIST WHERE MANAGER.managerID = '" + managerID
					+ "' AND MANAGER.managerID = USER.managerID AND userID = makerID AND listReportNum >= 100";
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				String listTitle = rs.getString("listTitle");
				int listID = rs.getInt("listID");
				String makerID = rs.getString("makerID");
				int listReportNum = rs.getInt("listReportNum");
				
				System.out.println("List Title : " + listTitle + ", List ID : " + listID + ", Maker ID : " + makerID + ", Report : " + listReportNum);
				++playlistCnt;
			}
			
			if (playlistCnt == 0) {
				System.out.println("## No playlist to delete ##");
			}
			else {
				System.out.print("Enter the ID of the Playlist you want to delete : ");
				int deletePlaylistID = 0;
				
				try {
					deletePlaylistID = scanner.nextInt();
					scanner.nextLine();
				}
				catch (Exception e) {
					System.out.println("*** Please enter an integer ***");
					scanner.nextLine();
				}
				
				String deleteQuery = "DELETE FROM PLAYLIST WHERE listID = " + deletePlaylistID + " AND "
						+ "makerID IN (SELECT userID FROM USER WHERE managerID = '" + managerID + "') AND listReportNum >= 100";
				int deleteNum = stmt.executeUpdate(deleteQuery);
				
				if (deleteNum == 0) {
					System.out.println("## Error : Cannot Delete a Playlist ##");
				}
				else {
					System.out.println("## " + deletePlaylistID + " Deleted ##");
				}
			}
			
			rs.close();
			jdbc.closeConnection();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateManager() {
		while(true) {
			System.out.println("\n----- Update Manager Information -----");
			System.out.println("1. Change your PW");
			System.out.println("2. Change your first name");
			System.out.println("3. Change your last name");
			System.out.println("4. Return to previous menu");
			System.out.print("Please enter a number to select a mode : ");
		
			String mode = scanner.nextLine();
		
			if (mode.equals("1")) {
				changePW();
			}
			else if (mode.equals("2")) {
				changeFname();
			}
			else if (mode.equals("3")) {
				changeLname();
			}
			else if (mode.equals("4")) {
				return;
			}
			else {
				System.out.println("----- Wrong command. Try again -----");
			}
		}
	}
	
	public void changePW() {
		try {
			stmt = jdbc.getStatement();
		
			System.out.println("\n----- Change Manager Password -----");
		
			System.out.print("Please enter manager's new PW (Max length = 10) : ");
			managerPW = scanner.nextLine();
			
			if (managerPW.length() > 10) {
				System.out.println("*** Please use another PW ***");
				System.out.print("Please enter manager's new PW (Max length = 10) : ");
				managerPW = scanner.nextLine();
			}
			
			String query = "UPDATE MANAGER SET managerPW = '" + managerPW + "' WHERE managerID = '" + managerID + "'";
			stmt.executeUpdate(query);
			
			jdbc.closeConnection();
			System.out.println("----- Password Changed -----");
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void changeFname() {
		try {
			stmt = jdbc.getStatement();
		
			System.out.println("\n----- Change Manager's First Name -----");
		
			System.out.print("Please enter manager's new first name : ");
			String Fname = scanner.nextLine();
			
			String query = "UPDATE MANAGER SET Fname = '" + Fname + "' WHERE managerID = '" + managerID +"')";
			stmt.executeUpdate(query);
			
			jdbc.closeConnection();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void changeLname() {
		try {
			stmt = jdbc.getStatement();
		
			System.out.println("\n----- Change Manager's Last Name -----");
		
			System.out.print("Please enter manager's new last name : ");
			String Lname = scanner.nextLine();
			
			String query = "UPDATE MANAGER SET Lname = '" + Lname + "' WHERE managerID = '" + managerID +"')";
			stmt.executeUpdate(query);
			
			jdbc.closeConnection();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void createManager() {
		try {
			stmt = jdbc.getStatement();
			
			System.out.println("\n----- Create New Manager -----");
			System.out.print("Please enter new manager's ID (Max length = 10) : ");
			managerID = scanner.nextLine();
			
			if (managerID.length() > 10) {
				System.out.println("*** Please use another ID ***");
				System.out.print("Please enter new manager's ID (Max length = 10) : ");
				managerID = scanner.nextLine();
			}
		
			ResultSet rs = stmt.executeQuery("SELECT managerID FROM MANAGER");
			
			while(rs.next()) {
				String ID = rs.getString("managerID");
				
				if (managerID.equals(ID)) {
					System.out.println("----- Creation Failed. There is a manager with a duplicate ID. -----");
					System.out.println("1. Try again");
					System.out.println("2. Return to Main");
					System.out.println("All numbers except 1,2. Quit Program");
					System.out.print("Please enter a number : ");
					
					String mode = scanner.nextLine();
					
					if (mode.equals("1")) {
						rs.close();
						jdbc.closeConnection();
						
						createManager();
					}
					else if (mode.equals("2")) {
						rs.close();
						jdbc.closeConnection();
						
						startManager();
					}
					else { 
						System.out.println("----- Quit Program -----");
						rs.close();
						jdbc.closeConnection();
						System.exit(0);
					}
				}
			}
			rs.close();
			
			System.out.print("Please enter new manager's PW (Max length = 10) : ");
			managerPW = scanner.nextLine();
			
			if (managerPW.length() > 10) {
				System.out.println("*** Please use another PW ***");
				System.out.print("Please enter new manager's PW (Max length = 10) : ");
				managerPW = scanner.nextLine();
			}
			
			System.out.print("Please enter new manager's first name : ");
			String Fname = scanner.nextLine();
			
			System.out.print("Please enter new manager's last name : ");
			String Lname = scanner.nextLine();
			
			String query = "INSERT INTO MANAGER(managerID, Fname, Lname, managerPW) "
					+ "VALUES ('" + managerID + "', '" + Fname + "', '" + Lname + "', '" + managerPW + "')";
			stmt.executeUpdate(query);
			
			System.out.println("----- Creation Success! Return to Main -----\n");
			
			rs.close();
			jdbc.closeConnection();
			
			startManager();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteManager() {
		try {
			stmt = jdbc.getStatement();
			
			System.out.println("\n----- Delete Manager -----");
			
			String query = "SELECT managerID FROM MANAGER";
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			
			int managerCnt = 1;
			
			while(rs.next()) {
				String managerID = rs.getString("managerID");
				System.out.println(managerCnt + ". " + managerID);
				++managerCnt;
			}
			rs.close();
			
			if (managerCnt == 1) {
				System.out.println("## Error : There is no managers to delete ##");
				jdbc.closeConnection();
				startManager();
			}
			
			System.out.print("Please enter a manager ID you want to delete : ");
			String dManagerID = scanner.nextLine();
			
			stmt.executeUpdate("UPDATE USER SET managerID = 'manager' WHERE managerID = '" + dManagerID + "'");
			
			String deleteQuery = "DELETE FROM MANAGER WHERE managerID = '" + dManagerID + "'";
			int num = stmt.executeUpdate(deleteQuery);
			
			if (num == 0) {
				System.out.println("## Error : Cannot Delete a Manager ##");
			}
			else {
				System.out.println("## " + dManagerID + " Deleted ##");
				System.out.println();
			}
			
			jdbc.closeConnection();
			
			startManager();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
