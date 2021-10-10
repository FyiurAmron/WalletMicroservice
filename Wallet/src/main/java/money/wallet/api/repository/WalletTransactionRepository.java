package money.wallet.api.repository;

import money.wallet.api.model.WalletTransaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * new records created, no altering of old records
 */
@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    List<WalletTransaction> findAllByWalletId( long walletId );

    List<WalletTransaction> findAllByWalletId( long walletId, Pageable page );

    List<WalletTransaction> findAllByWalletId( long walletId, Sort sort );
}
