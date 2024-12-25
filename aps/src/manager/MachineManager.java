package manager;

import model.Buffer;
import model.Machine;
import model.Order;

import java.util.List;

/**
 * Класс для управления станками.
 * Назначает заказы из буфера доступным станками.
 */
public class MachineManager {

    public final List<Machine> machines;

    /**
     * Конструктор создает менеджер для заданного списка курьеров.
     * @param machines список курьеров.
     */
    public MachineManager(List<Machine> machines){
        this.machines = machines;
    }

    /**
     * Выбираем заказ из буфера (последний добавленный, LIFO) и назначает его станку.
     * @param buffer буфер заказов.
     * @param currentTime текущее время симуляции.
     */
    public void assignOrderToMachine(Buffer buffer, double currentTime) {
        if (buffer.isEmpty()) {
            return;
        }

        Machine availableMachine = machines.stream()
                .filter(machine -> !machine.isBusy())
                .findFirst()
                .orElse(null); // доступный станок или null, если все заняты.

        if (availableMachine == null) {
            return;
        }
        Order order = buffer.getNextOrder();
        availableMachine.assignOrder(order, currentTime);
    }
}
