package money.wallet.api;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;

import money.wallet.api.data.*;
import money.wallet.api.exception.IllegalOperationAmountException;
import money.wallet.api.service.RepositoryWalletService;

import static org.junit.jupiter.api.Assertions.*;

@Tag( TestUtils.INTEGRATION_TAG )
@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext( classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD )
// alternatively, to speed things up, create a context doing a DB reset at setup
public class WalletServiceTests {
    private static final long EXAMPLE_TRANSACTION_ID = 1337;
    private static final long EXAMPLE_AMOUNT = 42;
    private static final int OP_REPEATS = 5;

    @Autowired
    private RepositoryWalletService walletService;

    // we don't expose this type of comparison in WalletAmount to not pollute the implementation
    private void assertAmountEquals( Long nullableAmount, WalletAmount walletAmount ) {
        assertEquals(
                nullableAmount == null ? null : new WalletAmount( nullableAmount ),
                walletAmount
        );
    }

    private void assertOperationResults(
            WalletOperation walletOperation,
            WalletOperationType type,
            Long amount,
            Long balanceBefore,
            Long balanceAfter,
            Long transactionId
    ) {
        assertEquals(
                type,
                walletOperation.type()
        );
        assertAmountEquals(
                amount,
                walletOperation.amount()
        );
        assertAmountEquals(
                balanceBefore,
                walletOperation.balanceBefore()
        );
        assertAmountEquals(
                balanceAfter,
                walletOperation.balanceAfter()
        );
        assertEquals(
                transactionId,
                walletOperation.transactionId()
        );
    }

    @Test
    public void isWalletCreatedEmpty() {
        WalletOperation createWalletOperation = walletService.createWallet( EXAMPLE_TRANSACTION_ID );

        assertOperationResults(
                createWalletOperation,
                WalletOperationType.CREATE,
                null,
                null,
                0L,
                EXAMPLE_TRANSACTION_ID
        );
    }

    @Test
    public void isWalletBalanceZeroAfterCreation() {
        WalletOperation createWalletOperation = walletService.createWallet( EXAMPLE_TRANSACTION_ID );
        WalletOperation balanceWalletOperation = walletService.getBalance( createWalletOperation.walletId() );

        assertOperationResults(
                balanceWalletOperation,
                WalletOperationType.BALANCE,
                null,
                0L,
                0L,
                null
        );
    }

    @Test
    public void throwsOnCreateWithNonUniqueIds() {
        walletService.createWallet( EXAMPLE_TRANSACTION_ID );
        assertThrows( EntityExistsException.class, () ->
                walletService.createWallet( EXAMPLE_TRANSACTION_ID ) );
    }

    @Test
    public void areAmountsProperlyDeposited() {
        WalletOperation createWalletOperation = walletService.createWallet( EXAMPLE_TRANSACTION_ID );
        WalletOperation makeDepositWalletOperation = walletService.makeDeposit(
                createWalletOperation.walletId(),
                EXAMPLE_AMOUNT,
                EXAMPLE_TRANSACTION_ID + 1
        );

        assertOperationResults(
                makeDepositWalletOperation,
                WalletOperationType.DEPOSIT,
                EXAMPLE_AMOUNT,
                0L,
                EXAMPLE_AMOUNT,
                EXAMPLE_TRANSACTION_ID + 1
        );

        WalletOperation makeDepositWalletResponse2 = walletService.makeDeposit(
                createWalletOperation.walletId(),
                EXAMPLE_AMOUNT * 2,
                EXAMPLE_TRANSACTION_ID + 2
        );

        assertOperationResults(
                makeDepositWalletResponse2,
                WalletOperationType.DEPOSIT,
                EXAMPLE_AMOUNT * 2,
                EXAMPLE_AMOUNT,
                EXAMPLE_AMOUNT * 3,
                EXAMPLE_TRANSACTION_ID + 2
        );
    }

