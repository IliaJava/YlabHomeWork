package org.example.service;


import org.example.model.Category;
import org.example.model.Product;
import org.example.repository.FileDataManager;
import org.example.repository.ProductDataManager;
import org.example.repository.ProductRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Сервис для управления товарами с кэшированием и метриками
 */
public class ProductService {
    private ProductRepository productRepository;
    private CacheService cacheService;
    private MetricsService metricsService;
    private ProductDataManager productDataManager;
    /**
     * Конструктор сервиса товаров
     * @param productRepository репозиторий товаров
     * @param cacheService сервис кэширования
     * @param metricsService сервис метрик
     * @param productDataManager менеджер данных товаров
     */

    public ProductService(ProductRepository productRepository, CacheService cacheService,
                          MetricsService metricsService, ProductDataManager productDataManager) {
        this.productRepository = productRepository;
        this.cacheService = cacheService;
        this.metricsService = metricsService;
        this.productDataManager = productDataManager;
        loadProductsFromFile();
    }

    /**
     * Добавление товара с кэшированием и метриками
     * @param product товар для добавления
     * @return true если добавление успешно, false в противном случае
     */

    public boolean addProduct(Product product) {
        long startTime = System.currentTimeMillis();

        try {
            productRepository.addProduct(product);

            cacheService.clear();

            CompletableFuture.runAsync(this::saveProductsToFile);

            metricsService.recordOperationTime("addProduct", System.currentTimeMillis() - startTime);
            metricsService.incrementOperationCount("addProduct");
            return true;
        } catch (Exception e) {
            metricsService.incrementErrorCount("addProduct");
            return false;
        }
    }
    /**
     * Обновление товара с инвалидацией кэша
     * @param product товар для обновления
     * @return true если обновление успешно, false в противном случае
     */
    public boolean updateProduct(Product product) {
        long startTime = System.currentTimeMillis();

        boolean success = productRepository.updateProduct(product);
        if (success) {
            cacheService.clear();
            CompletableFuture.runAsync(this::saveProductsToFile);
        }

        metricsService.recordOperationTime("updateProduct", System.currentTimeMillis() - startTime);
        metricsService.incrementOperationCount("updateProduct");
        return success;
    }
    /**
     * Удаление товара
     * @param productId идентификатор товара
     * @return true если удаление успешно, false в противном случае
     */
    public boolean deleteProduct(String productId) {
        long startTime = System.currentTimeMillis();

        boolean success = productRepository.deleteProduct(productId);
        if (success) {
            cacheService.clear();
            CompletableFuture.runAsync(this::saveProductsToFile);
        }

        metricsService.recordOperationTime("deleteProduct", System.currentTimeMillis() - startTime);
        metricsService.incrementOperationCount("deleteProduct");
        return success;
    }
    /**
     * Поиск товаров с кэшированием
     * @param name название товара (может быть null)
     * @param category категория товара (может быть null)
     * @param brand бренд товара (может быть null)
     * @param minPrice минимальная цена (может быть null)
     * @param maxPrice максимальная цена (может быть null)
     * @return список найденных товаров
     */
    public List<Product> searchProducts(String name, Category category, String brand,
                                        Double minPrice, Double maxPrice) {
        String cacheKey = generateCacheKey(name, category, brand, minPrice, maxPrice);

        // Проверка кэша
        List<Product> cachedResult = cacheService.get(cacheKey);
        if (cachedResult != null) {
            metricsService.incrementCacheHit();
            return cachedResult;
        }

        long startTime = System.currentTimeMillis();
        List<Product> result = productRepository.searchProducts(name, category, brand, minPrice, maxPrice);


        cacheService.put(cacheKey, result);

        metricsService.recordOperationTime("searchProducts", System.currentTimeMillis() - startTime);
        metricsService.incrementOperationCount("searchProducts");
        metricsService.incrementCacheMiss();

        return result;
    }
    /**
     * Генерация ключа для кэша на основе параметров поиска
     */
    private String generateCacheKey(String name, Category category, String brand,
                                    Double minPrice, Double maxPrice) {
        return String.format("search_%s_%s_%s_%s_%s",
                name,
                category != null ? category.getId() : "null",
                brand,
                minPrice,
                maxPrice);
    }

    public Product getProductById(String id) {
        String cacheKey = "product_" + id;
        Product cachedProduct = cacheService.get(cacheKey);

        if (cachedProduct != null) {
            metricsService.incrementCacheHit();
            return cachedProduct;
        }

        Product product = productRepository.getProductById(id);
        if (product != null) {
            cacheService.put(cacheKey, product);
        }

        metricsService.incrementCacheMiss();
        return product;
    }
    public List<Product> getAllProducts() {
        return productRepository.getAllProducts();
    }

    public List<Product> getProductsByCategory(Category category) {
        return productRepository.getProductsByCategory(category);
    }

    public int getTotalProductsCount() {
        return productRepository.getAllProducts().size();
    }

    private void loadProductsFromFile() {
        List<Product> products = productDataManager.loadProducts();
        for (Product product : products) {
            productRepository.addProduct(product);
        }
    }

    private void saveProductsToFile() {
        productDataManager.saveProducts(productRepository.getAllProducts());
    }
}
