package money.wallet.api.data;

import money.wallet.api.model.WalletTransactionType;

public enum WalletOperationType {
    CREATE( WalletTransactionType.CREATE ),
    BALANCE( null ),
    WITHDRAWAL( WalletTransactionType.WITHDRAWAL ),
    DEPOSIT( WalletTransactionType.DEPOSIT ),
    ;
    WalletTransactionType walletTransactionType;

    WalletOperationType( WalletTransactionType walletTransactionType ) {
        this.walletTransactionType = walletTransactionType;
    }

    WalletTransactionType toWalletTransactionType() {
        return walletTransactionType;
    }
}
