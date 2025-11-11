package org.example.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


    /**
     * Сервис для сбора метрик производительности
     */
    public class MetricsService {
        private Map<String, AtomicLong> operationCounts;
        private Map<String, AtomicLong> operationTimes;
        private AtomicLong cacheHits;
        private AtomicLong cacheMisses;
        private AtomicLong totalErrors;

        public MetricsService() {
            this.operationCounts = new ConcurrentHashMap<>();
            this.operationTimes = new ConcurrentHashMap<>();
            this.cacheHits = new AtomicLong(0);
            this.cacheMisses = new AtomicLong(0);
            this.totalErrors = new AtomicLong(0);
        }
        public void incrementOperationCount(String operation) {
            operationCounts.computeIfAbsent(operation, k -> new AtomicLong(0)).incrementAndGet();
        }

        public void recordOperationTime(String operation, long time) {
            operationTimes.computeIfAbsent(operation, k -> new AtomicLong(0)).addAndGet(time);
        }

        public void incrementCacheHit() {
            cacheHits.incrementAndGet();
        }

        public void incrementCacheMiss() {
            cacheMisses.incrementAndGet();
        }

        public void incrementErrorCount(String operation) {
            totalErrors.incrementAndGet();
        }

        public void printMetrics() {
            System.out.println("\n=== МЕТРИКИ СИСТЕМЫ ===");
            System.out.printf("Попадания в кэш: %d\n", cacheHits.get());
            System.out.printf("Промахи кэша: %d\n", cacheMisses.get());
            System.out.printf("Общее количество ошибок: %d\n", totalErrors.get());

            double hitRatio = cacheHits.get() + cacheMisses.get() > 0 ?
                    (double) cacheHits.get() / (cacheHits.get() + cacheMisses.get()) : 0;
            System.out.printf("Эффективность кэша: %.2f%%\n", hitRatio * 100);

            System.out.println("\nСтатистика операций:");
            for (Map.Entry<String, AtomicLong> entry : operationCounts.entrySet()) {
                String operation = entry.getKey();
                long count = entry.getValue().get();
                long totalTime = operationTimes.getOrDefault(operation, new AtomicLong(0)).get();
                double avgTime = count > 0 ? (double) totalTime / count : 0;

                System.out.printf("- %s: выполнено %d раз, среднее время: %.2f мс\n",
                        operation, count, avgTime);
            }
        }
}
