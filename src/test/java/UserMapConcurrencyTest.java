import org.junit.Test;

import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by mtumilowicz on 2019-01-25.
 */
public class UserMapConcurrencyTest {

    private Map<Integer, User> userMap = Map.of(
            1, new User(1, 40),
            2, new User(2, 33)
    );

    @Test
    public void write_transfer() {
        ReadWriteLock lock = new ReentrantReadWriteLock();
        LockExecutor executor = new LockExecutor(lock);

        var transferred = executor.write(() -> {
            var transfer = PositiveInt.of(15);
            return Map.of(1, userMap.get(1).outcome(transfer),
                    2, userMap.get(2).income(transfer));
        });

        assertThat(transferred.get(1).getBalance(), is(25));
        assertThat(transferred.get(2).getBalance(), is(48));
    }

    @Test
    public void read_sum_balance() {
        ReadWriteLock lock = new ReentrantReadWriteLock();
        LockExecutor executor = new LockExecutor(lock);

        var balanceAll = executor.read(() -> userMap.values()
                .stream()
                .map(User::getBalance)
                .mapToInt(x -> x)
                .sum());

        assertThat(balanceAll, is(73));
    }
}