package org.example.repository;

import org.example.model.Category;
import org.example.model.Product;
import org.example.model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Менеджер для сохранения и загрузки данных в файлы
 */
public class FileDataManager {
    private static final String PRODUCTS_FILE = "products.dat";
    private static final String USERS_FILE = "users.dat";
    private static final String CATEGORIES_FILE = "categories.dat";
    /**
     * Сохранение товаров в файл
     */
    public void saveProducts(List<Product> products) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PRODUCTS_FILE))) {
            oos.writeObject(products);
            System.out.println("Товары успешно сохранены в файл: " + PRODUCTS_FILE);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении товаров: " + e.getMessage());
        }
    }

    /**
     * Загрузка товаров из файла
     */
    @SuppressWarnings("unchecked")
    public List<Product> loadProducts() {
        File file = new File(PRODUCTS_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PRODUCTS_FILE))) {
            return (List<Product>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка при загрузке товаров: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    /**
     * Сохранение пользователей в файл
     */
    public void saveUsers(List<User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
            System.out.println("Пользователи успешно сохранены в файл: " + USERS_FILE);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении пользователей: " + e.getMessage());
        }
    }
    /**
     * Загрузка пользователей из файла
     */
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
    /**
     * Инициализация стандартных категорий
     */
    public List<Category> initializeCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("CAT1", "Электроника", "Электронные устройства и гаджеты"));
        categories.add(new Category("CAT2", "Одежда", "Одежда и аксессуары"));
        categories.add(new Category("CAT3", "Книги", "Книги и литература"));
        categories.add(new Category("CAT4", "Дом и сад", "Товары для дома и сада"));
        categories.add(new Category("CAT5", "Спорт", "Спортивные товары и инвентарь"));
        return categories;
    }
}
