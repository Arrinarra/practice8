import java.util.List;
import java.util.Random;

/**
 * Контроллер, управляющий логикой экзамена и синхронизацией потоков.
 */
public class ExamController {
    private final List<Student> students;
    private final List<CheatSheet> cheatSheets;
    private volatile boolean examFinished = false;
    private final Random random = new Random();

    /**
     * Конструктор контроллера.
     * @param students Список студентов.
     * @param cheatSheets Список шпаргалок.
     */
    public ExamController(List<Student> students, List<CheatSheet> cheatSheets) {
        this.students = students;
        this.cheatSheets = cheatSheets;
    }

    /**
     * Проверяет, завершен ли экзамен.
     * @return true, если экзамен завершен.
     */
    public synchronized boolean isExamFinished() {
        return examFinished;
    }

    /**
     * Передает шпаргалку другому студенту.
     * @param from Студент, передающий шпаргалку.
     * @param sheet Шпаргалка для передачи.
     */
    public synchronized void transferCheatSheet(Student from, CheatSheet sheet) {
        if (examFinished) return;

        sheet.setHolder(null);
        System.out.printf("Шпаргалка %d освобождена студентом %d.%n", sheet.getId(), from.getId());

        try {
            int delay = 50 + random.nextInt(150);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Передача прервана: " + e.getMessage());
            return;
        }

        if (examFinished) return;

        Student to;
        do {
            to = students.get(random.nextInt(students.size()));
        } while (to == from);

        sheet.setHolder(to);
        to.incrementUsage();
        System.out.printf("Студент %d передал шпаргалку %d студенту %d.%n", 
                          from.getId(), sheet.getId(), to.getId());
        checkExamCompletion();
    }

    /**
     * Проверяет условия завершения экзамена.
     */
    private synchronized void checkExamCompletion() {
        for (Student student : students) {
            if (student.getUsageCount() > 3) {
                failExam("Студент " + student.getId() + " использовал шпаргалку слишком много раз: " + student.getUsageCount());
            }
        }
    }

    /**
     * Завершает экзамен с неудачей.
     * @param details Детали инцидента.
     */
    public synchronized void failExam(String details) {
        if (!examFinished) {
            examFinished = true;
            System.out.println("\n====== ПРЕРЫВАНИЕ ЭКЗАМЕНА ======");
            System.out.println(details);
            printStatistics();
        }
    }
    
    /**
     * Выводит статистику по использованию шпаргалок.
     */
    private void printStatistics() {
        System.out.println("\nИтоговая статистика:");
        for (Student student : students) {
            System.out.printf("Студент %d использовал шпаргалку %d раз%n",
                              student.getId(), student.getUsageCount());
        }
    }
    
    /**
     * Выводит сообщение о успешном завершении экзамена.
     */
    public void completeExam() {
        if (!examFinished) {
            examFinished = true;
            System.out.println("\n====== ЭКЗАМЕН УСПЕШНО ЗАВЕРШЕН ======");
            printStatistics();
        }
    }
}