    @Test
    public void areMultipleDepositsSummedCorrectly() {
        WalletOperation createWalletOperation = walletService.createWallet( EXAMPLE_TRANSACTION_ID );

        for ( int i = 0; i < OP_REPEATS; i++ ) {
            walletService.makeDeposit(
                    createWalletOperation.walletId(),
                    EXAMPLE_AMOUNT,
                    EXAMPLE_TRANSACTION_ID + 2 + i
            );
        }

        long finalBalance = walletService.getBalance( createWalletOperation.walletId() ).balanceBefore().value();

        assertEquals( EXAMPLE_AMOUNT * OP_REPEATS, finalBalance );
    }

    @Test
    public void throwsOnDepositsWithNonUniqueIds() {
        WalletOperation createWalletOperation = walletService.createWallet( EXAMPLE_TRANSACTION_ID );
        walletService.makeDeposit(
                createWalletOperation.walletId(),
                EXAMPLE_AMOUNT,
                EXAMPLE_TRANSACTION_ID + 1
        );
        assertThrows( EntityExistsException.class, () ->
                walletService.makeDeposit(
                        createWalletOperation.walletId(),
                        EXAMPLE_AMOUNT * 2,
                        EXAMPLE_TRANSACTION_ID + 1
                )
        );
    }

    @Test
    public void throwsOnNegativeDeposits() {
        WalletOperation createWalletOperation = walletService.createWallet( EXAMPLE_TRANSACTION_ID );

        assertThrows( IllegalOperationAmountException.class, () ->
                walletService.makeDeposit(
                        createWalletOperation.walletId(),
                        -EXAMPLE_AMOUNT,
                        EXAMPLE_TRANSACTION_ID + 1
                )
        );
    }

    @Test
    public void throwsOnZeroDeposits() {
        WalletOperation createWalletOperation = walletService.createWallet( EXAMPLE_TRANSACTION_ID );

        assertThrows( IllegalOperationAmountException.class, () ->
                walletService.makeDeposit(
                        createWalletOperation.walletId(),
                        0,
                        EXAMPLE_TRANSACTION_ID + 1
                )
        );
    }

    @Test
    public void throwsOnNegativeWithdrawals() {
        WalletOperation createWalletOperation = walletService.createWallet( EXAMPLE_TRANSACTION_ID );

        walletService.makeDeposit(
                createWalletOperation.walletId(),
                EXAMPLE_AMOUNT,
                EXAMPLE_TRANSACTION_ID + 1
        );

        assertThrows( IllegalOperationAmountException.class, () ->
                walletService.makeWithdrawal(
                        createWalletOperation.walletId(),
                        -EXAMPLE_AMOUNT,
                        EXAMPLE_TRANSACTION_ID + 2
                )
        );
    }

    @Test
    public void throwsOnZeroWithdrawals() {
        WalletOperation createWalletOperation = walletService.createWallet( EXAMPLE_TRANSACTION_ID );

        walletService.makeDeposit(
                createWalletOperation.walletId(),
                EXAMPLE_AMOUNT,
                EXAMPLE_TRANSACTION_ID + 1
        );

        assertThrows( IllegalOperationAmountException.class, () ->
                walletService.makeWithdrawal(
                        createWalletOperation.walletId(),
                        0,
                        EXAMPLE_TRANSACTION_ID + 2
                )
        );
    }

    @Test
    public void throwsOnWithdrawalsWithNonUniqueIds() {
        WalletOperation createWalletOperation = walletService.createWallet( EXAMPLE_TRANSACTION_ID );
        walletService.makeDeposit(
                createWalletOperation.walletId(),
                EXAMPLE_AMOUNT * 3,
                EXAMPLE_TRANSACTION_ID + 1
        );
        walletService.makeWithdrawal(
                createWalletOperation.walletId(),
                EXAMPLE_AMOUNT,
                EXAMPLE_TRANSACTION_ID + 2
        );
        assertThrows( EntityExistsException.class, () ->
                walletService.makeWithdrawal(
                        createWalletOperation.walletId(),
                        EXAMPLE_AMOUNT,
                        EXAMPLE_TRANSACTION_ID + 2
                )
        );
    }

