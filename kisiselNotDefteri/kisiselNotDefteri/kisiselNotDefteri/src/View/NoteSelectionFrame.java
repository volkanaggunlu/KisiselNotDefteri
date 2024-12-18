package View;

import Model.Notes;
import Controller.NotesFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NoteSelectionFrame extends JFrame {
    private int userId;

    public NoteSelectionFrame(int userId) {
        this.userId = userId;

        setTitle("Not Defteri Seçimi");
        setSize(400, 200);
        setLayout(new GridLayout(4, 1));

        JButton dailyButton = new JButton("Günlük Not Defteri");
        JButton workButton = new JButton("Çalışma Not Defteri");
        JButton financeButton = new JButton("Finansal Not Defteri");
        JButton exitButton = new JButton("Çıkış");

        dailyButton.addActionListener(new NoteButtonListener("Günlük"));
        workButton.addActionListener(new NoteButtonListener("Çalışma"));
        financeButton.addActionListener(new NoteButtonListener("Finansal"));

        exitButton.addActionListener(e -> {
            dispose(); // Mevcut formu kapatma
            new LoginFrame(); // LoginFrame ekranını açma
        });

        add(dailyButton);
        add(workButton);
        add(financeButton);
        add(exitButton);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private class NoteButtonListener implements ActionListener {
        private String noteType;

        public NoteButtonListener(String noteType) {
            this.noteType = noteType;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Notes note = NotesFactory.createNote(noteType, noteType + " Defteri");
            note.saveNote(userId);
            JOptionPane.showMessageDialog(NoteSelectionFrame.this, noteType + " Not Defteri oluşturuldu!");
            dispose(); // Bu formu kapat
        }
    }
}
