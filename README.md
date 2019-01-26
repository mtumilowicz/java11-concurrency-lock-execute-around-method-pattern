# java11-concurrency-lock-execute-around-method-pattern
Example of Execute Around Method pattern.

# preface
For details about `ReadWriteLock` please refer my other
github project: https://github.com/mtumilowicz/java-concurrency-readwritelock

In this project we will show how to use Execute Around Method 
(EAM) pattern to wrap critical sections and minimise the pain
of working with the `ReadWriteLock` interface.

# project description
**The main goal**: simple implementation of transferring 
money from user to user.

1. `User` class
    ```
    @Value
    class User {
        int id;
        int balance;
    
        User income(PositiveInt value) {
            return new User(id, balance + value.amount);
        }
    
        User outcome(PositiveInt value) {
            return new User(id, balance - value.amount);
        }
    }
    ```
    ```
    class PositiveInt {
        final int amount;
    
        private PositiveInt(int amount) {
            this.amount = amount;
        }
    
        static PositiveInt of(int amount) {
            Preconditions.checkArgument(amount > 0);
    
            return new PositiveInt(amount);
        }
    }
    ```
1. Immutable map of users:
    ```
    Map<Integer, User> userMap = Map.of(
                1, new User(1, 40),
                2, new User(2, 33)
        );
    ```
1. We want to provide tread-safe read and writes on that map
    * we will use `ReadWriteLock` interface
    * we provide class that absorbs the pain of working
    with that interface (locking/unlocking tiresome obligation)
    ```
    * we will supply action to execute in locked block
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    @RequiredArgsConstructor
    class LockExecutor {
        ReadWriteLock lock;
    
        <T> T write(Supplier<T> action) {
            lock.writeLock().lock();
            try {
                return action.get();
            } finally {
                lock.writeLock().unlock();
            }
        }
    
        <T> T read(Supplier<T> action) {
            lock.readLock().lock();
            try {
                return action.get();
            } finally {
                lock.readLock().unlock();
            }
        }
    }
    ```
    **Remark:** we choose `Supplier` as a parameter, because
    it's often very handy to return a value from that methods.
    
# tests
1. We want to transfer 15 credits from the first user, to the 
second one
    ```
    ReadWriteLock lock = new ReentrantReadWriteLock();
    LockExecutor executor = new LockExecutor(lock);
    
    var transferred = executor.write(() -> {
        var transfer = PositiveInt.of(15);
        return Map.of(1, userMap.get(1).outcome(transfer),
                2, userMap.get(2).income(transfer));
    });
    
    assertThat(transferred.get(1).getBalance(), is(25));
    assertThat(transferred.get(2).getBalance(), is(48));
    ```
1. We want to sum all balances from all users
    ```
    ReadWriteLock lock = new ReentrantReadWriteLock();
    LockExecutor executor = new LockExecutor(lock);
    
    var balanceAll = executor.read(() -> userMap.values()
            .stream()
            .map(User::getBalance)
            .mapToInt(x -> x)
            .sum());
    
    assertThat(balanceAll, is(73));
    ```