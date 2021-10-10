package money.wallet.api.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import money.wallet.api.dto.WalletAmount;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
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

    long amount;

    // usefulness of the below fields depends on the actual use case;
    // they increase DB size (and somewhat denormalize it, w.r.t. balance), but also ease management and debugging
    long balanceBefore;
    long balanceAfter;

    Date start;
    Date stop;
}
