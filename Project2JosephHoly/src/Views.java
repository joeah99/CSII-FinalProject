import java.util.List;
import java.util.Scanner;

public class Views {
	public static final String ANSI_RESET = "\u001B[0m";

	public static final String ANSI_Green = "\u001B[32m";

	public static final String ANSI_Yellow = "\u001B[33m";

	public static final String ANSI_Blue = "\u001B[34m";

	public static final String ANSI_Cyan = "\u001B[36m";

	public static void mainWindow(int userCount) {
		System.out.println(ANSI_Cyan + " ========================================");
		System.out.println("|         Welcome to Jstgram 2.0!        |");
		System.out.println("|                                        |");
		System.out.println("|              *************             |");
		System.out.println("|                    *                   |");
		System.out.println("|                    *                   |");
		System.out.println("|                    *                   |");
		System.out.println("|                    *                   |");
		System.out.println("|              *     *                   |");
		System.out.println("|              *******                   |");
		System.out.println("|                                        |");
		System.out.println("| Current number of users in database: " + userCount + " |");
		System.out.println(" ========================================" + ANSI_RESET);
	}

	public static void accountWindow(String currentUser) {
		String formattedCurrentUser = String.format("|   Current user: %-23s|", currentUser);

		System.out.println(ANSI_Cyan + " ========================================");
		System.out.println("|                                        |");
		System.out.println("|    (P) Posts                           |");
		System.out.println("|    (V) Post Visibility                 |");
		System.out.println("|    (Q) Quit                            |");
		System.out.println("|                                        |");
		System.out.println(formattedCurrentUser);
		System.out.println(" ========================================" + ANSI_RESET);
	}

	public static void postWindow1(String currentUser) {
		System.out.println(ANSI_Cyan + " ========================================");
		System.out.println("|Choose how posts are sorted:            |");
		System.out.println("|Time asc. (default) = any key           |");
		System.out.println("|Time desc. = TD                         |");
		System.out.println("|Username asc. = UA                      |");
		System.out.println("|Username desc. = UD                     |");
		System.out.println("|                                        |");
		System.out.println("| My posts and visible posts             |");
		System.out.println("|                                        |" + ANSI_RESET);
	}

	public static void postWindow2(String currentUser) {
		String formattedCurrentUser = String.format("|   Current user: %-23s|", currentUser);
		System.out.println(ANSI_Cyan + "|                                        |");
		System.out.println("|    (+) Publish a new post              |");
		System.out.println("|    (B) Back                            |");
		System.out.println("|    (S) Sort By                         |");
		System.out.println("|                                        |");
		System.out.println(formattedCurrentUser);
		System.out.println(" ========================================" + ANSI_RESET);
	}

	public static void visibilityWindow(String currentUser, List<String> visibleUsers) {
		String formattedCurrentUser = String.format("|   Current user: %-23s|", currentUser);

		System.out.println(ANSI_Cyan + " ========================================");
		System.out.println("|My posts are visible to following users |");
		System.out.println("|                                        |");

		for (String visibleUser : visibleUsers) {
			if (!visibleUser.equals(currentUser)) {
				String formattedVisibleUser = String.format("| %-38s |", visibleUser);
				System.out.println(formattedVisibleUser);
			}
		}

		System.out.println("|                                        |");
		System.out.println("|    (+) Add a user                      |");
		System.out.println("|    (-) Delete a user                   |");
		System.out.println("|    (B) Back                            |");
		System.out.println("|                                        |");
		System.out.println(formattedCurrentUser);
		System.out.println(" ========================================" + ANSI_RESET);
	}

}