import java.util.concurrent.locks.Lock;

/**
 * Created by mtumilowicz on 2019-01-07.
 */
public class XXX {
    private final Lock lock;

    public XXX(Lock lock) {
        this.lock = lock;
    }

    public void run(Runnable action) {
        lock.lock();
        try {
            action.run();
        } finally {
            lock.unlock();
        }
    }
}
