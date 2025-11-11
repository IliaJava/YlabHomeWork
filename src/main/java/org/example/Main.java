package org.example;

import org.example.model.Category;
import org.example.model.Product;
import org.example.model.User;
import org.example.repository.FileDataManager;
import org.example.repository.ProductRepository;
import org.example.repository.UserRepository;
import org.example.service.*;

import java.util.List;
import java.util.Scanner;

/**
 * Главный класс приложения - точка входа и управление консольным интерфейсом
 */
public class Main {
    private ProductService productService;
    private UserService userService;
    private AuditService auditService;
    private MetricsService metricsService;
    private List<Category> categories;
    private Scanner scanner;

    public static void main(String[] args) {
        Main app = new Main();
        app.initialize();
        app.run();
    }

    /**
     * Инициализация всех компонентов системы
     */
    private void initialize() {
        // Инициализация репозиториев
        ProductRepository productRepository = new ProductRepository();
        UserRepository userRepository = new UserRepository();
        FileDataManager fileDataManager = new FileDataManager();

        // Инициализация сервисов
        CacheService cacheService = new CacheService();
        metricsService = new MetricsService();
        auditService = new AuditService();

        productService = new ProductService(productRepository, cacheService,
                metricsService, fileDataManager);
        userService = new UserService(userRepository, fileDataManager);

        // Загрузка категорий
        categories = fileDataManager.initializeCategories();
        scanner = new Scanner(System.in);

        System.out.println("=== СЕРВИС КАТАЛОГА ТОВАРОВ ===");
    }

    /**
     * Главный цикл приложения
     */
    private void run() {
        while (true) {
            if (!userService.isLoggedIn()) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private void showLoginMenu() {
        System.out.println("\n=== АВТОРИЗАЦИЯ ===");
        System.out.print("Логин: ");
        String username = scanner.nextLine();
        System.out.print("Пароль: ");
        String password = scanner.nextLine();

        if (userService.login(username, password)) {
            auditService.logLogin(username);
            System.out.println("Успешный вход! Добро пожаловать, " + username);
        } else {
            System.out.println("Ошибка авторизации! Проверьте логин и пароль.");
        }
    }

    private void showMainMenu() {
        User currentUser = userService.getCurrentUser();
        System.out.printf("\n=== ГЛАВНОЕ МЕНЮ (Пользователь: %s, Роль: %s) ===\n",
                currentUser.getUsername(), currentUser.getRole());

        System.out.println("1. Просмотр всех товаров");
        System.out.println("2. Поиск товаров");
        System.out.println("3. Добавить товар");

        if (userService.hasPermission(User.Role.MANAGER)) {
            System.out.println("4. Редактировать товар");
            System.out.println("5. Удалить товар");
        }

        if (userService.hasPermission(User.Role.ADMIN)) {
            System.out.println("6. Управление пользователями");
            System.out.println("7. Показать метрики");
            System.out.println("8. Показать аудит");
        }

        System.out.println("0. Выход");
        System.out.print("Выберите действие: ");

        int choice = readIntInput();
        handleMainMenuChoice(choice);
    }

    private void handleMainMenuChoice(int choice) {
        switch (choice) {
            case 1:
                showAllProducts();
                break;
            case 2:
                searchProducts();
                break;
            case 3:
                if (userService.hasPermission(User.Role.MANAGER)) {
                    addProduct();
                } else {
                    System.out.println("Недостаточно прав для выполнения этой операции!");
                }
                break;
            case 4:
                if (userService.hasPermission(User.Role.MANAGER)) {
                    editProduct();
                } else {
                    System.out.println("Недостаточно прав для выполнения этой операции!");
                }
                break;
            case 5:
                if (userService.hasPermission(User.Role.MANAGER)) {
                    deleteProduct();
                } else {
                    System.out.println("Недостаточно прав для выполнения этой операции!");
                }
                break;
            case 6:
                if (userService.hasPermission(User.Role.ADMIN)) {
                    showUserManagementMenu();
                } else {
                    System.out.println("Недостаточно прав для выполнения этой операции!");
                }
                break;
            case 7:
                if (userService.hasPermission(User.Role.ADMIN)) {
                    metricsService.printMetrics();
                } else {
                    System.out.println("Недостаточно прав для выполнения этой операции!");
                }
                break;
            case 8:
                if (userService.hasPermission(User.Role.ADMIN)) {
                    auditService.printAuditLogs();
                } else {
                    System.out.println("Недостаточно прав для выполнения этой операции!");
                }
                break;
            case 0:
                logout();
                break;
            default:
                System.out.println("Неверный выбор!");
        }
    }

    /**
     * Меню управления пользователями (только для администраторов)
     */
    private void showUserManagementMenu() {
        while (true) {
            System.out.println("\n=== УПРАВЛЕНИЕ ПОЛЬЗОВАТЕЛЯМИ ===");
            System.out.println("1. Показать всех пользователей");
            System.out.println("2. Добавить пользователя");
            System.out.println("3. Изменить пользователя");
            System.out.println("4. Удалить пользователя");
            System.out.println("0. Назад в главное меню");
            System.out.print("Выберите действие: ");

            int choice = readIntInput();

            switch (choice) {
                case 1:
                    showAllUsers();
                    break;
                case 2:
                    addUser();
                    break;
                case 3:
                    editUser();
                    break;
                case 4:
                    deleteUser();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Неверный выбор!");
            }
        }
    }

    private void showAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("Пользователи не найдены.");
        } else {
            System.out.println("\n=== ВСЕ ПОЛЬЗОВАТЕЛИ ===");
            System.out.printf("%-15s %-10s\n", "Логин", "Роль");
            System.out.println("-------------------------------");
            for (User user : users) {
                System.out.printf("%-15s %-10s\n", user.getUsername(), user.getRole());
            }
        }
    }

