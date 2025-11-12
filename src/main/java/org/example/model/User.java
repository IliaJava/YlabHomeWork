package org.example.model;


import java.io.Serializable;
import java.util.Objects;
/**
 * Модель пользователя системы
 *
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1l;
    private String username;
    private String password;
    private Role role;
    /**
     * Конструктор пользователя
     * @param username имя пользователя
     * @param password пароль
     * @param role роль пользователя
     */
    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        User user = (User) object;
        return Objects.equals(username, user.username) && Objects.equals(password, user.password) && role == user.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
