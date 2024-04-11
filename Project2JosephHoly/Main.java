import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
	public static String currentUser = null;
	public static int userCount = 0;
	public static int screen = 1;
	public static List<String> userList = new ArrayList<>();
	public static List<String> passwordList = new ArrayList<>();
	public static List<String> postList = new ArrayList<>();
	public static List<String> visibleUsers = new ArrayList<>();
	public static LocalDateTime newPostTime = LocalDateTime.now();
	public static DateTimeFormatter postTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	public static String usableNPT = newPostTime.format(postTimeFormatter);
	public static int defaultSetting = 1;
	public static final String ANSI_Cyan = "\u001B[36m";

	public static void main(String[] args) {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(Database.url, Database.username, Database.password);
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		while (true) {
			String countUsers = "select count(*) userID from Users;";
			String userData = "select username, password from Users;";
			PreparedStatement userCountPrep = null;
			ResultSet userCountRS = null;
			PreparedStatement userStoragePrep = null;
			ResultSet userStorageRS = null;
			try {
				userCountPrep = conn.prepareStatement(countUsers);
				userCountRS = userCountPrep.executeQuery();
				while (userCountRS.next()) {
					userCount = userCountRS.getInt("userID");
				}
				userStoragePrep = conn.prepareStatement(userData);
				userStorageRS = userStoragePrep.executeQuery();
				while (userStorageRS.next()) {
					String username = userStorageRS.getString("username");
					String password = userStorageRS.getString("password");
					userList.add(username);
					passwordList.add(password);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			Views newViews = new Views();
			while (screen == 1) {
				newViews.mainWindow(userCount);
				Scanner logInScanner = new Scanner(System.in);
				System.out.print("Type your username: ");
				String userInput = logInScanner.nextLine();
				if (userList.contains(userInput)) {
					System.out.print("Type your password: ");
					String passwordInput = logInScanner.nextLine();
					int userIndex = userList.indexOf(userInput);
					if (passwordInput.equals(passwordList.get(userIndex))) {
						currentUser = userInput;
						System.out.println("Logged in!");
						screen += 1;
					} else {
						System.out.println("Incorrect password. Try again.");
						continue;
					}
				} else {
					System.out.println("Incorrect username");
					Scanner createScanner = new Scanner(System.in);
					System.out.println("Press C to create a new account" + "\n" + "Or press any key to try again");
					String createInput = logInScanner.nextLine();
					if (createInput.equals("C")) {
						String newDatabaseAccount = """
									insert into Users (username, password) values(?, ?);
								""";
						PreparedStatement newAccountPrep = null;
						ResultSet newAccountRS = null;
						Scanner newUsernameScanner = new Scanner(System.in);
						System.out.println("Enter your new account username: ");
						String newUsernameInput = logInScanner.nextLine();
						if (userList.contains(newUsernameInput)) {
							System.out.println("This username has already been taken.");
							continue;
						}
						Scanner newPasswordScanner = new Scanner(System.in);
						System.out.println("Enter your new account password: ");
						String newPasswordInput = logInScanner.nextLine();
						try {
							newAccountPrep = conn.prepareStatement(newDatabaseAccount, Statement.RETURN_GENERATED_KEYS);
							newAccountPrep.setString(1, newUsernameInput);
							newAccountPrep.setString(2, newPasswordInput);
							newAccountPrep.executeUpdate();
							newAccountRS = newAccountPrep.getGeneratedKeys();
							newAccountRS.next();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						userList.add(newUsernameInput);
						passwordList.add(newPasswordInput);
						System.out.println("Awesome, you created an account!" + "\n" + "You can now log in.");
					} else {
						continue;
					}
				}
			}
			while (screen == 2) {
				newViews.accountWindow(currentUser);
				Scanner accountWindowScanner = new Scanner(System.in);
				System.out.print("Your choice: ");
				String accountChoice = accountWindowScanner.nextLine();
				if (accountChoice.equals("P")) {
					screen += 1;
				} else if (accountChoice.equals("V")) {
					screen += 2;
				} else if (accountChoice.equals("Q")) {
					System.exit(0);
				} else {
					continue;
				}
			}
			while (screen == 3) {
				Views.postWindow1(currentUser);
				String username = "select userID, username from Users;";
				String postString = "";
				if (defaultSetting == 1) {
					postString = "select * from Posts where userID in (select visibleID from Visibility where userID = (select userID from Users where username = ?)) order by postTime;";
				} else if (defaultSetting == 2) {
					postString = "select * from Posts where userID in (select visibleID from Visibility where userID = (select userID from Users where username = ?)) order by postTime desc;";
				} else if (defaultSetting == 3) {
					postString = "select * from Posts where userID in (select visibleID from Visibility where userID = (select userID from Users where username = ?)) order by (select username from Users where Users.userID = Posts.userID);";
				} else if (defaultSetting == 4) {
					postString = "select * from Posts where userID in (select visibleID from Visibility where userID = (select userID from Users where username = ?)) order by (select username from Users where Users.userID = Posts.userID) desc;";
				}
				PreparedStatement postUsernamePrep = null;
				ResultSet postUsernameRS = null;
				PreparedStatement postPrep = null;
				ResultSet postRS = null;
				Map<String, String> usernameMap = new HashMap<>();
				try {
					postUsernamePrep = conn.prepareStatement(username);
					postUsernameRS = postUsernamePrep.executeQuery();
					while (postUsernameRS.next()) {
						String userID = postUsernameRS.getString("userID");
						String getUsername = postUsernameRS.getString("username");
						usernameMap.put(userID, getUsername);
					}
					postPrep = conn.prepareStatement(postString);
					postPrep.setString(1, currentUser);
					postRS = postPrep.executeQuery();
					while (postRS.next()) {
						int postID = postRS.getInt("postID");
						String postText = postRS.getString("postText");
						String postTime = postRS.getString("postTime");
						int userID = postRS.getInt("userID");
						String postUsername = usernameMap.get(String.valueOf(userID));
						System.out.println("| " + String.format("%-48s", "\u001B[34m" + postText + "\u001B[0m") + "|");
						System.out.print("|" + String.format("%50s%n",
								"\u001B[33m" + postUsername + ", " + postTime + "\u001B[0m" + " |"));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				Views.postWindow2(currentUser);
				Scanner postScanner = new Scanner(System.in);
				System.out.print("Your choice: ");
				String postChoice = postScanner.nextLine();
				if (postChoice.equals("+")) {
					int currentUserID = userList.indexOf(currentUser) + 1;
					Scanner newPostScanner = new Scanner(System.in);
					System.out.print("Create your post: ");
					String newPostChoice = newPostScanner.nextLine();
					String newPost = """
								insert into Posts
								(postText, postTime, userID)
							values(?, ?, ?);
								""";
					PreparedStatement newPostPrep = null;
					ResultSet newPostRS = null;
					try {
						newPostPrep = conn.prepareStatement(newPost, Statement.RETURN_GENERATED_KEYS);
						newPostPrep.setString(1, newPostChoice);
						newPostPrep.setString(2, usableNPT);
						newPostPrep.setInt(3, currentUserID);
						newPostPrep.executeUpdate();
						newPostRS = newPostPrep.getGeneratedKeys();
						newPostRS.next();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					continue;
				} else if (postChoice.equals("S")) {
					Scanner sortScanner = new Scanner(System.in);
					System.out.print("Sort posts by: ");
					String sortChoice = sortScanner.nextLine();
					if (sortChoice.equals("TD")) {
						defaultSetting = 2;
						continue;
					} else if (sortChoice.equals("UA")) {
						defaultSetting = 3;
						continue;
					} else if (sortChoice.equals("UD")) {
						defaultSetting = 4;
						continue;
					} else {
						defaultSetting = 1;
						continue;
					}
				} else if (postChoice.equals("B")) {
					screen -= 1;
				} else {
					continue;
				}
			}
			while (screen == 4) {
				Views.visibilityWindow(currentUser, getVisibleUsers(conn, currentUser));

				Scanner visibilityScanner = new Scanner(System.in);
				System.out.print("Your choice: ");
				String visibilityChoice = visibilityScanner.nextLine();
				if (visibilityChoice.equals("+")) {
					System.out.print("Enter username to add: ");
					String userAdd = visibilityScanner.nextLine();
					if (userAdd.equals(currentUser)) {
						System.out.println("Incorrect, try again.");
					} else {
						addUserVisibility(conn, userAdd, currentUser);
					}
				} else if (visibilityChoice.equals("-")) {
					System.out.print("Enter username to delete: ");
					String userDelete = visibilityScanner.nextLine();
					deleteUserVisibility(conn, userDelete, currentUser);
				} else if (visibilityChoice.equals("B")) {
					screen -= 2;
				} else {
					System.out.print("Incorrect");
					System.out.print("\n");
					continue;
				}
			}
		}
	}

	private static List<String> getVisibleUsers(Connection conn, String currentUser) {
		List<String> visibleUsers = new ArrayList<>();
		String visibilityQuery = "select username from Users where userID in (select visibleID from Visibility where userID = (select userID from Users where username = ?))";
		PreparedStatement visibilityPrep = null;
		ResultSet visibilityRS = null;
		try {
			visibilityPrep = conn.prepareStatement(visibilityQuery);
			visibilityPrep.setString(1, currentUser);
			visibilityRS = visibilityPrep.executeQuery(); // execute query
			while (visibilityRS.next()) {
				visibleUsers.add(visibilityRS.getString("username"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (visibilityRS != null) {
				visibilityRS.close();
			}
			if (visibilityPrep != null) {
				visibilityPrep.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return visibleUsers;
	}

	private static void addUserVisibility(Connection conn, String currentUser, String userToAdd) {
		List<String> visibleUsers = getVisibleUsers(conn, currentUser);
		if (currentUser.equals(userToAdd)) {
			System.out.println("Incorrect. Try Again.");
		}
		if (visibleUsers.contains(userToAdd)) {
			System.out.println("Incorrect. Try again.");
			return;
		}
		String addVisibilityQuery = "insert into Visibility (userID, visibleID) values ((select userID from Users where username = ?),(select userID from Users where username = ?))";
		PreparedStatement addVisibilityPrep = null;
		try {
			addVisibilityPrep = conn.prepareStatement(addVisibilityQuery);
			addVisibilityPrep.setString(1, currentUser);
			addVisibilityPrep.setString(2, userToAdd);
			addVisibilityPrep.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (addVisibilityPrep != null) {
				addVisibilityPrep.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void deleteUserVisibility(Connection conn, String currentUser, String userToDelete) {
		List<String> visibleUsers = getVisibleUsers(conn, currentUser);
		if (userToDelete.equals(currentUser)) {
			System.out.println("Incorrect. Try again.");
			return;
		}
		if (!visibleUsers.contains(userToDelete)) {
			System.out.println("Incorrect. Try again.");
			return;
		}
		String deleteVisibilityQuery = "delete from Visibility where userID = (select userID from Users where username = ?) and visibleID = (select userID from Users where username = ?)";
		PreparedStatement deleteVisibilityPrep = null;
		try {
			deleteVisibilityPrep = conn.prepareStatement(deleteVisibilityQuery);
			deleteVisibilityPrep.setString(1, currentUser);
			deleteVisibilityPrep.setString(2, userToDelete);
			deleteVisibilityPrep.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (deleteVisibilityPrep != null) {
				deleteVisibilityPrep.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
