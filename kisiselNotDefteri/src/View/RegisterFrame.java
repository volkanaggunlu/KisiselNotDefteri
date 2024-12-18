package View;

import Model.DatabasePlatform;
import Model.MySQLConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class RegisterFrame extends JFrame {
    DatabasePlatform databasePlatform = MySQLConnection.getInstance();

    public RegisterFrame() {
        setTitle("Kayıt Ol");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 2));

        // Bileşenler
        JLabel usernameLabel = new JLabel("Kullanıcı Adı:");
        JTextField usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Şifre:");
        JPasswordField passwordField = new JPasswordField();

        JButton registerButton = new JButton("Kaydol");

        // "Kaydol" butonuna basıldığında
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (registerUser(username, password)) {
                    JOptionPane.showMessageDialog(RegisterFrame.this, "Sisteme başarıyla kaydoldunuz.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // RegisterFrame'i kapat
                    new LoginFrame(); // Login ekranına geri dön
                } else {
                    JOptionPane.showMessageDialog(RegisterFrame.this, "Kayıt işlemi başarısız oldu.", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Bileşenleri ekle
        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(new JLabel()); // Boşluk
        add(registerButton);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Kullanıcıyı veritabanına ekleme
    private boolean registerUser(String username, String password) {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection connection = databasePlatform.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0; // Ekleme başarılıysa true döner
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}