    private void addUser() {
        System.out.println("\n=== ДОБАВЛЕНИЕ ПОЛЬЗОВАТЕЛЯ ===");

        System.out.print("Логин: ");
        String username = scanner.nextLine();

        if (userService.userExists(username)) {
            System.out.println("Пользователь с таким логином уже существует!");
            return;
        }

        System.out.print("Пароль: ");
        String password = scanner.nextLine();

        System.out.println("Выберите роль:");
        System.out.println("1. Администратор");
        System.out.println("2. Менеджер");
        System.out.println("3. Просмотр");
        System.out.print("Выберите роль (1-3): ");

        int roleChoice = readIntInput();
        User.Role role;

        switch (roleChoice) {
            case 1:
                role = User.Role.ADMIN;
                break;
            case 2:
                role = User.Role.MANAGER;
                break;
            case 3:
                role = User.Role.VIEWER;
                break;
            default:
                System.out.println("Неверный выбор роли!");
                return;
        }

        if (userService.addUser(username, password, role)) {
            auditService.logUserAction(userService.getCurrentUser().getUsername(),
                    "ADD_USER", username);
            System.out.println("Пользователь успешно добавлен!");
        } else {
            System.out.println("Ошибка при добавлении пользователя!");
        }
    }

    private void editUser() {
        System.out.println("\n=== ИЗМЕНЕНИЕ ПОЛЬЗОВАТЕЛЯ ===");

        System.out.print("Введите логин пользователя для изменения: ");
        String username = scanner.nextLine();

        if (!userService.userExists(username)) {
            System.out.println("Пользователь не найден!");
            return;
        }

        System.out.println("Оставьте поле пустым, чтобы не изменять значение.");

        System.out.print("Новый пароль: ");
        String newPassword = scanner.nextLine();
        newPassword = newPassword.isEmpty() ? null : newPassword;

        System.out.println("Новая роль:");
        System.out.println("1. Администратор");
        System.out.println("2. Менеджер");
        System.out.println("3. Просмотр");
        System.out.println("0. Не изменять роль");
        System.out.print("Выберите роль (0-3): ");

        int roleChoice = readIntInput();
        User.Role newRole = null;

        switch (roleChoice) {
            case 1:
                newRole = User.Role.ADMIN;
                break;
            case 2:
                newRole = User.Role.MANAGER;
                break;
            case 3:
                newRole = User.Role.VIEWER;
                break;
            case 0:
                break;
            default:
                System.out.println("Неверный выбор роли!");
                return;
        }

        if (userService.updateUser(username, newPassword, newRole)) {
            auditService.logUserAction(userService.getCurrentUser().getUsername(),
                    "UPDATE_USER", username);
            System.out.println("Пользователь успешно обновлен!");
        } else {
            System.out.println("Ошибка при обновлении пользователя!");
        }
    }

