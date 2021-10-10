package money.wallet.api.exception;

public class IllegalWalletAmountException extends IllegalArgumentException {
    public IllegalWalletAmountException( String msg ) {
        super( msg );
    }
}
