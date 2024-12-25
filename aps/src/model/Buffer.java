package model;

import java.util.LinkedList;

/**
 * Класс для управления буфером заказов.
 * Реализует стратегию LIFO и обрабатывает переполнение буфера.
 */
public class Buffer {
    private final int capacity; // Максимальное количество заказов в буфере
    private final LinkedList<Order> orders;

    /**
     * Создает буфер с заданной емкостью.
     *
     * @param capacity максимальное количество заказов в буфере.
     */
    public Buffer(int capacity) {
        this.capacity = capacity;
        this.orders = new LinkedList<>();
    }

    public boolean isFull() {
        return orders.size() >= capacity;
    }

    public boolean isEmpty() {
        return orders.isEmpty();
    }

    /**
     * Добавляет заказ в буфер.
     *
     * @param order заказ для добавления.
     */
    public void addOrder(Order order) {
        // Добавление в начало очереди (LIFO)
        orders.addFirst(order);
    }

    public Order getNextOrder() {
        return !isEmpty() ? orders.removeFirst() : null;
    }

    /**
     * Удаляет самый старый заказ при переполнении буфера.
     */
    public void rejectOldestOrder() {
        if (!isEmpty()) {
            Order oldestOrder = orders.removeLast(); // Удалить самый старый заказ
            oldestOrder.rejectOrder();
            System.out.println("Заказ " + oldestOrder.getId() + " отклонен (буфер заполнен)");
        }
    }

    public void info() {
        System.out.println("Размер буфера: " + orders.size() + "/" + capacity);
        System.out.println("+-----------+-----------+");
        System.out.println("| Id буфера | Состояние |");
        System.out.println("+-----------+-----------+");
        for (int i = 0; i < capacity; i++) {
            if (i < orders.size()) {
                System.out.printf("| %-9d | %-9s |\n", i + 1, "Заказ " + orders.get(i).getId());
            } else {
                System.out.printf("| %-9d | %-9s |\n", i + 1, "пуст");
            }
        }
        System.out.println("+-----------+-----------+");
    }
}
