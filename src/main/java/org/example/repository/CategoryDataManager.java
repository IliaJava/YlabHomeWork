package org.example.repository;

import org.example.model.Category;

import java.util.List;

/**
 * Интерфейс для управления категориями
 */
public interface CategoryDataManager {
    List<Category> initializeCategories();

        /**
         * Инициализирует стандартные категории
         * @return список категорий
         */

}
