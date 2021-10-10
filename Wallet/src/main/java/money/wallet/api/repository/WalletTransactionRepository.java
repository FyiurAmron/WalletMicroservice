package money.wallet.api.repository;

import money.wallet.api.model.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * new records created, no altering of old records
 */
@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
}
