package org.example.repository;

import org.example.model.Product;

import java.util.List;

/**
 * Интерфейс для управления операциями с товарами
 */
public interface ProductDataManager {
    /**
     * Сохраняет список товаров в хранилище
     * @param products список товаров для сохранения
     */
    void saveProducts(List<Product> products);

    /**
     * Загружает список товаров из хранилища
     * @return список товаров
     */
    List<Product> loadProducts();
}
