package money.wallet.api.service;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import money.wallet.api.exception.*;
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
            throw new TransactionIdAlreadyExistsException( transactionId );
        }
    }

    private Wallet getWalletById( long walletId ) {
        return walletRepository.findById( walletId )
                               .orElseThrow( () -> new WalletIdNotFoundException( walletId ) );
    }

    @Override
    public WalletOperation createWallet( long transactionId )
            throws TransactionIdAlreadyExistsException {
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
    public WalletOperation getBalance( long walletId )
            throws WalletIdNotFoundException {
        var executionTimer = new ExecutionTimer();
        Wallet wallet = getWalletById( walletId );
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

    private WalletStatement toWalletStatement( List<WalletTransaction> walletTransactionList, long walletId ) {
        if ( walletTransactionList.size() == 0 ) {
            throw new WalletIdNotFoundException( walletId );
        }
        return WalletStatement.fromWalletTransactions( walletTransactionList );
    }

    @Override
    public WalletStatement getStatement( long walletId )
            throws WalletIdNotFoundException {
        return toWalletStatement( walletTransactionRepository.findAllByWalletId( walletId ), walletId );
    }

    @Override
    public WalletStatement getStatement( long walletId, Sort sort )
            throws WalletIdNotFoundException {
        return toWalletStatement( walletTransactionRepository.findAllByWalletId( walletId, sort ), walletId );
    }

    @Override
    public WalletStatement getStatement( long walletId, Pageable pageable )
            throws WalletIdNotFoundException {
        return toWalletStatement( walletTransactionRepository.findAllByWalletId( walletId, pageable ), walletId );
    }

    private WalletOperation modifyWalletAmount( long walletId, long amount, long transactionId, boolean isDeposit ) {
        var executionTimer = new ExecutionTimer();
        verifyThatTransactionIdIsUnique( transactionId );
        Wallet wallet = getWalletById( walletId );
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
    public WalletOperation makeDeposit( long walletId, long amount, long transactionId )
            throws WalletIdNotFoundException,
                   IllegalOperationAmountException,
                   TransactionIdAlreadyExistsException {
        return modifyWalletAmount( walletId, amount, transactionId, true );
    }

    @Override
    @Transactional( isolation = Isolation.SERIALIZABLE )
    public WalletOperation makeWithdrawal( long walletId, long amount, long transactionId )
            throws WalletIdNotFoundException,
                   IllegalOperationAmountException,
                   TransactionIdAlreadyExistsException {
        return modifyWalletAmount( walletId, amount, transactionId, false );
    }
}