    @Test
    public void throwsOnMixedOpsWithNonUniqueIds() {
        WalletOperation createWalletOperation = walletService.createWallet( EXAMPLE_TRANSACTION_ID );
        assertThrows( EntityExistsException.class, () ->
                walletService.createWallet( EXAMPLE_TRANSACTION_ID )
        );
        walletService.makeDeposit(
                createWalletOperation.walletId(),
                EXAMPLE_AMOUNT * 3,
                EXAMPLE_TRANSACTION_ID + 1
        );
        assertThrows( EntityExistsException.class, () ->
                walletService.makeDeposit(
                        createWalletOperation.walletId(),
                        EXAMPLE_AMOUNT * 3,
                        EXAMPLE_TRANSACTION_ID // used by create
                )
        );
        assertThrows( EntityExistsException.class, () ->
                walletService.makeDeposit(
                        createWalletOperation.walletId(),
                        EXAMPLE_AMOUNT * 3,
                        EXAMPLE_TRANSACTION_ID + 1 // used by deposit
                )
        );
        walletService.makeWithdrawal(
                createWalletOperation.walletId(),
                EXAMPLE_AMOUNT,
                EXAMPLE_TRANSACTION_ID + 2
        );
        assertThrows( EntityExistsException.class, () ->
                walletService.makeWithdrawal(
                        createWalletOperation.walletId(),
                        EXAMPLE_AMOUNT,
                        EXAMPLE_TRANSACTION_ID // used by create
                )
        );
        assertThrows( EntityExistsException.class, () ->
                walletService.makeWithdrawal(
                        createWalletOperation.walletId(),
                        EXAMPLE_AMOUNT,
                        EXAMPLE_TRANSACTION_ID + 1 // used by deposit
                )
        );
        assertThrows( EntityExistsException.class, () ->
                walletService.makeWithdrawal(
                        createWalletOperation.walletId(),
                        EXAMPLE_AMOUNT,
                        EXAMPLE_TRANSACTION_ID + 2 // used by withdrawal
                )
        );
    }

    @Test
    public void throwsOnBalanceExceeded() {
        WalletOperation createWalletOperation = walletService.createWallet( EXAMPLE_TRANSACTION_ID );
        walletService.makeDeposit(
                createWalletOperation.walletId(),
                EXAMPLE_AMOUNT * 2,
                EXAMPLE_TRANSACTION_ID + 1
        );

        assertThrows( IllegalOperationAmountException.class, () ->
                walletService.makeWithdrawal(
                        createWalletOperation.walletId(),
                        EXAMPLE_AMOUNT * 2 + 1,
                        EXAMPLE_TRANSACTION_ID + 2
                )
        );

        walletService.makeWithdrawal(
                createWalletOperation.walletId(),
                EXAMPLE_AMOUNT,
                EXAMPLE_TRANSACTION_ID + 2
        );

        assertThrows( IllegalOperationAmountException.class, () ->
                walletService.makeWithdrawal(
                        createWalletOperation.walletId(),
                        EXAMPLE_AMOUNT + 1,
                        EXAMPLE_TRANSACTION_ID + 3
                )
        );
    }

    @Test
    public void isCompleteWithdrawalInSequenceOfWithdrawalsPossible() {
        WalletOperation createWalletOperation = walletService.createWallet( EXAMPLE_TRANSACTION_ID );

        walletService.makeDeposit(
                createWalletOperation.walletId(),
                EXAMPLE_AMOUNT * OP_REPEATS,
                EXAMPLE_TRANSACTION_ID + 1
        );

        for ( int i = 0; i < OP_REPEATS; i++ ) {
            walletService.makeWithdrawal(
                    createWalletOperation.walletId(),
                    EXAMPLE_AMOUNT,
                    EXAMPLE_TRANSACTION_ID + 2 + i
            );
        }

        long finalBalance = walletService.getBalance( createWalletOperation.walletId() ).balanceBefore().value();

        assertEquals( 0, finalBalance );
    }

