package Controller;

import Model.UserRoleObserver;

public class UserRoleManager {
    private String role; // Kullanıcı rolü: admin veya user
    private UserRoleObserver observer; // Tek bir observer tutuluyor

    // Rolü ayarla ve observer'ı bilgilendir
    public void setRole(String role) {
        this.role = role;
        notifyObserver();
    }

    // Geçerli rolü döndür
    public String getRole() {
        return role;
    }

    // Observer'ı ekle
    public void setObserver(UserRoleObserver observer) {
        this.observer = observer;
    }

    // Observer'ı bilgilendir
    private void notifyObserver() {
        if (observer != null) {
            observer.update(role);
        }
    }
}