import lombok.Value;

/**
 * Created by mtumilowicz on 2019-01-26.
 */
@Value
class User {
    int id;
    int balance;

    User income(int value) {
        return new User(id, balance + value);
    }

    User outcome(int value) {
        return new User(id, balance - value);
    }
}
