package model;

/**
 * Класс для представления заказа.
 * Хранит параметры заказа, такие как время создания, время доставки, статус и идентификатор курьера.
 */
public class Order {
    private final int id;
    private final double orderTime;
    private double dispatchTime;
    private double productionTime; // Время выполнения заказа
    private boolean rejected;
    private int machineId;

    /**
     * Создает заказ с заданным идентификатором и временем создания.
     *
     * @param id        уникальный идентификатор заказа.
     * @param orderTime время создания заказа.
     */
    public Order(int id, double orderTime) {
        this.id = id;
        this.orderTime = orderTime;
        this.rejected = false;
    }

    public int getId() {
        return id;
    }

    public double getDispatchTime() {
        return dispatchTime;
    }

    public void setStartProductionTime(double dispatchTime) {
        this.dispatchTime = dispatchTime;
    }

    public double getProductionTime() {
        return productionTime;
    }

    public void setProductionTime(double productionTime) {
        this.productionTime = productionTime;
    }

    public void setMachineId(int machineId) {
        this.machineId = machineId;
    }

    public void rejectOrder() {
        this.rejected = true;
    }

    public double getServiceTime() {
        return productionTime;
    }
}
