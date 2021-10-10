package money.wallet.api.model;

import javax.persistence.*;

@Entity
public class WalletTransaction {
    @Id
    @GeneratedValue
    private Long id;
}
