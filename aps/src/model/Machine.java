package model;

/**
 * Класс для представления станка.
 * Управляет назначением и выполнением заказов, а также ведет статистику своей работы.
 */
public class Machine {
    private final int id;
    private boolean isBusy;
    private Order currentOrder = null;
    private double totalWorkTime = 0.0;
    private int orderAmount = 0;

    /**
     * Создает станок с заданным идентификатором.
     *
     * @param id идентификатор курьера.
     */
    public Machine(int id) {
        this.id = id;
        this.isBusy = false;
    }

    public double generateServiceTime() {
        double minServiceTime = 10.0;
        double maxServiceTime = 20.0;
//        double minServiceTime = 1.0;
//        double maxServiceTime = 9.0;
        return minServiceTime + (maxServiceTime - minServiceTime) * Math.random();
    }

    public int getId() {
        return id;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public boolean hasCompletedOrder(double currentTime) {
        if (currentOrder != null) {
            double completionTime = currentOrder.getDispatchTime() + currentOrder.getProductionTime();
            return currentTime >= completionTime;
        }
        return false;
    }

    /**
     * Назначает заказ станку.
     *
     * @param order       заказ для выполнения.
     * @param currentTime текущее время симуляции.
     */
    public void assignOrder(Order order, double currentTime) {
        orderAmount++;
        this.isBusy = true;
        this.currentOrder = order;
        order.setStartProductionTime(currentTime);
        order.setMachineId(this.id);
        double serviceTime = generateServiceTime();
        order.setProductionTime(serviceTime);
    }

    /**
     * Завершает выполнение текущего заказа.
     *
     * @return выполненный заказ.
     */
    public Order releaseOrder() {
        if (currentOrder != null) {
            totalWorkTime += currentOrder.getProductionTime();
            Order completedOrder = currentOrder;
            currentOrder = null;
            isBusy = false;
            return completedOrder;
        }
        return null;
    }

    public double getTotalWorkTime() {
        return totalWorkTime;
    }

    public int getOrderAmount() {
        return this.orderAmount;
    }

    public int getCurrentOrderId() {
        return (currentOrder != null ? currentOrder.getId() : 0);
    }

    public double getMachineLoadPercentage(double simulationTime) {
        return totalWorkTime == 0 ? 0 : (totalWorkTime / simulationTime) * 100;
    }

}