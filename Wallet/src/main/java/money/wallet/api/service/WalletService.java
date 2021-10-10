package money.wallet.api.service;

import money.wallet.api.dto.WalletOperation;
import money.wallet.api.dto.WalletStatement;

public interface WalletService {
    WalletOperation createWallet();

    WalletOperation getBalance( long walletId );

    WalletStatement getStatement( long walletId );

    WalletOperation makeDeposit( long walletId, long amount, long transactionId );

    WalletOperation makeWithdrawal( long walletId, long amount, long transactionId );
}
