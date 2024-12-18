package View;

import Controller.DatabaseConnector;
import Controller.RelationalDatabaseConnector;
import Model.DatabasePlatform;
import Model.*;
import Controller.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFrame extends JFrame {
    private int userAuthority;
    DatabasePlatform databasePlatform = MySQLConnection.getInstance();
    DatabaseConnector databaseConnector = new RelationalDatabaseConnector(databasePlatform);
    private final UserRoleManager userRoleManager = new UserRoleManager(); // UserRoleManager burada başlatılıyor

    public LoginFrame() {
        // Observer ekleniyor
        userRoleManager.setObserver(new UserObserver("LoginFrame Observer"));

        setTitle("Giriş Yap");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 2));

        // Bileşenler
        JLabel emailLabel = new JLabel("Kullanıcı Adı:");
        JTextField usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Şifre:");
        JPasswordField passwordField = new JPasswordField();

        JButton loginButton = new JButton("Giriş Yap");
        JButton registerButton = new JButton("Kaydol");
        JButton adminLoginButton = new JButton("Admin Girişi");

        // Kaydol butonuna basıldığında
        registerButton.addActionListener(e -> {
            new RegisterFrame(); // RegisterFrame penceresini aç
            dispose();
        });

        // Giriş yap butonuna basıldığında
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (authenticateUser(username, password)) {
                dispose(); // Login formunu kapat

                int userId = getUserId(username); // Kullanıcının ID'sini al
                if (!userHasNotes(userId)) {
                    new NoteSelectionFrame(userId); // Yeni not oluşturma ekranı
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Hoş geldiniz, not defterinize erişebilirsiniz!");
                    String userTitle = getNoteTitleByUserId(userId); // Veritabanından başlık getir
                    if (userTitle != null) {
                        new NotePadForm(userId, userTitle); // Not başlığı ile formu aç
                    } else {
                        JOptionPane.showMessageDialog(LoginFrame.this, "Başlık bulunamadı!", "Hata", JOptionPane.ERROR_MESSAGE);
                    }
                }

                userRoleManager.setRole("user"); // Kullanıcı rolünü "user" olarak ayarla
            } else {
                JOptionPane.showMessageDialog(LoginFrame.this, "Geçersiz kullanıcı adı veya şifre!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Admin Girişi Butonuna Basıldığında
        adminLoginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (authenticateAdmin(username, password)) {
                JOptionPane.showMessageDialog(LoginFrame.this, "Admin girişi başarılı!");
                new AdminPanelFrame(); // Admin Panelini açma
                dispose();

                userRoleManager.setRole("admin"); // Admin rolünü "admin" olarak ayarla
            } else {
                JOptionPane.showMessageDialog(LoginFrame.this, "Yetkisiz giriş veya hatalı bilgiler!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Bileşenleri ekle
        add(emailLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(new JLabel()); // Boşluk
        add(loginButton);
        add(new JLabel()); // Boşluk
        add(registerButton);
        add(new JLabel()); // Boşluk
        add(adminLoginButton); // Admin butonunu ekledik

        getRootPane().setDefaultButton(loginButton);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Kullanıcı doğrulama
    private boolean authenticateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection connection = databasePlatform.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    userAuthority = resultSet.getInt("authority"); // Yetki seviyesini al
                    return true;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    // Admin doğrulama
    private boolean authenticateAdmin(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ? AND authority = 1";
        try (Connection connection = databasePlatform.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    // Kullanıcı ID'si çekme
    private int getUserId(String username) {
        String query = "SELECT id FROM users WHERE username = ?";
        try (Connection connection = databasePlatform.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    // Kullanıcının notu olup olmadığını kontrol etme
    private boolean userHasNotes(int userId) {
        String query = "SELECT * FROM notes WHERE user_id = ?";
        try (Connection connection = databasePlatform.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    // Kullanıcının not başlığını veritabanından alır
    private String getNoteTitleByUserId(int userId) {
        String query = "SELECT title FROM notes WHERE user_id = ? LIMIT 1";
        try (Connection connection = databasePlatform.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("title");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}