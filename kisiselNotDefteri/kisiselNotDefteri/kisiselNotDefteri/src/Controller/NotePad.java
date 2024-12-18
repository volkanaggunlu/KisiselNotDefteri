package Controller;
import Model.*;

public class NotePad {
    private NoteState currentState;

    public NotePad() {
        this.currentState = new EmptyState(); // Varsayılan durum
    }

    public void setState(NoteState state) {
        this.currentState = state;
        currentState.handleState();
    }

    public NoteState getState() {
        return currentState;
    }
}

