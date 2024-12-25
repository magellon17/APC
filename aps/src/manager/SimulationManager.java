package manager;

import generator.OrderGenerator;
import model.Buffer;
import model.Machine;
import model.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Класс для управления симуляцией системы доставки.
 * Отвечает за инициализацию компонентов, управление генерацией заказов, назначением станков и сбором статистики.
 */
public class SimulationManager {
    private final List<Machine> machines;
    private final Buffer buffer;
    private final List<OrderGenerator> generators;
    private final List<OrderManager> orderManagers;
    private final MachineManager machineManager;
    private final List<Order> orders;
    private double currentTime;

    /**
     * Конструктор инициализирует станки, буфер, генераторы заказов и системы диспетчеров.
     *
     * @param numMachines    количество станков.
     * @param bufferCapacity размер буфера.
     * @param lambda         интенсивность генерации заказов.
     * @param numGenerators  количество генераторов заказов.
     */
    public SimulationManager(int numMachines, int bufferCapacity, double lambda, int numGenerators) {
        this.machines = new ArrayList<>();
        for (int i = 0; i < numMachines; i++) {
            machines.add(new Machine(i + 1));
        }
        this.buffer = new Buffer(bufferCapacity);
        this.generators = new ArrayList<>();
        this.orderManagers = new ArrayList<>();
        for (int i = 0; i < numGenerators; i++) {
            OrderGenerator generator = new OrderGenerator(i + 1, lambda);
            generators.add(generator);
            orderManagers.add(new OrderManager(buffer, generator));
        }
        this.machineManager = new MachineManager(machines);
        this.orders = new ArrayList<>();
        this.currentTime = 0.0;
    }

    /**
     * Запускает симуляцию в пошаговом режиме.
     *
     * @param simulationTime общее время симуляции.
     */
    public void runStepByStepSimulation(double simulationTime) {
        Scanner scanner = new Scanner(System.in);
        while (currentTime < simulationTime) {
            System.out.println("Введите букву 'n' для перехода к следующему шагу.");
            String input = scanner.nextLine();

            if (!input.equalsIgnoreCase("n")) {
                continue;
            }

            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println("Текущее время: " + currentTime);

            for (OrderGenerator generator : generators) {
                if (currentTime >= generator.getNextOrderTime()) {
                    Order newOrder = generator.generateOrder(currentTime);
                    System.out.println("Заказ " + newOrder.getId() + " был сгенерирован.");
                    orders.add(newOrder);
                    orderManagers.get(generator.getId() - 1).addOrderToBuffer(newOrder); // Добавляем в буфер
                    generator.scheduleNextOrder(); // Планируем следующее время для заказа
                    System.out.println("Следующее время генерации заказа: " + generator.getNextOrderTime());
                    break;
                }
            }

            // Назначение станку нового заказа, если он не занят
            machineManager.assignOrderToMachine(buffer, currentTime);

            for (Machine machine : machines) {
                if (machine.hasCompletedOrder(currentTime)) {
                    Order completedOrder = machine.releaseOrder();
                    System.out.println("Заказ " + completedOrder.getId() + " выполнен станком " + machine.getId());
                }
            }

            currentTime += 1;

            stepByStepStats();
        }
        System.out.println("Симуляции закончена.");
    }

    private void stepByStepStats() {
        System.out.println("Информация о буфере:");
        buffer.info();

        System.out.println("\nИнформация о генераторах:");
        System.out.println("+----+----------------------+");
        System.out.printf("| %-2s | %-11s |\n", "Id", "Общее кол-во заказов");
        System.out.println("+----+----------------------+");
        for (var generator : generators) {
            System.out.printf("| %-2d | %-20d |\n", generator.getId(), generator.generatedItemsAmount);
        }
        System.out.println("+----+----------------------+");

        System.out.println("\nИнформация о станках:");
        System.out.println("+----+-----------+----------------------+--------------------+");
        System.out.printf("| %-2s | %-7s | %-11s | %-15s |\n", "Id", "Id Заказа", "Общее кол-во заказов", "Общее время работы");
        System.out.println("+----+-----------+----------------------+--------------------+");
        for (var courier : machines) {
            int currentOrderId = courier.getCurrentOrderId();
            System.out.printf("| %-2d | %-9s | %-20d | %-18.2f |\n",
                    courier.getId(),
                    (currentOrderId == 0 ? "None" : currentOrderId),
                    courier.getOrderAmount(),
                    courier.getTotalWorkTime());
        }
        System.out.println("+----+-----------+----------------------+--------------------+");
    }

