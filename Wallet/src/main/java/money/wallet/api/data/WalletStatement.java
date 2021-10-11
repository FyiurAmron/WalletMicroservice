package money.wallet.api.data;

import money.wallet.api.model.WalletTransaction;

import java.util.List;
import java.util.stream.StreamSupport;

public record WalletStatement( List<WalletOperation> walletStatementItems ) {
    public static WalletStatement fromWalletTransactions( Iterable<WalletTransaction> walletTransactions ) {
        return new WalletStatement(
                StreamSupport.stream( walletTransactions.spliterator(), false )
                             .map( WalletOperation::fromWalletTransaction )
                             .toList()
        );
    }
}
