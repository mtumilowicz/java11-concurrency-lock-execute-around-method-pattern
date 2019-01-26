import org.junit.Test;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by mtumilowicz on 2019-01-25.
 */
public class XXXTest {

    @Test
    public void transfer() {
        var userMap = Stream.of(new User(1, 40), new User(2, 33))
                .collect(Collectors.toMap(User::getId, Function.identity()));

        ReadWriteLock lock = new ReentrantReadWriteLock();
        LockExecutor executor = new LockExecutor(lock);

        executor.write(() -> {
            int transfer = 15;
            userMap.replace(1, userMap.get(1).outcome(transfer));
            userMap.replace(2, userMap.get(2).income(transfer));
        });

        assertThat(userMap.get(1).getBalance(), is(25));
        assertThat(userMap.get(2).getBalance(), is(48));
    }

    @Test
    public void read() {
        var userMap = Stream.of(new User(1, 40), new User(2, 33))
                .collect(Collectors.toMap(User::getId, Function.identity()));

        ReadWriteLock lock = new ReentrantReadWriteLock();
        LockExecutor executor = new LockExecutor(lock);

        var balanceAll = executor.read(() -> userMap.values().stream().map(User::getBalance).mapToInt(x -> x).sum());

        assertThat(balanceAll, is(73));
    }
}