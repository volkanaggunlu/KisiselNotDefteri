package Model;

import View.NotePadForm;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class FinancialNotes implements Notes {
    private String title;

    public FinancialNotes(String title) {
        this.title = title;
    }

    @Override
    public void saveNote(int userId) {
        String query = "INSERT INTO notes (user_id, title, content) VALUES (?, ?, ?)";
        try (Connection connection = MySQLConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, "Finansal not oluşturuldu.");

            preparedStatement.executeUpdate();
            new NotePadForm(userId, title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}