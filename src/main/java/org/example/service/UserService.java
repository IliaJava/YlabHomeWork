package org.example.service;

import org.example.model.Role;
import org.example.model.User;
import org.example.repository.UserDataManager;
import org.example.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
/**
 * Сервис для управления пользователями и аутентификации
 */
public class UserService {
    private UserRepository userRepository;
    private UserDataManager userDataManager;
    private User currentUser;

    /**
     * Конструктор сервиса пользователей
     * @param userRepository репозиторий пользователей
     * @param userDataManager менеджер данных пользователей
     */
    public UserService(UserRepository userRepository, UserDataManager userDataManager) {
        this.userRepository = userRepository;
        this.userDataManager = userDataManager;
        loadUsersFromFile();
    }
    /**
     * Аутентификация пользователя
     * @param username имя пользователя
     * @param password пароль
     * @return true если аутентификация успешна, false в противном случае
     */
    public boolean login(String username, String password) {
        boolean authenticated = userRepository.authenticate(username, password);
        if (authenticated) {
            currentUser = userRepository.getUserByUsername(username);
        }
        return authenticated;
    }
    /**
     * Выход пользователя из системы
     */
    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
    /**
     * Проверка прав доступа пользователя
     * @param requiredRole требуемая роль
     * @return true если пользователь имеет достаточные права, false в противном случае
     */
    public boolean hasPermission(Role requiredRole) {
        if (currentUser == null) return false;

        // Иерархия прав: ADMIN > MANAGER > VIEWER
        switch (requiredRole) {
            case ADMIN:
                return currentUser.getRole() == Role.ADMIN;
            case MANAGER:
                return currentUser.getRole() == Role.ADMIN ||
                        currentUser.getRole() == Role.MANAGER;
            case VIEWER:
                return true;
            default:
                return false;
        }
    }

    /**
     * Добавление нового пользователя (только для администраторов)
     * @param username имя пользователя
     * @param password пароль
     * @param role роль пользователя
     * @return true если пользователь добавлен, false в противном случае
     */
    public boolean addUser(String username, String password, Role role) {
        if (!hasPermission(Role.ADMIN)) {
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
     * @param username имя пользователя
     * @param newPassword новый пароль (может быть null)
     * @param newRole новая роль (может быть null)
     * @return true если пользователь обновлен, false в противном случае
     */
    public boolean updateUser(String username, String newPassword, Role newRole) {
        if (!hasPermission(Role.ADMIN)) {
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
     * @param username имя пользователя
     * @return true если пользователь удален, false в противном случае
     */
    public boolean deleteUser(String username) {
        if (!hasPermission(Role.ADMIN)) {
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
     * @return список пользователей или пустой список если недостаточно прав
     */
    public List<User> getAllUsers() {
        if (!hasPermission(Role.ADMIN)) {
            return new ArrayList<>();
        }
        return userRepository.getAllUsers();
    }

    /**
     * Проверка существования пользователя
     * @param username имя пользователя
     * @return true если пользователь существует, false в противном случае
     */
    public boolean userExists(String username) {
        return userRepository.userExists(username);
    }

    /**
     * Загрузка пользователей из файла
     */
    private void loadUsersFromFile() {
        List<User> users = userDataManager.loadUsers();
        if (!users.isEmpty()) {

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
        userDataManager.saveUsers(userRepository.getAllUsers());
    }
}
