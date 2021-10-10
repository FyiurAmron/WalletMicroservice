package money.wallet.api.model;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Getter
@Setter
@Accessors( chain = true )
@NoArgsConstructor
@Entity
public class Wallet {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.PROTECTED)
    Long id;
    /*
    // `Isolation.SERIALIZABLE` used instead due to expected large amounts of concurrent DB writes
    @Version
    @Setter(AccessLevel.PROTECTED)
    Integer version;
     */

    long balance;
}
