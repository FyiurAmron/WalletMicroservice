package money.wallet.api.dto;

import money.wallet.api.util.ExecutionTimer;

public record WalletResponse(
        long walletId,
        WalletOperation operation,
        WalletAmount beforeOp,
        WalletAmount afterOp,
        ExecutionTimer executionTimer
) {
}
