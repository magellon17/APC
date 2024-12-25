package manager;

import model.Buffer;
import model.Order;
import generator.OrderGenerator;

public class OrderManager {
    private final Buffer buffer;
    private final OrderGenerator generator;

    public OrderManager(Buffer buffer, OrderGenerator generator) {
        this.buffer = buffer;
        this.generator = generator;
    }

    /**
     * Добавляет заказ в буфер. Если буфер переполнен, удаляет самый старый заказ.
     *
     * @param order заказ на добавление.
     */
    public void addOrderToBuffer(Order order) {
        if (buffer.isFull()) {
            buffer.rejectOldestOrder(); // Удалить самый старый заказ
            generator.incrementRejectedOrders(); // Увеличиваем счетчик отклоненных заказов
        }
        buffer.addOrder(order);
    }
}