package org.example;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import static org.example.Books_Information.*;
import static org.example.User_Information.*;

public class Main {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/library_Management";
    private static final String USER = "root";
    private static final String PASSWORD = "Root";
    private static final String USERNAME = "admin";
    private static final String ADMIN_PW = "password";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Scanner scanner = new Scanner(System.in)) {
             boolean loggedIn = false;
             while (!loggedIn) {
                System.out.println("ADMIN: Librarian");
                System.out.print("Enter username: ");
                String username = scanner.nextLine();
                System.out.print("Enter password: ");
                String password = scanner.nextLine();


                if (username.equals(USERNAME) && password.equals(ADMIN_PW)) {
                    loggedIn = true;
                    System.out.println("Login successful!");
                } else {
                    System.out.println("Invalid username or password. Please try again.");
                }
            }

            while (true) {
                System.out.println("-------------------------------------------------");
                System.out.println("Welcome To THE KNOWLEDGE HUB");
                System.out.println("-------------------------------------------------");
                System.out.println("Library Management System");
                System.out.println("1. Add Book");
                System.out.println("2. View Available Books");
                System.out.println("3. Borrow a Book");
                System.out.println("4. Return a Book");
                System.out.println("5. Register a Member");
                System.out.println("6. View Members");
                System.out.println("7. Update Member Information");
                System.out.println("8. Search Books");
                System.out.println("9. Remove a Book");
                System.out.println("10. Remove a Member");
                System.out.println("11. Exit");
                System.out.print("Choose an option: ");
                int option = 0;
                while (true) {
                    try {
                        option = scanner.nextInt();
                        scanner.nextLine();
                        break;
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input! Please enter an integer.");
                        System.out.println("Choose an Option: ");
                        scanner.nextLine();
                    }
                }

                switch (option) {
                    case 1:
                        addBook(connection, scanner);
                        break;
                    case 2:
                        displayAvailableBooks(connection);
                        break;
                    case 3:
                        issueBook(connection, scanner);
                        break;
                    case 4:
                        returnBook(connection, scanner);
                        break;
                    case 5:
                        addMember(connection, scanner);
                        break;
                    case 6:
                        displayMembers(connection);
                        break;
                    case 7:
                        updateUserDetail(connection, scanner);
                        break;
                    case 8:
                        searchBooks(connection,scanner);
                        break;
                    case 9:
                        deleteBook(connection,scanner);
                        break;
                    case 10:
                        deleteUser(connection, scanner);
                        break;
                    case 11:
                        System.out.println("Exiting the system. Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
        }
    }

}
