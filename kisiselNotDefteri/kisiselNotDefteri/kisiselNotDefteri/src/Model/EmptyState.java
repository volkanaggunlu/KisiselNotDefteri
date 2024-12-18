package Model;

public class EmptyState implements NoteState{

    @Override
    public void handleState() {
        System.out.println("Not defteri şu anda boş.");
    }
}
