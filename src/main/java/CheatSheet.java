import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Класс, представляющий шпаргалку как разделяемый ресурс.
 * Использует ReentrantLock для синхронизации доступа.
 */
public class CheatSheet {
    private final int id;
    private Student holder;
    private final Lock lock = new ReentrantLock();

    /**
     * Конструктор шпаргалки.
     * @param id Уникальный идентификатор шпаргалки.
     */
    public CheatSheet(int id) {
        this.id = id;
    }

    /**
     * Устанавливает текущего владельца шпаргалки.
     * @param student Студент, получающий шпаргалку.
     */
    public void setHolder(Student student) {
        lock.lock();
        try {
            this.holder = student;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Возвращает текущего владельца шпаргалки.
     * @return Текущий владелец.
     */
    public Student getHolder() {
        lock.lock();
        try {
            return holder;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Проверяет, свободна ли шпаргалка.
     * @return true, если шпаргалка свободна.
     */
    public boolean isFree() {
        lock.lock();
        try {
            return holder == null;
        } finally {
            lock.unlock();
        }
    }
        
    public int getId() {
        return id;
        }
    }
