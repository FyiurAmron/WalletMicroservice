package money.wallet.api.service;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import money.wallet.api.dto.*;
import money.wallet.api.model.*;
import money.wallet.api.repository.WalletRepository;
import money.wallet.api.repository.WalletTransactionRepository;
import money.wallet.api.util.ExecutionTimer;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults( makeFinal = true, level = AccessLevel.PRIVATE )
public class RepositoryWalletService implements WalletService {
    WalletRepository walletRepository;
    WalletTransactionRepository walletTransactionRepository;

    @Override
    public WalletResponse createWallet() {
        var executionTimer = new ExecutionTimer();
        var wallet = new Wallet();
        walletRepository.saveAndFlush( wallet );
        executionTimer.stop();

        return new WalletResponse(
                wallet.getId(),
                new WalletOperation( WalletOperationType.CREATE ),
                null,
                WalletAmount.from( wallet ),
                executionTimer
        );
    }

    @Override
    public WalletResponse getBalance( long walletId ) {
        var executionTimer = new ExecutionTimer();
        Wallet wallet = walletRepository.getById( walletId );
        WalletAmount walletBalance = WalletAmount.from( wallet );
        executionTimer.stop();

        return new WalletResponse(
                wallet.getId(),
                new WalletOperation( WalletOperationType.BALANCE ),
                walletBalance,
                walletBalance,
                executionTimer
        );
    }

    @Override
    public WalletStatement getStatement( long walletId ) {
        // TODO paging/sorting
        return new WalletStatement();
    }

    private WalletResponse modifyWalletAmount( long walletId, long amount, long transactionId, boolean isDeposit ) {
        var executionTimer = new ExecutionTimer();
        if ( walletTransactionRepository.existsById( transactionId ) ) {
            throw new IllegalStateException();
        }
        Wallet wallet = walletRepository.getById( walletId );
        WalletAmount oldBalance = WalletAmount.from( wallet );
        WalletAmount changeAmount = new WalletAmount( amount );
        WalletAmount newBalance = isDeposit
                ? oldBalance.increaseBy( changeAmount )
                : oldBalance.decreaseBy( changeAmount );
        wallet.setBalance( newBalance.value() );
        walletRepository.saveAndFlush( wallet );
        // TODO add WalletTransaction entry
        executionTimer.stop();

        return new WalletResponse(
                wallet.getId(),
                new WalletOperation( isDeposit ? WalletOperationType.DEPOSIT : WalletOperationType.WITHDRAWAL ),
                oldBalance,
                newBalance,
                executionTimer
        );
    }

    @Override
    @Transactional( isolation = Isolation.SERIALIZABLE )
    public WalletResponse makeDeposit( long walletId, long amount, long transactionId ) {
        return modifyWalletAmount( walletId, amount, transactionId, true );
    }

    @Override
    @Transactional( isolation = Isolation.SERIALIZABLE )
    public WalletResponse makeWithdrawal( long walletId, long amount, long transactionId ) {
        return modifyWalletAmount( walletId, amount, transactionId, false );
    }
}
