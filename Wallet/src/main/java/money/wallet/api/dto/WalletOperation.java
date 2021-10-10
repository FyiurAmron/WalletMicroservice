package money.wallet.api.dto;

import money.wallet.api.model.WalletTransaction;
import money.wallet.api.util.ExecutionTimer;

public record WalletOperation(
        ExecutionTimer executionTimer,
        WalletOperationType type,
        long walletId,
        WalletAmount amount,
        WalletAmount balanceBefore,
        WalletAmount balanceAfter,
        Long transactionId
) {
    public WalletOperation {
        executionTimer.stop(); // idempotent, no side effects
    }

    public WalletTransaction toWalletTransaction() {
        var walletTransaction = new WalletTransaction();

        walletTransaction
                .setType( type.toWalletTransactionType() )
                .setAmount( amount.value() )
                .setBalanceBefore( balanceBefore.value() )
                .setBalanceAfter( balanceAfter.value() )
                .setWalletId( walletId )
                .setId( transactionId )
                .setStart( executionTimer.getStart() )
                .setStop( executionTimer.getStop() );


        return walletTransaction;
    }
}