    @Test
    public void throwsOnBalanceMaximumExceeded() {
        WalletOperation createWalletOperation = walletService.createWallet( EXAMPLE_TRANSACTION_ID );

        assertThrows( IllegalOperationAmountException.class, () ->
                walletService.makeDeposit(
                        createWalletOperation.walletId(),
                        WalletAmount.MAX_AMOUNT + 1,
                        EXAMPLE_TRANSACTION_ID + 1
                )
        );

        walletService.makeDeposit(
                createWalletOperation.walletId(),
                WalletAmount.MAX_AMOUNT,
                EXAMPLE_TRANSACTION_ID + 1
        );

        assertThrows( IllegalOperationAmountException.class, () ->
                walletService.makeDeposit(
                        createWalletOperation.walletId(),
                        1,
                        EXAMPLE_TRANSACTION_ID + 2
                )
        );
    }

    // id=1 would usually be the 1st one autogenerated, but it shouldn't exist yet

    @Test
    public void throwsOnBalanceForNonexistentWallet() {
        assertThrows( EntityNotFoundException.class, () ->
                walletService.getBalance( 1 )
        );
    }

    @Test
    public void throwsOnDepositForNonexistentWallet() {
        assertThrows( EntityNotFoundException.class, () ->
                walletService.makeDeposit( 1, EXAMPLE_AMOUNT, EXAMPLE_TRANSACTION_ID )
        );
    }

    @Test
    public void throwsOnWithdrawalForNonexistentWallet() {
        assertThrows( EntityNotFoundException.class, () ->
                walletService.makeWithdrawal( 1, EXAMPLE_AMOUNT, EXAMPLE_TRANSACTION_ID )
        );
    }

    @Test
    public void throwsOnStatementForNonexistentWallet() {
        assertThrows( EntityNotFoundException.class, () ->
                walletService.getStatement( 1 )
        );
    }

    @Test
    public void isSimpleStatementValid() {
        WalletOperation createWalletOperation = walletService.createWallet( EXAMPLE_TRANSACTION_ID );

        walletService.makeDeposit(
                createWalletOperation.walletId(),
                EXAMPLE_AMOUNT * 9,
                EXAMPLE_TRANSACTION_ID + 1
        );

        walletService.makeWithdrawal(
                createWalletOperation.walletId(),
                EXAMPLE_AMOUNT * 3,
                EXAMPLE_TRANSACTION_ID + 2
        );

        walletService.makeWithdrawal(
                createWalletOperation.walletId(),
                EXAMPLE_AMOUNT * 2,
                EXAMPLE_TRANSACTION_ID + 3
        );

        WalletStatement walletStatement = walletService.getStatement( createWalletOperation.walletId() );

        List<WalletOperation> walletTransactionViewLists = walletStatement.walletStatementItems();
        assertEquals( 4, walletTransactionViewLists.size() );
        assertOperationResults(
                walletTransactionViewLists.get( 0 ),
                WalletOperationType.CREATE,
                null,
                null,
                0L,
                EXAMPLE_TRANSACTION_ID
        );
        assertOperationResults(
                walletTransactionViewLists.get( 1 ),
                WalletOperationType.DEPOSIT,
                EXAMPLE_AMOUNT * 9,
                0L,
                EXAMPLE_AMOUNT * 9,
                EXAMPLE_TRANSACTION_ID + 1
        );
        assertOperationResults(
                walletTransactionViewLists.get( 2 ),
                WalletOperationType.WITHDRAWAL,
                EXAMPLE_AMOUNT * 3,
                EXAMPLE_AMOUNT * 9,
                EXAMPLE_AMOUNT * 6,
                EXAMPLE_TRANSACTION_ID + 2
        );
        assertOperationResults(
                walletTransactionViewLists.get( 3 ),
                WalletOperationType.WITHDRAWAL,
                EXAMPLE_AMOUNT * 2,
                EXAMPLE_AMOUNT * 6,
                EXAMPLE_AMOUNT * 4,
                EXAMPLE_TRANSACTION_ID + 3
        );

        long finalBalance = walletService.getBalance( createWalletOperation.walletId() ).balanceBefore().value();

        assertEquals( EXAMPLE_AMOUNT * 4, finalBalance );
    }

    /*
    @Test
    public void isWalletRemoved() {
    }
    */
}
