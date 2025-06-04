import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Класс, представляющий преподавателя как поток.
 * Проверяет аудиторию на наличие списывания.
 */
public class Teacher implements Runnable {
    private final List<CheatSheet> cheatSheets;
    private final ExamController controller;
    private final Random random = new Random();
    private int checkCount = 0;

    /**
     * Конструктор преподавателя.
     * @param cheatSheets Список шпаргалок.
     * @param controller Контроллер экзамена.
     */
    public Teacher(List<CheatSheet> cheatSheets, ExamController controller) {
        this.cheatSheets = cheatSheets;
        this.controller = controller;
    }

    @Override
    public void run() {
        try {
            while (!controller.isExamFinished()) {
                int delay = 500 + random.nextInt(1500);
                TimeUnit.MILLISECONDS.sleep(delay);
                
                if (controller.isExamFinished()) break;

                checkCount++;
                StringBuilder status = new StringBuilder();
                status.append("\n--- Проверка #").append(checkCount).append(" ---\n");
                
                // Собираем информацию о владельцах
                for (CheatSheet sheet : cheatSheets) {
                    Student holder = sheet.getHolder();
                    status.append("Шпаргалка ").append(sheet.getId())
                          .append(": ")
                          .append(holder == null ? "СВОБОДНА" : "студент " + holder.getId())
                          .append("\n");
                }
                
                // Проверяем свободные шпаргалки
                StringBuilder freeSheets = new StringBuilder();
                for (CheatSheet sheet : cheatSheets) {
                    if (sheet.isFree()) {
                        if (!freeSheets.isEmpty()) freeSheets.append(", ");
                        freeSheets.append(sheet.getId());
                    }
                }

                if (!freeSheets.isEmpty()) {
                    status.append("НАРУШЕНИЕ: Обнаружены свободные шпаргалки (ID: ").append(freeSheets).append(")!");
                    System.out.println(status);
                    controller.failExam(status.toString());
                } else {
                    status.append("Нарушений не обнаружено");
                    System.out.println(status);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Наблюдение преподавателя прервано: " + e.getMessage());
        }
    }
}