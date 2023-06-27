import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class User {
	JDBC jdbc = new JDBC();
	Statement stmt;
	Scanner scanner = new Scanner(System.in);
	
	String userID, userPW;
	int age;
	
	public static void main(String args[]) {
		User user = new User();
		user.startUser();
	}
	
	public void startUser() {
		System.out.println("----- User Program -----");
		System.out.println("1. Login");
		System.out.println("2. Sign Up");
		System.out.println("3. Quit program");
		System.out.print("Please enter a number to select a mode : ");
		
		String mode = scanner.nextLine();
		
		if (mode.equals("1")) {
			loginUser();
		}
		else if (mode.equals("2")) {
			createUser();
		}
		else if (mode.equals("3")) {
			System.out.println("----- Quit Program -----");
			System.exit(0);
		}
		else {
			System.out.println("----- Wrong command. Try again -----");
			startUser();
		}
	}
	
	public void loginUser() {
		boolean isLogin = false;
		stmt = jdbc.getStatement();
		
		System.out.print("Please enter your ID : ");
		userID = scanner.nextLine();
		
		System.out.print("Please enter your Password : ");
		userPW = scanner.nextLine();
		
		try {
			ResultSet rs = stmt.executeQuery("SELECT userID, userPW, age FROM USER");
			
			while(rs.next()) {
				String ID = rs.getString("userID");
				String PW = rs.getString("userPW");
				int getAge = rs.getInt("age");
				
				if (userID.equals(ID) && userPW.equals(PW)) {
					age = getAge;
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
					loginUser();
				}
				else if (mode.equals("2")) {
					startUser();
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
		System.out.println("\n----- User Menu - User : " + userID + " -----");
		System.out.println("1. Show & Watch Videos | Playlists");
		System.out.println("2. Manage Your Channel");
		System.out.println("3. Update User Information");
		System.out.println("4. Quit program");
		System.out.print("Please enter a number to select a mode : ");
		
		String mode = scanner.nextLine();
		
		if (mode.equals("1")) {
			show();
		}
		else if (mode.equals("2")) {
			manageChannel();
		}
		else if (mode.equals("3")) {
			updateUser();
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
			System.out.println("\n----- Show & Watch Videos | Playlists -----");;
			System.out.println("1. Show & Watch Videos");
			System.out.println("2. Show & Watch Playlists");
			System.out.println("3. Return to previous menu");
			System.out.print("Please enter a number to select a mode : ");
		
			String mode = scanner.nextLine();
			
			if (mode.equals("1")) {
				showVideo();
			}
			else if (mode.equals("2")) {
				showPlaylist();
			}
			else if (mode.equals("3")) {
				return;
			}
			else {
				System.out.println("----- Wrong command. Try again -----");
			}
		}
	}
	
	public void showVideo() {
		stmt = jdbc.getStatement();
		
		System.out.println("\n----- Show Videos -----");
		
		try {
			String query = "SELECT videoTitle, uploaderID, videoLength, views, ageLimit FROM VIDEO, USER WHERE ageLimit <= age AND userID = '" + userID + "'";
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				String videoTitle = rs.getString("videoTitle");
				String uploaderID = rs.getString("uploaderID");
				String videoLength = rs.getString("videoLength");
				int views = rs.getInt("views"); 
				int ageLimit = rs.getInt("ageLimit");
				
				System.out.println("Video Title : " + videoTitle + ", Uploader ID : " + uploaderID + ", Video Length : " + videoLength
						+ ", Views : " + views + ", Age Limit : " + ageLimit);
			}
			
			rs.close();
			jdbc.closeConnection();
			
			System.out.println("\n*** If you want to watch a video, type '1'. If not, type something else. ***");
			System.out.print("Please enter a key to select a mode : ");
			
			String watch = scanner.nextLine();
			if (watch.equals("1")) {
				watchVideo();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void watchVideo() {
		stmt = jdbc.getStatement();
		
		System.out.println("\n## Wacth a Video ##");
		System.out.print("Please enter a name of video you want to watch : ");
		String videoTitle = scanner.nextLine();
		
		System.out.print("Please enter a uploader ID of video you want to watch : ");
		String uploaderID = scanner.nextLine();
		
		try {
			String query = "SELECT ageLimit, videoID, videoTitle, uploaderID, subscribedNum, uploadDate, videoLength, views, likes FROM VIDEO, USER "
					+ "WHERE videoTitle = '" + videoTitle + "' AND uploaderID = '" + uploaderID +"' AND uploaderID = userID";
			ResultSet rs = stmt.executeQuery(query);
			
			/* Watch a video */
			int videoID = 0;
			boolean existVideo = rs.next();
			
			if (!existVideo) {
				System.out.println("----- There is no video of the name '" + videoTitle + "'. Return to previous menu -----");
				
				rs.close();
				jdbc.closeConnection();
				
				return;
			}
			else {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				String time = sdf.format(timestamp);
				
				int ageLimit = rs.getInt("ageLimit");
				if (age < ageLimit) {
					System.out.println("----- Age Limit!! You cannot watch this video -----");
					
					rs.close();
					jdbc.closeConnection();
					
					return;
				}
				
				videoID = rs.getInt("videoID");
				String getVideoTitle = rs.getString("videoTitle");
				String getUploaderID = rs.getString("uploaderID");
				int subNum = rs.getInt("subscribedNum");
				String uploadDate = rs.getString("uploadDate");
				String videoLength = rs.getString("videoLength");
				int views = rs.getInt("views");
				int likes = rs.getInt("likes");
				
				System.out.println("\n----- " + getVideoTitle + " -----");
				System.out.println("Uploader ID : " + getUploaderID + ", Subscribers : " + subNum + ", Upload Date : " + uploadDate
						+ ", Length : " + videoLength + ", Views : " + views + ", Likes : " + likes);
				
				/* 영상을 보고 난 뒤 조회수 상승 */
				stmt.executeUpdate("UPDATE VIDEO SET views = views + 1 WHERE videoID = " + videoID);
				
				/* 시청 기록 추가 */
				stmt.executeUpdate("INSERT INTO WATCH VALUES ('" + userID + "', " + videoID + ", '" + time + "', NULL)");
				
				rs.close();
				jdbc.closeConnection();
				
				/* 댓글 보여주기 */
				showComments(videoID);
				
				interactVideo(videoID, uploaderID, time);
				
				return;
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void showComments(int videoID) throws SQLException {
		stmt = jdbc.getStatement();
		
		String query = "SELECT userID, comment FROM WATCH WHERE watchVideoID = " + videoID;
		ResultSet rs = stmt.executeQuery(query);
		
		System.out.println("-------- Comments --------");
		while(rs.next()) {
			String userID = rs.getString("userID");
			String comment = rs.getString("comment");
			
			if (comment != null) {
				System.out.println(userID + " : " + comment);
			}
		}
		System.out.println("--------------------------");
	}
	
	public void interactVideo(int videoID,  String uploaderID, String time) throws SQLException {
		while(true) {
			stmt = jdbc.getStatement();
		
			boolean subscribes = false;
			ResultSet rs = stmt.executeQuery("SELECT * FROM SUBSCRIBES WHERE userID = '" + userID + "' AND subUserID = '" + uploaderID + "'");
			if (rs.next()) {
				subscribes = true;
			}
			rs.close();
			jdbc.closeConnection();
		
			System.out.println("\n## Interaction ##");
			System.out.println("1. Like this video");
		
			if (!subscribes) {
				System.out.println("2. Subscribe uploader");
			}
			else {
				System.out.println("2. Unsubscribe uploader");
			}
		
			System.out.println("3. Leave a comment");
			System.out.println("4. Put this video in a playlist");
			System.out.println("5. Report this video");
			System.out.println("6. Report uploader");
			System.out.println("7. Return to previous menu");
			System.out.print("Please enter a number to select a mode : ");
			String mode = scanner.nextLine();
		
			if (mode.equals("1")) {
				likeVideo(videoID);
			}
			else if (mode.equals("2")) {
				subscribe(uploaderID, subscribes);
			}
			else if (mode.equals("3")) {
				leaveComment(videoID, time);
			}
			else if (mode.equals("4")) {
				putPlaylist(videoID);
			}
			else if (mode.equals("5")) {
				report(videoID, "video");
			}
			else if (mode.equals("6")) {
				report(uploaderID);
			}
			else if (mode.equals("7")) {
				return;
			}
			else {
				System.out.println("----- Wrong command. Try again -----");
				interactVideo(videoID, uploaderID, time);
			}
		}
	}
	
	public void likeVideo(int videoID) throws SQLException {
		stmt = jdbc.getStatement();
		
		stmt.executeUpdate("UPDATE VIDEO SET likes = likes + 1 WHERE videoID = " + videoID);
		System.out.println("---- Like! -----");
		
		jdbc.closeConnection();
	}
	
	/* 구독, 구독 취소 기능 */
	public void subscribe(String uploaderID, boolean subscribes) throws SQLException {
		stmt = jdbc.getStatement();
		
		if (!subscribes) {
			stmt.executeUpdate("UPDATE USER SET subscribedNum = subscribedNum + 1 WHERE userID = '" + uploaderID + "'");
			stmt.executeUpdate("INSERT INTO SUBSCRIBES VALUES ('" + userID + "', '" + uploaderID + "')");
		
			System.out.println("----- Subscribe " + userID + " -----");
		}
		else {
			stmt.executeUpdate("UPDATE USER SET subscribedNum = subscribedNum - 1 WHERE userID = '" + uploaderID + "'");
			stmt.executeUpdate("DELETE FROM SUBSCRIBES WHERE userID = '" + userID + "' AND subUserID = '" + uploaderID + "'");
			
			System.out.println("----- Unubscribe " + userID + " -----");
		}
		
		jdbc.closeConnection();
	}
	
	public void leaveComment(int videoID, String time) throws SQLException {
		System.out.print("Please leave a comment : ");
		String comment = scanner.nextLine();
		
		stmt = jdbc.getStatement();
		String query = "UPDATE WATCH SET comment = '" + comment + "' WHERE userID = '" + userID + "' AND watchVideoID = " + videoID + " AND watchStartTime = '" + time + "'";
		stmt.executeUpdate(query);
		
		System.out.println("---- You leaved a comment! -----");
		System.out.println(userID + " : " + comment);
		
		jdbc.closeConnection();
	}
	
	public void putPlaylist(int videoID) {
		boolean hasList = showUserPlaylist();
		
		if (hasList) {
			System.out.print("\nPlease enter a name of playlist you want to put a video : ");
			String listN = scanner.nextLine();
			
			stmt = jdbc.getStatement();
			
			try {
				ResultSet rs = stmt.executeQuery("SELECT listID FROM PLAYLIST WHERE listTitle = '" + listN + "' AND makerID = '" + userID + "'");
				rs.next();
				String listID = rs.getString("listID");
				rs.close();
			
				int num = stmt.executeUpdate("INSERT INTO CONSIST_OF VALUES (" + listID + ", " + videoID + ")");
				
				if (num == 0) {
					System.out.println("*** Cannot insert a video in playlist ***");
				}
				else {
					System.out.println("----- Insertion Complete! -----");
				}
				
				stmt.close();
			}
			catch (SQLException e) {
				System.out.println("*** This video is already in playlist! ***");
				try {
					stmt.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				return;
			}
		}
		else {
			System.out.println("*** You don't have a playlist. Return to previous menu ***");
		}
	}
	
	/* Report Video or Playlist */
	public void report(int ID, String kind) throws SQLException {
		stmt = jdbc.getStatement();
		
		if (kind.equals("video")) {
			int num = stmt.executeUpdate("UPDATE VIDEO SET vReportNum = vReportNum + 1 WHERE videoID = " + ID);
			if (num == 0) {
				System.out.println("## Cannot report the video ##");
			}
			else {
				System.out.println("----- Report Complete -----");
			}
		}
		else if (kind.equals("playlist")) {
			int num = stmt.executeUpdate("UPDATE PLAYLIST SET listReportNum = listReportNum + 1 WHERE listID = " + ID);
			if (num == 0) {
				System.out.println("## Cannot report the playlist ##");
			}
			else {
				System.out.println("----- Report Complete -----");
			}
		}
		
		jdbc.closeConnection();
	}
	
	/* Report User */
	public void report(String userID) throws SQLException {
		stmt = jdbc.getStatement();
		
		int num = stmt.executeUpdate("UPDATE USER SET userReportNum = userReportNum + 1 WHERE userID = '" + userID + "'");
		if (num == 0) {
			System.out.println("## Cannot report the user ##");
		}
		else {
			System.out.println("----- Report Complete -----");
		}
		
		jdbc.closeConnection();
	}
	
	public void showPlaylist() {
		stmt = jdbc.getStatement();
		
		System.out.println("\n----- Show PlayLists -----");
		
		try {
			String query = "SELECT listTitle, makerID FROM PLAYLIST";
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				String listTitle = rs.getString("listTitle");
				String makerID = rs.getString("makerID");
				
				System.out.println("List Title : " + listTitle + ", Maker ID : " + makerID);
			}
			
			rs.close();
			jdbc.closeConnection();
			
			watchPlaylist();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void watchPlaylist() {
		stmt = jdbc.getStatement();
		
		System.out.println("\n## Wacth a Playlist ##");
		System.out.print("Please enter a name of playlist you want to watch : ");
		String name = scanner.nextLine();
		
		System.out.print("Please enter a maker ID of playlist you want to watch : ");
		String makerID = scanner.nextLine();
		
		String orderQuery = selectOrder();
		
		System.out.println("\n----- " + name + " -----");
		try {
			String query = "SELECT listID, videoTitle, uploaderID, videoLength, views, ageLimit FROM USER, VIDEO, CONSIST_OF, PLAYLIST "
					+ "WHERE ageLimit <= age AND userID = '" + userID + "' AND makerID = '" + makerID + "' AND listTitle = '" + name + "' AND listID = cListID AND cVideoID = videoID"
					+ orderQuery;
			ResultSet rs = stmt.executeQuery(query);
			int listID = 0;
			
			boolean inList = false;
			
			while(rs.next()) {
				inList = true;
				listID = rs.getInt("listID");
				String videoTitle = rs.getString("videoTitle");
				String uploaderID = rs.getString("uploaderID");
				String videoLength = rs.getString("videoLength");
				int views = rs.getInt("views"); 
				int ageLimit = rs.getInt("ageLimit");
				
				System.out.println("Video Title : " + videoTitle + ", Uploader ID : " + uploaderID + ", Video Length : " + videoLength
						+ ", Views : " + views + ", Age Limit : " + ageLimit);
			}
			
			rs.close();
			stmt.close();
			
			if (!inList) {
				System.out.println("*** There is no playlist named '" + name + "' or no videos in '" + name + "' ***");
			}
			else {
				interactPlaylist(listID);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String selectOrder() {
		System.out.println("\n----- Select sort order ------");
		System.out.println("1. Default order");
		System.out.println("2. Upload Date (most recent)");
		System.out.println("3. Upload Date (oldest)");
		System.out.println("4. Video length (longest)");
		System.out.println("5. Most popular");
		System.out.print("Please selecct the number of sort order : ");
		String order = scanner.nextLine();
		
		String query = "";
		
		if (order.equals("1")) {}
		else if (order.equals("2")) {
			query += " ORDER BY uploadDate DESC";
		}
		else if (order.equals("3")) {
			query += " ORDER BY uploadDate ASC";
		}
		else if (order.equals("4")) {
			query += " ORDER BY videoLength DESC";
		}
		else if (order.equals("5")) {
			query += " ORDER BY views DESC";
		}
		else {
			System.out.println("*** Wrong command. Try again ***");
			selectOrder();
		}
		
		return query;
	}
	
	public void interactPlaylist(int listID) throws SQLException {
		while(true) {
			System.out.println("\n*** Interact Playlist ***");
			System.out.println("1. Watch a video");
			System.out.println("2. Report a playlist");
			System.out.println("3. Return to previous menu");
			System.out.print("Please enter a key to select a mode : ");
		
			String mode = scanner.nextLine();
			if (mode.equals("1")) {
				watchVideo();
			}
			else if (mode.equals("2")) {
				report(listID, "playlist");
			}
			else if (mode.equals("3")) {
				return;
			}
			else {
				System.out.println("----- Wrong command. Try again -----");
			}
		}
	}
	
	public void manageChannel() {
		while(true) {
			System.out.println("\n----- Manage " + userID + "'s Channel -----");
			System.out.println("1. Show Channel Info");
			System.out.println("2. Show Subscribing Channels");
			System.out.println("3. Upload Video");
			System.out.println("4. Delete Video");
			System.out.println("5. Show My Upload List");
			System.out.println("6. Show My View History");
			System.out.println("7. Make Playlist");
			System.out.println("8. Delete Playlist");
			System.out.println("9. Show User's playlist");
			System.out.println("0. Return to previous menu");
			System.out.print("Please enter a number to select a mode : ");
		
			String mode = scanner.nextLine();
		
			if (mode.equals("1")) {
				showUserInfo();
			}
			else if (mode.equals("2")) {
				showSubs();
			}
			else if (mode.equals("3")) {
				uploadVideo();
			}
			else if (mode.equals("4")) {
				deleteVideo();
			}
			else if (mode.equals("5")) {
				showUploadList();
			}
			else if (mode.equals("6")) {
				showViewHistory();
			}
			else if (mode.equals("7")) {
				makePlaylist();
			}
			else if (mode.equals("8")) {
				deletePlaylist();	
			}
			else if (mode.equals("9")) {
				showUserPlaylist();
			}
			else if (mode.equals("0")) {
				return;
			}
			else {
				System.out.println("----- Wrong command. Try again -----");
			}
		}
	}
	
	public void showUserInfo() {
		stmt = jdbc.getStatement();
		System.out.println("\n----- Show Channel Info -----");
		
		try {
			String query = "SELECT userID, subscribedNum, age FROM USER WHERE userID = '" + userID + "'";
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				String userID = rs.getString("userID");
				int subscribedNum = rs.getInt("subscribedNum");
				int age = rs.getInt("age");
				
				System.out.println("User ID : " + userID + ", Subs Number : " + subscribedNum + ", Age : " + age);
			}
			
			rs.close();
			jdbc.closeConnection();
		}
		catch (SQLException e) {
			
		}
	}
	
	public void showSubs() {
		stmt = jdbc.getStatement();
		System.out.println("\n----- List of Subscribing Channels -----");
		
		try {
			String query = "SELECT subUserID FROM SUBSCRIBES WHERE userID = '" + userID + "'";
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				String subUserID = rs.getString("subUserID");
				System.out.println(subUserID);
			}
			
			rs.close();
			jdbc.closeConnection();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void uploadVideo() {
		System.out.println("\n----- Upload a Video -----");
		
		System.out.print("Please enter a video name : ");
		String videoName = scanner.nextLine();
		
		System.out.print("Please enter a video's length (yy:mm:ss) : ");
		String videoLen = scanner.nextLine();
		
		System.out.print("Please enter an age limit of the video : ");
		int ageLimit = scanner.nextInt();
		scanner.nextLine();
		
		Date today = new Date();
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
		String uploadDate = date.format(today);
		
		stmt =jdbc.getStatement();
		
		try {
			String query = "INSERT INTO VIDEO (`uploaderID`, `videoTitle`, `uploadDate`, `videoLength`, `views`, `likes`, `vReportNum`, `ageLimit`) "
					+ "VALUES ('" + userID + "', '"+ videoName + "', '" + uploadDate + "', '" + videoLen + "', 0, 0, 0, " + ageLimit + ")";
			stmt.executeUpdate(query);
			
			System.out.println("----- Upload Complete! -----");
		} 
		catch (SQLException e) {
			System.out.println("*** You cannot upload a video with the duplicate name ***");
		}
		
		jdbc.closeConnection();
	}
	
	public void deleteVideo() {
		boolean hasUploadList = showUploadList();
		
		if (!hasUploadList) {
			System.out.println("----- There are no videos to delete -----");
			manageChannel();
		}
		
		try {
			stmt = jdbc.getStatement();
			System.out.print("Please enter a video title you want to delete : ");
			String videoTitle = scanner.nextLine();
		
			int num = stmt.executeUpdate("DELETE FROM VIDEO WHERE videoTitle = '" + videoTitle + "' AND uploaderID = '" + userID + "'");
			if (num == 0) {
				System.out.println("## Cannot delete a video ##");
			}
			else {
				System.out.println("----- Delete " + videoTitle + " -----");
			}
			
			jdbc.closeConnection();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean showUploadList() {
		stmt = jdbc.getStatement();
		
		boolean hasUploadList = false;
		
		System.out.println("\n## " + userID + "'s Upload List ##");
		
		try {
			String query = "SELECT videoTitle, uploadDate, videoLength, views, likes, ageLimit FROM VIDEO WHERE uploaderID = '" + userID + "' ORDER BY uploadDate DESC";
			ResultSet rs = stmt.executeQuery(query);
		
			while(rs.next()) {
				hasUploadList = true;
				
				String videoTitle = rs.getString("videoTitle");
				String uploadDate = rs.getString("uploadDate");
				String videoLength = rs.getString("videoLength");
				int views = rs.getInt("views");
				int likes = rs.getInt("likes");
				int ageLimit = rs.getInt("ageLimit");
				
				System.out.println("Video Title : " + videoTitle + ", Upload Date : " + uploadDate
						+ ", Video Length : " + videoLength + ", Views : " + views + ", Likes : " + likes + ", Age Limit : " + ageLimit);
			}
			
			rs.close();
			jdbc.closeConnection();
			
			return hasUploadList;
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return hasUploadList;
	}
	
	public void showViewHistory() {
		stmt = jdbc.getStatement();
		
		System.out.println("\n## " + userID + "'s View History ##");
		
		try {
			String query = "SELECT videoTitle, uploaderID, watchStartTime FROM WATCH, VIDEO WHERE userID = '" + userID + "' AND watchVideoID = videoID ORDER BY watchStartTime DESC";
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				String videoTitle = rs.getString("videoTitle");
				String uploaderID = rs.getString("uploaderID");
				String watchTime = rs.getString("watchStartTime");
				
				System.out.println("Video Title : " + videoTitle + ", Uploader ID : " + uploaderID + ", Watch Start Time : " + watchTime);
			}
			
			rs.close();
			jdbc.closeConnection();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void makePlaylist() {
		System.out.println("\n----- Make a Playlist -----");
		
		System.out.print("Please enter a playlist title : ");
		String listName = scanner.nextLine();
		
		stmt = jdbc.getStatement();
		
		try {
			String query = "INSERT INTO `PLAYLIST` (`makerID`, `listTitle`, `listReportNum`) VALUES "
					+ "('" + userID + "', '" + listName + "', 0)";
			stmt.executeUpdate(query);
			jdbc.closeConnection();
			
			System.out.println("----- Creation Complete! -----");
		}
		catch (SQLException e) {
			System.out.println("*** Cannot create a playlist with a duplicate name ***");
			e.printStackTrace();
		}
	}
	
	public void deletePlaylist() {
		boolean hasPlaylist = showUserPlaylist();
		
		if (!hasPlaylist) {
			System.out.println("----- There is no playlist to delete -----");
			manageChannel();
		}
		
		try {
			stmt = jdbc.getStatement();
			System.out.print("Please enter a playlist title you want to delete : ");
			String listTitle = scanner.nextLine();
		
			int num = stmt.executeUpdate("DELETE FROM PLAYLIST WHERE listTitle = '" + listTitle + "' AND makerID = '" + userID + "'");
			if (num == 0) {
				System.out.println("## Cannot delete a playlist ##");
			}
			else {
				System.out.println("----- Delete " + listTitle + " -----");
			}
			
			jdbc.closeConnection();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean showUserPlaylist() {
		stmt = jdbc.getStatement();
		boolean hasPlaylist = false;
	
		System.out.println("\n## " + userID + "'s Playlist List ##");
		
		try {
			String query = "SELECT listTitle FROM PLAYLIST WHERE makerID = '" + userID + "'";
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				hasPlaylist = true;
				String listTitle = rs.getString("listTitle");
				System.out.println("Playlist Title : " + listTitle);
			}
			
			rs.close();
			jdbc.closeConnection();
			
			return hasPlaylist;
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return hasPlaylist;
	}
	
	public void updateUser() {
		while(true) {
			System.out.println("\n----- Update User Information -----");
			System.out.println("1. Change user's password");
			System.out.println("2. Delete your account");
			System.out.println("3. Return to previous menu");
			System.out.print("Please enter a number to select a mode : ");
		
			String mode = scanner.nextLine();
			
			if (mode.equals("1")) {
				changePW();
			}
			else if (mode.equals("2")) {
				deleteUser();
			}
			else if (mode.equals("3")) {
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
		
			System.out.println("\n----- Change User Password -----");
		
			System.out.print("Please enter your new PW (Max length = 10) : ");
			userPW = scanner.nextLine();
			
			if (userPW.length() > 10) {
				System.out.println("*** Please use another PW ***");
				System.out.print("Please enter new user's PW (Max length = 10) : ");
				userPW = scanner.nextLine();
			}
			
			String query = "UPDATE USER SET userPW = '" + userPW + "' WHERE userID = '" + userID + "'";
			stmt.executeUpdate(query);
			
			jdbc.closeConnection();
			System.out.println("----- Password Changed -----");
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteUser() {
		System.out.println("\n----- Delete Your Account -----");
		
		System.out.println("IF YOU WANT TO DELETE THIS ACCOUNT FOR SURE, TYPE 'Y'");
		String command = scanner.nextLine();
		
		if (command.equals("Y")) {
			try {
				stmt = jdbc.getStatement();
				
				String query = "DELETE FROM USER WHERE userID = '" + userID + "'";
				stmt.executeUpdate(query);
				
				jdbc.closeConnection();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
			
			System.out.println("----- " + userID + " Deleted. Return to initial menu -----\n");
			startUser();
		}
		else {
			System.out.println("----- Account deletion canceled -----");
		}
	}
	
	public void createUser() {
		try {
			stmt = jdbc.getStatement();
			
			System.out.println("\n----- Sign Up -----");
			System.out.print("Please enter new user's ID (Max length = 10) : ");
			userID = scanner.nextLine();
			
			if (userID.length() > 10) {
				System.out.println("*** Please use another ID ***");
				System.out.print("Please enter new user's ID (Max length = 10) : ");
				userID = scanner.nextLine();
			}
		
			ResultSet rs = stmt.executeQuery("SELECT userID FROM USER");
			
			while(rs.next()) {
				String ID = rs.getString("userID");
				
				if (userID.equals(ID)) {
					System.out.println("----- Creation Failed. There is a user with a duplicate ID. -----");
					System.out.println("1. Try again");
					System.out.println("2. Return to Main");
					System.out.println("All numbers except 1,2. Quit Program");
					System.out.print("Please enter a number : ");
					
					String mode = scanner.nextLine();
					
					if (mode.equals("1")) {
						rs.close();
						jdbc.closeConnection();
						
						createUser();
					}
					else if (mode.equals("2")) {
						rs.close();
						jdbc.closeConnection();
						
						startUser();
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
			
			System.out.print("Please enter new user's PW (Max Length = 10) : ");
			userPW = scanner.nextLine();
			
			if (userPW.length() > 10) {
				System.out.println("*** Please use another PW ***");
				System.out.print("Please enter new user's PW (Max length = 10) : ");
				userPW = scanner.nextLine();
			}
			
			System.out.print("Please enter new user's age : ");
			int age = scanner.nextInt();
			scanner.nextLine();
			
			String managerQuery = "SELECT managerID FROM MANAGER ORDER BY RAND() LIMIT 1";
			ResultSet managerRS = stmt.executeQuery(managerQuery);
			
			String managerID = null;
			while(managerRS.next()) {
				managerID = managerRS.getString("managerID");
			}
			
			String query = "INSERT INTO USER VALUES ('" + managerID + "', '" + userID + "', '" + userPW + "', 0, 0, " + age + ")";
			stmt.executeUpdate(query);
			
			System.out.println("----- Creation Success! Return to Main -----\n");
			
			rs.close();
			jdbc.closeConnection();
			
			startUser();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
