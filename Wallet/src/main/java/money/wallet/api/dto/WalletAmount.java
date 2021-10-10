package money.wallet.api.dto;

import money.wallet.api.model.Wallet;

/**
 * In case we have to add currencies, formatting etc. later on.
 */
public record WalletAmount( long value ) {
    public static final WalletAmount ZERO = new WalletAmount( 0 );

    public static final long MAX_AMOUNT = Long.MAX_VALUE / 4;

    public static WalletAmount from( Wallet wallet ) {
        return new WalletAmount( wallet.getBalance() );
    }

    public WalletAmount {
        if ( value < 0 ) {
            throw new IllegalArgumentException( "value '" + value + "' < 0" );
        }
        if ( value > MAX_AMOUNT ) {
            throw new IllegalArgumentException( "value '" + value + "' > MAX_AMOUNT '" + MAX_AMOUNT + "'" );
        }
    }

    private void requireNonZero() {
        if ( value == 0 ) {
            throw new IllegalArgumentException( "value '" + value + "' == 0" );
        }
    }

    public WalletAmount increaseBy( WalletAmount walletAmount ) {
        walletAmount.requireNonZero();
        long newValue = value + walletAmount.value;
        if ( newValue > MAX_AMOUNT ) {
            throw new IllegalArgumentException(
                    "new value '" + value + "' > MAX_AMOUNT '" + MAX_AMOUNT + "'" );
        }
        return new WalletAmount( newValue );
    }

    public WalletAmount decreaseBy( WalletAmount walletAmount ) {
        walletAmount.requireNonZero();
        long newValue = value - walletAmount.value;
        if ( newValue < 0 ) {
            throw new IllegalArgumentException(
                    "new value '" + value + "' < 0" );
        }
        return new WalletAmount( newValue );
    }
}
