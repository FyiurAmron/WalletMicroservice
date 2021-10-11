package money.wallet.api.data;

import money.wallet.api.model.WalletTransactionType;

import java.util.HashMap;
import java.util.Map;

public enum WalletOperationType {
    CREATE( WalletTransactionType.CREATE ),
    // REMOVE( WalletTransactionType.REMOVE ),
    BALANCE( null ),
    WITHDRAWAL( WalletTransactionType.WITHDRAWAL ),
    DEPOSIT( WalletTransactionType.DEPOSIT ),
    ;
    WalletTransactionType walletTransactionType;
    static final Map<WalletTransactionType, WalletOperationType> typeMap = new HashMap<>();

    WalletOperationType( WalletTransactionType walletTransactionType ) {
        this.walletTransactionType = walletTransactionType;
    }

    static {
        for ( var wot : values() ) {
            typeMap.put( wot.walletTransactionType, wot );
        }
    }

    public static WalletOperationType from( WalletTransactionType walletTransactionType ) {
        return typeMap.get( walletTransactionType );
    }

    public WalletTransactionType toWalletTransactionType() {
        return walletTransactionType;
    }
}
