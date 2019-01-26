import com.google.common.base.Preconditions;
import lombok.Value;

/**
 * Created by mtumilowicz on 2019-01-26.
 */
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
