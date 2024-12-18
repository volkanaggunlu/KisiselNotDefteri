package Model;

public class UserObserver implements UserRoleObserver {
    private String observerName;

    public UserObserver(String name) {
        this.observerName = name;
    }

    @Override
    public void update(String role) {
        System.out.println(observerName + " kullanıcısının yeni rolü: " + role);
    }
}