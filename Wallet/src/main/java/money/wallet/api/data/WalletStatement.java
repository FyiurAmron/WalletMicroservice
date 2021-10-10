package money.wallet.api.data;

import money.wallet.api.model.WalletTransaction;

public record WalletStatement( Iterable<WalletTransaction> walletTransactionList ) {
}
