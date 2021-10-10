package money.wallet.api.dto;

public record WalletOperation(
        WalletOperationType walletOperationType,
        long operationAmount
) {
    public WalletOperation( WalletOperationType walletOperationType ) {
        this( walletOperationType, 0L );
    }
}
