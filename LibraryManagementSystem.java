import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class LibraryManagementSystem {

    private static final String URL =
            "jdbc:mysql://localhost:3306/library_db?useSSL=false&serverTimezone=UTC";

    private static final String USERNAME = "root";
    private static final String PASSWORD = "1234";

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.println("\n=================================");
            System.out.println("     LIBRARY MANAGEMENT SYSTEM");
            System.out.println("=================================");
            System.out.println("1. Add Book");
            System.out.println("2. View All Books");
            System.out.println("3. Search Book");
            System.out.println("4. Issue Book");
            System.out.println("5. Return Book");
            System.out.println("6. Delete Book");
            System.out.println("7. Exit");
            System.out.println("=================================");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {

                case 1:
                    addBook(scanner);
                    break;

                case 2:
                    viewBooks();
                    break;

                case 3:
                    searchBook(scanner);
                    break;

                case 4:
                    issueBook(scanner);
                    break;

                case 5:
                    returnBook(scanner);
                    break;

                case 6:
                    deleteBook(scanner);
                    break;

                case 7:
                    System.out.println("Thank you for using the Library Management System!");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Add a new book
    static void addBook(Scanner scanner) {

        System.out.print("Enter book title: ");
        String title = scanner.nextLine();

        System.out.print("Enter author name: ");
        String author = scanner.nextLine();

        String sql = "INSERT INTO books (title, author) VALUES (?, ?)";

        try (Connection connection =
                     DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, title);
            statement.setString(2, author);

            statement.executeUpdate();

            System.out.println("Book added successfully!");

        } catch (Exception e) {
            System.out.println("Error adding book.");
            e.printStackTrace();
        }
    }

    // View all books
    static void viewBooks() {

        String sql = "SELECT * FROM books";

        try (Connection connection =
                     DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            System.out.println("\n------------- ALL BOOKS -------------");

            boolean found = false;

            while (result.next()) {

                found = true;

                System.out.println(
                        "ID: " + result.getInt("id") +
                        " | Title: " + result.getString("title") +
                        " | Author: " + result.getString("author") +
                        " | Status: " + result.getString("status")
                );
            }

            if (!found) {
                System.out.println("No books found.");
            }

        } catch (Exception e) {
            System.out.println("Error viewing books.");
            e.printStackTrace();
        }
    }

    // Search for a book
    static void searchBook(Scanner scanner) {

        System.out.print("Enter book title to search: ");
        String title = scanner.nextLine();

        String sql =
                "SELECT * FROM books WHERE title LIKE ?";

        try (Connection connection =
                     DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, "%" + title + "%");

            ResultSet result = statement.executeQuery();

            boolean found = false;

            System.out.println("\n------------- SEARCH RESULTS -------------");

            while (result.next()) {

                found = true;

                System.out.println(
                        "ID: " + result.getInt("id") +
                        " | Title: " + result.getString("title") +
                        " | Author: " + result.getString("author") +
                        " | Status: " + result.getString("status")
                );
            }

            if (!found) {
                System.out.println("No matching book found.");
            }

            result.close();

        } catch (Exception e) {
            System.out.println("Error searching for book.");
            e.printStackTrace();
        }
    }

    // Issue a book
    static void issueBook(Scanner scanner) {

        System.out.print("Enter book ID to issue: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        String checkSql =
                "SELECT status FROM books WHERE id = ?";

        String updateSql =
                "UPDATE books SET status = 'Issued' WHERE id = ?";

        try (Connection connection =
                     DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement checkStatement =
                     connection.prepareStatement(checkSql)) {

            checkStatement.setInt(1, id);

            ResultSet result = checkStatement.executeQuery();

            if (!result.next()) {
                System.out.println("Book not found.");
                result.close();
                return;
            }

            String status = result.getString("status");
            result.close();

            if (status.equalsIgnoreCase("Issued")) {
                System.out.println("This book is already issued.");
                return;
            }

            try (PreparedStatement updateStatement =
                         connection.prepareStatement(updateSql)) {

                updateStatement.setInt(1, id);
                updateStatement.executeUpdate();

                System.out.println("Book issued successfully!");
            }

        } catch (Exception e) {
            System.out.println("Error issuing book.");
            e.printStackTrace();
        }
    }

    // Return a book
    static void returnBook(Scanner scanner) {

        System.out.print("Enter book ID to return: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        String checkSql =
                "SELECT status FROM books WHERE id = ?";

        String updateSql =
                "UPDATE books SET status = 'Available' WHERE id = ?";

        try (Connection connection =
                     DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement checkStatement =
                     connection.prepareStatement(checkSql)) {

            checkStatement.setInt(1, id);

            ResultSet result = checkStatement.executeQuery();

            if (!result.next()) {
                System.out.println("Book not found.");
                result.close();
                return;
            }

            String status = result.getString("status");
            result.close();

            if (status.equalsIgnoreCase("Available")) {
                System.out.println("This book is already available.");
                return;
            }

            try (PreparedStatement updateStatement =
                         connection.prepareStatement(updateSql)) {

                updateStatement.setInt(1, id);
                updateStatement.executeUpdate();

                System.out.println("Book returned successfully!");
            }

        } catch (Exception e) {
            System.out.println("Error returning book.");
            e.printStackTrace();
        }
    }

    // Delete a book
    static void deleteBook(Scanner scanner) {

        System.out.print("Enter book ID to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        String sql = "DELETE FROM books WHERE id = ?";

        try (Connection connection =
                     DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            int rows = statement.executeUpdate();

            if (rows > 0) {
                System.out.println("Book deleted successfully!");
            } else {
                System.out.println("Book not found.");
            }

        } catch (Exception e) {
            System.out.println("Error deleting book.");
            e.printStackTrace();
        }
    }
}