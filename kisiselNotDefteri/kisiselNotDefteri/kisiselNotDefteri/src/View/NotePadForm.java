package View;

import Model.*;
import Controller.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class NotePadForm extends JFrame {
    private JTextArea noteArea; // Not alanı
    private JTextArea inputArea; // Kullanıcıdan gelen yeni metin için
    private int userId; // Kullanıcı ID'si
    private final String noteTitle; // Not başlığı
    private DatabasePlatform databasePlatform = MySQLConnection.getInstance();
    private DatabaseConnector databaseConnector = new RelationalDatabaseConnector(databasePlatform);

    private NoteState currentState; // State deseni için
    private UserRoleManager roleManager; // Observer deseni için

    public NotePadForm(int userId, String noteTitle) {
        this.userId = userId;
        this.noteTitle = noteTitle;
        this.currentState = new EmptyState(); // Varsayılan durum
        this.roleManager = new UserRoleManager(); // Kullanıcı rol yöneticisi

        setTitle("Not Defteri - " + noteTitle); // Başlık
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Başlık kısmı (Title alanı, üstte olacak)
        JLabel titleLabel = new JLabel("Başlık: " + noteTitle, JLabel.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Önceki notların gösterileceği kısmı (Sadece okunabilir)
        noteArea = new JTextArea();
        noteArea.setEditable(false); // Bu alan sadece okunabilir olacak
        JScrollPane noteScrollPane = new JScrollPane(noteArea);
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(noteScrollPane, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        // Kullanıcıdan yeni içerik girişi için metin kutusu
        inputArea = new JTextArea();
        inputArea.setRows(3); // 3 satır yüksekliğinde
        JScrollPane inputScrollPane = new JScrollPane(inputArea);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);

        // Alt panel (Kaydet Butonu, Sil Butonu ve Çıkış Butonu)
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Butonlar sağda
        JButton saveButton = new JButton("Kaydet");
        JButton deleteLastLineButton = new JButton("Son Satırı Sil");
        JButton exitButton = new JButton("Çıkış"); // Çıkış butonu
        JButton deleteButton = new JButton("Not Defterini Sil");

        buttonPanel.add(saveButton);
        buttonPanel.add(deleteLastLineButton);
        buttonPanel.add(exitButton); // Çıkış butonu ekleniyor
        buttonPanel.add(deleteButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        bottomPanel.add(inputScrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Kaydet butonuna basıldığında
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveNote(); // Notu kaydet
            }
        });

        // Son satırı sil butonuna basıldığında
        deleteLastLineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteLastLine(); // Son satırı sil
            }
        });

        // Sil butonuna basıldığında
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                        NotePadForm.this,
                        "Not defterini silmek istediğinize emin misiniz?",
                        "Onay",
                        JOptionPane.YES_NO_OPTION
                );
                if (choice == JOptionPane.YES_OPTION) {
                    deleteNote(); // Not defterini sil
                }
            }
        });

        // Çıkış butonuna basıldığında
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Mevcut pencereyi kapat
                new LoginFrame(); // LoginFrame formunu aç
            }
        });

        // Enter tuşuna basıldığında "Kaydet" butonunu tetikle
        inputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    saveNote(); // Enter tuşuna basıldığında kaydet
                }
            }
        });

        // Daha önce yazılmış notları yükle
        loadNotes();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Son satırı sil
    private void deleteLastLine() {
        String content = noteArea.getText();
        if (!content.trim().isEmpty()) {
            // Son satırı sil
            String[] lines = content.split("\n");
            StringBuilder updatedContent = new StringBuilder();
            for (int i = 0; i < lines.length - 1; i++) {
                updatedContent.append(lines[i]).append("\n");
            }

            // Veritabanından da sil
            String lastLine = lines[lines.length - 1];
            String query = "DELETE FROM notes WHERE user_id = ? AND content = ?";
            try (Connection connection = databasePlatform.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, userId);
                preparedStatement.setString(2, lastLine.trim());
                preparedStatement.executeUpdate();
                noteArea.setText(updatedContent.toString()); // Alanı güncelle
                JOptionPane.showMessageDialog(this, "Son satır silindi!");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Son satır silinirken hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Silinecek bir satır yok.", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Daha önceki notları yükle
    private void loadNotes() {
        String query = "SELECT content FROM notes WHERE user_id = ?";
        try (Connection connection = databasePlatform.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                StringBuilder allContent = new StringBuilder();
                while (resultSet.next()) {
                    String content = resultSet.getString("content");
                    if (content != null && !content.trim().isEmpty()) {
                        allContent.append(content).append("\n");
                    }
                }
                noteArea.setText(allContent.toString()); // Tüm içerikleri yükle
                updateState(); // State güncelle
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Notu kaydet
    private void saveNote() {
        String content = inputArea.getText();
        if (!content.trim().isEmpty()) {
            String query = "INSERT INTO notes (user_id, title, content) VALUES (?, ?, ?)";
            try (Connection connection = databasePlatform.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, userId);
                preparedStatement.setString(2, noteTitle); // Başlık doğru kullanılıyor
                preparedStatement.setString(3, content);
                preparedStatement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Not kaydedildi!");

                // Kaydettikten sonra, önceki içeriklere ekle
                noteArea.append(content + "\n");

                // TextArea'yı temizle
                inputArea.setText("");

                updateState(); // State güncelle
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Not kaydedilirken hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Not içeriği boş olamaz.", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Not defterini sil
    private void deleteNote() {
        String query = "DELETE FROM notes WHERE user_id = ?";
        try (Connection connection = databasePlatform.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Not defteri silindi.");
            noteArea.setText(""); // Alanı temizle
            updateState(); // State güncelle
            dispose();
            new LoginFrame();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Not defteri silinirken hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    // State güncelle
    private void updateState() {
        String content = noteArea.getText();
        if (content.trim().isEmpty()) {
            currentState = new EmptyState();
        } else {
            currentState = new FilledState();
        }
    }
}