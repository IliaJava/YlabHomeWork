package org.example.service;

import java.util.LinkedHashMap;
import java.util.Map;

public class CacheService {
/**
 * Сервис кэширования для ускорения повторных запросов
 * Использует LRU (Least Recently Used) стратегию
 */
private final int MAX_CACHE_SIZE = 100;
    private Map<String, CacheEntry> cache;

    private static class CacheEntry {
Object data;
    long timestamp;
        /**
         * Конструктор сервиса кэширования
         */
    CacheEntry(Object data) {
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
}

public CacheService() {
    //инициализация кеша при получении элемента переходит в конец спика
    this.cache = new LinkedHashMap<String, CacheEntry>(MAX_CACHE_SIZE, 0.75f, true) {
        @Override
                //Удаление самогом старого элемента в кеше
        protected boolean removeEldestEntry(Map.Entry<String, CacheEntry> eldest) {
            return size() > MAX_CACHE_SIZE;
        }
    };
}
    /**
     * Добавление данных в кэш
     * @param key ключ для кэширования
     * @param data данные для кэширования
     */
    public void put(String key, Object data) {
        cache.put(key, new CacheEntry(data));
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        CacheEntry entry = cache.get(key);
        if (entry != null) {
            return (T) entry.data;
        }
        return null;
    }

    /**
     * Удаление данных из кэша
     * @param key ключ для удаления
     */
    public void remove(String key) {
        cache.remove(key);
    }
    /**
     * Очистка кэша при изменении данных
     */
    public void clear() {
        cache.clear();
    }

    /**
     * Получение статистики кэша
     * @return текущий размер кэша
     */
    public int getCacheSize() {
        return cache.size();
    }

}
