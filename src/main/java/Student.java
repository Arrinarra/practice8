import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Класс, представляющий студента как поток.
 * Студенты передают шпаргалки друг другу.
 */
public class Student implements Runnable {
    public final int id;
    private final List<CheatSheet> cheatSheets;
    private final ExamController controller;
    private int usageCount = 0;
    private final Random random = new Random();

    /**
     * Конструктор студента.
     * @param id Уникальный идентификатор студента.
     * @param cheatSheets Список шпаргалок.
     * @param controller Контроллер экзамена.
     */
    public Student(int id, List<CheatSheet> cheatSheets, ExamController controller) {
        this.id = id;
        this.cheatSheets = cheatSheets;
        this.controller = controller;
    }

    /**
     * Увеличивает счетчик использования шпаргалки.
     */
    public void incrementUsage() {
        usageCount++;
    }

    /**
     * Возвращает количество использований шпаргалки.
     * @return Количество использований.
     */
    public int getUsageCount() {
        return usageCount;
    }

    @Override
public void run() {
    try {
        while (!controller.isExamFinished()) {
            TimeUnit.MILLISECONDS.sleep(100 + random.nextInt(400));
            
            if (controller.isExamFinished()) break;
            
            for (CheatSheet sheet : cheatSheets) {
                if (this.equals(sheet.getHolder())) {
                    System.out.printf("Студент %d готовится передать шпаргалку %d...%n",
                                      id, sheet.getId());
                    controller.transferCheatSheet(this, sheet);
                    break;
                }
            }
        }
    } catch (InterruptedException e) {
        System.out.printf("Студент %d прерван: %s%n", id, e.getMessage());
        Thread.currentThread().interrupt();
    }
}
public int getId() {
        return id;
    }
}