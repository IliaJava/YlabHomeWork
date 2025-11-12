package org.example.model;

import java.io.Serializable;
/**
 * Категория товара для организации каталога
 */
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;
    public String id;
    public String name;
    public String description;
    /**
     * Конструктор категории
     * @param id уникальный идентификатор
     * @param name название категории
     * @param description описание категории
     */
    public Category(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Category{" +
                "name='" + name + '\'' +
                '}';
    }
}

