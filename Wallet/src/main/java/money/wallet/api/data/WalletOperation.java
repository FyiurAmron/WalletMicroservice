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
                .setAmount( WalletAmount.toLong( amount ) ) // see above note on Optional<>
                .setBalanceBefore( WalletAmount.toLong( balanceBefore ) ) // ditto
                .setBalanceAfter( WalletAmount.toLong( balanceAfter ) ) // ditto
                .setWalletId( walletId )
                .setId( transactionId )
                .setStart( executionTimer.getStart() )
                .setStop( executionTimer.getStop() );

        return walletTransaction;
    }

    public static WalletOperation fromWalletTransaction( WalletTransaction walletTransaction ) {
        return new WalletOperation(
                new ExecutionTimer( walletTransaction.getStart(), walletTransaction.getStop() ),
                WalletOperationType.from( walletTransaction.getType() ),
                walletTransaction.getWalletId(),
                WalletAmount.from( walletTransaction.getAmount() ),
                WalletAmount.from( walletTransaction.getBalanceBefore() ),
                WalletAmount.from( walletTransaction.getBalanceAfter() ),
                walletTransaction.getId()
        );
    }
}
