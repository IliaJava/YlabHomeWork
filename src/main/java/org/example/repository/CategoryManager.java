package org.example.repository;

import org.example.model.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Реализация менеджера данных для категорий
 */
public class CategoryManager implements CategoryDataManager{
    @Override
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