    private void deleteUser() {
        System.out.println("\n=== УДАЛЕНИЕ ПОЛЬЗОВАТЕЛЯ ===");

        System.out.print("Введите логин пользователя для удаления: ");
        String username = scanner.nextLine();

        if (!userService.userExists(username)) {
            System.out.println("Пользователь не найден!");
            return;
        }

        // Нельзя удалить самого себя
        if (userService.getCurrentUser().getUsername().equals(username)) {
            System.out.println("Нельзя удалить текущего пользователя!");
            return;
        }

        System.out.print("Вы уверены, что хотите удалить пользователя " + username + "? (y/n): ");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("y")) {
            if (userService.deleteUser(username)) {
                auditService.logUserAction(userService.getCurrentUser().getUsername(),
                        "DELETE_USER", username);
                System.out.println("Пользователь успешно удален!");
            } else {
                System.out.println("Ошибка при удалении пользователя!");
            }
        }
    }

    // Остальные методы остаются без изменений...
    private void showAllProducts() {
        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("Товары не найдены.");
        } else {
            System.out.println("\n=== ВСЕ ТОВАРЫ (" + products.size() + ") ===");
            products.forEach(System.out::println);
        }
    }

    private void searchProducts() {
        System.out.println("\n=== ПОИСК ТОВАРОВ ===");

        System.out.print("Название (оставьте пустым для пропуска): ");
        String name = scanner.nextLine().trim();
        name = name.isEmpty() ? null : name;

        System.out.println("Категории:");
        for (int i = 0; i < categories.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, categories.get(i).getName());
        }
        System.out.print("Выберите категорию (0 для пропуска): ");
        int categoryChoice = readIntInput();
        Category category = (categoryChoice > 0 && categoryChoice <= categories.size()) ?
                categories.get(categoryChoice - 1) : null;

        System.out.print("Бренд (оставьте пустым для пропуска): ");
        String brand = scanner.nextLine().trim();
        brand = brand.isEmpty() ? null : brand;

        System.out.print("Минимальная цена (оставьте пустым для пропуска): ");
        String minPriceStr = scanner.nextLine().trim();
        Double minPrice = minPriceStr.isEmpty() ? null : Double.parseDouble(minPriceStr);

        System.out.print("Максимальная цена (оставьте пустым для пропуска): ");
        String maxPriceStr = scanner.nextLine().trim();
        Double maxPrice = maxPriceStr.isEmpty() ? null : Double.parseDouble(maxPriceStr);

        List<Product> results = productService.searchProducts(name, category, brand, minPrice, maxPrice);

        System.out.println("\n=== РЕЗУЛЬТАТЫ ПОИСКА (" + results.size() + " товаров) ===");
        if (results.isEmpty()) {
            System.out.println("Товары не найдены.");
        } else {
            results.forEach(System.out::println);
        }
    }

    private void addProduct() {
        System.out.println("\n=== ДОБАВЛЕНИЕ ТОВАРА ===");

        System.out.print("ID товара: ");
        String id = scanner.nextLine();

        System.out.print("Название: ");
        String name = scanner.nextLine();

        System.out.print("Описание: ");
        String description = scanner.nextLine();

        System.out.print("Цена: ");
        double price = readDoubleInput();

        System.out.println("Выберите категорию:");
        for (int i = 0; i < categories.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, categories.get(i).getName());
        }
        int categoryChoice = readIntInput();
        if (categoryChoice < 1 || categoryChoice > categories.size()) {
            System.out.println("Неверный выбор категории!");
            return;
        }
        Category category = categories.get(categoryChoice - 1);

        System.out.print("Бренд: ");
        String brand = scanner.nextLine();

        System.out.print("Количество на складе: ");
        int stockQuantity = readIntInput();

        Product product = new Product(id, name, description, price, category, brand, stockQuantity);

        if (productService.addProduct(product)) {
            auditService.logProductAction(userService.getCurrentUser().getUsername(),
                    "ADD_PRODUCT", id);
            System.out.println("Товар успешно добавлен!");
        } else {
            System.out.println("Ошибка при добавлении товара!");
        }
    }

    private void editProduct() {
        System.out.println("\n=== РЕДАКТИРОВАНИЕ ТОВАРА ===");
        System.out.print("Введите ID товара для редактирования: ");
        String id = scanner.nextLine();

        Product product = productService.getProductById(id);
        if (product == null) {
            System.out.println("Товар не найден!");
            return;
        }

        System.out.println("Текущие данные: " + product);
        System.out.println("Оставьте поле пустым, чтобы не изменять значение.");

        System.out.print("Новое название: ");
        String name = scanner.nextLine();
        if (!name.isEmpty()) {
            product.setName(name);
        }

        System.out.print("Новое описание: ");
        String description = scanner.nextLine();
        if (!description.isEmpty()) {
            product.setDescription(description);
        }

        System.out.print("Новая цена: ");
        String priceStr = scanner.nextLine();
        if (!priceStr.isEmpty()) {
            product.setPrice(Double.parseDouble(priceStr));
        }

        System.out.print("Новый бренд: ");
        String brand = scanner.nextLine();
        if (!brand.isEmpty()) {
            product.setBrand(brand);
        }

        System.out.print("Новое количество на складе: ");
        String stockStr = scanner.nextLine();
        if (!stockStr.isEmpty()) {
            product.setStockQuantity(Integer.parseInt(stockStr));
        }

        if (productService.updateProduct(product)) {
            auditService.logProductAction(userService.getCurrentUser().getUsername(),
                    "UPDATE_PRODUCT", id);
            System.out.println("Товар успешно обновлен!");
        } else {
            System.out.println("Ошибка при обновлении товара!");
        }
    }

    private void deleteProduct() {
        System.out.println("\n=== УДАЛЕНИЕ ТОВАРА ===");
        System.out.print("Введите ID товара для удаления: ");
        String id = scanner.nextLine();

        Product product = productService.getProductById(id);
        if (product == null) {
            System.out.println("Товар не найден!");
            return;
        }

        System.out.println("Вы уверены, что хотите удалить товар: " + product.getName() + "? (y/n)");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("y")) {
            if (productService.deleteProduct(id)) {
                auditService.logProductAction(userService.getCurrentUser().getUsername(),
                        "DELETE_PRODUCT", id);
                System.out.println("Товар успешно удален!");
            } else {
                System.out.println("Ошибка при удалении товара!");
            }
        }
    }

    private void logout() {
        String username = userService.getCurrentUser().getUsername();
        userService.logout();
        auditService.logLogout(username);
        System.out.println("Выход выполнен успешно!");
    }

    private int readIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Неверный формат числа. Попробуйте снова: ");
            }
        }
    }

    private double readDoubleInput() {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Неверный формат числа. Попробуйте снова: ");
            }
        }
    }
}