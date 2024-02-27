import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DeadlineDAO {
    private Connection connection;
    private List<Deadline> deadlines;

    public DeadlineDAO() {
        try {
            connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addDeadline(Deadline deadline) {
        String query = "INSERT INTO deadlinetable (title, description, due_date, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, deadline.getTitle());
            preparedStatement.setString(2, deadline.getDescription());
            preparedStatement.setDate(3, Date.valueOf(deadline.getDueDate()));
            preparedStatement.setString(4, deadline.getStatus());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Deadline> getAllDeadlines() {
        List<Deadline> deadlines = new ArrayList<>();
        String query = "SELECT * FROM deadlinetable";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                LocalDate dueDate = resultSet.getDate("due_date").toLocalDate();
                String status = resultSet.getString("status");
                Deadline deadline = new Deadline(title, description, dueDate, status);
                deadlines.add(deadline);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deadlines;
    }

    public List<Deadline> getDeadlinesByDate(LocalDate date) {
        List<Deadline> deadlines = new ArrayList<>();
        String query = "SELECT * FROM deadlinetable WHERE due_date = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setDate(1, Date.valueOf(date));
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String title = resultSet.getString("title");
                    String description = resultSet.getString("description");
                    LocalDate dueDate = resultSet.getDate("due_date").toLocalDate();
                    String status = resultSet.getString("status");
                    Deadline deadline = new Deadline(title, description, dueDate, status);
                    deadlines.add(deadline);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deadlines;
    }

    public boolean deleteDeadline(Deadline deadlineToDelete) {
        String query = "DELETE FROM deadlinetable WHERE title = ? AND description = ? AND due_date = ? AND status = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, deadlineToDelete.getTitle());
            preparedStatement.setString(2, deadlineToDelete.getDescription());
            preparedStatement.setDate(3, Date.valueOf(deadlineToDelete.getDueDate()));
            preparedStatement.setString(4, deadlineToDelete.getStatus());
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0; // Return true if rows were affected (deleted), false otherwise
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if an exception occurred
        }
    }

    public void updateDeadline(Deadline deadline) {
        String query = "UPDATE deadlinetable SET description=?, due_date=?, status=? WHERE title=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, deadline.getDescription());
            preparedStatement.setDate(2, java.sql.Date.valueOf(deadline.getDueDate()));
            preparedStatement.setString(3, deadline.getStatus());
            preparedStatement.setString(4, deadline.getTitle()); // Using title for identification
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Deadline> getDeadlinesByCategory(String category) {
        List<Deadline> deadlines = new ArrayList<>();
        String query = "SELECT * FROM deadlinetable WHERE category = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, category);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                LocalDate dueDate = resultSet.getDate("due_date").toLocalDate();
                String status = resultSet.getString("status");
                Deadline deadline = new Deadline(title, description, dueDate, status);
                deadlines.add(deadline);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deadlines;
    }

    public List<Deadline> getDeadlinesByStatus(String status) {
        List<Deadline> deadlinesByStatus = new ArrayList<>();
        for (Deadline deadline : deadlines) {
            if (deadline.getStatus().equalsIgnoreCase(status)) {
                deadlinesByStatus.add(deadline);
            }
        }
        return deadlinesByStatus;
    }

}
