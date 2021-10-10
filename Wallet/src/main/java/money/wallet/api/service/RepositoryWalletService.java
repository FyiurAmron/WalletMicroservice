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

import javax.persistence.EntityExistsException;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults( makeFinal = true, level = AccessLevel.PRIVATE )
public class RepositoryWalletService implements WalletService {
    WalletRepository walletRepository;
    WalletTransactionRepository walletTransactionRepository;

    @Override
    public WalletOperation createWallet() {
        var executionTimer = new ExecutionTimer();
        var wallet = new Wallet();
        walletRepository.saveAndFlush( wallet );

        return new WalletOperation(
                executionTimer,
                WalletOperationType.CREATE,
                wallet.getId(),
                null,
                null,
                new WalletAmount( wallet.getBalance() ),
                null
        );
    }

    @Override
    public WalletOperation getBalance( long walletId ) {
        var executionTimer = new ExecutionTimer();
        Wallet wallet = walletRepository.getById( walletId );
        WalletAmount walletBalance = WalletAmount.from( wallet );

        return new WalletOperation(
                executionTimer,
                WalletOperationType.BALANCE,
                wallet.getId(),
                null,
                walletBalance,
                walletBalance,
                null
        );
    }

    @Override
    public WalletStatement getStatement( long walletId ) {
        // TODO paging/sorting (if needed)
        return new WalletStatement();
    }

    private WalletOperation modifyWalletAmount( long walletId, long amount, long transactionId, boolean isDeposit ) {
        var executionTimer = new ExecutionTimer();
        if ( walletTransactionRepository.existsById( transactionId ) ) {
            throw new EntityExistsException( "wallet transaction with ID '" + transactionId
                                                     + "' already present in DB" );
        }

        Wallet wallet = walletRepository.getById( walletId );
        WalletAmount oldBalance = WalletAmount.from( wallet );
        WalletAmount changeAmount = new WalletAmount( amount );
        WalletAmount newBalance = isDeposit
                ? oldBalance.increaseBy( changeAmount )
                : oldBalance.decreaseBy( changeAmount );
        wallet.setBalance( newBalance.value() );

        var walletOperation = new WalletOperation(
                executionTimer,
                isDeposit
                        ? WalletOperationType.DEPOSIT
                        : WalletOperationType.WITHDRAWAL,
                wallet.getId(),
                changeAmount,
                oldBalance,
                newBalance,
                transactionId
        );
        WalletTransaction walletTransaction = walletOperation.toWalletTransaction();
        walletTransactionRepository.saveAndFlush( walletTransaction );

        return walletOperation;
    }

    @Override
    @Transactional( isolation = Isolation.SERIALIZABLE )
    public WalletOperation makeDeposit( long walletId, long amount, long transactionId ) {
        return modifyWalletAmount( walletId, amount, transactionId, true );
    }

    @Override
    @Transactional( isolation = Isolation.SERIALIZABLE )
    public WalletOperation makeWithdrawal( long walletId, long amount, long transactionId ) {
        return modifyWalletAmount( walletId, amount, transactionId, false );
    }
}
