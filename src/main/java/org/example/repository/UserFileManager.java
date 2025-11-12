package org.example.repository;

import org.example.model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация менеджера данных для пользователей с использованием сериализации
 */
public class UserFileManager implements UserDataManager{
    private static final String USERS_FILE = "users.dat";

    @Override
    public void saveUsers(List<User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
            System.out.println("Пользователи успешно сохранены в файл: " + USERS_FILE);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении пользователей: " + e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<User> loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USERS_FILE))) {
            return (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка при загрузке пользователей: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}

