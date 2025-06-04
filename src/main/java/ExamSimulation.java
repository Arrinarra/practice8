import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExamSimulation {
    public static void main(String[] args) {
        // Проверка наличия аргумента
        if (args.length < 1) {
            System.out.println("Usage: java ExamSimulation <numberOfStudents>");
            return;
        }

        // Обработка ввода с проверкой на число
        int n;
        try {
            n = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка. Введите число");
            return;
        }

        // Проверка минимального количества студентов
        if (n < 2) {
            System.out.println("Количество студентов должно быть не менее 2");
            return;
        }
        System.out.printf("Экзамен начался! Участники: %d студентов, 2 шпаргалки.%n", n);
        System.out.printf("Стартовые владельцы: студент %d (шпаргалка 1), студент %d (шпаргалка 2)%n%n", 
                          0, 1);
        // Инициализация ресурсов
        List<CheatSheet> cheatSheets = new ArrayList<>();
        cheatSheets.add(new CheatSheet(1));
        cheatSheets.add(new CheatSheet(2));

        List<Student> students = new ArrayList<>();
        ExamController controller = new ExamController(students, cheatSheets);

        // Создание студентов
        for (int i = 0; i < n; i++) {
            students.add(new Student(i, cheatSheets, controller));
        }

        // Назначение начальных владельцев шпаргалок
        cheatSheets.get(0).setHolder(students.get(0));
        cheatSheets.get(1).setHolder(students.get(1));

        // Запуск потоков
        ExecutorService executor = Executors.newFixedThreadPool(n + 1);
        students.forEach(executor::submit);
        executor.submit(new Teacher(cheatSheets, controller));

        executor.shutdown();
        try {
            // Завершаем экзамен через 30 секунд, если не было нарушений
            if (executor.awaitTermination(30, TimeUnit.SECONDS)) {
                if (!controller.isExamFinished()) {
                    controller.completeExam();
                }
            } else {
                executor.shutdownNow();
                controller.failExam("Экзамен прерван по таймауту");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            controller.failExam("Главный поток прерван: " + e.getMessage());
        }
    }
}