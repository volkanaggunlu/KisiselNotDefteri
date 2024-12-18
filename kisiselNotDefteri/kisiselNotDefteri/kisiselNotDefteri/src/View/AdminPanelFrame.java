package View;

import Model.DatabasePlatform;
import Model.MySQLConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

public class AdminPanelFrame extends JFrame {
    private DatabasePlatform databasePlatform = MySQLConnection.getInstance();

    public AdminPanelFrame() {
        setTitle("Admin Paneli");
        setSize(600, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Kullanıcıları gösteren tablo
        JTable usersTable = new JTable();
        loadUsersIntoTable(usersTable);

        JScrollPane tableScrollPane = new JScrollPane(usersTable);
        add(tableScrollPane, BorderLayout.CENTER);

        // Kullanıcı silme bileşenleri
        JPanel deletePanel = new JPanel(new FlowLayout());
        JLabel usernameLabel = new JLabel("Silinecek Kullanıcı:");
        JTextField usernameField = new JTextField(15);
        JButton deleteButton = new JButton("Kullanıcıyı Sil");
        JButton exitButton = new JButton("Çıkış");

        deletePanel.add(usernameLabel);
        deletePanel.add(usernameField);
        deletePanel.add(deleteButton);
        deletePanel.add(exitButton);

        add(deletePanel, BorderLayout.SOUTH);

        // Kullanıcı silme butonu
        deleteButton.addActionListener(e -> {
            String username = usernameField.getText();
            if (!username.isEmpty()) {
                deleteUser(username);
                loadUsersIntoTable(usersTable); // Tabloyu güncelle
            } else {
                JOptionPane.showMessageDialog(this, "Kullanıcı adı boş olamaz!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Çıkış butonu
        exitButton.addActionListener(e -> {
            dispose(); // Mevcut pencereyi kapatma
            new LoginFrame(); // LoginFrame ekranını açma
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Kullanıcıları tabloya yükleme
    private void loadUsersIntoTable(JTable table) {
        String query = "SELECT id, username, authority FROM users";
        try (Connection connection = databasePlatform.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            // Tablo modelini oluştur
            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.addColumn("ID");
            tableModel.addColumn("Kullanıcı Adı");
            tableModel.addColumn("Yetki Seviyesi");

            // Verileri tabloya ekle
            while (resultSet.next()) {
                Vector<Object> row = new Vector<>();
                row.add(resultSet.getInt("id"));
                row.add(resultSet.getString("username"));
                row.add(resultSet.getInt("authority"));
                tableModel.addRow(row);
            }

            table.setModel(tableModel);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Kullanıcı verileri yüklenirken hata oluştu!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Kullanıcıyı ve ilişkili notlarını silme
    private void deleteUser(String username) {
        String deleteNotesQuery = "DELETE FROM notes WHERE user_id = (SELECT id FROM users WHERE username = ?)";
        String deleteUserQuery = "DELETE FROM users WHERE username = ?";

        try (Connection connection = databasePlatform.getConnection();
             PreparedStatement deleteNotesStmt = connection.prepareStatement(deleteNotesQuery);
             PreparedStatement deleteUserStmt = connection.prepareStatement(deleteUserQuery)) {

            // İlişkili notları sil
            deleteNotesStmt.setString(1, username);
            deleteNotesStmt.executeUpdate();

            // Kullanıcıyı sil
            deleteUserStmt.setString(1, username);
            int rowsAffected = deleteUserStmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Kullanıcı başarıyla silindi!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Kullanıcı bulunamadı!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Kullanıcı silinirken hata oluştu!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
}