package org.example.service;

import org.example.model.User;
import org.example.repository.FileDataManager;
import org.example.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
/**
 * Сервис для управления пользователями и аутентификации
 */
public class UserService {
    private UserRepository userRepository;
    private FileDataManager fileDataManager;
    private User currentUser;

    public UserService(UserRepository userRepository, FileDataManager fileDataManager) {
        this.userRepository = userRepository;
        this.fileDataManager = fileDataManager;
        loadUsersFromFile();
    }

    public boolean login(String username, String password) {
        boolean authenticated = userRepository.authenticate(username, password);
        if (authenticated) {
            currentUser = userRepository.getUserByUsername(username);
        }
        return authenticated;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean hasPermission(User.Role requiredRole) {
        if (currentUser == null) return false;

        // Иерархия прав: ADMIN > MANAGER > VIEWER
        switch (requiredRole) {
            case ADMIN:
                return currentUser.getRole() == User.Role.ADMIN;
            case MANAGER:
                return currentUser.getRole() == User.Role.ADMIN ||
                        currentUser.getRole() == User.Role.MANAGER;
            case VIEWER:
                return true;
            default:
                return false;
        }
    }

    /**
     * Добавление нового пользователя (только для администраторов)
     */
    public boolean addUser(String username, String password, User.Role role) {
        if (!hasPermission(User.Role.ADMIN)) {
            return false;
        }

        User newUser = new User(username, password, role);
        boolean success = userRepository.addUser(newUser);
        if (success) {
            // Асинхронное сохранение в файл
            CompletableFuture.runAsync(this::saveUsersToFile);
        }
        return success;
    }

    /**
     * Обновление пользователя (только для администраторов)
     */
    public boolean updateUser(String username, String newPassword, User.Role newRole) {
        if (!hasPermission(User.Role.ADMIN)) {
            return false;
        }

        User existingUser = userRepository.getUserByUsername(username);
        if (existingUser == null) {
            return false;
        }

        if (newPassword != null && !newPassword.trim().isEmpty()) {
            existingUser.setPassword(newPassword);
        }

        if (newRole != null) {
            existingUser.setRole(newRole);
        }

        boolean success = userRepository.updateUser(existingUser);
        if (success) {
            CompletableFuture.runAsync(this::saveUsersToFile);
        }
        return success;
    }

    /**
     * Удаление пользователя (только для администраторов)
     */
    public boolean deleteUser(String username) {
        if (!hasPermission(User.Role.ADMIN)) {
            return false;
        }

        // Нельзя удалить текущего пользователя
        if (currentUser.getUsername().equals(username)) {
            return false;
        }

        boolean success = userRepository.deleteUser(username);
        if (success) {
            CompletableFuture.runAsync(this::saveUsersToFile);
        }
        return success;
    }

    /**
     * Получение списка всех пользователей (только для администраторов)
     */
    public List<User> getAllUsers() {
        if (!hasPermission(User.Role.ADMIN)) {
            return new ArrayList<>();
        }
        return userRepository.getAllUsers();
    }

    /**
     * Проверка существования пользователя
     */
    public boolean userExists(String username) {
        return userRepository.userExists(username);
    }

    /**
     * Загрузка пользователей из файла
     */
    private void loadUsersFromFile() {
        List<User> users = fileDataManager.loadUsers();
        if (!users.isEmpty()) {
            // Очищаем стандартных пользователей и загружаем из файла
            userRepository = new UserRepository();
            for (User user : users) {
                userRepository.addUser(user);
            }
        }
    }

    /**
     * Сохранение пользователей в файл
     */
    private void saveUsersToFile() {
        fileDataManager.saveUsers(userRepository.getAllUsers());
    }
}
