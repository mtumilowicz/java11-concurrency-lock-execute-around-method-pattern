import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Supplier;

/**
 * Created by mtumilowicz on 2019-01-07.
 */
public class LockExecutor {
    private final ReadWriteLock lock;

    LockExecutor(ReadWriteLock lock) {
        this.lock = lock;
    }

    public void write(Runnable action) {
        lock.writeLock().lock();
        try {
            action.run();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public <T> T read(Supplier<T> action) {
        lock.readLock().lock();
        try {
            return action.get();
        } finally {
            lock.readLock().unlock();
        }
    }
}
