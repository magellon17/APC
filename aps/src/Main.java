import manager.SimulationManager;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Выберите режим работы (авто, шаг):");
        Scanner sc = new Scanner(System.in);

        while (true) {
            String operationgMode = sc.nextLine().toLowerCase();
            if (operationgMode.equals("авто")) {
                SimulationManager automaticSimulationManager = new SimulationManager(16, 40, 0.2, 10);
                automaticSimulationManager.runAutomaticSimulation(2500);
                break;
            } else if (operationgMode.equals("шаг")) {
                SimulationManager stepSimulationManager = new SimulationManager(2, 3, 0.5, 1);
                stepSimulationManager.runStepByStepSimulation(1000);
                break;
            } else {
                System.out.println("Неверный режим работы. Попробуйте снова ввести режим работы.");
            }
        }
        sc.close();
    }
}
