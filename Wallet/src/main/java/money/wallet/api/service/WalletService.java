package money.wallet.api.service;

import money.wallet.api.data.WalletOperation;
import money.wallet.api.data.WalletStatement;

public interface WalletService {
    WalletOperation createWallet();

    WalletOperation getBalance( long walletId );

    WalletStatement getStatement( long walletId );

    WalletOperation makeDeposit( long walletId, long amount, long transactionId );

    WalletOperation makeWithdrawal( long walletId, long amount, long transactionId );
}
