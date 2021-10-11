package money.wallet.api.data;

import money.wallet.api.exception.IllegalOperationAmountException;
import money.wallet.api.model.Wallet;

/**
 * VO representing a wallet amount (balance/deposit/withdrawal etc.) as a natural number (currently).
 * Useful in case we have to add currencies, formatting etc. later on,
 * or if needed to e.g. just treat it as fixed point non-integral number.
 */
public record WalletAmount( long value ) {
    public static final WalletAmount ZERO = new WalletAmount( 0 );

    public static final long MAX_AMOUNT = Long.MAX_VALUE / 4;

    public static WalletAmount from( Wallet wallet ) {
        return new WalletAmount( wallet.getBalance() );
    }

    public static WalletAmount from( Long amount ) {
        return amount == null ? null : new WalletAmount( amount );
    }

    public static Long toLong( WalletAmount walletAmount ) {
        return walletAmount == null ? null : walletAmount.value;
    }

    public WalletAmount {
        if ( value < 0 ) {
            throw new IllegalOperationAmountException( "value '" + value + "' < 0" );
        }
        if ( value > MAX_AMOUNT ) {
            throw new IllegalOperationAmountException( "value '" + value + "' > MAX_AMOUNT '" + MAX_AMOUNT + "'" );
        }
    }

    private void requireNonZero() {
        if ( value == 0 ) {
            throw new IllegalOperationAmountException( "value '" + value + "' == 0" );
        }
    }

    public WalletAmount increaseBy( WalletAmount walletAmount ) {
        walletAmount.requireNonZero();
        long newValue = value + walletAmount.value;
        if ( newValue > MAX_AMOUNT ) {
            throw new IllegalOperationAmountException(
                    "new value '" + value + "' > MAX_AMOUNT '" + MAX_AMOUNT + "'" );
        }
        return new WalletAmount( newValue );
    }

    public WalletAmount decreaseBy( WalletAmount walletAmount ) {
        walletAmount.requireNonZero();
        long newValue = value - walletAmount.value;
        if ( newValue < 0 ) {
            throw new IllegalOperationAmountException(
                    "new value '" + value + "' < 0" );
        }
        return new WalletAmount( newValue );
    }
}