    /**
     * Запускает симуляцию в автоматическом режиме.
     *
     * @param duration общее время симуляции.
     */
    public void runAutomaticSimulation(double duration) {
        while (currentTime < duration) {

            for (OrderGenerator generator : generators) {
                if (currentTime >= generator.getNextOrderTime()) {
                    Order newOrder = generator.generateOrder(currentTime);
                    orders.add(newOrder);
                    orderManagers.get(generator.getId() - 1).addOrderToBuffer(newOrder);
                    generator.scheduleNextOrder();
                }
            }

            machineManager.assignOrderToMachine(buffer, currentTime);

            for (Machine machine : machines) {
                if (machine.hasCompletedOrder(currentTime)) {
                    Order completedOrder = machine.releaseOrder();
                    System.out.println("Заказ " + completedOrder.getId() + " выполнен станком " + machine.getId());
                }
            }

            currentTime += 1;
        }
        printSimulationResults();
    }

    private void printSimulationResults() {
        System.out.printf("Симуляция закончилась в %.2f \n", currentTime);
        System.out.println("Общее количество сгенерированных заявок: " + orders.size());
        System.out.println("Общее количество отклоненных заявок: " + getTotalRejectedOrders());
        double rejectedRate = (orders.size() > 0) ? (100.0 * getTotalRejectedOrders() / orders.size()) : 0.0;
        System.out.printf("Процент отказа - %.2f %% процентов\n", rejectedRate);

        couriersTable();
        generatorsTable();
    }

    private int getTotalRejectedOrders() {
        int totalRejected = 0;
        for (OrderGenerator generator : generators) {
            totalRejected += generator.getRejectedOrders();
        }
        return totalRejected;
    }

    private void couriersTable() {
        System.out.println("Статистика по станкам:");
        System.out.println("+----+--------------------+---------------------+");
        System.out.println("| Id | Общее время работы | Процент загрузки    |");
        System.out.println("+----+--------------------+---------------------+");
        for (Machine machine : machines) {
            double loadPercentage = machine.getMachineLoadPercentage(currentTime);
            System.out.printf("| %-2d | %-18.2f | %-19.2f |\n",
                    machine.getId(),
                    machine.getTotalWorkTime(), loadPercentage);
        }
        System.out.println("+----+--------------------+---------------------+");
    }

    private void generatorsTable() {
        System.out.println("Статистика по генераторам заявок:");
        System.out.println("+----+---------------+------------------+--------------------+------------------------+");
        System.out.printf("| %-2s | %-10s | %-14s | %-16s | %-16s |\n",
                "Id", "Кол-во заявок", "Откл. заявки (%)", "Ср. время ожидания", "Ср. кв. время ожидания");
        System.out.println("+----+---------------+------------------+--------------------+------------------------+");
        for (OrderGenerator generator : generators) {
            System.out.printf(
                    "| %2d | %13d | %16.2f | %18.2f | %22.2f |\n",
                    generator.getId(),
                    generator.getGeneratedOrders(),
                    100.0 * generator.getRejectedOrders() / generator.getTotalRequests(),
                    generator.getAverageWaitTime(),
                    generator.getWaitTimeVariance()
            );
        }

        System.out.println("+----+---------------+------------------+--------------------+------------------------+");
    }
}
