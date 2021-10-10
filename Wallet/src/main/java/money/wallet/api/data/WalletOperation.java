package money.wallet.api.data;

import money.wallet.api.model.WalletTransaction;
import money.wallet.api.util.ExecutionTimer;

public record WalletOperation(
        ExecutionTimer executionTimer,
        WalletOperationType type,
        long walletId,
        WalletAmount amount, // Optional<WalletAmount> could've been used, but that would add...
        WalletAmount balanceBefore, // ... another abstraction layer with no real gain here -
        WalletAmount balanceAfter, // both entity and response use null anyway
        Long transactionId // ditto
) {
    public WalletOperation {
        executionTimer.stop(); // idempotent, no side effects
    }

    public WalletTransaction toWalletTransaction() {
        var walletTransaction = new WalletTransaction();

        walletTransaction
                .setType( type.toWalletTransactionType() )
                .setAmount( amount == null ? null : amount.value() ) // see above note on Optional<>
                .setBalanceBefore( balanceBefore == null ? null : balanceBefore.value() ) // ditto
                .setBalanceAfter( balanceAfter == null ? null : balanceAfter.value() ) // ditto
                .setWalletId( walletId )
                .setId( transactionId )
                .setStart( executionTimer.getStart() )
                .setStop( executionTimer.getStop() );

        return walletTransaction;
    }
}
