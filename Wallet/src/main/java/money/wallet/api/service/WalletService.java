package money.wallet.api.service;

import money.wallet.api.dto.WalletResponse;
import money.wallet.api.dto.WalletStatement;

public interface WalletService {
    WalletResponse createWallet();

    WalletResponse getBalance( long walletId );

    WalletStatement getStatement( long walletId );

    WalletResponse makeDeposit( long walletId, long amount, long transactionId );

    WalletResponse makeWithdrawal( long walletId, long amount, long transactionId );
}
