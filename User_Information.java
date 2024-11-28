package org.example;

import java.sql.*;
import java.util.Scanner;

public class User_Information {
    public static void addMember(Connection connection, Scanner scanner) {
        String getIdSql = "SELECT MAX(CAST(member_id AS UNSIGNED)) AS max_member_id FROM users";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(getIdSql)) {
            int nextMemberId = 1;
            if (resultSet.next()) {
                int maxMemberId = resultSet.getInt("max_member_id");
                nextMemberId = maxMemberId + 1;
            }
            System.out.println("Auto-generated Member ID: " + nextMemberId);
            System.out.print("Enter member name: ");
            String name = scanner.nextLine();
            System.out.print("Enter mobile number: ");
            String mobileNumber = scanner.nextLine();
            System.out.print("Enter email: ");
            String email = scanner.nextLine();
            System.out.print("Enter address: ");
            String address = scanner.nextLine();

            String sql = "INSERT INTO users (user_name, member_id, mobile_no, email, address) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, String.valueOf(nextMemberId)); // Auto-generated member ID
                preparedStatement.setString(3, mobileNumber);
                preparedStatement.setString(4, email);
                preparedStatement.setString(5, address);

                preparedStatement.executeUpdate();
                System.out.println("Member registered: " + name + " (ID: " + nextMemberId + ")");
            } catch (SQLException e) {
                System.out.println("Error registering member: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Error generating member ID: " + e.getMessage());
        }
    }


    public static void displayMembers(Connection connection) {
        String sql = "SELECT * FROM users";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            System.out.println("\nRegistered Members:");
            while (resultSet.next()) {
                System.out.println("User_ID: "+ resultSet.getInt(1) + "\nUser_Name: " +
                        resultSet.getString(2) + "\nMember_Id: " +
                        resultSet.getString(3)+"\nMobile_Number: "+
                        resultSet.getString(4)+"\nEmail: "+
                        resultSet.getString(5)+"\nAddress: " +
                        resultSet.getString(6));
                System.out.println("---------------------------------");
            }
            System.out.println("Members found Successfully");
        } catch (SQLException e) {
            System.out.println("Error fetching members: " + e.getMessage());
        }
    }

    public static void updateUserDetail(Connection conn, Scanner scanner) {
        // Prompt user for the username and new mobile number
        System.out.println("Enter the username whose contact detail is to be updated:");
        String user_name = scanner.nextLine();
        System.out.println("Enter the new mobile number:");
        String mobile_no = scanner.nextLine();

        // Validate the mobile number
        if (mobile_no.matches("\\d{10}")) { // Regular expression to check if it's exactly 10 digits
            try {
                // SQL query to update the mobile number for the given username
                String query = "UPDATE library_Management.users SET mobile_no = ? WHERE user_name = ?";
                PreparedStatement ps = conn.prepareStatement(query);

                // Setting the parameters in the prepared statement
                ps.setString(1, mobile_no);
                ps.setString(2, user_name);

                // Execute the update query and check the number of rows affected
                int rowsUpdated = ps.executeUpdate();

                // Provide feedback based on the result
                if (rowsUpdated > 0) {
                    System.out.println("Contact updated successfully for user: " + user_name +
                            "\nUpdated Contact number: " + mobile_no);
                } else {
                    System.out.println("No user found with the username: " + user_name);
                }
            } catch (SQLException e) {
                System.out.println("An error occurred while updating the contact details.");
                e.printStackTrace();
            }
        } else {
            System.out.println("Invalid mobile number. Please enter exactly 10 digits.");
        }
    }


    public static void deleteUser(Connection connection, Scanner scanner) {
        System.out.print("Enter the member name to remove: ");
        String memberName = scanner.nextLine();

        // First, find the member ID based on the name
        String findMemberIdSql = "SELECT user_id FROM users WHERE user_name = ?";
        try (PreparedStatement findStatement = connection.prepareStatement(findMemberIdSql)) {
            findStatement.setString(1, memberName);
            ResultSet resultSet = findStatement.executeQuery();

            if (resultSet.next()) {
                int memberId = resultSet.getInt("user_id");

                // Delete related loans for this member
                String deleteLoansSql = "DELETE FROM issue_return_books WHERE member_id = ?";
                try (PreparedStatement deleteLoansStatement = connection.prepareStatement(deleteLoansSql)) {
                    deleteLoansStatement.setInt(1, memberId);
                    deleteLoansStatement.executeUpdate();
                }

                // Now delete the member
                String deleteMemberSql = "DELETE FROM users WHERE user_id = ?";
                try (PreparedStatement deleteMemberStatement = connection.prepareStatement(deleteMemberSql)) {
                    deleteMemberStatement.setInt(1, memberId);
                    int rowsAffected = deleteMemberStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Member removed successfully: " + memberName);
                    } else {
                        System.out.println("No member found with the name: " + memberName);
                    }
                }
            } else {
                System.out.println("No member found with the name: " + memberName);
            }
        } catch (SQLException e) {
            System.out.println("Error removing member: " + e.getMessage());
        }
    }


}
