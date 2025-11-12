package org.example.service;

import org.example.model.AuditLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для аудита действий пользователей
 */
public class AuditService {
    private List<AuditLog> auditLogs;

    public AuditService() {
        this.auditLogs = new ArrayList<>();
    }
    /**
     * Логирование действия пользователя
     * @param username имя пользователя
     * @param action выполненное действие
     * @param details детали действия
     */
    public void logAction(String username, String action, String details) {
        AuditLog log = new AuditLog(username, action, details);
        auditLogs.add(log);
        System.out.println(log); // Вывод в консоль для демонстрации
    }
    /**
     * Логирование входа пользователя
     * @param username имя пользователя
     */
    public void logLogin(String username) {
        logAction(username, "LOGIN", "Пользователь вошел в систему");
    }
    /**
     * Логирование выхода пользователя
     * @param username имя пользователя
     */
    public void logLogout(String username) {
        logAction(username, "LOGOUT", "Пользователь вышел из системы");
    }
    /**
     * Логирование действия с товаром
     * @param username имя пользователя
     * @param action действие с товаром
     * @param productId идентификатор товара
     */
    public void logProductAction(String username, String action, String productId) {
        logAction(username, action, "Товар ID: " + productId);
    }
    /**
     * Логирование действия с пользователем
     * @param username имя пользователя
     * @param action действие с пользователем
     * @param targetUser целевой пользователь
     */
    public void logUserAction(String username, String action, String targetUser) {
        logAction(username, action, "Пользователь: " + targetUser);
    }
    /**
     * Вывод всех записей аудита в консоль
     */
    public List<AuditLog> getAuditLogs() {
        return new ArrayList<>(auditLogs);
    }

    public void printAuditLogs() {
        System.out.println("\n=== АУДИТ ДЕЙСТВИЙ ===");
        if (auditLogs.isEmpty()) {
            System.out.println("Записей аудита нет.");
        } else {
            auditLogs.forEach(System.out::println);
        }
    }
}