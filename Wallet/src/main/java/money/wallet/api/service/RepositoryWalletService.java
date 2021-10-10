package money.wallet.api.service;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;

import money.wallet.api.data.*;
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

    private void verifyThatTransactionIdIsUnique( long transactionId ) {
        if ( walletTransactionRepository.existsById( transactionId ) ) {
            throw new EntityExistsException( "wallet transaction with ID '" + transactionId
                                                     + "' already present in DB" );
        }
    }

    @Override
    public WalletOperation createWallet( long transactionId ) {
        var executionTimer = new ExecutionTimer();
        verifyThatTransactionIdIsUnique( transactionId );
        var wallet = new Wallet();
        walletRepository.saveAndFlush( wallet );

        var walletOperation = new WalletOperation(
                executionTimer,
                WalletOperationType.CREATE,
                wallet.getId(),
                null,
                null,
                new WalletAmount( wallet.getBalance() ),
                transactionId
        );

        WalletTransaction walletTransaction = walletOperation.toWalletTransaction();
        walletTransactionRepository.saveAndFlush( walletTransaction );

        return walletOperation;
    }

    // WalletOperation removeWallet() { /* */ }

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

    private WalletStatement toWalletStatement(List<WalletTransaction> walletTransactionList ) {
        if ( walletTransactionList.size() == 0 ) {
            throw new EntityNotFoundException();
        }
        return WalletStatement.fromWalletTransactions( walletTransactionList );
    }

    @Override
    public WalletStatement getStatement( long walletId ) {
        return toWalletStatement( walletTransactionRepository.findAllByWalletId( walletId ) );
    }

    @Override
    public WalletStatement getStatement( long walletId, Sort sort ) {
        return toWalletStatement( walletTransactionRepository.findAllByWalletId( walletId, sort ) );
    }

    @Override
    public WalletStatement getStatement( long walletId, Pageable pageable ) {
        return toWalletStatement( walletTransactionRepository.findAllByWalletId( walletId, pageable ) );
    }

    private WalletOperation modifyWalletAmount( long walletId, long amount, long transactionId, boolean isDeposit ) {
        var executionTimer = new ExecutionTimer();
        verifyThatTransactionIdIsUnique( transactionId );
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
