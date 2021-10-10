package money.wallet.api.data;

import money.wallet.api.model.WalletTransaction;
import money.wallet.api.model.WalletTransactionType;

import java.util.Date;

public record WalletTransactionView(
        long transactionId,
        long walletId,
        WalletTransactionType type,
        Long amount,
        Long balanceBefore,
        Long balanceAfter,

        Date startDate,
        Date stopDate
) {

    public static WalletTransactionView fromWalletTransaction( WalletTransaction walletTransaction ) {
        return new WalletTransactionView(
                walletTransaction.getId(),
                walletTransaction.getWalletId(),
                walletTransaction.getType(),
                walletTransaction.getAmount(),
                walletTransaction.getBalanceBefore(),
                walletTransaction.getBalanceAfter(),
                walletTransaction.getStart(),
                walletTransaction.getStop()
        );
    }
}
