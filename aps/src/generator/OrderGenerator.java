package generator;

import model.Order;

import java.util.Random;

/**
 * Класс для генерации заказов.
 * Использует распределение Пуассона для планирования времени новых заказов.
 */
public class OrderGenerator {
    private static int orderId = 0;
    private final double lambda;
    private final int id;
    private final Random random;
    public int generatedItemsAmount = 0;
    private double nextOrderTime;
    private int totalRequests = 0;
    private int rejectedOrders = 0;
    private double totalWaitTime = 0.0;
    private double totalWaitTimeSquared = 0.0;
    private double totalSystemTime = 0.0; // Общее время в системе
    private double totalSystemTimeSquared = 0.0; // Сумма квадратов времени в системе

    /**
     * Создает генератор заказов с заданным параметром интенсивности.
     *
     * @param id     идентификатор генератора.
     * @param lambda интенсивность генерации заказов.
     */
    public OrderGenerator(int id, double lambda) {
        this.id = id;
        this.lambda = lambda;
        this.random = new Random();
    }

    /**
     * Генерирует новый заказ, если текущий момент времени позволяет.
     *
     * @param currentTime текущее время симуляции.
     * @return созданный заказ или null, если заказ не может быть сгенерирован.
     */
    public Order generateOrder(double currentTime) {
        totalRequests++;
        orderId++;
        generatedItemsAmount++;
        Order newOrder = new Order(orderId, currentTime);

        double waitTime = currentTime - nextOrderTime; // Время ожидания
        totalWaitTime += waitTime;
        totalWaitTimeSquared += waitTime * waitTime;

        double serviceTime = newOrder.getServiceTime(); // Время обработки заказа
        double systemTime = waitTime + serviceTime; // Время в системе
        totalSystemTime += systemTime;
        totalSystemTimeSquared += systemTime * systemTime;

        scheduleNextOrder(); // Запланировать следующий заказ
        return newOrder;
    }

    public double getNextOrderTime() {
        return nextOrderTime;
    }

    public void incrementRejectedOrders() {
        rejectedOrders++;
    }

    public int getId() {
        return this.id;
    }

    /**
     * Планирует время следующего заказа.
     */
    public void scheduleNextOrder() {
        nextOrderTime += -1.0 * Math.log(Math.random()) / lambda;
    }

    public int getRejectedOrders() {
        return rejectedOrders;
    }

    public int getTotalRequests() {
        return totalRequests;
    }

    public double getAverageWaitTime() {
        return generatedItemsAmount == 0 ? 0 : totalWaitTime / generatedItemsAmount;
    }

    public double getWaitTimeVariance() {
        if (generatedItemsAmount > 0) {
            double mean = getAverageWaitTime();
            return (totalWaitTimeSquared / generatedItemsAmount) - (mean * mean);
        }
        return 0.0;
    }

    public double getAverageSystemTime() {
        return generatedItemsAmount == 0 ? 0 : totalSystemTime / generatedItemsAmount;
    }

    public double getSystemTimeVariance() {
        if (generatedItemsAmount > 0) {
            double mean = getAverageSystemTime();
            return (totalSystemTimeSquared / generatedItemsAmount) - (mean * mean);
        }
        return 0.0;
    }

    public int getGeneratedOrders() {
        return generatedItemsAmount;
    }
}
