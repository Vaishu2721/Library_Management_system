package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Books_Information {
    public static void addBook(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter book title: ");
            String title = scanner.nextLine();
            System.out.print("Enter author: ");
            String author = scanner.nextLine();
            System.out.print("Enter book category: ");
            String category = scanner.nextLine();
            System.out.print("Enter book edition: ");
            int edition = scanner.nextInt();
            System.out.print("Enter book availability (true/false): ");
            boolean available = scanner.nextBoolean();

            // Consume the leftover newline
            scanner.nextLine();

            System.out.print("Enter date (yyyy-MM-dd): ");
            String dateInput = scanner.nextLine();

            // Validate and parse the date
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate addedDate = LocalDate.parse(dateInput, formatter);

            if (addedDate.equals(LocalDate.now())) {
                String query = "INSERT INTO library_Management.books(book_tittle, author, category, edition, is_Available, added_date) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, title);
                preparedStatement.setString(2, author);
                preparedStatement.setString(3, category);
                preparedStatement.setInt(4, edition);
                preparedStatement.setBoolean(5, available);
                preparedStatement.setString(6, dateInput);
                preparedStatement.executeUpdate();
                System.out.println("Book added successfully!");
            } else {
                System.out.println("Date does not match today's date.");
            }
        } catch (DateTimeParseException e) {
            System.out.println("Invalid Date. Enter today's date in the format of yyyy-MM-dd.");
        } catch (Exception e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }

    public static void displayAvailableBooks(Connection conn) {
        try {
            String query = "SELECT * FROM library_Management.books where is_Available = true";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet data = ps.executeQuery();

            // Check if the ResultSet is empty
            if (!data.isBeforeFirst()) {
                System.out.println("No books are currently available.");
                return;
            }

            // Iterate through the ResultSet and print the data
            while (data.next()) {
                System.out.println("ID: " + data.getInt(1) + "\nBook_Name: " +
                        data.getString(2) + "\nAuthor:  " +
                        data.getString(3) + "\nCategory:  " +
                        data.getString(4) + "\nEdition:  " +
                        data.getInt(5) + "\nAvailability:  " +
                        data.getBoolean(6)+ "\nDate: " + data.getString(7));
                System.out.println("------------------------------------------------------------");
            }
            System.out.println("Books displayed successfully.");
        } catch (SQLException e) {
            System.out.println("Books not fetched.");
            e.printStackTrace();
        }
    }

    public static void deleteBook(Connection conn, Scanner scanner) {
        try {
            System.out.println("Enter the book title to be deleted:");
            String title = scanner.nextLine().trim();

            // Step 1: Get the book_id of the book to be deleted
            String getBookIdQuery = "SELECT book_id FROM books WHERE book_tittle = ?";
            PreparedStatement getBookIdPs = conn.prepareStatement(getBookIdQuery);
            getBookIdPs.setString(1, title);
            ResultSet rs = getBookIdPs.executeQuery();

            if (rs.next()) {
                int bookId = rs.getInt("book_id");

                // Step 2: Delete references from issue_return_books
                String deleteReferencesQuery = "DELETE FROM issue_return_books WHERE book_id = ?";
                PreparedStatement deleteReferencesPs = conn.prepareStatement(deleteReferencesQuery);
                deleteReferencesPs.setInt(1, bookId);
                deleteReferencesPs.executeUpdate();

                // Step 3: Delete the book from books
                String deleteBookQuery = "DELETE FROM books WHERE book_id = ?";
                PreparedStatement deleteBookPs = conn.prepareStatement(deleteBookQuery);
                deleteBookPs.setInt(1, bookId);
                int rowsDeleted = deleteBookPs.executeUpdate();

                // Feedback to user
                if (rowsDeleted > 0) {
                    System.out.println("Book deleted successfully.");
                } else {
                    System.out.println("No book found with the given title.");
                }
            } else {
                System.out.println("No book found with the given title.");
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while deleting the book.");
            e.printStackTrace();
        }
    }


    public static void searchBooks(Connection connection, Scanner scanner) {
        System.out.println("Search Books by:");
        System.out.println("1. Author");
        System.out.println("2. Category");
        System.out.println("3. View All Books");
        System.out.print("Choose an option: ");
        int option = scanner.nextInt();
        scanner.nextLine();

        switch (option) {
            case 1:
                System.out.print("Enter author name: ");
                String author = scanner.nextLine();
                searchBooksByAuthor(connection, author);
                break;
            case 2:
                System.out.print("Enter book category: ");
                String category = scanner.nextLine();
                searchBooksByCategory(connection, category);
                break;
            case 3:
                displayAvailableBooks(connection);
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    public static void searchBooksByAuthor(Connection connection, String author) {
        String authorInput = author.toLowerCase(); // Convert user input to lowercase
        String sql = "SELECT * FROM books WHERE LOWER(author) = ?"; // Exact case-insensitive match

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            // Set the parameter for the query
            statement.setString(1, authorInput);

            ResultSet resultSet = statement.executeQuery();

            // Check if there are any results
            if (!resultSet.isBeforeFirst()) {
                System.out.println("No books found by author: " + authorInput);
            } else {
                System.out.println("\nBooks by " + authorInput + ":");
                while (resultSet.next()) {
                    System.out.println("- " + resultSet.getString("book_tittle") + " [Category: " +
                            resultSet.getString("category") + "]");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching books by author: " + e.getMessage());
        }
    }

    public static void searchBooksByCategory(Connection connection, String category) {
        String categoryInput = category.toLowerCase(); // Convert user input to lowercase

        String sql = "SELECT * FROM books WHERE LOWER(category) = ?"; // Exact case-insensitive match

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            // Set the parameter for the query
            statement.setString(1, categoryInput);

            ResultSet resultSet = statement.executeQuery();

            // Check if there are any results
            if (!resultSet.isBeforeFirst()) {
                System.out.println("No books found in category: " + categoryInput);
            } else {
                System.out.println("\nBooks in category: " + categoryInput);
                while (resultSet.next()) {
                    System.out.println("- " + resultSet.getString("book_tittle") + " [Author: " +
                            resultSet.getString("author") + "]");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching books by category: " + e.getMessage());
        }
    }

    public static void issueBook(Connection connection, Scanner scanner) {
        System.out.print("Enter member ID: ");
        String memberId = scanner.nextLine();

        System.out.print("Enter book title: ");
        String bookTitle = scanner.nextLine();

        String book_data = "SELECT book_id FROM books WHERE book_tittle = ? AND is_Available = TRUE";
        String user_info = "SELECT user_id FROM users WHERE member_id = ?";

        try (PreparedStatement bookStatement = connection.prepareStatement(book_data);
             PreparedStatement memberStatement = connection.prepareStatement(user_info)) {

            bookStatement.setString(1, bookTitle);
            ResultSet bookResultSet = bookStatement.executeQuery();

            memberStatement.setString(1, memberId);
            ResultSet memberResultSet = memberStatement.executeQuery();

            if (bookResultSet.next() && memberResultSet.next()) {
                int bookId = bookResultSet.getInt("book_id");
                int memberID = memberResultSet.getInt("user_id");

                String borrowSql = "INSERT INTO issue_return_books (member_id, book_id, issued_date) VALUES (?, ?, NOW())";
                try (PreparedStatement borrowStatement = connection.prepareStatement(borrowSql)) {
                    borrowStatement.setInt(1, memberID); // Use the correct member internal id
                    borrowStatement.setInt(2, bookId);
                    borrowStatement.executeUpdate();

                    String updateBookSql = "UPDATE books SET is_Available = FALSE WHERE book_id = ?";
                    try (PreparedStatement updateBookStatement = connection.prepareStatement(updateBookSql)) {
                        updateBookStatement.setInt(1, bookId);
                        updateBookStatement.executeUpdate();
                    }

                    System.out.println("Book borrowed: " + bookTitle);
                    System.out.println("Book is issued Successfully");
                }
            } else {
                System.out.println("Book is not available or Member not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error borrowing book: " + e.getMessage());
        }
    }

    public static void returnBook(Connection connection, Scanner scanner) {
        System.out.print("Enter member ID: ");
        String memberId = scanner.nextLine();
        System.out.print("Enter book title: ");
        String bookTitle = scanner.nextLine();

        String book_data = "SELECT book_id FROM books WHERE book_tittle = ?";
        String user_info = "SELECT user_id FROM users WHERE member_id = ?";
        String issued_book = "SELECT id FROM issue_return_books WHERE member_id = ? AND book_id = ? AND return_date IS NULL";

        try (PreparedStatement bookStatement = connection.prepareStatement(book_data);
             PreparedStatement memberStatement = connection.prepareStatement(user_info)) {

            bookStatement.setString(1, bookTitle);
            ResultSet bookResultSet = bookStatement.executeQuery();

            memberStatement.setString(1, memberId);
            ResultSet memberResultSet = memberStatement.executeQuery();

            if (bookResultSet.next() && memberResultSet.next()) {
                int bookId = bookResultSet.getInt("book_id");
                int memberID = memberResultSet.getInt("user_id");

                try (PreparedStatement loanStatement = connection.prepareStatement(issued_book)) {
                    loanStatement.setInt(1, memberID);
                    loanStatement.setInt(2, bookId);
                    ResultSet loanResultSet = loanStatement.executeQuery();

                    if (loanResultSet.next()) {

                        String returnSql = "UPDATE issue_return_books SET return_date = NOW() WHERE id = ?";
                        try (PreparedStatement returnStatement = connection.prepareStatement(returnSql)) {
                            returnStatement.setInt(1, loanResultSet.getInt("id"));
                            returnStatement.executeUpdate();
                        }

                        String updateBookSql = "UPDATE books SET is_Available = TRUE WHERE book_id = ?";
                        try (PreparedStatement updateBookStatement = connection.prepareStatement(updateBookSql)) {
                            updateBookStatement.setInt(1, bookId);
                            updateBookStatement.executeUpdate();
                        }
                        System.out.println("Book returned: " + bookTitle);
                        System.out.println("Book Returned Successfully");
                    } else {
                        System.out.println("This book was not borrowed by this member.");
                    }
                }
            } else {
                System.out.println("Book or member not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error returning book: " + e.getMessage());
        }
    }
}

