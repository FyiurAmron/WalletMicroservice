package money.wallet.api;

import money.wallet.api.data.*;
import money.wallet.api.exception.IllegalWalletAmountException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag( TestUtils.UNIT_TAG )
public class WalletAmountTests {
    private static final long EXAMPLE_AMOUNT = 42;
    private static final int OP_REPEATS = 5;

    @Test
    public void isSimpleWalletAmountCreatedProperly() {
        var walletAmount = new WalletAmount( EXAMPLE_AMOUNT );
        assertEquals( EXAMPLE_AMOUNT, walletAmount.value() );
    }

    @Test
    public void throwsOnNegativeAmountCreation() {
        assertThrows( IllegalWalletAmountException.class, () ->
                new WalletAmount( -EXAMPLE_AMOUNT )
        );
    }

    @Test
    public void throwsOnTooLargeAmountCreation() {
        assertThrows( IllegalWalletAmountException.class, () ->
                new WalletAmount( WalletAmount.MAX_AMOUNT + 1 )
        );
    }

    @Test
    public void throwsOnTooLargeResultingAmount() {
        var walletAmount = new WalletAmount( WalletAmount.MAX_AMOUNT );
        assertThrows( IllegalWalletAmountException.class, () ->
                walletAmount.increaseBy( new WalletAmount( 1 ) )
        );
    }

    @Test
    public void throwsOnZeroIncrease() {
        var walletAmount = new WalletAmount( EXAMPLE_AMOUNT );
        assertThrows( IllegalWalletAmountException.class, () ->
                walletAmount.increaseBy( WalletAmount.ZERO )
        );
    }

    @Test
    public void throwsOnZeroDecrease() {
        var walletAmount = new WalletAmount( EXAMPLE_AMOUNT );
        assertThrows( IllegalWalletAmountException.class, () ->
                walletAmount.increaseBy( WalletAmount.ZERO )
        );
    }

    @Test
    public void areSimpleIncreaseDecreasePairsZeroingEachOther() {
        var oneAmount = new WalletAmount( 1 );
        var exampleAmount = new WalletAmount( EXAMPLE_AMOUNT );
        WalletAmount resultAmount = WalletAmount.ZERO
                .increaseBy( oneAmount )
                .decreaseBy( oneAmount )
                .increaseBy( exampleAmount )
                .decreaseBy( exampleAmount );
        assertEquals( WalletAmount.ZERO, resultAmount );
    }

    @Test
    public void areMultipleAmountIncreasesSummedCorrectly() {
        var baseWalletAmount = new WalletAmount( EXAMPLE_AMOUNT );

        WalletAmount walletAmount = baseWalletAmount;
        for ( int i = 0; i < OP_REPEATS; i++ ) {
            walletAmount = walletAmount.increaseBy( baseWalletAmount );
        }

        assertEquals( new WalletAmount( EXAMPLE_AMOUNT * ( OP_REPEATS + 1 ) ), walletAmount );
    }

    @Test
    public void areAmountSequencesProperlyCalculated() {
        var baseWalletAmount = new WalletAmount( EXAMPLE_AMOUNT );
        var doubleWalletAmount = new WalletAmount( 2 * EXAMPLE_AMOUNT );
        WalletAmount walletAmount = baseWalletAmount;
        walletAmount = walletAmount.increaseBy( baseWalletAmount );
        walletAmount = walletAmount.increaseBy( doubleWalletAmount );
        walletAmount = walletAmount.decreaseBy( baseWalletAmount );
        walletAmount = walletAmount.decreaseBy( baseWalletAmount );
        assertEquals( doubleWalletAmount, walletAmount );
    }

    @Test
    public void throwsOnDecreaseBelowZero() {
        var walletAmount = new WalletAmount( EXAMPLE_AMOUNT );
        assertThrows( IllegalWalletAmountException.class, () ->
                walletAmount.decreaseBy( walletAmount.increaseBy( new WalletAmount( 1 ) ) )
        );
    }
}
