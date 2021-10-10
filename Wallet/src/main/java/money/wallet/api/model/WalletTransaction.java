package money.wallet.api.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import money.wallet.api.dto.WalletAmount;

import javax.persistence.*;

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
    long balanceBefore;
    long balanceAfter;
}
