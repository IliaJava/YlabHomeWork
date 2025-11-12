package org.example.repository;

import org.example.model.Product;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация менеджера данных для товаров с использованием сериализации
 */
public class ProductFileManager implements ProductDataManager{
    private static final String PRODUCTS_FILE = "products.dat";

    @Override
    public void saveProducts(List<Product> products) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PRODUCTS_FILE))) {
            oos.writeObject(products);
            System.out.println("Товары успешно сохранены в файл: " + PRODUCTS_FILE);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении товаров: " + e.getMessage());
        }
    }

    @Override
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
}


