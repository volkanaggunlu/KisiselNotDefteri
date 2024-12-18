package Controller;
import Model.*;

public class NotesFactory {
    public static Notes createNote(String noteType, String title) {
        switch (noteType) {
            case "Günlük":
                return new DailyNotes(title);
            case "Çalışma":
                return new WorkNotes(title);
            case "Finansal":
                return new FinancialNotes(title);
            default:
                throw new IllegalArgumentException("Geçersiz not türü!");
        }
    }
}
