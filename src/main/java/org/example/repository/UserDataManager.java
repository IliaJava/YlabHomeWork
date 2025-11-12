package org.example.repository;

import org.example.model.User;

import java.util.List;

/**
 * Интерфейс для управления операциями с пользователями
 */
public interface UserDataManager {
    /**
     * Сохраняет список пользователей в хранилище
     * @param users список пользователей для сохранения
     */
    void saveUsers(List<User> users);

    /**
     * Загружает список пользователей из хранилища
     * @return список пользователей
     */
    List<User> loadUsers();
}

