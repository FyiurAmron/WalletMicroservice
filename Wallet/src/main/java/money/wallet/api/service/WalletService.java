package money.wallet.api.service;

import money.wallet.api.data.WalletOperation;
import money.wallet.api.data.WalletStatement;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface WalletService {
    WalletOperation createWallet();

    // WalletOperation removeWallet();

    WalletOperation getBalance( long walletId );

    WalletStatement getStatement( long walletId );

    WalletStatement getStatement( long walletId, Sort sort );

    WalletStatement getStatement( long walletId, Pageable pageable );

    WalletOperation makeDeposit( long walletId, long amount, long transactionId );

    WalletOperation makeWithdrawal( long walletId, long amount, long transactionId );
}
