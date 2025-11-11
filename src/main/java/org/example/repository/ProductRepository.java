package org.example.repository;

import org.example.model.Category;
import org.example.model.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductRepository {
    private Map<String, Product> products;
    private Map<Category, List<Product>> productsByCategory;
    public ProductRepository() {
        this.products = new HashMap<>();
        this.productsByCategory = new HashMap<>();
    }
    /**
     * Добавление товара с организацией по категориям
     */
    public void addProduct(Product product) {
        products.put(product.getId(), product);

        // Организация по категориям для быстрого поиска
        Category category = product.getCategory();
        productsByCategory.computeIfAbsent(category, k -> new ArrayList<>()).add(product);
    }
    /**
     * Обновление товара с поддержанием целостности данных
     */
    public boolean updateProduct(Product product) {
        if (products.containsKey(product.getId())) {
            Product oldProduct = products.get(product.getId());
            Category oldCategory = oldProduct.getCategory();
            Category newCategory = product.getCategory();

            // Если категория изменилась, обновляем индексы
            if (!oldCategory.equals(newCategory)) {
                removeProductFromCategory(oldProduct, oldCategory);
                productsByCategory.computeIfAbsent(newCategory, k -> new ArrayList<>()).add(product);
            }

            products.put(product.getId(), product);
            return true;
        }
        return false;
    }
    /**
     * Удаление товара из всех индексов
     */
    public boolean deleteProduct(String productId) {
        Product product = products.get(productId);
        if (product != null) {
            products.remove(productId);
            removeProductFromCategory(product, product.getCategory());
            return true;
        }
        return false;
    }

    private void removeProductFromCategory(Product product, Category category) {
        List<Product> categoryProducts = productsByCategory.get(category);
        if (categoryProducts != null) {
            categoryProducts.remove(product);
            if (categoryProducts.isEmpty()) {
                productsByCategory.remove(category);
            }
        }
    }

    public Product getProductById(String id) {
        return products.get(id);
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    /**
     * Поиск товаров по категории (оптимизированный через предварительную индексацию)
     */
    public List<Product> getProductsByCategory(Category category) {
        return productsByCategory.getOrDefault(category, new ArrayList<>());
    }

    /**
     * Поиск товаров по бренду
     */
    public List<Product> getProductsByBrand(String brand) {
        return products.values().stream()
                .filter(p -> p.getBrand().equalsIgnoreCase(brand))
                .collect(Collectors.toList());
    }
    /**
     * Фильтрация товаров по диапазону цен
     */
    public List<Product> getProductsByPriceRange(double minPrice, double maxPrice) {
        return products.values().stream()
                .filter(p -> p.getPrice() >= minPrice && p.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    /**
     * Поиск товаров по имени (с поддержкой частичного совпадения)
     */
    public List<Product> searchProductsByName(String name) {
        String searchTerm = name.toLowerCase();
        return products.values().stream()
                .filter(p -> p.getName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

    /**
     * Комплексный поиск с несколькими критериями
     */
    public List<Product> searchProducts(String name, Category category,
                                        String brand, Double minPrice, Double maxPrice) {
        return products.values().stream()
                .filter(p -> name == null || p.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(p -> category == null || p.getCategory().equals(category))
                .filter(p -> brand == null || p.getBrand().equalsIgnoreCase(brand))
                .filter(p -> minPrice == null || p.getPrice() >= minPrice)
                .filter(p -> maxPrice == null || p.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }


}
