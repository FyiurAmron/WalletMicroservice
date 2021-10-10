package money.wallet.api.model;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Accessors( chain = true )
@NoArgsConstructor
@FieldDefaults( level = AccessLevel.PRIVATE )
@Entity
public class WalletTransaction {
    @Id
    // @GeneratedValue
    Long id;

    long walletId;

    @Enumerated( EnumType.ORDINAL )
    WalletTransactionType type;

    Long amount; // null if the account is being created

    // usefulness of the below fields depends on the actual use case;
    // they increase DB size (and somewhat denormalize it, w.r.t. balance), but also ease management and debugging
    Long balanceBefore; // null if the account is being created
    Long balanceAfter; // null if the account is being removed

    Date start;
    Date stop;
}
